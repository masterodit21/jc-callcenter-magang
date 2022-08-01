package com.rkrzmail.nikita.data;

import com.rkrzmail.nikita.data.Nset;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
 

public class NikitaExpression  { 
    
    private static final MathEval.FunctionHandler unFunctionHandler = new MathEval.FunctionHandler() {
        public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
            if (fncnam.equalsIgnoreCase("ucase")) {
                return String.valueOf(fncargs.next()).toUpperCase();
            }else if (fncnam.equalsIgnoreCase("lcase")) {
                return String.valueOf(fncargs.next()).toLowerCase();
            }else if (fncnam.equalsIgnoreCase("concat")||fncnam.equalsIgnoreCase("append")) {
                StringBuffer sb = new StringBuffer();
                while (fncargs.hasNext()) {                             
                    sb.append(String.valueOf(fncargs.next()));
                }                        
                return sb.toString();
            }else if (fncnam.equalsIgnoreCase("length")||fncnam.equalsIgnoreCase("len")) {
                 return String.valueOf(fncargs.next()).length();
            }else if (fncnam.equalsIgnoreCase("substring")||fncnam.equalsIgnoreCase("mid")) {
                    ArrayList<String>  args  = new ArrayList<String>();
                    while (fncargs.hasNext()) {   args.add(String.valueOf(fncargs.next())); }  
                    try {
                        if (args.size()==2) {
                            return String.valueOf(args.get(0)).substring(Integer.valueOf(args.get(1)));
                        }else if (args.size()==3) {
                            return String.valueOf(args.get(0)).substring(Integer.valueOf(args.get(1)).intValue(),Integer.valueOf(args.get(2)).intValue()  );
                        }                        
                    } catch (Exception e) { }                    
                    return "";
            }else if (fncnam.equalsIgnoreCase("contain")) {
                return String.valueOf(fncargs.next()).contains(String.valueOf(fncargs.next()));   
            }else if (fncnam.equalsIgnoreCase("trim")) {
                return String.valueOf(fncargs.next()).trim();
            }else if (fncnam.equalsIgnoreCase("regex")||fncnam.equalsIgnoreCase("matches")) {
                return String.valueOf(fncargs.next()).matches(String.valueOf(fncargs.next()));
            }else if (fncnam.equalsIgnoreCase("now")) { 
                Calendar calendar = Calendar.getInstance();
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
            }else if (fncnam.equalsIgnoreCase("dateformat")) { 
                ArrayList<String>  args  = new ArrayList<String>();
                while (fncargs.hasNext()) {   args.add(String.valueOf(fncargs.next())); }  
                //date youdate, todate
                try {
                    String str = String.valueOf(args.get(0));
                    if (isLongIntegerNumber(str)) {
                        return new SimpleDateFormat(String.valueOf(args.get(2))).format(new Date(Long.parseLong(str)));
                    }else{
                        SimpleDateFormat  simpleDateFormat = new SimpleDateFormat(String.valueOf(args.get(1)));
                        Date date= simpleDateFormat.parse(String.valueOf(args.get(0)));
                        return new SimpleDateFormat(String.valueOf(args.get(2))).format(date.getTime());
                    }
                } catch (Exception e) { }
                return "";
            }else if (fncnam.equalsIgnoreCase("datetolong")||fncnam.equalsIgnoreCase("datetoint")) {
                // yyyy-mm-dd hh:nn:sss to long
                try {
                    
                    SimpleDateFormat  simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date= simpleDateFormat.parse(String.valueOf(fncargs.next()));
                    return date.getTime();
                } catch (Exception e) {  }
                return 0;
            }else if (fncnam.equalsIgnoreCase("diff")) { 
                ArrayList<String>  args  = new ArrayList<String>();
                while (fncargs.hasNext()) {   args.add(String.valueOf(fncargs.next())); }  
                // (string date, string date)int (detik) 
                try {
                    String str = String.valueOf(args.get(0));
                    if (isLongIntegerNumber(str)) {
                          
                    }else{
                        
                    }
                } catch (Exception e) {  }
                return 0;
            }else if (fncnam.equalsIgnoreCase("dadd")) {    
                //date1-add (string date, int detik)string (yyyy-mm-dd hh:nn:ss)
                
                
            }
            return fncnam;
        }
    };
    private void functionListener(final MathEval mathEval,final Map var1){
            mathEval.setUnFunctionHandler(unFunctionHandler);
            mathEval.setFunctionHandler("escape", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
                     return NikitaScriptObjectNotation.escapeJava(String.valueOf(fncargs.next()),true,false);
                }
            });
            mathEval.setFunctionHandler("unescape", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
                     return NikitaScriptObjectNotation.unescapeJava(String.valueOf(fncargs.next()));
                }
            });
            mathEval.setFunctionHandler("concat", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
                    StringBuffer sb = new StringBuffer();
                    while (fncargs.hasNext()) {                             
                        sb.append(String.valueOf(fncargs.next()));
                    }                        
                    return sb.toString();
                }
            });                        
   
            mathEval.setFunctionHandler("int", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
                    String s = String.valueOf(fncargs.next());
                    if (isDecimalNumber(s)) {
                        return Double.valueOf(s).intValue();
                    }else if (isLongIntegerNumber(s)) {
                        return (int)Long.parseLong(s);
                    }  
                    return 0;
                }
            });
            mathEval.setFunctionHandler("long", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
                    String s = String.valueOf(fncargs.next());
                    if (isDecimalNumber(s)) {
                        return Double.valueOf(s).longValue();
                    }else if (isLongIntegerNumber(s)) {
                        return Long.parseLong(s);
                    }                        
                    return 0;
                }
            });
           
            //expression(exp)
            mathEval.setFunctionHandler("expression", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
                    return  expression(   String.valueOf(fncargs.next()) , var1 ); // String.valueOf(fncargs.next()) 
                }
            });
            //iif(exp, true, false))
            mathEval.setFunctionHandler("iif", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
                    Object result = expression(String.valueOf(fncargs.next()), var1 );
                    if (String.valueOf(result).equalsIgnoreCase("true")) {
                        String s = String.valueOf(fncargs.next());
                        fncargs.next();
                        return s;
                    }else{
                        fncargs.next();//true result not 
                        String s = String.valueOf(fncargs.next());
                        return s; 
                    }                   
                }
            });
            //validation(args...exp)
            mathEval.setFunctionHandler("validation", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
                    ArrayList<String>  args  = new ArrayList<String>();
                    while (fncargs.hasNext()) {   args.add(String.valueOf(fncargs.next())); }  
                    for (int i = 0; i < args.size(); i++) {
                        String object = args.get(i);
                        if ( object.equalsIgnoreCase("true")||object.equalsIgnoreCase("")    ) {
                            continue;
                        }else{
                            return object;   //show error
                        }
                    }
                    return "";           
                }
            });
            //nop(args...)
            mathEval.setFunctionHandler("nop", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
                    ArrayList<String>  args  = new ArrayList<String>();
                    while (fncargs.hasNext()) {   args.add(String.valueOf(fncargs.next())); }  
                    for (int i = 0; i < args.size()-1; i++) {
                        args.get(i) ;
                    }
                    return String.valueOf(  args.get(args.size()-1) );        
                }
            });
            //iftrue(exp, true)
            mathEval.setFunctionHandler("iftrue", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
                    if (String.valueOf(fncargs.next()).equalsIgnoreCase("true")) {
                        return String.valueOf(fncargs.next());
                    }
                    return "";        
                }
            });
            //set(var, value)
            mathEval.setFunctionHandler("set", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {                     
                    var1.put( String.valueOf(fncargs.next()) , String.valueOf(fncargs.next()));
                    return "";        
                }
            });
            mathEval.setFunctionHandler("fillnson", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
                    String s = String.valueOf(fncargs.next());
                    return new NikitaScriptObjectNotation(s).parseNson() ;
                }
            });    
            mathEval.setFunctionHandler("nson", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
                    String s = String.valueOf(fncargs.next());
                    return new NikitaScriptObjectNotation(s).parseNson() ;
                }
            });       
            mathEval.setFunctionHandler("gson", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {
                    String s = String.valueOf(fncargs.next());
                    return new NikitaScriptObjectNotation(s).parseJson();
                }
            });    
            mathEval.setFunctionHandler("json", new MathEval.FunctionHandler() { 
                public Object evaluateFunction(String fncnam, MathEval.ArgParser fncargs) throws ArithmeticException {                     
                    return NikitaScriptObjectNotation.toJson(fncargs.next()) ;
                }
            });
    }
    private Object getVariableValue(Map var, ArgParser argParser, int count){
        if (argParser.isArgsVariable(1)){
            return var.get(argParser.getArgsValue(count));
        }        
        return argParser.getArgsString(count);
    }
    private String getVariableValueString(Map var, ArgParser argParser, int count){         
        return String.valueOf(getVariableValue(var, argParser, count));
    }
    private Object caseVariableValueString(Class clas, Object value){
        if (clas.getName().equals(Double.TYPE.getName())) {
            return Double.valueOf(String.valueOf(value)).doubleValue();
        }else if (clas.getName().equals(Long.TYPE.getName())) {
            return Double.valueOf(String.valueOf(value)).longValue();
          }else if (clas.getName().equals(Integer.TYPE.getName())) {
            return Double.valueOf(String.valueOf(value)).intValue();
        }else if (clas.getName().equals(Boolean.TYPE.getName())) {
            if (String.valueOf(value).equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            }
            return Boolean.FALSE;
         }else if (clas.getName().equals(Byte.TYPE.getName())) {
            return Double.valueOf(String.valueOf(value)).byteValue();
        }else if (clas.getName().equals(Short.TYPE.getName())) {
            return Double.valueOf(String.valueOf(value)).shortValue();
        }else if (clas.getName().equals(Character.TYPE.getName())) {
            return String.valueOf(value).charAt(0);
        }else if (clas.getName().equals(String.class.getName())) {
            return String.valueOf(value);       
        }      
        return value;
    }
    private void invoke (Map var, ArgParser argParser){
       
        if (argParser.getArgsString(0).equalsIgnoreCase("n")||argParser.getArgsString(0).equalsIgnoreCase("new")) {
             //inv n,$d,'class',argd,param
            if (argParser.isArgsVariable(1)) {
                var.put(argParser.getArgsValue(1),"");
                if (argParser.getArgsCount()==5) {
                    try {
                            String cname = getVariableValueString(var, argParser, 2);
                            Object con = new NikitaScriptObjectNotation(getVariableValueString(var, argParser, 3)).parseNson();
                            if (con instanceof Vector) {
                                Vector vcon= (Vector)con;
                                Class[] contr = new Class[vcon.size()]  ;
                                for (int i = 0; i < vcon.size(); i++) {
                                    String object = String.valueOf( vcon.get(i) ) ;
                                    contr[i] =  Class.forName( object  );
                                }
                                Constructor c = Class.forName( cname  ).getConstructor( contr );
                                
                                Object[] param = new Object[vcon.size()];
                                for (int i = 0; i < vcon.size(); i++) {
                                    param[i] = caseVariableValueString(contr[i], getVariableValue(var, argParser, i) );                                   
                                }
                                
                               
                                var.put(argParser.getArgsValue(1),  c.newInstance(param) );
                            }
                    } catch (Exception e) { e.printStackTrace(); }
                }else{
                    if (argParser.isArgsVariable(1)) {
                        try {
                            String cname = getVariableValueString(var, argParser, 2);
                            var.put(argParser.getArgsValue(1), Class.forName( cname  ).newInstance());
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                }
            }            
        }else if (argParser.getArgsString(0).equalsIgnoreCase("f")||argParser.getArgsString(0).equalsIgnoreCase("field")) {
            //inv f,$d,'class',fieldname 
            try {
                Field field = Class.forName(argParser.getArgsString(2)).getField( argParser.getArgsString(3) );
                Object object = field.get( argParser.getArgsString(3) );                
  
                var.put(argParser.getArgsValue(1), object );
            } catch (Exception e) { e.printStackTrace(); }   
        }else if (argParser.getArgsString(0).equalsIgnoreCase("s")||argParser.getArgsString(0).equalsIgnoreCase("static")) {
            //inv s,$d,'class',methode,args,param
            try {
                Method method = Class.forName("java.lang.Math").getMethod("abs", Double.TYPE  );
                Object object = method.invoke(null, -123);
                //Class.getPrimitiveClass("float");


               // System.err.println(object.getClass().getTypeName());
               // var.put(argParser.getArgsValue(1), Class.forName( cname  ).newInstance());
           } catch (Exception e) { e.printStackTrace(); }   
        }else if (argParser.getArgsString(0).equalsIgnoreCase("m")||argParser.getArgsString(0).equalsIgnoreCase("methode")) {   
             //inv m,$d,'class',methode,args,param
            
        }else{            
           
        }
    } 
    public Object expression(String expression){
        return expression(expression, new Hashtable());
    }
    public interface VariableListener{
        public void set(String key, Object value);
        public Object get(String key) ;
    }
    private VariableListener newVariableListenerInstance(){
        return new VariableListener(){ 
            public void set(String key, Object value) { }
            public Object get(String key) { return "";  }
        };
    }
    public Object expression(String expression, Map var1){
            final Map var = (var1!=null?var1:new HashMap());
             
            final HashMap convert = new HashMap();
            final HashMap math = new HashMap(){ 
                    public Object put(Object key, Object value) {
                        String s = String.valueOf(convert.get(key));
                        if (s.startsWith("@") || s.startsWith("$") || s.startsWith("!")) {
                            var.put(s, value);
                        }
                        return super.put(key, value);  
                    }                   
                    public Object get(Object key) {
                        String s = String.valueOf(key);
                        if (s.equalsIgnoreCase("true")) {
                            return Boolean.TRUE;
                        }else if (s.equalsIgnoreCase("false")) {
                            return Boolean.FALSE;
                        }
                        
                        Object out = convert.get(key);
                        s = String.valueOf(convert.get(key));
                        if (out!=null) {
                            if (s.startsWith("@") || s.startsWith("$") || s.startsWith("!")) {
                                return var.get(s);
                            }else{
                                return s;
                            }
                        }else{
                        	return var.get(key);//0; modif 09/09/2017
                        }
                        
                    }                    
                };
                MathEval mathEval = new MathEval(math);
                functionListener(mathEval, var1);
                 
                
                Object value = "";
                try {
                    value = mathEval.evaluate(convertMathEval(expression + " ", convert));
                } catch (Exception e) { }
                //System.out.println("----------------------------------");
                //System.out.println("exp:  "+expression);
                //System.out.println("exe:  "+convertMathEval(expression, convert));
                //System.out.println("var:  "+new Nset(convert).toJSON());
                //System.out.println("mat:  "+value);
                
                var.put("@NIKITAEXPRESSIONRESULT", value);
        return value;       
    }
    public void evaluate(String expression){
        evaluate(expression, new Hashtable());
    }
    public void evaluate(String expression, Map var){
        Stack<Integer> stack = new Stack<Integer>();        
        String[] exp = expression.split("[\\r\\n]+"); 
       
        Map<String, Integer>  label = new HashMap<String, Integer> ();
        for (int i = 0; i < exp.length; i++) {
            String string = exp[i].trim();
            if (string.startsWith(":")) {
                label.put(string.substring(1).trim(), i);
            }else if (string.startsWith("lbl") && string.contains(":")) {
                label.put(string.substring(string.indexOf(":")+1).trim(), i);
            }            
        }  
        
        //execute
        for (int i = 0; i < exp.length; i++) {
            String string = exp[i].trim();
            String argsdt = string.length()>=4  ? string.substring(3) : "";
            
            if (string.startsWith("#")||string.startsWith("/")||string.startsWith(":")) {
                //comment
            }else  if (string.startsWith("mov")||string.startsWith("set")||string.startsWith("get")) {
                ArgParser argParser = new ArgParser(argsdt);
                if (argParser.isArgsVariable(0)) {
                    if (argParser.isArgsVariable(1)) {
                        var.put(argParser.getArgsValue(0), var.get(argParser.getArgsValue(1)));
                    }else if (argParser.isArgsString(1)) {
                        var.put(argParser.getArgsValue(0), argParser.getArgsString(1));
                    }else if (argParser.isArgsNumeric(i)) {
                        var.put(argParser.getArgsValue(0), argParser.getArgsObject(1));
                    }else {
                        var.put(argParser.getArgsValue(0), argParser.getArgsObject(1));
                    }                    
                }   
            }else if (string.startsWith("exp")) {
            	int sp = argsdt.indexOf(",");
                if (sp!=-1) {
                    String arg0 = argsdt.substring(0, sp).trim();
                    String arg1 = argsdt.substring(sp +1).trim();                     
                    var.put(arg0, expression(arg1, var) );
                }
                
                /*
                ArgParser argParser = new ArgParser(argsdt);
                String eval = "";
                if (argParser.isArgsVariable(1)) {
                    eval = String.valueOf(var.get(argParser.getArgsValue(1)));
                }else if (argParser.isArgsString(1)) {
                    eval = argParser.getArgsString(1);
                }else{
                    eval = argParser.getArgsValue(1);
                }
                System.out.println("exp: "+eval);
                if (argParser.isArgsVariable(0)) {
                    try {
                        var.put(argParser.getArgsValue(0), expression(eval, var) );
                    } catch (Exception e) {
                         var.put(argParser.getArgsValue(0), "");
                    }                   
                }
                        */
            }else if (string.startsWith("inv")) {
                ArgParser argParser = new ArgParser(argsdt);
                invoke(var, argParser);
           
            }else if (string.startsWith("jmp")) {
                ArgParser argParser = new ArgParser(argsdt);
                if (label.containsKey(argParser.getArgsValue(0))) {
                    i=label.get(argParser.getArgsValue(0));
                    continue;
                }else{
                    break;//error
                }             
            }else if (string.startsWith("jne")) {
                ArgParser argParser = new ArgParser(argsdt);
                boolean jump = false ; Object object = null ;
                if (argParser.isArgsVariable(1)) {
                    object = var.get(argParser.getArgsValue(1));
                }else{
                    object = argParser.getArgsObject(1);
                }
                
                if (argParser.isArgsVariable(0)) {
                    jump = !equal(String.valueOf( var.get(argParser.getArgsValue(0)) ), object);
                }else   {
                    jump = !equal(String.valueOf( argParser.getArgsValue(0) ), object) ;
                }
                if (jump) {
                    if (label.containsKey(argParser.getArgsValue(2))) {
                        i=label.get(argParser.getArgsValue(2));
                        continue;
                    }else{
                        break;//error
                    }  
                }
            }else if (string.startsWith("jnz")) {
                ArgParser argParser = new ArgParser(argsdt);
                boolean jump = false ;
                if (argParser.isArgsVariable(0)) {
                    jump = !equal(String.valueOf( var.get(argParser.getArgsValue(0)) ), 0);
                }else   {
                    jump = !equal(argParser.getArgsValue(0) , 0)  ;
                }
                if (jump) {
                    if (label.containsKey(argParser.getArgsValue(1))) {
                        i=label.get(argParser.getArgsValue(1));
                        continue;
                    }else{
                        break;//error
                    }  
                }
            }else if (string.startsWith("out")) { 
                ArgParser argParser = new ArgParser(argsdt);
                for (int j = 0; j < argParser.getArgsCount(); j++) { 
                    if (argParser.isArgsVariable(j)) {
                        System.out.print(var.get(argParser.getArgsValue(j)) );
                    }else{
                        System.out.print(argParser.getArgsObject(j));
                    }
                }  
                System.out.println();
            }else if (string.startsWith("inc")) {   
                ArgParser argParser = new ArgParser(argsdt);
                if (argParser.isArgsVariable(0)) {
                    String s = String.valueOf(var.get(argParser.getArgsValue(0)));
                    if (isDecimalNumber(s)) {
                        var.put(argParser.getArgsValue(0), Double.valueOf(s).doubleValue()+1 );
                    }                    
                }
            }else if (string.startsWith("dec")) {       
                ArgParser argParser = new ArgParser(argsdt);
                if (argParser.isArgsVariable(0)) {
                    String s = String.valueOf(var.get(argParser.getArgsValue(0)));
                    if (isDecimalNumber(s)) {
                        var.put(argParser.getArgsValue(0), Double.valueOf(s).doubleValue()-1 ) ;
                    }                    
                }
            }else if (string.startsWith("end")) {   
                break;//brea;                
            }else if (string.startsWith("cal")||string.startsWith("call")) {
                ArgParser argParser = new ArgParser(argsdt);
                if (label.containsKey(argParser.getArgsValue(0))) {
                    stack.push(i);
                    i=label.get(argParser.getArgsValue(0));
                    
                    continue;
                }else{
                    break;//error
                }
            }else if (string.startsWith("ret")) {
                if (!stack.isEmpty()) {
                    i=stack.pop();
                    continue;
                }else{
                    break;//error
                }
            }else if (string.length()>=3) {                
                //expression(string, var);
            }
        }
    } 
     
    private String convertMathEval(String eval, Map mathvariable ){
        mathvariable.clear();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < eval.length(); i++) {
            int offset=parseMath(eval, sb, i, mathvariable);
            if (offset==i) {
                sb.append(eval.charAt(offset));
            }
            i = offset;
        }      
        
            String s = sb.toString();
            
            s=s.replaceAll("\\b([aA][nN][dD])\\b", "&");
            s=s.replaceAll("\\b([oO][rR])\\b", "|");
            s=s.replaceAll("\\b([lL][iI][kK][eE])\\b", "þ");
        s=s.replaceAll(">=", "»");
        s=s.replaceAll("<=", "«");
        s=s.replaceAll("==", ":");
        s=s.replaceAll("!=", "~");
        s=s.replaceAll(">", ">");    
        s=s.replaceAll("<", "<");    
        s=s.replace("++", "‡");
            
        return s;
    }
    private int parseMath(String eval, StringBuffer sb, int offset, Map mathvariable ){
        char c =  eval.charAt(offset);
        switch (c){
            case '\'':
                return parseQuoteSingle(eval, sb, offset+1, mathvariable);
            case '"':    
                return parseQuoteDouble(eval, sb, offset+1, mathvariable);
             case '(':
                sb.append(c);
                return parseGroup(eval, sb, offset+1, mathvariable);
            case '$':
            case '@':
                return parseComponantName(eval, sb, offset, mathvariable);
            default:                
                return offset;
        } 
        
    }
    private int parseQuoteSingle(String eval, StringBuffer sb, int offset, Map mathvariable ){
        String buffer = "bnrf\\";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = offset ; i < eval.length(); i++) {
            char c =  eval.charAt(i);            
            switch (c){
                case '\'':
                   
                   String name = "squote"+mathvariable.size();  
                   mathvariable.put(name, stringBuffer.toString());
                   sb.append(name);
                   
                   //System.out.println(name+ " = " + stringBuffer.toString());
                   return i; 
                case '\\':   
                    if (eval.length()>i+1) {
                        i++;
                        String ns = eval.substring(i, i+1);
                        if ("'".equals(ns)) {
                            continue;
                        }else if (buffer.contains(ns)) {
                            continue;
                        } 
                    }   
                    return  eval.length();//err0r
                default:
                  int xi=parseMath(eval, sb, i, mathvariable);                    
                   if (xi==i) {                        
                       stringBuffer.append(eval.charAt(xi));
                   }
                   i=xi; 
                    
            } 
        }
        return eval.length();
    }
    private int parseQuoteDouble(String eval, StringBuffer sb, int offset, Map mathvariable ){
        String buffer = "bnrf\\";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = offset ; i < eval.length(); i++) {
            char c =  eval.charAt(i);            
            switch (c){
                case '"':
                    
                   String name = "dquote"+mathvariable.size();  
                   mathvariable.put(name, stringBuffer.toString());
                   sb.append(name);
                   
                   //System.out.println(name+ " = " + stringBuffer.toString());
                   return i; 
                case '\\':   
                    if (eval.length()>i+1) {
                        i++;
                        String ns = eval.substring(i, i+1);
                        if ("'".equals(ns)) {
                            continue;
                        }else if (buffer.contains(ns)) {
                            continue;
                        } 
                    }   
                    return  eval.length();//err0r
                default:
                   int xi=parseMath(eval, sb, i, mathvariable);                    
                   if (xi==i) {                        
                       stringBuffer.append(eval.charAt(xi));
                   }
                   i=xi; 
            } 
        }
        return eval.length();
    }
    private int parseComponantName(String eval, StringBuffer sb, int offset, Map mathvariable ){
        for (int i = offset+1; i < eval.length(); i++) {
            char c =  eval.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                
                String name = "comp"+mathvariable.size()  ;//eval.substring(offset, i).replaceAll("[\\W]", "").toLowerCase();  
                mathvariable.put(name, eval.substring(offset, i));
                sb.append(name);
                //System.out.println(name+ " = " +eval.substring(offset, i));
                
                return i-1;
             
            }
        }
        return eval.length();
    }
    private int parseGroup(String eval, StringBuffer sb, int offset, Map mathvariable ){        
        for (int i = offset; i < eval.length(); i++) {
            char c =  eval.charAt(i);
            switch (c){
                case ')':
                   sb.append(c);
                   return i; 
                default:
                   int xi=parseMath(eval, sb, i, mathvariable);                    
                   if (xi==i) {
                       sb.append(eval.charAt(xi));
                   }
                   i=xi; 
            } 
        }
        return eval.length();
    }
    
    private boolean equal(Object object , Object object1){
        if (object instanceof Number || object1 instanceof Number) {
            if (isDecimalNumber(String.valueOf(object)) && isDecimalNumber(String.valueOf(object1))) {
                return Double.valueOf(String.valueOf(object)).doubleValue()== Double.valueOf(String.valueOf(object1)).doubleValue();
            }
        }else{
            return String.valueOf(object).equals(String.valueOf(object1));
        }
        return false;
    }
    private int skipWhitespace(String exp, int ofs, int end) {
        while(ofs<=end && Character.isWhitespace(exp.charAt(ofs))) { ofs++; }
        return ofs;
    }
   
    public class ArgParser{
        private final String splitecode = ",";
        
        private String[] sargsData = new String[0]  ;
        public ArgParser(String args){
            ArrayList<String> argsData = new ArrayList<String>() {}  ;
            
            for (int i = 0; i < args.length(); i++) {
                int n = findArgsNext(args, i);
                
                String s = args.substring(i, n);
                argsData.add(s);
                
                i = n;
            }
            sargsData =argsData.toArray(new String[]{});                
            //for (int i = 0; i < sargsData.length; i++) {      System.out.println(sargsData[i]);            }
        }
         
        private int findArgsNext(String args, int start){           
            for (int i = start; i < args.length(); i++) {
                char c = args.charAt(i);
                switch (c){
                    case '\''://string sqoute
                        i=findArgsString(args, i);
                        break;
                    case '"'://string dqoute
                        i=findArgsString(args, i);
                        break;
                    case '('://group    
                        i=findArgsGroup(args, i);
                        break;
                    case ','://next arg    
                        return i;
                    default:
                    
                }                
            }
            return args.length();//-1 args(last)
        }
        private int findArgsString(String args, int start){
           String s = args.substring(start, start+1);
           String buffer = "bnrf\\";
           for (int i = start+1; i < args.length(); i++) {
                char c = args.charAt(i);                
                switch (c){
                    case '\\': 
                        if (args.length()>i+1) {
                            i++;
                            String ns = args.substring(i, i+1);
                            if (s.equals(ns)) {
                                continue;
                            }else if (buffer.contains(ns)) {
                                continue;
                            } 
                        }   
                        return  args.length();//err0r
                    case '\''://string sqoute
                        
                    case '"'://string dqoute
                        if (s.equalsIgnoreCase(args.substring(i,i+1))) {
                            return i;
                        }
                        
                    default:
                    
                }                
            } 
           return args.length();//-1 args(last)
        }
        private int findArgsGroup(String args, int start){
            for (int i = start+1; i < args.length(); i++) {
                char c = args.charAt(i);
                switch (c){
                    case '\''://string sqoute
                    case '"'://string dqoute
                        i = findArgsString(args, i);
                       
                    case '('://group    
                        i = findArgsGroup(args, i);
                       
                    case ')'://next arg    
                        return i;
                    default:
                    
                }                
            }
           return args.length();//-1 args(last)
        }
        private int getArgsString(String args){
            if (args.startsWith("'")||args.startsWith("\"")) {
                String split = args.substring(0,1);                  
                for (int offset = 1; offset < args.length(); offset++) {
                    String s = args.substring(offset, offset+1);
                    if (s.equals("\\")) {
                        if (args.length()>offset+1) {
                            offset++;
                            s = args.substring(offset, offset+1);
                            if (s.equals(split)) {
                                continue;
                            }else if ("bnrf\\".contains(s)) {
                                continue;
                            } 
                        }   
                        break;//err0r
                    }else if (s.equals(split)) {
                        return offset+1;
                    }                    
                }
                return -1;//arg string error                 
            }
            return 0;//bukan arg string
        }
        public String[] getArgs(){
            return  sargsData;
        }
        public int getArgsCount(){
            return  sargsData.length;
        }
        public String getArgsValue(int i){
            if (sargsData.length>i) {
                return sargsData[i].trim();
            }
            return  "";
        }
        public boolean isArgsVariable(int i){
            if (getArgsValue(i).startsWith("@")||getArgsValue(i).startsWith("$")) {
                return true;
            }
            return false;
        }
        public boolean isArgsString(int i){
            if (getArgsValue(i).startsWith("'") && getArgsValue(i).endsWith("'")) {
                return true;
            }else if (getArgsValue(i).startsWith("\"") && getArgsValue(i).endsWith("\"")) {
                return true;
            }
            return false;
        }
        public boolean isArgsNumeric(int i){
            return isDecimalNumber(getArgsValue(i));
        }
 
        public String getArgsString(int i){
            if (isArgsString(i)) {
                String s = getArgsValue(i);
                if (s.endsWith(s.substring(0,1))) {                     
                    // return s.substring(1,s.length()-1);                   
                    return  NikitaScriptObjectNotation.unescapeJava(s.substring(1,s.length()-1));
                }else{
                    return "";//error
                }                
            }
            return  "";//getArgsValue(i);
        } 
        public Object getArgsObject(int i){
            if (isArgsString(i)) {
                return getArgsString(i);
            }else if (getArgsValue(i).equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            }else if (getArgsValue(i).equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            }else if (isArgsNumeric(i)) {
                return  Double.valueOf(getArgsValue(i)).doubleValue();
            }
            return  getArgsString(i);
        }
    }
    public void atg(String expression ){        
        if (expression.equals("true")) {
            //boolean
        }else if (expression.startsWith("'")||expression.startsWith("\"")) {
            //string
        }else if (expression.startsWith("(")) {
            //group
        }else if (expression.startsWith("{")) { 
            //block
        }else {
            //maybe number
        }              
    }
                
    private static boolean isDecimalNumber(String str) {
        return str.matches("^[-+]?[0-9]*.?[0-9]+([eE][-+]?[0-9]+)?$");
    }
    private static boolean isLongIntegerNumber(String str) {
        return str.matches("-?\\d+");  
    }
     
 
} 
