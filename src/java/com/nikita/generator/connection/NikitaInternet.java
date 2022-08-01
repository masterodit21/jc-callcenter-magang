package com.nikita.generator.connection;


import com.rkrzmail.nikita.data.HashtableMulti;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Base64Coder;
import com.rkrzmail.nikita.utility.Utility;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;



 

public class NikitaInternet {
    public static int iConnectionTimeout = 30000;
    
    public static Nikitaset getNikitaset(HttpResponse httpResponse){		 
		return new Nikitaset(getNset(httpResponse));
	}	
	public static Nset getNset(HttpResponse httpResponse){		 
		return Nset.readJSON(getString(httpResponse));
	}
	public static String getString(HttpResponse httpResponse){
		try {
			return getString(httpResponse.getEntity().getContent());
		}  catch (Exception e) { 
                    e.printStackTrace();
                }
		return Nset.readJSON("{'error':'Connection timeout', 'nfid':'NikitaInternet'}", true).toJSON();
	}
	public static String getString(InputStream inputStream){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();	
	    try {
	    	int length = 0;byte[] buffer = new byte[1024];
			while ((length = inputStream.read(buffer)) != -1) {
			    baos.write(buffer, 0, length);
			}
		} catch (IOException e) { }		 
		  
		return new String(baos.toByteArray());
	}
	public static boolean getImage(HttpResponse httpResponse, String pathfullname){
		try {
			try {
				InputStream inputStream = (httpResponse.getEntity().getContent());
				 
				                        FileOutputStream fileOutputStream =new FileOutputStream(pathfullname) ;
                                int length = 0;byte[] buffer = new byte[1024];
				while ((length = inputStream.read(buffer)) != -1) {
					fileOutputStream.write(buffer, 0, length);
				}
				fileOutputStream.close();					 
			}  catch (Exception e) { new File(pathfullname).delete(); }
			return true;
		} catch (Exception e) { }
		return false;
	}
	
