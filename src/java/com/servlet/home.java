/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.NikitaViewV3;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Accordion;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.FileUploder;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Receiver;
import com.nikita.generator.ui.Webview;
import com.nikita.generator.ui.layout.BorderLayout;
import com.nikita.generator.ui.layout.FrameLayout;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;

/**
 *
 * @author user
 */
public class home extends NikitaServlet {

    StringBuffer sbMessage = new StringBuffer();
    NikitaConnection nikitaConnection;
    Nikitaset position;
    int imsg = 0;

    @Override
    public void OnCreate(final NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        //@+SESSION-LOGON-MODE
        position = nikitaConnection.Query("SELECT position FROM sys_user WHERE username = ? ", response.getVirtualString("@+SESSION-LOGON-USER"));

        if (response.getVirtualString("@+SESSION-THEME").equals("")) {
            response.setVirtual("@+SESSION-THEME", "south");
        }
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Nikita Generator");
        nf.setIcon("/static/img/generator.png");
        nf.setStayOnPage(true);

        final Nikitaset msg = nikitaConnection.Query("SELECT * FROM nikita_message WHERE username=? and status='inbox'   and msgread='' ", response.getVirtualString("@+SESSION-LOGON-USER"));

        HorizontalLayout hl = new HorizontalLayout();
        final Image notification = new Image();
        final Label l = new Label();
        l.setId("indicator");
        l.setStyle(new Style().setStyle("font-size", "10px").setStyle("color", "red"));

        hl.addComponent(l);

        notification.setId("imgnotif");
        notification.setText("/static/img/message_notif.png");
        notification.setHint("Notification");
        notification.setStyle(new Style().addClass("textsmart").setStyle("width", "32px").setStyle("height", "32px").setStyle("margin-right", "5px"));
        hl.addComponent(notification);
        hl.setStyle(new Style().setStyle("position", "absolute").setStyle("right", "0px").setStyle("padding", "5px").setStyle("font-weight", "bold"));

        notification.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webmessage", request, "", false, "all");
            }
        });

        l.setVisible(msg.getRows() >= 1);
        notification.setVisible(msg.getRows() >= 1);

        Receiver r = new Receiver();
        r.setId("ncm");
        r.setText("nikitacontrolreceiver");
        r.setVisible(false);
        r.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
            }
        });
        hl.addComponent(r);
        imsg = msg.getRows();
        for (int i = 0; i < msg.getRows(); i++) {
            if (msg.getText(i, "msgtype").equalsIgnoreCase("alert") || msg.getText(i, "msgtype").equalsIgnoreCase("1")) {
                sbMessage.append("  ");
                imsg--;
            }
        }
        l.setText("<b>" + imsg + "</b>");
        if (sbMessage.toString().length() >= 1) {
            nf.setOnLoadListener(new NikitaForm.OnLoadListener() {
                public void OnLoad(NikitaRequest request, NikitaResponse response, Component component) {
                    response.showform("webmessagealert", request, "", true);

                }
            });
            //response.showform("webmessagealert", request, "", true);
        }

        response.setBeforeWritelistener(new NikitaResponse.BeforeWriteListener() {
            public void OnWrite(NikitaResponse resp) {
                if (msg.getRows() >= 1) {
                    l.setVisible(true);
                    l.setText("<b>" + imsg + "</b>");
                    resp.refreshComponent(l);
                    notification.setVisible(true);
                    resp.refreshComponent(notification);
                }
                if (sbMessage.toString().length() >= 1) {
                    resp.showform("webmessagealert", request, "", true);
                }
            }
        });

        nf.setOnRestoreListener(new NikitaForm.OnRestoreListener() {
            public void OnRestore(NikitaRequest request, NikitaResponse response, Component component) {
                l.setText("<b>" + imsg + "</b>");
                response.refreshComponent(notification);
                response.refreshComponent(l);
            }
        });

        Label lbLabel = new Label();
        lbLabel.setId("realname");
        lbLabel.setText(response.getVirtualString("@+SESSION-LOGON-NAME"));
        hl.addComponent(lbLabel);

        Image image = new Image();
        image.setId("img1");
        image.setText((response.getVirtualString("@+SESSION-LOGON-AVATAR").equals("") ? "/static/img/generator.png" : response.getVirtualString("@+SESSION-LOGON-AVATAR")));
        image.setHint("Signout");
        image.setStyle(new Style().addClass("textsmart").setStyle("width", "32px").setStyle("height", "32px"));
        hl.addComponent(image);
        hl.setStyle(new Style().setStyle("position", "absolute").setStyle("right", "0px").setStyle("padding", "5px").setStyle("font-weight", "bold"));

        image.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showDialog("Signout", "Are you sure to signout ?", "signout", "No", "Yes");
            }
        });

        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                if (reqestcode.equals("signout") && responsecode.equals("button2")) {

                    //history timesheet
                    Nset data = Nset.newObject();
                    data.setData("username", response.getVirtualString("@+SESSION-LOGON-USER"));
                    data.setData("application", "Nikita Generator");
                    data.setData("activityname", "signout");
                    data.setData("activitytype", "signout");
                    data.setData("mode", "signout");
                    data.setData("additional", "");
                    Utility.SaveActivity(data, response);

                    response.setVirtual("@+SESSION-LOGON", "");
                    response.setVirtual("@+SESSION-LOGON-NAME", "");
                    response.setVirtual("@+SESSION-LOGON-USER", "");
                    response.setVirtual("@+SESSION-LOGON-MODE", "");
                    response.setVirtual("@+SESSION-LOGON-AVATAR", "");
                    response.setVirtual("@+SESSION-RELOG", "");
                    //response.openWindows("home", "");
                    response.reloadBrowser();
                    response.write();
                }
            }
        });

        nf.addComponent(hl);

        Accordion accordion = new Accordion();
        accordion.setId("acc1");

        hl = new HorizontalLayout();
        image = new Image();
        image.setId("img1");
        image.setText("/static/img/start.png");
        image.setStyle(new Style().setStyle("width", "40px").setStyle("height", "40px").setStyle("z-order", "1000").setStyle("background-color", "transparent"));
        hl.setStyle(new Style().setStyle("position", "fixed").setStyle("bottom", "2px").setStyle("left", "2px"));
        //image.getStyle().setAttr("onmouseout", "document.getElementById('home--acc1').style.display='none';");
        image.getStyle().setAttr("onmouseover", "document.getElementById('home--acc1').style.display='block';");
