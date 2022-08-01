package com.rkrzmail.nikita.data;
/**
 * created by 13k.mail@gmail.com
 */

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.rkrzmail.nikita.utility.Utility;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author Rama Nset v2.0
 */
public class NsetV2 extends Nset{
    protected Object masterdata ;
    
    protected NsetV2(){        
    }    
    
    public NsetV2(Object data){
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
                    if (n.getData(i).isNumber()) {
                        out=out.getData(n.getData(i).toInteger());
                    }else{
                        out=out.getData(n.getData(i).toString());
                    }                  
                }
                return out;
            }else{
                String string = stream.substring(1,stream.length()-1);
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
                if (getData(keys[i]).toString().equals(value)) {                    
                    return true;
                }
            }
    	}
        if (masterdata instanceof Vector) {
            for (int i = 0; i < getArraySize(); i++) {
                if (getData(i).toString().equals(value)) {                    
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
        return -1;//???
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
        return String.valueOf(masterdata);
    }
    public long toLong(){
        return Utility.getNumber(masterdata).longValue();
    }
    public boolean toBoolean(){
        if (masterdata instanceof Boolean) {
            return ((Boolean)masterdata).booleanValue();
        }
        return false;
    }
    public int toInteger(){
        return Utility.getNumber(masterdata).intValue();
    }
    public double toDouble(){
        return Utility.getNumber(masterdata).doubleValue();
    } 
    public Number toNumber(){
        if (masterdata instanceof Number) {
            return ((Number)masterdata);
        }
        return 0;
    } 
    public boolean isNumber(){
        return masterdata instanceof Number;
    }
    public boolean isBoolean(){
        return masterdata instanceof Boolean;
    }     
    public boolean isNsetArray(){
        if (masterdata instanceof Nset) {
            return ((Nset)masterdata).isNsetArray();
        }else if (masterdata instanceof Vector) {
            return true;
        }
        return false;
    }
    public boolean isNsetObject(){
        if (masterdata instanceof Nset) {
            return ((Nset)masterdata).isNsetObject();
        }else if (masterdata instanceof Hashtable) {
            return true;
        }
        return false;
    }
    public boolean isNset(){
        if (masterdata instanceof Nset) {
            return true;
        }else if (masterdata instanceof Vector) {
            return true;
        }else if (masterdata instanceof Hashtable) {
            return true;
        }
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
        return setDataInternal(key, data.masterdata);
    }
    public Nset setData(String key, Vector data){
        return setDataInternal(key, data);
    }
    public Nset setData(String key, Hashtable data){         
        return setDataInternal(key, data);
    }
    private Nset setDataInternal(String key, Object data){
        if (masterdata instanceof Hashtable) {
            ((Hashtable)masterdata).put(key, data);
        }else if(masterdata == null){
            //masterdata = Nset.newObject().setDataInternal(key, data).masterdata;
        }
        return this;
    }
    public Nset setData(String key, String data){
        return setDataInternal(key, data);
    }
    public Nset setData(String key, boolean data){
        return setDataInternal(key, new Boolean(data));
    }
    public Nset setData(String key, int data){
        return setDataInternal(key, new Long(data));
    }
    public Nset setData(String key, double data){
        return setDataInternal(key, new Double(data));
    }
    public Nset setData(String key, long data){        
        return setDataInternal(key, new Long(data));
    }
    public Nset setDataNull(String key){        
        return setDataInternal(key, Nset.newNull());
    }
     
    public Nset addData(Nset data){
        return addDataInternal(data.masterdata); 
    }
    public Nset addData(Vector data){
       return addDataInternal(data);
    }
    public Nset addData(Hashtable data){
        return addDataInternal(data); 
    }
    private Nset addDataInternal(Object data){
        if (masterdata instanceof Vector) {
            ((Vector)masterdata).addElement(data);
        }else if(masterdata == null){
            //masterdata=Nset.newArray().addDataInternal(data).masterdata;
            
        }
        return this;
    }
    public Nset addData(String data){
        return addDataInternal(data);
    }
    public Nset addData(boolean data){
        return addDataInternal(new Boolean(data));
    }
    public Nset addData(int data){
        return addDataInternal(new Integer(data));
    }
    public Nset addData(long data){
        return addDataInternal(new Long(data));
    }
    public Nset addData(double data){         
       return addDataInternal(new Double(data));
    }   
    public Nset addDataNull(double data){         
       return addDataInternal(Nset.newNull());
    }     
    
    public Nset insertData(int index, Nset data){
        return insertDataInternal(index, data.masterdata); 
    }
    public Nset insertData(int index, Vector data){
       return insertDataInternal(index, data);
    }
    public Nset insertData(int index, Hashtable data){
        return insertDataInternal(index, data); 
    }
    private Nset insertDataInternal(int index, Object data){
        if (masterdata instanceof Vector) {
            try {
                ((Vector)masterdata).insertElementAt(data, index);
            } catch (Exception e) {  }
        }else if(masterdata == null){
            //masterdata=Nset.newArray().addDataInternal(data).masterdata;
        }
        return this;
    }
    public Nset insertData(int index, String data){
        return insertDataInternal(index, data);
    }
    public Nset insertData(int index, boolean data){
        return insertDataInternal(index, new Boolean(data));
    }
    public Nset insertData(int index, int data){
        return insertDataInternal(index, new Integer(data));
    }
    public Nset insertData(int index, long data){
        return insertDataInternal(index, new Long(data));
    }
    public Nset insertData(int index, double data){         
       return insertDataInternal(index, new Double(data));
    }   
    public Nset insertDataNull(int index, double data){         
       return insertDataInternal(index, Nset.newNull());
    }
    
    public void toJSON(Writer writer){
        JsonWriter jsonWriter = new  JsonWriter(writer);
        Object dataObject = masterdata;
        try {
            if (dataObject == null) {
                //nothing                
            }else if (dataObject instanceof  Vector) {
                OutJsonArray(jsonWriter, (Vector)dataObject);
            }else if (dataObject instanceof  Hashtable) {
                OutJsonObject(jsonWriter, (Hashtable)dataObject);
            }
            jsonWriter.flush();
        } catch (Exception e) { }   
    }
    public void toJSON(OutputStream outputStream){
        toJSON(new OutputStreamWriter(outputStream));               
    }
    
    public String toJSON(){
        StringWriter writer = new StringWriter( );
        toJSON(writer);
        return writer.toString();
        /*
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(4096); 
        JsonWriter jsonWriter = new  JsonWriter(new OutputStreamWriter(byteArrayOutputStream));
        toJSON(byteArrayOutputStream);
        return new String( byteArrayOutputStream.toByteArray());
        */
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
    private static void OutJsonArray(JsonWriter jsonWriter, Vector vector) throws IOException{
            jsonWriter.beginArray();
            Enumeration enumeration = vector.elements();
            while (enumeration.hasMoreElements()) {
               OutJsonValue(jsonWriter,  enumeration.nextElement());
            }
            jsonWriter.endArray();
    }
    private static void OutJsonObject(JsonWriter jsonWriter,  Hashtable hashtable) throws IOException{
            jsonWriter.beginObject();
            Enumeration enumeration = hashtable.keys();
            while (enumeration.hasMoreElements()) {
                String key = (String)enumeration.nextElement();
                jsonWriter.name(key);
                OutJsonValue(jsonWriter,  hashtable.get(key));
            }
            jsonWriter.endObject();
    }
    private  static void OutJsonValue(JsonWriter jsonWriter, Object object) throws IOException{
        if (object == null||object instanceof NullPointerException) {
            String nil = null;
            jsonWriter.value(nil);
        }else if (object instanceof Nset) {
            OutJsonValue(jsonWriter, ( (Nset)object).getInternalObject());            
        }else if (object instanceof Hashtable) {
            OutJsonObject(jsonWriter, (Hashtable)object);
        }else if (object instanceof Vector) {
            OutJsonArray(jsonWriter, (Vector)object);
        }else if (object instanceof String) {    
            jsonWriter.value((String)object);
        }else if (object instanceof Number) {  
            jsonWriter.value((Number)object);
        }else if (object instanceof Boolean) {  
            jsonWriter.value(((Boolean)object).booleanValue());
        }else{
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
            if (Utility.isLongIntegerNumber(s)) {
                return Utility.getLong(s);
            }else{
                return Utility.getDouble(s);
            }            
        }else if (reader.peek().equals(JsonToken.BOOLEAN)) {
            return new Boolean(reader.nextBoolean());
        }else if (reader.peek().equals(JsonToken.NULL)) {
            reader.nextNull();
            return Nset.newNull();
        }else{
            return reader.nextString();
        }    
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

