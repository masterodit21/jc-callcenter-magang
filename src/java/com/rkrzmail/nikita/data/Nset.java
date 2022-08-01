package com.rkrzmail.nikita.data;
/**
 * created by 13k.mail@gmail.com
 */
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.rkrzmail.nikita.utility.Utility;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Rama Nset v2.0
 */
public class Nset {
    protected Object masterdata ;
    
    protected Nset(){
    }
    
    public Nset(Object data){
        if (data instanceof Nset) {
            masterdata = ((Nset)data).getInternalObject();
        }else{
            masterdata = data;
        }
    }
 
    public Nset getData(){
        return this;
    }
    public Object getInternalObject(){
        return masterdata;
    }
    private Nset get(int stream){
        return getData(stream);
    }
    public Nset get(String stream){
        
        if (stream.startsWith("[")&&stream.endsWith("]")) {
            if (stream.contains(",")||stream.contains("\"")) {
                Nset n = Nset.readJSON(stream);
                Nset out = getData();
                
                for (int i = 0; i < n.getArraySize(); i++) {
                    
                    if (Utility.isNumeric(n.getData(i).toString())) {
                        out= out.getData(n.getData(i).toInteger());
                    }else if (n.getData(i).isNumber()) {
                        out=out.getData(n.getData(i).toInteger());
                    }else{
                        out=out.getData(n.getData(i).toString());
                    }                  
                }
                return out;
            }else{
                String string =stream.substring(1,stream.length()-1);
                if (Utility.isNumeric(string)) {
                    return getData(Utility.getInt(string));
                }else{
                    return getData(string);
                }
            }
        }else{
            if (Utility.isNumeric(stream)) {
                return getData(Utility.getInt(stream));
            }
        }
        return getData(stream);
    }
            
    public Nset getData(String key){
        if (masterdata instanceof Hashtable) {
            Object data = ((Hashtable)masterdata).get(key);
            return new Nset(data);
        }
        return new Nset();
    }
    public boolean containsKey(String key){
    	if (masterdata instanceof Hashtable) {
    		return ((Hashtable)masterdata).containsKey(key);
    	}
    	return false;
    }
    public boolean containsValue(String value){
    	if (masterdata instanceof Hashtable) {
            String[] keys = getObjectKeys();
            for (int i = 0; i < keys.length; i++) {
                if (getData(keys[i]).toString().equalsIgnoreCase(value)) {                    
                    return true;
                }
            }
    	}
        if (masterdata instanceof Vector) {
            for (int i = 0; i < getArraySize(); i++) {
                if (getData(i).toString().equalsIgnoreCase(value)) {                    
                    return true;
                }                
            }    		 
    	}
    	return false;
    }
    public void removeByKey(String key){
        if (masterdata instanceof Hashtable) {
            ((Hashtable)masterdata).remove(key);
    	}
    }
    public void removeByIndex(int index){
        if (masterdata instanceof Vector) {
    		((Vector)masterdata).removeElementAt(index);
    	}
    }
    public Nset getData(int index){
        if (masterdata instanceof Vector) {
            if (((Vector)masterdata).size()>index) {
                Object data = ((Vector)masterdata).elementAt(index);
                return new Nset(data); 
            }
        }
        return new Nset();
    }   
    public int getSize(){
        if (masterdata instanceof Vector) {
            return ((Vector)masterdata).size();
        }
        if (masterdata instanceof Hashtable) {
            return  ((Hashtable)masterdata).size();
        }
        return 0;
    }
    public int getArraySize(){
        if (masterdata instanceof Vector) {
            return ((Vector)masterdata).size();
        }
        return -1;
    }
    public String[] getObjectKeys(){
         if (masterdata instanceof Hashtable) {
            Enumeration hdata = ((Hashtable)masterdata).keys();
            int i =0;while (hdata.hasMoreElements()) {hdata.nextElement();i++;}
            hdata = ((Hashtable)masterdata).keys();
            String[] rString = new String[i];i=0;
            while (hdata.hasMoreElements()) {                 
                 rString[i] = (String)hdata.nextElement();i++;
            }            
            return rString;
        }
        return new String[]{};
    }
    public String toString(){
        if (masterdata instanceof String) {
            return (String)masterdata;
        }else if (masterdata instanceof Vector) {
            return ((Vector)masterdata).toString();
        }else if (masterdata instanceof Hashtable) {
            return ((Hashtable)masterdata).toString();
        }else if (masterdata instanceof JsonPrimitive) {
            return ((JsonPrimitive)masterdata).getAsString();
        }
        return "";
    }
    public long toLong(){
        try {
            if (masterdata instanceof JsonPrimitive) {
                return ((JsonPrimitive)masterdata).getAsLong();
            }
            return Long.parseLong(toString());
        } catch (Exception e) {}
        return 0;
    }
    public boolean toBoolean(){
        try {
            if (masterdata instanceof JsonPrimitive) {
                return ((JsonPrimitive)masterdata).getAsBoolean();
            }
            return Boolean.parseBoolean(toString());
        } catch (Exception e) {}
        return false;
    }
    public int toInteger(){
        try {
            if (masterdata instanceof JsonPrimitive) {
                return ((JsonPrimitive)masterdata).getAsInt();
            }
            return Integer.parseInt(toString());
        } catch (Exception e) {}
        return 0;
    }
    public double toDouble(){
        try {
            if (masterdata instanceof JsonPrimitive) {
                return ((JsonPrimitive)masterdata).getAsDouble();
            }
            return Double.parseDouble(toString());
        } catch (Exception e) {}
        return 0;
    } 
    public Number toNumber(){
        try {
            if (masterdata instanceof JsonPrimitive) {
                return ((JsonPrimitive)masterdata).getAsNumber();
            }
            return Double.parseDouble(toString());
        } catch (Exception e) {}
        return 0;
    } 
    public boolean isNumber(){
         try {
            if (masterdata instanceof JsonPrimitive) {
                return ((JsonPrimitive)masterdata).isNumber();
            }
            return false;
        } catch (Exception e) {}
        return false;
    }
    public boolean isBoolean(){
         try {
            if (masterdata instanceof JsonPrimitive) {
                return ((JsonPrimitive)masterdata).isBoolean();
            }
            return false;
        } catch (Exception e) {}
        return false;
    }
     