//        image.getStyle().addClass("ntop");
        hl.addComponent(image);

        image.setOnClickListener(new Component.OnClickListener() {
            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {

            }
        });

        HorizontalLayout hl3 = new HorizontalLayout();
        HorizontalLayout hl2 = new HorizontalLayout();
        image = new Image() {
            private String href = "";

            public String getView(NikitaViewV3 v3) {
                StringBuffer sb = new StringBuffer();
                sb.append("<a href=\"" + href + "\" target=\"_blank\">");
                sb.append(super.getView(v3));
                sb.append("</a>");
                return sb.toString();

            }

            public Image get(String href) {
                this.href = href;
                return this;
            }
        }.get("help_menu/");
        image.setId("img2");
        image.setText("/static/img/help.png");
        image.setStyle(new Style().setStyle("width", "20px").setStyle("height", "20px"));
        hl2.setStyle(new Style().setStyle("position", "fixed").setStyle("bottom", "2px").setStyle("right", "2px"));
        hl2.addComponent(image);

        hl3.addComponent(hl);
        hl3.addComponent(hl2);
        nf.addComponent(hl3);

        accordion.setStyle(new Style().setStyle("width", "200px"));
        VerticalLayout horisontalLayout = new VerticalLayout();
        accordion.getStyle().setAttr("onmouseout", "document.getElementById('home--acc1').style.display='none';");
        accordion.getStyle().setAttr("onmouseover", "document.getElementById('home--acc1').style.display='block';");
        accordion.getStyle().setStyle("position", "fixed").setStyle("bottom", "0px").setStyle("left", "1px").setStyle("padding-bottom", "40px").setStyle("display", "none");

        accordion.getStyle().addClass("ntop");
        nf.addComponent(accordion);

        horisontalLayout.setText("Notification");
        Button file = new Button();
        file.setId("home-regis");
        file.setText("Register Device");
        file.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(file);
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("nikitaregisterdevice", request, "", false, "all");
                response.write();
            }
        });

        file = new Button();
        file.setId("home-target");
        file.setText("Target Device");
        file.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(file);
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("nikitatargetdevice", request, "", false, "all");
                response.write();
            }
        });

        file = new Button();
        file.setId("home-chat");
        file.setText("Mobile Chat");
        file.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(file);
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webchat", request, "", false, "all");
                response.write();
            }
        });

        file = new Button();
        file.setId("home-msg");
        file.setText("Message");
        file.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(file);
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webmessage", request, "", false, "all");
                response.write();
            }
        });

        if (NikitaService.isModeCloud()) {
        } else {
            accordion.addComponent(horisontalLayout);
        }

        horisontalLayout = new VerticalLayout();

        horisontalLayout.setText("Connection");
        horisontalLayout.setId("nikitaaec");

        //System.err.println(Nset.readJSON("[{'text':'Menuses','child':[{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'}]},{'text':'Menuses','child':[{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'}]},{'text':'Menuses','child':[{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'}]}]", true).toJSON());
        //accordion.setData(Nset.readJSON("[{'text':'Menuses','child':[{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'}]},{'text':'Menuses','child':[{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'}]},{'text':'Menuses','child':[{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'}]}]", true));
        accordion.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showAlert("aaaaaaaa");
                response.write();
            }
        });

        Button btnComp = new Button();
        btnComp.setId("home-dashboard");
        btnComp.setText("Dashboard");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.showform("dashboard", request);
                response.openWindows("dashboard", "_BLANK");

                response.write();

            }
        });
        btnComp.setVisible(false);

        btnComp = new Button();
        btnComp.setId("home-connection");
        btnComp.setText("Connection");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showform("webconnection", request);
                response.write();

            }
        });

        file = new Button();
        file.setId("home-db");
        file.setText("DB Query");
        file.setStyle(new Style().setStyle("width", "100%").setAttr("n-div-actionwait", "1"));
        horisontalLayout.addComponent(file);
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("mode", "insert");
                request.setParameter("flag", "home");
                response.showform("webdatabase", request);
                response.write();

            }
        });

        file = new Button();
        file.setId("home-clipboard");
        file.setText("Theme");
        file.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(file);
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showform("theme", request);
                //response.write();
                //response.openWindows("theme", "");
                response.write();
            }
        });

        btnComp = new Button();
        btnComp.setId("home-filter");
        btnComp.setText("Filter");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        if (NikitaService.isModeCloud()) {
        } else {
            horisontalLayout.addComponent(btnComp);
        }
        btnComp.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.showform("module", request);
                ///response.openWindows("module", "home_content");
