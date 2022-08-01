package com.rkrzmail.nikita.data;

import com.rkrzmail.nikita.utility.Utility;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * created by 13k.mail@gmail.com
 */
public class Nikitaset implements IRecordset {

    //{"nfid":"Nikitaset","error":"","info":null,"header":[],"data":[[]]}
    private String error;
    private Object info;
    private Vector<String> header = new Vector<String>();
    private Vector<Vector<String>> data = new Vector<Vector<String>>();

    public Nikitaset(ResultSet resultSet) {
        this(resultSet, -1, -1);
    }

    public Nikitaset(ResultSet resultSet, int page, int rowperpage) {
        this(resultSet, page, rowperpage, 0);
    }

    public Nikitaset(ResultSet resultSet, int page, int rowperpage, int limit) {
        try {
            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                header.addElement(resultSet.getMetaData().getColumnLabel(i + 1));//new
                //header.addElement(resultSet.getMetaData().getColumnName(i+1)); //old
            }
            if (rowperpage <= 0) {
                while (resultSet.next()) {
                    Vector<String> field = new Vector<String>();
                    for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                        try {
                            String sdata = resultSet.getString(i + 1);
                            field.addElement(sdata != null ? sdata : "");
                        } catch (Exception e) {
                            field.addElement("");
                        }
                    }
                    data.addElement(field);
                }
            } else {
                try {
                    int rows = 0;
                    if (limit >= 1) {
                        rows = limit;
                        while (resultSet.next()) {
                            Vector<String> field = new Vector<String>();
                            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                                try {
                                    String sdata = resultSet.getString(i + 1);
                                    field.addElement(sdata != null ? sdata : "");
                                } catch (Exception e) {
                                    field.addElement("");
                                }
                            }
                            data.addElement(field);
                        }
                    } else if (resultSet.getStatement().getResultSetType() == ResultSet.TYPE_FORWARD_ONLY) {
                        int currrow = rowperpage * (page - 1);
                        currrow = currrow < 0 ? 0 : currrow;
                        currrow = currrow + 1;

                        while (resultSet.next()) {
                            if (resultSet.getRow() >= currrow && resultSet.getRow() < currrow + rowperpage) {
                                Vector<String> field = new Vector<String>();
                                for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                                    try {
                                        String sdata = resultSet.getString(i + 1);
                                        field.addElement(sdata != null ? sdata : "");
                                    } catch (Exception e) {
                                        field.addElement("");
                                    }
                                }
                                data.addElement(field);
                            }
                            rows = resultSet.getRow();
                        }

                        page = page <= 0 ? 0 : page;
                        int pmax = rows / rowperpage + (rows % rowperpage <= 0 ? 0 : 1);
                        page = page >= pmax ? pmax : page;
                        page = page <= 0 ? 1 : page;

                    } else {

                        resultSet.last();
                        rows = resultSet.getRow();
                        page = page <= 0 ? 0 : page;
                        int pmax = rows / rowperpage + (rows % rowperpage <= 0 ? 0 : 1);
                        page = page >= pmax ? pmax : page;
                        page = page <= 0 ? 1 : page;

                        int currrow = rowperpage * (page - 1);
                        currrow = currrow < 0 ? 0 : currrow;

                        for (int row = currrow; row < Math.min(currrow + rowperpage, rows); row++) {
                            resultSet.absolute(row + 1);//start with 1
                            Vector<String> field = new Vector<String>();
                            for (int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) {
                                try {
                                    String sdata = resultSet.getString(i + 1);
                                    field.addElement(sdata != null ? sdata : "");
                                } catch (Exception e) {
                                    field.addElement("");
                                }
                            }
                            data.addElement(field);
                        }
                    }