    public boolean isNsetArray(){
        try {
            if (masterdata instanceof Nset) {
                return ((Nset)masterdata).isNsetArray();
            }else if (masterdata instanceof Vector) {
                return true;
            }else if (masterdata instanceof Hashtable) {
                return false;
            }
            return false;
        } catch (Exception e) {}
        return false;
    }
    public boolean isNsetObject(){
        try {
            if (masterdata instanceof Nset) {
                return ((Nset)masterdata).isNsetObject();
            }else if (masterdata instanceof Vector) {
                return false;
            }else if (masterdata instanceof Hashtable) {
                return true;
            }
            return false;
        } catch (Exception e) {}
        return false;
    }
    public boolean isNset(){
        try {
            if (masterdata instanceof Nset) {
                return true;
            }else if (masterdata instanceof Vector) {
                return true;
            }else if (masterdata instanceof Hashtable) {
                return true;
            }
            return false;
        } catch (Exception e) {}
        return false;
    }
    public Nset clone(){
        return Nset.readJSON(this.toJSON());
    }
    public static Nset newArray(){
        return new Nset(new Vector());
    }
    public static Nset newObject(){
        return new Nset(new Hashtable());
    }
    public static Nset newNull(){
        return new Nset(null);
    }    
    public Nset setData(String key, Nset data){
        if (masterdata instanceof Hashtable) {
            ((Hashtable)masterdata).put(key, data.masterdata);
        }else if(masterdata==null){
            masterdata=Nset.newObject().setData(key, data).masterdata;
        }
        return this;
    }
    public Nset setData(String key, Vector data){
        if (masterdata instanceof Hashtable) {
            ((Hashtable)masterdata).put(key, data);
        }else if(masterdata==null){
            masterdata=Nset.newObject().setData(key, data).masterdata;
        }
        return this;
    }
    public Nset setData(String key, Hashtable data){
        if (masterdata instanceof Hashtable) {
            ((Hashtable)masterdata).put(key, data);
        }else if(masterdata==null){
            masterdata=Nset.newObject().setData(key, data).masterdata;
        }
        return this;
    }
    private Nset setData(String key, JsonPrimitive data){
        if (masterdata instanceof Hashtable) {
            ((Hashtable)masterdata).put(key, data);
        }else if(masterdata==null){
            masterdata=Nset.newObject().setData(key, data).masterdata;
        }
        return this;
    }
    public Nset setData(String key, String data){
        return setData(key, new JsonPrimitive(data));     
    }
    public Nset setData(String key, boolean data){
        return setData(key, new JsonPrimitive(data));
    }
    public Nset setData(String key, int data){
        return setData(key, new JsonPrimitive(data));
    }
    public Nset setData(String key, double data){
        return setData(key, new JsonPrimitive(data));
    }
    public Nset setData(String key, long data){
        return setData(key, new JsonPrimitive(data));
    }
    
