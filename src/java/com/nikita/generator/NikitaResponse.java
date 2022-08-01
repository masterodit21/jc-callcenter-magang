package com.nikita.generator;

import com.nikita.generator.action.ReportAction;
import com.nikita.generator.action.StringAction;
import com.nikita.generator.action.WebAction;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.nikita.generator.mobile.Generator;
import com.nikita.generator.ui.Function;
import com.nikita.generator.ui.SmartGrid;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.layout.DivLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.NikitaExpression;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Base64Coder;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.MjpegInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * created by 13k.mail@gmail.comnsonCodeScript
 */
public class NikitaResponse {

    private Hashtable virtual = new Hashtable();

    private NikitaServletResponse resp;
    private HttpServletRequest req;
    private Cookie[] cookies;
    private NikitaForm content;
    private Nset actionresult;
    private Nset actionstream = Nset.newArray();
    private Nset actiononload = Nset.newArray();
    private NikitaLogic nlogic;
    private Vector<NikitaViewV3> nikitaViewV3s = new Vector<NikitaViewV3>();

    private String currCompId;

    public String getCurrCompId() {
        return currCompId;
    }

    public void setCurrCompId(String currCompId) {
        this.currCompId = currCompId;
    }

    private Nset nNikitaFormPublic = null;

    public Nset getNikitaFormPublic() {
        if (nNikitaFormPublic != null) {
            return nNikitaFormPublic;
        } else {
            nNikitaFormPublic = Nset.readJSON(getVirtualString("@+SESSION-NikitaFormPublic"));
            if (!nNikitaFormPublic.isNsetObject()) {
                nNikitaFormPublic = Nset.newObject();
                setVirtualRegistered("@+SESSION-NikitaFormPublic", "{}");
            }
        }
        return nNikitaFormPublic;
    }

    public void setNikitaFormPublic(String key, Object data) {
        getNikitaFormPublic().setData(key, String.valueOf(data));
        setVirtualRegistered("@+SESSION-NikitaFormPublic", getNikitaFormPublic().toJSON());
    }

    public void clearNikitaFormPublic() {
        nNikitaFormPublic = Nset.newObject();
        setVirtualRegistered("@+SESSION-NikitaFormPublic", "{}");
    }

    public void mergerVirtual(NikitaResponse target) {
        Nset n = new Nset(virtual);
        String[] keys = n.getObjectKeys();
        for (int i = 0; i < keys.length; i++) {
            target.setVirtual(keys[i], n.getData(keys[i]).getInternalObject());
        }
    }

    public NikitaResponse newInstance() {
        NikitaResponse n = new NikitaResponse(req, resp);

        n.setNikitaLogic(nlogic);
        return n;
    }

    //17/01/2017    
    private Nset getmobilegenerator = null;

    public void openFormStream(String key, Object data) {
        String v = String.valueOf(data);
        setVirtualRegistered("@+SESSION-WEB-FORMSTREAM-DATA", v);
        if (!v.equals("")) {
            getMobileGeneratorReload();
        }
    }

    public Nset getMobileGeneratorA() {
        if (req instanceof NikitaFilter.NikitaGeneratorPostData) {
            return ((NikitaFilter.NikitaGeneratorPostData) req).getMobileGenerator();
        }
        if (getmobilegenerator != null) {
        } else if (req != null && req.getParameter("mobilegenerator") != null) {
            getmobilegenerator = Nset.readJSON(req.getParameter("mobilegenerator"));
            getMobileGeneratorReload();
        }
        return getmobilegenerator;
    }

    public Nset getMobileGenerator() {
        return getMobileGeneratorA();
    }

    public Nset getMobileGeneratorReload() {
        if (req instanceof NikitaFilter.NikitaGeneratorPostData) {
            return ((NikitaFilter.NikitaGeneratorPostData) req).getMobileGeneratorReload();
        }
        getMobileGeneratorA();
        String vstream = String.valueOf(req.getSession().getAttribute(NikitaResponse.getCookieOrSessionKey("@+SESSION-WEB-FORMSTREAM-DATA")));
        if (getContent() != null && getContent().isActionDataState() && !vstream.trim().equalsIgnoreCase("")) {
            Nset n = Nset.readJSON(vstream);
            if (getmobilegenerator != null && getmobilegenerator.isNsetObject() && n.isNset()) {
                if (getmobilegenerator != null && getmobilegenerator.isNsetObject() && n.isNset()) {
                    Hashtable vvv = (Hashtable) getmobilegenerator.getInternalObject();
                    String[] keys = n.getObjectKeys();
                    for (int i = 0; i < keys.length; i++) {
                        vvv.put(keys[i], n.getData(keys[i]).getInternalObject());
                    }
                }
            }
        }
        return getMobileGeneratorA();
    }

    public void clearMobileGenerator() {
        setVirtualRegistered("@+SESSION-WEB-FORMSTREAM-DATA", "");
        getMobileGeneratorReload();

        if (getContent() != null && getMobileGenerator() != null) {
            try {
                String[] keys = getMobileGenerator().getObjectKeys();
                for (int i = 0; i < keys.length; i++) {
                    if (keys[i].equals(getContent().getName())) {
                    } else {
                        getMobileGenerator().setData(keys[i], Nset.newObject());
                    }
                }
            } catch (Exception e) {
            }
        }

    }

    //nikita willy lagii benar benar galaoo 2014/12/17
    public NikitaConnection getConnection() {
        return resp.getConnection();
    }

    public NikitaConnection getConnection(String s) {
        System.out.println("NikitaResponse.getConnection : " + String.valueOf(NikitaService.sThreadLocal.get().NIKITAGENERATOR_LOGIC));
        return resp.getConnection(s);
    }

    public void closeAllConnection() {
        resp.closeAllConnection();
    }
    //----------------------------------------------

    public void setNikitaLogic(NikitaLogic nlogic) {
        this.nlogic = nlogic;
    }

    public NikitaLogic getNikitaLogic() {
        return this.nlogic;
    }

    private NikitaEngine nikitaEngine;

    public NikitaEngine getNikitaEngine() {
        return nikitaEngine;
    }

    public void setNikitaEngine(NikitaEngine nikitaEngine) {
        this.nikitaEngine = nikitaEngine;
    }

    protected boolean isConsumed = false;

    private String fname = "";

    public void setServletFormName(String fname) {
        this.fname = fname;
    }

    public String getServletFormName() {
        return this.fname;
    }

    public NikitaResponse(HttpServletRequest request, NikitaServletResponse response) {
        try {
            cookies = request.getCookies();
        } catch (Exception e) {
        }
        resp = response;
        req = request;
    }

    public HttpServletResponse getHttpServletResponse() {
        return resp;
    }

    public boolean isComponent(String key) {
        return key.startsWith("$");
    }

    public boolean isVirtual(String key) {
        return key.startsWith("@");
    }

    public boolean isVirtualComponnet(String key) {
        return key.startsWith("@$");
    }

    public Component getComponent(String key) {
        if (key.contains("[")) {
            key = key.substring(0, key.indexOf("["));
        }
        if (getContent() == null) {
            return new Component();
        } else if (key.startsWith("$#")) {
            Component comp = getContent().findComponentbyId(key.substring(2));
            return comp != null ? comp : new Component();
        } else if (key.startsWith("$")) {
            Component comp = getContent().findComponentbyName(key.substring(1));
            return comp != null ? comp : new Component();
        }
        return new Component();
    }

    public String getVirtualString(String key) {
        Object obj = getVirtual(key);
        return obj != null ? obj.toString() : "";
    }

    public Object nsonCodeScript(String key) {
        if (key.startsWith("@+NSON-CODE") || key.startsWith("@+NSON-SCRIPT")) {
            //cari ( sampai }
            String arr = "";
            String exp = "";
            int i = key.indexOf("]");

            if (i >= 10) {
                arr = key.substring(key.startsWith("@+NSON-CODE") ? 11 : 13, i);
                exp = key.substring(i + 1);
            }
            arr = arr.replaceAll("[\\\"\\[\\]\\s]", "");
            if (arr.startsWith("@") || arr.trim().startsWith("$")) {
                exp = getVirtualString(arr);
            }
            if (exp.trim().length() >= 1) {
                HashMap var = new HashMap() {
                    public Object put(Object key, Object value) {
                        String s = String.valueOf(key);
                        if (s.startsWith("$")) {
                            getComponent(s).setText(String.valueOf(value));
                        } else {
                            setVirtual(String.valueOf(key), value);
                        }
                        return true;
                    }

                    public Object get(Object key) {
                        return getVirtual(String.valueOf(key));
                    }
                };
                if (key.startsWith("@+NSON-CODE")) {

                    return new NikitaExpression().expression(exp, var);
                } else {
                    new NikitaExpression().evaluate(exp, var);//evaluate scrip
                    return key;
                }

            }
        }
        return null;
    }

    public Object nsonExpression(String exp) {
        HashMap var = new HashMap() {
            public Object put(Object key, Object value) {
                String s = String.valueOf(key);
                if (s.startsWith("$")) {
                    getComponent(s).setText(String.valueOf(value));
                } else {
                    setVirtual(String.valueOf(key), value);
                }
                return true;
            }

            public Object get(Object key) {
                return getVirtual(String.valueOf(key));
            }
        };
        return new NikitaExpression().expression(exp, var);
    }

    public Object nsonEvaluateScript(String exp) {
        HashMap var = new HashMap() {
            public Object put(Object key, Object value) {
                String s = String.valueOf(key);
                if (s.startsWith("$")) {
                    getComponent(s).setText(String.valueOf(value));
                } else {
                    setVirtual(String.valueOf(key), value);
                }
                return true;
            }

            public Object get(Object key) {
                return getVirtual(String.valueOf(key));
            }
        };
        new NikitaExpression().evaluate(exp, var);
        return "";
    }