                    info = Nset.newObject().setData("nfid", "Nset").setData("mode", "paging").setData("rows", rows).setData("row", rowperpage).setData("page", page);

                } catch (Exception e) {
                }
            }
            //add info type n len fields
            if (info instanceof Nset) {
            } else {
                info = Nset.newObject();
            }
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

            ((Nset) info).setData("metadata", Nset.newObject().setData("type", metatype).setData("size", metasize).setData("name", metaname).setData("label", metalabl));
            //System.out.println(((Nset)info).toJSON());
        } catch (Exception e) {
        }
    }

    public static boolean isNikitaset(Nset x) {
        if (x.getInternalObject() instanceof Hashtable) {
            if (x.getData("nfid").toString().equals("Nikitaset")) {
                Hashtable keys = (Hashtable) x.getInternalObject();
                if (keys.containsKey("error") && keys.containsKey("info") && keys.containsKey("header") & keys.containsKey("data")) {
                    return true;
                }
            }
        }
        return false;
    }

    //new 03102016 //==========================//
    public static boolean isNikitaset(Nson x) {
        if (x.getInternalObject() instanceof Map) {
            if (x.getData("nfid").toString().equals("Nikitaset")) {
                Map keys = (Map) x.getInternalObject();
                if (keys.containsKey("error") && keys.containsKey("info") && keys.containsKey("header") & keys.containsKey("data")) {
                    return true;
                }
            }
        }
        return false;
    }

    //==========================//
    public Nikitaset(IRecordset recordset) {
        error = recordset.getError();
        info = recordset.getInfo();
        header = recordset.getDataAllHeader();
        data = recordset.getDataAllVector();
    }

    public Nikitaset(Nset nikitaset) {
        error = nikitaset.getData("error").toString();
        info = nikitaset.getData("info").getInternalObject();
        try {
            header = (Vector<String>) nikitaset.getData("header").getInternalObject();
        } catch (Exception e) {
        }
        try {
            data = (Vector<Vector<String>>) nikitaset.getData("data").getInternalObject();
        } catch (Exception e) {
        }
    }

    public Nikitaset(String error) {
        this.error = error;
    }

    public Nikitaset(Vector<String> header, Vector<Vector<String>> data) {
        this.header = header;
        this.data = data;
    }

    public Nikitaset(Vector<String> header, Vector<Vector<String>> data, String error, Object info) {
        this.header = header;
        this.data = data;

        this.error = error;
        this.info = info;
    }

    public void setAliasName(Vector<String> header) {
        this.header = header;
    }

    public void setInfo(Object info) {
        this.info = info;
    }

    public void setAllData(Vector<Vector<String>> dtAll) {
        this.data = dtAll;
    }

    public Nset toNset() {
        Hashtable mst = new Hashtable();
        mst.put("nfid", "Nikitaset");
        mst.put("error", this.getError());
        mst.put("info", this.getInfo() != null ? this.getInfo() : Nset.newNull());
        mst.put("header", this.getDataAllHeader());
        mst.put("data", this.getDataAllVector());
        return new Nset(mst);
    }

    @Override
    public Vector<String> getDataAllHeader() {
        return this.header != null ? this.header : new Vector<String>();
    }

    @Override
    public Vector<Vector<String>> getDataAllVector() {
        return this.data != null ? this.data : new Vector<Vector<String>>();
    }

    @Override
    public String getError() {
        return error != null ? error : "";
    }

    @Override
    public Object getInfo() {
        return info;
    }

    @Override
    public int getRows() {
        return getDataAllVector().size();
    }

    @Override
    public int getCols() {
        return getDataAllHeader().size();
    }

    @Override
    public String getText(int row, int col) {
        try {
            String result = data.elementAt(row).elementAt(col);
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
        }
        return "";
    }

    //26/06/2016 not used
    private String getText(int row, String colname, boolean label) {
        //alias[AS] support 26/06/2016
        if (label && getInfo() instanceof Nset) {
            Nset n = ((Nset) getInfo());
            if (n.containsKey("metadata") && n.getData("metadata").containsKey("label")) {
                Nset nAS = n.getData("metadata").getData("label");
                if (nAS.getInternalObject() instanceof Vector) {
                    try {
                        for (int i = 0; i < nAS.getSize(); i++) {
                            String s = nAS.getData(i).toString();
                            if (nAS.getData(i).toString().equalsIgnoreCase(colname)) {//nocase sensitife
                                return getText(row, i);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        } // end AS    
        return getText(row, colname);//getText(row, header.indexOf(colname))
    }

    @Override
    public String getText(int row, String colname) {
        //int col = header!=null ? header.indexOf(colname): -1;
        int col = -1;
        if (header != null) {
            for (int i = 0; i < header.size(); i++) {
                if (header.elementAt(i).equalsIgnoreCase(colname)) {//nocase sensitife
                    col = i;
                    break;
                }
            }
        }
        if (col >= 0) {
            return getText(row, col);
        } else {
            if (getInfo() instanceof Nset) {
                Nset n = ((Nset) getInfo());
                if (n.containsKey("metadata") && n.getData("metadata").containsKey("name")) {
                    Nset nAS = n.getData("metadata").getData("name");
                    for (int i = 0; i < nAS.getSize(); i++) {
                        String name = nAS.getData(i).toString();
                        if (name.equalsIgnoreCase(colname)) {//nocase sensitife
                            return getText(row, i);
                        }
                    }
                }
            }
        }
        return "";
        //return getText(row, header.indexOf(colname));
    }

    @Override
    public String getHeader(int col) {
        try {
            String result = header.elementAt(col);
            if (result != null) {
                return result;
            }
        } catch (Exception e) {
        }
        return "";
    }

    public void union(Nikitaset rst) {
        data.addAll(rst.getDataAllVector());
    }

    public void unioncol(Nikitaset rst) {
        for (int i = 0; i < rst.getDataAllHeader().size(); i++) {
            header.addAll(rst.getDataAllHeader());
            //add metadata
            if (rst.getInfo() instanceof Nset && info instanceof Nset) {
                Nset nInfo = (Nset) rst.getInfo();
                Nset cInfo = (Nset) info;//current info
                if (nInfo.containsKey("metadata")) {
                    if (nInfo.getData("metadata").getData("size").isNsetArray()) {
                        try {
                            Vector v = (Vector) cInfo.getData("metadata").getData("size").getInternalObject();
                            Vector z = (Vector) nInfo.getData("metadata").getData("size").getInternalObject();
                            v.addAll(z);
                            cInfo.getData("metadata").setData("size", new Nset(v));
                        } catch (Exception e) {
                        }
                    }
                    if (nInfo.getData("metadata").getData("type").isNsetArray()) {
                        try {
                            Vector v = (Vector) cInfo.getData("metadata").getData("type").getInternalObject();
                            Vector z = (Vector) nInfo.getData("metadata").getData("type").getInternalObject();
                            v.addAll(z);
                            cInfo.getData("metadata").setData("type", new Nset(v));
                        } catch (Exception e) {
                        }
                    }
                    if (nInfo.getData("metadata").getData("label").isNsetArray()) {
                        try {
                            Vector v = (Vector) cInfo.getData("metadata").getData("label").getInternalObject();
                            Vector z = (Vector) nInfo.getData("metadata").getData("label").getInternalObject();
                            v.addAll(z);
                            cInfo.getData("metadata").setData("label", new Nset(v));
                        } catch (Exception e) {
                        }
                    }
                    if (nInfo.getData("metadata").getData("name").isNsetArray()) {
                        try {
                            Vector v = (Vector) cInfo.getData("metadata").getData("name").getInternalObject();
                            Vector z = (Vector) nInfo.getData("metadata").getData("name").getInternalObject();
                            v.addAll(z);
                            cInfo.getData("metadata").setData("name", new Nset(v));
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }

        for (int i = 0; i < Math.min(data.size(), rst.getDataAllVector().size()); i++) {
            try {
                data.elementAt(i).addAll(rst.getDataAllVector().elementAt(i));
            } catch (Exception e) {
            }
        }
    }

    public void swapColomOrder(int col, int tocol) {
        try {
            String h1 = header.elementAt(col);
            String h2 = header.elementAt(tocol);
            header.setElementAt(h2, col);
            header.setElementAt(h1, tocol);

            for (int row = 0; row < data.size(); row++) {
                h1 = data.elementAt(row).elementAt(col);
                h2 = data.elementAt(row).elementAt(tocol);
                data.elementAt(row).setElementAt(h2, col);
                data.elementAt(row).setElementAt(h1, tocol);
            }
        } catch (Exception e) {
        }
    }

    public Vector<Vector<String>> copyDataAllVectorAtCol(int... col) {
        Vector<Vector<String>> copy = new Vector<Vector<String>>();
        for (int row = 0; row < data.size(); row++) {
            Vector<String> vector = new Vector<String>();
            for (int j = 0; j < col.length; j++) {
                vector.addElement(getText(row, col[j]));
            }
            copy.addElement(vector);
        }
        return copy;
    }

    public Object getStream(String stream) {
        Nset n = Nset.readJSON(stream);
        if (n.getData(0).toString().equals("error")) {
            return getError();
        } else if (n.getData(0).toString().equals("header")) {
            if (n.getArraySize() == 1) {
                return new Nset(getDataAllHeader());
            } else {
                return getHeader(n.getData(1).toInteger());
            }
        } else if (n.getData(0).toString().equals("data")) {//data,row,col
            if (n.getArraySize() == 1) {
                return new Nset(getDataAllVector());
            } else if (n.getArraySize() == 2) {
                return new Nset(getDataAllVector()).getData(n.getData(1).toInteger());
            } else if (n.getArraySize() == 3) {
                String col = n.getData(2).toString();
                if (Utility.isNumeric(col)) {
                    return getText(n.getData(1).toInteger(), n.getData(2).toInteger());
                } else {
                    return getText(n.getData(1).toInteger(), n.getData(2).toString());
                }
            }
            return "";
        } else if (n.getData(0).toString().equals("rows")) {
            return getRows();
        } else if (n.getData(0).toString().equals("maxrows")) {
            int maxrows = getRows();
            Nset info = new Nset(getInfo());
            if (info.getData("nfid").toString().equals("Nset") && info.getData("mode").toString().equals("paging")) {
                maxrows = info.getData("rows").toInteger();
                maxrows = (maxrows >= 1 ? maxrows : getRows());
            }
            return maxrows;
        } else if (n.getData(0).toString().equals("page")) {
            int page = 0;
            Nset info = new Nset(getInfo());
            if (info.getData("nfid").toString().equals("Nset") && info.getData("mode").toString().equals("paging")) {
                page = info.getData("page").toInteger();
            }
            return page;
        } else if (n.getData(0).toString().equals("cols")) {
            return getCols();
        } else if (n.getData(0).toString().equals("copy")) {
            int[] cols = new int[n.getArraySize() - 1];
            for (int i = 0; i < cols.length; i++) {
                cols[i] = n.getData(2).toInteger();
            }
            return copyDataAllVectorAtCol(cols);
        } else if (n.getData(0).toString().equals("clone")) {
            return new Nikitaset(Nset.readJSON(toNset().toJSON()));
        } else if (n.getData(0).toString().equals("core")) {
            return new Nset(getInfo()).getData("core").toInteger();
        } else if (n.getData(0).toString().equals("info")) {
            return getInfo();
            //add 20160117
        } else if (n.getData(0).toString().equals("colname")) {
            return new Nset(getInfo()).getData("metadata").getData("name").toJSON();
        } else if (n.getData(0).toString().equals("label")) {
            return new Nset(getInfo()).getData("metadata").getData("label").toJSON();
        } else if (n.getData(0).toString().equals("ncount")) {//nikitasetcount default 0            
            return new Nset(getInfo()).getData("nsresult").toInteger();
        } else if (n.getData(0).toString().equals("time")) {
            return new Nset(getInfo()).getData("time").toLong();
        } else if (n.getData(0).toString().equals("coretime")) {
            return new Nset(getInfo()).getData("coretime").toLong();
        } else if (n.getData(0).toString().equals("result")) {
            //return callable
            String col = n.getData(1).toString();
            if (Utility.isNumeric(col)) {
                return new Nset(getInfo()).getData("result").getData(n.getData(1).toInteger()).toString();
            } else {

            }
            //add 20170909
        } else if (n.getData(0).toString().equals("select")) {
            return select(n.getData(1).toString(), n.getData(2).toString());
        } else if (n.getArraySize() == 2 && Utility.isNumeric(n.getData(0).toString())) {
            int r = n.getData(0).toInteger();
            String col = n.getData(1).toString();
            if (Utility.isNumeric(col)) {
                return getText(r, n.getData(1).toInteger());
            } else {
                return getText(r, col);
            }
        }

        return "";
    }

    private Nikitaset buffNikitaset;

    private void createNikitasetBuffer(String fields) {
        buffNikitaset = new Nikitaset(Utility.splitVector(fields, ","), new Vector<Vector<String>>());
    }

    private void putNikitasetBuffer(Vector<String> row) {
        buffNikitaset.getDataAllVector().addElement(row);
    }

    public Nikitaset select(String fields, String where) {
        if (fields.trim().equals("") || fields.trim().equals("*")) {
            fields = this.getDataAllHeader().toString();
            fields = fields.startsWith("[") ? fields.substring(1) : fields;
            fields = fields.endsWith("]") ? fields.substring(0, fields.length() - 1) : fields;
        }
        fields = fields.replaceAll(" ", "");
        Vector<String> cols = Utility.splitVector(fields, ",");
        createNikitasetBuffer(fields);
        for (int row = 0; row < this.getRows(); row++) {
            if (whereExppression(this, row, where)) {
                Vector<String> v = new Vector<String>();
                for (int col = 0; col < cols.size(); col++) {
                    v.addElement(this.getText(row, cols.elementAt(col)));
                }
                putNikitasetBuffer(v);
            }
        }
        return buffNikitaset;
    }
    NikitaExpression expression = new NikitaExpression();

    private boolean whereExppression(final Nikitaset nikitaset, final int row, String where) {
        if (where.trim().equals("")) {
            return true;
        }
        return Boolean.valueOf(String.valueOf(
                expression.expression(where, new Hashtable() {
                    public synchronized Object get(Object key) {
                        String colname = String.valueOf(key);

                        return nikitaset.getText(row, colname);
                    }
                })
        ));
    }
}
