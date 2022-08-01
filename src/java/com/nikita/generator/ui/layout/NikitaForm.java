/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nikita.generator.ui.layout;

import com.nikita.generator.Component;
import com.nikita.generator.ComponentGroup;
import com.nikita.generator.ComponentManager;
import com.nikita.generator.NikitaEngine;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * created by 13k.mail@gmail.com
 */
//<meta name="viewport" content="width=device-width, initial-scale=1">
//<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0">
public class NikitaForm extends ComponentGroup {

    private String basetheme = "";
    private String icon = "";

    public boolean isActionDataState() {
        return true;
    }
    //new request
    private NikitaEngine engine;

    public NikitaEngine getNikitaEngine() {
        return engine;
    }

    public void setNikitaEngine(NikitaEngine engine) {
        this.engine = engine;
    }
    private NikitaRequest request;
    private NikitaResponse response;
    private NikitaLogic logic;

    public void setNikitaAction(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        this.request = request;
        this.response = response;
        this.logic = logic;
    }

    public void execLogicComponent(String compid) {
        if (this.request != null) {
            NikitaServlet.execLogicComponent(this, request, response, logic, compid);
        }
    }

    public void setBaseTheme(String s) {
        basetheme = s;
    }

    public NikitaForm(String id) {
        setId(id);
    }

    public NikitaForm(NikitaServlet id) {
        setId(id.getClass().getSimpleName());
    }

    //mob   
    public Vector<Component> populateAllComponents() {
        Vector<Component> components = new Vector<Component>();
        populateComponents(this, components);
        return components;
    }

    //mob
    private void populateComponents(ComponentGroup component, Vector<Component> components) {
        for (int i = 0; i < component.getComponentCount(); i++) {
            if (component.getComponent(i) instanceof ComponentGroup) {
                components.add(component.getComponent(i));
                populateComponents((ComponentGroup) component.getComponent(i), components);
            } else {
                components.add(component.getComponent(i));
            }
        }
    }

    //mob
    public String getCallerName() {
        if (caller.indexOf("-") >= 0) {
            return caller.substring(0, caller.indexOf("-"));
        }
        return caller;
    }

    //mob
    public String getCallerInstance() {
        return caller;
    }

    private String formid = "";

    public void setFormId(String formid) {
        this.formid = formid;//stop to hire
    }

    public String getFormId() {
        return formid;
    }

    public String getJsId() {
        return getId() + "-" + getInstanceId();//stop to hire
    }