    public Nset addData(Nset data){
        if (masterdata instanceof Vector) {
            ((Vector)masterdata).addElement(data.masterdata);
        }else if(masterdata==null){
            masterdata=Nset.newArray().addData(data).masterdata;
        }
        return this;
    }
    public Nset addData(Vector data){
        if (masterdata instanceof Vector) {
            ((Vector)masterdata).addElement(data);
        }else if(masterdata==null){
            masterdata=Nset.newArray().addData(data).masterdata;
        }
        return this;
    }
    public Nset addData(Hashtable data){
        if (masterdata instanceof Vector) {
            ((Vector)masterdata).addElement(data);
        }else if(masterdata==null){
            masterdata=Nset.newArray().addData(data).masterdata;
        }
        return this;
    }
    private Nset addData(JsonPrimitive data){
        if (masterdata instanceof Vector) {
            ((Vector)masterdata).addElement(data);
        }else if(masterdata==null){
            masterdata=Nset.newArray().addData(data).masterdata;
        }
        return this;
    }
    public Nset addData(String data){
         return addData(new JsonPrimitive(data));
    }
    public Nset addData(boolean data){
         return addData(new JsonPrimitive(data));
    }
    public Nset addData(int data){
         return addData(new JsonPrimitive(data));
    }
    public Nset addData(long data){
         return addData(new JsonPrimitive(data));
    }
    public Nset addData(double data){
         return  addData(new JsonPrimitive(data));
    }   
    public void toJSON(final Writer writer){
         toJSON(new OutputStream(){
             public void write(int b) throws IOException {
                 writer.write(b);
             }             
         });
    }
    public synchronized void toJSON(OutputStream outputStream){
        List overflowOutJson = new ArrayList();
        JsonWriter jsonWriter = new  JsonWriter(new OutputStreamWriter(outputStream));
        Object dataObject = masterdata;
        try {
            if (dataObject == null) {
                //nothing 
            }else if (dataObject instanceof  Vector) {
                OutJsonArray(overflowOutJson, jsonWriter, (Vector)dataObject);
            }else if (dataObject instanceof  Hashtable) {
                OutJsonObject(overflowOutJson, jsonWriter, (Hashtable)dataObject);
            }
            jsonWriter.flush();
        } catch (Exception e) { e.printStackTrace(); System.err.println(e.getMessage());}   
    }
    
