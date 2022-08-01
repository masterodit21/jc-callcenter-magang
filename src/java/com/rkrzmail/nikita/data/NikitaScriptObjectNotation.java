/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.rkrzmail.nikita.data;

 
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;
import org.apache.commons.lang.UnhandledException;

/**
 *
 * @author rkrzmail
 */
public class NikitaScriptObjectNotation {
   
    private String nson; boolean json = false;
    public NikitaScriptObjectNotation(String nson){
        this.nson=nson!=null?nson.trim():"";
    }
    public NikitaScriptObjectNotation(String nson, boolean json){
        this.nson=nson!=null?nson.trim():"";
        this.json=json;
    }
    public Object parseJson(){
        Object object =  parseNson();
        return object;
    }
    boolean error = false;
    private void setError(){
        error = true;
        nson = "";
    }
    public Object parseNson(){       
        Object result = null;    
        nson =  nson.trim(); //trim 
        // nson = nson.replaceAll("[\\r\\n]+", "");
        if (nson.startsWith("{")) {
            result = readObject();
        }else if (nson.startsWith("[")) {
            result = readArray();
        }else  if (nson.contains(",")){
            nson = "["+nson+"]";
            result = readArray();
        }   
        if (error || nson.trim().length()>=1) {
            return null;
        }
        return result;
    }
        
    public static String toJson(  Object o ){
        StringBuffer sb = new StringBuffer();        
        if (o instanceof Hashtable) {
            writeObject(sb,o);
        }else if (o instanceof Vector) {
            writeArray(sb,o); 
        }
        return sb.toString();
    }  
    private static void writeArray(StringBuffer sb , Object object){
        if (object instanceof Vector) {
            Vector dataobject = (Vector)object;
            sb.append("[");            
            for (int i = 0; i < dataobject.size(); i++) {
                object = dataobject.elementAt(i);
                sb.append((i>=1?",":""));
                writeValue(sb, object);                
            }
            sb.append("]");
        }       
    }
    private static void writeObject(StringBuffer sb, Object object){
         
        if (object instanceof Hashtable) {
            Hashtable dataobject = (Hashtable)object; 
            sb.append("{");
            int count = 0;
            Enumeration enumeration = dataobject.keys();            
            while (enumeration.hasMoreElements()) {
                String key = (String)enumeration.nextElement();
                object = dataobject.get(key);
                 
                sb.append((count>=1?",":""));
                sb.append("\"");
                sb.append(key);
                sb.append("\":");
                
                writeValue(sb, object);
                count++;                
            }
            sb.append("}");
        }       
    }
    private static void writeValue(StringBuffer sb, Object object){
        if (object==null) {
            sb.append("null");
        }else if (object instanceof Hashtable) {
            writeObject(sb,object);
        }else if (object instanceof Vector) {
           writeArray(sb,object);
        }else if (object instanceof Number) {
            sb.append(  ((Number)object)  );
        }else if (object instanceof String) {
            sb.append("\"").append( escapeJava((String)object, true, true ) ).append("\"");
        }else if (object instanceof Boolean) {
            sb.append( ((Boolean)object) ? "true" : "false");
        }else if (object instanceof NullPointerException) {
            sb.append( "null" );
        }else  {
             sb.append("\"").append( escapeJava(String.valueOf(object), true, true ) ).append("\"");
             //sb.append(String.valueOf(object));
        }   
    }
    public static final Object NSON_NULL =  new NullPointerException(){
        public String toString() {
            return "null"; 
        }  
    };
    public Object newNullInstance(){
        return NSON_NULL;
    }     
   
    
    public class NsonString {
        private String string;
        public NsonString(String str){
            this.string=str;
        }
        public String toString() {
            return string; 
        }  
    };
    