//                response.openWindows("module", "home_content",request);
                response.showform("webfilter", request, "", false);
                response.write();
            }
        });

        file = new Button();
        file.setId("home-forms");
        file.setText("Form All");
        file.setStyle(new Style().setStyle("width", "100%"));
        if (NikitaService.isModeCloud()) {
        } else {
            horisontalLayout.addComponent(file);
        }
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.openWindows("webform[all]", "home_content",request);
                response.showformGen("webform", request, "", false, "all");
                response.write();
            }
        });

        file = new Button();
        file.setId("home-formduplicate");
        file.setText("Form Dupl.");
        file.setStyle(new Style().setStyle("width", "100%"));
        if (NikitaService.isModeCloud()) {
        } else {
            horisontalLayout.addComponent(file);
        }
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.openWindows("webform[all]", "home_content",request);
                request.setParameter("code", "duplicate");
                response.showformGen("webform", request, "", false, "duplicate");
                response.write();
            }
        });

        btnComp = new Button();
        btnComp.setId("home-component");
        btnComp.setText("Component");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        //horisontalLayout.addComponent(btnComp);

        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.openWindows("webcomponent", "home_content",request);
                response.showformGen("webcomponent", request, "", false, "all");

                response.write();

            }
        });

        file = new Button();
        file.setId("home-logdb");
        file.setText("System LogDB");
        file.setStyle(new Style().setStyle("width", "100%"));
        if (NikitaService.isModeCloud()) {
        } else {
            horisontalLayout.addComponent(file);
        }
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("weblogdb", request, "", false, "all");
                response.write();
            }
        });

        accordion.addComponent(horisontalLayout);

        horisontalLayout = new VerticalLayout();
        horisontalLayout.setText("Export & Import");

        file = new Button();
        file.setId("home-export");
        file.setText("Export Form");
        file.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(file);
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webexport", request, "", false, "all");
                response.write();
            }
        });

        file = new Button();
        file.setId("home-export-res");
        file.setText("Export Resources");
        file.setStyle(new Style().setStyle("width", "100%"));
        if (NikitaService.isModeCloud()) {
        } else {
            horisontalLayout.addComponent(file);
        }
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webexportresource", request, "", false, "all");
                response.write();
            }
        });

        file = new Button();
        file.setId("home-export-setting");
        file.setText("Export Generator All");
        file.setStyle(new Style().setStyle("width", "100%"));
        if (NikitaService.isModeCloud()) {
        } else {
            horisontalLayout.addComponent(file);
        }
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webexportsetting", request, "", false, "all");
                response.write();
            }
        });

        file = new Button();
        file.setId("home-import");
        file.setText("Import");
        file.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(file);
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webimportgo", request, "", false, "all");
                response.write();
            }
        });

        file = new Button();
        file.setId("home-importexcel");
        file.setText("Import Excel");
        file.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(file);
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webimportgoexcel", request, "", false, "all");
                response.write();
            }
        });
        file.setVisible(false);

        accordion.addComponent(horisontalLayout);

        horisontalLayout = new VerticalLayout();
        horisontalLayout.setText("Build Generator");

        boolean buildAccess = Utility.getInt(position.getText(0, 0)) >= 2;//check admin

        btnComp = new Button();
        btnComp.setId("home-master");
        btnComp.setText("Generate Master");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        if (buildAccess) {
            horisontalLayout.addComponent(btnComp);
        }

        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webgeneratemaster", request, "", false, "");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("home-finder");
        btnComp.setText("Generate Finder");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        if (buildAccess) {
            horisontalLayout.addComponent(btnComp);
        }
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webgeneratefinder", request, "", false, "");
                response.write();

            }
        });

        file = new Button();
        file.setId("home-compile");
        file.setText("Build Application");
        file.setStyle(new Style().setStyle("width", "100%"));
        if (NikitaService.isModeCloud()) {
        } else {
            horisontalLayout.addComponent(file);
        }
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webbuild", request, "", false, "");
                response.write();
            }
        });

        file = new Button();
        file.setId("home-publish");
        file.setText("Publish Nv3");
        file.setStyle(new Style().setStyle("width", "100%"));
        if (NikitaService.isModeCloud()) {
        } else {
            horisontalLayout.addComponent(file);
        }
        file.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webpublishnv3", request, "", false, "");
                response.write();
            }
        });

        if (NikitaService.isModeCloud()) {
            if (buildAccess) {
                accordion.addComponent(horisontalLayout);
            }
        } else {
            accordion.addComponent(horisontalLayout);
        }

        horisontalLayout = new VerticalLayout();
        horisontalLayout.setText("Mobile Legacy");
        //accordion.addComponent(horisontalLayout);

        btnComp = new Button();
        btnComp.setId("mobile-app");
        btnComp.setText("Application");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.showform("webform", request);
                // response.openWindows("webform", "home_content");

                response.showformGen("172", request, "", false);
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("mobile-model");
        btnComp.setText("Model");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.showform("webform", request);
                // response.openWindows("webform", "home_content");

                response.showformGen("173", request, "", false, "v1");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("mobile-form");
        btnComp.setText("Form");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.showform("webform", request);
                // response.openWindows("webform", "home_content");

                response.showformGen("188", request, "", false, "v1");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("mobile-mf");
        btnComp.setText("Model Form");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.showform("webform", request);
                // response.openWindows("webform", "home_content");

                response.showformGen("175", request, "", false, "v1");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("mobile-comp");
        btnComp.setText("Component");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.showform("webform", request);
                // response.openWindows("webform", "home_content");

                response.showformGen("176", request, "", false, "v1");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("mobile-route");
        btnComp.setText("Route");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.showform("webform", request);
                // response.openWindows("webform", "home_content");

                response.showformGen("178", request, "", false, "v1");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("mobile-param");
        btnComp.setText("Parameter");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.showform("webform", request);
                // response.openWindows("webform", "home_content");

                response.showformGen("182", request, "", false, "v1");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("mobile-version");
        btnComp.setText("Version");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.showform("webform", request);
                // response.openWindows("webform", "home_content");

                response.showformGen("195", request, "", false, "v1");
                response.write();

            }
        });

        horisontalLayout = new VerticalLayout();
        horisontalLayout.setText("Mobile Generator");
        accordion.addComponent(horisontalLayout);

        btnComp = new Button();
        btnComp.setId("home-module");
        btnComp.setText("Module");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        if (NikitaService.isModeCloud()) {

        } else {
            horisontalLayout.addComponent(btnComp);
        }
        btnComp.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.showform("module", request);
                ///response.openWindows("module", "home_content");
