/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nikita.generator.connection;

import com.rkrzmail.nikita.data.NikitaCursor;
import com.rkrzmail.nikita.data.NikitaProperty;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;
import oracle.jdbc.OracleTypes;
import org.apache.hadoop.io.DataOutputBuffer;

/**
 * created by 13k.mail@gmail.com
 */
public class NikitaConnection {

    public static final String LOGIC = "logic";
    public static final String MOBILE = "mobile";
    public static final String NIKITA = "nikita";
    public static final String DEFAULT = "default";

    public static NikitaConnection getConnection() {
        return getConnection("default");
    }

    public static NikitaConnection getConnection(String name) {
        String cls = classDefault(defLogin.getData(name).getData("class").toString());
        if (cls.toLowerCase().startsWith("nikitaconnection")) {
            return new NikitaWillyConnection().openConnection(cls, defLogin.getData(name).getData("url").toString(), defLogin.getData(name).getData("user").toString(), defLogin.getData(name).getData("pass").toString());
        }
        return new NikitaConnection().openConnection(cls, defLogin.getData(name).getData("url").toString(), defLogin.getData(name).getData("user").toString(), defLogin.getData(name).getData("pass").toString());
    }

    private NikitaConnection openConnection(String cname, String url, String user, String pass) {
        user = user != null ? user : "";
        pass = pass != null ? pass : "";
        error = null;
        if (cname.contains("com.mysql.jdbc.Driver")) {
            core = CORE_MYSQL;
        } else if (cname.contains("com.microsoft.sqlserver.jdbc.SQLServerDriver")) {
            core = CORE_SQLSERVER;
        } else if (cname.contains("org.sqlite.JDBC")) {
            core = CORE_SQLITE;
        } else if (cname.contains("oracle.jdbc.driver.OracleDriver")) {
            core = CORE_ORACLE;
        } else if (cname.contains("nikitaconnection")) {
            core = -1;
        }
        try {
            Class.forName(cname);
            if (user.trim().length() >= 1 || pass.trim().length() >= 1) {
                conn = DriverManager.getConnection(url, user, pass);
            } else {
                conn = DriverManager.getConnection(url);
            }
        } catch (ClassNotFoundException | SQLException e) {
            error = e.getMessage();
        }
        return this;
    }

    private static String classDefault(String cls) {
        switch (cls.toLowerCase()) {
            case "mysql":
                return "com.mysql.jdbc.Driver";
            case "sqlserver":
                return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
            case "sqllite":
                return "org.sqlite.JDBC"; //c = DriverManager.getConnection("jdbc:sqlite:test.db");
            case "oracle":
                return "oracle.jdbc.driver.OracleDriver";
            case "nikita":
                return "nikitaconnection";
        }
        return cls;
    }

    public String getError() {
        return error != null ? error : "";
    }
    protected String error;
    private Connection conn;

    public Connection getInternalConnection() {
        return conn;
    }

