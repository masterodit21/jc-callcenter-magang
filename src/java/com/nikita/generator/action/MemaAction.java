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
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public class MemaAction implements IAction{

    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String code = response.getVirtualString(currdata.getData("code").toString());           
        Object coldef = response.getVirtual(currdata.getData("args").getData("param1").toString());
        Object master = response.getVirtual(currdata.getData("args").getData("param2").toString());
        
        try {
            if (code.equalsIgnoreCase("fieldrule")||code.equalsIgnoreCase("documentrule")) {
        	Nikitaset ns = new Nikitaset("");
        	if (master instanceof Nikitaset) {        		
                    ns = (Nikitaset) master;			
        	}else if (master instanceof Nset) {
                    ns = new Nikitaset( (Nset) master );
                }else{
                    ns =  new Nikitaset(String.valueOf(master));
                }      	
        	Nset coldefinisi = Nset.newArray();
			if (coldef instanceof Nset) {
				coldefinisi = (Nset) coldef;
			}else{
				coldefinisi = Nset.readJSON(String.valueOf(coldef));
			}
            
                for (int i = 0; i < ns.getRows(); i++) {  
                    boolean view = viewRuleLogic(response, coldefinisi, ns, i);
                    boolean mandatory = mandatoryRuleLogic(response, coldefinisi, ns, i);

                    String cname = ns.getText(i, "FIELD_NAME").trim();            	
                    //view
                    if (ns.getText(i, "FIELD_VIEW_ARRAY").trim().length()>=1) {
                        cname = ns.getText(i, "FIELD_VIEW_ARRAY").trim();
                    }                
                    String[] cnameArray = Utility.split(cname, ",");                
                    for (int j = 0; j < cnameArray.length; j++) {
                        cname = cnameArray[j];
                        if (!cname.startsWith("$")) {
                            cname = "$" + cname;
                        }
                        Component component =  response.getComponent(cname);
                        component.setVisible(view);
                        response.viewVisibility(component, view);
                    }
                    
                    //mandatory
                    cname = ns.getText(i, "FIELD_NAME").trim();  
                    if (ns.getText(i, "FIELD_MANDATORY_ARRAY").trim().length()>=1) {
                        cname = ns.getText(i, "FIELD_MANDATORY_ARRAY").trim();
                    }
                    cnameArray = Utility.split(cname, ",");                
                    for (int j = 0; j < cnameArray.length; j++) {
                        cname = cnameArray[j];
                        if (!cname.startsWith("$")) {
                            cname = "$" + cname;
                        }
                        Component component =  response.getComponent(cname);
                        component.setMandatory(mandatory);
                        //response.refreshComponent(component);
                        response.viewMandatory(component, mandatory);
                    }                 
                    //result per rows bye bye

                }
            }else if (code.equalsIgnoreCase("taskopencom")) {
                /*   */
                String mobstream = response.getVirtualString(currdata.getData("args").getData("param1").toString());
                //String form = response.getVirtualString(currdata.getData("args").getData("param2").toString());
                
            }else if (code.equalsIgnoreCase("tasksavecom")) {
                /*  */
                String mobstream = response.getVirtualString(currdata.getData("args").getData("param1").toString());
                String result = currdata.getData("args").getData("result").toString();
                Vector<Component>components = response.getContent().populateAllComponents();
                
                
            }else if (code.equalsIgnoreCase("save")) {
            }else if (code.equalsIgnoreCase("document")) {
                String docaction = response.getVirtualString(currdata.getData("args").getData("param1").toString()).trim();
                String comp = currdata.getData("args").getData("param2").toString();
                String currcompid = String.valueOf(response.getCurrCompId());
                 // response.submit(response.getComponent(comp), modal);              
                if (docaction.equalsIgnoreCase("upload")) {
                    Component component = response.getComponent("$#"+currcompid);
                    response.submit(component, "nikitadocument-"+component.getId()+"-upload"); //akan jadi response
                }else if (docaction.equalsIgnoreCase("receive")) {
                    String requestname = response.getVirtualString("@+REQUESTCODE");//submit
                    String responsename = response.getVirtualString("@+RESPONSECODE");//response [fname upload]
                    
                    Nset nv = Nset.newObject();
                    if (response.getVirtual("@+RESULT") instanceof  Nset) {
                        nv = (Nset)response.getVirtual("@+RESULT");
                    }
                    String fsavename = nv.getData("fsavename").toString();
                    String filename = nv.getData("filename").toString();
                    String fname  = nv.getData("fname").toString();
                    String id  = nv.getData("id").toString();
                    String docname = fsavename +"-"+filename;
                    
                    if (requestname.equalsIgnoreCase("submit") && responsename.equalsIgnoreCase(fname) && responsename.equalsIgnoreCase("nikitadocument-"+id+"-upload")) {
                        try {
                            String origin = NikitaService.getDirTmp() + NikitaService.getFileSeparator()+fsavename +".tmp";
                            String destination = NikitaConnection.getDefaultPropertySetting().getData("init").getData("document").toString()  + NikitaService.getFileSeparator() + docname;
                            Utility.copyFile(origin, destination);
                            new File(origin).delete();
                        } catch (Exception e) {  }
                        Component component = response.getComponent("$#"+id);
                        component.setText(docname);
                        response.refreshComponent(component);
                         
                    }
                    
                }else if (docaction.equalsIgnoreCase("docsavepoint")) {   
                    
                }else if (docaction.equalsIgnoreCase("docrollback")) {      
                    
                }                          
                
            }
        } catch (Exception e) { e.printStackTrace();}
            
        return true;
    }
	public boolean viewRuleLogic(NikitaResponse response, Nset coldefinisi, Nikitaset ns, int row){		
		String colwillexpand = ns.getText(row, "FIELD_INISIASI");		
		Nset colafterexpand = Nset.readJSON(colwillexpand);
		
		boolean view = colafterexpand.getSize()>=1;
		for (int col = 0; col < coldefinisi.getSize(); col++) {				
			if (colafterexpand.containsKey(coldefinisi.getData(col).toString())) {
                            Nset colValueNset = colafterexpand.getData(coldefinisi.getData(col).toString());
                            view=operator(response, colValueNset.getData("voperator").toString(), getTextComponentFromAll(response, coldefinisi.getData(col).toString()) , colValueNset.getData("vvalue").toString(), view);
                            if (view ) {
                                view = true;	        		
                            } else{
                                view =  false;
                                break;				
                            }
			}			     
        } 
		return view;
	}
	public boolean mandatoryRuleLogic(NikitaResponse response, Nset coldefinisi, Nikitaset ns, int row){		 
		String colwillexpand = ns.getText(row, "FIELD_INISIASI");
		Nset colafterexpand = Nset.readJSON(colwillexpand);
		
		boolean mandatory =  coldefinisi.getSize()>=1 ;//colafterexpand.getSize()>=1;
		for (int col = 0; col < coldefinisi.getSize(); col++) {		
			if (colafterexpand.containsKey(coldefinisi.getData(col).toString())) {
				Nset colValueNset = colafterexpand.getData(coldefinisi.getData(col).toString());
                                mandatory=operator(response, colValueNset.getData("moperator").toString(), getTextComponentFromAll(response, coldefinisi.getData(col).toString()), colValueNset.getData("mvalue").toString(), mandatory);
                                if (mandatory) {
                                        mandatory =  true;	        		
                                } else{
                                        mandatory =  false;	
                                    break;
				}  
			}			      
        } 
		return mandatory;
	}
	public boolean mandatoryRuleLogicOld(NikitaResponse response, Nset coldefinisi, Nikitaset ns, int row){		 
		String colwillexpand = ns.getText(row, "FIELD_INISIASI");
		Nset colafterexpand = Nset.readJSON(colwillexpand);
		
		boolean mandatory =  false ;//colafterexpand.getSize()>=1;
		for (int col = 0; col < coldefinisi.getSize(); col++) {		
			if (colafterexpand.containsKey(coldefinisi.getData(col).toString())) {
				Nset colValueNset = colafterexpand.getData(coldefinisi.getData(col).toString());			
	        	if (operator(response, colValueNset.getData("moperator").toString(), getTextComponentFromAll(response, coldefinisi.getData(col).toString()), colValueNset.getData("mvalue").toString(), false)) {
	        		mandatory =  true;
	        		break;
	        	} else{
	        		mandatory = mandatory || false;					
				}  
			}			      
        } 
		return mandatory;
	}
	public static String getTextComponentFromAll(NikitaResponse response, String name){
		name = name.startsWith("$")?name:"$"+name;
		name = name.length()<=1?" ":name;
		Component component = response.getComponent(name) ;
		if (component.getName().equalsIgnoreCase(name.substring(1))) {
                    return component.getText();
		}else if(response.getMobileGenerator()!=null){                        
                    String[] keys = response.getMobileGenerator().getObjectKeys();
                    for (int i = 0; i < keys.length; i++) {
                        Nset comps = response.getMobileGenerator().getData(keys[i]) ;//cops
                        if (comps.containsKey(name.substring(1))) {
                            Nset comp = comps.getData(name.substring(1));
                            if (comp.getData(4).toString().toLowerCase().contains("radiobox") ) {
                                return Nset.readJSON(comp.getData(0).toString()).getData(0).toString();
                            }else if (comp.getData(4).toString().toLowerCase().contains("checkbox") ||comp.getData(4).toString().toLowerCase().contains("combolist") ) {
                                return comp.getData(0).toString().length()>=2?comp.getData(0).toString().substring(1,comp.getData(0).toString().length()-1):"";
                            }
                            return comp.getData(0).toString();
                        }
                    }
		}
		return "";
	}
	public boolean operator(NikitaResponse response, String operator, String targetval, String value, boolean def){
		
        if (operator.equalsIgnoreCase("contain")||operator.equalsIgnoreCase("contains")) {
        	return targetval.contains(value);
        }else if (operator.equalsIgnoreCase("inarraycoma")||operator.equalsIgnoreCase("inarraycomma")) {
        	//Vector<String> v = Utility.splitVector(value, ",");
        	if (targetval.startsWith("[")) {
        		targetval=targetval.substring(1);
			}
        	if (targetval.endsWith("]")) {
        		targetval=targetval.substring(0,targetval.length()-1);
			}
        	return value.contains(targetval);
        }else if (operator.equalsIgnoreCase("inarrayjson")) {
        	Nset n = Nset.readJSON(value);
        	if (n.isNsetArray()) {
        		return n.containsValue(targetval);
			}else if (n.isNsetObject()) {
				return n.containsKey(targetval)||n.containsValue(targetval);
			}
        	return false;
        }else if (operator.equalsIgnoreCase("between")) {
        	String[] v = Utility.split (value.trim()+",", ",");
        	return Utility.getDouble(targetval)>=Utility.getDouble(v[0]) && Utility.getDouble(targetval)<=Utility.getDouble(v[1]);
        }else if (operator.equalsIgnoreCase(">")||operator.equalsIgnoreCase("greaterthan")) {
        	return Utility.getDouble(targetval)>Utility.getDouble(value);
        }else if (operator.equalsIgnoreCase(">=")||operator.equalsIgnoreCase("greaterthanorequalto")) {
        	return Utility.getDouble(targetval)>=Utility.getDouble(value);
        }else if (operator.equalsIgnoreCase("<")||operator.equalsIgnoreCase("lessthan")) {
        	return Utility.getDouble(targetval)<Utility.getDouble(value);
        }else if (operator.equalsIgnoreCase("<=")||operator.equalsIgnoreCase("lessthanorequalto")) {
        	return Utility.getDouble(targetval)<=Utility.getDouble(value);
        }else if (operator.equalsIgnoreCase("==")||operator.equalsIgnoreCase("equalsIgnoreCase")) {
        	return targetval.equalsIgnoreCase(value);
        }else if (operator.equalsIgnoreCase("==")||operator.equalsIgnoreCase("equal")) {
            if (Utility.isNumeric(targetval) && Utility.isNumeric(value)) {
            	return Utility.getDouble(targetval)==Utility.getDouble(value);
            }else{
                    return targetval.equals(value);
            }
        }else if (operator.equalsIgnoreCase("!=")||operator.equalsIgnoreCase("notequal")) {
            if (Utility.isNumeric(targetval) && Utility.isNumeric(value)) {
            	return !(Utility.getDouble(targetval)==Utility.getDouble(value));
            }else{
                    return !targetval.equals(value);
            }
        }else if (operator.equalsIgnoreCase("*")) {   
        	return true;
        }else if (operator.equalsIgnoreCase("hide")) {  
        	return false;
        }else if (operator.equalsIgnoreCase("startswith")) {  
        	return targetval.startsWith(value);	
        }else if (operator.equalsIgnoreCase("endswith")) {  
        	return targetval.endsWith(value);		
        }else if (operator.equalsIgnoreCase("startzip")) {
        	if (value.length()>=2) {
        		return targetval.startsWith(value.substring(0,2));
			}else{
				return targetval.startsWith(value);
			}
        	
        }else if (operator.equalsIgnoreCase("mandatoryarray")) {
        	Vector<String> v = Utility.splitVector(value, ",");
        	boolean bmand = false;
        	for (int i = 0; i < v.size(); i++) {
        		String text = getTextComponentFromAll(response, targetval);
                        if (text.equals("")) {	
                                return false;
                        }else{
                                bmand = true;
				}
			}
        	return  bmand;
        }
        
        return  def;
    }
}
