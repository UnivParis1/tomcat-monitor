/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univparis1.tomcatmonitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;
import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.Container;
import org.apache.catalina.ContainerServlet;
import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.Host;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.Wrapper;
import org.apache.catalina.util.ServerInfo;
import org.apache.tomcat.util.modeler.Registry;

/**
 *
 * @author vincent
 */
public class DefaultServlet extends HttpServlet implements ContainerServlet {
    private Wrapper wrapper;
    private Host host;
    private Engine engine;

    @Override
    public Wrapper getWrapper() {
        return wrapper;
    }

    @Override
    public void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;

        if (wrapper == null) {
            host = null;
            engine = null;
        }
        else {
            Context context = (Context)wrapper.getParent();
            host = (Host)context.getParent();
            engine = (Engine)host.getParent();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Lire l'heure une fois seulement
        long now = System.currentTimeMillis();

        // Créer une session pour les tests
        request.getSession();

        response.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            printTomcatState(out, now);
        } catch (Exception e) {
            e.printStackTrace(out);
        } finally {            
            out.close();
        }
    }

    private void printTomcatState(PrintWriter out, long now) throws ServletException, IOException {
        printOsState(out);
        printJvmState(out);
/*
        out.println("# Engine");
        out.println("Info: " + engine.getInfo());
*/
        out.println("# Contexts");
        out.println();

        Container[] children = host.findChildren();
        for (int i = 0; i < children.length; i++) {
            Context context = (Context)children[i];

            if (!context.getAvailable())
                continue;

            printContextState(out, context, now);
        }
        
        printRequestProcessorState(out);
    }

    private void printOsState(PrintWriter out) throws ServletException, IOException {
        out.println("# OS State");

        long[] result = new long[16];
        try {
            String methodName = "info";
            Class paramTypes[] = new Class[1];
            paramTypes[0] = result.getClass();
            Object paramValues[] = new Object[1];
            paramValues[0] = result;
            Method method = Class.forName("org.apache.tomcat.jni.OS").getMethod(methodName, paramTypes);
            method.invoke(null, paramValues);
        } catch (Throwable t) {
            out.println("Erreur JNI : " + t.getClass().getName());
            out.println();
            return;
        }
        
        out.println("Physical memory: " + result[0] + " octets");
        out.println("Available memory: " + result[1] + " octets");
        out.println("Total page file: " + result[2] + " octets");
        out.println("Free page file: " + result[3] + " octets");
        out.println("Memory load: " + result[6]);
        out.println("Process kernel time: " + result[11] + " us");
        out.println("Process user time: " + result[12] + " us");

        out.println();
    }
    
    private void printJvmState(PrintWriter out) throws ServletException, IOException {
        out.println("# JVM State");
        out.println("JVM Vendor: " + System.getProperty("java.vm.vendor"));
        out.println("JVM Version: " + System.getProperty("java.runtime.version"));
        out.println("Tomcat Version: " + ServerInfo.getServerInfo());

        Runtime runtime = Runtime.getRuntime();
        long freeMemory = runtime.freeMemory();
        long totalMemory = runtime.totalMemory();
        long usedMemory = totalMemory - freeMemory;
        //long maxMemory = runtime.maxMemory();
        //out.println("FreeMemory: " + freeMemory + " octets");
        out.println("UsedMemory: " + usedMemory + " octets");
        out.println("TotalMemory: " + totalMemory + " octets");
        //out.println("MaxMemory: " + maxMemory + " octets");

        out.println();
    }

    private void printContextState(PrintWriter out, Context context, long now) throws ServletException, IOException {
        String displayPath = context.getPath();
        if( displayPath.equals("") )
            displayPath = "/";

        out.println(displayPath);

        out.println("  SessionTimeout: " + context.getSessionTimeout() + " minutes");

        Manager manager = context.getManager();
        //out.println("  Info: " + manager.getInfo());
        out.println("  MaxInactiveInterval: " + manager.getMaxInactiveInterval() + " secondes");

        out.println("  ExpiredSessions: " + manager.getExpiredSessions());
        out.println("  MaxActive: " + manager.getMaxActive());
        out.println("  ActiveSessions: " + manager.getActiveSessions());
        Session[] sessions = manager.findSessions();
        //out.println("  Nombre de sessions: " + sessions.length);

        for (Session session : sessions) {
            printSessionState(out, context, session, now);
        }
        
        out.println();
    }

    private void printSessionState(PrintWriter out, Context context, Session session, long now) throws ServletException, IOException {
        out.println("   # " + session.getId());
        //out.println("   IdInternal: " + session.getIdInternal());
        //out.println("   Info: " + session.getInfo());
        out.println("   MaxInactiveInterval: " + session.getMaxInactiveInterval() + " secondes");
        long idle = (now - session.getLastAccessedTimeInternal()) / 1000;
        out.println("   Idle: " + idle + " secondes");

        String uid = Tools.getCasUid(session);
        if (uid != null)
            out.println("   Cas UID: " + uid);
        
        out.println();
    }
    
    private void printRequestProcessorState(PrintWriter out) throws ServletException, IOException {
        out.println("# Connectors");
        out.println();

        // Retrieve the MBean server
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();

        // Query Global Request Processors
        try {
            // Query Thread Pools
            String onStr = "*:type=ThreadPool,*";
            ObjectName objectName = new ObjectName(onStr);
            Set set = mBeanServer.queryMBeans(objectName, null);
            Iterator iterator = set.iterator();
            while (iterator.hasNext()) {
                ObjectInstance oi = (ObjectInstance) iterator.next();
                printConnectorState(out, mBeanServer, oi);
            }

            out.println();
        } catch (JMException e) {
            throw new RuntimeException(e);
        }
    }

    private void printThreadPoolState(PrintWriter out, MBeanServer mBeanServer, ObjectInstance oi) throws ServletException, IOException, JMException {
        ObjectName tpName = oi.getObjectName();
        out.println(" maxThreads: " + mBeanServer.getAttribute(tpName, "maxThreads"));
        out.println(" currentThreadCount: " + mBeanServer.getAttribute(tpName, "currentThreadCount"));
        out.println(" currentThreadsBusy: " + mBeanServer.getAttribute(tpName, "currentThreadsBusy"));
        //out.println(" keepAliveCount: " + mBeanServer.getAttribute(tpName, "keepAliveCount")); // Marche pas sur Tomcat 6.0.32 ??
        //out.println();
    }

    private void printGlobalRequestProcessorState(PrintWriter out, MBeanServer mBeanServer, ObjectInstance grpInstance) throws ServletException, IOException, JMException {
        ObjectName grpName = grpInstance.getObjectName();
        out.println(" # " + grpName);
        out.println(" maxTime: " + mBeanServer.getAttribute(grpName, "maxTime"));
        out.println(" processingTime: " + mBeanServer.getAttribute(grpName, "processingTime"));
        out.println(" requestCount: " + mBeanServer.getAttribute(grpName, "requestCount"));
        out.println(" errorCount: " + mBeanServer.getAttribute(grpName, "errorCount"));
        out.println(" bytesReceived: " + mBeanServer.getAttribute(grpName, "bytesReceived"));
        out.println(" bytesSent: " + mBeanServer.getAttribute(grpName, "bytesSent"));
        //out.println();
    }

    private void printRequestProcessorState(PrintWriter out, MBeanServer mBeanServer, ObjectInstance rpInstance) throws ServletException, IOException, JMException {
        // Source : StatusTransformer.writeProcessorState()
        ObjectName rpName = rpInstance.getObjectName();
        out.println("  # " + rpName);
        Integer stageValue = (Integer)mBeanServer.getAttribute(rpName, "stage");
        int stage = stageValue.intValue();
        boolean fullStatus = true;
        boolean showRequest = true;
        String stageStr = null;

        switch (stage) {

        case (1/*org.apache.coyote.Constants.STAGE_PARSE*/):
            stageStr = "P";
            fullStatus = false;
            break;
        case (2/*org.apache.coyote.Constants.STAGE_PREPARE*/):
            stageStr = "P";
            fullStatus = false;
            break;
        case (3/*org.apache.coyote.Constants.STAGE_SERVICE*/):
            stageStr = "S";
            break;
        case (4/*org.apache.coyote.Constants.STAGE_ENDINPUT*/):
            stageStr = "F";
            break;
        case (5/*org.apache.coyote.Constants.STAGE_ENDOUTPUT*/):
            stageStr = "F";
            break;
        case (7/*org.apache.coyote.Constants.STAGE_ENDED*/):
            stageStr = "R";
            fullStatus = false;
            break;
        case (6/*org.apache.coyote.Constants.STAGE_KEEPALIVE*/):
            stageStr = "K";
            fullStatus = true;
            showRequest = false;
            break;
        case (0/*org.apache.coyote.Constants.STAGE_NEW*/):
            stageStr = "R";
            fullStatus = false;
            break;
        default:
            // Unknown stage
            stageStr = "?";
            fullStatus = false;
        }
        
        out.println("  stage: " + stageStr);
        out.println("  protocol: " + mBeanServer.getAttribute(rpName, "protocol"));
        out.println("  method: " + mBeanServer.getAttribute(rpName, "method"));
        out.println("  currentUri: " + mBeanServer.getAttribute(rpName, "currentUri"));
        out.println("  currentQueryString: " + mBeanServer.getAttribute(rpName, "currentQueryString"));
    }

    private void printConnectorState(PrintWriter out, MBeanServer mBeanServer, ObjectInstance oi) throws ServletException, IOException, JMException {
        // Source : StatusTransformer.writeConnectorState()
        ObjectName tpName = oi.getObjectName();
        out.println("# " + tpName);
        String name = tpName.getKeyProperty("name");
        out.println(name);

        printThreadPoolState(out, mBeanServer, oi);
        
        ObjectName grpName = new ObjectName("Catalina:type=GlobalRequestProcessor,name=" + name);
        ObjectInstance grpInstance = mBeanServer.getObjectInstance(grpName);
        printGlobalRequestProcessorState(out, mBeanServer, grpInstance);
        
        // Query Request Processors
        String onStr = "*:type=RequestProcessor,worker=" + name + ",*";
        ObjectName objectName = new ObjectName(onStr);
        Set set = mBeanServer.queryMBeans(objectName, null);
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            ObjectInstance rpInstance = (ObjectInstance) iterator.next();
            printRequestProcessorState(out, mBeanServer, rpInstance);
        }

        out.println();
    }
}
