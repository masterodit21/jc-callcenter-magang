/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.nikita.generator.action.IAction;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.expression.IExpression;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import java.io.InputStream;
 /**
 * created by 13k.mail@gmail.com
 */
public class Application extends NikitaService{
    protected static ComponentAdapter componentAdapter;
    protected static ActionAdapter actionAdapter;
    protected static ExpressionAdapter expressionAdapter;
    protected static RequestAdapter requestAdapter;
    
    public static String getVersion(){
        return "1.0.2";
    }
    
    public interface ComponentAdapter{
        public Component onCreateComponent(Component comp, Nikitaset ns, int row);
    }
    
    public interface ActionAdapter{
        public IAction onCreateAction(Nset action, NikitaRequest request, NikitaResponse response, NikitaLogic data);
    }
    
    public interface ExpressionAdapter{
        public IExpression onCreateExpression(Nset action, NikitaRequest request, NikitaResponse response, NikitaLogic data);
    }
    
    public interface RequestAdapter{
        public String onRequestAdapter(NikitaRequest request, NikitaResponse response, String req);
    }
    
    public static void setDefaultConnectionSetting(InputStream ini){
        NikitaConnection.setDefaultConnectionSetting(ini);
    }
    
    public static void setDefaultComponentAdapter(ComponentAdapter compAdapter){
         componentAdapter = compAdapter;
    }
    
    public static void setDefaultActionAdapter(ActionAdapter actAdapter){
        actionAdapter=actAdapter;
    }
    
    public static void setDefaultExpressionAdapter(ExpressionAdapter expAdapter){
         expressionAdapter=expAdapter;
    }
    
    public static void setDefaultRequestAdapter(RequestAdapter requestAdapter){
         requestAdapter=requestAdapter;
    }
}
