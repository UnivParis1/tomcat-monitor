/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univparis1.tomcatmonitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.JMException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.QueryExp;
import javax.management.ReflectionException;
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
        private long memoryMax = 0;
        private long memoryTotal = 0;
        private long memoryFree = 0;
        private int threadsMax = 0;
        private int threadsTotal = 0;
        private int threadsService = 0;
        private int threadsKeepalive = 0;
        private int threadsOther = 0;
        private int threadsReady = 0;
        private int requestsTotal = 0;
        private int requestsError = 0;
        private int sessionsTotal = 0;
        private int sessionsAuthenticated = 0;
        private int c3p0MaxPoolSize = 0;
        private int c3p0NumConnections = 0;
        private int c3p0NumBusyConnections = 0;
        private int sopraPoolMaxObjet = 0;
        private int sopraPoolObjetUtilisees = 0;

        public long getMemoryMax() {
            return memoryMax;
        }

        public void setMemoryMax(long memoryMax) {
            this.memoryMax = memoryMax;
        }

        public long getMemoryTotal() {
            return memoryTotal;
        }

        public void setMemoryTotal(long memoryTotal) {
            this.memoryTotal = memoryTotal;
        }

        public long getMemoryFree() {
            return memoryFree;
        }

        public void setMemoryFree(long memoryFree) {
            this.memoryFree = memoryFree;
        }

        public long getMemoryUsed() {
            return memoryTotal - memoryFree;
        }

        public int getThreadsMax() {
            return threadsMax;
        }

        public void setThreadsMax(int threadsMax) {
            this.threadsMax = threadsMax;
        }

        public int getThreadsTotal() {
            return threadsTotal;
        }

        public void addThreadsTotal() {
            threadsTotal++;
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

        public int getThreadsOther() {
            return threadsOther;
        }

        public void addThreadsOther() {
            threadsOther++;
        }

        public int getThreadsReady() {
            return threadsReady;
        }

        public void addThreadsReady() {
            threadsReady++;
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

        public int getC3p0NumIdleConnections() {
            return c3p0NumConnections - c3p0NumBusyConnections;
        }

        public int getSopraPoolMaxObjet() {
            return sopraPoolMaxObjet;
        }

        public void setSopraPoolMaxObjet(int sopraPoolMaxObjet) {
            this.sopraPoolMaxObjet = sopraPoolMaxObjet;
        }

        public int getSopraPoolObjetUtilisees() {
            return sopraPoolObjetUtilisees;
        }

        public void setSopraPoolObjetUtilisees(int sopraPoolObjetUtilisees) {
            this.sopraPoolObjetUtilisees = sopraPoolObjetUtilisees;
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
                out.println("memory.max=" + result.getMemoryMax());
                out.println("memory.total=" + result.getMemoryTotal());
                out.println("memory.used=" + result.getMemoryUsed());
                out.println("memory.free=" + result.getMemoryFree());

                getConnectorState(result);
                out.println("threads.max=" + result.getThreadsMax());

                getThreadsState(result);
                out.println("threads.total=" + result.getThreadsTotal());
                out.println("threads.service=" + result.getThreadsService());
                out.println("threads.keepalive=" + result.getThreadsKeepalive());
                out.println("threads.other=" + result.getThreadsOther());
                out.println("threads.ready=" + result.getThreadsReady());

                getGlobalRequestProcessorState(result);
                out.println("requests.total=" + result.getRequestsTotal());
                out.println("requests.error=" + result.getRequestsError());
                out.println("requests.successful=" + result.getRequestsSuccessful());

                if (context != null) {
                    getContextState(result, context);
                    out.println("sessions.total=" + result.getSessionsTotal());
                    out.println("sessions.authenticated=" + result.getSessionsAuthenticated());
                    out.println("sessions.anonymous=" + result.getSessionsAnonymous());
                    out.println("sopraPool.maxObjet=" + result.getSopraPoolMaxObjet());
                    out.println("sopraPool.objetUtilisees=" + result.getSopraPoolObjetUtilisees());

                    getC3p0State(result, context);
                    out.println("c3p0.maxPoolSize=" + result.getC3p0MaxPoolSize());
                    out.println("c3p0.numConnections=" + result.getC3p0NumConnections());
                    out.println("c3p0.numBusyConnections=" + result.getC3p0NumBusyConnections());
                    out.println("c3p0.numIdleConnections=" + result.getC3p0NumIdleConnections());
                }
            }
            else if (var.equals("memory.max")) {
                getMemoryState(result);
                out.println(result.getMemoryMax());
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
            else if (var.equals("threads.max")) {
                getConnectorState(result);
                out.println(result.getThreadsMax());
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
            else if (var.equals("threads.ready")) {
                getThreadsState(result);
                out.println(result.getThreadsReady());
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
            else if (var.equals("sopraPool.maxObjet")) {
                getContextState(result, context);
                out.println(result.getSopraPoolMaxObjet());
            }
            else if (var.equals("sopraPool.objetUtilisees")) {
                getContextState(result, context);
                out.println(result.getSopraPoolObjetUtilisees());
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
            else if (var.equals("c3p0.numIdleConnections")) {
                getC3p0State(result, context);
                out.println(result.getC3p0NumIdleConnections());
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
        result.setMemoryMax(runtime.maxMemory());
        result.setMemoryTotal(runtime.totalMemory());
        result.setMemoryFree(runtime.freeMemory());
    }

    private static int getIntValue(MBeanServer mBeanServer, ObjectName name, String attribute, int defaultValue) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        Object value = mBeanServer.getAttribute(name, attribute);
        if (value == null)
            return defaultValue;
        else if (value instanceof Integer)
            return ((Integer)value).intValue();
        else if (value instanceof String)
            return Integer.parseInt((String)value);
        else
            throw new RuntimeException("Invalid type: " + value.getClass().getName());
    }

    private void getConnectorState(Result result) {
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            ObjectName objectName = new ObjectName("Catalina:type=Connector,*");
            QueryExp query = Query.eq(Query.attr("protocol"), Query.value("AJP/1.3"));
            Set set = mBeanServer.queryMBeans(objectName, query);
            for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                ObjectInstance oi = (ObjectInstance) iterator.next();
                ObjectName rpName = oi.getObjectName();

                // Avec Tomcat 6.0.32 la valeur est de type String
                // De plus la propriété vaut null si l'attribut maxThreads n'est pas explicitement défini dans server.xml
                int maxThreads = getIntValue(mBeanServer, rpName, "maxThreads", 0);
                result.setThreadsMax(maxThreads);
            }
        } catch (JMException e) {
            throw new RuntimeException(e);
        }
    }

    private void getThreadsState(Result result) {
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            String onStr = "Catalina:type=RequestProcessor,worker=\"ajp-*\",*"; // Tomcat 7
            ObjectName objectName = new ObjectName(onStr);
            Set set = mBeanServer.queryMBeans(objectName, null);
            if (set.isEmpty()) {
                onStr = "Catalina:type=RequestProcessor,worker=jk-*,*"; // Tomcat 6
                objectName = new ObjectName(onStr);
                set = mBeanServer.queryMBeans(objectName, null);
            }
            for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
                ObjectInstance oi = (ObjectInstance) iterator.next();
                ObjectName rpName = oi.getObjectName();
                
                result.addThreadsTotal();

                // Voir conversion stage/état dans org.apache.catalina.manager.StatusTransformer.writeProcessorState()
                Integer stageValue = (Integer)mBeanServer.getAttribute(rpName, "stage");
                int stage = stageValue.intValue();
                switch (stage) {
                    case (3/*org.apache.coyote.Constants.STAGE_SERVICE*/):
                        result.addThreadsService();
                        break;

                    case (6/*org.apache.coyote.Constants.STAGE_KEEPALIVE*/):
                        result.addThreadsKeepalive();
                        break;

                    case (0/*org.apache.coyote.Constants.STAGE_NEW*/):
                        result.addThreadsReady();
                        break;

                    case (7/*org.apache.coyote.Constants.STAGE_ENDED*/):
                        result.addThreadsReady();
                        break;

                    default:
                        result.addThreadsOther();
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
            String onStr = "Catalina:type=GlobalRequestProcessor,name=\"ajp-*\""; // Tomcat 7
            ObjectName objectName = new ObjectName(onStr);
            Set set = mBeanServer.queryMBeans(objectName, null);
            if (set.isEmpty()) {
                onStr = "Catalina:type=GlobalRequestProcessor,name=jk-*";  // Tomcat 6
                objectName = new ObjectName(onStr);
                set = mBeanServer.queryMBeans(objectName, null);
            }
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
                getSopraPoolState(result, context);
                return;
            }
        }
        
        throw new IllegalArgumentException("Unknown context : " + contextPath);
    }

    private void getSopraPoolState(Result result, Context context) {
        try {
            ClassLoader loader = context.getLoader().getClassLoader();
            Class poolManagerClass = loader.loadClass("com.sopragroup.fwk.util.pool.PoolManager");

            Method getInstance = poolManagerClass.getMethod("getInstance");
            Object poolManager = getInstance.invoke(null);

            Method getNomPoolDefaut = poolManager.getClass().getMethod("getNomPoolDefaut");
            String nomPoolDefaut = (String)getNomPoolDefaut.invoke(poolManager);

            Method donnerPool = poolManager.getClass().getDeclaredMethod("donnerPool", String.class);
            donnerPool.setAccessible(true);
            Object poolConnection = donnerPool.invoke(poolManager, nomPoolDefaut);

            Field fieldMaxObjet = poolConnection.getClass().getSuperclass().getDeclaredField("m_nbMaxObjet");
            fieldMaxObjet.setAccessible(true);
            result.setSopraPoolMaxObjet(fieldMaxObjet.getInt(poolConnection));

            Field fieldObjetUtilisees = poolConnection.getClass().getSuperclass().getDeclaredField("m_nbObjetUtilisees");
            fieldObjetUtilisees.setAccessible(true);
            result.setSopraPoolObjetUtilisees(fieldObjetUtilisees.getInt(poolConnection));
        }
        catch (Exception e) {
            // Ignorer
        }
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
