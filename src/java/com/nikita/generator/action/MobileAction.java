/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.AppNikita;
import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.Style;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.nikita.generator.mobile.Generator;
import com.nikita.generator.ui.Document;
import com.nikita.generator.ui.FileUploder;
import com.rkrzmail.nikita.data.NikitaExpression;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Hashtable;

/**
 *
 * @author rkrzmail
 */
public class MobileAction  implements IAction{
    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaConnection nc = response.getConnection(NikitaConnection.MOBILE); 
        String code = response.getVirtualString(currdata.getData("code").toString());  
        
        if (code.equals("save")||code.equals("saveandsend")) {		
            //createifneed(nc);
            String reqcode = response.getVirtualString(currdata.getData("args").getData("param1").toString()).toLowerCase();
            String status = response.getVirtualString(currdata.getData("args").getData("param5").toString()).toLowerCase();
            String curform = response.getVirtualString(currdata.getData("args").getData("param6").toString()).toLowerCase();
            if (curform.startsWith("{")) {
                Nset n = Nset.readJSON(curform);
                curform=n.getData("formid").toString();
                if (!n.getData("formname").toString().equals("")) {
                    curform=n.getData("formname").toString();
                }
            }else if (curform.equalsIgnoreCase("")){
                curform = response.getContent().getName();
            }

            if (code.equals("saveandsend")) {
                    status= "1"; //lock
            }else if (status.equals("0")||status.equals("draft")) {
                    status= "0";
            }else if (status.equals("1")||status.equals("queue")||status.equals("finish")||status.equals("lock")) {
                    status= "1";
            }else if (status.equals("2")||status.equals("sent")) {
                    status= "2";
            }else if (Utility.isNumeric(status)) {
                    //none
            }else{
                status= "0";
            }			
            String activityid = response.getVirtualString("@+CORE-ACTIVITYID");

            if (response.getVirtualString("@+CORE-STATUS").equals("2") && code.equals("saveandsend")) {
                    if (Generator.getNikitaParameters(response).getData("INIT-MOBILE-ACTIVITY-STATUS-SENT-COUNT").toInteger()>=1) {

                    }else{
                            String message = Generator.getNikitaParameters(response).getData("INIT-MOBILE-ACTIVITY-STATUS-SENT-MESSAGE").toString() ;
                            currdata.getData("args").setData("message", message.equals("")?"Activity Has been Sent":message );
                            //new ShowAlertAction().OnAction(comp, currdata);
                            return false;
                    }
            }else if (response.getVirtualString("@+CORE-ACTIVITYID").equals("")) {				 
                    Generator.saveOpenForms(response);
                    activityid = AppNikita.getInstance().getDeviceId()+"."+Long.toHexString(System.currentTimeMillis()) +"."+Long.toHexString(System.nanoTime());
                    //nc.Query(convertinsert("mobileactivity",  "modifydate="+Utility.Now(), "activityid="+activityid, "orderid="+Generator.getVirtualString("@+CORE-ORDERID"), "threadid=",  "username=", "content=", "body="+Generator.getStreamFormsWithCurrentForm(curform).toJSON(), "read="+Generator.getVirtualString("@+CORE-VIEW"),  "status="+status, "createdate="+Utility.Now()));

                    Generator.setVirtual(response, "@+CORE-ACTIVITYID", activityid);
            }else{
                    Generator.saveOpenForms(response);
                    //update
                    //String sql = convertupdate("mobileactivity", "modifydate="+Utility.Now(),   "content=", "body="+Generator.getStreamFormsWithCurrentForm(curform).toJSON(), "read="+Generator.getVirtualString("@+CORE-VIEW"),  "status="+status ) +" WHERE activityid='"+ StringEscapeUtils.escapeSql(Generator.getVirtualString("@+CORE-ACTIVITYID")) +"' ";
                    //Nikitaset n = nc.Query(sql);
            }	
            if (code.equals("saveandsend")) { 
                 Hashtable<String, String> hashtable = AppNikita.getInstance().getArgsData(response);
                 hashtable.put("body", Generator.getStreamForms(response).toJSON());//stream data


                 hashtable.put("username", response.getVirtualString("@+CORE-USERNAME") );
                 hashtable.put("activityid", response.getVirtualString("@+CORE-ACTIVITYID"));
                 hashtable.put("orderid",response.getVirtualString("@+CORE-ORDERID"));
                 hashtable.put("threadid", response.getVirtualString("@+CORE-THREADID"));					
                 hashtable.put("status", response.getVirtualString("@+CORE-STATUS"));
                 hashtable.put("mobileactioncode", "save"); 
                 hashtable.put("mobileorigin", "web"); 
                 String x = NikitaInternet.getString(   NikitaInternet.postHttp( getMobileUrl()+ "/mobile.mactivity" , hashtable ) );

                 response.setVirtual(currdata.getData("args").getData("result").toString(), Nset.readJSON(x));	
            } else{
                 Hashtable<String, String> hashtable = AppNikita.getInstance().getArgsData(response);
                 hashtable.put("body", Generator.getStreamForms(response).toJSON());//stream data


                 hashtable.put("username", response.getVirtualString("@+CORE-USERNAME") );
                 hashtable.put("activityid", response.getVirtualString("@+CORE-ACTIVITYID"));
                 hashtable.put("orderid",response.getVirtualString("@+CORE-ORDERID"));
                 hashtable.put("threadid", response.getVirtualString("@+CORE-THREADID"));					
                 hashtable.put("status", response.getVirtualString("@+CORE-STATUS"));
                 hashtable.put("mobileactioncode", "save"); 
                 hashtable.put("mobileorigin", "web"); 
                 String x = NikitaInternet.getString(   NikitaInternet.postHttp( getMobileUrl()+ "/mobile.mactivitysave" , hashtable ) );
                 
                 response.setVirtual(currdata.getData("args").getData("result").toString(), Nset.readJSON(x));
            }        
        }else if (code.equals("triger")) {   
            String link = response.getVirtualString(currdata.getData("args").getData("param1").toString()).trim();
			
	    Generator.saveOpenForms(response);
            Hashtable<String, String> hashtable = AppNikita.getInstance().getArgsData(response);
            hashtable.put("body", Generator.getStreamForms(response).toJSON());//stream data
            hashtable.put("mobileactioncode", "triger");
            hashtable.put("mobileorigin", "web"); 
            String x = NikitaInternet.getString( NikitaInternet.postHttp( getMobileUrl()+ (link.equals("")?"/mobile.mtriger":"/"+link), hashtable ) );
			
            response.setVirtual(currdata.getData("args").getData("result").toString(), Nset.readJSON(x));	
		
        }else if (code.equals("openmobilegenerator")) {
            //load 
            /*
            param2=Data Draft;variable
            param3=Forms;variable
            */
            
            
            
        }else if (code.equals("setactivity")) {	
            String param1 = currdata.getData("args").getData("param1").toString();  //formname			 
            if (param1.equals("")||param1.startsWith("$")) {
                 response.setMobileActivityStream();
            }else{
                param1 = response.getVirtualString(currdata.getData("args").getData("param1").toString());  
                response.setMobileActivityStream(param1);
            }
        }else if (code.equals("getactivity")) {
            
            response.setVirtual(currdata.getData("args").getData("result").toString(), response.getMobileActivityStream().toJSON());
        }else if (code.equals("clearactivity")) {	
            response.clearMobileActivityStream();     
        }else if (code.equals("replaceactivity")) {	
	}else if (code.equals("mandatory")) {	
			//String forms = response.getVirtualString(data.getData("args").getData("param1").toString()).toLowerCase();
			//response.getComponent(key)
			StringBuffer sb = new StringBuffer();
			int comps = response.getContent().getComponentCount();
			for (int i = 0; i < comps; i++) {
				Component component = response.getContent().getComponent(i);
				if (component.isEnable() && component.isVisible()) {
                                        String x = component.getText();
                                        if (component instanceof FileUploder||component instanceof Document) {
                                            //dontrefresh
                                        }else if (component.isMandatory() && (component.getText().equals("") ||component.getText().equals("[]"))) {
						component.setStyle(Style.createStyle(component.getStyle()).setStyle("n-error", "true")  );
						if (!sb.toString().equals("") && !component.getLabel().equals("")) {
							sb.append("\r\n");
						}
						sb.append(component.getLabel());
                                                //response.refreshClassStyle(component, "n-error", true);
                                                //response.refreshComponent(component); 
                                                component.setMandatory(true);
                                                response.viewMandatoryError(component, true);
						continue;
					}else if  (component.isMandatory()){
                                            if (component.getValidation().matches("[($']+")) {                                                
                                                String result =  String.valueOf( response.getVirtual("@+NSON-CODE("+component.getValidation()+")" )  ) ;
                                                if (result.equalsIgnoreCase("true")||result.equalsIgnoreCase("null")||result.equalsIgnoreCase("")) {
                                                }else{
                                                    if (!sb.toString().equals("") && !component.getLabel().equals("")) {
							sb.append("\r\n");
                                                    }
                                                    sb.append(component.getLabel());
                                                    //response.refreshClassStyle(component, "n-error", true);
                                                    //response.refreshComponent(component);    
                                                    response.refreshComponent(component);    
                                                    component.setMandatory(true);
                                                    response.viewMandatoryError(component, true);
                                                    continue;
                                                }
                                            }
                                        }
                                     component.setMandatory(false);
                                    response.viewMandatoryError(component, false);
                                    //response.refreshComponent(component);    
				}
                                //response.refreshClassStyle(component, "n-error", false);
				Style style = Style.createStyle(component.getStyle());
				if (style.getInternalStyle().containsKey("n-error")) {
                                    //component.setStyle(style.setStyle("n-error", "false")  );
                                    //component.setStyle(style.addClass("error") );
                                    //response.refreshClassStyle(component, "n-error", true);
				}else{
                                    //response.refreshClassStyle(component, "n-error", false);
                                     //component.setEnable(false);
                                } 	
				//component.setStyle(Style.createStyle(component.getStyle()).setStyle("n-error", "true")  );
                                //response.refreshComponent(component);
			}				
			response.setVirtual(currdata.getData("args").getData("result").toString(), sb.toString());
			response.setVirtual(currdata.getData("args").getData("param9").toString(), sb.toString());
				 		    
        }
        
        return true;
    }
    
    public String getMobileUrl(){
        if (NikitaConnection.getDefaultPropertySetting().getData("init").containsKey("mobileactionurl")) {
             return NikitaConnection.getDefaultPropertySetting().getData("init").getData("mobileactionurl").toString();
        }
        return NikitaService.getBaseUrl();
    }
}
