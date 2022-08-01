package com.rkrzmail.nikita.utility;

import com.nikita.generator.NikitaResponse;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.web.utility.WebUtility;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringEscapeUtils;
 
 
 

 /**
 * created by 13k.mail@gmail.com
 */
public class Utility {

    static PrivateKey getPrivate(byte[] hexToBytes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public interface NikitaParse{
        public Object getVirtual(String name);
    }
    public static String nhtmlNikitaParse(String html, NikitaParse np) {
        StringBuilder sb = new StringBuilder(); 
        StringTokenizer stringTokenizer = new StringTokenizer(html, "{}", true);
        while (stringTokenizer.hasMoreElements()) {
            String nhtml = String.valueOf(stringTokenizer.nextElement());
            String vArgs = nhtml.trim();
            if ((vArgs.startsWith("@")||vArgs.startsWith("$")||vArgs.startsWith("!")) ) {                
                if ( (vArgs.contains("(")||vArgs.contains(")")||vArgs.contains("[")||vArgs.contains("]")) ) {
                    if (vArgs.endsWith(")")||vArgs.endsWith("]")) {                        
                        vArgs = "";
                    }else{
                        //salah
                        vArgs = "error";
                    }
                }else{
                    vArgs = "";
                }
                if (vArgs.equals("")  && sb.toString().endsWith("{")) {
                    if (stringTokenizer.hasMoreElements()) {
                        String vhtml = String.valueOf(stringTokenizer.nextElement());
                        if (vhtml.equals("}")) {
                            if (np!=null) {
                                sb.deleteCharAt(sb.length()-1);
                                sb.append(String.valueOf(np.getVirtual(nhtml)));
                            }else{
                                sb.append("");
                            }
                            
                        }else{
                            sb.append(nhtml);
                            sb.append(vhtml);
                        }                        
                    }else{
                        sb.append(nhtml);
                    }                    
                }else{
                    sb.append(nhtml);
                }           
            }else{
                sb.append(nhtml);
            }
        }
        return sb.toString();
    }
     
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "KMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    public static String readInputStreamAsString(String inputStreamPath) throws IOException {
        InputStream inputStream = new DataInputStream(new FileInputStream(inputStreamPath));
        return  readInputStreamAsString(inputStream);
    }
    public static String readInputStreamAsString(InputStream inputStream ) throws IOException {        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        inputStream.close();
        return baos.toString();
    }
    public static String[] getDataArray(Vector nodes) {	 
		String[] result = new String[nodes.size()];
		if (nodes.size() > 0) {
			for (int loop = 0; loop < nodes.size(); loop++) {
				result[loop] = String.valueOf(nodes.elementAt(loop)) ;
			}
		}
		return result;
	}
    public static String[] getObjectKeys(Hashtable masterdata){
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
    
    public static String[] getObjectKeys(Enumeration hdata){
        if (hdata !=null) {
           Vector<String> v = new Vector<String>();
           while (hdata.hasMoreElements()) {                 
                v.addElement((String)hdata.nextElement()); 
           }   
            String[] rString = new String[v.size()];
            for (int i = 0; i < v.size(); i++) {
                rString[i] = v.get(i);                
            }
           
           return rString;
       }
       return new String[]{};
   }
    public static boolean extrackZip( InputStream in, String path){
        try{     
            path = path + ((path.endsWith("/")||path.endsWith("\\"))?"":System.getProperty("file.separator"));
            ZipInputStream zis =   new ZipInputStream(in);
            ZipEntry ze = zis.getNextEntry();
            while(ze!=null){

                byte[] buffer = new byte[1024];
                int length; 

                //create folder
                String sfzip = path+ze.getName();
                if (ze.isDirectory()) {
                    new File(sfzip).mkdirs();
                }else if (ze.getName().contains("/")) {
                    String fname = ze.getName().substring(0, ze.getName().lastIndexOf("/"));
                    fname = fname.replace("/", System.getProperty("file.separator"));
                    sfzip = path+ze.getName().replace("/", System.getProperty("file.separator"));

                    new File(path+fname).mkdirs();

                    OutputStream out = new FileOutputStream(sfzip); 
                    while ((length = zis.read(buffer)) > 0) {
                        out.write( buffer, 0, length );
                    } 
                    out.flush();
                    out.close();

                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

            return true;
        }catch(IOException ex){ }
        return false;
    }
        public static String unZip(String str){
            if (str == null || str.length() == 0) {
                       return str;
            }
            ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes());
            GZIPInputStream gzip;
            try {
                StringBuffer stringBuffer = new StringBuffer();
                gzip = new GZIPInputStream(in);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = gzip.read(buffer)) > 0) {
                    stringBuffer.append(  new String(buffer, 0, length) );
                }
                gzip.close();
                return stringBuffer.toString();
            } catch (IOException ex) {  }
            return str;
        }
        
