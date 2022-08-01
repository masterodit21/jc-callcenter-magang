/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator.action;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaService;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import com.sun.mail.smtp.SMTPTransport;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.security.Security;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author rkrzmail
 */
public class MailAction implements IAction{

   
    public boolean OnAction(Nset currdata, NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        String to       = response.getVirtualString(currdata.getData("args").getData("param1").toString());  
        
        String subject  = response.getVirtualString(currdata.getData("args").getData("param2").toString()); 
        String body     = response.getVirtualString(currdata.getData("args").getData("param3").toString()); 
        String att      = response.getVirtualString(currdata.getData("args").getData("param4").toString()); 
        
        String host     = response.getVirtualString(currdata.getData("args").getData("param7").toString());   //port
        String userid   = response.getVirtualString(currdata.getData("args").getData("param8").toString());   
        String password = response.getVirtualString(currdata.getData("args").getData("param9").toString());   
      
        Object option =  response.getVirtual(currdata.getData("args").getData("param6").toString());   //option
        if (option instanceof Nset) {            
        }else{
            option = Nset.readJSON(String.valueOf(option));
        }
        
        try {  
            sendEmail(host, (Nset)option ,userid, password, to, subject, body, att);
            response.setVirtual(currdata.getData("args").getData("result").toString(), "");
        } catch (Exception e) { 
            response.setVirtual(currdata.getData("args").getData("result").toString(), Nset.newObject().setData("error", e.getMessage()).toJSON()); 
        }
        return true;
    }
    
     public static void sendEmail(String url, Nset option, String username, String password, String recipientEmail, String title, String message, String attachment) throws AddressException, MessagingException, MalformedURLException {
        boolean smtps = option.getData("smtps").toString().equalsIgnoreCase("true");
         
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());    
       
        String host = url.contains(":")?url.substring(0, url.indexOf(":")):url;
        String port = url.contains(":")?url.substring(url.indexOf(":")+1):"";
        
        String from = username;
        username = username.contains("@")?username.substring(0, username.indexOf("@")):username;
        
        // Get a Properties object
        Properties props = System.getProperties();
        
        if (smtps) {
            props.setProperty("mail.smtps.host", host);//"smtp.gmail.com"
            //props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            //props.setProperty("mail.smtp.socketFactory.fallback", "false");
            //props.setProperty("mail.smtp.port", "465");
            props.setProperty("mail.smtp.port", port.equalsIgnoreCase("")?"465":port);
            //props.setProperty("mail.smtp.socketFactory.port", "465");
            props.setProperty("mail.smtps.auth", "true");
            //props.put("mail.smtps.quitwait", "false");//If set to false, the QUIT command is sent and the connection is immediately closed. If set   to true (the default), causes the transport to wait for the response to the QUIT command.
        }else{
            props.setProperty("mail.smtp.port", port.equalsIgnoreCase("")?"587":port);
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.starttls.enable", "true");
        }
       
        Session session = Session.getInstance(props, null);

        // -- Create a new message --         
        MimeMessage msg = new MimeMessage(session);
   
