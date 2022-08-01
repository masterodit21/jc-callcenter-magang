/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author rkrzmail
 */
public abstract class NikitaCode {
    private Vector<String>  ncode = new  Vector<String> ();
    public abstract void nSourceCode();
    
    public final void begin(){  
        ncode.clear();
    }    
    protected final void mov(String arg, String arg1){   
        ncode.add("mov     "+arg+","+zvarArg(arg1)+"");      
    }
    protected final void mov(String arg, boolean arg1){
        ncode.add("mov     "+arg+","+arg1);
    }
    protected final void mov(String arg, int arg1){
        ncode.add("mov     "+arg+","+arg1);
    }
    protected final void execute(String result, String data, String methode, Class<?>[]args, String...params){
        ncode.add("execute "+result+","+data+","+methode+","+zgetDataType(args)+","+zgetDataValue(params));
    }
    private String zgetDataType(Class<?>[]args){
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < args.length; i++) {
            sb.append(i>=1?",":"").append(args[i].getName());            
        }
        return  sb.append("]").toString();
    }
    private String zvarArg(String arg1){
        if (arg1.startsWith("@")||arg1.startsWith("$")||arg1.startsWith("!")) {
            return arg1;
        }else if (arg1.startsWith("'")||arg1.endsWith("'")) {
            return arg1;
        }else{
            return "'"+arg1+"'";
        }   
    }
    private String zgetDataValue(String []args){
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < args.length; i++) {
            sb.append(i>=1?",":"").append(zvarArg(args[i]));            
        }
        return  sb.append("]").toString();
    }
    protected final void invoke(String result, Class<?> class1, String methode, Class<?>[]args, String...params){
        ncode.add("invoke  "+result+","+class1.getName()+","+methode+","+zgetDataType(args)+","+zgetDataValue(params));
    }
    protected final void invoke(String result, String class1, String methode, Class<?>[]args, String...params){
        ncode.add("invoke  "+result+","+class1+","+methode+","+zgetDataType(args)+","+zgetDataValue(params));
    }
    protected final void finvoke(String result, Class<?> class1, String field){
        ncode.add("finvoke "+result+","+class1.getName()+","+field);
    }
    protected final void finvoke(String result, String class1, String field ){
        ncode.add("finvoke "+result+","+class1+","+field);
    }
    protected final void jnew(String result, Class<?> class1  ){
        ncode.add("jnew    "+result+","+class1.getName());
    }
   
    protected final void label(String arg){
        ncode.add(":"+(arg.startsWith(":")?arg.substring(1):arg));
    }
    protected final void lbl(String arg){
        ncode.add(":"+(arg.startsWith(":")?arg.substring(1):arg));
    }
    protected final void jmp(String arg){
        ncode.add("jmp     "+arg);
    }
    protected final void inc(String arg){
        ncode.add("inc     "+arg);
    }
    protected final void dec(String arg){
        ncode.add("dec     "+arg);
    }
    protected final void set(String var, String var2){
        ncode.add("set     "+var+","+zvarArg(var2));
    }
    protected final void get(String result, String var){
        ncode.add("get     "+result+","+zvarArg(var));
    }
    public final void cjne(String arg,String arg1, String jump){
        ncode.add("cjne    "+arg+","+zvarArg(arg1)+","+jump);
    }
    public final void cjne(String arg,int arg1, String jump){
        ncode.add("cjne    "+arg+","+arg1+","+jump);
    }
    public final void cje(String arg,String arg1, String jump){
        ncode.add("cje     "+arg+","+zvarArg(arg1)+","+jump);
    }
    public final void cje(String arg,int arg1, String jump){
        ncode.add("cje     "+arg+","+arg1+","+jump);
    }
    public final void cjnz(String arg,  String jump){
        ncode.add("cjnz    "+arg+","+jump);
    }
    public final void cjz(String arg,  String jump){
        ncode.add("cjz     "+arg+","+jump);
    }
    public final void cast(String arg,  String cast){
        ncode.add("cast    "+arg+","+cast);
    }
    protected final void toint(String arg){
        ncode.add("toint   "+arg);
    }
    protected final void tolong(String arg){
        ncode.add("tolong  "+arg);
    }
    protected final void todouble(String arg){
        ncode.add("todbl   "+arg);
    }
    protected final void toboolean(String arg){
        ncode.add("tobool  "+arg);
    }
    protected final void tostring(String arg){
        ncode.add("tostr   "+arg);
    }
    protected final void cmp(String result, String arg1, String arg2){   
        ncode.add("cmp     "+result+","+zvarArg(arg1)+","+zvarArg(arg2));      
    }
    protected final void eval(String result, String formula, String...params){   
        ncode.add("eval    "+result+","+formula+","+zgetDataValue(params));      
    }
    protected final void or(String result, String arg1, String arg2){   
        ncode.add("and     "+result+","+zvarArg(arg1)+","+zvarArg(arg2));      
    }
     public final void write(String arg){
        ncode.add("write   "+ zvarArg(arg) );
    }
      public final void sout(String arg){
        ncode.add("sout    "+zvarArg(arg) );
    }
      public final void print(String arg){
        ncode.add("print   "+zvarArg(arg) );
    }
    public final void println(String arg){
        ncode.add("println "+zvarArg(arg) );
    }
    protected final void iif(String arg){
        ncode.add("iif "+arg);
    }
    public NikitaCode run(){
        nSourceCode();
        return this;
    }
    public String sourceprint(){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ncode.size(); i++) {
            sb.append(ncode.get(i)).append("\n");
            
        }
        return  sb.toString();
    }
    
    
    
    public static void asmNikita(String scrpt, Hashtable var){
        String[] asmcode = scrpt.split("\n");
        Hashtable<String, Integer>  label = new Hashtable<String, Integer> ();
        for (int i = 0; i < asmcode.length; i++) {
            String string = asmcode[i].trim();
            if (string.startsWith(":")) {
                label.put(string.substring(1).trim(), i);
            }
        }
        //execute
         
        for (int i = 0; i < asmcode.length; i++) {
            String string = asmcode[i].trim();    
            
            scrpt=string.trim();int space = scrpt.indexOf(" ");
            if (space>=1) {
                String asm = scrpt.substring(0, scrpt.indexOf(" ")).trim();
                String[] args = new String[]{"","","","","",""};
                String data = scrpt.substring(scrpt.indexOf(" ")).trim();
                
                if (asm.equalsIgnoreCase("mov")) {
                    args[0] = data.substring(0,data.indexOf(",")).trim();
                    args[1] = data.substring(data.indexOf(",")+1).trim();
                    
                    if (args[1].startsWith("'")&& args[1].endsWith("'")) {
                        var.put(args[0], args[1].substring(1, args[1].length()-1));
                    }else if (args[1].startsWith("@")||args[1].startsWith("$")||args[1].startsWith("!") ) {
                        var.put(args[0], var.get(args[1])  );
                    }else if (args[1].equalsIgnoreCase("true")||args[1].equalsIgnoreCase("false")) {
                        var.put(args[0], args[1].equalsIgnoreCase("true")?true:false);
                    }else{
                        var.put(args[0], args[1]);
                    } 
                }else if (asm.equalsIgnoreCase("cjne")) {
                    args[0] = data.substring(0,data.indexOf(",")).trim();
                    data = data.substring(data.indexOf(",")+1).trim();
                    args[1] = data.substring(0, data.indexOf(",")).trim();
                    args[2] = data.substring(data.indexOf(",")+1).trim();
                    
                    if (var.get(args[0]).equals(var.get(args[1]))||String.valueOf(var.get(args[0])).equals( String.valueOf(var.get(args[1]))  )  ) {                        
                    }else{
                        if (label.containsKey(args[2])) {
                            i=label.get(args[2]);
                            continue;
                        }else{
                            break;//error
                        }
                    }
                }else if (asm.equalsIgnoreCase("cje")) {
                    args[0] = data.substring(0,data.indexOf(",")).trim();
                    data = data.substring(data.indexOf(",")+1).trim();
                    args[1] = data.substring(0, data.indexOf(",")).trim();
                    args[2] = data.substring(data.indexOf(",")+1).trim();
                    
                    if (var.get(args[0]).equals(var.get(args[1]))||String.valueOf(var.get(args[0])).equals( String.valueOf(var.get(args[1]))  )  ) {                        
                        if (label.containsKey(args[2])) {
                            i=label.get(args[2]);
                            continue;
                        }else{
                            break;//error
                        }
                    }
                }else if (asm.equalsIgnoreCase("cjnz")) {
                    args[0] = data.substring(0,data.indexOf(",")).trim();
                    args[1] = data.substring(data.indexOf(",")+1).trim();
                    if (var.get(args[0]).equals( 0 )||String.valueOf(var.get(args[0])).equals( "0" )) {
                    }else{
                        if (label.containsKey(args[1])) {
                            i=label.get(args[1]);
                            continue;
                        }else{
                            break;//error
                        }
                    }
                }else if (asm.equalsIgnoreCase("cjz")) {
                    args[0] = data.substring(0,data.indexOf(",")).trim();
                    args[1] = data.substring(data.indexOf(",")+1).trim();
                    if (var.get(args[0]).equals( 0 )||String.valueOf(var.get(args[0])).equals( "0" )) {
                        if (label.containsKey(args[1])) {
                            i=label.get(args[1]);
                            continue;
                        }else{
                            break;//error
                        }
                    }
                }else if (asm.equalsIgnoreCase("jmp")) {
                    args[0] = data.trim();
                    if (label.containsKey(args[0])) {
                        i=label.get(args[0]);
                        continue;
                    }else{
                        break;//error
                    }
                }else if (asm.equalsIgnoreCase("invoke")) { 
                    args[0] = data.substring(0,data.indexOf(",")).trim();
                    data = data.substring(data.indexOf(",")+1).trim();
                    args[1] = data.substring(0, data.indexOf(",")).trim();
                    data = data.substring(data.indexOf(",")+1).trim();
                    args[2] = data.substring(0, data.indexOf(",")).trim();
                    data = data.substring(data.indexOf(",")+1).trim();                    
                    args[3] = data.substring(0, data.indexOf("]")+1).trim();
                    data = data.substring(data.indexOf("]")+1).trim();
                    args[4] = data.substring(data.indexOf(",")+1).trim();  
                    
                   
                    
                    try {
                       // Method method = var.get(args[1]).getClass().getMethod( args[2]   , null  );
                        //var.put(args[0], Class.forName(args[1]).newInstance());
                    } catch (Exception e) { }
                }else if (asm.equalsIgnoreCase("finvoke")) {
                }else if (asm.equalsIgnoreCase("execute")) {    


                }else if (asm.equalsIgnoreCase("cast")) {  
                    
                }else if (asm.equalsIgnoreCase("jnew")) {  
                    args[0] = data.substring(0,data.indexOf(",")).trim();
                    args[1] = data.substring(data.indexOf(",")+1).trim();
                    try {
                        var.put(args[0], Class.forName(args[1]).newInstance());
                    } catch (Exception e) { }
                }else if (asm.equalsIgnoreCase("eval")) {
                    
                }else if (asm.equalsIgnoreCase("inc")) {
                    args[0] = data.trim();
                    var.put(args[0], Utility.getInt(String.valueOf(var.get(args[0])))+1  );
                }else if (asm.equalsIgnoreCase("dec")) {  
                    args[0] = data.trim();
                     var.put(args[0], Utility.getInt(String.valueOf(var.get(args[0])))-1  );
                }else if (asm.equalsIgnoreCase("set")) {  
                    var.put(args[0], var.get(args[1]));
                }else if (asm.equalsIgnoreCase("get")) {  
                    var.put(args[0], var.get(args[1]));
                }else if (asm.equalsIgnoreCase("toint")) {       
                }else if (asm.equalsIgnoreCase("tolong")) {   
                }else if (asm.equalsIgnoreCase("todouble")) { 
                }else if (asm.equalsIgnoreCase("tostring")) {  
                }else if (asm.equalsIgnoreCase("tojson")) {

                }else if (asm.equalsIgnoreCase("tonset")) {
                }else if (asm.equalsIgnoreCase("rjson")) {   
                    var.put(args[0], Nset.readJSON( String.valueOf(var.get(args[1])) ));
                }else if (asm.equalsIgnoreCase("println")) {  
                    args[0] = data.trim();
                    System.out.println(var.get(args[0]));
                }else if (asm.equalsIgnoreCase("sout")||asm.equalsIgnoreCase("print")) {  
                    args[0] = data.trim();
                    System.out.print(var.get(args[0]));
                }else if (asm.equalsIgnoreCase("write")) { 
                    args[0] = data.trim();

                }

            }
        }
        
    }
}
