/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

 
import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.utility.Utility;
import com.web.utility.Base64Encoder;
import com.web.utility.MJPG;
import com.web.utility.MjpegInputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author rkrzmail
 */
public class webipcameracapture  extends NikitaServlet{
    
    
    
    /*parameter
    search
    mode
    data
    */
  
    @Override
    public void OnCreate(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        //url server
        //file savename
     
            try {

                  URL url;
                    

                    try {
                            urlString="http://ce3014.myfoscam.org:20054/videostream.cgi?user=user&pwd=user&rate=0";
                           // urlString="http://plazacam.studentaffairs.duke.edu/axis-cgi/mjpg/video.cgi";
                           url = new URL(urlString);
                         }
                         catch (MalformedURLException e) {
                           System.err.println("Invalid URL");
                           return;
                         }

                         try {
                           conn = (HttpURLConnection)url.openConnection();

                           //conn.setRequestProperty("Authorization", "Basic " + base64.encode(user + ":" + pass));
                           //httpIn = new BufferedInputStream(conn.getInputStream(), 8192);
                           
                           MjpegInputStream inputStream = new MjpegInputStream(conn.getInputStream());
                           
                            ByteArrayInputStream jpgIn = new ByteArrayInputStream(inputStream.readMjpegFrame());
                            Utility.copyFile(jpgIn, "D:\\s.png");
                           
                            inputStream.close();
                            conn.disconnect();
                         }  catch (IOException e) {
                           
                           System.err.println("Unable to connect: " + e.getMessage());
                           return;
                         }
              
                
                
                
        } catch (Exception e) { // response.writeStream(e.getMessage()); 
        }
          
            response.getHttpServletResponse().setContentType("multipart/x-mixed-replace; boundary=--BoundaryString");
        try {
            new MJPG().doGet(response.getHttpServletResponse().getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(webipcameracapture.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
     private String urlString, user, pass;
  private byte[] curFrame;
  private boolean frameAvailable;
  private Thread streamReader;
  private HttpURLConnection conn;
  private BufferedInputStream httpIn;
  private ByteArrayOutputStream jpgOut;
  
     public void run( ) {
        URL url;
        Base64Encoder base64 = new Base64Encoder();

        try {
           urlString="http://ce3014.myfoscam.org:20054/videostream.cgi?user=user&pwd=user&rate=0";
           urlString="http://plazacam.studentaffairs.duke.edu/axis-cgi/mjpg/video.cgi";
          url = new URL(urlString);
        }
        catch (MalformedURLException e) {
          System.err.println("Invalid URL");
          return;
        }

        try {
          conn = (HttpURLConnection)url.openConnection();
         
          //conn.setRequestProperty("Authorization", "Basic " + base64.encode(user + ":" + pass));
          httpIn = new BufferedInputStream(conn.getInputStream(), 8192);
        }  catch (IOException e) {
          System.err.println("Unable to connect: " + e.getMessage());
          return;
        }

        int prev = 0;
        int cur = 0;

        try {
            while (httpIn != null && (cur = httpIn.read()) >= 0) {
                  if (prev == 0xFF && cur == 0xD8) {
                        System.err.println("new"); 
                        jpgOut = new ByteArrayOutputStream(8192);
                        jpgOut.write((byte)prev);
                  }
                  if (jpgOut != null) {
                    jpgOut.write((byte)cur);
                  }
                  if (prev == 0xFF && cur == 0xD9) {
                    synchronized(curFrame) {
                        curFrame = jpgOut.toByteArray();
                        try {
                            
                            
                            
                            ByteArrayInputStream jpgIn = new ByteArrayInputStream(curFrame);
                            Utility.copyFile(jpgIn, "D:\\s.png");
                             
                             
                        } catch (Exception e) { System.err.println(e.getMessage());  }
                      
                    }
                    frameAvailable = true;
                    jpgOut.close();
                    System.err.println("finish"); 
                    return;
                  }
                  prev = cur;


            }
        }
        catch (IOException e) {
          System.err.println("I/O Error: " + e.getMessage());
        }
    }
}
