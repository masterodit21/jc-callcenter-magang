/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet.smart;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.connection.NikitaInternet;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author rkrzmail
 */
public class SmartUtility {
    public static void sendEmailActivation(String to, String title, String message){
        Hashtable<String, String> hashtable = new Hashtable<String, String>();
        hashtable.put("to", to);
        hashtable.put("subject", title);
        hashtable.put("message", message);
        NikitaInternet.getHttp( "http://exact.co.id/core/sendmail.php?", hashtable);
    }
    public static void sendSMSActivation(String to, String Message){
        
    }
    public static boolean isStatusActive(String status){
        return true;
    }
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    public static boolean validateEmail(String email) {
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
    }
    public static boolean validatePhone(String phone) {
            if (phone.trim().length()>=7 && phone.trim().length()<=20) {
                 return true;
            }
            return false;
    }
    public static Nikitaset zipDetail(NikitaConnection nikitaConnection, String zip){
        return nikitaConnection.Query("SELECT CITY_CODE,ZIP_CATEGORY FROM ZIP_CODE = ?", zip); 
    }
     public static Nikitaset zipOrdesCoverage(NikitaConnection nikitaConnection, String zip){
        return nikitaConnection.Query("SELECT CITY_CODE,ZIP_CATEGORY FROM ZIP_CODE = ?", zip); 
    }
     