//                response.openWindows("module", "home_content",request);
                response.showform("webmodule", request, "", false);
                response.write();
            }
        });

        /*
        btnComp = new Button();
        btnComp.setId("home-model");
        btnComp.setText("ModuleForm");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {
            
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                
                response.showform("webmoduleform", request);
                response.write();
                
            }
        });
         */
        btnComp = new Button();
        btnComp.setId("home-mobile-form");
        btnComp.setText("Form");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);

        btnComp.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("type", "mobileform");
                response.showformGen("webform", request, "", false, "mobile");
                response.write();
            }
        });
        btnComp = new Button();
        btnComp.setId("home-mobile-storage");
        btnComp.setText("Asset");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {

                response.showform("webasset", request);
                response.write();

            }
        });
        btnComp.setVisible(false);

        btnComp = new Button();
        btnComp.setId("home-mobile-param");
        btnComp.setText("Parameter");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {

                request.setParameter("type", "mobileform");
                response.showformGen("webparameter", request, "", false, "mobile");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("home-mobileconn");
        btnComp.setText("Mobile Connection");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showform("webmobileconn", request, "", false);
                response.write();
            }
        });

        horisontalLayout = new VerticalLayout();
        horisontalLayout.setText("Web Generator");
        horisontalLayout.setId("nikitaaec");

        //accordion.setData(Nset.readJSON("[{'text':'Menuses','child':[{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'}]},{'text':'Menuses','child':[{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'}]},{'text':'Menuses','child':[{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'},{'id':'1','text':'View'}]}]", true));
        accordion.setOnClickListener(new Component.OnClickListener() {

            @Override
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showAlert("aaaaaaaa");
                response.write();
            }
        });

        btnComp = new Button();
        btnComp.setId("home-form");
        btnComp.setText("Form");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.showform("webform", request);
                // response.openWindows("webform", "home_content");
                request.setParameter("type", "webform");
                response.showformGen("webform", request, "", false, "web");
                response.write();

            }
        });

        /*
        
        btnComp = new Button();
        btnComp.setId("home-logic");
        btnComp.setText("Logic");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {
            
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                //response.showform("weblogiclist", request);
              //   response.openWindows("weblogiclist", "home_content");
                 response.openWindows("weblogiclist", "home_content",request);
                response.write();
                
            }
        });
         */
        btnComp = new Button();
        btnComp.setId("home-resources");
        btnComp.setText("Resources");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showform("webresource", request);
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("home-parameter");
        btnComp.setText("Parameter");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        if (NikitaService.isModeCloud()) {

        } else {
            horisontalLayout.addComponent(btnComp);
        }

        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {

                request.setParameter("type", "webform");
                response.showformGen("webparameter", request, "", false, "web");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("home-setring");
        btnComp.setText("Language String");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        if (NikitaService.isModeCloud()) {

        } else {
            horisontalLayout.addComponent(btnComp);
        }
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {

                request.setParameter("type", "webstring");
                response.showformGen("webstring", request, "", false, "web");
                response.write();

            }
        });

        HorizontalLayout top = new HorizontalLayout();
        Image img = new Image();
        img.setText("img/homecenter.gif");

        top.addComponent(img);
        Style style = new Style();
        style.setStyle("background-image", "url(static/img/homecenter.gif)");;
        style.setStyle("background-repeat", "repeat-x");
        style.setStyle("width", "100%");
        style.setStyle("height", "80px");
        style.setStyle("overflow", "hidden");
        top.setStyle(style);

        BorderLayout borderLayout = new BorderLayout();

        VerticalLayout left = new VerticalLayout();
        left.setId("home-left");
        style = new Style();

        style.setStyle("width", "200px");
        //style.setStyle("height", "600px"); 

        left.setStyle(style);
        accordion.addComponent(horisontalLayout);

        horisontalLayout = new VerticalLayout();
        horisontalLayout.setText("Task Generator");
        accordion.addComponent(horisontalLayout);

        btnComp = new Button();
        btnComp.setId("home-service-function");
        btnComp.setText("Link Function");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
