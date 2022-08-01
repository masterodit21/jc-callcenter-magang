/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.nikita.generator;

import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.utility.Utility;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Random;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author rkrzmail
 */
public class NikitaServletResponse implements HttpServletResponse{
    private HttpServletResponse curr;
    public HttpServletRequest req ;
    
    private Hashtable<String, Object> virtual = new Hashtable();
    
    private Hashtable<String, String> parameter = new Hashtable<String, String>();
    public Hashtable<String, String> getInternalParameter(){
        return  parameter;
    }
    public void setParameter(String name, String value){
        parameter.put(name, value);
        try {
            //data.setAttribute(name, name);
        } catch (Exception e) {}            
    }    
    public String getParameter(String name){
        if (parameter.get(name)!=null) {
                return (String)parameter.get(name);
        }
        try {
            String s = req.getParameter(name);
            return s!=null?s:"";
        } catch (Exception e) {  }
        return "";
    }
    
    public void setVirtualStatic(String key, Object data){
        virtual.put(String.valueOf(key).toLowerCase().trim(), data);
    }
    public Object getVirtualStatic(String key){
        return virtual.get(String.valueOf(key).toLowerCase().trim());
    }  
    private NikitaMobile nikitaMobile;
    public NikitaMobile getNikitaMobile(HttpServletRequest resRequest){
        if (nikitaMobile==null && resRequest!=null && resRequest.getSession()!=null) {
            Object object = resRequest.getSession().getAttribute("NikitaMobileActivity");
            if (object instanceof  String ) {
            }else{
                StringBuffer sb = new StringBuffer();
                Random randomGenerator = new Random();
                for (int idx = 1; idx <= 16; ++idx){
                    sb.append( randomGenerator.nextInt(100)  );
                }
                String key =sb.toString()+Utility.MD5(Utility.Now()).substring(0,6);
                resRequest.getSession().setAttribute("NikitaMobileActivity", key);
            }
            String key = (String)resRequest.getSession().getAttribute("NikitaMobileActivity");               
            nikitaMobile = new NikitaMobile(key);
        }else  if (nikitaMobile==null){
            nikitaMobile = new NikitaMobile("");
        }
        return  nikitaMobile ;
    }
    
    private Hashtable<String, NikitaConnection> ncarr = new Hashtable<String, NikitaConnection>();
    public NikitaConnection getConnection(){        
        return ncarr.get(NikitaConnection.LOGIC);
    }
    public NikitaConnection getConnection(String s){
        s=s.equals("")?NikitaConnection.DEFAULT:s;                
        
        
       
             
        NikitaConnection nc =  ncarr.get(s);
        if (nc==null) {
            nc = NikitaConnection.getConnection(s);
            ncarr.put(s, nc);   
        }else{
            if ( nc.isClosed() ) {
                nc = NikitaConnection.getConnection(s);
                ncarr.put(s, nc);   
            }
        }
        return nc;
    }
    public void closeAllConnection(){
        Enumeration<NikitaConnection> en =ncarr.elements();
        while (en.hasMoreElements()) {
            NikitaConnection nc = en.nextElement();
            nc.closeConnection();
        }
    } 
    
    
    public NikitaServletResponse(HttpServletRequest req, HttpServletResponse curr){
        this.curr=curr;
        this.req=req;
    }
    
    @Override
    public String getCharacterEncoding() {
        return curr.getCharacterEncoding();
    }

    @Override
    public String getContentType() {
        return curr.getContentType();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return curr.getOutputStream();
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return curr.getWriter();
    }

    @Override
    public void setCharacterEncoding(String charset) {
        curr.setCharacterEncoding(charset);
    }

    @Override
    public void setContentLength(int len) {
        curr.setContentLength(len);
    }

    @Override
    public void setContentLengthLong(long len) {
        curr.setContentLengthLong(len);
    }

    @Override
    public void setContentType(String type) {
        curr.setContentType(type);
    }

    @Override
    public void setBufferSize(int size) {
        curr.setBufferSize(size);
    }

    @Override
    public int getBufferSize() {
        return curr.getBufferSize();
    }

    @Override
    public void flushBuffer() throws IOException {
        curr.flushBuffer();
    }

    @Override
    public void resetBuffer() {
        curr.resetBuffer();
    }

    @Override
    public boolean isCommitted() {
        return curr.isCommitted();
    }

    @Override
    public void reset() {
        curr.reset();
    }

    @Override
    public void setLocale(Locale loc) {
        curr.setLocale(loc);
    }

    @Override
    public Locale getLocale() {
        return curr.getLocale();
    }

    @Override
    public void addCookie(Cookie cookie) {
        curr.addCookie(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
        return curr.containsHeader(name);
    }

    @Override
    public String encodeURL(String url) {
        return curr.encodeURL(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
        return curr.encodeRedirectURL(url);
    }

    @Override
    public String encodeUrl(String url) {
        return curr.encodeUrl(url);
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return curr.encodeRedirectUrl(url);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        curr.sendError(sc,msg);
    }

    @Override
    public void sendError(int sc) throws IOException {
        curr.sendError(sc);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        curr.sendRedirect(location);
    }

    @Override
    public void setDateHeader(String name, long date) {
        curr.setDateHeader(name, date);
    }

    @Override
    public void addDateHeader(String name, long date) {
        curr.addDateHeader(name, date);
    }

    @Override
    public void setHeader(String name, String value) {
        curr.setHeader(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        curr.addHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        curr.setIntHeader(name,value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        curr.addIntHeader(name,value);
    }

    @Override
    public void setStatus(int sc) {
        curr.setStatus(sc);
    }

    @Override
    public void setStatus(int sc, String sm) {
        curr.setStatus(sc,sm);
    }

    @Override
    public int getStatus() {
        return curr.getStatus();
    }

    @Override
    public String getHeader(String name) {
        return curr.getHeader(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return curr.getHeaders(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        return curr.getHeaderNames();
    }
    
}