    public String toJSON(){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4096); 
        JsonWriter jsonWriter = new  JsonWriter(new OutputStreamWriter(byteArrayOutputStream));
        toJSON(byteArrayOutputStream);
        return new String( byteArrayOutputStream.toByteArray());
    }
    public String toCsv(){
        StringBuffer stringBuffer = new StringBuffer();                
        for (int i = 0; i < this.getArraySize(); i++) {
            if ( getData(i).isNset() ) {
                stringBuffer.append(i>=1?"\r\n":"");           
                for (int j = 0; j < getData(i).getArraySize(); j++) {
                    stringBuffer.append(j>=1?";":"").append(getData(i).getData(i).toString());
                } 
            }else{
                stringBuffer.append(i>=1?",":"").append(getData(i).toString());;           
            }        
        }                
        return stringBuffer.toString();
    }
    public static Nset readflattoTree(Nset s){
        //flat {'id':'','text':'','url':'','parent':'idparent'}
        //tree [{'id':'','text':'','url':'','childs':[]},...]
        return s;
    }
    
    public static Nset readsplitString(String s){
        return readsplitString(s, "|");
    }
    public static Nset readsplitString(String s, String separator){
        String original=s; 
        Vector<String> nodes = new Vector<String>();
        int index = original.indexOf(separator);
        while (index >= 0) {
                nodes.addElement(original.substring(0, index));
                original = original.substring(index + separator.length());
                index = original.indexOf(separator);
        }
        nodes.addElement(original);
	 
        return new Nset(nodes);
    }
    public static Nset readJSON(InputStream is){
        Object dataObject; 
        try {  
            JsonReader reader = new JsonReader(new InputStreamReader(  is ));
            //reader.setLenient(true);
            if (reader.peek().equals(JsonToken.BEGIN_ARRAY)) {
                  dataObject=JsonArray(reader);
            }else if (reader.peek().equals(JsonToken.BEGIN_OBJECT)) {
                  dataObject=JsonObject(reader);
            } else{
                  dataObject = "";
            }          
            return new Nset(dataObject);
        } catch (Exception ex) { ex.printStackTrace(); }
        return new Nset();
    }
    
    public static Nset readJSON(String json){
        Object dataObject; 
        try {  
            JsonReader reader = new JsonReader(new InputStreamReader( new ByteArrayInputStream(json.getBytes()) ));
            if (reader.peek().equals(JsonToken.BEGIN_ARRAY)) {
                  dataObject=JsonArray(reader);
            }else if (reader.peek().equals(JsonToken.BEGIN_OBJECT)) {
                 dataObject=JsonObject(reader);
            }else{
                  dataObject = "";  
            }            
           return new Nset(dataObject);
        } catch (Exception ex) { }
        return new Nset();
    }     
    public static Nset readJSON(String json, boolean singlequote){
        if (singlequote) {
            return Nset.readJSON(json.replaceAll("\'", "\""));
        }
        return Nset.readJSON(json);
    }
    private static void OutJsonArray(List overflowOutJson, JsonWriter jsonWriter, Vector vector) throws IOException{             
        //overflowOutJson.add(vector.hashCode());
        jsonWriter.beginArray();
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
               
                Object v = enumeration.nextElement();
                //if (!overflowOutJson.contains(v.hashCode())  ) {
                    OutJsonValue(overflowOutJson, jsonWriter,  v);
                //}  
               
            }
            jsonWriter.endArray();
    }
    private static void OutJsonObject(List  overflowOutJson, JsonWriter jsonWriter,  Hashtable hashtable) throws IOException{
        //overflowOutJson.add(hashtable.hashCode() );    
        jsonWriter.beginObject();
            Enumeration enumeration = hashtable.keys();
            while (enumeration.hasMoreElements()) {
                String key = (String)enumeration.nextElement();
                jsonWriter.name(key);
                Object v = hashtable.get(key);
                //if (!overflowOutJson.contains(v.hashCode() )  ) {
                    OutJsonValue(overflowOutJson, jsonWriter,  v);
               //}
                     //System.out.println(v.toString());
            }
            jsonWriter.endObject();
    }
    private  static void OutJsonValue(List overflowOutJson, JsonWriter jsonWriter, Object object) throws IOException{
        if (object == null) {
            String nil = null;
            jsonWriter.value(nil);
        }else if (object instanceof Nset) {
            OutJsonValue(overflowOutJson, jsonWriter, ( (Nset)object).getInternalObject());            
        }else if (object instanceof Hashtable) {
            OutJsonObject(overflowOutJson, jsonWriter, (Hashtable)object);
        }else if (object instanceof Vector) {
            OutJsonArray(overflowOutJson, jsonWriter, (Vector)object);
        }else if (object instanceof String) {    
            jsonWriter.value((String)object);
        }else if (object instanceof JsonPrimitive) {    
            JsonPrimitive obj = (JsonPrimitive)object;
            if (obj.isBoolean()) {
                jsonWriter.value(obj.getAsBoolean());
            }else if (obj.isNumber()) {
                jsonWriter.value(obj.getAsNumber());
            }else{
                jsonWriter.value(obj.getAsString());
            }    
        }else if (object instanceof Number) {  
            jsonWriter.value((Number)object);
        }else if (object instanceof Boolean) {  
            jsonWriter.value(((Boolean)object).booleanValue());
        }else if (object instanceof NullPointerException) {
            String nil = null;
            jsonWriter.value(nil); 
        }else{
            ///String nil = null;
            jsonWriter.value(String.valueOf(object)); 
        }
    }        
    private static Object JsonArray(JsonReader reader) throws IOException{
        Vector vector = new Vector();
        reader.beginArray();
        while (reader.hasNext()) {
              vector.addElement(JsonValue(reader));
        }              
        reader.endArray();
        return vector;
    }
    private static Object JsonObject(JsonReader reader) throws IOException{
        Hashtable hashtable = new Hashtable();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = JsonMember(reader);
            hashtable.put(name, JsonValue(reader));
        }
        reader.endObject();
        return hashtable;
    }
    private static String JsonMember(JsonReader reader) throws IOException{
        if (reader.peek().equals(JsonToken.NAME)) {
            return reader.nextName();
        } 
        return "";//must error
    }    
    
    private static Object JsonValue(JsonReader reader) throws IOException{
        if (reader.peek().equals(JsonToken.BEGIN_ARRAY)) {
            return JsonArray(reader);
        }else if (reader.peek().equals(JsonToken.BEGIN_OBJECT)) {
            return JsonObject(reader);
        }else if (reader.peek().equals(JsonToken.STRING)) {
            return reader.nextString();
        }else if (reader.peek().equals(JsonToken.NUMBER)) {
            String s = reader.nextString();
            if (s.contains(".")||s.contains(",")) {
                return new JsonPrimitive(Utility.getDouble(s));
            }
            return new JsonPrimitive(Utility.getLong(s));
        }else if (reader.peek().equals(JsonToken.BOOLEAN)) {
            return new JsonPrimitive(reader.nextBoolean());
        }else if (reader.peek().equals(JsonToken.NULL)) {
            reader.nextNull();
        }else{
            return reader.nextString();
        }    
        return "";
    }
    public String printObjectModel(){
       return  printObjectModel(getObjectKeys());
    }
    public String printObjectModel(String...buff){
        StringBuffer stringBuffer = new StringBuffer();
       
        for (int i = 0; i < buff.length; i++) {
            String string = buff[i];
            stringBuffer.append("    public static String ").append(buff[i].toUpperCase()).append(" = ").append("\"").append(buff[i]).append("\";").append("\r\n");
        }
        return stringBuffer.toString();
    }
    public String printObjectModelSetGet(){
         return  printObjectModelSetGet(getObjectKeys());
    }
    public String printObjectModelSetGetWithPrefix(String...buff){
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < buff.length; i++) {
            String string = buff[i];
            stringBuffer.append("    private ").append(getPrefix(string,false)).append(" ").append(buff[i]).append(";").append("\r\n");
        }
        stringBuffer.append("\r\n");
        for (int i = 0; i < buff.length; i++) {
            String string = buff[i];
            String name = string.substring(getPrefix(string, true).length());
            stringBuffer.append("    public void set").append(name).append("(").append(getPrefix(string,false)).append("  s){\r\n").append("        ").append(buff[i]).append("=s;").append("\r\n    }\r\n");
            stringBuffer.append("    public ").append(getPrefix(string,false)).append(" get").append(name).append("()").append("{").append("\r\n").append("        return ").append(buff[i]).append(";\r\n    }").append("\r\n");
        }
        return stringBuffer.toString();
    }
    private String getPrefix(String string, boolean k){
        String val;String key;
        if (string.startsWith("sa")) {
            key = "sa";
            val =  "String[]";   
        }else if (string.startsWith("bya")) {
            key = "bya";
            val =  "byte[]";   
        }else if (string.startsWith("s")) {
            key = "s";
            val =  "String";   
        }else if (string.startsWith("i")) {
            key =  "i";
            val =  "int";   
        }else if (string.startsWith("l")) {
            key = "l";
            val = "long";
        }else if (string.startsWith("d")) {
            key = "d";
            val = "double";   
        }else if (string.startsWith("b")) {
            key = "b";
            val = "boolean";   
        }else if (string.startsWith("by")) {
            key = "by";
            val = "byte";   
        }else if (string.startsWith("v")) {
            key = "v";
            val = "Vector";   
        }else if (string.startsWith("h")) {
            key = "h";
            val = "Hashtable";   
        }else{
            key = "";
            val = "String";
        }
        return k?key:val;    
    }
    public String printObjectModelSetGet(String...buff){
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < buff.length; i++) {
            String string = buff[i];
            stringBuffer.append("    private String ").append(buff[i]).append(";").append("\r\n");
        }
        for (int i = 0; i < buff.length; i++) {
            String string = buff[i];
            stringBuffer.append("    public void set").append(buff[i]).append("(String s){\r\n").append("        ").append(buff[i]).append("=s;").append("\r\n    }\r\n");
            stringBuffer.append("    public String get").append(buff[i]).append("()").append("{").append("\r\n").append("        return ").append(buff[i]).append(";\r\n    }").append("\r\n");
        }
        return stringBuffer.toString();
    }
    public Nset copyNikitasetAtCol(int...col){
        Vector<Vector<String>> copy = new Vector<Vector<String>>();
        for (int row = 0; row < getArraySize(); row++) {
            for (int j = 0; j < col.length; j++) {
                Vector<String> vector = new Vector<String>();
                vector.addElement(getData(row).getData(col[j]).toString());
                copy.addElement(vector);
            }            
        }        
        return new Nset(copy);
    }
    public Nset copyNikitasettoObjectCol(String...id){
        Vector<Hashtable<String, String>> copy = new Vector<Hashtable<String, String>>();
        for (int row = 0; row < getArraySize(); row++) {
            Hashtable<String, String> object = new Hashtable<String, String>();
            for (int j = 0; j < id.length; j++) {                
                if (!id[j].equals("")) {
                    object.put(id[j], getData(row).getData(j).toString());
                } 
            }  
            copy.addElement(object);
        }        
        return new Nset(copy);
    }
}