    public static String getAddressKodepos(String address){
        List<String> node = Utility.splitList(address, " ");
        for (int i = 0; i < node.size(); i++) {
            String string = node.get(i);
            if (Utility.isNumeric(string) && string.length()>=5 && string.length()<=6) {
                return string;
            }            
        }
        return "";
    }    
    public static boolean getConnectionError(Nikitaset nikitaset, NikitaResponse response){
        Nson nson = Nson.newObject();
        if (nikitaset.getError().length()>=1) {
            if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("errordbview").toString().equalsIgnoreCase("hide")) {
                StringBuilder stringBuilder = new StringBuilder( );
                for (int i = 0; i < Math.min(Thread.currentThread().getStackTrace().length, 5); i++) {                 
                    stringBuilder.append( "" ).append(Thread.currentThread().getStackTrace()[i].getClassName());stringBuilder.append( " " )   ;
                    stringBuilder.append( "" ).append(Thread.currentThread().getStackTrace()[i].getMethodName());stringBuilder.append( " " )   ;
                    stringBuilder.append( "" ).append(Thread.currentThread().getStackTrace()[i].getFileName());stringBuilder.append( ":" )   ;
                    stringBuilder.append( "" ).append(Thread.currentThread().getStackTrace()[i].getLineNumber());stringBuilder.append( ",\r\n" )   ;
                }    
                NikitaConnection nikitaConnection = NikitaConnection.getConnection("smart");
                Nikitaset ns = nikitaConnection.Query("INSERT INTO TRX_ERROR_LOG  (ERRMSG, STACKTRACE) VALUES (?, ?)", nikitaset.getError(), stringBuilder.toString());
                nson.setData("error", "LOG ID : " + ns.getText(0, 0)); 
                nson.setData("errno", 1); 
                response.writeStream(nson.toJson());
            }else{
                StringBuilder stringBuilder = new StringBuilder( );
                for (int i = 0; i < Math.min(Thread.currentThread().getStackTrace().length, 5); i++) {                 
                    stringBuilder.append( "" ).append(Thread.currentThread().getStackTrace()[i].getClassName()); stringBuilder.append( " " )   ;
                    stringBuilder.append( "" ).append(Thread.currentThread().getStackTrace()[i].getMethodName());stringBuilder.append( " " )   ;
                    stringBuilder.append( "" ).append(Thread.currentThread().getStackTrace()[i].getFileName());  stringBuilder.append( ":" )   ;
                    stringBuilder.append( "" ).append(Thread.currentThread().getStackTrace()[i].getLineNumber());stringBuilder.append( ",\r\n" )   ;
                }  
                nson.setData("stack", stringBuilder.toString()); 
                nson.setData("error", nikitaset.getError()); 
                nson.setData("errno", 1); 
                response.writeStream(nson.toJson());
            }
            
            return true;
        }
        return false;
    }
    public static boolean validateAccount(NikitaConnection nikitaConnection,  NikitaRequest request, NikitaResponse response) {
        String application = request.getParameter("smartapplication"); //mysmart|dop|web|smart|java
        String username = request.getParameter("smartusername"); //
        String nikitaauth = request.getParameter("smartnikitaauth");  
        String imei = request.getParameter("smartdeviceimei");  
        
        Nikitaset nikitaset = nikitaConnection.Query("SELECT USERNAME, EXPIRED_DATE FROM TRX_LOGIN_SESSION WHERE USERNAME=? AND NIKITAAUTH=? AND DEVICEIMEI=? ", username, nikitaauth, imei);
        if (SmartUtility.getConnectionError(nikitaset, response)) {           
            return false;
        } else if (nikitaset.getRows() >= 1) {
            if (SmartUtility.dateDiff(SmartUtility.parseDate(Utility.Now(), ""), SmartUtility.parseDate(nikitaset.getText(0, "EXPIRED_DATE"), ""))<0) {
                Nson nson = Nson.newObject();
                nson.setData("errno", "101"); 
                nson.setData("error", "login expired");          
                nson.setData("action", "logout"); 
                response.writeStreamHeader("autoaction", "logout");
                response.writeStream(nson.toJson());
                return false;
            }
        } else  {
            Nson nson = Nson.newObject();
            nson.setData("errno", "100"); 
            nson.setData("error", "validasi username gagal");          
            nson.setData("action", "logout"); 
            response.writeStreamHeader("autoaction", "logout");
            response.writeStream(nson.toJson());
            return false;
        }
        //lihat disetting sekaranf mode verivikasi (receive|send)
        //nikitaset = nikitaConnection.Query("SELECT smart_value FROM smart_setting WHERE smart_key = ? ", "MYSMART_USER_VERIFICATION_MODE");
        //String vermode = nikitaset.getText(0, 0);//default empty == send code
        
        
        return true;
    }
    
    public static String getAccounPhoneRegistered(NikitaConnection nikitaConnection,  String username) {
        
        
        return "";
    }
    
    /*
    Date Manipulation
     1. YYYY-MM-DD HH:MM:SS [YYYY-MM-DD HH:MM:SS.MS TIMEZONE]
     2. YYYY-MM-DD HH:MI:SS
     3. YYYY-MM-DD HH:NN:SS
    . YYYY = YEAR[4dg]
    . YY   = YEAR[2dg]
    . MM   = MONTH [Angka 2dg]
    . MMM  = MONTH [3 digit Name]
    . MMMM = MONTH [All digit Name]
    . DD   = Day
    . HH   = Jam 
    . :MM: = Minute
    . SS   = Second
    
    .day,month,year,second,minute,hour, dayname, dayof
    */
    public static Date dateAdd(Date date, int days){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
    public static Date dateAdd(String date, int second){
        Calendar cal = Calendar.getInstance();
        //cal.setTime(date);
        cal.add(Calendar.SECOND, second); //minus number would decrement the days
        return cal.getTime();
    }
    public static long dateDiff(Date date, Date date2){
        return date2.getTime()- date.getTime();
    }
    public static Date convertTimeZone(Date date, String fromTz, String toTz){
         
        return date;
    }    
    public static String formatDate(Date date, String format){  
        format = format.toUpperCase();
        format = format.replace("YY", "yy");
        format = format.replace("DD", "dd");
        format = format.replace(":MM:", ":mm:");
        format = format.replace("NN", "mm");
        format = format.replace("SS", "ss");        
        format = format.replace("YYYY-MM-DD HH:NN:SS", "yyyy-MM-dd HH:mm:ss");
        format = format.equals("")?"yyyy-MM-dd HH:mm:ss":format; 
        
        return new SimpleDateFormat(format).format(date);
    }
    public static Date parseDate(String date, String format)  { 
        format = format.toUpperCase();
        format = format.replace("YY", "yy");
        format = format.replace("DD", "dd");
        format = format.replace(":MM:", ":mm:");
        format = format.replace("NN", "mm");
        format = format.replace("SS", "ss");        
        format = format.replace("YYYY-MM-DD HH:NN:SS", "yyyy-MM-dd HH:mm:ss");
        format = format.equals("")?"yyyy-MM-dd HH:mm:ss":format;
        try {
           return new SimpleDateFormat(format).parse(date); 
        } catch (Exception e) { }
        return new Date(0);
    }
}