        public static String unZipN(String str){
             /*
            try{
                ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("UTF-8"));
                ByteArrayOutputStream out = new ByteArrayOutputStream(); 
                
                ZipInputStream zis =   new ZipInputStream(in);
                ZipEntry ze = zis.getNextEntry();
                while(ze!=null){
                    if (ze.getName().equals("N")) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            out.write( buffer, 0, length );
                        } 
                    }
                    ze = zis.getNextEntry();
                }
                zis.closeEntry();
                zis.close();

                return out.toString("UTF-8");//defaut charset
            }catch(IOException ex){ }
             */
            return str;            
        }
        public static byte[] toGzip(String str){
            if (str == null || str.length() == 0) {
                return new byte[0];
            }
            return toGzip(str.getBytes());
        }
        public static byte[] toGzip(byte[] str){
            if (str == null || str.length == 0) {
                return new byte[0];
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip;
            try {
                
                gzip = new GZIPOutputStream(out);
                gzip.write(str);
                gzip.close();
                return out.toByteArray();
            } catch (IOException ex) {  }
            return new byte[0];
        }
        public static byte[] toDeflater(String str){
            if (str == null || str.length() == 0) {
                return new byte[0];
            }
            return toDeflater(str.getBytes());
        }
        public static byte[] toDeflater(byte[] str){
            if (str == null || str.length== 0) {
                return new byte[0];
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DeflaterOutputStream gzip;
            try {
                gzip = new DeflaterOutputStream(out);
                gzip.write(str);
                gzip.close();
                return out.toByteArray();
            } catch (IOException ex) {  }
            return new byte[0];
        }  
        
        public static String toZipN(String str){
             /*
            try{
                ByteArrayOutputStream out = new ByteArrayOutputStream(); 
                ZipOutputStream zos = new ZipOutputStream(out);
                zos.setLevel(7);
                    ZipEntry ze= new ZipEntry( "N" );
             
                    zos.putNextEntry(ze);                     
                    zos.write(str.getBytes());
                    zos.closeEntry();                 
                zos.close();
                return out.toString();//Utility.encodeBase64(  out.toString("UTF-8")  );
           }catch(IOException ex){}
           */
            
           return str;   
        }
	public static String toZip(String str){
            if (str == null || str.length() == 0) {
                return str;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPOutputStream gzip;
            try {
                gzip = new GZIPOutputStream(out);
                gzip.write(str.getBytes());
                gzip.close();
                return out.toString();
            } catch (IOException ex) {  }
            
            
            /*
            byte[] buffer = new byte[1024];
            
            try{

                    FileOutputStream fos = new FileOutputStream("C:\\MyFile.zip");
                          ZipOutputStream zos = new ZipOutputStream(fos);
                          ZipEntry ze= new ZipEntry("spy.log");
                    zos.putNextEntry(ze);
                    FileInputStream in = new FileInputStream("C:\\spy.log");

                    int len;
                    while ((len = in.read(buffer)) > 0) {
                            zos.write(buffer, 0, len);
                    }

                    in.close();
                    zos.closeEntry();

                    //remember close it
                    zos.close();

                    System.out.println("Done");

            }catch(IOException ex){
               ex.printStackTrace();
            }
            */
            return str;
        }

	public static String getDate() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	 public static String urlEncode(String s){
                try {
                    return URLEncoder.encode(s,"UTF-8");
                } catch (Exception e) { }
                return  "";
        }
 
	public static String getDefaultPath() {
		 return null;
	}

	 

	public static String getDefaultPath(String fname) {
		return getDefaultPath() + fname;
	}

	 
	public static Vector<Vector<String>> splitVector(String original, String separatorcol, String separatorrow) {
		Vector<Vector<String>> nodes = new Vector<Vector<String>>();
		int index = original.indexOf(separatorrow);
		while (index >= 0) {
			nodes.addElement(splitVector(original.substring(0, index), separatorcol));
			original = original.substring(index + separatorrow.length());
			index = original.indexOf(separatorrow);
		}
		nodes.addElement(splitVector(original, separatorcol));
		return nodes;
	}

	public static Vector<String> splitVector(String original, String separator) {
		Vector<String> nodes = new Vector<String>();
		int index = original.indexOf(separator);
		while (index >= 0) {
			nodes.addElement(original.substring(0, index));
			original = original.substring(index + separator.length());
			index = original.indexOf(separator);
		}
		nodes.addElement(original);
		return nodes;
	}
        public static List<String> splitList(String original, String separator) {
            List<String> nodes = new ArrayList<String>();
            int index = original.indexOf(separator);
            while (index >= 0) {
                nodes.add(original.substring(0, index));
                original = original.substring(index + separator.length());
                index = original.indexOf(separator);
            }
            nodes.add(original);
            return nodes;
	}
        public static String concatVector(Vector<String> original, String separator, int start) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = start; i < original.size(); i++) {
                stringBuffer.append(separator).append(original.elementAt(i));
            }
            return stringBuffer.toString();
        }
	public static String[] split(String original, String separator) {
		List<String> nodes = splitList(original, separator);
		String[] result = new String[nodes.size()];
		if (nodes.size() > 0) {
                    for (int loop = 0; loop < nodes.size(); loop++) {
                        result[loop] = (String) nodes.get(loop);
                    }
		}		 
		return result;
	} 
	public static String getHttpConnection(String stringURL) {		 
		StringBuffer data = new StringBuffer();
		try {
                    URL url = new URL(stringURL);
                    URLConnection ucon = url.openConnection();
                    ucon.setConnectTimeout(25000);
                    InputStream is = ucon.getInputStream();

                    int current = 0;
                    while ((current = is.read()) != -1) {
                        data.append((char) current);
                    }
		} catch (Exception e) {
                    data.append("Connection Timeout");
		}		 
		return data.toString();
	}

        public static String right(String text, int len) {
		String numbers = text.length() <= len ? text : text.substring(text.length() - len);		 
		return numbers;
	}
	public static String repeat(String sString, int iTimes) {
		String output = "";
		for (int i = 0; i < iTimes; i++)
			output = output + sString;
		return output;
	}

	@SuppressWarnings("deprecation")
	public static String getURLenc(String... get) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < get.length; i++) {
			buffer.append((i % 2) == 0 ? get[i] : URLEncoder.encode(get[i]));
		}
		return buffer.toString();
	}
        public static String encodeURL(String u) {
            return URLEncoder.encode(u);
        }
        public static String escapeSQL(String u) {
            return  StringEscapeUtils.escapeSql(u) ;
        }
	 public static String decodeURL(String u) {
            return URLDecoder.decode(u) ;
        }
         public static String encodeBase64(String u) {
            return Base64Coder.encodeString(u);
        }
         public static String decodeBase64(String u) {
            return  Base64Coder.decodeString(u);
        }
	public static final String MD5(final String s) {
            if (!s.equals("")) { 
                try { 
                    MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
                    digest.update(s.getBytes());
                    byte messageDigest[] = digest.digest();

                    // Create Hex String
                    StringBuffer hexString = new StringBuffer();
                    for (int i = 0; i < messageDigest.length; i++) {
                            String h = Integer.toHexString(0xFF & messageDigest[i]);
                            while (h.length() < 2)
                                    h = "0" + h;
                            hexString.append(h);
                    }
                    return hexString.toString();
		} catch (NoSuchAlgorithmException e) { }        
            
            }        
            return "";
	}
        public static final String SHA1(final String s) {
            if (!s.equals("")) {
                try {
                    MessageDigest digest = java.security.MessageDigest.getInstance("SHA1");
                    digest.update(s.getBytes());
                    byte messageDigest[] = digest.digest();

                    StringBuffer hexString = new StringBuffer();
                    for (int i = 0; i < messageDigest.length; i++) {
                        hexString.append(Integer.toString((messageDigest[i] & 0xff) + 0x100, 16).substring(1));
                    }
                    return hexString.toString();
                } catch (NoSuchAlgorithmException e) { }                
            }            
            return "";
        }
        public static String hash256(String data) throws NoSuchAlgorithmException {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(data.getBytes());
            return bytesToHex(md.digest());
        }
        public static String bytesToHex(byte[] bytes) {
            StringBuffer result = new StringBuffer();
            for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
            return result.toString();
        }
        public static byte[] hexToBytes(char[] hex) {
            int length = hex.length / 2;
            byte[] raw = new byte[length];
            for (int i = 0; i < length; i++) {
              int high = Character.digit(hex[i * 2], 16);
              int low = Character.digit(hex[i * 2 + 1], 16);
              int value = (high << 4) | low;
              if (value > 127)
                value -= 256;
              raw[i] = (byte) value;
            }
            return raw;
          }

          public static byte[] hexToBytes(String hex) {
            return hexToBytes(hex.toCharArray());
          }

        public static String sha256(String base) {
            try{
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(base.getBytes("UTF-8"));
                StringBuffer hexString = new StringBuffer();

                for (int i = 0; i < hash.length; i++) {
                    String hex = Integer.toHexString(0xff & hash[i]);
                    if(hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }

                return hexString.toString();
            } catch(Exception ex){
               throw new RuntimeException(ex);
            }
        }
        public static final String Npassword(final String s) {
            return DigestUtils.sha256Hex(s)+DigestUtils.md5Hex(s);
        }
	 public static void ssssss(String[] args)throws Exception  {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream("c:\\loging.log");

            byte[] dataBytes = new byte[1024];

            int nread = 0; 
            while ((nread = fis.read(dataBytes)) != -1) {
              md.update(dataBytes, 0, nread);
            };
            byte[] mdbytes = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mdbytes.length; i++) {
              sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            System.out.println("Hex format : " + sb.toString());

           //convert the byte to hex format method 2
            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<mdbytes.length;i++) {
              hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
            }

            System.out.println("Hex format : " + hexString.toString());
        }   
    
	   
        public static long getTime(String time) {
            try {  
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");                
		return simpleDateFormat.parse(time).getTime() ;
            } catch (Exception e) {
                return 0;
            }
        }
        public static long getDateTime(String date) {
            if (isNumeric(date)) {
                return getLong(date);
            }
            try {  
                //dd/mm/yyyy|dd-mm-yyyy|yyyy-mm-dd
                String sd ="-";String time = "";
                if (date.contains(".")) {
                    date=date.substring(0,date.indexOf("."));
                }
                if (date.contains(":")&& date.length()>=18) {
                    time = " HH:mm:ss";
                }
                if (date.contains("-")) {
                    sd = "-";
                }else if (date.contains("/")) {
                    sd = "/";
                }
                if (date.length()>=10) {  
                    if (isNumeric(date.substring(0,4))) {
                        //yyyy-mm-dd
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy"+sd+"MM"+sd+"dd"+time);                
                        return simpleDateFormat.parse(date).getTime() ;
                    }else if (isNumeric(date.substring(6,10))) {   
                        //dd/mm/yyyy
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd"+sd+"MM"+sd+"yyyy"+time);                
                        return simpleDateFormat.parse(date).getTime() ;
                    }                  
                }else{
                    //???
                }
            } catch (Exception e) { }
            return 0;
        }
        public static long getDate(String date) {
            if (isNumeric(date)) {
                return getLong(date);
            }
            if (date.trim().length()>=10) {
                 return getDateTime(date.trim().substring(0,10)) ;
            }
            return getDateTime(date) ;
        }
        public static String getNumberPointOnly(String s) {
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                if ("01234567890.".indexOf(s.charAt(i)) != -1) {
                    buf.append(s.charAt(i));
                }
            }		 
            return buf.toString();
        } 
        public static String getNumberOnly(String s) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
                    if ("01234567890".indexOf(s.charAt(i)) != -1) {
                        buf.append(s.charAt(i));
                    }
		}		 
		return buf.toString();
	}
         public static boolean isValidPassword(String password)   {

                // Regex to check valid password.
                String regex = "^(?=.*[0-9])"
                               + "(?=.*[a-z])(?=.*[A-Z])"
                               + "(?=.*[@#$%^&+=])"
                               + "(?=\\S+$).{8,20}$";

                // Compile the ReGex
                Pattern p = Pattern.compile(regex);

                // If the password is empty
                // return false
                if (password == null) {
                    return false;
                }

                // Pattern class contains matcher() method
                // to find matching between given password
                // and regular expression.
                Matcher m = p.matcher(password);

                // Return if the password
                // matched the ReGex
                return m.matches();
            }
        public static boolean isAnEmail(String value) {
            boolean isAnEmail = false;
            return isAnEmail = value.trim().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        }
        public static String getStringAccept(String s, String sAccepts) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < s.length(); i++) {
                    if (sAccepts.indexOf(s.charAt(i)) != -1) {
                        buf.append(s.charAt(i));
                    }
		}		 
		return buf.toString();
	}
	public static int getNumberOnlyInt(String s) {
		return getInt(getNumberOnly(s));
	}
	public static int getInt(String s) {
		return getNumber(s).intValue();
	}
	public static long getLong(String s) {
		return getNumber(s).longValue();
	}	 
	public static double getDouble(Object n) {
		return getNumber(n).doubleValue();
        }
        public static float getFloat(String s) {
		return getNumber(s).floatValue();				
	}
	public static Number getNumber(Object n) {
            if (n instanceof Number) {
                    return ((Number)n);
            }else if (isDecimalNumber(String.valueOf(n))){
                    return Double.valueOf(String.valueOf(n));
            }
            return 0;
        }
	
        public static boolean isNumeric(String str) {
            return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
        }
        private static boolean isDecimalNumber(String str) {
            return str.matches("^[-+]?[0-9]*.?[0-9]+([eE][-+]?[0-9]+)?$");
        }
        public static boolean isLongIntegerNumber(String str) {
            return str.matches("-?\\d+");  
        }    

	public static void deleteFileAll(String folder) {
		deleteFolderAll(new File(folder));
	}
        public static void deleteFolderChildAll(File dir) {
            try {
                for (File file : dir.listFiles()) {
                    if (file.isFile()) {
                        file.delete();
                    } else {
                        deleteFolderAll(file);
                        file.delete();
                    }
                }
            } catch (Exception e) { }
        }
	public static void deleteFolderAll(File dir) {
		try {
                    for (File file : dir.listFiles()) {
                        if (file.isFile()) {
                            file.delete();
                        } else {
                            deleteFolderAll(file);
                            file.delete();
                        }
                    }
		} catch (Exception e) { }
                try {
                    dir.delete();
                } catch (Exception e) {   }
	}
        public static String readFile(InputStream is) {
             try {
			byte[] buffer = new byte[1024];
			int length;StringBuffer sb = new StringBuffer();
			while ((length = is.read(buffer)) > 0) {
				sb.append(new String(buffer, 0, length));
			}
                        return sb.toString();
            } catch (Exception e) { }
            return "";
        }
	public static long copyFile(String origin, String destination) {
		try {
			return copyFile(new FileInputStream(origin), destination);
		} catch (Exception e) { return -1; }
	}
	public static long copyFile(InputStream is, String destination) {
		try {
                    OutputStream os = new FileOutputStream(destination);

                    byte[] buffer = new byte[1024];
                    int length; long l = 0;
                    while ((length = is.read(buffer)) > 0) {
                        l=l+length;
                        os.write(buffer, 0, length);
                    }
                    os.flush();
                    os.close();
                    is.close();
                    return l;
		} catch (Exception e) { return -1; }
	}
        public static String getString(String s, String chars) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			if (chars.indexOf(s.charAt(i)) != -1) {
				buf.append(s.charAt(i));
			}
		}
		try {
			return buf.toString();
		} catch (Exception e) {
		}
		return "";
	}
        
	 
        public static String getFileExtention(String fname){
            String result ="";
            if (fname.contains(".")) {
                result = fname.substring(fname.lastIndexOf(".")+1);
            }
            return result;
        }
	public static String insertString(String original, String sInsert, int igroup) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < original.length(); i++) {
			if ((i % igroup) == 0 && igroup != 0 && i != 0) {
				sb.append(sInsert + original.substring(i, i + 1));
			} else {
				sb.append(original.substring(i, i + 1));
			}
		}
		return sb.toString();
	}
        public static String formatDate(long currdate, String format) {
		return new SimpleDateFormat(format).format(new Date(currdate));
	}
        public static String NowTime() {
		Calendar calendar = Calendar.getInstance();
		return new SimpleDateFormat("HH:mm:ss").format(calendar.getTime());
	}
	public static String Now() {
		Calendar calendar = Calendar.getInstance();

		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
	}
        public static String NowDate() {
		Calendar calendar = Calendar.getInstance();

		return new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
	}
        public static String NowDateDB() {
		Calendar calendar = Calendar.getInstance();

		return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
	}
        public static String formatCurrencyBulat(double original) {
		NumberFormat formatter = new DecimalFormat("#0.00");
		return formatCurrencyBulat(String.valueOf(formatter.format(original)));
	}
        public static String formatCurrencyBulat(String original) {
		if (original.contains(".")) {
			return formatCurrency(original.substring(0, original.indexOf(".")));
		}else{
			return formatCurrency(original);
		}
	}
        public static String formatCurrency(long original) {
            return formatCurrency(String.valueOf(original));
        }
	public static String formatCurrency(String original) {
            if (original.contains(".")) {
                StringBuilder stringBuilder = new StringBuilder();
                int il = original.indexOf(".");
                stringBuilder.append(insertStringRev(original.substring(0, il), ",", 3));
                stringBuilder.append(original.substring(il));                
                return  stringBuilder.toString();
            }                    
            return insertStringRev(original, ",", 3);
	}
        public static String formatNumber(String original) {
            return original.replace(",", ""); 
        }
	public static String insertStringMax(String original, String sInsert, int max) {
		StringBuffer sb = new StringBuffer();
		for (int i = original.length(); i < max; i++) {
		   sb.append(sInsert);
		}
		sb.append(original);
		return sb.toString();
	}

	public static String insertStringRev(String original, String sInsert, int igroup) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < original.length(); i++) {

			if (((original.length() - i) % igroup) == 0 && igroup != 0 && i != 0) {
				sb.append(sInsert + original.substring(i, i + 1));
			} else {
				sb.append(original.substring(i, i + 1));
			}
		}
		return sb.toString();
	}

	public static String notNull(String s) {
		if (s != null) {
			return s;
		}
		return "";
	}

	public static boolean isExit;

	public static void setExit(boolean isExit) {
		Utility.isExit = isExit;
	}

	public static boolean isExit() {
		return isExit;
	}

	public static long converDateToLong(String date) {

		SimpleDateFormat df = null;

		if (date.length() == 10) {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy");
			}
		} else if (date.length() == 16) {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			}
		} else if (date.length() == 19) {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			}
		} else {
			if (date.contains("-")) {
				df = new SimpleDateFormat("dd-MM-yyyy");
			} else {
				df = new SimpleDateFormat("dd/MM/yyyy");
			}
		}

		try {
			Date dt = df.parse(date);
			long hasil = dt.getTime();
			return hasil;
		} catch (Exception e) {
		}

		return 0;
	}

	 
	public static boolean containsChar(String kalimat, char character) {
		boolean isContain = false;
		String charLower = kalimat.toLowerCase();
		char[] charArray = charLower.toCharArray();
		for (int i = 0; i < charLower.length(); i++) {
			if (charArray[i] == character) {
				isContain = true;
				return isContain;
			}
		}
		return isContain;
	}

	 

	 
	public static boolean isContainImages(String path) {
		boolean isThere = false;
		File file = new File(path);
		if (file.exists()) {
			isThere = true;
		} else {
			isThere = false;
		}
		return isThere;
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static void i(String tag, String msg) {
		 
	}
 
	public static void imagePost(String URL, String fileName) {

	}

	 
	
	public static Vector<LinkedHashMap<String, String>> getRecordWithSeparate(Vector<String> data, String separate){
		Vector<LinkedHashMap<String, String>> v = new Vector<LinkedHashMap<String,String>>();
		for (int i = 0; i < data.size(); i++) {
			if (data.elementAt(i).contains(separate)) {
				LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
				map.put(data.elementAt(i).substring(0, data.elementAt(i).indexOf(separate)), data.elementAt(i).substring(data.elementAt(i).indexOf(separate) + 1));
				v.add(map);
			}
		}
		return v;
	}
	
	 

        public static String formatsizeByte(long i) {
            if (i>=1024*1024*2+1) {
                return  formatCurrency((i/1024/1024)+"")+" MB";
            }else if (i>=1024+1) {
                return  formatCurrency((i/1024)+"")+" KB";
            }
            return  "1 KB";
        }
        public static String formatsizeKByte(long i) {
            String sRes=(i/1024+(i==0?0:1))+"";        
            return  formatCurrency(sRes);
        }
        public static String replace(String _text, String _searchStr, String _replacementStr)     {
            StringBuffer sb = new StringBuffer();
            int searchStringPos = _text.indexOf(_searchStr);
            int startPos = 0;
            int searchStringLength = _searchStr.length();
            while (searchStringPos != -1) {
                sb.append(_text.substring(startPos, searchStringPos)).append(_replacementStr);
                startPos = searchStringPos + searchStringLength;
                searchStringPos = _text.indexOf(_searchStr, startPos);
            }
            sb.append(_text.substring(startPos,_text.length()));
            return sb.toString();
        }
        
        public static Nikitaset SaveActivity(Nset result ,NikitaResponse response)     {
        String activityname = result.getData("activityname").toString();
        String activitytype = result.getData("activitytype").toString();
        String username = result.getData("username").toString();
        String application = result.getData("application").toString();
        String mode = result.getData("mode").toString();
        String additional = result.getData("additional").toString();
                
        NikitaConnection nikitaConnection =response.getConnection(NikitaConnection.LOGIC);
        int dbCore = WebUtility.getDBCore(nikitaConnection);
        
        Nikitaset nikitaset = nikitaConnection.Query("INSERT INTO sys_activity("+
                                          "activityname,activitytype,username,application,mode,additional,createdby,createddate)"+
                                          "VALUES(?,?,?,?,?,?,?,"+WebUtility.getDBDate(dbCore)+")",                                        
                                          activityname,
                                          activitytype,
                                          username,
                                          application,
                                          mode,
                                          additional,
                                          username);
        
        return nikitaset;
        }
        
        public static String FormSync(String formid){      
        
            return "";
        }
        
        
         
        
        public static String[] argumentsQueryNF(Object...param){      
            if (param!=null) {
                String[]  args = new String[param.length];
                for (int i = 0; i < args.length; i++) {
                    String strparam = String.valueOf(param[i]);
                    if (param[i] instanceof Date || param[i] instanceof java.sql.Timestamp) {
                        strparam = "dt|"+param[i];
                    }else if (param[i] instanceof java.sql.Date) {
                        strparam = "date|"+param[i];
                    }else if (param[i] instanceof java.sql.Time) {    
                        strparam = "time|"+param[i];
                    }else if (param[i] instanceof Long) {
                        strparam = "l|"+param[i];
                    }else if (param[i] instanceof Integer) {
                        strparam = "i|"+param[i];
                    }else if (param[i] instanceof Boolean) {
                        strparam = "b|"+param[i];
                    }else if (param[i] instanceof Double) {
                        strparam = "d|"+param[i];
                    }else if (param[i] instanceof Float) {   
                        strparam = "f|"+param[i];
                    }else  if (param[i] instanceof String) {   
                        strparam = "s|"+param[i];
                    }
                    args[i] = strparam;
                }
                return args;      
            }
            return null;
        }
        
        public static String toHexString(byte[] ba) {
            StringBuilder str = new StringBuilder();
            for(int i = 0; i < ba.length; i++)
                str.append(String.format("%x", ba[i]));
            return str.toString();
        }

        public static String fromHexString(String hex) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < hex.length(); i+=2) {
                str.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
            }
            return str.toString();
        }
        
}