        public static String getUrlHttp(String url, String...paramvalue)  {
            if (paramvalue!=null) {
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < paramvalue.length; i++) {
                    if (paramvalue[i].contains("=")) {
                        int split = paramvalue[i].indexOf("=");String sdata = Utility.urlEncode(paramvalue[i].substring(split+1));
                        stringBuffer.append(paramvalue[i].substring(0, split)).append("=").append(sdata).append("&");
                    }				
                }		
                url = url + (url.contains("?")?"&":"?")+stringBuffer.toString(); 
            }
            return url;
        }
        public static HttpResponse getHttp(String url, String...paramvalue)  {
               return getHttp(url, Nset.newObject(), paramvalue);
        }
	public static HttpResponse getHttp(String url, Nset hdr, String...paramvalue)  {
            try {
                if (paramvalue!=null) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < paramvalue.length; i++) {
                        if (paramvalue[i].contains("=")) {
                            int split = paramvalue[i].indexOf("=");String sdata = Utility.urlEncode(paramvalue[i].substring(split+1));
                            stringBuffer.append(paramvalue[i].substring(0, split)).append("=").append(sdata).append("&");
                        }				
                    }		
                    url = url + (url.contains("?")?"&":"?")+stringBuffer.toString(); 
                }			 
			
                System.err.println(url);
                if (url.endsWith("&")) {
                    url = url.substring(0,url.length()-1);
                }
                HttpURLConnection connection =   internalConnection(hdr, url);
                connection.setRequestMethod("GET");
     
                return internalHttpResponse(connection);
            } catch (Exception e) {
                System.out.println(e.getMessage());                
                return new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("", 1, 1), 0, ""));
            }		  
	} 
        public static HttpResponse postHttp(String url, String...paramvalue)  {
            if (paramvalue!=null) {
                Hashtable<String, String> arg = new Hashtable<String, String>();
                for (int i = 0; i < paramvalue.length; i++) {
                    if (paramvalue[i].contains("=")) {
                        int split = paramvalue[i].indexOf("=");
                        arg.put(paramvalue[i].substring(0, split), paramvalue[i].substring(split+1));
                    }				
                }
                return postHttp(url, arg);
            }
            return postHttp(url);
	}
        public static HttpResponse getHttp(String url, Hashtable<String, String> arg) {
             System.out.println("getHttp"); 
            StringBuilder sb = new StringBuilder(url.contains("?")?"":"?");         
            Nset nset = new Nset(arg) ;
            String[] keys =  nset.getObjectKeys();
            for (int i = 0; i < keys.length; i++) {
                sb.append("&");
                sb.append(keys[i]);
                sb.append("=");
                sb.append(URLEncoder.encode( nset.getData(keys[i]).toString() )  );
            }
            return getHttp(url+sb.toString(), new String[0]);
        }
	public static HttpResponse postHttp(String url, Hashtable<String, String> arg) {
		return postHttp(url, null,  arg);
	}
        public static HttpResponse postHttpBody(String url, Hashtable<String, String> header, String body) {
              System.out.println("postHttp"); 
            try {
                HttpURLConnection connection =   internalConnection(Nset.newObject(), url);
                
                 
                Nset nheader = new Nset(header) ;
                String[] keyheader =  nheader.getObjectKeys();			
                for (int i = 0; i < keyheader.length; i++) {
                    connection.setRequestProperty(keyheader[i], nheader.getData(keyheader[i]).toString() );
                }
                
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(iConnectionTimeout);
              	
                
                
                OutputStream output = connection.getOutputStream();
                
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, Charset.defaultCharset()), true);

                writer.write(body );
                writer.flush();               

                return internalHttpResponse(connection);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("", 1, 1), 0, ""));
            }		
        }
	public static HttpResponse postHttp(String url, Hashtable<String, String> header, Hashtable<String, String> arg) {
             System.out.println("postHttp"); 
            try {
                HttpURLConnection connection =   internalConnection( Nset.newObject(), url);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
               
                
                
                Nset nheader = new Nset(header) ;
                String[] keyheader =  nheader.getObjectKeys();			
                for (int i = 0; i < keyheader.length; i++) {
                    connection.setRequestProperty(keyheader[i], nheader.getData(keyheader[i]).toString() );
                }	
                
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(iConnectionTimeout);
                
                
                OutputStream output = connection.getOutputStream();
                
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, Charset.defaultCharset()), true);

                Nset nset = new Nset(arg) ;
                String[] keys =  nset.getObjectKeys();
                for (int i = 0; i < keys.length; i++) {
                    writer.write("&");
                    writer.write(keys[i]);
                    writer.write("=");
                    writer.write(URLEncoder.encode( nset.getData(keys[i]).toString() )  );
                }
                writer.flush();               

                return internalHttpResponse(connection);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("", 1, 1), 0, ""));
            }		
             
	}
	public static HttpResponse postHttpData(String url, String contentType, String data) {
            System.out.println("postHttpData"); 
            try {
                HttpURLConnection connection =   internalConnection(Nset.newObject(), url);
                connection.setRequestProperty("Content-Type", contentType);
                
                connection.setRequestMethod("POST");
                connection.setConnectTimeout(iConnectionTimeout);
                
                OutputStream output = connection.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, Charset.defaultCharset()), true);

                writer.write(data);
                writer.flush();               

                return internalHttpResponse(connection);
            } catch (Exception e) {
                return new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("", 1, 1), 0, ""));
            }	
        }
	public static HttpResponse multipartHttp(String url, Hashtable<String, String> arg, String imagename, InputStream file) {
		HashtableMulti<String, String, InputStream> inps = new HashtableMulti<String, String, InputStream>  ();
		inps.put("image", imagename, file);		
		return multipartHttp(url, arg, inps);
	}
	
	public static HttpResponse multipartHttp(String url, Hashtable<String, String> arg, HashtableMulti<String, String, InputStream> file) {
            System.out.println("multipartHttp"); 
            try {
                    HttpURLConnection connection =   internalConnection(Nset.newObject(), url);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                     
                    connection.setRequestMethod("POST");
                    connection.setConnectTimeout(iConnectionTimeout);
                    
                    String CRLF = "\r\n"; 
                    String boundary = Long.toHexString(System.currentTimeMillis())+System.currentTimeMillis()+"NIKITAGEN";
                  
                    connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                    connection.setRequestMethod("POST");
                    
                    OutputStream output = connection.getOutputStream();
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, Charset.defaultCharset()), true);
                    // Send normal param.
                    Nset nset = new Nset(arg) ;
                    String[] keys =  nset.getObjectKeys();
                    for (int i = 0; i < keys.length; i++) {
                        writer.append("--" + boundary).append(CRLF);
                        writer.append("Content-Disposition: form-data; name=\""+keys[i]+"\"").append(CRLF);
                        writer.append("Content-Type: text/plain; charset=" + Charset.defaultCharset()).append(CRLF);
                        writer.append(CRLF).append(nset.getData(keys[i]).toString()).append(CRLF).flush();
                    }      
                    
                    
                    nset = new Nset(file) ;
                    keys =  nset.getObjectKeys();
                    for (int i = 0; i < keys.length; i++) {                             
                        // Send binary file.
                       String fname ="";
                       writer.append("--" + boundary).append(CRLF);
                       writer.append("Content-Disposition: form-data; name=\""+keys[i]+"\"; filename=\"" + file.get(keys[i]) + "\"").append(CRLF);
                       writer.append("Content-Type: " + "image/png").append(CRLF);
                       writer.append("Content-Transfer-Encoding: binary").append(CRLF);
                       writer.append(CRLF).flush();
                       InputStream is = file.getData(keys[i]);
                       byte[] data = new byte[1024];int len=0;
                       while ((len = is.read(data) )!=-1 ) {
                           output.write(data,0, len);
                       }
                       output.flush();    
                       is.close();
                    }	                        
                                    
                    writer.append(CRLF).flush(); // CRLF is important! It indicates end of boundary.
                    // End of multipart/form-data.
                    writer.append("--" + boundary + "--").append(CRLF).flush();
                    
                    return internalHttpResponse(connection);
                } catch (Exception e) {
                    return new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("", 1, 1), 0, ""));
                }
		 
	}
    private static String  getResponseMessage(HttpURLConnection connection){
        try {
            return connection.getResponseMessage();
        } catch (Exception e) { 
            return  e!=null?e.getMessage():"null";
        }       
    }
    private static int  getResponseCode(HttpURLConnection connection){
        try {
            return connection.getResponseCode();
        } catch (Exception e) { 
            return  -1;
        }       
    }
    private static HttpResponse internalHttpResponse(HttpURLConnection connection){
        System.out.println("internalHttpResponse");  
        HttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("", 1, 1), getResponseCode(connection), getResponseMessage(connection))){
                private HttpURLConnection connection;
                public HttpEntity getEntity() {
                    return new BasicHttpEntity(){
                        private HttpURLConnection conn;
                        public InputStream getContent() throws IllegalStateException {
                            System.out.println("getContent"); 
                            try {
                                System.out.print("getResponseCode: "); 
                                System.out.println(conn.getResponseCode()); 
                            } catch (Exception e) {  }
                            /*
                            try {
                                System.out.print("getStatusLine: "); 
                                System.out.println(getStatusLine()); 
                                System.out.print("getStatusCode: "); 
                                System.out.println(getStatusLine().getStatusCode()); 
                            } catch (Exception e) {  }
                            */
                            try {  
                                int status = 0;
                                try {
                                    status = conn.getResponseCode();
                                } catch (Exception e) { }
                                
                                if(status >= HttpStatus.SC_BAD_REQUEST){
                                    return this.conn.getErrorStream()!=null?this.conn.getErrorStream():this.conn.getInputStream();
                                }else {                        
                                    return this.conn.getInputStream();
                                }
                            } catch (Exception ex) {
                                System.out.print("getContentErr: "); 
                                ex.printStackTrace();
                                System.out.print(ex.getMessage());
                                return null;
                            }
                             
                        }
                        public HttpEntity get(HttpURLConnection conn){
                            this.conn=conn;
                            return this;
                        };
                    }.get(connection);
                     
                } 

                    
                public HttpResponse get(HttpURLConnection connection){
                    this.connection=connection;
                    return this;
                };
            }.get(connection);   
          System.out.println("internalHttpResponse."); 
            return response;
    }
    
    final static TrustManager[] trustAllCerts = new TrustManager[]{ new X509ExtendedTrustManager() {
                                @Override
                                public java.security.cert.X509Certificate[] getAcceptedIssuers()   {
                                    return null;
                                }

                                @Override
                                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)  {
                                }

                                @Override
                                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)   {
                                }

                                @Override
                                public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string, Socket socket)   {

                                }

                                @Override
                                public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string, Socket socket) throws CertificateException    {

                                }

                                @Override
                                public void checkClientTrusted(java.security.cert.X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException     {

                                }

                                @Override
                                public void checkServerTrusted(java.security.cert.X509Certificate[] xcs, String string, SSLEngine ssle) throws CertificateException   {

                                }

                            }
                  };
            
    private static HttpURLConnection internalConnection(Nset hdr, String urlConnection) {
        System.out.println("internalConnection");  
        HttpURLConnection conn = null; 
        try {
            if (urlConnection.startsWith("https")) {                
                /*   
                try {
                    //KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    //keyStore.load(new FileInputStream("C://keystore"), new char[] {'m','o','b','i','l','e','1','2','3','+'});
                    //TrustManager[] trustAllCerts = new TrustManager[] { new EasyX509TrustManager(keyStore) }; 
                                    
                    TrustManagerFactory tmf =   TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, tmf.getTrustManagers() , new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                } catch (GeneralSecurityException e) { }
                */
                conn = (HttpsURLConnection) new URL( urlConnection  ).openConnection(); 
                
                if (NikitaConnection.getDefaultPropertySetting().getData("init").getData("https-bypass").toString().trim().equalsIgnoreCase("true")) {
                        SSLContext sc = SSLContext.getInstance("SSL");
                        sc.init(null, trustAllCerts, new java.security.SecureRandom());
                        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                          // Create all-trusting host name verifier
                        HostnameVerifier allHostsValid = new  HostnameVerifier()  {
                            @Override
                            public boolean verify(String hostname, SSLSession session)  {
                                return true;
                            }
                        };                
                        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);//Install the all-trusting host verifier                    
                }
            }else{
                conn = (HttpURLConnection) new URL( urlConnection ).openConnection(); 
            }
                        
            if (urlConnection.contains("nikitauthorization=basic")) {
                String username = "";
                String password = "";
                if (urlConnection.contains("username=")) {
                    username=urlConnection.substring(urlConnection.indexOf("username=")+9);
                    if (username.contains("=")) {
                        username=username.substring(0,username.indexOf("="));
                    }
                    if (username.contains("&")) {
                        username=username.substring(0,username.indexOf("&"));
                    }
                }
                if (urlConnection.contains("password=")) {
                    password=urlConnection.substring(urlConnection.indexOf("password=")+9);
                    if (password.contains("=")) {
                        password=password.substring(0,password.indexOf("="));
                    }
                    if (password.contains("&")) {
                        password=password.substring(0,password.indexOf("&"));
                    }
                }
                conn.addRequestProperty("Authorization", "Basic "+ Base64Coder.encodeString(username+":"+password) );
            }
            
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36 OPR/38.0.2220.41");
            conn.setRequestProperty("Accept", "text/plain, */*; q=0.01");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Nikita", "webservice");
	 
            
            //conn.setRequestProperty("Access-Control-Allow-Origin", "*");
            String[] keys = hdr.getObjectKeys();
            for (int i = 0; i < keys.length; i++) {
                String val    = hdr.getData(keys[i]).toString();      
                conn.setRequestProperty(keys[i],val);
            }
             conn.setDoInput(true);             
        }catch (Exception ex) { 
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println("internalConnection.");  
        return conn;
        
    }
    
    
    
    public static DefaultHttpClient getThreadSafeClient()  {

        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager mgr = client.getConnectionManager();
        HttpParams params = client.getParams();
        client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, 
                mgr.getSchemeRegistry()), params);
        return client;
    }

 
}