    public void addComponent(Component component) {
        component.setForm(this);
        super.addComponent(component);
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    protected String getClickAction(String action) {
        return "";
    }

    public String getIcon() {
        return this.icon;
    }

    public String getView(NikitaViewV3 v3) {
        setId(getId());

        return super.getView(v3);
    }

    public String getFileNikita() {
        if (nikita.equals("")) {
            StringBuilder sb = new StringBuilder();
            Random randomGenerator = new Random();
            for (int idx = 1; idx <= 16; ++idx) {
                sb.append(randomGenerator.nextInt(100));
            }
            nikita = Utility.MD5(System.currentTimeMillis() + NikitaService.icount + sb.toString());
            return nikita;
        }
        return nikita;
    }

    private String iniload = "";

    public void setInitLoad(String init) {
        iniload = init;
    }
    private boolean statyonpage = false;

    public void setStayOnPage(boolean statyonpage) {
        this.statyonpage = statyonpage;
    }

    public String getViewForm(String caller, NikitaViewV3 v3) {
        setFormCaller(caller);
        return getViewForm(v3);
    }

    public String getViewTag(NikitaViewV3 v3) {
        StringBuilder sbuBuffer = new StringBuilder();
        sbuBuffer.append("<div  ").append(getMobileGeneratorFName()).append("  class=\"").append(getMobileGenerator()).append("\" ").append(this.engine != null && this.engine.isNv3() ? "nv3" : "").append("  style=\"display:none\" fnikita=\"").append(getFileNikita()).append("\" frmode=\"nikitaform\" id=\"").append(getJsId()).append("\" caller=\"").append(caller).append("\"  instance=\"").append(getInstanceId()).append("\"    nform=\"").append(getId()).append("\"  reqcod=\"").append(reqcod).append("\"  \">");
        sbuBuffer.append("</div>");
        return sbuBuffer.toString();
    }

    public String getViewTitle() {
        if (getStyle() != null && getStyle().getInternalStyle().containsKey("n-content-title")) {
            String ct = getStyle().getInternalStyle().getData("n-content-title").toString();
            if (ct.equalsIgnoreCase("true") || ct.equalsIgnoreCase("show")) {
                StringBuffer sbuBuffer = new StringBuffer();
                sbuBuffer.append("<div class=\"ui-nikita-content-title\"  >").append(escapeHtml(getText())).append("</div>");
                return sbuBuffer.toString();
            }
        }
        return "";
    }

    public String getMobileGeneratorFName() {
        String actionform = NikitaConnection.getDefaultPropertySetting().getData("init").getData("actionformdata").toString().trim();
        if (actionform.equals("*")) {
        } else if (actionform.contains(getName())) {
        } else if (actionform.contains(getId())) {
        } else {
            return "";
        }
        if (getInstanceId().contains(NikitaEngine.NFID_ARRAY_INDEX)) {
            if (!getName().contains(NikitaEngine.NFID_ARRAY_INDEX)) {
                setName(getName() + NikitaEngine.NFID_ARRAY_INDEX + NikitaEngine.getFormIndex(getInstanceId()));
            }
        }
        return " nformname=\"" + Component.escapeHtml(getName()) + "\" ";
    }

    public String getMobileGenerator() {
        String actionform = NikitaConnection.getDefaultPropertySetting().getData("init").getData("actionformdata").toString().trim();
        if (actionform.equals("*")) {
        } else if (actionform.contains(getName())) {
        } else if (actionform.contains(getId())) {
        } else {
            return "";
        }
        return "mobilenikitagenerator";
    }

    public String getViewForm(NikitaViewV3 v3) {
        v3.setData(this);
        v3.setFormNikita(getFileNikita());
        StringBuilder sbuBuffer = new StringBuilder();
        sbuBuffer.append("<div ").append(getMobileGeneratorFName()).append(" class=\"").append(getMobileGenerator()).append("\" ").append(this.engine != null && this.engine.isNv3() ? "nv3" : "").append("  ").append(getViewAttributPrefix("n-div-")).append(" style=\"").append("").append(getViewStylePrefix("n-div-")).append("\" fnikita=\"").append(getFileNikita()).append("\" frmode=\"nikitaform\" id=\"").append(getJsId()).append("\" caller=\"").append(caller).append("\"  instance=\"").append(getInstanceId()).append("\"    nform=\"").append(getId()).append("\"  reqcod=\"").append(reqcod).append("\" >");
        sbuBuffer.append(getView(v3));
        sbuBuffer.append("<div style=\"display:none\" class=\"nikitageneratorformactivate\">").append(Utility.encodeBase64(iniload)).append("</div>");

        sbuBuffer.append("</div>");
        //show form busy //style=\"height:32px;width:32px;position:absolute;left:50%;top:50%;\" 
        sbuBuffer.append("<div id=\"").append(getJsId() + "-busy").append("\" fid=\"").append(getJsId()).append("\"  frmid class=\"nikita-form-busy\" >");
        sbuBuffer.append("<div class=\"nikita-form-busy-bg\" style=\"background-color:white;\" ></div>");
        sbuBuffer.append("<img class=\"nikita-form-busy-img\"  src=\"static/lib/css/images/busy.gif\" alt=\"Loading..\"/>");
        sbuBuffer.append("<div id=\"").append(getJsId() + "-busy-pb").append("\" class=\"nikitaprogressbar nprogressbar\"  ><div id=\"").append(getJsId() + "-busy-pb-text").append("\"   class=\"nikitaprogressbar-label\"  ></div></div>");
        sbuBuffer.append("</div>");

        //show form wait
        sbuBuffer.append("<div id=\"").append(getJsId() + "-wait").append("\" class=\"nikita-form-wait\"  style=\"display:none;overflow:hidden;position:absolute;left:0px;width:100%;height:100%;bottom:0px;right:0px;top:0px;\" >");
        sbuBuffer.append("<img class=\"nikita-form-wait-img\"  src=\"static/lib/css/images/busy.gif\" alt=\"Loading..\"/>");
        sbuBuffer.append("</div>");
        return sbuBuffer.toString();
    }

    public String getViewHtml(NikitaViewV3 v3) {
        String nikitaTrheme = NikitaService.getBaseNikitaTheme();
        setBaseTheme(NikitaService.getBaseTheme());

        String theme = NikitaConnection.getDefaultPropertySetting().getData("init").getData("theme").toString();
        theme = theme.equals("") ? "lightness" : theme;

        if (viewListener != null) {
            NikitaResponse nikitaResponse = viewListener.beforeView(this);
            if (nikitaResponse != null) {
                setBaseTheme(nikitaResponse.getVirtualString("@+SESSION-THEME"));
            }
        }
        if (nikitaTrheme.equalsIgnoreCase("") || nikitaTrheme.equalsIgnoreCase("null")) {
            nikitaTrheme = basetheme;
        }
        v3.setData(this);
        v3.setFormNikita(getFileNikita());

        StringBuilder sbuBuffer = new StringBuilder();
        sbuBuffer.append("<!DOCTYPE HTML>");
        sbuBuffer.append("<html>");
        sbuBuffer.append("<head>");
        sbuBuffer.append("<meta ").append(this.engine != null && this.engine.isNv3() ? "nv3" : "").append(" http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\">");//<meta http-equiv="X-UA-Compatible" content="IE=8" >
        sbuBuffer.append("<meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0\">");

        sbuBuffer.append("<title id=\"nikitatitle\">").append(escapeHtml(getEngineMask() + getText())).append("</title>");
        if (icon.equals("") && getStyle() != null) {
            icon = getStyle().getInternalObject().getData("style").getData("n-icon").toString();
        }
        if (!icon.equals("")) {
            sbuBuffer.append("<link rel=\"shortcut icon\" href=\"").append(getIconUrl()).append("\" type=\"image/x-icon\" />");
        } else {
            sbuBuffer.append("<link rel=\"shortcut icon\" href=\"").append("static/img/icon.png").append("\" type=\"image/x-icon\" />");
        }

        if (getStyle() != null) {
            String s = getStyle().getViewAttr("n-theme", " ");
            if (s.length() >= 3) {
                basetheme = s.trim();
                if (basetheme.startsWith("=")) {
                    basetheme = basetheme.substring(1);
                }
                basetheme = basetheme.replace("\"", "");
                nikitaTrheme = basetheme;
            }
        }
        nikitaTrheme = nikitaTrheme.equals("") || nikitaTrheme.equals("null") ? theme : nikitaTrheme;
        //sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"static/lib/jquery-ui-1.10.4.custom.min.css\">");

        sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"static/lib/ui/").append(nikitaTrheme).append("/jquery-ui.min.css\">");

        String mapikey = NikitaConnection.getDefaultPropertySetting().getData("init").getData("n-map-api-key").toString();
        String nmpaapi = NikitaConnection.getDefaultPropertySetting().getData("init").getData("n-map-api").toString();
        if (getStyle() != null && getStyle().getInternalStyle().containsKey("n-map-api") || (nmpaapi.equalsIgnoreCase("true") || nmpaapi.equals(""))) {
            if (getStyle() != null && getStyle().getInternalStyle().containsKey("n-map-api-key")) {
                mapikey = getStyle().getInternalStyle().getData("n-map-api-key").toString();
            }
            if (mapikey.trim().equals("")) {
                sbuBuffer.append("<script src=\"https://maps.googleapis.com/maps/api/js?v=3.exp\"></script> ");
            } else {
                sbuBuffer.append("<script src=\"https://maps.googleapis.com/maps/api/js?v=3.exp&key=" + Utility.encodeURL(mapikey) + "\"></script> ");
            }
            //sbuBuffer.append("<script src=\"static/lib/maps/maps.api.v3.js\"></script> ");
        }

        //combolis
        //sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"static/lib/css/easy/easyui.css\"/>\n");            
        sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"static/lib/css/ui.dropdownchecklist.css\"/>\n");

        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/jquery-1.11.1.min.js\"></script>");
        //new datagrid
        //sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/jquery.easyui.min.js\"></script>" ); 
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/jquery-ui.min.js\"></script>");

        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/jquery.dialogextend.js\"></script>");

        //new datagrid resesi
        //sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/colresizable-1.5.min.js\"></script>" );         
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/nikita.js\"></script>");

        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/jquery.form.js\"></script>");
        //combolis       
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/ui.dropdownchecklist.js\"></script>\n");

        //collapsablelist
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/collapsiblelists.js\"></script>\n");

        //layout
        //sbuBuffer.append("<script type=\"text/javascript\" src=\"static/js/jquery.layout-latest.js\"></script>\n");
        //sbuBuffer.append("<script type=\"text/javascript\" src=\"static/js/lib/jlayout.flow.js\"></script>\n");         
        //sbuBuffer.append("<script type=\"text/javascript\" src=\"static/js/lib/jlayout.grid.js\"></script>\n"); 
        //sbuBuffer.append("<script type=\"text/javascript\" src=\"static/js/lib/jlayout.flexgrid.js\"></script>\n"); 
        //sbuBuffer.append("<script type=\"text/javascript\" src=\"static/js/lib/jquery.sizes.js\"></script>\n"); 
        //sbuBuffer.append("<script type=\"text/javascript\" src=\"static/js/lib/jlayout.border.js\"></script>\n"); 
        //sbuBuffer.append("<script type=\"text/javascript\" src=\"static/js/lib/jquery.jlayout.js\"></script>\n"); 
        //sbuBuffer.append("<script type=\"text/javascript\" charset=\"UTF-8\" src=\"static/media/js/jquery.datatables.js\"></script>"); 
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/contentmenu.js\"></script>");
        sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"static/lib/css/contentmenu.css\">");
        sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"static/lib/css/nikita.css\"/>");
        //zoom
        sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/wheelzoom.js\"></script>");
        //sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/css/easytree/ui.easytree.css\"></script>" );
        //sbuBuffer.append("<script type=\"text/javascript\" src=\"static/lib/jquery.easytree.min.js\"></script>" );

        //google Analitycs
        sbuBuffer.append("<script async src=\"https://www.googletagmanager.com/gtag/js?id=G-RY4HM2PJ8M\"></script>");
        sbuBuffer.append("  <script>\n"
                + "            window.dataLayer = window.dataLayer || [];\n"
                + "            function gtag() {\n"
                + "                dataLayer.push(arguments);\n"
                + "            }\n"
                + "            gtag('js', new Date());\n"
                + "\n"
                + "            gtag('config', 'G-RY4HM2PJ8M');\n"
                + "        </script>");
        
        
        /*
        if (getStyle()!=null) {
            String
            s = getStyle().getViewAttr("n-meta-", " ");
            if (s.length()>=3) {
                sbuBuffer.append("<meta  "+s +" >");
            }                       
            s = getStyle().getViewAttr("n-script-", " ");
            if (s.length()>=3) {                
                sbuBuffer.append("<script type=\"text/javascript\" charset=\"UTF-8\" "+s+"  ></script>"); 
            }
            s = getStyle().getViewAttr("n-js-", " ");
            if (s.length()>=3) {                
                sbuBuffer.append("<script type=\"text/javascript\" charset=\"UTF-8\" "+s+"  ></script>"); 
            }
            s = getStyle().getViewAttr("n-link-", " ");
            if (s.length()>=3) {                
                sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" "+s+"   />" );   
            }
            s = getStyle().getViewAttr("n-css-", " ");
            if (s.length()>=3) {                
                sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" "+s+"   />" );   
            }
            
            s = getStyle().getViewAttr("n-html-", " ");
            if (s.length()>=3) {
                String[] names = getStyle().getInternalAttr().getObjectKeys();
                for (int i = 0; i < names.length; i++) {
                    if (names[i].startsWith("n-html-")) {
                        sbuBuffer.append(getStyle().getInternalAttr().getData(names[i]).toString()); 
                    } 
                }
            }
        }
         */
        for (int i = -1; i < 13; i++) {
            String cnt = (i != -1) ? ("-" + i) : "";
            if (getStyle() != null && getStyle().getInternalAttr().containsKey("n-css" + cnt)) {
                String src = getStyle().getInternalAttr().getData("n-css" + cnt).toString().trim();
                if (src.startsWith("http:") || src.startsWith("https:")) {
                    sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"" + src + "\"/>");
                } else {
                    sbuBuffer.append("<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"" + src + "?fnamecss=" + "\"/>");
                }
            }
            if (getStyle() != null && getStyle().getInternalAttr().containsKey("n-js" + cnt)) {
                String src = getStyle().getInternalAttr().getData("n-js" + cnt).toString().trim();
                if (src.startsWith("http:") || src.startsWith("https:")) {
                    sbuBuffer.append("<script type=\"text/javascript\" charset=\"UTF-8\" src=\"" + src + "\"></script>");
                } else {
                    sbuBuffer.append("<script type=\"text/javascript\" charset=\"UTF-8\" src=\"" + src + "?fnamejs=" + "\"></script>");
                }
            }
        }

        sbuBuffer.append("<script id=\"0\" type=\"text/javascript\">");
        sbuBuffer.append(" $(document).ready(function() {");
        sbuBuffer.append("  globalapikey = '").append(mapikey).append("'; ");
        if (NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("requestcontentdata")) {
            sbuBuffer.append("  requestcontentdata = '").append(NikitaConnection.getDefaultPropertySetting().getData("init").getData("requestcontentdata").toString()).append("'; ");
        }

        sbuBuffer.append("  first(); ");
        int i = NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitatimerbroadcast").toInteger();
        if (i >= 1) {
            sbuBuffer.append(" timerbroadcast(").append(i * 1000).append("); ");
        }
        String s = NikitaConnection.getDefaultPropertySetting().getData("init").getData("chat").toString().trim();
        if (s.startsWith("ws")) {
            sbuBuffer.append(" websocket('").append(s).append("'); ");
        }
        s = NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz").toString().trim();
        if (s.startsWith("ws")) {
            try {

                s = s.replace("requesthost", NikitaService.getBaseRequestServer());
                s = s.replace("requestport", String.valueOf(NikitaService.getBaseRequestPort()));
                s = s.replace("requestcontext", NikitaService.getBaseContext());
                s = s.replace("@+BASEURL", NikitaService.getBaseUrl().replace("https://", "").replace("http://", ""));
                sbuBuffer.append(" NikitaRzBase('").append(s + "?appid=").append(URLEncoder.encode(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), "UTF-8")).append("'); ");

            } catch (Exception ex) {
            }
        }
        if (statyonpage || NikitaConnection.getDefaultPropertySetting().getData("init").getData("stayonpage").toString().trim().equals("true")) {
            sbuBuffer.append(" stayonpage(); ");
        }
        sbuBuffer.append("  init(); ");
        String ajaxerror = NikitaConnection.getDefaultPropertySetting().getData("init").getData("ajaxerror").toString().trim();
        if (ajaxerror.length() >= 1) {
            sbuBuffer.append("  ajaxerror='" + ajaxerror + "'; ");
        }
        if (readylistener != null) {
            sbuBuffer.append(" sendactionpost('").append(getId()).append("','ready','").append(getId()).append("','").append("ready").append("');");
        }
        sbuBuffer.append(" $( '#0' ).remove(); ");
        sbuBuffer.append(" $( document ).tooltip(); ");
        sbuBuffer.append(" });");
        sbuBuffer.append("</script>");

        //System.out.println(getStyle()!=null?getStyle().getViewStyle("n-body-", " "):"");
        sbuBuffer.append("</head>\n");
        sbuBuffer.append("<body style=\"").append(getStyle() != null ? getStyle().getViewStyle("n-body-", " ") : "").append("\" >");
        sbuBuffer.append("<div id=\"nikitageneratorversionnv3\" style=\"display:none\" theme=\"").append(Component.escapeHtml(nikitaTrheme)).append("\">").append(Component.escapeHtml(NikitaService.getBaseNikitaGeneratorData().toJSON())).append("</div>");
        sbuBuffer.append("<div  ").append(getMobileGeneratorFName()).append("  class=\"").append(getMobileGenerator()).append("\" ").append(getViewAttributPrefix("n-div-")).append(" style=\"").append("").append(getViewStylePrefix("n-div-")).append("\" fnikita=\"").append(getFileNikita()).append("\"  frmode=\"nikitawindows\" id=\"").append(getJsId()).append("\" caller=\"").append("").append("\"  instance=\"").append(getInstanceId()).append("\"    nform=\"").append(getId()).append("\"    reqcod=\"").append("").append("\" >");
        sbuBuffer.append(getView(v3));
        sbuBuffer.append("</div>");

        /*
        //not used
        sbuBuffer.append("<div id=\"nikita-busy\" fid=\"").append(getJsId()).append("\" frmid style=\"display:none;overflow:hidden;position:fixed;left:0px;bottom:0px;right:0px;top:0px;\" >");
                sbuBuffer.append("<img style=\"height:24px;width:24px;position:fixed;left:50%;top:50%;\" src=\"static/lib/css/images/busy.gif\" alt=\"Loading..\"/>");
        sbuBuffer.append("</div>");
        
        sbuBuffer.append("<div id=\"nikita-wait\" style=\"display:none;overflow:hidden;position:fixed;left:0px;bottom:0px;right:0px;top:0px;\" >");
                sbuBuffer.append("<img style=\"height:24px;width:24px;position:fixed;left:50%;top:50%;\" src=\"static/lib/css/images/busy.gif\" alt=\"Loading..\"/>");
        sbuBuffer.append("</div>");
         */
        //show form busy
        sbuBuffer.append("<div id=\"").append(getJsId() + "-busy").append("\" class=\"nikita-form-busy\" frmid fid=\"").append(getJsId()).append("\"  style=\"display:none;overflow:hidden;position:fixed;left:0px;bottom:0px;right:0px;top:0px;\" >");
        sbuBuffer.append("<div class=\"nikita-form-busy-bg\"   style=\"background-color:white;position:fixed;left:0px;bottom:0px;right:0px;top:0px;\" ></div>");
        sbuBuffer.append("<img style=\"height:32px;width:32px;position:fixed;left:50%;top:50%;\" src=\"static/lib/css/images/busy.gif\" alt=\"Loading..\"/>");
        sbuBuffer.append("<div id=\"").append(getJsId() + "-busy-pb").append("\" class=\"nikitaprogressbar nprogressbar\" ><div id=\"").append(getJsId() + "-busy-pb-text").append("\"  class=\"nikitaprogressbar-label\"  ></div></div>");
        sbuBuffer.append("</div>");
        //show form wait
        sbuBuffer.append("<div id=\"").append(getJsId() + "-wait").append("\" class=\"nikita-form-wait\"  style=\"display:none;overflow:hidden;position:fixed;left:0px;bottom:0px;right:0px;top:0px;\" >");
        sbuBuffer.append("<img style=\"height:32px;width:32px;position:fixed;left:50%;top:50%;\" src=\"static/lib/css/images/busy.gif\" alt=\"Loading..\"/>");
        sbuBuffer.append("</div>");

        //sbuBuffer.append("<input id=\"nativeopen\" style=\"display:none;\" type=\"text\"   >");
        sbuBuffer.append("<div style=\"display:none\" class=\"nikitageneratorformactivate\">").append(Utility.encodeBase64(iniload)).append("</div>");
        sbuBuffer.append("<div style=\"display:none\" class=\"nikitageneratorformbase\">").append(Utility.encodeBase64(NikitaService.getBaseContext())).append("</div>");

        sbuBuffer.append("</body>");
        sbuBuffer.append("</html>");

        int len = sbuBuffer.toString().length();
        int max = 1024 * (NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitaformbuffer").toInteger());//(12KB)
        if (len < max) {
            sbuBuffer.append("<!--");
            for (int x = len; x < max; x++) {
                sbuBuffer.append(" ");
            }
            sbuBuffer.append("-->");
        }
        return sbuBuffer.toString();
    }

    public String getEngineMask() {
        String mask = "";
        if (engine != null && NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("enginemask")) {
            String em = NikitaConnection.getDefaultPropertySetting().getData("init").getData("enginemask").toString().toLowerCase();
            if (engine.isNv3() && em.contains("nv3")) {
                mask = "[n] ";
            }
            if (!engine.isNv3() && (em.contains("db") || em.contains("dev"))) {
                mask = "[d] ";
            }
            if ((NikitaConnection.getDefaultPropertySetting().getData("init").getData("engine").toString().toLowerCase().contains("memory"))) {
                mask = "[m] ";
            }
        } else {
            String em = NikitaConnection.getDefaultPropertySetting().getData("init").getData("enginemask").toString().toLowerCase();
            if (em.contains("java") || em.contains("class") || em.contains("code") || em.contains("source")) {
                mask = "[c] ";
            }
        }
        return mask;
    }

    private String getIconUrl() {
        if (getIcon().startsWith("http")) {
            return getIcon();
        } else if (getIcon().startsWith("/res/asset")) {
            return NikitaService.getBaseContext() + getIcon();
        } else if (getIcon().startsWith("/res/resource")) {
            return NikitaService.getBaseContext() + getIcon();
        } else if (getIcon().startsWith("/static")) {
            return NikitaService.getBaseContext() + getIcon();
        } else if (getIcon().startsWith("img/")) {
            return NikitaService.getBaseContext() + "/static/" + getIcon();
        } else if (getIcon().startsWith("/@+CONTEXT/")) {
            return NikitaService.getBaseContext() + getIcon().substring(11);
        } else if (getIcon().startsWith("/context/")) {
            return NikitaService.getBaseContext() + getIcon().substring(9);
        } else {
            return getIcon();
        }
    }

    private String caller = "";
    private String fmmode = "";
    private String reqcod = "";
    private String nikita = "";

    public void setFormCaller(String caller) {
        this.caller = caller;
    }

    public void setRequestCode(String reqcode) {
        this.reqcod = reqcode;
    }

    public String getRequestCode() {
        return this.reqcod;
    }

    public String getFormCaller() {
        return this.caller;
    }

    public boolean isFormMode() {
        return fmmode.equals("nikitaform");
    }

    private boolean closeconsume = false;

    public void setBackConsume(boolean navback) {
        closeconsume = navback;
    }

    public boolean isBackConsume() {
        return closeconsume;
    }

    private boolean bforceRestore = false;

    public void setNoRestoreData(boolean b) {
        bforceRestore = b;
    }

    public boolean isNoRestoreData() {
        return bforceRestore;
    }

    public interface OnActionResultListener {

        public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result);
    }
    private OnActionResultListener listener;

    public void setOnActionResultListener(OnActionResultListener listener) {
        this.listener = listener;
    }

    public OnActionResultListener getOnActionResultListener() {
        return listener;
    }

    public interface OnLoadListener {

        public void OnLoad(NikitaRequest request, NikitaResponse response, Component component);
    }
    private OnLoadListener loadlistener;

    public void setOnLoadListener(OnLoadListener listener) {
        this.loadlistener = listener;
    }

    public OnLoadListener getOnLoadListener() {
        return loadlistener;
    }

    public interface OnBackListener {

        public void OnBack(NikitaRequest request, NikitaResponse response, Component component);
    }
    private OnBackListener onBackListener;

    public void setOnBackListener(OnBackListener listener) {
        this.onBackListener = listener;
    }

    public OnBackListener getOnBackListener() {
        return onBackListener;
    }

    public interface OnReadyListener {

        public void OnReady(NikitaRequest request, NikitaResponse response, Component component);
    }
    private OnReadyListener readylistener;

    public void setOnReadyListener(OnReadyListener listener) {
        this.readylistener = listener;
    }

    public OnReadyListener getOnReadyListener() {
        return readylistener;
    }

    public interface OnRestoreListener {

        public void OnRestore(NikitaRequest request, NikitaResponse response, Component component);
    }

    private OnRestoreListener restorelistener;

    public void setOnRestoreListener(OnRestoreListener listener) {
        this.restorelistener = listener;
    }

    public OnRestoreListener getOnRestoredListener() {
        return restorelistener;
    }

    public void restoreData(Nset data) {
        caller = data.getData("form-" + getId()).getData("fcall").toString();
        fmmode = data.getData("form-" + getId()).getData("fmode").toString();
        reqcod = data.getData("form-" + getId()).getData("fcode").toString();
        nikita = data.getData("form-" + getId()).getData("fnikita").toString();

        String instance = data.getData("form-" + getId()).getData("instance").toString();
        setInstanceId(instance);

        super.restoreData(data);
    }

    //new tag for somethink
    public void createNewGeneratorComponent(Component comp) {

    }

    public String getComponentTag() {

        return "";
    }

    public Vector<Component> autoCreatedComponent() {
        Vector<Component> components = new Vector<Component>();

        Nset tag = Nset.readJSON(getTag()).getData("autocomponent");
        String[] keys = tag.getObjectKeys();
        for (int i = 0; i < keys.length; i++) {
            String string = keys[i];
            Nset currCompData = tag.getData(keys[i]);
            Component component = ComponentManager.createComponent(currCompData.getData("id").toString(), getId());
            component.setName(currCompData.getData("name").toString());
            component.setLabel(currCompData.getData("label").toString());
            component.setText(currCompData.getData("text").toString());
            component.setTag(currCompData.getData("tag").toString());
            component.setHint(currCompData.getData("hint").toString());

            component.setVisible(currCompData.getData("visible").toString().equals("1") ? true : false);
            component.setEnable(currCompData.getData("enable").toString().equals("1") ? true : false);
            component.setData(currCompData.getData("data"));
            component.setStyle(Style.createStyle(currCompData.getData("style").toString()));

        }
        return components;
    }

}
