/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univparis1.tomcatmonitor;

import java.lang.reflect.Method;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Session;

public class Tools {
    public static String getCasUid(Session session) {
        try {
            HttpSession httpSession = session.getSession();

            // Ancien client Yale
            String yaleUser = (String)httpSession.getAttribute("edu.yale.its.tp.cas.client.filter.user");
            if (yaleUser != null)
                return yaleUser;

            // Nouveau client Jasig
            Object assertion = httpSession.getAttribute("_const_cas_assertion_");
            // class org.jasig.cas.client.validation.AssertionImpl
            if (assertion != null) {
                Method getPrincipal = assertion.getClass().getMethod("getPrincipal");
                Object principal = getPrincipal.invoke(assertion);
                // class org.jasig.cas.client.authentication.AttributePrincipalImpl

                Method getName = principal.getClass().getMethod("getName");
                String name = (String)getName.invoke(principal);
                return name;
            }

            return null;
        }
        catch (Exception e) {
            return null;
        }
    }
}