//        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("type", "link");
                response.showformGen("webform", request, "", false, "function");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("home-service-link");
        btnComp.setText("Link Form");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("type", "link");
                response.showformGen("webform", request, "", false, "link");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("home-service-service");
        btnComp.setText("Link Service");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
//        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("type", "link");
                response.showformGen("webform", request, "", false, "service");
                response.write();

            }
        });

        horisontalLayout = new VerticalLayout();
        horisontalLayout.setText("Sch. Generator");
        if (NikitaService.isModeCloud()) {
        } else {
            accordion.addComponent(horisontalLayout);
        }

        btnComp = new Button();
        btnComp.setId("home-sch-manager");
        btnComp.setText("Sch. Management");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webschmanagement", request, "", false, "service");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("home-sch-his");
        btnComp.setText("Sch. History");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webschhistory", request, "", false, "service");
                response.write();

            }
        });

        horisontalLayout = new VerticalLayout();
        horisontalLayout.setText("Nikita Workflow");
        btnComp = new Button();
        btnComp.setId("workflowgroup");
        btnComp.setText("Workflow");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webworkflowgroup", request, "", false, "service");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("workflow");
        btnComp.setText("Design");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                response.showformGen("webworkflow", request, "", false, "service");
                response.write();

            }
        });
        if (NikitaService.isModeCloud()) {
        } else {
            accordion.addComponent(horisontalLayout);
        }

        horisontalLayout = new VerticalLayout();
        horisontalLayout.setText("User Management");
        accordion.addComponent(horisontalLayout);

        btnComp = new Button();
        btnComp.setId("home-user-view");
        btnComp.setText("Users");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        horisontalLayout.addComponent(btnComp);
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("position", "user");
                response.showformGen("webuser", request, "", false, "link");
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("home-unlock-user");
        btnComp.setText("Unlock Form");
        btnComp.setStyle(new Style().setStyle("width", "100%"));
        if (NikitaService.isModeCloud()) {
        } else {
            horisontalLayout.addComponent(btnComp);
        }
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                request.setParameter("user", response.getVirtualString("@+SESSION-LOGON-USER"));
                response.showform("webunlockform", request, "", false);
                response.write();

            }
        });

        btnComp = new Button();
        btnComp.setId("home-user-access");
        btnComp.setText("User Access Form");
        if (Utility.getInt(position.getText(0, 0)) > 2) {
            btnComp.setStyle(new Style().setStyle("width", "100%"));
            btnComp.setVisible(true);
        } else {
            btnComp.setStyle(new Style().setStyle("opacity", "0.2").setStyle("width", "100%"));
            btnComp.setVisible(false);
        }
        if (NikitaService.isModeCloud()) {
        } else {
            horisontalLayout.addComponent(btnComp);
        }
        btnComp.setOnClickListener(new Component.OnClickListener() {

            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                if (Utility.getInt(position.getText(0, 0)) > 2) {
                    request.setParameter("user", response.getVirtualString("@+SESSION-LOGON-USER"));
                    response.showform("webaccessform", request, "", false);
                    response.write();
                } else {
                    response.showDialogResult("Warning", "You don't have access", "warning", null, "", "");
                }

            }
        });

        //borderLayout.setComponentTop(top);
        //borderLayout.setComponentLeft(accordion);        
        Webview webview = new Webview();
        webview.setId("home_content");
        //webview.setText("webcomponent");
        style = new Style();
        style.setStyle("width", "100%");
        style.setStyle("height", "100%");
        style.setStyle("overflow-y", "hidden");

        webview.setStyle(style);
        borderLayout.setComponentCenter(webview);

        nf.setStyle(new Style().setStyle("n-body-background-image", "url(static/img/sailormoon.jpg)"));
        // nf.getStyle().setStyle("n-body-background-repeat", "no-repeat");
        nf.getStyle().setStyle("n-body-background-position", "center");
        //nf.getStyle().setStyle("n-body-opacity", "0.4");

        nf.addComponent(borderLayout);
        response.setContent(nf);
    }

    public void OnActionA(NikitaRequest request, NikitaResponse response, NikitaLogic logic, String component, String action) {
        if (!action.equals("") && sbMessage.toString().length() >= 1) {
            response.showform("webmessagealert", request, "", true);
        } else {

            super.OnAction(request, response, logic, component, action); //To change body of generated methods, choose Tools | Templates.
        }
    }

}