    public Object getVirtual(String key) {
        String name = key;
        if ((key.startsWith("@[") && key.endsWith("]")) || (key.startsWith("@{") && key.endsWith("}"))) {
            return Nset.readJSON(key.substring(1));
        } else if (key.startsWith("@#")) {
            return key.substring(2);
        }

        String f = "";
        if ((key.startsWith("!") && key.contains("(")) || (key.startsWith("@") && key.contains("(")) || (key.startsWith("$") && key.contains("("))) {
            f = key.substring(key.lastIndexOf("(") + 1);//yang terakhir
            key = key.substring(0, key.lastIndexOf("("));
            if (f.contains(")")) {
                f = f.substring(0, f.indexOf(")"));
            }
        }
        f = f.trim();

        //add 20/04/2021
        if (f.startsWith("!")) {
            f = f.substring(1);
        }

        //NSON EXPRESSION  @+NSON-EXP (depreketert)
        Object nsonObject = nsonCodeScript(key);
        if (nsonObject != null) {
            return nsonObject;
        }
        //====================================

        if (key.startsWith("@?")) {
            if (name.contains("(")) {
                name = name.substring(0, name.lastIndexOf("("));
            }
            key = "@#" + name.substring(2);
        }

        Object reObject = getVirtualStream(key);
        if (f.startsWith("$")) {//"$"
            //new function
            Component component = getComponent(f);
            if (component instanceof Function) {
                //component.setOn               
                Function function = ((Function) component);
                if (reObject instanceof Nikitaset) {
                    function.setText("");
                } else if (reObject instanceof Nset) {
                    function.setText("");
                } else if (reObject instanceof String) {
                    function.setText((String) reObject);
                } else if (reObject instanceof Long) {
                    function.setText("");
                } else if (reObject instanceof Integer) {
                    function.setText("");
                } else if (reObject instanceof Double) {
                    function.setText("");
                } else if (reObject instanceof Boolean) {
                    function.setText("");
                } else {
                    function.setText("");
                }
                function.runLogic("function");
                return function.getText();
            }

        } else if (f.startsWith("numberformat")) {
            String patern = f.substring(12);
            try {
                System.out.println("numberformat:" + patern);
                if (reObject instanceof String) {
                    return new DecimalFormat(patern, DecimalFormatSymbols.getInstance(Locale.US)).format(Utility.getLong((String) reObject));
                } else if (reObject instanceof Integer) {
                    return new DecimalFormat(patern, DecimalFormatSymbols.getInstance(Locale.US)).format((Integer) reObject);
                } else if (reObject instanceof Long) {
                    return new DecimalFormat(patern, DecimalFormatSymbols.getInstance(Locale.US)).format((Long) reObject);
                } else if (reObject instanceof Double) {
                    return new DecimalFormat(patern, DecimalFormatSymbols.getInstance(Locale.US)).format((Double) reObject);
                } else if (reObject == null) {
                    return "";
                }
            } catch (Exception e) {
            }
            return "";
        } else if (f.startsWith("dateformat")) {
            String patern = f.substring(10);

            return "";
        } else if (f.equals("abs")) {
            return Math.abs(Utility.getNumber(reObject).doubleValue());
        } else if (f.equals("floor")) {
            return Math.floor(Utility.getNumber(reObject).doubleValue());
        } else if (f.equals("round")) {
            return Math.round(Utility.getNumber(reObject).doubleValue());
        } else if (f.equals("ceil")) {
            return Math.ceil(Utility.getNumber(reObject).doubleValue());
        } else if (f.equals("cbrt")) {
            return Math.cbrt(Utility.getNumber(reObject).doubleValue());
        } else if (f.equals("ulp")) {
            return Math.ulp(Utility.getNumber(reObject).doubleValue());

        } else if (f.equals("integer") || f.equals("int") || f.equals("inc") || f.equals("dec")) {
            int inc = f.equals("inc") ? 1 : 0;
            inc = f.equals("dec") ? -1 : inc;
            return Utility.getNumber(reObject).intValue() + inc;
        } else if (f.equals("curr") || f.equals("fcurrview")) {
            return Utility.formatCurrency(String.valueOf(reObject));
        } else if (f.equals("numonly")) {
            return Utility.getNumberOnly(String.valueOf(reObject));
        } else if (f.equals("numpointonly")) {
            return Utility.getNumberPointOnly(String.valueOf(reObject));
        } else if (f.equals("num") || f.equals("fnumview")) {
            if (f.equals("fnumview")) {
                return Utility.formatNumber(String.valueOf(reObject));
            } else if (String.valueOf(reObject).contains(".")) {
                return Utility.getNumber(Utility.formatNumber(String.valueOf(reObject))).doubleValue();
            }
            return Utility.getNumber(Utility.formatNumber(String.valueOf(reObject))).longValue();
        } else if (f.equals("long")) {
            return Utility.getNumber(reObject).longValue();
        } else if (f.equals("double") || f.equals("single") || f.equals("float")) {
            return Utility.getNumber(reObject).doubleValue();
        } else if (f.equals("decimal") || f.equals("fdecimal") || f.startsWith("fdecimal")) {
            String result = BigDecimal.valueOf(Utility.getNumber(reObject).doubleValue()).toPlainString();
            if (f.equals("fdecimal")) {
            } else if (f.equals("decimal")) {
                if (result.contains(".")) {
                    return Utility.getNumber(result.substring(result.indexOf(".") + 1)).longValue();
                } else {
                    return 0;
                }
            } else {
                int sig = Utility.getInt(f.substring(8));
                String num = result;
                String dec = "";
                if (result.contains(".")) {
                    num = result.substring(0, result.indexOf("."));
                    dec = result.substring(result.indexOf(".") + 1);
                }
                StringBuilder stringBuilder = new StringBuilder(num);
                stringBuilder.append(".");
                for (int i = 0; i < sig; i++) {
                    if (dec.length() > i) {
                        stringBuilder.append(dec.substring(i, i + 1));
                    } else {
                        stringBuilder.append("0");
                    }
                }
                result = stringBuilder.toString();
            }
            return result;
        } else if (f.equals("phone") || f.equals("phone62") || f.equals("phone0")) {
            String str = Utility.getNumberOnly(getVirtualString(key));
            if (f.equals("phone62")) {
                if (str.startsWith("0")) {
                    return "62" + str.substring(1);
                } else if (str.startsWith("62")) {
                    return str;
                } else {
                    return "62" + str;
                }
            } else if (f.equals("phone0")) {
                if (str.startsWith("0")) {
                    return str;
                } else if (str.startsWith("62")) {
                    return "0" + str.substring(2);
                } else {
                    return "0" + str;
                }
            } else {
                return str;
            }
        } else if (f.equals("dottemp") ) {
            return getVirtualString(key)+".tmp";    
        } else if (f.equals("dotpng") ) {
            return getVirtualString(key)+".png";  
        } else if (f.equals("dotxls") ) {
            return getVirtualString(key)+".xls";    
        } else if (f.equals("dotcom") ) {
            return getVirtualString(key)+".com";
        } else if (f.equals("dotpdf") ) {
            return getVirtualString(key)+".pdf";   
        } else if (f.equals("like") ) {
            return "%"+getVirtualString(key)+"%";    
        } else if (f.equals("slike") ) {
            return "%"+ getVirtualString(key)  ;   
        } else if (f.equals("elike") ) {         
            return  getVirtualString(key)+"%";   
        } else if (f.equals("email") || f.equals("psw")) {
            String str = (getVirtualString(key));
            if (f.startsWith("email")) {
                return Utility.isAnEmail(str);
            } else {
                return Utility.isValidPassword(str);
            }
        } else if (f.equals("Aa0") || f.equals("Aa") || f.equals("0")) {
            if (f.startsWith("Aa0")) {
                return Utility.getStringAccept(getVirtualString(key), "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm01234567890");
            } else if (f.startsWith("Aa")) {
                return Utility.getStringAccept(getVirtualString(key), "QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm");
            } else {
                return Utility.getNumberOnly(getVirtualString(key));
            }
        } else if (f.equals("name")) {
            return key;
        } else if (f.equals("exec")) {
            String key2 = getVirtualString(key);
            if (key2.startsWith("@") || key2.startsWith("$")) {
                return getVirtual(key2);
            }
            return "";
        } else if (f.equals("boolean")) {
            if (reObject instanceof Boolean) {
                return (Boolean) reObject;
            } else {
                return Boolean.valueOf(String.valueOf(reObject));
            }
        } else if (f.equals("nsonscript")) {
            return nsonEvaluateScript(String.valueOf(reObject));
        } else if (f.equals("expression")) {
            return nsonExpression(String.valueOf(reObject));
        } else if (f.equals("nson")) {
            if (reObject instanceof Nikitaset) {
                return new Nson((Map) ((Nikitaset) reObject).toNset());
            } else if (reObject instanceof Nset) {
                if (((Nset) reObject).getInternalObject() instanceof Map) {
                    return new Nson((Map) ((Nset) reObject).getInternalObject());
                } else if (((Nset) reObject).getInternalObject() instanceof List) {
                    return new Nson((List) ((Nset) reObject).getInternalObject());
                }
                return Nson.newObject();
            } else {
                return Nson.readNson(String.valueOf(reObject), new Nson.OnVariableListener() {
                    public Object get(String name, boolean singlequote, boolean doublequote) {
                        if (singlequote) {
                            return NikitaResponse.this.getVirtualString(name);
                        } else if (!singlequote && !doublequote) {
                            return NikitaResponse.this.getVirtualString(name);
                        } else {
                            return name;
                        }
                    }
                });
            }
        } else if (f.equals("json")) {
            if (reObject instanceof String) {
                return Nset.newArray().addData((String) reObject).toJSON();
            } else if (reObject instanceof Nikitaset) {
                return ((Nikitaset) reObject).toNset().toJSON();
            } else if (reObject instanceof Nset) {
                return ((Nset) reObject).toJSON();
            } else if (reObject instanceof Nson) {
                return ((Nson) reObject).toJson();
            }
        } else if (f.equals("arrayview") || f.equals("arraydb") || f.equals("arrayin")) {
            Nset v = null;
            if (reObject instanceof Nikitaset) {
                v = ((Nikitaset) reObject).toNset();
            } else if (reObject instanceof Nset) {
                v = ((Nset) reObject);
            } else if (String.valueOf(reObject).trim().startsWith("[")) {
                v = Nset.readJSON(String.valueOf(reObject));
            }
            if (f.equals("arrayview")) {
                if (v != null) {
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < v.getArraySize(); i++) {
                        builder.append(i >= 1 ? "," : "").append(v.getData(i).toString());
                    }
                    return builder.toString();
                } else {
                    return "";
                }
            } else {
                if (v != null) {
                    StringBuilder builder = new StringBuilder("(");
                    for (int i = 0; i < v.getArraySize(); i++) {
                        builder.append(i >= 1 ? "," : "").append("'").append(Component.escapeSql(v.getData(i).toString())).append("'");
                    }
                    builder.append(")");
                    if (f.equals("arrayin") && v.getArraySize() == 0) {
                        return "('')";
                    }
                    return builder.toString();
                } else if (f.equals("arrayin")) {
                    return "('')";
                } else {
                    return "()";
                }
            }
        } else if (f.equals("arraydistinct")) {
            Nset n;
            if (reObject instanceof Nset) {
                n = (Nset) reObject;
            } else {
                n = Nset.readJSON(String.valueOf(reObject));
            }
            final Nson b2 = Nson.newObject();
            for (int j = 0; j < n.getArraySize(); ++j) {
                b2.setData(n.getData(j).toString(), "");
            }
            final Nset o = Nset.newArray();
            final Nson k = b2.getObjectKeys();
            for (int l = 0; l < k.size(); ++l) {
                o.addData(k.getData(l).toString());
            }
            return o;

        } else if (f.equals("arrayrandom")) {
            Nset n;
            if (reObject instanceof Nset) {
                n = (Nset) reObject;
            } else {
                n = Nset.readJSON(String.valueOf(reObject));
            }
            if (n.isNsetArray()) {
                final Nset o2 = Nset.newArray();
                final List v2 = (List) n.getInternalObject();
                final Random rand = new Random();
                rand.setSeed(System.currentTimeMillis());
                for (int numberOfElements = v2.size(), i = 0; i < numberOfElements; ++i) {
                    final int randomIndex = rand.nextInt(v2.size());
                    final Object randomElement = v2.get(randomIndex);
                    v2.remove(randomIndex);
                    o2.addData(String.valueOf(randomElement));
                }
                return o2;
            }
            return n;

        } else if (f.equals("arrayjson")) {
            if (reObject instanceof Nikitaset) {
                return ((Nikitaset) reObject).toNset().toJSON();
            } else if (reObject instanceof Nset) {
                return ((Nset) reObject).toJSON();
            } else if (String.valueOf(reObject).contains(",")) {
                return new Nset(Utility.splitVector(String.valueOf(reObject), ",")).toJSON();
            } else {
                return Nset.newArray().addData(String.valueOf(reObject)).toJSON();
            }
        } else if (f.equals("arrayselected")) {
            if (reObject instanceof Nikitaset) {
                Nset n = ((Nikitaset) reObject).toNset().getData("data");
                Nset b = Nset.newArray();
                for (int i = 0; i < n.getArraySize(); i++) {
                    b.addData(n.getData(i).getData(0).toString());
                }
                return b.toJSON();
            } else if (reObject instanceof Nset) {
                Nset n = ((Nset) reObject);
                Nset b = Nset.newArray();
                for (int i = 0; i < n.getArraySize(); i++) {
                    b.addData(n.getData(i).getData(0).toString());
                }
                return b.toJSON();
            }
            return "[]";
        } else if (f.equals("locationmap")) {
            if (reObject instanceof Nikitaset) {
                Nikitaset ns = ((Nikitaset) reObject) ;
                Nson nson = Nson.newArray();
                for (int i = 0; i < ns.getRows(); i++) {
                    Nson n = Nson.readJson(ns.getText(i, "options"));
                    if (!n.isNsonObject()) {
                        n = Nson.newObject();
                    }
                    String[] s = Utility.split(ns.getText(i, "location") +",", ",");
                    n.setData("lat", s[0]);
                    n.setData("lng", s[1]);   
                    if ( s[0].equalsIgnoreCase("")&&  s[1].equalsIgnoreCase("")) {
                    }else{
                        nson.addData(n);
                    }                   
                }
                return nson.toJson();
            }            
        } else if (f.equals("csv") || f.equals("comma")) {
            if (reObject instanceof String) {
                return Nset.readJSON((String) reObject).toCsv();
            } else if (reObject instanceof Nikitaset) {
                return ((Nikitaset) reObject).toNset().toCsv();
            } else if (reObject instanceof Nset) {
                return ((Nset) reObject).toCsv();
            }
        } else if (f.equals("quote") || f.equals("string") || f.equals("lcase") || f.equals("ucase")) {
            String buffer = "";
            if (reObject instanceof Nikitaset) {
                buffer = ((Nikitaset) reObject).toNset().toString();
            } else if (reObject instanceof Nset) {
                buffer = ((Nset) reObject).toString();
            } else {
                buffer = String.valueOf(reObject);
            }
            if (f.equals("lcase")) {
                return buffer.toLowerCase();
            } else if (f.equals("ucase")) {
                return buffer.toUpperCase();
            } else if (f.equals("quote")) {
                return "\"" + buffer + "\"";
            }
            return buffer;
        } else if (f.startsWith("escape") || f.startsWith("unescape") || f.startsWith("encode") || f.startsWith("decode")) {
            String buffer = "";
            if (reObject instanceof String) {
                buffer = StringEscapeUtils.escapeSql((String) reObject);
            } else if (reObject instanceof Nikitaset) {
                buffer = StringEscapeUtils.escapeSql(((Nikitaset) reObject).toNset().toString());
            } else if (reObject instanceof Nset) {
                buffer = StringEscapeUtils.escapeSql(((Nset) reObject).toString());
            }
            if (f.equals("escapesql")) {
                return StringEscapeUtils.escapeSql(buffer);
            } else if (f.equals("escapehtml")) {
                return StringEscapeUtils.escapeHtml(buffer);
            } else if (f.equals("escapejs")) {
                return StringEscapeUtils.escapeJavaScript(buffer);
            } else if (f.equals("escapejava")) {
                return StringEscapeUtils.escapeCsv(buffer);
            } else if (f.equals("escapecsv")) {
                return StringEscapeUtils.escapeJava(buffer);
            } else if (f.equals("escapejson")) {
                String s = buffer;
                s = String.valueOf(Nset.newArray().addData(s)).trim();
                s = s.substring(1, s.length() - 1).trim();
                if (s.startsWith("\"") && s.endsWith("\"")) {
                    s = s.substring(1, s.length() - 1);
                }
                return s;
                //==============================================//
            } else if (f.equals("unescapehtml")) {
                return StringEscapeUtils.unescapeHtml(buffer);
            } else if (f.equals("unescapejs")) {
                return StringEscapeUtils.unescapeJava(buffer);
            } else if (f.equals("unescapejava")) {
                return StringEscapeUtils.unescapeJavaScript(buffer);
            } else if (f.equals("unescapecsv")) {
                return StringEscapeUtils.unescapeCsv(buffer);
            } else if (f.equals("unescapejson")) {
                return Nset.readJSON("[" + buffer + "]").getData(0).toString();
                //==============================================//
            } else if (f.equals("encodeurl")) {
                return URLEncoder.encode(buffer);
            } else if (f.equals("decodeurl")) {
                return URLDecoder.decode(buffer);
            } else if (f.equals("encodebase64")) {
                return Base64Coder.encodeString(buffer);
            } else if (f.equals("decodebase64")) {
                return Base64Coder.decodeString(buffer);
            }
        } else if (f.startsWith("expression")) {
            if (getVirtualString("@" + f).equalsIgnoreCase("true")) {
                return reObject;
            } else {
                return "";
            }
        } else if (f.equals("trim")) {
            if (reObject instanceof String) {
                return ((String) reObject).trim();
            } else if (reObject instanceof Nikitaset) {
                return ((Nikitaset) reObject).toNset().toString().trim();
            } else if (reObject instanceof Nset) {
                return ((Nset) reObject).toString().trim();
            } else {
                return String.valueOf(reObject).trim();
            }
        } else if (f.equals("md5")) {
            if (reObject instanceof String) {
                return Utility.MD5((String) reObject);
            } else if (reObject instanceof Nikitaset) {
                return Utility.MD5(((Nikitaset) reObject).toNset().toString());
            } else if (reObject instanceof Nset) {
                return Utility.MD5(((Nset) reObject).toString());
            } else {
                Utility.MD5(String.valueOf(reObject));
            }
        } else if (f.equals("sha1")) {
            if (reObject instanceof String) {
                return Utility.SHA1((String) reObject);
            } else if (reObject instanceof Nikitaset) {
                return Utility.SHA1(((Nikitaset) reObject).toNset().toString());
            } else if (reObject instanceof Nset) {
                return Utility.SHA1(((Nset) reObject).toString());
            }
        } else if (f.equals("filltonset")) {
            if (reObject instanceof String) {
                return fillStreamNset(Nset.readJSON((String) reObject));
            } else if (reObject instanceof Nikitaset) {
                return fillStreamNset(((Nikitaset) reObject).toNset());
            } else if (reObject instanceof Nset) {
                return fillStreamNset((Nset) reObject);
            }
        } else if (f.equals("nset")) {
            if (reObject instanceof String) {
                return Nset.readJSON((String) reObject);
            } else if (reObject instanceof Nikitaset) {
                return ((Nikitaset) reObject).toNset();
            } else if (reObject instanceof Nset) {
                return ((Nset) reObject);
            } else if (reObject instanceof Nson) {
                return new Nset(((Nson) reObject).getInternalObject());
            } else {
                return Nset.readJSON(String.valueOf(reObject));
            }
        } else if (f.equals("currentns")) {
            if (reObject instanceof Nikitaset) {
                Nikitaset dt = (Nikitaset) reObject;
                int i = Utility.getNumber(getVirtualString("@+LOGICCOUNT")).intValue() ;            
                Vector<Vector<String>> curr = new Vector<>();
                if (dt.getRows() > i) {
                    Vector<Vector<String>> v = dt.getDataAllVector();
                    curr.add(v.get(i));  
                }
                return new Nikitaset(dt.getDataAllHeader(), curr );  
            }else{
                return new Nikitaset("Nikitaset not found");  
            }
                  
        } else if (f.equals("datatonikitaset")) {
            if (reObject instanceof Nset) {
                Nset nset = ((Nset) reObject);
                Vector<String> hdr = new Vector<String>();
                Vector<Vector<String>> dat = new Vector<Vector<String>>();
                String[] keys = nset.getData(0).getObjectKeys();
                for (int i = 0; i < keys.length; i++) {
                    hdr.addElement(keys[i]);
                }
                for (int i = 0; i < nset.getSize(); i++) {
                    Vector<String> row = new Vector<String>();
                    for (int j = 0; j < keys.length; j++) {
                        row.addElement(nset.getData(i).getData(keys[j]).toString());
                    }
                    dat.addElement(row);
                }
                return new Nikitaset(hdr, dat);
            }
        } else if (f.equals("nikitasettodata")) {
            if (reObject instanceof Nikitaset) {
                Nikitaset dt = (Nikitaset) reObject;
                Nset nset = Nset.newArray();
                for (int i = 0; i < dt.getRows(); i++) {
                    Nset row = Nset.newObject();
                    for (int c = 0; c < dt.getCols(); c++) {
                        row.setData(dt.getHeader(c), dt.getText(i, c));
                    }
                    nset.addData(row);
                }
                return nset;
            }
        } else if (f.equals("nikitasettodatalocase")) {
            if (reObject instanceof Nikitaset) {
                Nikitaset dt = (Nikitaset) reObject;
                Nset nset = Nset.newArray();
                for (int i = 0; i < dt.getRows(); i++) {
                    Nset row = Nset.newObject();
                    for (int c = 0; c < dt.getCols(); c++) {
                        row.setData(dt.getHeader(c).toLowerCase(), dt.getText(i, c));
                    }
                    nset.addData(row);
                }
                return nset;
            }
        } else if (f.equals("nikitasettodataupcase")) {
            if (reObject instanceof Nikitaset) {
                Nikitaset dt = (Nikitaset) reObject;
                Nset nset = Nset.newArray();
                for (int i = 0; i < dt.getRows(); i++) {
                    Nset row = Nset.newObject();
                    for (int c = 0; c < dt.getCols(); c++) {
                        row.setData(dt.getHeader(c).toUpperCase(), dt.getText(i, c));
                    }
                    nset.addData(row);
                }
                return nset;
            }
        } else if (f.equals("coltorow") || f.equals("rowtocol") || f.equals("transpose")) {
            if (reObject instanceof Nikitaset) {
                Nikitaset dt = (Nikitaset) reObject;
                Vector<String> header = new Vector<String>();
                Vector<Vector<String>> data = new Vector<Vector<String>>();
                int cols = dt.getDataAllHeader().size();
                for (int col = 0; col < cols; col++) {
                    Vector<String> currdata = new Vector<String>();
                    if (f.equalsIgnoreCase("transpose")) {
                        currdata.addElement(dt.getDataAllHeader().elementAt(col));
                    }
                    for (int row = 0; row < dt.getRows(); row++) {
                        currdata.addElement(dt.getText(row, col));
                    }
                    if (col == 0) {
                        header.addAll(currdata);
                    } else {
                        data.addElement(currdata);
                    }
                }
                return new Nikitaset(header, data);
            }

        } else if (f.equals("nikitaset")) {
            if (reObject instanceof String) {
                Nset n = Nset.readJSON((String) reObject);
                if (Nikitaset.isNikitaset(n)) {
                    return new Nikitaset(n);
                } else {
                    return n;
                }
            } else if (reObject instanceof Nikitaset) {
                return ((Nikitaset) reObject).toNset();
            } else if (reObject instanceof Nset) {
                Nset n = ((Nset) reObject);
                if (Nikitaset.isNikitaset(n)) {
                    return new Nikitaset(n);
                } else {
                    return n;
                }
            } else if (reObject instanceof Nson) {
                Nson n = ((Nson) reObject);
                if (Nikitaset.isNikitaset(n)) {
                    return new Nikitaset(new Nset(((Nson) reObject).getInternalObject()));
                } else {
                    return n;
                }

            } else {
                return new Nikitaset(Nset.readJSON(String.valueOf(reObject)));
            }
        } else if (f.equals("nhtml")) {
            return Utility.nhtmlNikitaParse(String.valueOf(reObject), new Utility.NikitaParse() {
                public Object getVirtual(String name) {
                    return NikitaResponse.this.getVirtual(name);
                }
            });
        } else if (f.equals("njson")) {
            return Utility.nhtmlNikitaParse(String.valueOf(reObject), new Utility.NikitaParse() {
                public Object getVirtual(String name) {
                    String s = "null";
                    Object buff = NikitaResponse.this.getVirtual(name);
                    if (buff == null) {
                    } else if (buff instanceof Boolean) {
                        s = String.valueOf(buff);
                    } else if (buff instanceof Number) {
                        s = String.valueOf(buff);
                    } else if (buff instanceof Nset) {
                        s = ((Nset) buff).toJSON();
                    } else if (buff instanceof Nson) {
                        s = ((Nson) buff).toJson();
                    } else if (buff instanceof Nikitaset) {
                        s = ((Nikitaset) buff).toNset().toJSON();
                    } else if (buff instanceof Vector || buff instanceof Hashtable) {
                        s = new Nset(buff).toJSON();
                    } else {
                        if (name.trim().endsWith("(ustring)")) {
                            s = String.valueOf(buff);
                            s = String.valueOf(Nset.newArray().addData(s)).trim();
                            s = s.substring(1, s.length() - 1);
                            if (s.startsWith("\"") && s.endsWith("\"")) {
                                s = s.substring(1, s.length() - 1);
                            }
                        } else if (name.trim().endsWith("(nstring)")) {
                            s = String.valueOf(buff);

                        } else { //include qstring
                            s = String.valueOf(buff);
                            s = String.valueOf(Nset.newArray().addData(s)).trim();
                            s = s.substring(1, s.length() - 1);
                        }
                    }
                    return s;
                }
            });
        } else if (f.equals("nujson")) {
            return Utility.nhtmlNikitaParse(String.valueOf(reObject), new Utility.NikitaParse() {
                public Object getVirtual(String name) {
                    String s = "null";
                    Object buff = NikitaResponse.this.getVirtual(name);
                    if (buff == null) {
                    } else if (buff instanceof Boolean) {
                        s = String.valueOf(buff);
                    } else if (buff instanceof Number) {
                        s = String.valueOf(buff);
                    } else if (buff instanceof Nset) {
                        s = ((Nset) buff).toJSON();
                    } else if (buff instanceof Nikitaset) {
                        s = ((Nikitaset) buff).toNset().toJSON();
                    } else if (buff instanceof Vector || buff instanceof Hashtable) {
                        s = new Nset(buff).toJSON();
                    } else {
                        if (name.trim().endsWith("(qstring)")) {
                            s = String.valueOf(buff);
                            s = String.valueOf(Nset.newArray().addData(s)).trim();
                            s = s.substring(1, s.length() - 1);
                        } else if (name.trim().endsWith("(nstring)")) {
                            s = String.valueOf(buff);
                        } else {//include ustring
                            s = String.valueOf(buff);
                            s = String.valueOf(Nset.newArray().addData(s)).trim();
                            s = s.substring(1, s.length() - 1);
                            if (s.startsWith("\"") && s.endsWith("\"")) {
                                s = s.substring(1, s.length() - 1);
                            }
                        }

                    }
                    return s;
                }
            });
        } else if (f.equals("type") || f.equals("ntype")) {
            if (reObject instanceof String) {
                return "string";
            } else if (reObject instanceof Nikitaset) {
                return "nikitaset";
            } else if (reObject instanceof Nset) {
                return "nset";
            } else if (reObject instanceof Nson) {
                if (f.equals("ntype")) {
                    return "nson-" + ((Nson) reObject).asType();
                } else {
                    return "nson";
                }
            } else if (reObject instanceof Integer) {
                return "integer";
            } else if (reObject instanceof Long) {
                return "long";
            } else if (reObject instanceof Double) {
                return "double";
            } else {
                return "";
            }
        } else if (f.equals("newarray")) {
            Nset n = Nset.newArray();
            setVirtual(key, n);
            return n;
        } else if (f.equals("newobject")) {
            Nset n = Nset.newObject();
            setVirtual(key, n);
            return n;
        } else if (f.equals("newstring")) {
            setVirtual(key, "");
            return "";
        } else if (f.equals("newint") || f.equals("newlong") || f.equals("newfloat") || f.equals("newdecimal") || f.equals("newdouble")) {
            setVirtual(key, 0);
            return 0;
        } else if (f.equals("fdate") || f.equals("fdatenumber") || f.equals("fdateint") || f.equals("fdatelong")) {
            if (reObject instanceof String) {
                return Utility.getDateTime((String) reObject);
            } else if (reObject instanceof Long) {
                return (Long) reObject;
            }
        } else if (f.equals("fdateview")) {
            if (reObject instanceof String) {
                long l = Utility.getDate((String) reObject);
                if (l != 0) {
                    return Utility.formatDate(l, "dd/MM/yyyy");
                }
                //return DateFormatAction.FormatDate(-1, (String)reObject, "");
            } else if (reObject instanceof Long) {
                long l = (Long) reObject;
                if (l != 0) {
                    return Utility.formatDate(l, "dd/MM/yyyy");
                }
            }
        } else if (f.equals("fdatetimeview")) {
            if (reObject instanceof String) {
                long l = Utility.getDateTime((String) reObject);
                if (l != 0) {
                    return Utility.formatDate(l, "dd/MM/yyyy HH:mm:ss");
                }
            } else if (reObject instanceof Long) {
                long l = (Long) reObject;
                if (l != 0) {
                    return Utility.formatDate(l, "dd/MM/yyyy HH:mm:ss");
                }
            }
            //return DateFormatAction.FormatDate(-1, reObject.toString(), Utility.NowTime());
        } else if (f.equals("fdatedb") || f.equals("todate")) {
            if (reObject instanceof String) {
                long l = Utility.getDate((String) reObject);
                if (l != 0) {
                    return Utility.formatDate(l, "yyyy-MM-dd");
                }
            } else if (reObject instanceof Long) {
                long l = (Long) reObject;
                if (l != 0) {
                    return Utility.formatDate(l, "yyyy-MM-dd");
                }
            }
            //return DateFormatAction.FormatDate(-1, reObject.toString(), Utility.NowTime());
        } else if (f.equals("fdatetimedb") || f.equals("todatetime")) {
            //yyyy-mm-dd hh:nn:ss
            if (reObject instanceof String) {
                long l = Utility.getDateTime((String) reObject);
                if (l != 0) {
                    return Utility.formatDate(l, "yyyy-MM-dd HH:mm:ss");
                }
            } else if (reObject instanceof Long) {
                long l = (Long) reObject;
                if (l != 0) {
                    return Utility.formatDate(l, "yyyy-MM-dd HH:mm:ss");
                }
            }
            //return DateFormatAction.FormatDate(-1, reObject.toString(), Utility.NowTime());
        } else if (f.equals("fdatetmaxdb") || f.equals("todatetmax")) {
            //yyyy-mm-dd hh:nn:ss
            if (reObject instanceof String) {
                long l = Utility.getDateTime((String) reObject);
                if (l != 0) {
                    return Utility.formatDate(l, "yyyy-MM-dd") + " 23:59:59";
                }
            } else if (reObject instanceof Long) {
                long l = (Long) reObject;
                if (l != 0) {
                    return Utility.formatDate(l, "yyyy-MM-dd") + " 23:59:59";
                }
            }
        } else if (f.equals("fdatetnowdb") || f.equals("todatetnow")) {
            //yyyy-mm-dd hh:nn:ss
            if (reObject instanceof String) {
                long l = Utility.getDateTime((String) reObject);
                if (l != 0) {
                    return Utility.formatDate(l, "yyyy-MM-dd") + Utility.Now().substring(10);
                }
            } else if (reObject instanceof Long) {
                long l = (Long) reObject;
                if (l != 0) {
                    return Utility.formatDate(l, "yyyy-MM-dd") + Utility.Now().substring(10);
                }
            }
        } else if (f.equals("fdatetmmindb") || f.equals("todatetmin")) {
            //yyyy-mm-dd hh:nn:ss
            if (reObject instanceof String) {
                long l = Utility.getDateTime((String) reObject);
                if (l != 0) {
                    return Utility.formatDate(l, "yyyy-MM-dd") + " 00:00:00";
                }
            } else if (reObject instanceof Long) {
                long l = (Long) reObject;
                if (l != 0) {
                    return Utility.formatDate(l, "yyyy-MM-dd") + " 00:00:00";
                }
            }
        } else if (f.equals("length") || f.equals("rows")) {
            if (reObject instanceof String) {
                return ((String) reObject).length();
            } else if (reObject instanceof Nikitaset) {
                return ((Nikitaset) reObject).getRows();
            } else if (reObject instanceof Nset) {
                return ((Nset) reObject).getArraySize() >= 0 ? ((Nset) reObject).getArraySize() : ((Nset) reObject).getObjectKeys().length;
            } else if (reObject instanceof Integer) {
                return ("" + ((Integer) reObject)).length();
            } else if (reObject instanceof Long) {
                return ("" + ((Long) reObject)).length();
            } else if (reObject instanceof Double) {
                return ("" + ((Double) reObject)).length();
            } else {
                return 0;
            }
        } else if (f.equals("error")) {
            if (reObject instanceof String) {
                return "";
            } else if (reObject instanceof Nikitaset) {
                return ((Nikitaset) reObject).getError();
            } else if (reObject instanceof Nset) {
                return "";
            } else {
                return "";
            }
        } else if (f.equals("header") || f.equals("headernset")) {
            if (reObject instanceof Nikitaset) {
                return new Nset(((Nikitaset) reObject).getDataAllHeader());
            } else {
                return Nset.newArray();
            }
        } else if (f.equals("data") || f.equals("datanset")) {
            if (reObject instanceof Nikitaset) {
                return new Nset(((Nikitaset) reObject).getDataAllVector());
            } else {
                return Nset.newArray();
            }
        }
        return reObject;
    }

    private Object getVirtualStream(String key) {
        String var = key;
        key = key.trim();

        if (key.startsWith("!")) {
            if (!key.contains("[")) {
                key = key + "[\"data\",0,\"" + key.substring(1).trim() + "\"]";
            } else if (key.contains("[") && key.contains("[")) {
                String index = key.substring(key.indexOf("[") + 1);
                key = key.substring(0, key.indexOf("["));

                if (index.contains("]")) {
                    index = index.substring(0, index.indexOf("]"));
                }
                key = key + "[\"data\"," + index + ",\"" + key.substring(1).trim() + "\"]";
            }
        }

        if (key.startsWith("@#")) {
            return key.substring(2);
        } else if (key.equals("@+FORMINDEX") || key.equals("@+ENGINE_FORMINDEX")) {
            try {
                return NikitaEngine.getFormIndex(getContent().getName());
            } catch (Exception e) {
            }
            return "";
        } else if (key.equals("@+ENGINE_FORMNAME")) {
            try {
                return getContent().getName();
            } catch (Exception e) {
            }
            return "";
        } else if (key.equals("@+ENGINE_ARRAY_INDEX")) {
            return NikitaEngine.NFID_ARRAY_INDEX;
        } else if (key.startsWith("@+MOBILEARGSDATA")) {
            return new Nset(AppNikita.getArgsData(this));
        } else if (key.equals("@+COREMYSQL")) {
            return NikitaConnection.CORE_MYSQL;
        } else if (key.equals("@+COREORACLE")) {
            return NikitaConnection.CORE_ORACLE;
        } else if (key.equals("@+CORESQLSERVER")) {
            return NikitaConnection.CORE_SQLSERVER;
        } else if (key.equals("@+CORESQLITE") || key.equals("@+CORESQLLITE")) {
            return NikitaConnection.CORE_SQLITE;
        } else if (key.startsWith("@+CORE-MOBILE-ACTIVITY")) {
            return Generator.getCoreVirtualString(this, key);
        } else if (key.startsWith("@+CORE-MOBILE-PUBLIC")) {
            key = key.substring("@+CORE-MOBILE-PUBLIC-".length()).trim();
            return Generator.getCoreVirtualPublicString(this, key);

        } else if (key.equals("@+CORE-MOBILEGENERATOR")) {
            return getMobileGenerator();
        } else if (key.startsWith("@+SESSION") || key.startsWith("@+SETTING-N-") || key.startsWith("@+CORE-")) {
            return req.getSession().getAttribute(getCookieOrSessionKey(key));
        } else if (key.equalsIgnoreCase("@nativecall") || key.equalsIgnoreCase("@nativemode")) {
            return req.getSession().getAttribute(getCookieOrSessionKey(key));
        } else if (key.equals("@+CORE-BASEURL") || key.equals("@+SETTING-BASEURL")) {

        } else if (key.startsWith("@+STATIC-")) {
            return AppNikita.getInstance().getVirtualString(key);
        } else if (key.startsWith("@+LOCAL-")) {
            return NikitaService.getBaseNikitaGeneratorData().getData(key.substring(8)).toString();
        } else if (key.startsWith("@+SETTING-")) {
            return NikitaConnection.getDefaultPropertySetting().getData("setting").getData(key.substring(10)).toString();
        } else if (key.startsWith("@+COOKIE")) {
            try {
                if (cookies != null) {
                    for (int i = 0; i < cookies.length; i++) {
                        if (cookies[i].getName().equals(getCookieOrSessionKey(key))) {
                            return cookies[i].getValue();
                        }
                    }
                }
            } catch (Exception e) {
            }
            return "";
        } else if (key.startsWith("@+UNIONCOL")) {
            //@+UNIONCOL ["nikitaset1","nikitaset2","nikitaset3"]

        } else if (key.startsWith("@+CHECKEDKEYS")) {
            key = key.substring(13);
            if (key.startsWith(".")) {
                key = key.substring(1);
            }
            if (key.startsWith("[")) {
                key = key.substring(1);
            }
            if (key.endsWith("]")) {
                key = key.substring(0, key.length() - 1);
            }
            if (key.startsWith("$#")) {
                Component cm = getContent().findComponentbyId(key.substring(2));
                if (cm instanceof Tablegrid) {
                    return ((Tablegrid) cm).getKeyChecked();
                }
            } else if (key.startsWith("$")) {
                Component cm = getContent().findComponentbyName(key.substring(1));
                if (cm instanceof Tablegrid) {
                    return ((Tablegrid) cm).getKeyChecked();
                }
            } else if (key.length() >= 2) {
                Component cm = getContent().findComponentbyId(key);
                if (cm instanceof Tablegrid) {
                    return ((Tablegrid) cm).getKeyChecked();
                }
            } else if (key.equals("")) {
                for (int i = 0; i < getContent().getComponentCount(); i++) {
                    if (getContent().getComponent(i) instanceof Tablegrid) {
                        return ((Tablegrid) getContent().getComponent(i)).getKeyChecked();
                    }
                }
            }
        } else if (key.startsWith("@+CHECKEDROWS")) {
            key = key.substring(13);
            if (key.startsWith(".")) {
                key = key.substring(1);
            }
            if (key.startsWith("[")) {
                key = key.substring(1);
            }
            if (key.endsWith("]")) {
                key = key.substring(0, key.length() - 1);
            }
            if (key.startsWith("$#")) {
                Component cm = getContent().findComponentbyId(key.substring(2));
                if (cm instanceof Tablegrid) {
                    return ((Tablegrid) cm).getRowChecked();
                }
            } else if (key.startsWith("$")) {
                Component cm = getContent().findComponentbyName(key.substring(1));
                if (cm instanceof Tablegrid) {
                    return ((Tablegrid) cm).getRowChecked();
                }
            } else if (key.length() >= 2) {
                Component cm = getContent().findComponentbyId(key);
                if (cm instanceof Tablegrid) {
                    return ((Tablegrid) cm).getRowChecked();
                }
            } else if (key.equals("")) {
                for (int i = 0; i < getContent().getComponentCount(); i++) {
                    if (getContent().getComponent(i) instanceof Tablegrid) {
                        return ((Tablegrid) getContent().getComponent(i)).getRowChecked();
                    }
                }
            }

        } else if (key.equals("@+BUTTON1")) {
            return "button1";
        } else if (key.equals("@+BUTTON2")) {
            return "button2";
        } else if (key.startsWith("@+BUTTONGRID") || key.startsWith("@+GRIDBUTTON")) {
            try {
                String sact = req.getParameter("action");
                if (sact.startsWith("item-")) {
                    sact = sact.substring(5);
                    if (sact.contains("-")) {
                        sact = sact.substring(sact.indexOf("-") + 1);
                    } else {
                        sact = "";
                    }
                } else if (sact.startsWith("actiongrid-")) {
                    sact = sact.substring(11);
                    if (sact.contains("-")) {
                        sact = sact.substring(sact.indexOf("-") + 1);
                    } else {
                        sact = "";
                    }
                } else {
                    sact = "";
                }
                return sact;
            } catch (Exception e) {
            }
            return "";
        } else if (key.equals("@+ENTER")) {
            return "\r\n";
        } else if (key.equals("@+SPACE")) {
            return " ";
        } else if (key.equals("@+SPACE32")) {
            return "                                ";
        } else if (key.equals("@+TAB")) {
            return "\t";
        } else if (key.equals("@+EMPTYSTRING")) {
            return "";
        } else if (key.equals("@+VERSION")) {
            return "WEB 1.0.13 Beta";//MOBILE DEKSTOP
        } else if (key.equals("@+FORMTITLE")) {
            return "";
        } else if (key.equals("@+FORMNAME")) {
            return "";
        } else if (key.equals("@+BROWSER")) {
            String s = req.getHeader("User-Agent");
            return s != null ? s.toUpperCase() : "";
        } else if (key.equals("@+DEVICEOS")) {
            String s = req.getHeader("User-Agent");
            s = s != null ? s.toUpperCase() : "";
            if (s.contains("X11;")) {
                return "UNIX";
            } else if (s.contains("WINDOWS")) {
                return "WINDOWS";
            } else if (s.contains("MAC OS")) {
                return "MAC";
            } else if (s.contains("LINUX")) {
                return "LINUX";
            }
            return "OTHER";
        } else if (key.equals("@+DEVICENAME")) {
            String mobile = "";
            String s = req.getHeader("User-Agent");
            s = s != null ? s.toUpperCase() : "";
            if (s.contains("MOBILE")) {
                mobile = "MOBILE-";
            }

            if (s.contains("CHROME")) {
                s = "CHROME";
            } else if (s.contains("FIREFOX")) {
                s = "FIREFOX";
            } else if (s.contains("OPERA")) {
                s = "OPERA";
            } else if (s.contains("J2ME")) {
                s = "J2ME";
            } else if (s.contains("MSIE") || s.contains("TRIDENT")) {
                s = "IE";
            } else {
                s = "UNKNOWN";
            }
            return "WEB-" + mobile + s;
        } else if (key.equals("@+DEVICEINFO")) {
            String s = req.getHeader("User-Agent");
            return s != null ? s : "";
        } else if (key.equals("@+NOW")) {
            return Utility.Now();
        } else if (key.equals("@+LIMIT")) {
            return NikitaConnection.LIMIT;
        } else if (key.equals("@+TIME")) {
            return System.currentTimeMillis();
        } else if (key.equals("@+RANDOM")) {
            StringBuffer sb = new StringBuffer();
            Random randomGenerator = new Random(System.currentTimeMillis());
            for (int idx = 1; idx <= 16; ++idx) {
                sb.append(randomGenerator.nextInt(100));
            }
            return sb.toString();
        } else if (key.equals("@+FILESEPARATOR")) {
            return NikitaService.getFileSeparator();
        } else if (key.equals("@+UNIQUE")) {
            return System.currentTimeMillis() + "-" + getVirtualString("@+CONNECTIONCOUNT");
        } else if (key.startsWith("$")) {
            if (getContent() != null) {
                Component comp;
                String ky = "";
                if (key.contains("[") && key.contains("]")) {
                    ky = key.substring(key.indexOf("["));
                    key = key.substring(0, key.indexOf("["));

                    ky = Utility.replace(ky, "[", "");
                    ky = Utility.replace(ky, "]", "");
                    ky = Utility.replace(ky, "\"", "");
                }

                ky = ky.trim();
                key = key.trim();

                if (key.startsWith("$#")) {
                    comp = getContent().findComponentbyId(key.substring(2));
                } else if (key.contains(".") && !key.endsWith(".")) {
                    comp = new Component();//Mobile Activity
                    Nset n = getMobileActivityStream();
                    String fname = key.substring(1, key.indexOf("."));
                    String cname = key.substring(key.indexOf(".") + 1);
                    comp.setText(n.getData(fname).getData(cname).getData(0).toString());
                    comp.setVisible(n.getData(fname).getData(cname).getData(1).toString().equals("1") ? true : false);
                    comp.setEnable(n.getData(fname).getData(cname).getData(2).toString().equals("1") ? true : false);

                } else {
                    comp = getContent().findComponentbyName(key.substring(1));
                }

                if (comp == null) {
                    comp = new Component();
                }

                if (ky.equals("tag")) {
                    return comp.getTag();
                } else if (ky.equals("visible")) {
                    return comp.isVisible() ? "true" : "false";
                } else if (ky.equals("enable")) {
                    return comp.isEnable() ? "true" : "false";
                } else if (ky.equals("id")) {
                    return comp.getId();
                } else if (ky.equals("comment")) {
                    return comp.getComment();
                } else if (ky.equals("name")) {
                    return comp.getName();
                } else if (ky.equals("data")) {
                    //return comp.getData()!=null?comp.getData().toJSON():""; 
                    return comp.getData() != null ? comp.getData() : Nset.newObject(); //new  
                } else if (ky.equals("datajson")) {
                    return comp.getData() != null ? comp.getData().toJSON() : "";
                } else if (ky.equals("datanset")) {
                    return comp.getData() != null ? comp.getData() : Nset.newObject();
                } else if (ky.equals("style")) {
                    return comp.getViewStyle();
                } else if (ky.equals("class")) {
                    return comp.getViewClass();
                } else if (ky.equals("attribut")) {
                    return comp.getViewAttribut();
                } else if (ky.equals("cselect")) {
                    if (comp instanceof SmartGrid) {
                        ((SmartGrid) comp).setSelectedRow("[]");
                    }
                } else if (ky.equals("sselect")) {
                    if (comp instanceof SmartGrid) {
                        Object object = getVirtual("@sselect");
                        if (object instanceof Nset) {
                            ((SmartGrid) comp).setSelectedRow(new Nset(object).toJSON());
                        } else {
                            ((SmartGrid) comp).setSelectedRow(getVirtualString("@sselect"));
                        }
                    }
                }
                return comp.getText();
            }
        } else if (key.startsWith("@+HEADER-")) {
            try {
                return req.getHeader(key.substring(9));
            } catch (Exception e) {
                return "";
            }
        } else if (key.startsWith("@+REMOTE-")) {
            try {
                if (key.startsWith("@+REMOTE-ADDRESS")) {
                    return req.getRemoteAddr();
                }
                if (key.startsWith("@+REMOTE-HOST")) {
                    return req.getRemoteHost();
                }
                if (key.startsWith("@+REMOTE-PORT")) {
                    return req.getRemotePort();
                }
                if (key.startsWith("@+REMOTE-USER")) {
                    return req.getRemoteUser();
                }
            } catch (Exception e) {
                return "";
            }
        } else if (key.startsWith("@+SERVER-")) {
            try {
                if (key.startsWith("@+SERVER-NAME")) {
                    return req.getServerName();
                }
                if (key.startsWith("@+SERVER-PORT")) {
                    return req.getServerPort();
                }
                if (key.startsWith("@+REMOTE-PATH")) {
                    return req.getServletPath();
                }
            } catch (Exception e) {
                return "";
            }
        } else if (key.equalsIgnoreCase("@+RAW")) {
            try {
                InputStream is = req.getInputStream();
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    arrayOutputStream.write(buffer, 0, length);
                }
                return arrayOutputStream.toString();
            } catch (Exception e) {
                return "";
            }
        } else if (key.startsWith("@+EXPORT-MODE")) {
            return String.valueOf(virtual.get(String.valueOf(key).toLowerCase().trim()));
        } else if (key.startsWith("@+") || key.startsWith("@-")) {
            try {
                //return req.getAttribute(key);
                return resp.getVirtualStatic(key);
            } catch (Exception e) {
            }

        } else if (key.startsWith("@") || key.startsWith("!")) {
            String stream = "";
            if (key.contains("[")) {
                stream = key.substring(key.indexOf("["));
                key = key.substring(0, key.indexOf("["));
            }
            key = key.trim();
            stream = stream.trim();

            Object obj = virtual.get(String.valueOf(key).toLowerCase().trim());
            if (key.equals("@")) {
                obj = "";
            }

            if (obj == null) {
                try {
                    return resp.getParameter(key.substring(1));
                } catch (Exception e) {
                }
            } else if (obj instanceof Nson && !stream.equals("")) {
                stream = runArrayStream(stream);
                return ((Nson) obj).get(stream);
            } else if (obj instanceof Nset && !stream.equals("")) {
                stream = runArrayStream(stream);
                return ((Nset) obj).get(stream);
            } else if (obj instanceof Nikitaset && !stream.equals("")) {
                stream = runArrayStream(stream);
                return ((Nikitaset) obj).getStream(stream);
            } else if (obj instanceof String && !stream.equals("")) {
                stream = runArrayStream(stream);
                return StringAction.getStringStream(((String) obj), stream);
            }
            return obj;
        } else if (key.startsWith("&")) {
            try {
                return req.getParameter(key.substring(1));
            } catch (Exception e) {
            }
        } else if (key.startsWith("!") || key.startsWith("'")) {
            return key.substring(1);
        }
        return var;
    }

    private Nset fillStreamNset(Nset stream) {
        if (stream.isNsetArray()) {
            Nset out = Nset.newArray();
            for (int i = 0; i < stream.getArraySize(); i++) {
                out.addData(fillStreamNset(stream.getData(i)));
            }
            return out;
        } else if (stream.isNsetObject()) {
            Nset out = Nset.newObject();
            String[] keys = stream.getObjectKeys();
            for (int i = 0; i < keys.length; i++) {
                out.setData(keys[i], fillStreamNset(stream.getData(keys[i])));
            }
            return out;
        } else {
            if (stream.toString().startsWith("@") || stream.toString().startsWith("$") || stream.toString().startsWith("!")) {
                Object obj = getVirtual(stream.toString());
                if (obj instanceof Nset) {
                    obj = ((Nset) obj);
                } else if (obj instanceof String) {
                    obj = (String) obj;
                } else if (obj instanceof Double) {
                    obj = (Double) obj;
                } else if (obj instanceof Integer) {
                    obj = (Integer) obj;
                } else if (obj instanceof Long) {
                    obj = (Long) obj;
                } else if (obj instanceof Boolean) {
                    obj = (Boolean) obj;
                } else if (obj instanceof Vector) {
                    obj = (((Vector) obj));
                } else if (obj instanceof Hashtable) {
                    obj = (((Hashtable) obj));
                } else {
                    obj = "";
                }
                return new Nset(obj);
            } else {
                return stream;
            }
        }
    }

    private String runArrayStream(String stream) {
        if (stream.contains("@") || stream.contains("$") || stream.startsWith("!")) {
            Nset n = Nset.readJSON(stream);
            Nset out = Nset.newArray();
            for (int i = 0; i < n.getArraySize(); i++) {
                if (n.getData(i).toString().startsWith("@") || n.getData(i).toString().startsWith("$") || n.getData(i).toString().startsWith("!")) {
                    Object obj = getVirtual(n.getData(i).toString());
                    if (obj instanceof Nset) {
                        out.addData((Nset) obj);
                    } else if (obj instanceof String) {
                        out.addData((String) obj);
                    } else if (obj instanceof Double) {
                        out.addData((Double) obj);
                    } else if (obj instanceof Integer) {
                        out.addData((Integer) obj);
                    } else if (obj instanceof Long) {
                        out.addData((Long) obj);
                    } else if (obj instanceof Boolean) {
                        out.addData((Boolean) obj);
                    } else if (obj instanceof Vector) {
                        out.addData((Vector) obj);
                    } else if (obj instanceof Hashtable) {
                        out.addData((Hashtable) obj);
                    } else if (obj == null) {
                        out.addData(n.getData(i).toString());
                    } else {
                        out.addData(obj.toString());
                    }
                } else {
                    out.addData(n.getData(i).toString());
                }
            }
            return out.toJSON();
        }
        return stream;
    }

    public static String getCookieOrSessionKey(String key) {
        if (key.startsWith("@+SESSION")) {
            if (key.startsWith("@+SESSION.")) {
                key = key.substring(10);
            } else if (key.startsWith("@+SESSION-")) {
                key = key.substring(10);
            } else {
                key = key.substring(2);
            }
        } else if (key.startsWith("@+COOKIE")) {
            if (key.startsWith("@+COOKIE.")) {
                key = key.substring(9);
            } else if (key.startsWith("@+COOKIE-")) {
                key = key.substring(9);
            } else {
                key = key.substring(2);
            }
        }
        return key;
    }

    public void setVirtualRegistered(String key, Object data) {
        if (key.startsWith("@+CORE-MOBILE-PUBLIC")) {
            key = key.substring("@+CORE-MOBILE-PUBLIC-".length()).trim();
            Generator.setCoreVirtualPublicString(this, key, data);
        } else if (key.startsWith("@+CORE-WEB-OPENSTREAM")) {
            Generator.setCoreOpenForm(this, key, data);
        } else if (key.equalsIgnoreCase("@nativecall") || key.equalsIgnoreCase("@nativemode") || key.startsWith("@+SESSION") || key.startsWith("@+SETTING-N-") || key.startsWith("@+CORE-")) {
            try {
                req.getSession().setMaxInactiveInterval(NikitaConnection.getDefaultPropertySetting().getData("init").getData("sessionmax").toInteger());
                req.getSession().setAttribute(getCookieOrSessionKey(key), data);
            } catch (Exception e) {
            }
            return;
        } else if (key.startsWith("@+COOKIE")) {
            Cookie cookie = new Cookie(getCookieOrSessionKey(key), data.toString());
            cookie.setMaxAge(NikitaConnection.getDefaultPropertySetting().getData("init").getData("cookiemax").toInteger());
            try {
                resp.addCookie(cookie);
            } catch (Exception e) {
            }
            return;
        } else if (key.startsWith("@+") || key.startsWith("@-")) {
            try {
                //req.setAttribute(key, data);
                resp.setVirtualStatic(key, data);
            } catch (Exception e) {
            }
        }
    }

    public void setVirtual(String key, Object data) {
        if (key.equals("")) {
        } else if (key.equalsIgnoreCase("@nativecall") || key.equalsIgnoreCase("@nativemode")) {
            setVirtualRegistered(key, data);
            return;
        } else if (key.startsWith("@+SESSION") || key.startsWith("@+SETTING-N-") || key.startsWith("@+CORE-") || key.startsWith("@+COOKIE") || key.startsWith("@+AUTHENTICATION") || key.startsWith("@+AUTHENTICATION")) {
            setVirtualRegistered(key, data);
            return;
        } else if (key.startsWith("@+LOCAL-")) {
            NikitaService.getBaseNikitaGeneratorData().setData(key.substring(8), String.valueOf(data));
            requestVariableLocal();
            return;
        } else if (key.startsWith("@+STATIC-")) {
            AppNikita.getInstance().setVirtual(key, String.valueOf(data));
            return;
        } else if (key.startsWith("@+EXPORT-MODE")) {
            virtual.put(String.valueOf(key).toLowerCase().trim(), data);
        } else if (key.startsWith("@+") || key.startsWith("@-")) {
            return;
        } else if (key.startsWith("@")) {
            virtual.put(String.valueOf(key).toLowerCase().trim(), data);
        } else if (key.startsWith("!")) {
            virtual.put(String.valueOf(key).toLowerCase().trim(), data);
        }
    }

    private String suffVariable(String key) {
        return "";
    }

    private String prefVariable(String key) {
        return "";
    }

    private boolean close = false;

    public void setLogicClose(boolean b) {
        setInterupt(b);
    }

    public void sendNext(String fna) {
        Nset n = Nset.newObject();
        n.setData("code", "open");
        n.setData("id", "");
        n.setData("data", fna);

        addtoSteamData(n);
    }

    private void addtoSteamData(Nset n) {
        if (n.getData("code").toString().equals("alert")) {
            System.out.print("alert:");
            System.out.println(writelock);
        }
        if (!writelock) {
            actionstream.addData(n);
        } else {
            if (n.getData("code").toString().equals("refresh")) {
            } else {
                actiononload.addData(n);
            }
        }
    }

    public void sendRedirect(String url) {
        try {
            resp.addHeader("Location ", url);
            resp.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
        } catch (Exception e) {
        }
    }

    public void sendNext(NikitaRequest request, NikitaLogic logic) {
        if (!NikitaService.runPublicServlet(fname, request, this, logic)) {
            NikitaService.showPageNotFound(resp);//??
        }
    }

    public void filterNext() {
        filterNext("");
    }

    public void filterNext(String fname) {
        this.fname = fname;
        Consume();
    }

    public void filterBaseReload(String fname, NikitaRequest req, NikitaResponse res, NikitaLogic nl) {
        if (fname.startsWith("{")) {
            Nset n = Nset.readJSON(fname);
            fname = n.getData("formid").toString();
            if (!n.getData("formname").toString().equals("")) {
                fname = n.getData("formname").toString();
            }
        }
        runServletGen(fname, req, res, nl);
        if (res.getContent() != null) {
            NikitaViewV3 nikitaViewV3 = NikitaViewV3.create();
            res.writeStream(res.getContent().getViewHtml(nikitaViewV3));
            nikitaViewV3.save(res.getVirtualString("@+NIKITAID"), res.getContent().getFileNikita());
        }
        Consume();
    }

    public void sendforward(String path) {
        try {
            //RequestDispatcher rd =req.getRequestDispatcher(path);
            //rd.forward(req, resp);
        } catch (Exception e) {
        }
    }

    public void sendInclude(String path) {
        try {

            //RequestDispatcher rd =req.getRequestDispatcher(path);
            //rd.include(req, resp);
        } catch (Exception e) {
        }
    }

    public void generateReport(NikitaRequest nikitaRequest, String pdfxlstype, String datasource, String nikitaconnection, Nset args, Nikitaset nikitaset, InputStream jasper, OutputStream outputStream) {
        ReportAction.generateReport(nikitaRequest, this, pdfxlstype, datasource, nikitaconnection, args, nikitaset, jasper, outputStream);
    }

    public void openWindows(String url, String target) {
        Nset n = Nset.newObject();
        n.setData("code", "open");
        n.setData("id", target);
        n.setData("data", url);
        Consume();
        addtoSteamData(n);
    }

    public void clientWindows(String url, String methode, String reqrescode, String args) {
        Nset n = Nset.newObject();
        n.setData("code", "client");
        n.setData("id", getContent().getJsId());
        n.setData("methode", methode);
        n.setData("reqrescode", reqrescode);
        n.setData("data", url);
        if (args.trim().startsWith("{") && args.trim().endsWith("}")) {
            n.setData("args", args);
        } else {
            n.setData("args", "{}");
        }
        Consume();
        addtoSteamData(n);
    }

    public void openDialogPopUp(String url, String style) {
        Nset n = Nset.newObject();
        n.setData("code", "diapopup");
        n.setData("data", url);

        n.setData("style", style);
        Consume();
        addtoSteamData(n);
    }

    public void openWindowsGen(String fname, NikitaRequest req, NikitaResponse res, NikitaLogic nl) {

        String[] fn = splitFname(fname);
        runServletGen(fn[0], req, res, nl);
        if (getContent() != null) {
            NikitaForm content = res.getContent();
            showContentBody(content);
        }
    }

    public void showContentGen(String fname, String reqcode, String target, NikitaRequest req, NikitaResponse res, NikitaLogic nl) {
        showWindowsGen(fname, reqcode, target, req, res, nl);
    }

    public void showWindowsGen(String fname, String reqcode, String target, NikitaRequest req, NikitaResponse res, NikitaLogic nl) {
        String owner = getContent().getJsId();
        target = getContent().getComponentbyId(target).getJsId();

        NikitaForm nf = getContent();  //buffer         

        String[] fn = splitFname(fname);
        res = newInstance();
        res.Consume();//must
        runServletGen(fn[0], req, res, nl);
        if (res.getContent() != null) {
            NikitaForm content = res.getContent();
            content.setInstanceId(fn[1]);
            ///setContent(nf);     
            //showContent(content, target);     

            content.setFormCaller(owner);
            content.setInstanceId(fn[1]);

            content.setInitLoad(res.getInitLoadAndClear());//addd for init
            showContentform(content, reqcode, target);

            setContent(nf);  //refiil
        }
    }

    public void openWindows(String fname, String target, NikitaRequest r) {
        r.setParameter("action", "");

        NikitaForm nf = getContent();
        Consume();
        String[] fn = splitFname(fname);
        NikitaService.runLocalServlet(fn[0], r, this, null);
        if (getContent() != null) {
            NikitaForm content = getContent();
            setContent(nf);
            content.setInstanceId(fn[1]);
            showContent(content, target);
        }
    }

    private String[] splitFname(String fname) {
        String[] result = new String[2];
        if (fname.contains("[")) {
            result[0] = fname.substring(0, fname.indexOf("["));
            fname = fname.substring(fname.indexOf("["));
            fname = fname.replace("[", "").replace("]", "");
            result[1] = fname;
        } else {
            result[0] = fname;
            result[1] = "";
        }
        return result;
    }

    public void showWindows(String fname, NikitaRequest r, NikitaLogic nl) {
        r.setParameter("action", "");
        Consume();
        // NikitaService.runLocalServlet(fname, r, this, nl);
        runServletGen(fname, r, this, null);
        /*
        Nset n = Nset.newObject();
        n.setData("code", "open");
        n.setData("data",  this.getContent().getViewHtml());         
        actionstream.addData(n); 
         */
        actionstream = Nset.newArray();
        NikitaViewV3 nikitaViewV3 = NikitaViewV3.create();
        writeStream(this.getContent().getViewHtml(nikitaViewV3));
        nikitaViewV3.save(getVirtualString("@+NIKITAID"), getContent().getFileNikita());

    }

    public void sendPushMessage(String device, String data) {

    }

    public void sendChat(String data) {
        Nset n = Nset.newObject();
        n.setData("code", "sendchat");
        n.setData("data", data);
        Consume();
        addtoSteamData(n);
    }

    public void addMapMarker(String jsid, Nset data) {
        Nset n = Nset.newObject();
        n.setData("code", "addmapmarker");
        n.setData("id", jsid);
        n.setData("data", data);
        Consume();
        addtoSteamData(n);
    }

    public void setMapMarker(String jsid, Nset data) {
        Nset n = Nset.newObject();
        n.setData("code", "setmapmarker");
        n.setData("id", jsid);
        n.setData("data", data);
        Consume();
        addtoSteamData(n);
    }

    public void reloadBrowser() {
        Nset n = Nset.newObject();
        n.setData("code", "reload");
        Consume();
        addtoSteamData(n);
    }

    public void reload(NikitaRequest r) {
        String owner = getContent().getJsId();
        r.setParameter("action", "");
        Consume();
        //add 29102015    

        //NikitaViewV3 nikitaViewV3  = NikitaViewV3.create();                    
        //getContent().getViewHtml(nikitaViewV3);
        //nikitaViewV3.save( getVirtualString("@+NIKITAID") , getContent().getFileNikita() );
        NikitaService.runLocalServlet(fname, r, this, null);
        if (getContent() != null) {
            getContent().setFormCaller(owner);
            showContent(getContent());

        }
    }

    public void showform(String fname, NikitaRequest r) {
        showform(fname, r, "", true);
    }

    public void runServletGen(String fname, NikitaRequest req, NikitaResponse res, NikitaLogic nl) {
        runServletGen(fname, req, res, nl, null);
    }

    public void runServletGen(String fname, NikitaRequest req, NikitaResponse res, NikitaLogic nl, NikitaEngine nikitaEngine) {
        int i = getNikitaLogic().getCurrentRow();
        int l = getNikitaLogic().getLoopCount();
        boolean b = getNikitaLogic().expression;

        req.setParameter("form", fname);
        if (!nl.getFormType().contains("form")) {
            nl.setForm(nl.getFormId(), nl.getFormName(), nl.getFormTitle(), "form", nl.getFormStyle());
        }

        //02022017  
        if (fname.contains(NikitaEngine.NFID_ARRAY_INDEX)) {
            req.setParameter("findex", fname);
        } else {
            req.setParameter("findex", "");
        }

        req.setParameter("action", "");
        req.setParameter("component", "");
        res.Consume();
        if (!NikitaService.runLocalServlet(fname, req, res, nl, nikitaEngine)) {
            boolean isID = Utility.isNumeric(fname);
            Nikitaset nikitaset;
            if (nikitaEngine != null) {
                nikitaset = nikitaEngine.getForm();
            } else {
                nikitaset = res.getConnection().QueryPage(1, 1, "SELECT formid,formname,formtitle,formtype,formstyle FROM web_form WHERE " + (isID ? "formid" : "formname") + " = ?", NikitaEngine.getFormNameNoIndex(fname));
            }
            NikitaLogic nikitaLogic = getNikitaLogic();
            nikitaLogic.setForm(nikitaset.getText(0, 0), nikitaset.getText(0, 1), nikitaset.getText(0, 2), nikitaset.getText(0, 3), nikitaset.getText(0, 4));
            nikitaLogic.setFnameIndex(fname);//03022017

            if (nikitaset.getRows() >= 1) {
                nikitaLogic.setForm(nikitaset.getText(0, 0), nikitaset.getText(0, 1), nikitaset.getText(0, 2), nikitaset.getText(0, 3), nikitaset.getText(0, 4));
                if (nikitaLogic.getFormStyle().contains(":")) {
                    Style style = Style.createStyle(nikitaLogic.getFormStyle());
                    nikitaLogic.setFormVersion(style.getInternalObject().getData("style").getData("version").toString(), style.getInternalObject().getData("style").getData("isprivate").toString().equals("true"));
                }

            }

            new NikitaServlet().runFirst(req, res, nikitaLogic, nikitaEngine);
        }

        getNikitaLogic().expression = b;
        getNikitaLogic().setCurrentRow(i);
        getNikitaLogic().setLoopCount(l);
        setVirtualRegistered("@+LOGICCOUNT", getNikitaLogic().getLoopCount());
        setVirtualRegistered("@+LOGICCOUNTB1", getNikitaLogic().getLoopCount() + 1);
    }

    public void showformGen(String fname, NikitaRequest r, String reqcode, boolean modal) {
        showformGen(fname, r, reqcode, modal, "");
    }

    public void showformGen(String fname, NikitaRequest r, String reqcode, boolean modal, String newinst) {
        String owner = getContent().getJsId();
        Consume();
        NikitaForm nf = getContent();  //buffer 
        NikitaResponse res = newInstance();
        res.Consume();//must
        runServletGen(fname, r, res, getNikitaLogic());
        if (res.getContent() != null) {
            res.getContent().setFormCaller(owner);
            res.getContent().setInstanceId(newinst);

            res.getContent().setInitLoad(res.getInitLoadAndClear());//addd for init
            if (!NikitaEngine.getFormIndex(fname).equals("")) {
                res.getContent().setText(res.getContent().getText() + " [" + NikitaEngine.getFormIndex(fname) + "]");
            }
            showform(res.getContent(), reqcode, modal);

            setContent(nf);//refill
        }
    }

    public void showform(String fname, NikitaRequest r, String reqcode, boolean modal) {
        String owner = getContent().getJsId();

        NikitaForm nf = getContent();  //buffer   
        r.setParameter("action", "");

        Consume();
        NikitaResponse res = newInstance();
        res.Consume();//must
        NikitaService.runLocalServlet(fname, r, res, null);
        if (res.getContent() != null) {
            res.getContent().setFormCaller(owner);
            res.getContent().setInitLoad(res.getInitLoadAndClear());//addd for init

            showform(res.getContent(), reqcode, modal);

            setContent(nf);//refill
        }

    }

    /*
    public Component cloneform(String fname, NikitaRequest r){             
            r.setParameter("action", "");   
                   
            NikitaResponse res = new NikitaResponse(null, resp);     
            res.Consume(); 
            NikitaService.runLocalServlet(fname, r, res, null);
        if (res.getContent()!=null) {
            return  res.getContent();
        }
        return new Component();
    }
     */
    public void callWait() {
        Nset n = Nset.newObject();
        n.setData("code", "callwait");
        Consume();
        addtoSteamData(n);
    }

    public void showBusy() {
        Nset n = Nset.newObject();
        n.setData("code", "showbusy");
        Consume();
        addtoSteamData(n);
    }

    public void updateProgressbar(int val, String message) {
        if (req.getParameter("prbarid") != null) {
            Nset n = Nset.newObject();
            n.setData("value", val);
            n.setData("message", message);
            //setVirtual("@+SESSION-NIKITA-PROGRESS-"+req.getParameter("prbarid"), n.toString());
            AppNikita.getInstance().setVirtual("@+SESSION-NIKITA-PROGRESS-" + req.getParameter("prbarid"), n.toJSON());
        }
        if (val >= 100) {
            AppNikita.getInstance().removeVirtual("@+SESSION-NIKITA-PROGRESS-" + req.getParameter("prbarid"));
        }
    }

    public void checkfilesubmit(Component comp, String fname) {
        Nset n = Nset.newObject();
        n.setData("code", "checkfile");
        n.setData("id", comp.getJsId() + "_form");
        n.setData("fileid", comp.getJsId() + "_text");
        n.setData("text", fname);
        n.setData("fid", getContent().getJsId());//owner form
        Consume();
        addtoSteamData(n);
    }

    public void submit(Component comp, String fname) {
        Nset n = Nset.newObject();
        n.setData("code", "submit");
        n.setData("id", comp.getJsId() + "_form");
        n.setData("fileid", comp.getJsId() + "_text");
        n.setData("text", fname);
        n.setData("fid", getContent().getJsId());//owner form
        Consume();
        addtoSteamData(n);
    }

    public void requestVariableLocal() {
        if (actionstream.getArraySize() >= 1) {
        } else {
            Nset n = Nset.newObject();
            Consume();
            //addtoSteamData(n);  
        }
    }

    public void variableLocal() {
        Nset n = Nset.newObject();
        n.setData("code", "local");
        n.setData("data", NikitaService.getBaseNikitaGeneratorData().toJSON());
        Consume();
        //addtoSteamData(n);          
    }

    public Nset captureSnapshot(String urlString, String param) {
        String fname = Utility.MD5(urlString);

        if (param.equals("") || param.equals("image")) {
            try {
                Nset farrayname = Nset.newArray();

                String fsavename = System.currentTimeMillis() + "-" + fname + "-" + getVirtualString("@+CONNECTIONCOUNT");

                String pathfile = NikitaService.getDirTmp() + NikitaService.getFileSeparator() + fsavename + ".tmp";

                InputStream is = NikitaInternet.getHttp(urlString).getEntity().getContent();
                OutputStream os = new FileOutputStream(pathfile);

                byte[] buffer = new byte[1024];
                int length;
                int filesize = 0;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                    filesize = filesize + length;
                }
                os.flush();
                os.close();
                is.close();

                Nset nres = Nset.newObject();
                nres.setData("filename", "image");
                nres.setData("filesize", filesize);
                nres.setData("fname", fname);
                nres.setData("fsavename", fsavename);
                nres.setData("id", "");

                farrayname.addData(nres);

                nres = Nset.newObject();
                nres.setData("filename", farrayname.getData(0).getData("filename").toString());
                nres.setData("filesize", farrayname.getData(0).getData("filesize").toString());
                nres.setData("fname", farrayname.getData(0).getData("fname").toString());
                nres.setData("fsavename", farrayname.getData(0).getData("fsavename").toString());
                nres.setData("id", farrayname.getData(0).getData("id").toString());

                nres.setData("files", farrayname);

                return nres;
            } catch (Exception e) {
                Nset farrayname = Nset.newArray();

                Nset nres = Nset.newObject();
                nres.setData("error", e.getMessage());
                nres.setData("fname", fname);

                farrayname.addData(nres);

                nres = Nset.newObject();
                nres.setData("error", farrayname.getData(0).getData("error").toString());
                nres.setData("fname", farrayname.getData(0).getData("fname").toString());

                nres.setData("files", farrayname);
                return nres;
            }
        } else if (param.equals("mjpeg")) {
            try {
                Nset farrayname = Nset.newArray();

                String fsavename = System.currentTimeMillis() + "-" + fname + "-" + getVirtualString("@+CONNECTIONCOUNT");

                String pathfile = NikitaService.getDirTmp() + NikitaService.getFileSeparator() + fsavename + ".tmp";

                InputStream is = NikitaInternet.getHttp(urlString).getEntity().getContent();
                OutputStream os = new FileOutputStream(pathfile);

                MjpegInputStream inputStream = new MjpegInputStream(is);
                byte[] data = inputStream.readMjpegFrame();
                os.write(data);
                os.flush();
                os.close();
                is.close();

                Nset nres = Nset.newObject();
                nres.setData("filename", "mjpeg");
                nres.setData("filesize", data.length);
                nres.setData("fname", fname);
                nres.setData("fsavename", fsavename);
                nres.setData("id", "");

                farrayname.addData(nres);

                nres = Nset.newObject();
                nres.setData("filename", farrayname.getData(0).getData("filename").toString());
                nres.setData("filesize", farrayname.getData(0).getData("filesize").toString());
                nres.setData("fname", farrayname.getData(0).getData("fname").toString());
                nres.setData("fsavename", farrayname.getData(0).getData("fsavename").toString());
                nres.setData("id", farrayname.getData(0).getData("id").toString());

                nres.setData("files", farrayname);
                return nres;
            } catch (Exception e) {
                Nset farrayname = Nset.newArray();

                Nset nres = Nset.newObject();
                nres.setData("error", e.getMessage());
                nres.setData("fname", fname);

                farrayname.addData(nres);

                nres = Nset.newObject();
                nres.setData("error", farrayname.getData(0).getData("error").toString());
                nres.setData("fname", farrayname.getData(0).getData("fname").toString());

                nres.setData("files", farrayname);
                return nres;
            }
        }

        return Nset.newObject();
    }

    public void captureMJPEG(Component comp, String urlString, String fname) {
        URL url = null;
        try {
            //urlString="http://ce3014.myfoscam.org:20054/videostream.cgi?user=user&pwd=user&rate=0";
            // urlString="http://plazacam.studentaffairs.duke.edu/axis-cgi/mjpg/video.cgi";
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            System.err.println("Invalid URL");
            return;
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setRequestProperty("Authorization", "Basic " + base64.encode(user + ":" + pass));
            //httpIn = new BufferedInputStream(conn.getInputStream(), 8192);

            MjpegInputStream inputStream = new MjpegInputStream(conn.getInputStream());

            ByteArrayInputStream jpgIn = new ByteArrayInputStream(inputStream.readMjpegFrame());
            Utility.copyFile(jpgIn, "D:\\s.png");
            inputStream.close();
            conn.disconnect();
        } catch (IOException e) {
            System.err.println("Unable to connect: " + e.getMessage());
            return;
        }

    }

