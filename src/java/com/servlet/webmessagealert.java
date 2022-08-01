/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.Component;
import com.nikita.generator.IAdapterListener;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Button;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Tablegrid;
import com.nikita.generator.ui.Textbox;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.HorizontalLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.nikita.generator.ui.layout.VerticalLayout;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.WebUtility;

/**
 *
 * @author rkrzmail
 */
public class webmessagealert extends NikitaServlet{
     /*parameter
    search
    mode
    data
    */
    NikitaConnection nikitaConnection ;
    int dbCore;
    String user ;
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        nikitaConnection =response.getConnection(NikitaConnection.LOGIC);      
        user = response.getVirtualString("@+SESSION-LOGON-USER");
        dbCore = WebUtility.getDBCore(nikitaConnection);  
        
        NikitaForm nf = new NikitaForm(this);
        nf.setText("Message Alert");        
        VerticalLayout horisontalLayout = new VerticalLayout();
        horisontalLayout.setStyle(new Style().setStyle("height", "225px").setStyle("overflow-y", "auto").setStyle("overflow-x", "hidden"));
        
        
        Nikitaset msg = nikitaConnection.Query("SELECT * FROM nikita_message WHERE username=? and status='inbox' and msgread='' ", response.getVirtualString("@+SESSION-LOGON-USER")  );
        StringBuffer msgid = new StringBuffer();
        for (int i = 0; i < msg.getRows(); i++) {
            if (msg.getText(i, "msgtype").equalsIgnoreCase("alert")||msg.getText(i, "msgtype").equalsIgnoreCase("1")) {
                msgid.append(msgid.toString().equals("")?"'":",'").append(msg.getText(i, "messageid")).append("'");
                
                HorizontalLayout hl = new HorizontalLayout();
                    Nikitaset user  = nikitaConnection.Query("SELECT * FROM sys_user WHERE username =?", msg.getText(i, "sender"));
                    
                    
                    VerticalLayout vl = new VerticalLayout();
                    vl.setStyle(new Style().setStyle("width", "48px").setStyle("height", "48px").setStyle("margin-right", "5px"));
                    
                    
                    Image 
                    image = new Image();
                    image.setId("img1");
                    image.setText((user.getText(0, "avatar").equals("")?"/static/img/generator.png":(user.getText(0, "avatar"))));
                     
                    image.setStyle(new Style().setStyle("width", "48px").setStyle("height", "48px").setStyle("margin-right", "5px"));
                    vl.addComponent(image);
                    
                    Label
                    label =new Label();
                    label.setText("<table><tr><td>" + Component.escapeHtml(user.getText(0, "name")) +"</td></tr></table>"  );
                    label.setStyle(new Style().setStyle("font-size", "10px").setStyle("color", "blue"));
                    //vl.addComponent(label);
                    
                    label =new Label();
                    label.setText("<table><tr><td>" + Component.escapeHtml(Utility.formatDate(Utility.getDate(msg.getText(i, "rdate")), "dd/MM/yyyy")  ) +"</td></tr></table>"  );
                    label.setStyle(new Style().setStyle("font-size", "10px").setStyle("color", "blue"));
                    //vl.addComponent(label);
                    
                    hl.addComponent(vl);
                    
                     
                VerticalLayout hl1 = new VerticalLayout();
                    label =new Label();
                    label.setText("<b>"+ Component.escapeHtml(msg.getText(i, "subject"))+"</b>" );
                    label.setStyle(new Style().setStyle("color", "blue"));
                    hl1.addComponent(label);
                    
                    label =new Label();
                    label.setText( Component.escapeHtml(msg.getText(i, "body")) );
                    hl1.addComponent(label);
                    
                    hl.addComponent(hl1);
                
                horisontalLayout.addComponent(hl);
            }
        }
                 
        
        Button btn = new Button();
        btn.setId("msg-done");
        btn.setText("Done");     
        btn.setTag(msgid.toString());
        btn.setOnClickListener(new Component.OnClickListener() {
            public void OnClick(NikitaRequest request, NikitaResponse response, Component component) {
                nikitaConnection.Query("UPDATE nikita_message SET msgread='1' WHERE username =? and messageid IN ("+component.getTag()+")", response.getVirtualString("@+SESSION-LOGON-USER") );
                response.closeform(response.getContent());
   
            }
        });
        btn.setStyle(new Style().setStyle("margin-left", "390px"));
        nf.addComponent(horisontalLayout);
        nf.addComponent(btn);
         
  
        
        response.setContent(nf);        
        nf.setOnActionResultListener(new NikitaForm.OnActionResultListener() {
            public void OnResult(NikitaRequest request, NikitaResponse response, Component component, String reqestcode, String responsecode, Nset result) {
                
            }
        });
        nf.setStyle(new Style().setStyle("n-maximizable", "false").setStyle("n-resizable", "false").setStyle("n-minimizeable", "false"));
        nf.getStyle().setStyle("width", "480").setStyle("height", "320").setStyle("n-closable", "false");        
    }
    
      
    
}
