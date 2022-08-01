/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.web.utility;

import java.util.Hashtable;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
/**
 *
 * @author rkrzmail
 */
public class LDAPLoginAuthentication {
    public LDAPLoginAuthentication() {
        // TODO Auto-generated constructor
    }

    ResourceBundle resBundle = ResourceBundle.getBundle("settings");

    @SuppressWarnings("unchecked")
    public String authenticateUser(String username, String password) {
        String strUrl = "success";
        Hashtable env = new Hashtable(11);
        boolean b = false;
        String Securityprinciple = "cn=" + username + "," + resBundle.getString("UserSearch");
        env.put(Context.INITIAL_CONTEXT_FACTORY, resBundle.getString("InitialContextFactory"));
        env.put(Context.PROVIDER_URL, resBundle.getString("Provider_url"));
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, Securityprinciple);
        env.put(Context.SECURITY_CREDENTIALS, password);

        try {
            // Create initial context
            DirContext ctx = new InitialDirContext(env);
            // Close the context when we're done
            b = true;
            ctx.close();

        } catch (NamingException e) {
            b = false;
        } finally {
            if (b) {
                strUrl = "success";
            } else {
                strUrl = "failer";
            }
        }
        return strUrl;
    }
}