//    public void submitDone(String rescode, Nset w){
//        actionresult=w;
//        Nset n = Nset.newObject();
//        n.setData("code", "result");
//        n.setData("id",   getContent().getJsId());//sent toself
//        n.setData("fid",  getContent().getJsId());//owner form
//        n.setData("data", actionresult.toJSON());
//        n.setData("reqrescode", Nset.newObject().setData("responsecode", rescode).setData("requestcode", "submit").toJSON()   );
//        addtoSteamData(n);
//    }
    public void showContentform(String targetjsid, String stream) {
        Nset n = Nset.newObject();
        n.setData("code", "contentform");
        n.setData("id", targetjsid);//target 
        n.setData("data", stream);
        Consume();
        addtoSteamData(n);
    }

    private void showContentform(NikitaForm w, String reqcode, String targetjsid) {
        Nset n = Nset.newObject();
        n.setData("code", "contentform");
        n.setData("id", targetjsid);//target
        n.setData("fid", w.getJsId());
        n.setData("text", w.getText());

        n.setData("hideoldcontent", "true");
        n.setData("useexisting", "true");

        n.setData("reqcode", reqcode);

        w.setRequestCode(reqcode);
        NikitaViewV3 nikitaViewV3 = NikitaViewV3.create();
        nikitaViewV3s.add(nikitaViewV3);
        WebAction.latchsaveWeb(w, this);//23/01/2017

        n.setData("data", w.getViewTitle() + w.getViewForm(nikitaViewV3));
        Consume();
        addtoSteamData(n);
    }

    public void showform(NikitaForm w, String reqcode, boolean modal) {
        Nset n = Nset.newObject();
        n.setData("code", "showform");
        n.setData("id", "form" + w.getJsId());
        n.setData("fid", w.getJsId());
        n.setData("text", w.getEngineMask() + w.getText());//title
        n.setData("modal", modal ? "true" : "false");
        n.setData("reqcode", reqcode);
        n.setData("consume", w.isBackConsume() ? "close" : "no");
        n.setData("fname", w.getName());

        if (w.getStyle() != null) {
            if (w.getViewStyle().contains("n-maximize:true")) {
                w.getStyle().getInternalObject().getData("style").setData("maximize", "true");
                w.getStyle().getInternalObject().getData("style").setData("maximizable", "true");
            }
            if (w.getViewStyle().contains("n-maximizable:false")) {
                w.getStyle().getInternalObject().getData("style").setData("maximizable", "false");
            } else {
                w.getStyle().getInternalObject().getData("style").setData("maximizable", "true");
            }
            if (w.getViewStyle().contains("n-minimizable:false")) {
                w.getStyle().getInternalObject().getData("style").setData("minimizable", "false");
            } else {
                w.getStyle().getInternalObject().getData("style").setData("minimizable", "true");
            }
            if (w.getViewStyle().contains("n-closable:false")) {
                w.getStyle().getInternalObject().getData("style").setData("closable", "false");
            } else {
                w.getStyle().getInternalObject().getData("style").setData("closable", "true");
            }

            if (w.getViewStyle().contains("n-resizable:false")) {
                w.getStyle().getInternalObject().getData("style").setData("resizable", "false");
            } else {
                w.getStyle().getInternalObject().getData("style").setData("resizable", "true");
            }
            if (w.getViewStyle().contains("n-draggable:false")) {
                w.getStyle().getInternalObject().getData("style").setData("draggable", "false");
            } else {
                w.getStyle().getInternalObject().getData("style").setData("draggable", "true");
            }

            String wd = w.getStyle().getInternalObject().getData("style").getData("width").toString();
            if (wd.equals("auto") || wd.endsWith("%") || wd.equals("")) {
            } else if (!Utility.isNumeric(wd)) {
                w.getStyle().getInternalObject().getData("style").setData("width", Utility.getNumberOnlyInt(wd));
            }
            String hd = w.getStyle().getInternalObject().getData("style").getData("width").toString();
            if (hd.equals("auto") || hd.equals("") || hd.equals("%")) {
            } else if (!Utility.isNumeric(hd)) {
                w.getStyle().getInternalObject().getData("style").setData("height", Utility.getNumberOnlyInt(hd));
            }

            n.setData("style", w.getStyle().getInternalObject().getData("style").toJSON());
        } else {
            n.setData("style", Nset.newObject().setData("closable", "true").setData("minimizable", "true").setData("maximizable", "true").setData("resizable", "true").setData("draggable", "true").toJSON());
        }

        w.setRequestCode(reqcode);
        NikitaViewV3 nikitaViewV3 = NikitaViewV3.create();
        nikitaViewV3s.addElement(nikitaViewV3);

        WebAction.latchsaveWeb(w, this);//23/01/2017

        n.setData("data", w.getViewForm(nikitaViewV3));
        Consume();
        addtoSteamData(n);
    }

    public void refreshComponent(String id) {
        refreshComponent(getContent().findComponentbyId(id));
    }

    public void refreshClassStyle(Component w, String cls, boolean add) {
        Nset n = Nset.newObject();
        if (add) {
            n.setData("code", "addclass");
            n.setData("id", w.getJsId());
        } else {
            n.setData("code", "removeclass");
            n.setData("id", w.getJsId());
        }
        n.setData("class", cls);
        Consume();
        addtoSteamData(n);
    }
 public void refreshBadge(  String cls, String badge) {
        Nset n = Nset.newObject();
        n.setData("code", "badge");        
        n.setData("badge", badge);        
        n.setData("class", cls);
        Consume();
        addtoSteamData(n);
    }
 public void refreshPbar(  String cls, String value) {
        Nset n = Nset.newObject();
        n.setData("code", "pbar");        
        n.setData("value", value);        
        n.setData("class", cls);
        Consume();
        addtoSteamData(n);
    }
