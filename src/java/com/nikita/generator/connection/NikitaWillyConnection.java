 
package com.nikita.generator.connection;
/**
 * created by 13k.mail@gmail.com
 */
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Hashtable;
import javax.imageio.stream.FileImageInputStream;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.lang.StringEscapeUtils;
 

/**
 * created by 13k.mail@gmail.com
 */
 
public class NikitaWillyConnection extends NikitaConnection{
    private String urlConnection = "";
    private String usrConnection = "";
    private  String pwdConnection = "";
    
    public NikitaConnection openConnection(String cname, String url, String user, String pass){
        pwdConnection=pass;usrConnection=user;urlConnection=url;
        Query("");//just send
        return this;
    }     
    public Nikitaset QueryPage(String sql, Nset array, int page, int rowperpage) {
    	return runQuery(sql, array, page, rowperpage);
    }    

    public Nikitaset QueryPage(int page, int rowperpage, String sql, String...param){
    	return runQuery(page, rowperpage, sql, param);
    }
    
    private Nikitaset runQuery(String sql, Nset array, int page, int rowperpage) {    			
		Hashtable<String, String> args= new Hashtable<String, String> ();
        args.put("sql", Nset.newObject().setData("nfid", "nikitaconnection").setData("sql", sql).setData("arg", array).toJSON());
		
        if (rowperpage==-1 && page==-1) {
		}else{
			args.put("paging",  page+","+rowperpage);
		}		
		return sendQuery(args);
    }
    private Nikitaset runQuery(int page, int rowperpage, String s, String... param) {   
        StringBuffer sb = new StringBuffer();
        int index = s.indexOf("?");int i=0;
            while (index >= 0) {
                    sb.append(s.substring(0, index));
                    s = s.substring(index + 1);
                    index = s.indexOf("?");

                    sb.append("'").append(StringEscapeUtils.escapeSql(param[i])).append("'");i++;
            }
            sb.append(s);

            Hashtable<String, String> args= new Hashtable<String, String> ();
        args.put("sql", sb.toString());
		
        if (rowperpage==-1 && page==-1) {
		}else{
			args.put("paging",  page+","+rowperpage);
		}
		
		return sendQuery(args);
    }
    private Nikitaset sendQuery(Hashtable<String, String> args) {
        long start = System.currentTimeMillis();
    	args.put("username",  usrConnection);
    	args.put("password",  pwdConnection);
        try {            
            InputStream is = NikitaInternet.postHttp(urlConnection,args).getEntity().getContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {                        
                byte[] buffer = new byte[1024];
                int length;StringBuffer sb = new StringBuffer();
                while ((length = is.read(buffer)) > 0) {
                    baos.write(buffer, 0, length);
                    sb.append(new String(buffer, 0, length));
                }
            } catch (Exception e) { }                         
            Nikitaset ns =  new Nikitaset( Nset.readJSON( baos.toString() ) );
            Nset n = (new Nset(ns.getInfo()));
            if (!n.getData("core").toString().equals("")) {
               core=n.getData("core").toInteger();
            }     
            
            //13k overide time nikitaconnection
            ns.setInfo(new Nset(ns.getInfo()).setData("time", System.currentTimeMillis() - start  ));
            return   ns; 
        }catch (Exception ex) {
            System.out.println("NikitaWillyConnection");
        }         
        return  new Nikitaset( "Connection Problem" );
    }
    
    
    private Nikitaset QueryA(String s, String... param) {
        String url="";StringBuffer result= new StringBuffer();
        if (usrConnection.length()>=1 && pwdConnection.length()>=1) {
            url="?userid="+URLEncoder.encode(usrConnection)+"&password="+URLEncoder.encode(pwdConnection)+"&u="+URLEncoder.encode(usrConnection)+"&i="+URLEncoder.encode("NikitaConnection")+"&v="+URLEncoder.encode("2.0.0")+"&";
        }
        
        if (param!=null && param.length>=1) {
            Nset x = Nset.newObject();
            x.setData("SQL", s);
            x.setData("ARGS", Nset.newArray());
            for (int i = 0; i < param.length; i++) {
                
                x.getData("ARGS").addData(param[i]);
                
            }
            s=x.toJSON();
        }
        
        url=url+"sql="+URLEncoder.encode(s);
        url=urlConnection+url;
        try {

            /*
            //13kcrazy
            SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
		 
		HttpParams params = new BasicHttpParams();
		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		 
       
   
 
                
		ClientConnectionManager cm = new SingleClientConnManager(params, schemeRegistry);
		DefaultHttpClient httpClient =  new DefaultHttpClient(cm, params);

		 
	    HttpPost httppost = new HttpPost(url);	   
  	    //httppost.addHeader("Content-Type", "application/json");
            
	    MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart("userid", new StringBody(usrConnection));
            multipartEntity.addPart("password", new StringBody(pwdConnection));
            multipartEntity.addPart("sql", new StringBody(s));
            
	    httppost.setEntity(multipartEntity);
	    HttpResponse response;
	    response = httpClient.execute(httppost);
            
            InputStream is = response.getEntity().getContent();
            //13kcrazy
            */
          
            HttpURLConnection conn = null; 
             
            if (urlConnection.startsWith("https")) {                
                try {
                    //KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    //keyStore.load(new FileInputStream("C://keystore"), new char[] {'m','o','b','i','l','e','1','2','3','+'});
                    //TrustManager[] trustAllCerts = new TrustManager[] { new EasyX509TrustManager(keyStore) }; 
                                        
                    TrustManagerFactory tmf =   TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, tmf.getTrustManagers() , new java.security.SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                } catch (GeneralSecurityException e) { }
                conn = (HttpsURLConnection) new URL( urlConnection +"?u="+URLEncoder.encode(usrConnection)+"&i="+URLEncoder.encode("ServiceMobile")+"&v="+URLEncoder.encode("1.3.55")).openConnection(); 
            }else{
                conn = (HttpURLConnection) new URL( urlConnection +"?u="+URLEncoder.encode(usrConnection)+"&i="+URLEncoder.encode("ServiceMobile")+"&v="+URLEncoder.encode("1.3.55")).openConnection(); 
            }
                 
                //crazy at 20140808
                String CRLF = "\r\n"; 
                String boundary = Long.toHexString(System.currentTimeMillis())+System.currentTimeMillis()+"NIKITAWILLY";
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
               
                OutputStream output = conn.getOutputStream();
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, Charset.defaultCharset()), true);
                // Send normal param.
                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"userid\"").append(CRLF);
                writer.append("Content-Type: text/plain; charset=" + Charset.defaultCharset()).append(CRLF);
                writer.append(CRLF).append(usrConnection).append(CRLF).flush();

                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"password\"").append(CRLF);
                writer.append("Content-Type: text/plain; charset=" + Charset.defaultCharset()).append(CRLF);
                writer.append(CRLF).append(pwdConnection).append(CRLF).flush();

                writer.append("--" + boundary).append(CRLF);
                writer.append("Content-Disposition: form-data; name=\"sql\"").append(CRLF);
                writer.append("Content-Type: text/plain; charset=" + Charset.defaultCharset()).append(CRLF);
                writer.append(CRLF).append(s).append(CRLF).flush();
                
                // End of multipart/form-data.
                writer.append("--" + boundary + "--").append(CRLF).flush();
                //crazy at 20140808-end

            /*
            InputStream is =   conn.getInputStream();
            if (is != null) {
                byte[] data = new byte[1024];int len=0;
                    while ((len = is.read(data) )!=-1 ) {
                        result.append(new String(data, 0, len));
                    }
            }
            */
            Nikitaset ns= new Nikitaset( Nset.readJSON(conn.getInputStream()) );
            Nset n = (new Nset(ns.getInfo()));
            if (!n.getData("core").toString().equals("")) {
                core=n.getData("core").toInteger();
            }        
            return   ns;
        }catch (Exception ex) {
            System.out.println("NikitaWillyConnection");
            //ex.printStackTrace();
        }         
        return  new Nikitaset( "Connection Problem" );
    }
    
    
    
    /*
    public static DefaultHttpClient getThreadSafeClient()  {

        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager mgr = client.getConnectionManager();
        HttpParams params = client.getParams();
        client = new DefaultHttpClient(new ThreadSafeClientConnManager(params, 
                mgr.getSchemeRegistry()), params);
        return client;
    }
    */

    public boolean isNikitaConnection() {
        return true; 
    }
}
