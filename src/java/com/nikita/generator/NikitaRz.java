package com.nikita.generator;

 
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nset;
import com.servlet.btask;
import static com.servlet.btask.blockingQueue;
import static java.lang.Thread.sleep;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
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
    
@ServerEndpoint(value = "/nikitarz", configurator = NikitaRzConfigurator.class)
public class NikitaRz {
    public NikitaRz (){
        //System.out.println("ServerEndpoint:"+Thread.currentThread().getName()); 
        //System.out.println("ServerEndpoint:"+this.hashCode()); 
    }
    
    //queue holds the list of connected clients
    private class NiRz {        
        public NiRz ( String appid ){
           this.appid=appid;
        }
        public void addListener(String receiver){
            this.lst.addElement(receiver);
        }
        public void removeListener(String receiver){
            this.lst.remove(receiver);
        }
        public boolean isAppId(String appid){
            return  this.appid.equals(appid) ;
        }
        public boolean containsListener(String receiver){
            return this.lst.contains(receiver);
        }
        public boolean containsListener(String appid, String receiver){
            return  this.appid.equals(appid) && this.lst.contains(receiver);
        }
        private String appid;
        private Vector<String> lst = new Vector<String>();
    }
     
    private static volatile Queue<Session> queue = new ConcurrentLinkedQueue<Session>();
    private static volatile Hashtable<String, NiRz> listener = new Hashtable<String, NiRz>();
    
    private static Thread rateThread ; //rate publisher thread
    static {
     //rate publisher thread, generates a new value for USD rate every 2 seconds.
        rateThread=new Thread(){     
            public void run() {
                DecimalFormat df = new DecimalFormat("#.####");
                while(true)  {
                        double d=2+Math.random();     
                        if(queue!=null)
                            sendAll("USD Rate: "+df.format(d));    
                        try {
                            sleep(10000);
                        }   catch (InterruptedException e) {      
                    }
                }
            };
        } ;
        //rateThread.start();
    }
    public static void sendtoListener(String appid, String receiver, String msg) {       
        sendtoListenerA(appid, receiver, msg);
        btask(receiver);
    }
  
    public static void sendtoListenerA(String appid, String receiver, String msg) {
       for (Session session : queue) {
            try {
                if (listener.get(session.toString()).containsListener(appid, receiver)) {
                    Nset n = Nset.newObject();
                    n.setData("action", "listener");
                    n.setData("receiver", receiver);
                    n.setData("message", msg);                
                    session.getBasicRemote().sendText(n.toJSON());
                }
            } catch (Exception e) { }
        }
    }
      public static void sendtopBar(String appid, String receiver, String msg) {       
        sendtopBarA(appid, receiver, msg);
        btask(receiver);
    }
     public static void sendtopBarA(String appid, String receiver, String value) {
       for (Session session : queue) {
            try {
                if (listener.get(session.toString()).containsListener(appid, receiver)) {
                    Nset n = Nset.newObject();
                    n.setData("action", "pbar");
                    n.setData("receiver", receiver);
                    n.setData("message", value);                
                    session.getBasicRemote().sendText(n.toJSON());
                }
            } catch (Exception e) { }
        }
    } 
    public static void sendtoBroadcast(String appid, String receiver, String msg) {
        sendtoBroadcastA(appid, receiver, msg);
        btask(receiver);
    }
    public static void sendtoBroadcastA(String appid, String receiver, String msg) {
        for (Session session : queue) {
            try {
                if (listener.get(session.toString()).isAppId(appid)) {
                    Nset n = Nset.newObject();
                    n.setData("action", "broadcast");
                    n.setData("receiver", receiver);
                    n.setData("message", msg);                
                    session.getBasicRemote().sendText(n.toJSON());
                }
            } catch (Exception e) { }
        }
    }       
    
    public static void sendUnMessagetoAll(String msg) {
        try {
            ArrayList<Session > closedSessions= new ArrayList<>();
            for (Session session : queue) {
                if(!session.isOpen())  {
                    closedSessions.add(session);
                    listener.remove(session.toString());
                }  else  {
                    session.getBasicRemote().sendText(msg);
                }    
            }
            queue.removeAll(closedSessions);             
        } catch (Exception e) {  }
    }   
    
    public static void btask(String receiverText){
                String stringURL = NikitaConnection.getDefaultPropertySetting().getData("init").getData("btask-rx").toString();
                if (stringURL.equalsIgnoreCase(receiverText)) {
                    try {              
                        if (btask.blockingQueue.size()==0) {
                            //btask.blockingQueue.put("");//just put empty string
                        }                       
                    } catch (Exception ex) {  }
                }   
    }
    