    private Hashtable readObject(){
        Hashtable hashtable = new Hashtable();
        beginObject();
        while (hasNext()) {            
            hashtable.put(nextName(), readValue());
        }   
        endObject();
        return hashtable;
    }
    private Vector readArray(){
        Vector vector = new Vector();
        beginArray();
        while (hasNext()) {            
            vector.addElement(readValue());            
        }    
        endArray();
        return vector;
    }
    private Object readValue(){
        nson=nson.trim();
        if (nson.startsWith("{")) {
            return readObject() ;
        }else if (nson.startsWith("[")) {           
            return readArray(); 
        }else if (nson.startsWith("'")||nson.startsWith("\"")) {
            String s = nextString();
            errorNext();
            return s;        
        }else{
            String s = nextNsonString();
            errorNext();
            if (isNumeric(s)) {                
                if (s.contains(".")) {
                    return Double.parseDouble(s);
                }
                return Long.parseLong(s);
            }else if (s.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            }else if (s.equalsIgnoreCase("false")) {  
                return Boolean.FALSE;
            }else if (s.equalsIgnoreCase("null")) { 
                return newNullInstance();//null
            }else{
                return new NsonString(s);//nson
            }             
        } 
    }
    private void errorNext(){
        nson=nson.trim();
        if (nson.startsWith(",")||nson.startsWith("]")||nson.startsWith("}")) {
            if (nson.startsWith(",")) {
                nson=nson.substring(1);
            }           
        }else if (nson.equals("")) {
            
        }else{
            setError();
        }
    }
    private static boolean isNumeric(String str) {
        return str.matches("^[-+]?[0-9]*.?[0-9]+([eE][-+]?[0-9]+)?$");//return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
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
                            }else if ("/bnrft\\".contains(s)) {
                                continue;
                            }else if (s.equals("u")) {                                
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
    private void beginArray(){
        nson=nson.trim();
        if (nson.startsWith("[")) {
            nson = nson.substring(1);
        }else{
            setError();
        }
        
    }
    private void beginObject(){
        nson=nson.trim();
        if (nson.startsWith("{")) {
            nson = nson.substring(1);
        }else{
            setError();
        }
    }
    private void endArray(){
        nson=nson.trim();
        if (nson.startsWith("]")) {
            nson = nson.substring(1);
        }else{
            setError();
        }
        nson=nson.trim();
        errorNext();
    }
    private void endObject(){
        nson=nson.trim();
        if (nson.startsWith("}")) {
            nson = nson.substring(1);
        }else{
            setError();
        }    
        errorNext();
    }
    private String nextName(){
        String name =  "" ;
        int i = getArgsString(nson);
        switch (i){
            case -1://error
                setError();
            case 0://bukan string argumnets
                name =  nextNsonString();    
                break;
            case 1://emptystring
                nson = nson.substring(i+1); 
                break;
            default:
               name =  nson.substring(1,i-1);
               nson = nson.substring(i); 
                
        }
        nson = nson.trim();
        if (nson.startsWith(":")) {
            nson=nson.substring(1);
        }else{
            setError();//error
        }
        return unescapeJava(name);
    }
 
    private String nextNsonString(){
        for (int i = 0; i < nson.length(); i++) {
            String s = nson.substring(i,i+1);
            if (s.equals(",")||s.equals(":")||s.equals("}")||s.equals("]")) {
                s = nson.substring(0,i).trim();
                nson = nson.substring(i);
                return s;
            }
        } 
        setError();//error
        return "";
    }
    private String nextString(){
        String name =  "" ;
        int i = getArgsString(nson);
        switch (i){
            case -1://error
                setError();
            case 0://bukan string argumnets
                name =  nextNsonString();                
                break;
            case 1://emptystring
                nson = nson.substring(i+1); 
                break;
            default:
               name =  nson.substring(1,i-1);
               nson = nson.substring(i); 
        }        
        return unescapeJava(name);
    }
    private String nextBoolean(){
        return "";
    }
     
 
    private boolean hasNext(){
        nson = nson.trim();
        if (nson.startsWith("]")) {
            return false;
        }else if (nson.startsWith("}")) {
            return false;
        }else if (nson.length()>=1) {
            return true;
        }
        return false;
    }
 
    public static String escapeJava(String str) {
            return escapeJava(str, false, false);
    } 
    public static String escapeJava(String str, boolean escapeSingleQuotes, boolean escapeForwardSlash) {
            if (str == null) {
                 return null;
            }
             try {
                 StringWriter writer = new StringWriter(str.length() * 2);
                 escapeJava(writer, str, escapeSingleQuotes, escapeForwardSlash);
                 return writer.toString();
             } catch (IOException ioe) {
                 // this should never ever happen while writing to a StringWriter
                 throw new UnhandledException(ioe);
             }
    }
    public static void escapeJava(Writer out, String str) throws IOException {
            escapeJava(out, str, false, false);
    }     
    private static void escapeJava(Writer out, String str, boolean escapeSingleQuote,  boolean escapeForwardSlash) throws IOException {
            if (out == null) {
                 throw new IllegalArgumentException("The Writer must not be null");
            }
            if (str == null) {
                 return;
            }
            int sz;
            sz = str.length();
            for (int i = 0; i < sz; i++) {
                char ch = str.charAt(i);
                  // handle unicode
                if (ch > 0xfff) {
                     out.write("\\u" + hex(ch));
                } else if (ch > 0xff) {
                     out.write("\\u0" + hex(ch));
                } else if (ch > 0x7f) {
                     out.write("\\u00" + hex(ch));
                } else if (ch < 32) {
                     switch (ch) {
                         case '\b' :
                             out.write('\\');
                             out.write('b');
                             break;
                         case '\n' :
                             out.write('\\');
                             out.write('n');
                             break;
                         case '\t' :
                             out.write('\\');
                             out.write('t');
                             break;
                         case '\f' :
                             out.write('\\');
                             out.write('f');
                             break;
                         case '\r' :
                             out.write('\\');
                             out.write('r');
                             break;
                         default :
                             if (ch > 0xf) {
                                 out.write("\\u00" + hex(ch));
                             } else {
                                 out.write("\\u000" + hex(ch));
                             }
                             break;
                        }
                } else {
                        switch (ch) {
                         case '\'' :
                             if (escapeSingleQuote) {
                                 out.write('\\');
                             }
                             out.write('\'');
                             break;
                         case '"' :
                             out.write('\\');
                             out.write('"');
                             break;
                         case '\\' :
                             out.write('\\');
                             out.write('\\');
                             break;
                         case '/' :
                             if (escapeForwardSlash) {
                                 out.write('\\');
                             }
                             out.write('/');
                             break;
                         default :
                             out.write(ch);
                             break;
                    }
                }
            }
    }
    
    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
    }    
    public static String unescapeJava(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter(str.length());
            unescapeJava(writer, str);
            return writer.toString();
        } catch (IOException ioe) {
            // this should never ever happen while writing to a StringWriter
            throw new UnhandledException(ioe);
        }
    } 
     
    public static void unescapeJava(Writer out, String str) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (str == null) {
            return;
        }
             int sz = str.length();
             StringBuffer unicode = new StringBuffer(4);
             boolean hadSlash = false;
             boolean inUnicode = false;
             for (int i = 0; i < sz; i++) {
                 char ch = str.charAt(i);
                 if (inUnicode) {
                     // if in unicode, then we're reading unicode
                     // values in somehow
                     unicode.append(ch);
                     if (unicode.length() == 4) {
                         // unicode now contains the four hex digits
                         // which represents our unicode character
                         try {
                            int value = Integer.parseInt(unicode.toString(), 16);
                                out.write((char) value);
                                unicode.setLength(0);
                                inUnicode = false;
                                hadSlash = false;
                         } catch (NumberFormatException nfe) {
                           //  throw new NestableRuntimeException("Unable to parse unicode value: " + unicode, nfe);
                         }
                     }
                     continue;
                 }
                 if (hadSlash) {
                     // handle an escaped value
                     hadSlash = false;
                     switch (ch) {
                         case '\\':
                             out.write('\\');
                             break;
                         case '\'':
                             out.write('\'');
                             break;
                         case '\"':
                             out.write('"');
                             break;
                         case 'r':
                             out.write('\r');
                             break;
                         case 'f':
                             out.write('\f');
                             break;
                         case 't':
                             out.write('\t');
                             break;
                         case 'n':
                             out.write('\n');
                             break;
                         case 'b':
                             out.write('\b');
                             break;
                         case 'u':
                            {
                                 // uh-oh, we're in unicode country....
                                inUnicode = true;
                                break;
                            }
                         default :
                             out.write(ch);
                             break;
                     }
                     continue;
                 } else if (ch == '\\') {
                     hadSlash = true;
                     continue;
                 }
                 out.write(ch);
              }
             if (hadSlash) {
                 // then we're in the weird case of a \ at the end of the
                 // string, let's output it anyway.
                 out.write('\\');
             }
    }
}
