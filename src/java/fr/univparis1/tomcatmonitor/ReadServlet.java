/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univparis1.tomcatmonitor;

import java.io.IOException;
import java.io.PrintWriter;
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
import org.apache.tomcat.util.modeler.Registry;

/**
 *
 * @author vincent
 */
public class ReadServlet extends HttpServlet implements ContainerServlet {
    private Wrapper wrapper;
    private Host host;
    private Engine engine;
    
    private static class Result {
        // Memory
        private long memoryFree = 0;
        private long memoryTotal = 0;
        private long memoryMax = 0;
        private int threadsService = 0;
        private int threadsKeepalive = 0;
        private int threadsTotal = 0;
        private int requestsTotal = 0;
        private int requestsError = 0;
        private int sessionsTotal = 0;
        private int sessionsAuthenticated = 0;
        private int c3p0MaxPoolSize = 0;
        private int c3p0NumConnections = 0;
        private int c3p0NumBusyConnections = 0;

        public long getMemoryFree() {
            return memoryFree;
        }

        public void setMemoryFree(long memoryFree) {
            this.memoryFree = memoryFree;
        }

        public long getMemoryTotal() {
            return memoryTotal;
        }

        public void setMemoryTotal(long memoryTotal) {
            this.memoryTotal = memoryTotal;
        }

        public long getMemoryUsed() {
            return memoryTotal - memoryFree;
        }

        public long getMemoryMax() {
            return memoryMax;
        }

        public void setMemoryMax(long memoryMax) {
            this.memoryMax = memoryMax;
        }

        public int getThreadsService() {
            return threadsService;
        }

        public void addThreadsService() {
            threadsService++;
        }

        public int getThreadsKeepalive() {
            return threadsKeepalive;
        }

        public void addThreadsKeepalive() {
            threadsKeepalive++;
        }

        public int getThreadsTotal() {
            return threadsTotal;
        }

        public void addThreadsTotal() {
            threadsTotal++;
        }

        public int getThreadsOther() {
            return threadsTotal - threadsService - threadsKeepalive;
        }

        public int getRequestsTotal() {
            return requestsTotal;
        }

        public void setRequestsTotal(int requestsTotal) {
            this.requestsTotal = requestsTotal;
        }

        public int getRequestsError() {
            return requestsError;
        }

        public void setRequestsError(int requestsError) {
            this.requestsError = requestsError;
        }

        public int getRequestsSuccessful() {
            return requestsTotal - requestsError;
        }

        public int getSessionsTotal() {
            return sessionsTotal;
        }

        public void addSessionsTotal() {
            sessionsTotal++;
        }

        public int getSessionsAuthenticated() {
            return sessionsAuthenticated;
        }

        public void addSessionsAuthenticated() {
            sessionsAuthenticated++;
        }

        public int getSessionsAnonymous() {
            return sessionsTotal - sessionsAuthenticated;
        }

        public int getC3p0MaxPoolSize() {
            return c3p0MaxPoolSize;
        }

        public void setC3p0MaxPoolSize(int c3p0MaxPoolSize) {
            this.c3p0MaxPoolSize = c3p0MaxPoolSize;
        }

        public int getC3p0NumConnections() {
            return c3p0NumConnections;
        }

        public void setC3p0NumConnections(int c3p0NumConnections) {
            this.c3p0NumConnections = c3p0NumConnections;
        }

        public int getC3p0NumBusyConnections() {
            return c3p0NumBusyConnections;
        }

