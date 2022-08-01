/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.rkrzmail.nikita.data.Nset;
import com.rkrzmail.nikita.utility.Utility;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author rkrzmail
 */
public class NikitaFilter implements Filter {
        
    private ServletContext context;
    private static long modifdate;
    public static void resetConfig(){
        modifdate = System.currentTimeMillis();
    }        
    public void init(FilterConfig fConfig) throws ServletException {
        this.context = fConfig.getServletContext();
        this.context.log("AuthenticationFilter initialized");
        resetConfig();
        //System.out.println("F:"+Thread.currentThread().getName());
    }
     
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //System.out.println("F:"+Thread.currentThread().getName());
        //System.out.println("F:"+request.getScheme());
        NikitaService.iconcurrent ++ ;        
        NikitaService.imaxcurrent = Math.max(NikitaService.imaxcurrent, NikitaService.iconcurrent);
        //Set<Thread> th = Thread.getAllStackTraces().keySet();
        NikitaService.ithread = Thread.getAllStackTraces().size();
        NikitaService.imaxthread = Math.max(NikitaService.imaxthread, NikitaService.ithread);
        /*
        Thread[] ath = th.toArray(new Thread[th.size()]);
        for (int i = 0; i < ath.length; i++) {
              //System.out.print("-"+ath[i].getName()+", ");
        }
        NikitaService.ithread = ath.length;
        */
        //System.out.println("");
        
        // System.out.println("doFilter:"+((HttpServletRequest)request).getRequestURI());
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;         
        String uri = req.getRequestURI();
        String sv=req.getProtocol();
         
        String sva= req.getScheme();
         
        //this.context.log("Requested Resource::"+uri);
    //
       // HttpSession session = req.getSession(false);
         
        /*
        if(uri.endsWith("html") ){
            this.context.log("Unauthorized access request");
            
            res.sendRedirect("webform/");
        }else{
            // pass the request along the filter chain
            chain.doFilter(request, response);
        }
        */
        //request.getRequestDispatcher(null)
        
        
                
                
        if(uri.endsWith(".js")||uri.endsWith(".css")||uri.endsWith(".png")||uri.endsWith(".jpg")||uri.endsWith(".gif") ){
            if ( acceptsGZipEncoding(req)  && uri.contains("static/")) {               
                
                // Validate request headers for caching ---------------------------------------------------
                long lastModified = modifdate;
                String eTag = "eTag" + "_" + modifdate + "_";
                long expires = System.currentTimeMillis() + 604800000;
                // If-None-Match header should contain "*" or ETag. If so, then return 304.
                String ifNoneMatch = req.getHeader("If-None-Match");
                if (ifNoneMatch != null && matches(ifNoneMatch, eTag)) {
                    res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    res.setHeader("ETag", eTag); // Required in 304.
                    res.setDateHeader("Expires", expires); // Postpone cache with 1 week.
                    NikitaService.istaticrescount ++ ; 
                    NikitaService.iconcurrent -- ;
                    NikitaService.iconcurrent = Math.max(0, NikitaService.iconcurrent);
                    return;
                }
                // If-Modified-Since header should be greater than LastModified. If so, then return 304.
                
                // This header is ignored if any If-None-Match header is specified.
                long ifModifiedSince = req.getDateHeader("If-Modified-Since");
                if (ifNoneMatch == null && ifModifiedSince != -1 && ifModifiedSince + 1000 > lastModified) {
                    res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                    res.setHeader("ETag", eTag); // Required in 304.
                    res.setDateHeader("Expires", expires); // Postpone cache with 1 week.
                    NikitaService.istaticrescount ++ ; 
                    NikitaService.iconcurrent -- ;
                    NikitaService.iconcurrent = Math.max(0, NikitaService.iconcurrent);
                    return;
                }
                //---------------------------------------------------------------------//
                
                
                res.addHeader("Content-Encoding", "gzip");
                if (uri.endsWith("nikita.js")||uri.endsWith("nikita.css")) { //uri.endsWith(".js")||    
                    //res.addHuri.endsWith("nikita.js")||eader("Cache-Control", "no-cache");//must-revalidate
                }else{
                    res.addHeader("Cache-Control", "public, max-age=31536000");//1d=86400 1y=31536000
                    res.addDateHeader("last-modified", lastModified); 
                    res.addDateHeader("expires",   (System.currentTimeMillis() + 604800000));//31536000
                    res.addHeader("etag", eTag);//604800000
                }               
                GZipServletResponseWrapper gzipResponse = new GZipServletResponseWrapper(res);
                chain.doFilter(new NikitaGeneratorPostData((HttpServletRequest) request) , gzipResponse);
                gzipResponse.close();
               
                NikitaService.iconcurrent -- ;
                NikitaService.iconcurrent = Math.max(0, NikitaService.iconcurrent);
                return;
            }
        }
        
