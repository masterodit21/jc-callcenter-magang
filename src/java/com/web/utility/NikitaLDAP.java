/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.web.utility;

import com.rkrzmail.nikita.data.Nset;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

/**
 *
 * @author rkrzmail
 */
public class NikitaLDAP {    
    
    public static String ldapNikita(String ldapAdServer, String ldapSearchBase, String ldapUsername, String ldapPassword, String ldapAccountToLookup) throws NamingException {        
        //ldapAdServer = "ldap://ad.your-server.com:389";
        //final String ldapSearchBase = "dc=ad,dc=my-domain,dc=com";
        
        //final String ldapUsername = "myLdapUsername";
        //final String ldapPassword = "myLdapPassword";
        
        //final String ldapAccountToLookup = "myOtherLdapUsername";
        
        
        Hashtable<String, Object> env = new Hashtable<String, Object>();
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        if(ldapUsername != null) {
            env.put(Context.SECURITY_PRINCIPAL, ldapUsername);
        }
        if(ldapPassword != null) {
            env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        }
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapAdServer);

        //ensures that objectSID attribute values
        //will be returned as a byte[] instead of a String
        env.put("java.naming.ldap.attributes.binary", "objectSID");
        
        // the following is helpful in debugging errors
        //env.put("com.sun.jndi.ldap.trace.ber", System.err);
        
        LdapContext ctx = new InitialLdapContext();        
        NikitaLDAP ldap = new NikitaLDAP();
        
        //1) lookup the ldap account
        SearchResult srLdapUser = ldap.findAccountByAccountName(ctx, ldapSearchBase, ldapAccountToLookup);
        
        //2) get the SID of the users primary group
        String primaryGroupSID = ldap.getPrimaryGroupSID(srLdapUser);
        
        //3) get the users Primary Group
        String primaryGroupName = ldap.findGroupBySID(ctx, ldapSearchBase, primaryGroupSID);
        
        Nset result = Nset.newObject();
        
