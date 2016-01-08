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

            // Spring Security
            Object securityContext = httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
            // class org.springframework.security.core.context.SecurityContextImpl
            if (securityContext != null) {
                Method getAuthentication = securityContext.getClass().getMethod("getAuthentication");
                Object authentication = getAuthentication.invoke(securityContext);
                // org.springframework.security.authentication.UsernamePasswordAuthenticationToken

                Method getPrincipal = authentication.getClass().getMethod("getPrincipal");
                Object principal = getPrincipal.invoke(authentication);
                if (principal.getClass().getName().equals("gouv.education.apogee.commun.transverse.dto.security.UserDTO")) {
                    // Application AMUE SNW :
                    // authentication.getName() renvoie le numéro Harpège,
                    // donc utiliser plutôt authentication.getPrincipal().getUid()
                    Method getUid = principal.getClass().getMethod("getUid");
                    String uid = (String)getUid.invoke(principal);
                    return uid;
                }

                Method getName = authentication.getClass().getMethod("getName");
                String name = (String)getName.invoke(authentication);
                    return name;
            }

            return null;
        }
        catch (Exception e) {
            return null;
        }
    }
}