public void refreshOther(  String cls, String text) {
        Nset n = Nset.newObject();
        n.setData("code", "other");        
        n.setData("text", text);        
        n.setData("class", cls);
        Consume();
        addtoSteamData(n);
    }
    public void refreshComponent(Component w) {
        w.setForm(getContent());

        int i = isStreamContain("refresh", w.getJsId());
        if (i >= 0) {
            try {
                ((Vector) actionstream.getInternalObject()).removeElementAt(i);
            } catch (Exception e) {
            }
        }

        Nset n = Nset.newObject();
        n.setData("code", "refresh");
        n.setData("id", w.getJsId());

        WebAction.latchsaveWeb(w, getContent(), this);//23/01/2017
        /*
        if (w.getStyle()!=null) {
            if (!w.isVisible()) {
                w.getStyle().setStyle("display", "none");
            }
            n.setData("style",w.getStyle().getInternalObject().getData("style").toJSON());
        }else{
            if (!w.isVisible()) {
                n.setData("style", new Style().setStyle("display", "none").getInternalObject().getData("style").toJSON());
            }else{
                n.setData("style", "{}");
            }            
        }
         */
        Style style = new Style();
        if (w instanceof DivLayout) {
            style = w.getAutoInstanceStyle();
        }
        if (!w.isVisible()) {
            style.setStyle("display", "none");
        } else {
            style.setStyle("display", "inherit");
        }

        n.setData("style", style.getInternalObject().getData("style").toJSON());

        if (!nikitaViewV3s.contains(currNikitaViewV3)) {
            nikitaViewV3s.addElement(currNikitaViewV3);
        }
        n.setData("data", w.getView(currNikitaViewV3));
        n.setData("type", w.getType());

        Consume();
        addtoSteamData(n);
    }
    private NikitaViewV3 currNikitaViewV3 = NikitaViewV3.create();

    public void setCurrentNikitaViewV3(NikitaViewV3 v3) {
        currNikitaViewV3 = v3;
    }

    public NikitaViewV3 getCurrentNikitaViewV3() {
        return currNikitaViewV3;
    }

    public int isStreamContain(String code, String id) {
        for (int i = 0; i < actionstream.getArraySize(); i++) {
            if (actionstream.getData(i).getData("code").toString().equals(code) && actionstream.getData(i).getData("id").toString().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    public void showClipboard(String title, String message, String reqcode, Nset result, String button1, String button2, String button3) {
        Nset n = Nset.newObject();
        n.setData("code", "clipboard");
        n.setData("formid", getContent() != null ? getContent().getJsId() : "");
        n.setData("title", Component.escapeHtml(title));
        n.setData("message", Component.escapeHtml(message));
        n.setData("reqcode", reqcode);

        Nset buttons = Nset.newObject();
        if (!button1.equals("")) {
            buttons.setData("button1", button1);
        }
        if (!button2.equals("")) {
            buttons.setData("button2", button2);
        }
        if (!button3.equals("")) {
            buttons.setData("button3", button3);
        }
        buttons.setData("requestresult", Component.escapeHtml(result != null ? result.toJSON() : "{}")); //request to param 
        n.setData("button", buttons.toJSON());
        Consume();
        addtoSteamData(n);
    }

    public void showDialog(String title, String message, String reqcode, String button1) {
        showDialog(title, message, reqcode, button1, "");
    }

    public void showDialog(String title, String message, String reqcode, String button1, String button2) {
        showDialog(title, message, reqcode, button1, button2, "");
    }

    public void showDialog(String title, String message, String reqcode, String button1, String button2, String button3) {
        showDialogResult(title, message, reqcode, null, button1, button2, button3);
    }

    public void showDialogResult(String title, String message, String reqcode, Nset result, String button1, String button2) {
        showDialogResult(title, message, reqcode, result, button1, button2, "");
    }

    public void showDialogResult(String title, String message, String reqcode, Nset result, String button1, String button2, String button3) {
        Nset n = Nset.newObject();
        n.setData("code", "dialog");
        n.setData("formid", getContent() != null ? getContent().getJsId() : "");
        n.setData("title", title.trim().equals("") ? "~" : Component.escapeHtml(title));
        n.setData("message", Component.escapeHtml(message));
        n.setData("reqcode", reqcode);

        Nset buttons = Nset.newObject();
        if (!button1.equals("")) {
            buttons.setData("button1", button1);
        }
        if (!button2.equals("")) {
            buttons.setData("button2", button2);
        }
        if (!button3.equals("")) {
            buttons.setData("button3", button3);
        }
        buttons.setData("requestresult", Component.escapeHtml(result != null ? result.toJSON() : "{}")); //request to param 
        n.setData("button", buttons.toJSON());
        Consume();
        addtoSteamData(n);
    }

    public void showAlert(String s) {
        Nset n = Nset.newObject();
        n.setData("code", "alert");
        n.setData("data", s);
        addtoSteamData(n);
    }

    public void showContent(NikitaForm w) {
        showContent(w, w.getId());
    }

    public void showContent(NikitaForm w, String target) {
        Nset n = Nset.newObject();
        n.setData("code", "content");
        n.setData("id", getContent().getComponentbyId(target).getJsId());

        NikitaViewV3 nikitaViewV3 = NikitaViewV3.create();
        nikitaViewV3s.add(nikitaViewV3);
        n.setData("data", w.getViewForm(nikitaViewV3));
        WebAction.latchsaveWeb(w, this);//23/01/2017

        //n.setData("formowner", getContent().getFormJsId());
        //n.setData("formcontent", w.getFormJsId());
        addtoSteamData(n);
    }

    public void viewMandatory(Component component, boolean mand) {
        Nset n = Nset.newObject();
        n.setData("code", "viewmandatory");
        n.setData("id", (component.getJsId() + "_label_mand"));
        n.setData("vmandatory", mand);
        addtoSteamData(n);
    }

    public void viewVisibility(Component component, boolean visible) {
        Nset n = Nset.newObject();
        n.setData("code", "viewvisibility");
        n.setData("id", (component.getJsId()));
        n.setData("vvisibility", visible);
        addtoSteamData(n);
    }

    public void viewMandatoryError(Component component, boolean err) {
        Nset n = Nset.newObject();
        n.setData("code", "viewmandatoryerror");
        n.setData("id", component.getJsId());
        n.setData("verror", err);
        addtoSteamData(n);
    }

    public void showContentBody(NikitaForm w) {

        Nset n = Nset.newObject();
        n.setData("code", "body");
        n.setData("id", "");
        NikitaViewV3 nikitaViewV3 = NikitaViewV3.create();
        nikitaViewV3s.add(nikitaViewV3);
        n.setData("data", w.getViewForm(nikitaViewV3));
        WebAction.latchsaveWeb(w, this);//23/01/2017

        addtoSteamData(n);
    }

    public void closeform(NikitaForm v) {
        closeform(v.getId(), v.getInstanceId());
    }

    public void requestfocus(Textbox comp) {
        Nset n = Nset.newObject();
        n.setData("code", "requestfocus");
        n.setData("id", comp.getJsId() + "_text");
        n.setData("data", "");
        addtoSteamData(n);
    }

    public void closeform(String fname) {
        Nset n = Nset.newObject();
        n.setData("code", "closeform");
        if (fname.equals("*")) {
            n.setData("id", "*");
        } else {
            n.setData("id", "form" + fname + "-");
            n.setData("fname", fname);
        }
        WebAction.latchcloseWeb(fname);
        Consume();
        addtoSteamData(n);
    }

    public void closeform(String fname, String instance) {
        Nset n = Nset.newObject();
        n.setData("code", "closeform");
        n.setData("id", "form" + fname + "-" + instance);
        n.setData("fname", fname);
        WebAction.latchcloseWeb(fname + "-" + instance);
        Consume();
        addtoSteamData(n);
    }

    public void backform(String fname, String instance) {
        Nset n = Nset.newObject();
        n.setData("code", "backform");
        n.setData("id", "form" + fname + "-" + instance);
        n.setData("fname", fname);
        WebAction.latchcloseWeb(fname + "-" + instance);
        Consume();
        addtoSteamData(n);
    }

    public void cleardataform(String fname, String instance) {
        Nset n = Nset.newObject();
        n.setData("code", "cleardataform");
        n.setData("id", "form" + fname + "-" + instance);
        n.setData("fname", fname);
        WebAction.latchcloseWeb(fname + "-" + instance);
        Consume();
        addtoSteamData(n);
    }
    private long outlength = 0;

    public long getAppxOutData() {
        return outlength;
    }

    public Nset getInitLoadInternalObject() {
        return actiononload;
    }

    public void initLoadInclude(NikitaResponse nikitaResponse) {
        try {
            ((Vector) actiononload.getInternalObject()).addAll((Vector) nikitaResponse.getInitLoadInternalObject().getInternalObject());
        } catch (Exception e) {
        }
    }

    public String getInitLoad() {
        //System.out.println("getInitLoad");
        saveNikitaViewV3();
        return actiononload.toJSON();
    }

    public String getInitLoadAndClear() {
        //System.out.println("getInitLoadAndClear");
        String s = actiononload.toJSON();
        saveNikitaViewV3();
        clearInitLoad();
        return s;
    }
    private boolean writelock = false;

    public void setWriteLock(boolean lock) {
        writelock = lock;
    }

    public boolean writeStreamHeader(String s, String val) {
        try {
            outlength = s.length();
            if (!writelock) {
                resp.addHeader(s, val);
            }
            isConsumed = true;
            return true;
        } catch (Exception e) {
            setLogicClose(true);
            return false;
        }
    }

    public boolean writeStream(byte[] b) {
        return writeStream(b, null);
    }

    public boolean writeStream(Nset n) {
        return writeStream(null, n);
    }

    private boolean writeStream(byte[] b, Nset n) {
        try {
            if (!writelock) {
                if (isSupportUser()) {
                    if (isSupportGzip() && NikitaConnection.getDefaultPropertySetting().getData("init").getData("nzip").toString().equals("true")) {
                        long i = System.currentTimeMillis();
                        if (n != null) {
                            getHttpServletResponse().setHeader("Content-Encoding", "gzip");
                            directwriteGzip(n);
                            infoSize = "/-1[" + (System.currentTimeMillis() - i) + "ms]";
                        } else {
                            getHttpServletResponse().setHeader("Content-Encoding", "gzip");
                            directwrite(Utility.toGzip(b));
                            infoSize = "/" + Utility.formatsizeKByte(b.length) + "[" + (System.currentTimeMillis() - i) + "ms]";
                        }
                    } else if (isSupportDeflate() && NikitaConnection.getDefaultPropertySetting().getData("init").getData("nzip").toString().equals("true")) {
                        long i = System.currentTimeMillis();
                        if (n != null) {
                            getHttpServletResponse().setHeader("Content-Encoding", "deflate");
                            directwriteDeflater(n);
                            infoSize = "/-1[" + (System.currentTimeMillis() - i) + "ms]";
                        } else {
                            getHttpServletResponse().setHeader("Content-Encoding", "deflate");
                            directwrite(Utility.toDeflater(b));
                            infoSize = "/" + Utility.formatsizeKByte(b.length) + "[" + (System.currentTimeMillis() - i) + "ms]";
                        }
                    } else {
                        if (n != null) {
                            directwrite(n);
                        } else {
                            directwrite(b);
                        }
                    }
                } else {
                    if (n != null) {
                        directwrite(n);
                    } else {
                        directwrite(b);
                    }
                }
            }
            isConsumed = true;
            return true;
        } catch (Exception e) {
            setLogicClose(true);
            return false;
        }
    }

    private boolean directwriteGzip(Nset n) {
        try {
            outlength = -1;//b.length;
            GZIPOutputStream gzipos = new GZIPOutputStream(resp.getOutputStream(), true);
            n.toJSON(gzipos); //resp.getOutputStream().write(b);
            gzipos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();;
            setLogicClose(true);
            return false;
        }
    }

    private boolean directwriteDeflater(Nset n) {
        try {
            outlength = -1;//b.length;
            DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(resp.getOutputStream(), true);
            n.toJSON(deflaterOutputStream); //resp.getOutputStream().write(b);
            deflaterOutputStream.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();;
            setLogicClose(true);
            return false;
        }
    }

    private boolean directwrite(Nset n) {
        try {
            outlength = -1;//b.length;
            n.toJSON(resp.getOutputStream()); //resp.getOutputStream().write(b);
            return true;
        } catch (Exception e) {
            setLogicClose(true);
            return false;
        }
    }

    private boolean directwrite(byte[] b) {
        try {
            outlength = b.length;
            resp.getOutputStream().write(b);
            return true;
        } catch (Exception e) {
            setLogicClose(true);
            return false;
        }
    }

    public boolean writeStream(String s) {
        return writeStream(s.getBytes());
    }

    public void flush() {
        try {
            resp.getOutputStream().flush();
        } catch (Exception e) {
        }
    }

    public void Consume() {
        isConsumed = true;
    }

    public boolean isConsumed() {
        return isConsumed;
    }

    public void setContent(NikitaForm w) {
        content = w;
    }

    public NikitaForm getContent() {
        return content;
    }

    public void setResult(Nset w) {
        setResult("", w);
    }

    public void sendBroadcast(String receiver, Nset w) {
        actionresult = w;
        Nset n = Nset.newObject();
        n.setData("code", "broadcast");
        n.setData("id", getContent().getFormCaller());
        n.setData("receiver", receiver);//?
        n.setData("data", actionresult.toJSON());
        n.setData("reqrescode", Nset.newObject().setData("responsecode", "BROADCAST").setData("requestcode", receiver).toJSON());
        addtoSteamData(n);

    }

    public void sendBroadcastGlobal(String receiver, Nset w) {

        Nset n = Nset.newObject();
        n.setData("code", "broadcast");
        n.setData("receiver", receiver);
        n.setData("message", w.toJSON());
        //addtoSteamData(n);  
        NikitaRz.sendtoBroadcast(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), receiver, w.toJSON());
    }

    public void sendBroadcastListener(String receiver, Nset w) {
        Nset n = Nset.newObject();
        n.setData("code", "listener");
        n.setData("receiver", receiver);
        n.setData("message", w.toJSON());
        //addtoSteamData(n);    
        NikitaRz.sendtoListener(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), receiver, w.toJSON());
    }

    public void sendBroadcastListenerRemove(String receiver) {
        Nset n = Nset.newObject();
        n.setData("code", "removelistener");
        n.setData("receiver", receiver);
        addtoSteamData(n);
    }

    public void sendBroadcastListenerAdd(String receiver) {
        Nset n = Nset.newObject();
        n.setData("code", "addlistener");
        n.setData("receiver", receiver);
        addtoSteamData(n);
    }

    public void setResult(String rescode, Nset w) {
        actionresult = w;
        Nset n = Nset.newObject();
        n.setData("code", "result");
        n.setData("id", getContent().getFormCaller());
        n.setData("mode", getContent().isFormMode() ? "form" : "windows");//?
        n.setData("data", actionresult.toJSON());
        n.setData("reqrescode", Nset.newObject().setData("responsecode", rescode).setData("requestcode", getContent().getRequestCode()).toJSON());
        if (!getContent().getFormCaller().equals("")) {
            if (!getContent().getFormCaller().equals(getContent().getJsId())) {//??
                addtoSteamData(n);
            }
        }

    }

    public void getLocalDate(String rescode) {
        Nset n = Nset.newObject();
        n.setData("code", "localdate");
        n.setData("id", getContent().getJsId());
        n.setData("reqrescode", Nset.newObject().setData("responsecode", "LOCALDATE").setData("requestcode", rescode).toJSON());
        addtoSteamData(n);
    }

    public void getLocation(String rescode) {
        Nset n = Nset.newObject();
        n.setData("code", "location");
        n.setData("id", getContent().getJsId());
        n.setData("reqrescode", Nset.newObject().setData("responsecode", "LOCATION").setData("requestcode", rescode).toJSON());
        addtoSteamData(n);
    }

    public void getDateTimeClient(String rescode) {
        Nset n = Nset.newObject();
        n.setData("code", "datetime");
        n.setData("id", getContent().getJsId());
        n.setData("reqrescode", Nset.newObject().setData("responsecode", "DATETIME").setData("requestcode", rescode).toJSON());
        addtoSteamData(n);
    }

    public void getSerialPort(String rescode, String com, String setting, String prefix, String mode, String data) {
        Nset n = Nset.newObject();
        n.setData("code", "serialport");
        n.setData("id", getContent().getJsId());

        n.setData("com", com);
        n.setData("setting", setting);
        n.setData("prefix", prefix);
        n.setData("mode", mode);
        n.setData("data", data);

        n.setData("reqrescode", Nset.newObject().setData("responsecode", "SERIALPORT").setData("requestcode", rescode).toJSON());
        addtoSteamData(n);
    }

    public void getPrintPDF(String rescode, String printer, String dataurl) {
        Nset n = Nset.newObject();
        n.setData("code", "printpdf");
        n.setData("id", getContent().getJsId());

        n.setData("printer", printer);
        n.setData("url", dataurl);
        n.setData("printer", printer);
        n.setData("mode", "");
        n.setData("data", "");

        n.setData("reqrescode", Nset.newObject().setData("responsecode", "PRINTER").setData("requestcode", rescode).toJSON());
        addtoSteamData(n);
    }

    public void setInterupt(boolean b) {
        setVirtualRegistered("@+INTERUPT", b ? "true" : "false");
    }

    public boolean isInterupted() {
        return getVirtualString("@+INTERUPT").equals("true");
    }

    private Nset getResult() {
        return actionresult;
    }

    private Nset getActionStream() {
        return actionstream;
    }

    public boolean isContainData() {
        return actionstream.getArraySize() >= 1;
    }

    public void clearInitLoad() {
        actiononload = Nset.newArray();//clear
    }

    public void clearContainData() {
        actionstream = Nset.newArray();//clear
        actiononload = Nset.newArray();//clear
        isConsumed = false;
    }

    public interface BeforeWriteListener {

        public void OnWrite(NikitaResponse resp);
    }
    private BeforeWriteListener beforeWriteListener;

    public void setBeforeWritelistener(BeforeWriteListener beforeWriteListener) {
        this.beforeWriteListener = beforeWriteListener;
    }

    public boolean isSupportUser() {
        String support = req.getParameter("nodatazip");
        if (support != null && support.contains("true")) {
            return false;
        }
        return true;
    }

    public boolean isSupportGzip() {
        //System.out.println(req.getHeader("accept-encoding"));
        String support = req.getHeader("accept-encoding");
        if (support != null && support.contains("gzip")) {
            return true;
        }
        return false;
    }

    public boolean isSupportDeflate() {
        //System.out.println(req.getHeader("accept-encoding"));
        String support = req.getHeader("accept-encoding");
        if (support != null && support.contains("deflate")) {
            return true;
        }
        return false;
    }

    public void write() {
        //System.out.println("write");
        if (this.beforeWriteListener != null) {
            this.beforeWriteListener.OnWrite(this);
        }
        //add 26.10.2016
        variableLocal();

        /*
        String v = actionstream.toJSON(); 
        if (actionstream.getArraySize()>=1) {        
            writeStream(v);            
        }
         */
        if (actionstream.getArraySize() >= 1) {
            writeStream(actionstream);
        }

        saveNikitaViewV3();
        nikitaViewV3s.removeAllElements();

        //???
        actionstream = Nset.newArray();//clear
        isConsumed = false;
    }

    public void saveNikitaViewV3() {
        for (int i = 0; i < nikitaViewV3s.size(); i++) {
            nikitaViewV3s.elementAt(i).save(getVirtualString("@+NIKITAID"));
        }
    }

    private String infoSize = "";

    public String getInfoSize() {
        return infoSize;
    }

    public void writeContent() {
        writeContent("");
    }

    public void writeContent(String id) {
        for (int i = 0; i < actionstream.getArraySize(); i++) {
            if (actionstream.getData(i).getData("code").toString().equals("refresh")) {

            }
        }
        //System.out.println("w:"+getContent().getFileNikita() );
        showContent(getContent());
        //requestfocus(id);
        write();
    }

    public boolean handleFileMultipart(NikitaRequest nikitaRequest) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(nikitaRequest.getHttpServletRequest());
        if (isMultipart) {
            /*
                int ifilemax = Utility.getInt(nikitaRequest.getParameter("nikitacontentlengthmax"));
                if (ifilemax>=13 && nikitaRequest.getHttpServletRequest().getContentLength()>ifilemax) {
                    Nset nres = Nset.newObject();                                
                    nres.setData("filesize",nikitaRequest.getHttpServletRequest().getContentLength());
                    nres.setData("error",   "ContentLength maximum");
                    
                    
                    nres.setData("contentlengthmax",  nikitaRequest.getParameter("nikitacontentlengthmax"));  
                    nres.setData("contentlength",  nikitaRequest.getHttpServletRequest().getContentLength()); 
                    nres.setData("fname",  nikitaRequest.getParameter("nikitacontentfname")); 
                    nres.setData("message",  nikitaRequest.getParameter("nikitacontentmessage")); 
                    
                    
                    try {
                        nikitaRequest.getHttpServletRequest().getInputStream().close();
                    } catch (Exception e) { }
                    
                    writeStream(nres.toJSON()); 
                   
                    return isMultipart;
                }
             
             */
            HttpServletRequest request = nikitaRequest.getHttpServletRequest();
            try {
                ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
                // parse requests
                List<FileItem> fileItems = upload.parseRequest(request);

                String fnName = "";
                int count = 0;
                Nset farrayname = Nset.newArray();
                String fsavename = System.currentTimeMillis() + "-" + getVirtualString("@+CONNECTIONCOUNT") + "-" + count;
                if (fileItems != null && fileItems.size() > 0) {
                    for (FileItem fileItem : fileItems) {
                        if (fileItem.isFormField()) {
                            if (fileItem.getFieldName().equals("fnserver")) {
                                fnName = fileItem.getString();
                            }
                        } else {
                            fsavename = System.currentTimeMillis() + "-" + Utility.MD5(fnName) + "-" + getVirtualString("@+CONNECTIONCOUNT") + "-" + count;
                            if (count == 0 && !fnName.equals("")) {
                                // fsavename=fnName;
                            }
                            count++;
                            File fileTo = new File(NikitaService.getDirTmp() + NikitaService.getFileSeparator() + fsavename + ".tmp");
                            fileItem.write(fileTo);

                            Nset nres = Nset.newObject();
                            nres.setData("filename", fileItem.getName());
                            nres.setData("filesize", fileTo.length());
                            nres.setData("fname", fnName);
                            nres.setData("fsavename", fsavename);
                            nres.setData("id", fileItem.getFieldName());

                            farrayname.addData(nres);
                        }
                    }
                    if (farrayname.getArraySize() >= 1) {
                        Nset nres = Nset.newObject();
                        nres.setData("filename", farrayname.getData(0).getData("filename").toString());
                        nres.setData("filesize", farrayname.getData(0).getData("filesize").toString());
                        nres.setData("fname", farrayname.getData(0).getData("fname").toString());
                        nres.setData("fsavename", farrayname.getData(0).getData("fsavename").toString());
                        nres.setData("id", farrayname.getData(0).getData("id").toString());

                        nres.setData("files", farrayname);
                        writeStream(nres.toJSON());
                    }
                }
            } catch (Exception ex) {
            }

        }
        return isMultipart;
    }

    public void setMobileActivityStream(String json) {
        resp.getNikitaMobile(req).setMobileActivityStream(json);
    }

    public void setMobileActivityStream() {
        //{"compname":["text","visible[0,1]","enable[0,1]"]}    
        Nset nset = Nset.newObject();
        if (getContent() != null) {
            Vector<Component> all = getContent().populateAllComponents();
            for (int i = 0; i < all.size(); i++) {
                if (!all.elementAt(i).getName().equals("")) {
                    Nset compset = Nset.newArray();
                    compset.addData(all.elementAt(i).getText());
                    compset.addData(all.elementAt(i).isVisible() ? "1" : "0");
                    compset.addData(all.elementAt(i).isEnable() ? "1" : "0");
                    nset.setData(all.elementAt(i).getName(), compset);
                }
            }
            nset.addData(Nset.newObject().setData(Utility.MD5("init").toLowerCase(Locale.ENGLISH), Nset.newObject().setData("caller", getContent().getCallerName()).setData("callerinstance", getContent().getCallerInstance())));

            resp.getNikitaMobile(req).saveMobileActivityStream(getContent().getName(), nset);
        }
    }

    public void clearMobileActivityStream() {
        resp.getNikitaMobile(req).clearMobileActivityStream();
    }

    public Nset getMobileActivityStream() {
        return resp.getNikitaMobile(req).currMobileActivity;
    }

}