    @OnMessage
    public void onMessage(Session session, String msg) {
        System.out.println("onMessage:"+Thread.currentThread().getName()); 
        //System.out.println(msg);
        if (msg.startsWith("{")||msg.startsWith("[")) {
            Nset nV = Nset.readJSON(msg);
            if (nV.getData("action").toString().equalsIgnoreCase("broadcast")) {
                NikitaRz.sendtoBroadcast(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), nV.getData("receiver").toString(), nV.getData("message").toString());
                
            }else if (nV.getData("action").toString().equalsIgnoreCase("listener")) {
                NikitaRz.sendtoListener(NikitaConnection.getDefaultPropertySetting().getData("init").getData("nikitarz-appid").toString(), nV.getData("receiver").toString(), nV.getData("message").toString());
                
            }else if (nV.getData("action").toString().equalsIgnoreCase("addlistener")) {
                try {
                    listener.get(session.toString()).addListener(nV.getData("receiver").toString());
                } catch (Exception e) { }
            }else if (nV.getData("action").toString().equalsIgnoreCase("removelistener")) {
                try {
                    listener.get(session.toString()).removeListener(nV.getData("receiver").toString());
                } catch (Exception e) { }
            }else if (nV.getData("action").toString().equalsIgnoreCase("chat")) {
                
            }else if (nV.getData("action").toString().equalsIgnoreCase("push")) {  
                
            }else{
                
            }
        }else{
            
        }
    }
    @OnOpen
    public void open(Session session) {
        System.out.println("OnOpen:"+Thread.currentThread().getName()); 
        //System.out.println("OnOpen:"+this.hashCode()); 
       
        //System.out.println(session.getUserProperties().get("HttpSession"));
       // if (session.getUserProperties().get("HttpSession") instanceof HttpSession) {
            //auto connect
            queue.add(session);
            Map<String, List<String>> map = session.getRequestParameterMap();
            if (map== null) {
                map = new HashMap<>();            
            }
            String appid = "";
            if (map.get("appid")!=null && map.get("appid").size()>=1) {
                appid = map.get("appid").get(0);
            }          
            
            listener.put(session.toString(), new NiRz(appid));
            List<String> lst = map.get("receiver");
            if (lst!=null) {
                for (int i = 0; i < lst.size(); i++) {
                    String receiver = lst.get(i);               
                    listener.get(session.toString()).addListener(receiver);
                }
            }
            /*
            HttpSession httpSession =  (HttpSession)session.getUserProperties().get("HttpSession");
            Enumeration<String> keys = httpSession.getAttributeNames();
            while (keys.hasMoreElements()) {
                String string = keys.nextElement();
                System.out.print(string);
                System.out.print(":");
                System.out.println(httpSession.getAttribute(string));
            }
            System.out.println(httpSession.getId());
            if (httpSession.getServletContext() instanceof NikitaService) {
                System.out.println("---------NikitaService------");
                NikitaService nikitaService = (NikitaService)httpSession.getServletContext();
            }
            */
        //}else{
            //direct WS
                        
        //}
           /*     
        System.out.println( session.getPathParameters());
        System.out.println( session.getQueryString());
        System.out.println( session.getUserProperties());
        System.out.println( session.getRequestParameterMap());
        
         System.out.println("New session opened: "+session.getId()+" :"+ Thread.currentThread().getName());
         */
    }
    @OnError
    public void error(Session session, Throwable t) {
        queue.remove(session);
        listener.remove(session.toString());
        //System.err.println("Error on session "+session.getId());  
    }
    @OnClose
    public void closedConnection(Session session) { 
        queue.remove(session);
        listener.remove(session.toString());
        //System.out.println("session closed: "+session.getId() +" :"+ Thread.currentThread().getName());
    }
    private static void sendAll(String msg) {
        try {
            /* Send the new rate to all open WebSocket sessions */  
            ArrayList<Session > closedSessions= new ArrayList<>();
            for (Session session : queue) {
                if(!session.isOpen())  {
                    //System.err.println("Closed session: "+session.getId()+" :"+ Thread.currentThread().getName());
                    closedSessions.add(session);
                    listener.remove(session.toString());
                }  else    {
                    session.getBasicRemote().sendText(msg);
                    //session.getAsyncRemote().sendText(msg);
                }   
            }
            queue.removeAll(closedSessions);
            //System.out.println("Sending "+msg+" to "+queue.size()+" clients :" + Thread.currentThread().getName());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
   
    
}
