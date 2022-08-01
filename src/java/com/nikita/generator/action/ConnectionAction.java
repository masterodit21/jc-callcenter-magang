/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nikita.generator.action;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.nikita.generator.ui.DateTime;
import com.nikita.generator.ui.SmartGrid;
import com.nikita.generator.ui.Tablegrid;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.http.HttpResponse;

/**
 *
 * @author rkrzmail
 */
public class ConnectionAction implements IAction {

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());
        String conn = response.getVirtualString(currdata.getData("args").getData("param1").toString());
        String xsql = response.getVirtualString(currdata.getData("args").getData("param2").toString()); //query 
        String page = currdata.getData("args").getData("param4").toString();
        String args = response.getVirtualString(currdata.getData("args").getData("param3").toString());//arguments use to limit
        String data = currdata.getData("args").getData("param3").toString();
        String result = currdata.getData("args").getData("result").toString();

        String x = xsql;

        Nset ns = ConnectionAction.parseNikitaConnection(response, xsql);
        xsql = ns.getData("query").toString();
        conn = ns.getData("conn").toString();
        data = ns.getData("args").toString();
        //28/11/21
        if ( Utility.getInt(args)>=1 ) {
            xsql = "$LIMIT"+args+": "+xsql;
        }

        //05/07/2016==>transaksi
        String tran = currdata.getData("args").getData("param1").toString();
        String xtrx = response.getVirtualString(currdata.getData("args").getData("param1").toString());
        conn = xtrx + conn;//modify disini
        if (tran.equalsIgnoreCase("(begin)") || tran.equalsIgnoreCase("(setpoint)") || tran.equalsIgnoreCase("(savepoint)")) {
            NikitaConnection nc = response.getConnection(conn);
            nc.setSavepoint();
        } else if (tran.equalsIgnoreCase("(open)")) {
            NikitaConnection nc = response.getConnection(conn);
        } else if (tran.equalsIgnoreCase("(rollback)")) {
            NikitaConnection nc = response.getConnection(conn);
            nc.rollback();
        } else if (tran.equalsIgnoreCase("(close)")) {
            NikitaConnection nc = response.getConnection(conn);
            nc.closeConnection();
        } else if (tran.equalsIgnoreCase("(commit)")) {
            NikitaConnection nc = response.getConnection(conn);
            nc.commit();
        } else if (tran.equalsIgnoreCase("(autocommit)")) {
            NikitaConnection nc = response.getConnection(conn);
            nc.setAutoCommit(true);
        } else if (tran.equalsIgnoreCase("(noautocommit)") || tran.equalsIgnoreCase("(autocommitfalse)")) {
            NikitaConnection nc = response.getConnection(conn);
            nc.setAutoCommit(false);
        } else {
            /*
                update 16/01/2021 swapp connection
             */
            if (tran.trim().startsWith("{")) {
                Nson n = Nson.readJson(tran);
                if (n.containsKey("switchconn")) {
                    conn = n.getData("switchconn").toString();
                }
            }

            conn = ns.getData("conn").toString();
        }

        // argsname    
        if (code.equals("linkservice")) {
            //String Serv = response.getVirtualString(currdata.getData("args").getData("param1").toString()); //nc 
            String LinkQuery = response.getVirtualString(currdata.getData("args").getData("param2").toString());
            Object argsNset = response.getVirtual(currdata.getData("args").getData("param3").toString());

            Nset nLinkQ = Nset.readJSON(LinkQuery);

            String baseurl = response.getVirtualString(nLinkQ.getData("baseurl").toString());
            String service = response.getVirtualString(nLinkQ.getData("service").toString());
            Nset argsLink = nLinkQ.getData("args");
            String linkservice = baseurl + service;

            Nset arg = Nset.newObject();
            for (int i = 0; i < argsLink.getSize(); i++) {
                arg.setData(argsLink.getData(i).getData(0).toString(), response.getVirtualString(argsLink.getData(i).getData(1).toString()));
            }

            if (argsNset instanceof Nset) {
                Nset argsNset1 = (Nset) argsNset;
                String[] keys = argsNset1.getObjectKeys();
                for (int i = 0; i < keys.length; i++) {
                    arg.setData(keys[i], response.getVirtualString(argsNset1.getData(keys[i]).toString()));
                }
            }

            Component comp1 = response.getComponent(page);
            page = response.getVirtualString(currdata.getData("args").getData("param4").toString());
            if (comp1 instanceof Tablegrid) {
                if (request.getParameter("component").equals(comp1.getId())) {
                    ((Tablegrid) comp1).prepareFilter(request.getParameter("action"));
                }
                int currpage = ((Tablegrid) comp1).getCurrentPage() <= -1 ? 1 : ((Tablegrid) comp1).getCurrentPage();
                int showpage = ((Tablegrid) comp1).getShowPerPage() <= -1 ? 10 : ((Tablegrid) comp1).getShowPerPage();

                arg.setData("NIKITA-PAGE-GRID", (currpage + "," + showpage));
            } else if (comp1 instanceof SmartGrid) {//new Component   
                if (request.getParameter("component").equals(comp1.getId())) {
                    ((SmartGrid) comp1).onActionFilter(request.getParameter("action"), request.getParameter("actionnew"));
                }
                int currpage = ((SmartGrid) comp1).getCurrentPage() <= -1 ? 1 : ((SmartGrid) comp1).getCurrentPage();
                int showpage = ((SmartGrid) comp1).getShowPerPage() <= -1 ? 10 : ((SmartGrid) comp1).getShowPerPage();

                arg.setData("NIKITA-PAGE-GRID", (currpage + "," + showpage));
            } else {
                arg.setData("NIKITA-PAGE-GRID", page);
            }
            try {
                HttpResponse httpResponse = NikitaInternet.postHttp(linkservice, (Hashtable) arg.getInternalObject());
                try {
                    response.setVirtual(currdata.getData("args").getData("param8").toString(), httpResponse.getStatusLine().getStatusCode());
                } catch (Exception e) {
                    response.setVirtual(currdata.getData("args").getData("param8").toString(), "");
                }
                InputStream is;
                StringBuffer sb = new StringBuffer();
                try {
                    is = httpResponse.getEntity().getContent();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        sb.append(new String(buffer, 0, length));
                    }
                } catch (Exception ex) {
                    sb.append(Nset.newObject().setData("error", ex.getMessage()).setData("nfid", "error").toJSON());
                }

                //Nikitaset n;                     
                if (comp1 instanceof Tablegrid) {
                    if (request.getParameter("component").equals(comp1.getId())) {
                        //((Tablegrid)comp1).prepareFilter(request.getParameter("action"));
                    }
                    ((Tablegrid) comp1).setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
                        public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                        }
                    }, ((Tablegrid) comp1).getCurrentPage() != -1 ? ((Tablegrid) comp1).getCurrentPage() : 1, ((Tablegrid) comp1).getShowPerPage() >= 1 ? ((Tablegrid) comp1).getShowPerPage() : 10);
                    //n = nc.QueryPage(xsql, ConnectionAction.parseArgs(Nset.readJSON(ns.getData("args").toString()), Nset.readJSON(ns.getData("argsname").toString())), ((Tablegrid)comp).getCurrentPage() ,((Tablegrid)comp).getShowPerPage() );

                    response.refreshComponent(comp1);
                } else if (comp1 instanceof SmartGrid) {//new Component                
                    if (request.getParameter("component").equals(comp1.getId())) {
                        //((SmartGrid)comp1).onActionFilter(request.getParameter("action"), request.getParameter("actionnew"));
                    }
                    ((SmartGrid) comp1).setOnFilterClickListener(new SmartGrid.OnFilterClickListener() {
                        public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                        }
                    }, ((SmartGrid) comp1).getCurrentPage() != -1 ? ((SmartGrid) comp1).getCurrentPage() : 1, ((SmartGrid) comp1).getShowPerPage() >= 1 ? ((SmartGrid) comp1).getShowPerPage() : 10);
                    //n = nc.QueryPage(xsql, ConnectionAction.parseArgs(Nset.readJSON(ns.getData("args").toString()), Nset.readJSON(ns.getData("argsname").toString())), ((SmartGrid)comp).getCurrentPage() ,((SmartGrid)comp).getShowPerPage() );

                    response.refreshComponent(comp1);
                }
                response.setVirtual(currdata.getData("args").getData("result").toString(), sb.toString());
            } catch (Exception e) {
            }
        } else if (code.equals("linkforward")) {
            String baseurl = response.getVirtualString(currdata.getData("args").getData("param1").toString());
            String service = response.getVirtualString(currdata.getData("args").getData("param2").toString());
            Object addArgs = response.getVirtual(currdata.getData("args").getData("param3").toString());
            String linkservice = baseurl + service;

            Nset arg = request.getRequestParameter();
            if (addArgs instanceof Nset) {
                Nset n = (Nset) addArgs;
                String[] keys = n.getObjectKeys();
                for (int i = 0; i < keys.length; i++) {
                    arg.setData(keys[i], response.getVirtualString(n.getData(keys[i]).toString()));
                }
            }
            try {
                HttpResponse httpResponse = NikitaInternet.postHttp(linkservice, (Hashtable) arg.getInternalObject());
                try {
                    response.setVirtual(currdata.getData("args").getData("param8").toString(), httpResponse.getStatusLine().getStatusCode());
                } catch (Exception e) {
                    response.setVirtual(currdata.getData("args").getData("param8").toString(), "");
                }
                InputStream is;
                StringBuffer sb = new StringBuffer();
                try {
                    is = httpResponse.getEntity().getContent();
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        sb.append(new String(buffer, 0, length));
                    }
                } catch (Exception ex) {
                    sb.append(Nset.newObject().setData("error", ex.getMessage()).setData("nfid", "error").toJSON());
                }
                response.setVirtual(currdata.getData("args").getData("param9").toString(), sb.toString());
            } catch (Exception e) {
            }
        } else if (code.equals("query")) {
            NikitaConnection nc;
            /*
            Nset nargs   =  Nset.readJSON(data);
            String[] arg = new String[nargs.getArraySize()<=0?0:nargs.getArraySize()];
            for (int i = 0; i < arg.length; i++) {
                arg[i] = nargs.getData(i).toString();
            }
             */

            if (NikitaService.isModeCloud()) {
                System.out.println("isModeCloud. getConnection : " + String.valueOf(NikitaService.sLinkName.get()));

                if (conn.equalsIgnoreCase("sample") || conn.equalsIgnoreCase("default")) {
                    nc = response.getConnection(conn);
                } else {
                    String prefix = String.valueOf(NikitaService.sLinkName.get());
                    if (prefix.equalsIgnoreCase("") || Utility.isNumeric(prefix)) {
                        nc = response.getConnection(conn);//abaikan
                    } else if (prefix.startsWith("u")) {
                        if (prefix.contains("_")) {
                            if (conn.startsWith(prefix.substring(0, prefix.indexOf("_") + 1))) {
                                nc = response.getConnection(conn);
                            } else {
                                nc = response.getConnection("");
                            }
                        } else {
                            nc = response.getConnection(conn);
                        }
                    } else {
                        nc = response.getConnection(conn);
                    } 
                }
            } else {
                nc = response.getConnection(conn);
            }

            Nikitaset n;
            Component comp = response.getComponent(page);
            page = response.getVirtualString(currdata.getData("args").getData("param4").toString());
            
            //add 12/12/2021
            if (comp instanceof SmartGrid||comp instanceof Tablegrid) {
                if (response.getVirtualString("@+EXPORT-MODE").equalsIgnoreCase("export")) {
                    comp = null;
                    page = "";
                }
            }
            
            if (comp instanceof Tablegrid) {
                if (request.getParameter("component").equals(comp.getId())) {
                    ((Tablegrid) comp).prepareFilter(request.getParameter("action"));
                }
                ((Tablegrid) comp).setOnFilterClickListener(new Tablegrid.OnFilterClickListener() {
                    public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                    }
                }, ((Tablegrid) comp).getCurrentPage() != -1 ? ((Tablegrid) comp).getCurrentPage() : 1, ((Tablegrid) comp).getShowPerPage() >= 1 ? ((Tablegrid) comp).getShowPerPage() : 10);
                //n = nc.QueryPage( ((Tablegrid)comp).getCurrentPage() ,((Tablegrid)comp).getShowPerPage(), xsql, arg.length!=0?arg:null);
                n = nc.QueryPage(xsql, ConnectionAction.parseArgs(Nset.readJSON(ns.getData("args").toString()), Nset.readJSON(ns.getData("argsname").toString())), ((Tablegrid) comp).getCurrentPage(), ((Tablegrid) comp).getShowPerPage());

                response.refreshComponent(comp);
            } else if (comp instanceof SmartGrid) {//new Component                
                if (request.getParameter("component").equals(comp.getId())) {
                    ((SmartGrid) comp).onActionFilter(request.getParameter("action"), request.getParameter("actionnew"));
                }
                ((SmartGrid) comp).setOnFilterClickListener(new SmartGrid.OnFilterClickListener() {
                    public void OnFilter(NikitaRequest request, NikitaResponse response, Component component) {
                    }
                }, ((SmartGrid) comp).getCurrentPage() != -1 ? ((SmartGrid) comp).getCurrentPage() : 1, ((SmartGrid) comp).getShowPerPage() >= 1 ? ((SmartGrid) comp).getShowPerPage() : 10);

                n = nc.QueryPage(xsql, ConnectionAction.parseArgs(Nset.readJSON(ns.getData("args").toString()), Nset.readJSON(ns.getData("argsname").toString())), ((SmartGrid) comp).getCurrentPage(), ((SmartGrid) comp).getShowPerPage());

                response.refreshComponent(comp);
            } else if (!page.trim().equals("")) {

                if (page.contains(",")) {
                    //n = nc.QueryPage( Utility.getInt(page.substring(0, page.indexOf(","))),Utility.getInt(page.substring(page.indexOf(",")+1)), xsql, arg.length!=0?arg:null);
                    n = nc.QueryPage(xsql, ConnectionAction.parseArgs(Nset.readJSON(ns.getData("args").toString()), Nset.readJSON(ns.getData("argsname").toString())), Utility.getInt(page.substring(0, page.indexOf(","))), Utility.getInt(page.substring(page.indexOf(",") + 1)));
                } else if (Utility.isNumeric(page)) {
                    //n = nc.QueryPage(1, Utility.getInt(page) , xsql, arg.length!=0?arg:null);
                    n = nc.QueryPage(xsql, ConnectionAction.parseArgs(Nset.readJSON(ns.getData("args").toString()), Nset.readJSON(ns.getData("argsname").toString())), 1, Utility.getInt(page));
                } else {
                    //n = nc.QueryPage(1,10, xsql, arg.length!=0?arg:null);
                    n = nc.QueryPage(xsql, ConnectionAction.parseArgs(Nset.readJSON(ns.getData("args").toString()), Nset.readJSON(ns.getData("argsname").toString())), 1, 10);
                }
            } else {
                //n = nc.Query(xsql, arg.length!=0?arg:null);
                n = nc.QueryPage(xsql, ConnectionAction.parseArgs(Nset.readJSON(ns.getData("args").toString()), Nset.readJSON(ns.getData("argsname").toString())), -1, -1);
                if (comp instanceof Tablegrid) {
                    ((Tablegrid) comp).setOnFilterClickListener(null, -1, -1);
                    response.refreshComponent(comp);
                }
            }

            //add for database 13k tambahan          
            for (int i = 0; i < n.getCols(); i++) {
                response.setVirtual("!" + n.getHeader(i), n);
            }
            //26-06-2016
            if (n.getInfo() instanceof Nset) {
                Nset vn = ((Nset) n.getInfo());
                if (vn.containsKey("metadata") && vn.getData("metadata").containsKey("name")) {
                    Nset nAS = vn.getData("metadata").getData("name");
                    for (int i = 0; i < nAS.getSize(); i++) {
                        String name = nAS.getData(i).toString();
                        if (!name.equals("")) {
                            response.setVirtual("!" + name, n);
                        }
                    }
                }
            }

            //add 19/09/2016
            if (result.startsWith("$")) {
                Component component = response.getComponent(result);
                component.setData(n.toNset());
                response.refreshComponent(component);
            } else {
                response.setVirtual(result, n);//old
            }

        } else if (code.equals("rollback")) {
            response.getConnection(conn).rollback();
        } else if (code.equals("begin") || code.equals("setsavepoint") || code.equals("savepoint") || code.equals("setpoint")) {
            response.getConnection(conn).setSavepoint();
        } else if (code.equals("commit")) {
            response.getConnection(conn).commit();
        } else if (code.equals("close")) {
            response.getConnection(conn).closeConnection();
        } else if (code.equals("open")) {
            response.getConnection(conn);
        } else if (code.equals("autocommit")) {  //param2
            response.getConnection(conn).setAutoCommit(xsql.equals("1") || xsql.toLowerCase().equals("true") ? true : false);
        }

        return true;
    }

    private int getQueryArgs(String sql) {
        int count = 0;
        for (int i = 0; i < sql.length(); i++) {
            if (sql.charAt(i) == '?') {
                count++;
            }
        }
        return count;
    }

    public static Nset parseArgs(Nset nargs, Nset argname) {
        Nset nret = Nset.newArray();
        String[] arg = new String[nargs.getArraySize() <= 0 ? 0 : nargs.getArraySize()];
        for (int i = 0; i < arg.length; i++) {
            String varname = argname.getData(i).toString().trim();
            if ((varname.startsWith("@") || varname.startsWith("$") || varname.startsWith("!")) && varname.contains("(") && varname.endsWith(")")) {
                varname = varname.substring(varname.indexOf("(") + 1);
                if (varname.contains(")")) {
                    varname = varname.substring(0, varname.indexOf(")"));
                }
            } else {
                varname = "";//default string
            }

            if ("|date|todate|time|datetime|todatetime|timestamp|int|long|decimal|double|float|boolean|".contains(varname) || varname.equals("")) {
                //nret.addData(Nset.newObject().setData("val", nargs.getData(i).toString()).setData("var", varname));
                nret.addData(Nset.newArray().addData(nargs.getData(i).toString()).addData(varname));
            } else {
                //nret.addData(Nset.newObject().setData("val", nargs.getData(i).toString()).setData("var", ""));
                nret.addData(Nset.newArray().addData(nargs.getData(i).toString()).addData(varname));
            }
        }
        return nret;
    }

    public static Nset parseNikitaConnection(NikitaResponse response, String nc) {
        if (nc.startsWith("{")) {
            return parseNikitaConnection(response, Nset.readJSON(nc));
        } else {
            Nset nret = Nset.newObject();
            nret.setData("sql", nc);
            nret.setData("query", nc);
            nret.setData("conn", "default");
            return nret;
        }
    }

    public static Nset parseNikitaConnection(NikitaResponse response, Nset n) {
        Nset nret = Nset.newObject();
        Nset narg = Nset.newArray();
        Nset nals = Nset.newArray();
        StringBuffer sb = new StringBuffer();

        if (n.getData("dbmode").toString().equals("select")) {
            Nset f = Nset.readJSON(n.getData("fields").toString());
            Nset order = Nset.readJSON(n.getData("orderby").getData("orderbys").toString());

            sb.append("SELECT ");
            for (int i = 0; i < f.getArraySize(); i++) {
                sb.append(i >= 1 ? "," : "").append(f.getData(i).toString());
            }
            sb.append(f.getArraySize() >= 1 ? "" : "*");
            sb.append(" FROM ").append(n.getData("tbl").toString()).append(" ");

            if (n.getData("where").getData("type").toString().equals("1")) {
                if (!n.getData("where").getData("logic").toString().equals("0")) {
                    sb.append(" WHERE  ").append(n.getData("where").getData("param").getData("parameter1").toString()).append("=? ");
                    sb.append(n.getData("where").getData("logic").toString().equals("1") ? " OR " : " AND ").append(n.getData("where").getData("param").getData("parameter2").toString()).append("=? ");

                    narg.addData(response.getVirtualString(n.getData("where").getData("paramargs").getData("parameter1").toString()));
                    narg.addData(response.getVirtualString(n.getData("where").getData("paramargs").getData("parameter2").toString()));

                    nals.addData(n.getData("where").getData("paramargs").getData("parameter1").toString());
                    nals.addData(n.getData("where").getData("paramargs").getData("parameter2").toString());
                } else {
                    sb.append(" WHERE  ").append(n.getData("where").getData("param").getData("parameter1").toString()).append("=? ");
                    narg.addData(response.getVirtualString(n.getData("where").getData("paramargs").getData("parameter1").toString()));

                    nals.addData(n.getData("where").getData("paramargs").getData("parameter1").toString());
                }
            } else if (n.getData("where").getData("type").toString().equals("2")) {
                sb.append(" WHERE ").append(n.getData("where").getData("sqlwhere").toString());
                int i = n.getData("argswhere").getObjectKeys().length;
                for (int j = 0; j < i; j++) {
                    narg.addData(response.getVirtualString(n.getData("argswhere").getData("" + j).toString()));
                    nals.addData((n.getData("argswhere").getData("" + j).toString()));
                }
            }

            if (!n.getData("orderby").getData("conditionorders").toString().equals("3")) {
                if (order.getArraySize() >= 1) {
                    sb.append(" ORDER BY ");
                    for (int i = 0; i < order.getArraySize(); i++) {
                        sb.append(i >= 1 ? "," : "").append(order.getData(i).toString());
                    }
                }
                if (n.getData("orderby").getData("conditionorders").toString().equals("1")) {
                    sb.append(" ASC ");
                }
                if (n.getData("orderby").getData("conditionorders").toString().equals("2")) {
                    sb.append(" DESC ");
                }
            } else {
                sb.append(" ORDER BY ").append(n.getData("orderby").getData("customs").toString());
            }

            nret.setData("sql", sb.toString());
            nret.setData("query", sb.toString());
            nret.setData("args", narg);
            nret.setData("argsname", nals);
        } else if (n.getData("dbmode").toString().equals("delete")) {
            Nset f = Nset.readJSON(n.getData("fields").toString());
            sb.append("DELETE FROM ").append(n.getData("tbl").toString()).append(" ");

            if (n.getData("where").getData("type").toString().equals("1")) {
                if (!n.getData("where").getData("logic").toString().equals("0")) {
                    sb.append(" WHERE  ").append(n.getData("where").getData("param").getData("parameter1").toString()).append("=? ");
                    sb.append(n.getData("where").getData("logic").toString().equals("1") ? " OR " : " AND ").append(n.getData("where").getData("param").getData("parameter2").toString()).append("=? ");

                    narg.addData(response.getVirtualString(n.getData("where").getData("paramargs").getData("parameter1").toString()));
                    narg.addData(response.getVirtualString(n.getData("where").getData("paramargs").getData("parameter2").toString()));

                    nals.addData(n.getData("where").getData("paramargs").getData("parameter1").toString());
                    nals.addData(n.getData("where").getData("paramargs").getData("parameter2").toString());
                } else {
                    sb.append(" WHERE  ").append(n.getData("where").getData("param").getData("parameter1").toString()).append("=? ");
                    narg.addData(response.getVirtualString(n.getData("where").getData("paramargs").getData("parameter1").toString()));

                    nals.addData(n.getData("where").getData("paramargs").getData("parameter1").toString());
                }
            } else if (n.getData("where").getData("type").toString().equals("2")) {
                sb.append(" WHERE ").append(n.getData("where").getData("sqlwhere").toString());
                int i = n.getData("argswhere").getObjectKeys().length;
                for (int j = 0; j < i; j++) {
                    narg.addData(response.getVirtualString(n.getData("argswhere").getData("" + j).toString()));
                    nals.addData((n.getData("argswhere").getData("" + j).toString()));
                }
            }

            nret.setData("sql", sb.toString());
            nret.setData("query", sb.toString());
            nret.setData("args", narg);
            nret.setData("argsname", nals);
        } else if (n.getData("dbmode").toString().equals("insert")) {
            sb.append("INSERT INTO ").append(n.getData("tbl").toString()).append(" ( ");
            String[] keys = n.getData("args").getObjectKeys();

            for (int j = 0; j < keys.length; j++) {
                sb.append(j >= 1 ? "," : "").append(keys[j]).append("");
            }

            sb.append(" )VALUES ( ");
            for (int j = 0; j < keys.length; j++) {
                if (n.getData("args").getData(keys[j]).toString().endsWith("(todate)")) {
                    //sb.append(j>=1?",":"").append("TO_DATE(?, 'yyyy/mm/dd hh24:mi:ss')");
                } else {
                    //sb.append(j>=1?",":"").append("?");
                }
                sb.append(j >= 1 ? "," : "").append("?");
                if (n.getData("args").getData(keys[j]).toString().startsWith("$") && !n.getData("args").getData(keys[j]).toString().contains(")") && (response.getComponent(n.getData("args").getData(keys[j]).toString()) instanceof DateTime)) {
                    narg.addData(response.getVirtualString(n.getData("args").getData(keys[j]).toString() + "(datetime)"));
                    nals.addData(response.getVirtualString(n.getData("args").getData(keys[j]).toString() + "(datetime)"));
                } else {
                    narg.addData(response.getVirtualString(n.getData("args").getData(keys[j]).toString()));
                    nals.addData((n.getData("args").getData(keys[j]).toString()));
                }
            }
            sb.append(" ) ");

            nret.setData("sql", sb.toString());
            nret.setData("query", sb.toString());
            nret.setData("args", narg);
            nret.setData("argsname", nals);
        } else if (n.getData("dbmode").toString().equals("update")) {

            sb.append("UPDATE ").append(n.getData("tbl").toString()).append(" ");
            String[] keys = n.getData("args").getObjectKeys();

            for (int j = 0; j < keys.length; j++) {
                if (n.getData("args").getData(keys[j]).toString().endsWith("(todate)")) {
                    //sb.append(j>=1?",":"SET ").append(keys[j]).append("=TO_DATE(?, 'yyyy/mm/dd hh24:mi:ss') ");
                } else {
                    //sb.append(j>=1?",":"SET ").append(keys[j]).append("=? ");
                }
                sb.append(j >= 1 ? "," : "SET ").append(keys[j]).append("=? ");
                if (n.getData("args").getData(keys[j]).toString().startsWith("$") && !n.getData("args").getData(keys[j]).toString().contains(")") && (response.getComponent(n.getData("args").getData(keys[j]).toString()) instanceof DateTime)) {
                    narg.addData(response.getVirtualString(n.getData("args").getData(keys[j]).toString() + "(datetime)"));
                    nals.addData((n.getData("args").getData(keys[j]).toString() + "(datetime)"));
                } else {
                    narg.addData(response.getVirtualString(n.getData("args").getData(keys[j]).toString()));
                    nals.addData((n.getData("args").getData(keys[j]).toString()));
                }
            }

            if (n.getData("where").getData("type").toString().equals("1")) {
                if (!n.getData("where").getData("logic").toString().equals("0")) {
                    sb.append(" WHERE  ").append(n.getData("where").getData("param").getData("parameter1").toString()).append("=? ");
                    sb.append(n.getData("where").getData("logic").toString().equals("1") ? " OR " : " AND ").append(n.getData("where").getData("param").getData("parameter2").toString()).append("=? ");

                    narg.addData(response.getVirtualString(n.getData("where").getData("paramargs").getData("parameter1").toString()));
                    narg.addData(response.getVirtualString(n.getData("where").getData("paramargs").getData("parameter2").toString()));

                    nals.addData((n.getData("where").getData("paramargs").getData("parameter1").toString()));
                    nals.addData((n.getData("where").getData("paramargs").getData("parameter2").toString()));
                } else {
                    sb.append(" WHERE  ").append(n.getData("where").getData("param").getData("parameter1").toString()).append("=? ");
                    narg.addData(response.getVirtualString(n.getData("where").getData("paramargs").getData("parameter1").toString()));

                    nals.addData((n.getData("where").getData("paramargs").getData("parameter1").toString()));

                }
            } else if (n.getData("where").getData("type").toString().equals("2")) {
                sb.append(" WHERE ").append(n.getData("where").getData("sqlwhere").toString());
                int i = n.getData("argswhere").getObjectKeys().length;
                for (int j = 0; j < i; j++) {
                    narg.addData(response.getVirtualString(n.getData("argswhere").getData("" + j).toString()));
                    nals.addData((n.getData("argswhere").getData("" + j).toString()));
                }
            }

            nret.setData("sql", sb.toString());
            nret.setData("query", sb.toString());
            nret.setData("args", narg);
            nret.setData("argsname", nals);
        } else if (n.getData("dbmode").toString().equals("query")) {
            if (n.getData("sql").toString().equals("?")) {
                int i = 1;// n.getData("argswhere").getObjectKeys().length;
                for (int j = 0; j < i; j++) {
                    narg.addData(response.getVirtualString(n.getData("argswhere").getData("" + j).toString()));
                    nret.setData("sql", response.getVirtualString(n.getData("argswhere").getData("" + j).toString()));
                    nret.setData("query", response.getVirtualString(n.getData("argswhere").getData("" + j).toString()));
                    nret.setData("args", narg);
                    nret.setData("argsname", nals);
                }
            } else if (n.getData("sql").toString().startsWith("@")) {
                nret.setData("sql", response.getVirtualString(n.getData("sql").toString()));
                nret.setData("query", response.getVirtualString(n.getData("sql").toString()));
                nret.setData("args", narg);
                nret.setData("argsname", nals);
            } else {
                int i = n.getData("argswhere").getObjectKeys().length;
                for (int j = 0; j < i; j++) {
                    narg.addData(response.getVirtualString(n.getData("argswhere").getData("" + j).toString()));
                    nals.addData((n.getData("argswhere").getData("" + j).toString()));
                }

                nret.setData("sql", n.getData("sql").toString());
                nret.setData("query", n.getData("sql").toString());
                nret.setData("args", narg);
                nret.setData("argsname", nals);
            }
        } else if (n.getData("dbmode").toString().equals("call")) {
            int i = n.getData("argswhere").getObjectKeys().length;
            for (int j = 0; j < i; j++) {
                narg.addData(response.getVirtualString(n.getData("argswhere").getData("" + j).toString()));
                nals.addData((n.getData("argswhere").getData("" + j).toString()));
            }

            nret.setData("callz", n.getData("callz").toString());
            nret.setData("call", n.getData("callz").toString());
            nret.setData("args", narg);
            nret.setData("argsname", nals);
        } else {//query
            nret.setData("sql", n.getData("sql").toString());
            nret.setData("query", n.getData("sql").toString());
        }
        nret.setData("conn", n.getData("conn").toString());

        //System.out.println( n.getData("sql").toString());
        //  System.out.println(narg.toJSON()); 
        return nret;
    }

}