        msg.setFrom(new InternetAddress( from ));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));
         
        msg.setSubject(title);       
        msg.setSentDate(new Date());
        
        if (attachment.trim().equalsIgnoreCase("")) {
            if (option.containsKey("message-type") ) {              
                msg.setContent(message, option.getData("message-type").toString());
            }else{
                msg.setText(message, "utf-8");
            }            
        }else{
            //send attachment
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(message, "text/plain");
            mimeBodyPart.setHeader("Content-Type", "text/plain; charset=\"UTF-8\"");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);
            
            String[] atts = new String[]{attachment};
            if (option.getData("attachment").toString().equalsIgnoreCase("multi")) {                
                atts = Utility.split(attachment, option.containsKey("attachment-split") ? option.getData("attachment-split").toString():",");
            }
            //file stream
            for (int i = 0; i < atts.length; i++) {
                String attEmail = atts[i].trim();
                
                mimeBodyPart = new MimeBodyPart();
                String contentType = "*/*";
                String fname = FilenameUtils.getBaseName(attEmail);
                if (attEmail.startsWith("http://")||attEmail.startsWith("https://")) {
                    DataSource source = new URLDataSource(new URL(attEmail));
                    mimeBodyPart.setDataHandler(new DataHandler(source));                   
                    contentType =  source.getContentType();
                }else if (attEmail.startsWith("res/resource")||attEmail.startsWith("/res/resource")) {                     
                    DataSource source = new FileDataSource(NikitaConnection.getDefaultPropertySetting().getData("init").getData("resource").toString()+NikitaService.getFileSeparator()+fname);
                    mimeBodyPart.setDataHandler(new DataHandler(source));                    
                    contentType = source.getContentType();
                }else if (attEmail.startsWith("res/storage")||attEmail.startsWith("/res/storage")) { 
                    DataSource source = new FileDataSource(NikitaConnection.getDefaultPropertySetting().getData("init").getData("storage").toString()+NikitaService.getFileSeparator()+fname);
                    mimeBodyPart.setDataHandler(new DataHandler(source));                    
                    contentType = source.getContentType();
                }else{
                    DataSource source = new FileDataSource(attEmail);
                    mimeBodyPart.setDataHandler(new DataHandler(source));                    
                    contentType = source.getContentType();
                } 
                mimeBodyPart.setFileName(fname);
                mimeBodyPart.setHeader("Content-Type", contentType + "; name=\"" + fname + "\"");
                mimeBodyPart.setHeader("Content-Transfer-Encoding", "base64");
                multipart.addBodyPart(mimeBodyPart);
            }
                        
                        
            msg.setContent(multipart);
        }
                
        
        
        Transport transport = session.getTransport(smtps?"smtps":"smtp");
        transport.connect(host, username, password);
        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
    }

    private static String getBaseName(String url) {
        if (url.contains("/")) {
            url = url.substring(url.lastIndexOf("/")+1);
        }
        if (url.contains("\\")) {
            url = url.substring(url.lastIndexOf("\\")+1);
        }
        if (url.contains("=")) {
            url = url.substring(url.lastIndexOf("=")+1);
        }        
        url = getStringOnly(url, "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM !_");
        return url;
    }
        private static String getStringOnly(String s, String only) {
               StringBuilder buf = new StringBuilder();
               for (int i = 0; i < s.length(); i++) {
                       if (only.indexOf(s.charAt(i)) != -1) {
                               buf.append(s.charAt(i));
                       }
               }		 
               return buf.toString();
       }
    
    
    
    public static void Sends(final String username, final String password, String recipientEmail, String ccEmail, String title, String message) throws AddressException, MessagingException {
        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.smtps.host", "smtp.gmail.com");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtps.auth", "true");

        /*
        If set to false, the QUIT command is sent and the connection is immediately closed. If set 
        to true (the default), causes the transport to wait for the response to the QUIT command.

        ref :   http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html
                http://forum.java.sun.com/thread.jspa?threadID=5205249
                smtpsend.java - demo program from javamail
        */
        props.put("mail.smtps.quitwait", "false");

        Session session = Session.getInstance(props, null);

        // -- Create a new message --
        final MimeMessage msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        msg.setFrom(new InternetAddress(username + "@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail, false));

        if (ccEmail.length() > 0) {
            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccEmail, false));
        }

        msg.setSubject(title);
        msg.setText(message, "utf-8");
        msg.setSentDate(new Date());

        SMTPTransport t = (SMTPTransport)session.getTransport("smtps");

        t.connect("smtp.gmail.com", username, password);
        t.sendMessage(msg, msg.getAllRecipients());      
        t.close();
    }
    
    public   void SendEmail(String [] args)  {    
         //props.setProperty("mail.user", "myuser");
        //props.setProperty("mail.password", "mypwd");
       // Recipient's email ID needs to be mentioned.
       String to = "abcd@gmail.com";

       // Sender's email ID needs to be mentioned
       String from = "web@gmail.com";

       // Assuming you are sending email from localhost
       String host = "localhost";

       // Get system properties
       Properties properties = System.getProperties();

       // Setup mail server
       properties.setProperty("mail.smtp.host", host);

       // Get the default Session object.
       Session session = Session.getDefaultInstance(properties);

       try{
          // Create a default MimeMessage object.
          MimeMessage message = new MimeMessage(session);

          // Set From: header field of the header.
          message.setFrom(new InternetAddress(from));

          // Set To: header field of the header.
          message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

          // Set Subject: header field
          message.setSubject("This is the Subject Line!");

          // Now set the actual message
          message.setText("This is actual message");
 
          // Send message
          Transport.send(message);
          System.out.println("Sent message successfully....");
       }catch (MessagingException mex) {
          mex.printStackTrace();
       }
    }
    
    public static void SendHTMLEmail(String [] args) {
       //props.setProperty("mail.user", "myuser");
        //props.setProperty("mail.password", "mypwd");
      // Recipient's email ID needs to be mentioned.
      String to = "abcd@gmail.com";

      // Sender's email ID needs to be mentioned
      String from = "web@gmail.com";

      // Assuming you are sending email from localhost
      String host = "localhost";

      // Get system properties
      Properties properties = System.getProperties();

      // Setup mail server
      properties.setProperty("mail.smtp.host", host);

      // Get the default Session object.
      Session session = Session.getDefaultInstance(properties);

      try{
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

         // Set Subject: header field
         message.setSubject("This is the Subject Line!");

         // Send the actual HTML message, as big as you like
         message.setContent("<h1>This is actual message</h1>", "text/html" );

         // Send message
         Transport.send(message);
         System.out.println("Sent message successfully....");
      }catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
    
     public static void SendFileEmail(String [] args) {
        //props.setProperty("mail.user", "myuser");
        //props.setProperty("mail.password", "mypwd");
      // Recipient's email ID needs to be mentioned.
      String to = "abcd@gmail.com";

      // Sender's email ID needs to be mentioned
      String from = "web@gmail.com";

      // Assuming you are sending email from localhost
      String host = "localhost";

      // Get system properties
      Properties properties = System.getProperties();

      // Setup mail server
      properties.setProperty("mail.smtp.host", host);

      // Get the default Session object.
      Session session = Session.getDefaultInstance(properties);

      try{
         // Create a default MimeMessage object.
         MimeMessage message = new MimeMessage(session);

         // Set From: header field of the header.
         message.setFrom(new InternetAddress(from));

         // Set To: header field of the header.
         message.addRecipient(Message.RecipientType.TO,    new InternetAddress(to));

         // Set Subject: header field
         message.setSubject("This is the Subject Line!");

         // Create the message part 
         BodyPart messageBodyPart = new MimeBodyPart();

         // Fill the message
         messageBodyPart.setText("This is message body");
         
         // Create a multipar message
         Multipart multipart = new MimeMultipart();

         // Set text message part
         multipart.addBodyPart(messageBodyPart);

         // Part two is attachment
         messageBodyPart = new MimeBodyPart();
         String filename = "file.txt";
         DataSource source = new FileDataSource(filename);
         messageBodyPart.setDataHandler(new DataHandler(source));
         messageBodyPart.setFileName(filename);
         multipart.addBodyPart(messageBodyPart);

         // Send the complete message parts
         message.setContent(multipart );

         // Send message
         Transport.send(message);
         System.out.println("Sent message successfully....");
      }catch (MessagingException mex) {
         mex.printStackTrace();
      }
   }
    
    
        /**
       * Create a MimeMessage using the parameters provided.
       *
       * @param to Email address of the receiver.
       * @param from Email address of the sender, the mailbox account.
       * @param subject Subject of the email.
       * @param bodyText Body text of the email.
       * @return MimeMessage to be used to send email.
       * @throws MessagingException
       */
    public static MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        InternetAddress tAddress = new InternetAddress(to);
        InternetAddress fAddress = new InternetAddress(from);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                           new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }
    
        /**
        * Create a Message from an email
        *
        * @param email Email to be set to raw of message
        * @return Message containing base64url encoded email.
        * @throws IOException
        * @throws MessagingException
        */
    public static Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
         ByteArrayOutputStream bytes = new ByteArrayOutputStream();
         email.writeTo(bytes);
         //String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
         //Message message = new Message();
         //message.setRaw(encodedEmail);
         //return message;
         return null;
    }
    /**
      * Create a MimeMessage using the parameters provided.
      *
      * @param to Email address of the receiver.
      * @param from Email address of the sender, the mailbox account.
      * @param subject Subject of the email.
      * @param bodyText Body text of the email.
      * @param fileDir Path to the directory containing attachment.
      * @param filename Name of file to be attached.
      * @return MimeMessage to be used to send email.
      * @throws MessagingException
      */
     public static MimeMessage createEmailWithAttachment(String to, String from, String subject, String bodyText, String fileDir, String filename) throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        InternetAddress tAddress = new InternetAddress(to);
        InternetAddress fAddress = new InternetAddress(from);

        email.setFrom(fAddress);
        email.addRecipient(javax.mail.Message.RecipientType.TO, tAddress);
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/plain");
        mimeBodyPart.setHeader("Content-Type", "text/plain; charset=\"UTF-8\"");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(fileDir + filename);

        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName(filename);
        String contentType = Files.probeContentType(FileSystems.getDefault()
            .getPath(fileDir, filename));
        mimeBodyPart.setHeader("Content-Type", contentType + "; name=\"" + filename + "\"");
        mimeBodyPart.setHeader("Content-Transfer-Encoding", "base64");

        multipart.addBodyPart(mimeBodyPart);

        email.setContent(multipart);

        return email;
    }
     
     /**
    * Send an email from the user's mailbox to its recipient.
    *
    * @param service Authorized Gmail API instance.
    * @param userId User's email address. The special value "me"
    * can be used to indicate the authenticated user.
    * @param email Email to be sent.
    * @throws MessagingException
    * @throws IOException
    */
     //Gmail service
     public   void sendMessage(String service, String userId, MimeMessage email) throws MessagingException, IOException {
        Message message = createMessageWithEmail(email);
       // message = service.users().messages().send(userId, message).execute();

       // System.out.println("Message id: " + message.getId());
        //System.out.println(message.toPrettyString());
    }
     
    public void generateAndSendEmail() throws AddressException, MessagingException {
        Properties mailServerProperties;
        Session getMailSession;
        MimeMessage generateMailMessage;
        
        // Step1
        System.out.println("\n 1st ===> setup Mail Server Properties..");
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
        System.out.println("Mail Server Properties have been setup successfully..");

        // Step2
        System.out.println("\n\n 2nd ===> get Mail Session..");
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        generateMailMessage = new MimeMessage(getMailSession);
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("test1@crunchify.com"));
        generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress("test2@crunchify.com"));
        generateMailMessage.setSubject("Greetings from Crunchify..");
        String emailBody = "Test email by Crunchify.com JavaMail API example. " + "<br><br> Regards, <br>Crunchify Admin";
        generateMailMessage.setContent(emailBody, "text/html");
        System.out.println("Mail Session has been created successfully..");

        // Step3
        System.out.println("\n\n 3rd ===> Get Session and Send mail");
        Transport transport = getMailSession.getTransport("smtp");

        // Enter your correct gmail UserID and Password
        // if you have 2FA enabled then provide App Specific Password
        transport.connect("smtp.gmail.com", "<----- Your GMAIL ID ----->", "<----- Your GMAIL PASSWORD ----->");
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        transport.close();
    }
}
