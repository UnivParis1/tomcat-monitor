/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univparis1.tomcatmonitor;

import java.lang.reflect.Method;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.apache.catalina.Session;

public class Tools {
    public static String getCasUid(Session session) {
        try {
            HttpSession httpSession = session.getSession();

            // Ancien client Yale
            String uid = (String)httpSession.getAttribute("edu.yale.its.tp.cas.client.filter.user");
            if (uid != null)
                return uid;

            // Nouveau client Jasig
            Object assertion = httpSession.getAttribute("_const_cas_assertion_");
            if (assertion == null)
                return null;

            Method getPrincipal = assertion.getClass().getMethod("getPrincipal");
            Object principal = getPrincipal.invoke(assertion);

            Method getAttributes = principal.getClass().getMethod("getAttributes");
            Map map = (Map)getAttributes.invoke(principal);
            uid = (String)map.get("uid");

            return uid;
        }
        catch (Exception e) {
            return null;
        }
    }
}