        chain.doFilter(new NikitaGeneratorPostData((HttpServletRequest) request) , response);   
        NikitaService.iconcurrent -- ;
        NikitaService.iconcurrent = Math.max(0, NikitaService.iconcurrent);
    }
 
    private static boolean matches(String matchHeader, String toMatch) {
        String[] matchValues = matchHeader.split("\\s*,\\s*");
        Arrays.sort(matchValues);
        return Arrays.binarySearch(matchValues, toMatch) > -1
            || Arrays.binarySearch(matchValues, "*") > -1;
    }
    public void doFilterA(ServletRequest request,   ServletResponse response,   FilterChain chain)  throws IOException, ServletException {

        HttpServletRequest  httpRequest  = (HttpServletRequest)  request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        
        String encoding = httpRequest.getHeader("Content-Encoding");
        if(encoding != null){
            if(encoding.equalsIgnoreCase("gzip")){
                httpRequest = new GZIPServletRequestWrapper(httpRequest);
            }
        }
        
        if ( acceptsGZipEncoding(httpRequest) ) {            
            httpResponse.addHeader("Content-Encoding", "gzip");
            GZipServletResponseWrapper gzipResponse = new GZipServletResponseWrapper(httpResponse);
            chain.doFilter(request, gzipResponse);
            gzipResponse.close();
        } else {
            chain.doFilter(request, response);
        }
    }
    
    public class NikitaGeneratorPostData  extends HttpServletRequestWrapper{
        Nset args = Nset.newObject();
        boolean nikita = false;
        HttpServletRequest request ;
        public NikitaGeneratorPostData(HttpServletRequest request) {           
            super(request);
            String contenttype = request.getContentType();             
            this.request=request;
            if (String.valueOf(contenttype).trim().startsWith("application/nikitagenerator")||String.valueOf(contenttype).trim().startsWith("application/nikitaconnection")) {                
                nikita=true;                    
                String datanson = "";
                try {
                    InputStream is = request.getInputStream();
                    datanson =  readInputStreamAsString(is);                    
                } catch (Exception e) {}
                                
                if (contenttype.contains("nfid=base64")) {
                     datanson = Utility.decodeBase64(datanson);
                }
                args = Nset.readJSON(datanson);
            }else { 
                if (request.getParameter("nikitageneratorarguments")!=null) {
                    try {                        
                        String datanson =  request.getParameter("nikitageneratorarguments");       
                        args = Nset.readJSON(datanson);
                        nikita = true;
                    } catch (Exception e) {}
                }else if (request.getParameter("nikitageneratorb64arguments")!=null) {
                    try {                        
                        String datanson =  request.getParameter("nikitageneratorb64arguments");   
                        datanson = Utility.decodeBase64(datanson);
                        args = Nset.readJSON(datanson);
                        nikita = true;
                    } catch (Exception e) {}
                }
            }
        }
        private  String readInputStreamAsString(InputStream inputStream ) throws IOException {        
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            inputStream.close();
            return baos.toString();
        }
        public String getParameter(String name) {
            if (nikita) {
                if (args.containsKey(name)) {
                    return args.getData(name).toString(); 
                }                
            }
            return super.getParameter(name); 
        }
        public String[] getParameterValues(String name) {
            if (nikita) {
                if (args.containsKey(name)) {
                    return new String[] {getParameter(name)}; 
                }              
            }
            return super.getParameterValues(name);  
        }
        public Enumeration<String> getParameterNames() {
            if (nikita) {
                if (args.getInternalObject() instanceof  Hashtable) {
                    return ((Hashtable) args.getInternalObject()).keys();
                }                 
            }                
            return super.getParameterNames();
        }
        public Map<String, String[]> getParameterMap() {
            return super.getParameterMap(); 
        }
        private Nset getmobilegenerator = null;
        private Nset getMobileGeneratorA(){
            if (getmobilegenerator!=null) {
            }else if (getParameter("mobilegenerator")!=null) {
                getmobilegenerator = Nset.readJSON(getParameter("mobilegenerator")) ;  
                getMobileGeneratorReload();
            }
            return getmobilegenerator;
        }
        public Nset getMobileGenerator(){
            return getMobileGeneratorA();
        }
        public Nset getMobileGeneratorReload(){
            getMobileGeneratorA();
            String vstream = String.valueOf(request.getSession().getAttribute(NikitaResponse.getCookieOrSessionKey("@+SESSION-WEB-FORMSTREAM-DATA")));
            if (!vstream.trim().equalsIgnoreCase("")) {
                Nset n = Nset.readJSON(vstream);
                if (getmobilegenerator!=null && getmobilegenerator.isNsetObject() && n.isNset()) {
                    Hashtable vvv = (Hashtable)  getmobilegenerator.getInternalObject();
                    String[] keys = n.getObjectKeys();
                    for (int i = 0; i < keys.length; i++) {
                        vvv.put(keys[i], n.getData(keys[i]).getInternalObject());
                    }
                }
            }
            return getMobileGeneratorA();
        }
            
    }
    
 private class GZIPServletRequestWrapper extends HttpServletRequestWrapper{
        public GZIPServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }
        public ServletInputStream getInputStream() throws IOException {
            return new GZIPServletInputStream(super.getInputStream());
        }
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(new GZIPServletInputStream(super.getInputStream())));
        }
    }

    private class GZIPServletInputStream extends ServletInputStream{
        private InputStream input;
        public GZIPServletInputStream(InputStream input) throws IOException {
            this.input = new GZIPInputStream(input);
        }        
        public int read() throws IOException {
            return input.read();
        }
        public boolean isFinished() {
             return false;
        }
        public boolean isReady() {
            return true;
        }
        public void setReadListener(ReadListener readListener) {
            
        }
    }
    
    public boolean isSupportUser(HttpServletRequest httpRequest){
        String support= httpRequest.getParameter("nodatazip");
        if (support!=null && support.contains("true")) {
            return false;
        }
        return true;
    } 
     
    private boolean acceptsGZipEncoding(HttpServletRequest httpRequest) { 
        String acceptEncoding =  httpRequest.getHeader("Accept-Encoding");
        return acceptEncoding != null && acceptEncoding.indexOf("gzip") != -1 && isSupportUser(httpRequest);
    }
    public void destroy() {
        //close any resources here
    }
 
 
}
