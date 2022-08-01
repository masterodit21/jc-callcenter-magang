package com.nikita.generator.action;

import com.nikita.generator.Component;
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.action.IAction;
import java.util.Vector;
 
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;

public class CloneAction implements IAction{
    
    @Override
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
            String code = response.getVirtualString(currdata.getData("code").toString());           
        Object compz = response.getVirtual(currdata.getData("args").getData("param1").toString());
        String suffix = response.getVirtualString(currdata.getData("args").getData("param2").toString());
        String suffix2 = response.getVirtualString(currdata.getData("args").getData("param3").toString());

        if (code.equalsIgnoreCase("saveformclone")) {
            Nset nforms = Nset.newArray();
                    if (compz instanceof Nset) {
                            nforms = (Nset) compz;
                    }else{
                            nforms = Nset.newArray().addData(String.valueOf(compz));
                    }
                    for (int i = 0; i < nforms.getSize(); i++) {
                            String sform = nforms.getData(i).toString() ;
                            String sformclone = sform + suffix + suffix2;
                            /*
                            NikitaControler nikitaControler = new NikitaControler();
                            nikitaControler.openStreamComponents( Generator.getStreamFormsWithCurrentForm(sform)  ) ;
                            AppNikita.getInstance().setNikitaControler(sformclone, nikitaControler);
                                    */
                    }       	
        }else if (code.equalsIgnoreCase("readformclone")) {
                    Nset nforms = Nset.newArray();
                    if (compz instanceof Nset) {
                            nforms = (Nset) compz;
                    }else{
                            nforms = Nset.newArray().addData(String.valueOf(compz));
                    }
                    for (int i = 0; i < nforms.getSize(); i++) {
                            String sform = nforms.getData(i).toString() ;
                            String sformclone = sform + suffix + suffix2;
                            /*
                            NikitaControler nikitaControlerclone = AppNikita.getInstance().getNikitaComponent(sformclone);
                            NikitaControler nikitaControler = AppNikita.getInstance().getNikitaComponent(sform);
                            if (nikitaControlerclone!=null ) {
                                    if (nikitaControler!=null) {
                                            nikitaControler.openStreamComponents( nikitaControlerclone.getStreamComponents() );
                                    }else{
                                            Generator.openStreamForm(sform, nikitaControlerclone.getStreamComponents());
                                    }
                            }
                            */
                    }

        }else if (code.equalsIgnoreCase("nikitasetexpandable")) {
                Nikitaset nikitaset ;
                if (compz instanceof Nikitaset) {
                        nikitaset = (Nikitaset)compz;
                        String colwillexp = response.getVirtualString(currdata.getData("args").getData("param2").toString());
                        Object coldefinisiexp = response.getVirtual(currdata.getData("args").getData("param3").toString());
                        Nset defcol;
                        if (coldefinisiexp instanceof Nset) {
                            defcol = (Nset)coldefinisiexp;
                            } else {
                                    defcol = Nset.readJSON(String.valueOf(coldefinisiexp));
                            }

                            if (defcol.isNsetObject()) {
                                    defcol = defcol.getData("header");//just get header no default value
                            }
                            Vector<String> newheaders = new Vector<String> ();
                            Vector<Vector<String>> newdata = new Vector<Vector<String>> ();

                            int icol = nikitaset.getDataAllHeader().indexOf(colwillexp);
                            if (icol>=0) {						 
                                    //before
                                    for (int col = 0; col < icol; col++) {							
                                            newheaders.addElement(nikitaset.getHeader(col));
                                    }
                                    //expand
                                    for (int j = 0; j < defcol.getSize(); j++) {
                                            newheaders.addElement(defcol.getData(j).toString());
                                    } 						
                                    //after
                                    for (int col = icol+1; col < nikitaset.getCols(); col++) {
                                            newheaders.addElement(nikitaset.getHeader(col));
                                    }	

                                    for (int i = 0; i < nikitaset.getRows(); i++) {
                                            Vector<String> newrows = new Vector<String>();
                                            //before
                                            for (int col = 0; col < icol; col++) {							
                                                    newrows.addElement(nikitaset.getText(i, col));
                                            }
                                            //expand
                                            Nset ndata = Nset.readJSON(nikitaset.getText(i, icol));
                                            for (int j = 0; j < defcol.getSize(); j++) {
                                                if (ndata.getData( defcol.getData(j).toString() ).isNset() ) {
                                                    newrows.addElement(ndata.getData( defcol.getData(j).toString() ).toJSON());
                                                }else{
                                                    newrows.addElement(ndata.getData( defcol.getData(j).toString() ).toString());
                                                }
                                                   
                                            } 						
                                            //after
                                            for (int col = icol+1; col < nikitaset.getCols(); col++) {
                                                    newrows.addElement(nikitaset.getText(i, col));
                                            }		
                                            newdata.addElement(newrows);
                                    }	
                                    nikitaset = new Nikitaset(newheaders,newdata, nikitaset.getError(), nikitaset.getInfo() );
                            }				
                            String result = currdata.getData("args").getData("result").toString();
                            response.setVirtual(result, nikitaset);
                }
        }else if (code.equalsIgnoreCase("nikitasetexpsave")) {
            Nset header ;
            Nset currow ;
            Nset defcol;
            if (compz instanceof Nikitaset) {
                header = new Nset( ((Nikitaset)compz).getDataAllHeader() );
            }else if (compz instanceof Nset) {
                header = (Nset)compz;
            }else{
                header = Nset.readJSON(String.valueOf(compz));
            }
            Object param2 = response.getVirtual(currdata.getData("args").getData("param2").toString());
             if (param2 instanceof Nikitaset) {
                currow = new Nset( ((Nikitaset)param2).getDataAllHeader() );
            }else if (param2 instanceof Nset) {
                currow = (Nset)param2;
            }else{
                currow = Nset.readJSON(String.valueOf(param2));
            }
            Object coldefinisiexp = response.getVirtual(currdata.getData("args").getData("param3").toString());
            if (coldefinisiexp instanceof Nset) {
                    defcol = (Nset)coldefinisiexp;
            } else {
                    defcol = Nset.readJSON(String.valueOf(coldefinisiexp));
            }

            if (defcol.isNsetObject()) {
                defcol = defcol.getData("header");//just get header no default value
            }
            
            Nset resNset = Nset.newObject();
            Vector<String> vhdr = new  Vector<String>();
            if (header.getInternalObject() instanceof Vector) {
                vhdr = (Vector<String>)header.getInternalObject();
            }
            for (int j = 0; j < defcol.getSize(); j++) {
                String col = defcol.getData(j).toString();
                resNset.setData(col, currow.getData(vhdr.indexOf(col)).toString());
            } 
            
            String result = currdata.getData("args").getData("result").toString();
            response.setVirtual(result, resNset);
            
        }
        return true;
    }
 
}