    public void clearError() {
        error = "";
    }

    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            error = e.getMessage();
        }
    }

    public void setAutoCommit(boolean auto) {
        try {
            if (conn != null) {
                conn.setAutoCommit(auto);
            }
        } catch (SQLException e) {
            error = e.getMessage();
        }
    }

    public void setSavepoint() {
        try {
            if (conn != null) {
                conn.setSavepoint();
            }
        } catch (SQLException e) {
            error = e.getMessage();
        }
    }

    public void rollback() {
        try {
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException e) {
            error = e.getMessage();
        }
    }

    public void commit() {
        try {
            if (conn != null) {
                conn.commit();
            }
        } catch (SQLException e) {
            error = e.getMessage();
        }
    }

    public boolean isClosed() {
        try {
            if (conn != null) {
                return conn.isClosed();
            }
        } catch (SQLException e) {
        }
        return true;
    }

    public Nikitaset Call(String sql, Nset array) {
        Nikitaset rst;

        if (getError().length() == 0) {
            try {
                CallableStatement statement = conn.prepareCall(sql, getDatabaseCore() == CORE_SQLITE ? java.sql.ResultSet.TYPE_FORWARD_ONLY : java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);

                for (int i = 0; i < array.getArraySize(); i++) {
                    if (array.getData(i).isNumber()) {
                        if (array.getData(i).toDouble() == array.getData(i).toLong()) {
                            statement.setLong(i + 1, array.getData(i).toLong());
                        } else {
                            statement.setDouble(i + 1, array.getData(i).toDouble());
                        }
                    } else {
                        statement.setString(i + 1, array.getData(i).toString());
                    }
                }
                ResultSet rs = null;
                statement.execute();
                try {
                    rs = statement.getResultSet();
                } catch (SQLException e) {
                }
                rst = new Nikitaset(rs);
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                rst = new Nikitaset(e.getMessage());
            }
        } else {
            rst = new Nikitaset(getError());
        }
        return rst;
    }

    public NikitaCursor QueryCursor(String sql, String... param) {
        return new NikitaCursor("");
    }

    public Nikitaset Query(String sql, String... param) {
        return QueryPage(-1, -1, sql, param);
    }

    public Nikitaset QueryNF(String sql, String... param) {
        return QueryPageNF(-1, -1, sql, param);
    }

    public Nikitaset QueryPageNF(int page, int rowperpage, String sql, String... param) {
        Nset narr = Nset.newArray();
        if (param != null) {
            for (int i = 0; i < param.length; i++) {
                int ipos = param[i].indexOf("|");
                if (ipos >= 0 && ipos <= 10) {
                    String var = param[i].substring(0, ipos).toLowerCase();
                    if (var.equals("i") || var.equals("int") || var.equals("(int)")) {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData("int"));
                    } else if (var.equals("l") || var.equals("long") || var.equals("(long)")) {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData("long"));
                    } else if (var.equals("dt") || var.equals("datetime") || var.equals("(datetime)")) {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData("datetime"));
                    } else if (var.equals("t") || var.equals("time") || var.equals("(time)")) {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData("time"));
                    } else if (var.equals("d") || var.equals("date") || var.equals("(date)")) {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData("date"));
                    } else if (var.equals("f") || var.equals("float") || var.equals("(float)")) {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData("float"));
                    } else if (var.equals("b") || var.equals("boolean") || var.equals("(boolean)")) {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData("boolean"));
                    } else if (var.equals("double") || var.equals("(double)")) {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData("double"));
                    } else if (var.equals("decimal") || var.equals("(decimal)")) {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData("decimal"));
                    } else if (var.equals("#") || var.equals("n") || var.equals("number") || var.equals("(number)")) {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData("number"));
                    } else if (var.equals("noescape") || var.equals("(noescape)")) {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData("noescape"));

                    } else if (var.equals("clob") || var.equals("(clob)")) {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData("clob"));
                    } else if (var.equals("blob") || var.equals("(blob)")) {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData("blob"));
                    } else if (var.startsWith("out") || var.startsWith("(out")) {
                        var = var.startsWith("(") ? var.substring(1) : var;
                        var = var.endsWith(")") ? var.substring(0, var.length() - 1) : var;
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData(var));

                    } else {
                        narr.addData(Nset.newArray().addData(param[i].substring(ipos + 1)).addData(""));
                    }
                } else {
                    narr.addData(param[i]);
                }
            }
        }
        return QueryPage(sql, narr, page, rowperpage);
    }

    public Nikitaset QueryPage(int page, int rowperpage, String sql, String... param) {
        Nset narr = Nset.newArray();
        if (param != null) {
            for (int i = 0; i < param.length; i++) {
                narr.addData(param[i]);
            }
        }
        return QueryPage(sql, narr, page, rowperpage);
    }

    private Nset specialQuery(String sql, Nset array, int page, int rowperpage) {
        sql = sql.trim();
        Nset nset = Nset.newObject();
        if (sql.startsWith("{") && sql.endsWith("}")) {
            Nset n = Nset.readJSON(sql);
            if (n.getData("nfid").toString().equals("nikitaconnection")) {
                sql = n.getData("sql").toString();
                array = n.getData("arg");
            }
        } else if (sql.startsWith("[") && sql.endsWith("]")) {
            Nset n = Nset.readJSON(sql);
            if (n.getData(0).toString().equals("create")) {
                StringBuilder sbuff = new StringBuilder();
                sbuff.append("CREATE table ").append(n.getData(1).toString()).append("(");//id  INTEGER PRIMARY KEY AUTOINCREMENT
                for (int i = 2; i < n.getArraySize(); i++) {
                    if (n.getData(i).isNset()) {
                        sbuff.append((i > 2) ? "," : "").append(n.getData(i).getData(0).toString()).append(" ").append(n.getData(i).getData(1).toString());
                    } else {
                        sbuff.append((i > 2) ? "," : "").append(n.getData(i).toString()).append(" TEXT");
                    }
                }
                sbuff.append(")");
                sql = sbuff.toString();
                array = n.getData("arg");
            }
        } else {

        }
        nset.setData("sql", sql);
        nset.setData("arg", array);
        return nset;
    }

    private int resulttoFile(ResultSet resultSet, String fname, String tname) {
        int rows = 0;
        Vector<String> header = new Vector<String>();
        try {
            if (fname == null) {
                return 0;
            }
            FileOutputStream fos = new FileOutputStream(fname);
            DataOutputStream outputStream = new DataOutputStream(fos);
            try {
                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    header.addElement(resultSet.getMetaData().getColumnLabel(i + 1));//new
                }
                Nset info = Nset.newObject();
                Nset metatype = Nset.newArray();
                Nset metasize = Nset.newArray();
                Nset metaname = Nset.newArray();
                Nset metalabl = Nset.newArray();
                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                    metatype.addData(resultSet.getMetaData().getColumnTypeName(i + 1));
                    metasize.addData(resultSet.getMetaData().getColumnDisplaySize(i + 1));
                    metaname.addData(resultSet.getMetaData().getColumnName(i + 1));  //colname
                    metalabl.addData(resultSet.getMetaData().getColumnLabel(i + 1));  //collabel
                }
                info.setData("metadata", Nset.newObject().setData("type", metatype).setData("size", metasize).setData("name", metaname).setData("label", metalabl));
                Nikitaset nikitaset = new Nikitaset(header, new Vector<Vector<String>>(), "", info);

                outputStream.writeUTF(Nset.newObject().setData("rows", rows).setData("tablename", tname != null ? tname : "").setData("nikitaset", nikitaset.toNset()).toJSON());
                outputStream.flush();
                while (resultSet.next()) {
                    rows = resultSet.getRow();
                    Vector<String> field = new Vector<String>();
                    for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                        try {
                            String sdata = resultSet.getString(i + 1);
                            field.addElement(sdata != null ? sdata : "");
                        } catch (Exception e) {
                            field.addElement("");
                        }
                    }
                    outputStream.writeUTF(Nset.newArray().addData(field).toJSON());
                }
                outputStream.flush();

            } catch (Exception e) {
            }
            fos.close();
        } catch (Exception e) {
        }
        return rows;
    }
    public static final String LIMIT = "$$LIMIT$$NIKITACONNECTION$$LIMIT$$";

    public Nikitaset QueryPage(String sql, Nset array, int page, int rowperpage) {
        long start = System.currentTimeMillis();
        int limit = 0;
        Vector<Integer> out = new Vector<Integer>();

        sql = sql.trim();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }

        String fname = null;
        String tname = null;
        if (sql.startsWith("::")) {
            sql = sql.substring(2);
            if (sql.contains("::")) {
                fname = sql.substring(0, sql.indexOf("::"));
                sql = sql.substring(sql.indexOf("::") + 2);
                if (sql.contains("::")) {
                    tname = sql.substring(0, sql.indexOf("::"));
                    sql = sql.substring(sql.indexOf("::") + 2);
                }

            }
        }
        //28/11/21
        if (sql.startsWith("$LIMIT") && sql.contains(":")) {
            limit = Utility.getInt(sql.substring(6, sql.indexOf(":")));
            sql = sql.substring(sql.indexOf(":") + 1);
        }

        Nset nset = specialQuery(sql, array, page, rowperpage);
        sql = nset.getData("sql").toString();
        array = nset.getData("arg");

        Nikitaset rst;
        boolean useArg = (array.getArraySize() >= 1);
        useArg = true;//************
        if (getError().length() == 0) {
            try {
                if (useArg) {
                    Nset n = Nset.newObject();
                    if (sql.contains("@?")) {
                        sql = sql + " ";
                        StringBuilder sb = new StringBuilder();
                        Vector<String> v = Utility.splitVector(sql, "?");
                        for (int i = 0; i < v.size(); i++) {
                            if (i == 0) {
                                sb.append(v.elementAt(i));
                            } else if (sb.toString().endsWith("@")) {
                                sb.delete(sb.toString().length() - 1, sb.toString().length());

                                String _replacementStr = "";
                                if (array.getData(i - 1).isNsetObject()) {
                                    _replacementStr = array.getData(i - 1).getData("val").toString();
                                } else if (array.getData(i - 1).isNsetArray()) {
                                    _replacementStr = array.getData(i - 1).getData(0).toString();
                                } else {
                                    _replacementStr = array.getData(i - 1).toString();
                                }
                                sb.append(_replacementStr).append(v.elementAt(i));
                                n.setData("" + (i - 1), "");
                            } else {
                                sb.append("?").append(v.elementAt(i));
                            }
                        }
                        sql = sb.toString().substring(0, sb.toString().length() - 1);
                        for (int l = 0; l < array.getArraySize(); l++) {
                            int i = array.getArraySize() - l - 1;
                            if (n.containsKey(i + "")) {
                                ((Vector) array.getInternalObject()).removeElementAt(i);
                            }
                        }

                    }
                    //28/11/21
                    if (sql.contains(LIMIT)) {
                        int rows = limit;
                        page = page <= 0 ? 0 : page;
                        int pmax = rows / rowperpage + (rows % rowperpage <= 0 ? 0 : 1);
                        page = page >= pmax ? pmax : page;
                        page = page <= 0 ? 1 : page;
                        int currrow = rowperpage * (page - 1);
                        currrow = currrow < 0 ? 0 : currrow;//currrow start with 0
                        if (rowperpage == -1) {
                            sql = Utility.replace(sql, LIMIT, " ");
                        }else{
                            sql = Utility.replace(sql, LIMIT, " LIMIT " + currrow + "," + rowperpage + " ");
                        }                        
                    }
                    
                    PreparedStatement statement = conn.prepareStatement(sql, getDatabaseCore() == CORE_SQLITE ? java.sql.ResultSet.TYPE_FORWARD_ONLY : java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);

                    if (sql.trim().toLowerCase().startsWith("insert")) {
                        statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    } else if (sql.trim().toLowerCase().startsWith("call") || sql.trim().toLowerCase().startsWith("{")) {
                        CallableStatement callableStatement = conn.prepareCall(sql);
                        //callableStatement.registerOutParameter("", java.sql.Types.VARCHAR);      

                        statement = callableStatement;
                    } else {

                    }

                    //if (!sql.trim().toLowerCase().startsWith("select")) {
                    //    statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                    //} 
                    int esc = 0;
                    for (int l = 0; l < array.getArraySize(); l++) {
                        int i = l - esc;
                        if (n.containsKey(l + "")) {
                            //abaikan
                            //esc++;

                            /*}else if (array.getData(i).isNsetObject()) {
                                if (array.getData(i).getData("var").toString().equals("date")||array.getData(i).getData("var").toString().equals("todate")) {
                                    statement.setDate(i+1, new java.sql.Date(Utility.getDate( array.getData(i).getData("val").toString()  )));
                                }else if (array.getData(i).getData("var").toString().equals("time")) {
                                    statement.setTime(i+1,  new java.sql.Time( Utility.getTime(array.getData(i).getData("val").toString()  )));
                                }else if (array.getData(i).getData("var").toString().equals("datetime")||array.getData(i).getData("var").toString().equals("todatetime")) {
                                    statement.setTimestamp(i+1,  new java.sql.Timestamp( Utility.getDateTime(array.getData(i).getData("val").toString()  )));
                                }else if (array.getData(i).getData("var").toString().equals("timestamp")) {
                                    statement.setTimestamp(i+1,  new java.sql.Timestamp(System.currentTimeMillis()) );
                                }else if (array.getData(i).getData("var").toString().equals("int")) {
                                    statement.setInt(i+1, array.getData(i).getData("val").toInteger());
                                }else if (array.getData(i).getData("var").toString().equals("boolean")) {
                                    statement.setBoolean(i+1, array.getData(i).getData("val").toBoolean());
                                }else if (array.getData(i).getData("var").toString().equals("long")) {
                                    statement.setLong(i+1, array.getData(i).getData("val").toLong());
                                }else if (array.getData(i).getData("var").toString().equals("number")) {
                                    statement.setLong(i+1, array.getData(i).getData("val").toLong());
                                }else if (array.getData(i).getData("var").toString().equals("double")) {
                                    statement.setDouble(i+1, array.getData(i).getData("val").toDouble());
                                }else if (array.getData(i).getData("var").toString().equals("decimal")) {
                                    statement.setDouble(i+1, array.getData(i).getData("val").toDouble());
                                }else if (array.getData(i).getData("var").toString().equals("float")) {
                                    statement.setFloat(i+1, Utility.getFloat(array.getData(i).getData("val").toString()));
                                }else if (array.getData(i).getData("var").toString().equals("noescape")) {
                                     //abaikan
                                }else{
                                    statement.setString(i+1, array.getData(i).getData("val").toString());
                                }         
                            }else if (array.getData(i).isNsetArray()) { 
                                if (array.getData(i).getData(1).toString().equals("date")||array.getData(i).getData(1).toString().equals("todate")) {
                                    statement.setDate(i+1, new java.sql.Date(Utility.getDate( array.getData(i).getData(0).toString()  )));
                                }else if (array.getData(i).getData(1).toString().equals("time")) {
                                    statement.setTime(i+1,  new java.sql.Time( Utility.getTime(array.getData(i).getData(0).toString()  )));
                                }else if (array.getData(i).getData(1).toString().equals("datetime")||array.getData(i).getData(1).toString().equals("todatetime")) {
                                    statement.setTimestamp(i+1,  new java.sql.Timestamp( Utility.getDateTime(array.getData(i).getData(0).toString()  )));
                                }else if (array.getData(i).getData(1).toString().equals("timestamp")) {
                                    statement.setTimestamp(i+1,  new java.sql.Timestamp(System.currentTimeMillis()) );
                                }else if (array.getData(i).getData(1).toString().equals("int")) {
                                    statement.setInt(i+1, array.getData(i).getData(0).toInteger());
                                }else if (array.getData(i).getData(1).toString().equals("boolean")) {
                                    statement.setBoolean(i+1, array.getData(i).getData(0).toBoolean());
                                }else if (array.getData(i).getData(1).toString().equals("long")) {
                                    statement.setLong(i+1, array.getData(i).getData(0).toLong());
                                }else if (array.getData(i).getData(1).toString().equals("number")) {
                                    statement.setLong(i+1, array.getData(i).getData(0).toLong());
                                }else if (array.getData(i).getData(1).toString().equals("double")) {
                                    statement.setDouble(i+1, array.getData(i).getData(0).toDouble());
                                }else if (array.getData(i).getData(1).toString().equals("decimal")) {
                                    statement.setDouble(i+1, array.getData(i).getData(0).toDouble());
                                }else if (array.getData(i).getData(1).toString().equals("float")) {
                                    statement.setFloat(i+1, Utility.getFloat(array.getData(i).getData(0).toString()));
                                }else if (array.getData(i).getData(1).toString().equals("noescape")) {
                                    //abaikan
                                }else{
                                    statement.setString(i+1, array.getData(i).getData(0).toString());
                                } */
                        } else if (array.getData(i).isNsetObject() || array.getData(i).isNsetArray()) {
                            String vcode = getValue(array.getData(i), 1, "var").toString();

                            if (vcode.startsWith("!")) {
                                if (getValue(array.getData(i), 0, "val").toString().equalsIgnoreCase("")) {
                                    statement.setNull(i + 1, java.sql.Types.NULL);
                                    continue;
                                }
                                vcode = vcode.substring(0, vcode.length() - 4);
                            }

                            if (vcode.equals("date") || vcode.equals("todate")) {
                                statement.setDate(i + 1, new java.sql.Date(Utility.getDate(getValue(array.getData(i), 0, "val").toString())));
                            } else if (vcode.toString().equals("time")) {
                                statement.setTime(i + 1, new java.sql.Time(Utility.getTime(getValue(array.getData(i), 0, "val").toString())));
                            } else if (vcode.equals("datetime") || vcode.equals("todatetime")) {
                                statement.setTimestamp(i + 1, new java.sql.Timestamp(Utility.getDateTime(getValue(array.getData(i), 0, "val").toString())));
                            } else if (vcode.toString().equals("timestamp")) {
                                statement.setTimestamp(i + 1, new java.sql.Timestamp(System.currentTimeMillis()));
                            } else if (array.getData(i).getData(1).toString().equals("int")) {
                                statement.setInt(i + 1, getValue(array.getData(i), 0, "val").toInteger());
                            } else if (vcode.equals("boolean")) {
                                statement.setBoolean(i + 1, getValue(array.getData(i), 0, "val").toBoolean());
                            } else if (vcode.equals("long")) {
                                statement.setLong(i + 1, getValue(array.getData(i), 0, "val").toLong());
                            } else if (vcode.equals("number")) {
                                statement.setLong(i + 1, getValue(array.getData(i), 0, "val").toLong());
                            } else if (vcode.equals("double")) {
                                statement.setDouble(i + 1, getValue(array.getData(i), 0, "val").toDouble());
                            } else if (vcode.equals("decimal")) {
                                statement.setDouble(i + 1, getValue(array.getData(i), 0, "val").toDouble());
                            } else if (vcode.equals("float")) {
                                statement.setFloat(i + 1, getValue(array.getData(i), 0, "val").toNumber().floatValue());
                            } else if (vcode.equals("noescape")) {
                                //abaikan
                            } else if (vcode.equals("enull")) {
                                if (getValue(array.getData(i), 0, "val").toString().equalsIgnoreCase("")) {
                                    statement.setNull(i + 1, java.sql.Types.NULL);
                                } else {
                                    statement.setString(i + 1, array.getData(i).getData(0).toString());
                                }
                            } else if (vcode.equals("clob")) {
                                statement.setClob(i + 1, new InputStreamReader(new ByteArrayInputStream(getValue(array.getData(i), 0, "val").toString().getBytes("UTF_8"))));
                            } else if (vcode.equals("blob")) {
                                statement.setBlob(i + 1, new ByteArrayInputStream(getValue(array.getData(i), 0, "val").toString().getBytes("UTF_8")));
                            } else if (vcode.startsWith("out") && statement instanceof CallableStatement) {
                                CallableStatement callableStatement = (CallableStatement) statement;
                                /*String vname = getValue(array.getData(i), 2, "key").toString();
                                    vname = vname.startsWith("@")?vname.substring(1):vname;*/
                                int vname = i + 1;
                                if (vcode.endsWith("int")) {
                                    callableStatement.registerOutParameter(vname, java.sql.Types.INTEGER);
                                } else if (vcode.endsWith("double")) {
                                    callableStatement.registerOutParameter(vname, java.sql.Types.DOUBLE);
                                } else if (vcode.endsWith("float")) {
                                    callableStatement.registerOutParameter(vname, java.sql.Types.FLOAT);
                                } else if (vcode.endsWith("boolean")) {
                                    callableStatement.registerOutParameter(vname, java.sql.Types.BOOLEAN);
                                } else if (vcode.endsWith("clob")) {
                                    callableStatement.registerOutParameter(vname, java.sql.Types.CLOB);
                                } else if (vcode.endsWith("blob")) {
                                    callableStatement.registerOutParameter(vname, java.sql.Types.BLOB);
                                } else if (vcode.endsWith("number")) {
                                    callableStatement.registerOutParameter(vname, java.sql.Types.NUMERIC);
                                } else if (vcode.endsWith("decimal")) {
                                    callableStatement.registerOutParameter(vname, java.sql.Types.DECIMAL);
                                } else if (vcode.endsWith("date")) {
                                    callableStatement.registerOutParameter(vname, java.sql.Types.DATE);
                                } else if (vcode.endsWith("datetime")) {
                                    callableStatement.registerOutParameter(vname, java.sql.Types.TIMESTAMP);
                                } else if (vcode.endsWith("time")) {
                                    callableStatement.registerOutParameter(vname, java.sql.Types.TIME);
                                } else if (vcode.endsWith("nikitaset")) {
                                    callableStatement.registerOutParameter(vname, OracleTypes.CURSOR);
                                } else {
                                    callableStatement.registerOutParameter(vname, java.sql.Types.VARCHAR);
                                }
                                out.addElement(vname);
                            } else {
                                statement.setString(i + 1, array.getData(i).getData(0).toString());
                            }
                        } else if (array.getData(i).isNumber()) {
                            if (array.getData(i).toDouble() == array.getData(i).toLong()) {
                                statement.setLong(i + 1, array.getData(i).toLong());
                            } else {
                                statement.setDouble(i + 1, array.getData(i).toDouble());
                            }
                        } else {
                            statement.setString(i + 1, array.getData(i).toString());
                        }
                    } //end for               
                    ResultSet rs = null;

                    /*
                        if (sql.trim().toLowerCase().startsWith("select")) {
                            rs = statement.executeQuery();
                        }else{
                            statement.execute();
                            try {
                                rs=statement.getGeneratedKeys();
                            } catch (Exception e) {  }
                        }
                     */
                    statement.execute();
                    if (sql.trim().toLowerCase().startsWith("insert")) {
                        rs = statement.getGeneratedKeys();
                    } else if (sql.trim().toLowerCase().startsWith("call") || sql.trim().toLowerCase().startsWith("{")) {
                        CallableStatement callableStatement = (CallableStatement) statement;

                        //callableStatement.getRef("").getObject()
                        rs = statement.getResultSet();
                    } else {
                        rs = statement.getResultSet();
                    }

                    if (fname != null) {
                        int i = resulttoFile(rs, fname, tname);
                        rst = new Nikitaset(Nset.newObject().setData("rows", i).toJSON());
                    } else {
                        rst = new Nikitaset(rs, page, rowperpage, limit);
                    }

                    //05/07/2016
                    if (sql.trim().toLowerCase().startsWith("call") || sql.trim().toLowerCase().startsWith("{")) {
                        CallableStatement callableStatement = (CallableStatement) statement;

                        //callableStatement.getRef("").getObject()
                        Nset outresult = Nset.newObject();
                        for (int i = 0; i < out.size(); i++) {
                            Integer integer = out.get(i);
                            callableStatement.getDouble(i);
                            //outresult.setData("", null);
                        }
                    }

                    //13lk overide time nikitaconnection
                    long time = System.currentTimeMillis() - start;
                    rst.setInfo(new Nset(rst.getInfo()).setData("time", time).setData("coretime", time));

                    if (rs != null) {
                        rs.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                } else {

                    Statement statement = conn.createStatement(getDatabaseCore() == CORE_SQLITE ? java.sql.ResultSet.TYPE_FORWARD_ONLY : java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE, java.sql.ResultSet.CONCUR_READ_ONLY);
                    String queryString = sql;
                    //Statement statement = conn.createStatement();
                    //ResultSet rs = statement.executeQuery(sql);
                    //statement.getGeneratedKeys();
                    //keys.next();  
                    //key = keys.getInt(1);  
                    //keys.close();
                    ResultSet rs = null;
                    if (queryString.trim().toLowerCase().startsWith("select")) {
                        rs = statement.executeQuery(queryString);
                    } else {
                        statement.execute(queryString, Statement.RETURN_GENERATED_KEYS);
                        try {
                            rs = statement.getGeneratedKeys();
                        } catch (Exception e) {
                        }
                    }

                    if (fname != null) {
                        int i = resulttoFile(rs, fname, tname);
                        rst = new Nikitaset(Nset.newObject().setData("rows", i).toJSON());
                    } else {
                        rst = new Nikitaset(rs, page, rowperpage, limit);
                    }

                    if (rs != null) {
                        rs.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
            } catch (Exception e) {
                rst = new Nikitaset(e.getMessage());
                resulttoFile(null, fname, tname);
            }
        } else {
            rst = new Nikitaset(getError());
            resulttoFile(null, fname, tname);
        }
        if (rst.getInfo() instanceof Nset) {
            ((Nset) rst.getInfo()).setData("core", getDatabaseCore());
        } else {
            rst.setInfo(Nset.newObject());
            ((Nset) rst.getInfo()).setData("core", getDatabaseCore());
        }
        return rst;
    }

    private Nset getValue(Nset data, int index, String name) {
        if (data.isNsetArray()) {
            return data.getData(index);
        } else if (data.isNsetObject()) {
            return data.getData(name);
        }
        return Nset.newNull();
    }
    private static Nset defLogin = Nset.readJSON((""
            + "{"
            + "'$'      :{'class':'com.mysql.jdbc.Driver','url':'jdbc:mysql://localhost/nfmaster','user':'hikari','pass':'hikari'},"
            + "'logic'  :{'class':'com.mysql.jdbc.Driver','url':'jdbc:mysql://localhost/nikitamobile','user':'root','pass':'root'},"
            + "'default':{'class':'com.mysql.jdbc.Driver','url':'jdbc:mysql://localhost/phpmyadmin','user':'root','pass':'root'},"
            + "'finance':{'class':'','url':'','user':'','pass':''}"
            + "}"
            + ""), true);

    static {
        NikitaConnection.setDefaultConnectionSetting();
    }

    public static void setDefaultConnectionSetting() {
        setDefaultConnectionSetting(new NikitaProperty("nikita.ini"));
        //System.out.println(defLogin.toJSON());
    }

    public static Nset getDefaultPropertySetting() {
        return defLogin;
    }

    public static void setDefaultConnectionSetting(InputStream inputStream) {
        setDefaultConnectionSetting(new NikitaProperty(inputStream));
    }

    private static void setDefaultConnectionSetting(NikitaProperty nikitaProperty) {
        defLogin = nikitaProperty.read();

        if (defLogin.getData("init").getData("nikitaonline").toString().equals("true")) {
            Nikitaset nikitaset = new NikitaConnection().getConnection(NikitaConnection.LOGIC).Query("SELECT connname,connusername,connpassword,connclass,connurl FROM web_connection;");
            for (int i = 0; i < nikitaset.getRows(); i++) {
                if (!nikitaset.getText(i, 0).equals("")) {
                    Nset n = Nset.newObject();
                    n.setData("user", nikitaset.getText(i, 1).toString());
                    n.setData("pass", nikitaset.getText(i, 2).toString());
                    n.setData("class", nikitaset.getText(i, 3).toString());
                    n.setData("url", nikitaset.getText(i, 4).toString());
                    defLogin.setData(nikitaset.getText(i, 0).toString(), n);
                }
            }
            //System.out.println(defLogin.toJSON());
        }

    }

    public boolean isNikitaConnection() {
        return false;
    }
    protected int core = -1;

    public int getDatabaseCore() {
        return core;
    }

    public static final int CORE_MYSQL = 0;
    public static final int CORE_SQLSERVER = 1;
    public static final int CORE_ORACLE = 2;
    public static final int CORE_SQLITE = 3;

    public static String convertType(String type, int toDB) {
        type = type.toUpperCase();//CORE_MYSQL[0],CORE_SQLSERVER[1],CORE_ORACLE[2],CORE_SQLLITE[3]
        String[] ret = new String[]{"", "", "", ""};
        if (type.equals("STRING")) {
            ret = new String[]{"VARCHAR", "VARCHAR", "VARCHAR2", "STRING"};
        } else if (type.startsWith("VARCHAR")) {
            ret = new String[]{"VARCHAR", "VARCHAR", "VARCHAR2", "STRING"};
        } else if (type.startsWith("TEXT")) {
            ret = new String[]{"TEXT", "TEXT", "BLOB", "STRING"};

        } else if (type.startsWith("DATE")) {
            ret = new String[]{"DATE", "DATE", "DATE", "DATE"};
        } else if (type.startsWith("DATETIME")) {
            ret = new String[]{"DATETIME", "DATETIME", "DATETIME", "DATETIME"};
        } else if (type.startsWith("TIMESTAMP")) {
            ret = new String[]{"TIMESTAMP", "TIMESTAMP", "TIMESTAMP", "TIMESTAMP"};

        } else if (type.startsWith("BLOB")) {
            ret = new String[]{"BLOB", "BLOB", "BLOB", "BLOB"};
        } else if (type.startsWith("NULL")) {
            ret = new String[]{"VARCHAR", "VARCHAR", "VARCHAR2", "STRING"};
        } else if (type.startsWith("BOOLEAN")) {
            ret = new String[]{"BOOLEAN", "BOOLEAN", "VARCHAR2", "STRING"};
        } else if (type.startsWith("ENUM")) {
            ret = new String[]{"STRING", "STRING", "STRING", "STRING"};

        } else if (type.startsWith("INT")) {
            ret = new String[]{"INT", "INT", "INT", "INTEGER"};
        } else if (type.startsWith("BIGINT")) {
            ret = new String[]{"BIGINT", "BIGINT", "BIGINT", "INTEGER"};
        } else if (type.startsWith("TINYINT")) {
            ret = new String[]{"TINYINT", "TINYINT", "NUMBER", "INTEGER"};
        } else if (type.startsWith("LONG")) {
            ret = new String[]{"BIGINT", "BIGINT", "NUMBER", "INTEGER"};
        } else if (type.startsWith("FLOAT")) {
            ret = new String[]{"FLOAT", "FLOAT", "NUMBER", "FLOAT"};
        } else if (type.startsWith("DECIMAL")) {
            ret = new String[]{"DECIMAL", "DECIMAL", "NUMBER", "FLOAT"};
        } else if (type.startsWith("REAL")) {
            ret = new String[]{"DECIMAL", "REAL", "NUMBER", "FLOAT"};
        } else if (type.startsWith("NUMBER")) {
            ret = new String[]{"DECIMAL", "DECIMAL", "NUMBER", "FLOAT"};
        }

        try {
            return ret[toDB];
        } catch (Exception e) {
            return "";
        }
    }

    public static int getSqlTypeNumber(String type) {
        if (type.equalsIgnoreCase("BIT")) {
            return Types.BIT;
        } else if (type.equalsIgnoreCase("TINYINT")) {
            return Types.TINYINT;
        } else if (type.equalsIgnoreCase("SMALLINT")) {
            return Types.SMALLINT;
        } else if (type.equalsIgnoreCase("INTEGER")) {
            return Types.INTEGER;
        } else if (type.equalsIgnoreCase("BIGINT")) {
            return Types.BIGINT;
        } else if (type.equalsIgnoreCase("FLOAT")) {
            return Types.FLOAT;
        } else if (type.equalsIgnoreCase("REAL")) {
            return Types.REAL;
        } else if (type.equalsIgnoreCase("DOUBLE")) {
            return Types.DOUBLE;
        } else if (type.equalsIgnoreCase("NUMERIC")) {
            return Types.NUMERIC;
        } else if (type.equalsIgnoreCase("DECIMAL")) {
            return Types.DECIMAL;
        } else if (type.equalsIgnoreCase("CHAR")) {
            return Types.CHAR;
        } else if (type.equalsIgnoreCase("VARCHAR")) {
            return Types.VARCHAR;
        } else if (type.equalsIgnoreCase("LONGVARCHAR")) {
            return Types.LONGVARCHAR;
        } else if (type.equalsIgnoreCase("DATE")) {
            return Types.DATE;
        } else if (type.equalsIgnoreCase("TIME")) {
            return Types.TIME;
        } else if (type.equalsIgnoreCase("TIMESTAMP")) {
            return Types.TIMESTAMP;
        } else if (type.equalsIgnoreCase("BINARY")) {
            return Types.BINARY;
        } else if (type.equalsIgnoreCase("VARBINARY")) {
            return Types.VARBINARY;
        } else if (type.equalsIgnoreCase("LONGVARBINARY")) {
            return Types.LONGVARBINARY;
        } else if (type.equalsIgnoreCase("NULL")) {
            return Types.NULL;
        } else if (type.equalsIgnoreCase("OTHER")) {
            return Types.OTHER;
        } else if (type.equalsIgnoreCase("JAVA_OBJECT")) {
            return Types.JAVA_OBJECT;
        } else if (type.equalsIgnoreCase("DISTINCT")) {
            return Types.DISTINCT;
        } else if (type.equalsIgnoreCase("STRUCT")) {
            return Types.STRUCT;
        } else if (type.equalsIgnoreCase("ARRAY")) {
            return Types.ARRAY;
        } else if (type.equalsIgnoreCase("BLOB")) {
            return Types.BLOB;
        } else if (type.equalsIgnoreCase("CLOB")) {
            return Types.CLOB;
        } else if (type.equalsIgnoreCase("REF")) {
            return Types.REF;
        } else if (type.equalsIgnoreCase("DATALINK")) {
            return Types.DATALINK;
        } else if (type.equalsIgnoreCase("BOOLEAN")) {
            return Types.BOOLEAN;
        } else if (type.equalsIgnoreCase("ROWID")) {
            return Types.ROWID;
        } else if (type.equalsIgnoreCase("NCHAR")) {
            return Types.NCHAR;
        } else if (type.equalsIgnoreCase("NVARCHAR")) {
            return Types.NVARCHAR;
        } else if (type.equalsIgnoreCase("LONGNVARCHAR")) {
            return Types.LONGNVARCHAR;
        } else if (type.equalsIgnoreCase("NCLOB")) {
            return Types.NCLOB;
        } else if (type.equalsIgnoreCase("SQLXML")) {
            return Types.SQLXML;
        }
        return 0;
    }

    public static String getSqlTypeName(int type) {
        switch (type) {
            case Types.BIT:
                return "BIT";
            case Types.TINYINT:
                return "TINYINT";
            case Types.SMALLINT:
                return "SMALLINT";
            case Types.INTEGER:
                return "INTEGER";
            case Types.BIGINT:
                return "BIGINT";
            case Types.FLOAT:
                return "FLOAT";
            case Types.REAL:
                return "REAL";
            case Types.DOUBLE:
                return "DOUBLE";
            case Types.NUMERIC:
                return "NUMERIC";
            case Types.DECIMAL:
                return "DECIMAL";
            case Types.CHAR:
                return "CHAR";
            case Types.VARCHAR:
                return "VARCHAR";
            case Types.LONGVARCHAR:
                return "LONGVARCHAR";
            case Types.DATE:
                return "DATE";
            case Types.TIME:
                return "TIME";
            case Types.TIMESTAMP:
                return "TIMESTAMP";
            case Types.BINARY:
                return "BINARY";
            case Types.VARBINARY:
                return "VARBINARY";
            case Types.LONGVARBINARY:
                return "LONGVARBINARY";
            case Types.NULL:
                return "NULL";
            case Types.OTHER:
                return "OTHER";
            case Types.JAVA_OBJECT:
                return "JAVA_OBJECT";
            case Types.DISTINCT:
                return "DISTINCT";
            case Types.STRUCT:
                return "STRUCT";
            case Types.ARRAY:
                return "ARRAY";
            case Types.BLOB:
                return "BLOB";
            case Types.CLOB:
                return "CLOB";
            case Types.REF:
                return "REF";
            case Types.DATALINK:
                return "DATALINK";
            case Types.BOOLEAN:
                return "BOOLEAN";
            case Types.ROWID:
                return "ROWID";
            case Types.NCHAR:
                return "NCHAR";
            case Types.NVARCHAR:
                return "NVARCHAR";
            case Types.LONGNVARCHAR:
                return "LONGNVARCHAR";
            case Types.NCLOB:
                return "NCLOB";
            case Types.SQLXML:
                return "SQLXML";
        }
        return "?";
    }

}