        public void setC3p0NumBusyConnections(int c3p0NumBusyConnections) {
            this.c3p0NumBusyConnections = c3p0NumBusyConnections;
        }
    }

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
            Result result = new Result();
            String var = request.getParameter("var");
            String context = request.getParameter("context");
            if (var == null) {
                getMemoryState(result);
                out.println("memory.total=" + result.getMemoryTotal());
                out.println("memory.used=" + result.getMemoryUsed());
                out.println("memory.free=" + result.getMemoryFree());
                out.println("memory.max=" + result.getMemoryMax());

                getThreadsState(result);
                out.println("threads.total=" + result.getThreadsTotal());
                out.println("threads.service=" + result.getThreadsService());
                out.println("threads.keepalive=" + result.getThreadsKeepalive());
                out.println("threads.other=" + result.getThreadsOther());

                getGlobalRequestProcessorState(result);
                out.println("requests.total=" + result.getRequestsTotal());
                out.println("requests.error=" + result.getRequestsError());
                out.println("requests.successful=" + result.getRequestsSuccessful());

                if (context != null) {
                    getContextState(result, context);
                    out.println("sessions.total=" + result.getSessionsTotal());
                    out.println("sessions.authenticated=" + result.getSessionsAuthenticated());
                    out.println("sessions.anonymous=" + result.getSessionsAnonymous());

                    getC3p0State(result, context);
                    out.println("c3p0.maxPoolSize=" + result.getC3p0MaxPoolSize());
                    out.println("c3p0.numConnections=" + result.getC3p0NumConnections());
                    out.println("c3p0.numBusyConnections=" + result.getC3p0NumBusyConnections());
                }
            }
            else if (var.equals("memory.total")) {
                getMemoryState(result);
                out.println(result.getMemoryTotal());
            }
            else if (var.equals("memory.used")) {
                getMemoryState(result);
                out.println(result.getMemoryUsed());
            }
            else if (var.equals("memory.free")) {
                getMemoryState(result);
                out.println(result.getMemoryFree());
            }
            else if (var.equals("memory.max")) {
                getMemoryState(result);
                out.println(result.getMemoryMax());
            }
            else if (var.equals("threads.total")) {
                getThreadsState(result);
                out.println(result.getThreadsTotal());
            }
            else if (var.equals("threads.service")) {
                getThreadsState(result);
                out.println(result.getThreadsService());
            }
            else if (var.equals("threads.keepalive")) {
                getThreadsState(result);
                out.println(result.getThreadsKeepalive());
            }
            else if (var.equals("threads.other")) {
                getThreadsState(result);
                out.println(result.getThreadsOther());
            }
            else if (var.equals("requests.total")) {
                getGlobalRequestProcessorState(result);
                out.println(result.getRequestsTotal());
            }
            else if (var.equals("requests.error")) {
                getGlobalRequestProcessorState(result);
                out.println(result.getRequestsError());
            }
            else if (var.equals("requests.successful")) {
                getGlobalRequestProcessorState(result);
                out.println(result.getRequestsSuccessful());
            }
            else if (var.equals("sessions.total")) {
                getContextState(result, context);
                out.println(result.getSessionsTotal());
            }
            else if (var.equals("sessions.authenticated")) {
                getContextState(result, context);
                out.println(result.getSessionsAuthenticated());
            }
            else if (var.equals("sessions.anonymous")) {
                getContextState(result, context);
                out.println(result.getSessionsAnonymous());
            }
            else if (var.equals("c3p0.maxPoolSize")) {
                getC3p0State(result, context);
                out.println(result.getC3p0MaxPoolSize());
            }
            else if (var.equals("c3p0.numConnections")) {
                getC3p0State(result, context);
                out.println(result.getC3p0NumConnections());
            }
            else if (var.equals("c3p0.numBusyConnections")) {
                getC3p0State(result, context);
                out.println(result.getC3p0NumBusyConnections());
            }
            else {
                throw new IllegalArgumentException("Unknown variable: " + var);
/*
                out.println("Unknown variable: " + var);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
*/
            }
        }
        catch (Exception e) {
            //response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            e.printStackTrace(out);
            //out.println(e.getClass().getName() + ": " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {            
            out.close();
        }
    }
    
    private void getMemoryState(Result result) {
        Runtime runtime = Runtime.getRuntime();
        result.setMemoryTotal(runtime.totalMemory());
        result.setMemoryFree(runtime.freeMemory());
        result.setMemoryMax(runtime.maxMemory());
    }

    private void getThreadsState(Result result) {
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            String onStr = "*:type=RequestProcessor,worker=jk-*,*";
            ObjectName objectName = new ObjectName(onStr);
            Set set = mBeanServer.queryMBeans(objectName, null);
            for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                ObjectInstance oi = (ObjectInstance) iterator.next();
                ObjectName rpName = oi.getObjectName();
                
                result.addThreadsTotal();
                
                Integer stageValue = (Integer)mBeanServer.getAttribute(rpName, "stage");
                int stage = stageValue.intValue();
                switch (stage) {
                    case (3/*org.apache.coyote.Constants.STAGE_SERVICE*/):
                        result.addThreadsService();
                        break;

                    case (6/*org.apache.coyote.Constants.STAGE_KEEPALIVE*/):
                        result.addThreadsKeepalive();
                        break;
                }
            }
        } catch (JMException e) {
            throw new RuntimeException(e);
        }
    }

    private void getGlobalRequestProcessorState(Result result) {
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            String onStr = "*:type=GlobalRequestProcessor,name=jk-*,*";
            ObjectName objectName = new ObjectName(onStr);
            Set set = mBeanServer.queryMBeans(objectName, null);
            for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                ObjectInstance oi = (ObjectInstance) iterator.next();
                ObjectName rpName = oi.getObjectName();
                
                result.setRequestsTotal(((Integer)mBeanServer.getAttribute(rpName, "requestCount")).intValue());
                result.setRequestsError(((Integer)mBeanServer.getAttribute(rpName, "errorCount")).intValue());
                
                // Normalement, il n'y a qu'un seul GlobalRequestProcessor par type de connecteur
                if (iterator.hasNext())
                    throw new RuntimeException("Plusieurs GlobalRequestProcessor: " + onStr);
            }
        } catch (JMException e) {
            throw new RuntimeException(e);
        }
    }

    private void getC3p0State(Result result, String contextPath) {
        if (contextPath == null)
            throw new IllegalArgumentException("Missing argument: context");

        String contextDataSourceName = contextPath.substring(1);
        boolean found = false;

        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            String onStr = "com.mchange.v2.c3p0:type=PooledDataSource[*]";
            ObjectName objectName = new ObjectName(onStr);
            Set set = mBeanServer.queryMBeans(objectName, null);
            for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                ObjectInstance oi = (ObjectInstance) iterator.next();
                ObjectName rpName = oi.getObjectName();

                String dataSourceName = (String)mBeanServer.getAttribute(rpName, "dataSourceName");
                if (!dataSourceName.equals(contextDataSourceName))
                    continue;

                // S'il y a plusieurs PooledDataSource avec le même dataSourceName, on ne sait pas laquelle choisir...
                if (found)
                    throw new RuntimeException("Plusieurs PooledDataSource avec dataSourceName=\"" + contextDataSourceName + "\".");

                result.setC3p0MaxPoolSize(((Integer)mBeanServer.getAttribute(rpName, "maxPoolSize")));
                result.setC3p0NumConnections(((Integer)mBeanServer.getAttribute(rpName, "numConnections")));
                result.setC3p0NumBusyConnections(((Integer)mBeanServer.getAttribute(rpName, "numBusyConnections")));
                
                found = true;
            }
        } catch (JMException e) {
            throw new RuntimeException(e);
        }
    }

    private void getContextState(Result result, String contextPath) {
        if (contextPath == null)
            throw new IllegalArgumentException("Missing argument: context");

        String internalContextPath;
        if (contextPath.equals("/"))
            internalContextPath = "";
        else
            internalContextPath = contextPath;

        Container[] children = host.findChildren();
        for (int i = 0; i < children.length; i++) {
            Context context = (Context)children[i];
            if (context.getPath().equals(internalContextPath)) {
                getSessionsState(result, context);
                return;
            }
        }
        
        throw new IllegalArgumentException("Unknown context : " + contextPath);
    }

    private void getSessionsState(Result result, Context context) {
        Manager manager = context.getManager();
        Session[] sessions = manager.findSessions();
        for (Session session : sessions) {
            result.addSessionsTotal();

            String uid = Tools.getCasUid(session);
            if (uid != null)
                result.addSessionsAuthenticated();
        }
    }
}
