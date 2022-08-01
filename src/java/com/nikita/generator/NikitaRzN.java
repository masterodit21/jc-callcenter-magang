package com.nikita.generator;

 
import com.rkrzmail.nikita.data.Nson;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.Base32;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;


/**
 *
 * @author rkrzmail
 */
    
@ServerEndpoint(value = "/nikitarn", configurator = NikitaRzConfigurator.class)
public class NikitaRzN {
    public NikitaRzN (){
        //System.out.println("ServerEndpoint:"+Thread.currentThread().getName()); 
        //System.out.println("ServerEndpoint:"+this.hashCode()); 
    }
    
   
    
    private static volatile Hashtable<String, Session> hire = new Hashtable<>();
    private static volatile Hashtable<Session, String> hses = new Hashtable<>();
      private static volatile Hashtable<String, Nson> hson = new Hashtable<>();
     private static volatile Queue<Session> queue = new ConcurrentLinkedQueue<Session>();
      
      
       private static Thread rateThread ; //rate publisher thread
    static {
     //rate publisher thread, generates a new value for USD rate every 2 seconds.
        rateThread=new Thread(){     
            public void run() {               
                while(true)  {
                        if(queue!=null)
                            for (Session session : queue) {
                                if(!session.isOpen())  {                                    
                                }  else    {
                                    Nson nson = Nson.newObject();
                                    nson.setData("action", "barcode");
                                    if (hses.containsKey(session)) {
                                        String key = hses.get(session);
                                        nson.setData("barcode", Utility.MD5(System.currentTimeMillis()+"")+key);  
                                        session.getAsyncRemote().sendText(nson.toJson());
                                    }
                                    
                                }   
                            } 
                        try {
                            sleep(30000);
                        }   catch (InterruptedException e) {      
                    }
                }
            };
        } ;
        rateThread.start();
    }
    
      
      
      
      
      
      
    @OnMessage
    public void onMessage(Session session, String msg) {
        //System.out.println("onMessage:"+Thread.currentThread().getName()); 
        //diam saja
    }  
    
    private String getParameter(Session session, String param){
        try {
            return session.getRequestParameterMap().get(param).get(0);
        } catch (Exception e) { }
        return "";
    }
    public static void savetoSID(String sid, Nson msg) {
        hson.put(sid, msg);
    }   
    public static Nson readtoSID(String sid) {
        Nson nson =  hson.get(sid);
        if (nson!=null) {
            return nson;
        }
        return Nson.newObject();
    }   
    
    public static void closeSID(String sid) {
        Session session = hire.get(sid);
        if (session!=null) {
            try {
                if (hses.containsKey(session)) {
                    session.close();

                    String key = hses.get(session);
                    hses.remove(session);

                    if (hire.containsKey(key)) {
                        hire.remove(key);
                    } 
                    if (hson.containsKey(key)) {
                        hson.remove(key);
                    }  
                    queue.remove(session);
                }   
            } catch (IOException ex) { }
        }
    }
        
    public static void sendtoSID(String sid, String msg) {
        Session session = hire.get(sid);
        if (session!=null) {
            Nson nson = Nson.newObject();
            nson.setData("action", "message");
            nson.setData("message", msg);           
            session.getAsyncRemote().sendText(nson.toJson());
        }         
    }
    public static void sendtoSID(String sid,  Nson nson) {
        Session session = hire.get(sid);
        if (session!=null) {
            session.getAsyncRemote().sendText(nson.toJson());
        }         
    }
     public static boolean containsSID(String sid) {
                return hire.containsKey(sid);
    }
    
    @OnOpen
    public void open(Session session) {
        String sid = createSID();
        hire.put(sid, session);         
        hses.put(session, sid);
                
                
        Nson nson = Nson.newObject();
        nson.setData("action", "connected");
        nson.setData("sid", sid);
        nson.setData("cid", getParameter(session, "cid"));     
        nson.setData("appid", getParameter(session, "appid")); 
        
        if (getParameter(session, "appid").equalsIgnoreCase("request")) {
            queue.add(session);
        }
                
        Nson nson1 = Nson.newObject();
        nson1.setData("sid", sid);
        //nson1.setData("hid", sid.toLowerCase()+Utility.MD5(sid));     
        //nson1.setData("appid", getParameter(session, "appid")); 
        //nson1.setData("cid", System.currentTimeMillis()); 
        
        
        nson.setData("barcode", Utility.MD5(nson1.toJson())+sid); 
 
        session.getAsyncRemote().sendText(nson.toJson());
    }
     
    
    @OnError
    public void error(Session session, Throwable t) {
        if (hses.containsKey(session)) {
            String key = hses.get(session);
            hses.remove(session);
            
            if (hire.containsKey(key)) {
                hire.remove(key);
            } 
            if (hson.containsKey(key)) {
                hson.remove(key);
            }  
            
            queue.remove(session);
        }   
    }
    
    @OnClose
    public void closedConnection(Session session) { 
        if (hses.containsKey(session)) {
            String key = hses.get(session);
            hses.remove(session);
            
            if (hire.containsKey(key)) {
                hire.remove(key);
            }    
            if (hson.containsKey(key)) {
                hson.remove(key);
            }  
            queue.remove(session);
        }                
    }
     
    private static volatile long autosid = 0;
    private static synchronized String createSID(){
        long vaio = (autosid++);     
        byte[] ary = ByteBuffer.allocate(8).putLong(vaio).array();
        for (int i = 0; i < ary.length; i++) {
            if (i == 0 || ary[i] == 0) {      
            }else{
                byte[] bry = new byte[ary.length-i];
                for (int j = i; j < ary.length; j++) {
                    bry[j-i] = ary[j];
                }
                ary = bry ;
                break;
            }            
        } 
        if (autosid == Long.MAX_VALUE) {
            autosid = 0;
        }
        return Base32.encode(ary);
    }   
    
}