        result.setData("status", "OK");
        result.setData("primarysid",primaryGroupSID);
        result.setData("primarygroupname", primaryGroupName);
        return result.toJSON();
    }
    
    private SearchResult findAccountByAccountName(DirContext ctx, String ldapSearchBase, String accountName) throws NamingException {

        String searchFilter = "(&(objectClass=user)(sAMAccountName=" + accountName + "))";

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration<SearchResult> results = ctx.search(ldapSearchBase, searchFilter, searchControls);

        SearchResult searchResult = null;
        if(results.hasMoreElements()) {
             searchResult = (SearchResult) results.nextElement();

            //make sure there is not another item available, there should be only 1 match
            if(results.hasMoreElements()) {
                System.err.println("Matched multiple users for the accountName: " + accountName);
                return null;
            }
        }
        
        return searchResult;
    }
    
    private String findGroupBySID(DirContext ctx, String ldapSearchBase, String sid) throws NamingException {
        
        String searchFilter = "(&(objectClass=group)(objectSid=" + sid + "))";

        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        
        NamingEnumeration<SearchResult> results = ctx.search(ldapSearchBase, searchFilter, searchControls);

        if(results.hasMoreElements()) {
            SearchResult searchResult = (SearchResult) results.nextElement();

            //make sure there is not another item available, there should be only 1 match
            if(results.hasMoreElements()) {
                System.err.println("Matched multiple groups for the group with SID: " + sid);
                return null;
            } else {
                return (String)searchResult.getAttributes().get("sAMAccountName").get();
            }
        }
        return null;
    }
    
    private String getPrimaryGroupSID(SearchResult srLdapUser) throws NamingException {
        byte[] objectSID = (byte[])srLdapUser.getAttributes().get("objectSid").get();
        String strPrimaryGroupID = (String)srLdapUser.getAttributes().get("primaryGroupID").get();
        
        String strObjectSid = decodeSID(objectSID);
        
        return strObjectSid.substring(0, strObjectSid.lastIndexOf('-') + 1) + strPrimaryGroupID;
    }
    
    /**
     * The binary data is in the form:
     * byte[0] - revision level
     * byte[1] - count of sub-authorities
     * byte[2-7] - 48 bit authority (big-endian)
     * and then count x 32 bit sub authorities (little-endian)
     * 
     * The String value is: S-Revision-Authority-SubAuthority[n]...
     * 
     * Based on code from here - http://forums.oracle.com/forums/thread.jspa?threadID=1155740&tstart=0
     */
    private static String decodeSID(byte[] sid) {
        
        final StringBuilder strSid = new StringBuilder("S-");

        // get version
        final int revision = sid[0];
        strSid.append(Integer.toString(revision));
        
        //next byte is the count of sub-authorities
        final int countSubAuths = sid[1] & 0xFF;
        
        //get the authority
        long authority = 0;
        //String rid = "";
        for(int i = 2; i <= 7; i++) {
           authority |= ((long)sid[i]) << (8 * (5 - (i - 2)));
        }
        strSid.append("-");
        strSid.append(Long.toHexString(authority));
        
        //iterate all the sub-auths
        int offset = 8;
        int size = 4; //4 bytes for each sub auth
        for(int j = 0; j < countSubAuths; j++) {
            long subAuthority = 0;
            for(int k = 0; k < size; k++) {
                subAuthority |= (long)(sid[offset + k] & 0xFF) << (8 * k);
            }
            
            strSid.append("-");
            strSid.append(subAuthority);
            
            offset += size;
        }
        
        return strSid.toString();    
    }
    
    
    
    
    
    
    
    
    
        // private final static String ldapURI = "ldaps://ldap.server.com/dc=ldap,dc=server,dc=com";
	//private final static String contextFactory = "com.sun.jndi.ldap.LdapCtxFactory";

	private static DirContext ldapContext (String ldapURI, String contextFactory) throws Exception {
		Hashtable<String,String> env = new Hashtable <String,String>();
		return ldapContext(ldapURI, contextFactory, env);
	}

	private static DirContext ldapContext (String ldapURI, String contextFactory, Hashtable <String,String>env) throws Exception {
            env.put(Context.SECURITY_AUTHENTICATION, "simple");	//new 17/10/2016
            env.put(Context.INITIAL_CONTEXT_FACTORY, contextFactory);
		env.put(Context.PROVIDER_URL, ldapURI);
		DirContext ctx = new InitialDirContext(env);
		return ctx;
	}

	private static String getUid (String ldapURI, String contextFactory, String user) throws Exception {
		DirContext ctx = ldapContext(ldapURI, contextFactory);

		String filter = "(uid=" + user + ")";
		SearchControls ctrl = new SearchControls();
		ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration answer = ctx.search("", filter, ctrl);

		String dn;
		if (answer.hasMore()) {
			SearchResult result = (SearchResult) answer.next();
			dn = result.getNameInNamespace();
		}
		else {
			dn = null;
		}
		answer.close();
		return dn;
	}

	public static boolean ldapNikitaDirect (String ldapURI, String contextFactory, String dn, String password) throws Exception {
		Hashtable<String,String> env = new Hashtable <String,String>();
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, dn);
		env.put(Context.SECURITY_CREDENTIALS, password);

		try {
                    ldapContext(ldapURI, contextFactory, env);                    
		}catch (javax.naming.AuthenticationException e) {
			return false;
		}
		return true;
	}

	public static String ldapNikita(String ldapURI, String user, String password) throws Exception {
                 //String ldapURI = "ldaps://ldap.server.com/dc=ldap,dc=server,dc=com";
	        String contextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
          
                Nset result = Nset.newObject(); 
                String dn = getUid(ldapURI, contextFactory, user );
		if (dn != null) {
			/* Found user - test password */
			if ( ldapNikitaDirect(ldapURI, contextFactory,  dn, password ) ) {
				 
				 
                                result.setData("statis", "OK");
                                result.setData("error", "");
                                result.setData("message", "authentication succeeded");
			}
			else {
				 
				 
                                result.setData("statis", "error");
                                result.setData("error", "authentication failed");
			}
		}
		else {
			 
                         result.setData("statis", "error");
                          result.setData("error", "user not found");
		}
                
                return result.toJSON();
	}
}
