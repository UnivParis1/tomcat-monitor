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
        private long gcOldGenerationCollectionCount = 0;
        private long gcOldGenerationCollectionTime = 0;
        private long gcYoungGenerationCollectionCount = 0;
        private long gcYoungGenerationCollectionTime = 0;
        private double processCpuLoad = 0;
        private double systemCpuLoad = 0;
        private long processFileDescriptorCountMax = 0;
        private long processFileDescriptorCountOpen = 0;
        private int threadsMax = 0;
        private int threadsCount = 0;
        private int threadsBusy = 0;
        private int threadsService = 0;
        private int threadsKeepalive = 0;
        private int threadsReady = 0;
        private int threadsHttpMax = 0;
        private int threadsHttpCount = 0;
        private int threadsHttpBusy = 0;
        private int threadsHttpService = 0;
        private int threadsHttpKeepalive = 0;
        private int threadsHttpReady = 0;
        private int requestsTotal = 0;
        private int requestsError = 0;
        private int requestsHttpTotal = 0;
        private int requestsHttpError = 0;
        private int sessionsTotal = 0;
        private int sessionsAuthenticated = 0;
        private int c3p0MaxPoolSize = 0;
        private int c3p0NumConnections = 0;
        private int c3p0NumBusyConnections = 0;
        private int sopraPoolMaxObjet = 0;
        private int sopraPoolObjetUtilisees = 0;
        private int dbcpMaxTotal = 0;
        private int dbcpMaxActive = 0;
        private int dbcpNumActive = 0;
        private int dbcpMaxIdle = 0;
        private int dbcpNumIdle = 0;
        private int dbcpMinIdle = 0;
        private int hikariMaximumPoolSize = 0;
        private int hikariTotalConnections = 0;
        private int hikariActiveConnections = 0;
        private int hikariIdleConnections = 0;
        private int hikariMinimumIdle = 0;

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

        public long getGcOldGenerationCollectionCount() {
            return gcOldGenerationCollectionCount;
        }

        public void setGcOldGenerationCollectionCount(long gcOldGenerationCollectionCount) {
            this.gcOldGenerationCollectionCount = gcOldGenerationCollectionCount;
        }

        public long getGcOldGenerationCollectionTime() {
            return gcOldGenerationCollectionTime;
        }

        public void setGcOldGenerationCollectionTime(long gcOldGenerationCollectionTime) {
            this.gcOldGenerationCollectionTime = gcOldGenerationCollectionTime;
        }

        public long getGcYoungGenerationCollectionCount() {
            return gcYoungGenerationCollectionCount;
        }

        public void setGcYoungGenerationCollectionCount(long gcYoungGenerationCollectionCount) {
            this.gcYoungGenerationCollectionCount = gcYoungGenerationCollectionCount;
        }

        public long getGcYoungGenerationCollectionTime() {
            return gcYoungGenerationCollectionTime;
        }

        public void setGcYoungGenerationCollectionTime(long gcYoungGenerationCollectionTime) {
            this.gcYoungGenerationCollectionTime = gcYoungGenerationCollectionTime;
        }

        public double getProcessCpuLoad() {
            return processCpuLoad;
        }

        public void setProcessCpuLoad(double processCpuLoad) {
            this.processCpuLoad = processCpuLoad;
        }

        public double getSystemCpuLoad() {
            return systemCpuLoad;
        }

        public void setSystemCpuLoad(double processCpuLoad) {
            this.systemCpuLoad = processCpuLoad;
        }

        public long getProcessFileDescriptorCountMax() {
            return processFileDescriptorCountMax;
        }

        public void setProcessFileDescriptorCountMax(long processFileDescriptorCountMax) {
            this.processFileDescriptorCountMax = processFileDescriptorCountMax;
        }

        public long getProcessFileDescriptorCountOpen() {
            return processFileDescriptorCountOpen;
        }

        public void setProcessFileDescriptorCountOpen(long processFileDescriptorCountOpen) {
            this.processFileDescriptorCountOpen = processFileDescriptorCountOpen;
        }

        public int getThreadsMax() {
            return threadsMax;
        }

        public void setThreadsMax(int threadsMax) {
            this.threadsMax = threadsMax;
        }

        public int getThreadsCount() {
            return threadsCount;
        }

        public void setThreadsCount(int threadsCount) {
            this.threadsCount = threadsCount;
        }

        public int getThreadsBusy() {
            return threadsBusy;
        }

        public void setThreadsBusy(int threadsBusy) {
            this.threadsBusy = threadsBusy;
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

        public int getThreadsReady() {
            return threadsReady;
        }

        public void addThreadsReady() {
            threadsReady++;
        }

        public int getThreadsOther() {
            return getThreadsBusy() - getThreadsService() - getThreadsKeepalive();
        }

        public int getThreadsHttpMax() {
            return threadsHttpMax;
        }

        public void setThreadsHttpMax(int threadsHttpMax) {
            this.threadsHttpMax = threadsHttpMax;
        }

        public int getThreadsHttpCount() {
            return threadsHttpCount;
        }

        public void setThreadsHttpCount(int threadsHttpCount) {
            this.threadsHttpCount = threadsHttpCount;
        }

        public int getThreadsHttpBusy() {
            return threadsHttpBusy;
        }

        public void setThreadsHttpBusy(int threadsHttpBusy) {
            this.threadsHttpBusy = threadsHttpBusy;
        }

        public int getThreadsHttpService() {
            return threadsHttpService;
        }

        public void addThreadsHttpService() {
            threadsHttpService++;
        }

        public int getThreadsHttpKeepalive() {
            return threadsHttpKeepalive;
        }

        public void addThreadsHttpKeepalive() {
            threadsHttpKeepalive++;
        }

        public int getThreadsHttpReady() {
            return threadsHttpReady;
        }

        public void addThreadsHttpReady() {
            threadsHttpReady++;
        }

        public int getThreadsHttpOther() {
            return getThreadsHttpBusy() - getThreadsHttpService() - getThreadsHttpKeepalive();
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

        public int getRequestsHttpTotal() {
            return requestsHttpTotal;
        }

        public void setRequestsHttpTotal(int requestsHttpTotal) {
            this.requestsHttpTotal = requestsHttpTotal;
        }

        public int getRequestsHttpError() {
            return requestsHttpError;
        }

        public void setRequestsHttpError(int requestsHttpError) {
            this.requestsHttpError = requestsHttpError;
        }

        public int getRequestsHttpSuccessful() {
            return requestsHttpTotal - requestsHttpError;
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

        public int getSopraPoolNumIdleConnections() {
            return sopraPoolMaxObjet - sopraPoolObjetUtilisees;
        }

        public int getDbcpMaxTotal() {
            return dbcpMaxTotal;
        }

        public void setDbcpMaxTotal(int dbcpMaxTotal) {
            this.dbcpMaxTotal = dbcpMaxTotal;
        }

        public int getDbcpMaxActive() {
            return dbcpMaxActive;
        }

        public void setDbcpMaxActive(int dbcpMaxActive) {
            this.dbcpMaxActive = dbcpMaxActive;
        }

        public int getDbcpNumActive() {
            return dbcpNumActive;
        }

        public void setDbcpNumActive(int dbcpNumActive) {
            this.dbcpNumActive = dbcpNumActive;
        }

        public int getDbcpMaxIdle() {
            return dbcpMaxIdle;
        }

        public void setDbcpMaxIdle(int dbcpMaxIdle) {
            this.dbcpMaxIdle = dbcpMaxIdle;
        }

        public int getDbcpNumIdle() {
            return dbcpNumIdle;
        }

        public void setDbcpNumIdle(int dbcpNumIdle) {
            this.dbcpNumIdle = dbcpNumIdle;
        }

        public int getDbcpMinIdle() {
            return dbcpMinIdle;
        }

        public void setDbcpMinIdle(int dbcpMinIdle) {
            this.dbcpMinIdle = dbcpMinIdle;
        }

        public int getHikariMaximumPoolSize() {
            return hikariMaximumPoolSize;
        }

        public void setHikariMaximumPoolSize(int hikariMaximumPoolSize) {
            this.hikariMaximumPoolSize = hikariMaximumPoolSize;
        }

        public int getHikariTotalConnections() {
            return hikariTotalConnections;
        }

        public void setHikariTotalConnections(int hikariTotalConnections) {
            this.hikariTotalConnections = hikariTotalConnections;
        }

        public int getHikariActiveConnections() {
            return hikariActiveConnections;
        }

        public void setHikariActiveConnections(int hikariActiveConnections) {
            this.hikariActiveConnections = hikariActiveConnections;
        }

        public int getHikariIdleConnections() {
            return hikariIdleConnections;
        }

        public void setHikariIdleConnections(int hikariIdleConnections) {
            this.hikariIdleConnections = hikariIdleConnections;
        }

        public int getHikariMinimumIdle() {
            return hikariMinimumIdle;
        }

        public void setHikariMinimumIdle(int hikariMinimumIdle) {
            this.hikariMinimumIdle = hikariMinimumIdle;
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

                getGarbageCollectorState(result);
                out.println("gc.oldGen.collectionCount=" + result.getGcOldGenerationCollectionCount());
                out.println("gc.oldGen.collectionTime=" + result.getGcOldGenerationCollectionTime());
                out.println("gc.youngGen.collectionCount=" + result.getGcYoungGenerationCollectionCount());
                out.println("gc.youngGen.collectionTime=" + result.getGcYoungGenerationCollectionTime());

                getOperatingSystemState(result);
                out.println("process.cpu.load=" + result.getProcessCpuLoad());
                out.println("system.cpu.load=" + result.getSystemCpuLoad());
                out.println("process.fileDescriptorCount.max=" + result.getProcessFileDescriptorCountMax());
                out.println("process.fileDescriptorCount.open=" + result.getProcessFileDescriptorCountOpen());

                getConnectorState(result);
                out.println("threads.max=" + result.getThreadsMax());

                getThreadPoolState(result);
                out.println("threads.count=" + result.getThreadsCount());
                out.println("threads.busy=" + result.getThreadsBusy());

                getThreadsState(result);
                out.println("threads.service=" + result.getThreadsService());
                out.println("threads.keepalive=" + result.getThreadsKeepalive());
                out.println("threads.other=" + result.getThreadsOther());
                out.println("threads.ready=" + result.getThreadsReady());

                getConnectorStateHttp(result);
                out.println("threads.http.max=" + result.getThreadsHttpMax());

                getThreadPoolStateHttp(result);
                out.println("threads.http.count=" + result.getThreadsHttpCount());
                out.println("threads.http.busy=" + result.getThreadsHttpBusy());

                getThreadsStateHttp(result);
                out.println("threads.http.service=" + result.getThreadsHttpService());
                out.println("threads.http.keepalive=" + result.getThreadsHttpKeepalive());
                out.println("threads.http.other=" + result.getThreadsHttpOther());
                out.println("threads.http.ready=" + result.getThreadsHttpReady());

                getGlobalRequestProcessorState(result);
                out.println("requests.total=" + result.getRequestsTotal());
                out.println("requests.error=" + result.getRequestsError());
                out.println("requests.successful=" + result.getRequestsSuccessful());

                getGlobalRequestProcessorStateHttp(result);
                out.println("requests.http.total=" + result.getRequestsHttpTotal());
                out.println("requests.http.error=" + result.getRequestsHttpError());
                out.println("requests.http.successful=" + result.getRequestsHttpSuccessful());

                if (context != null) {
                    getContextState(result, context);
                    out.println("sessions.total=" + result.getSessionsTotal());
                    out.println("sessions.authenticated=" + result.getSessionsAuthenticated());
                    out.println("sessions.anonymous=" + result.getSessionsAnonymous());
                    out.println("sopraPool.maxObjet=" + result.getSopraPoolMaxObjet());
                    out.println("sopraPool.numIdleConnections=" + result.getSopraPoolNumIdleConnections());
                    out.println("sopraPool.objetUtilisees=" + result.getSopraPoolObjetUtilisees());

                    getC3p0State(result, context);
                    out.println("c3p0.maxPoolSize=" + result.getC3p0MaxPoolSize());
                    out.println("c3p0.numConnections=" + result.getC3p0NumConnections());
                    out.println("c3p0.numBusyConnections=" + result.getC3p0NumBusyConnections());
                    out.println("c3p0.numIdleConnections=" + result.getC3p0NumIdleConnections());

                    getDbcpState(result, context);
                    out.println("dbcp.maxTotal=" + result.getDbcpMaxTotal());
                    out.println("dbcp.maxActive=" + result.getDbcpMaxActive());
                    out.println("dbcp.numActive=" + result.getDbcpNumActive());
                    out.println("dbcp.maxIdle=" + result.getDbcpMaxIdle());
                    out.println("dbcp.numIdle=" + result.getDbcpNumIdle());
                    out.println("dbcp.minIdle=" + result.getDbcpMinIdle());

                    getHikariState(result, context);
                    out.println("hikari.maximumPoolSize=" + result.getHikariMaximumPoolSize());
                    out.println("hikari.totalConnections=" + result.getHikariTotalConnections());
                    out.println("hikari.activeConnections=" + result.getHikariActiveConnections());
                    out.println("hikari.idleConnections=" + result.getHikariIdleConnections());
                    out.println("hikari.minimumIdle=" + result.getHikariMinimumIdle());
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
            else if (var.equals("gc.oldGen.collectionCount")) {
                getGarbageCollectorState(result);
                out.println(result.getGcOldGenerationCollectionCount());
            }
            else if (var.equals("gc.oldGen.collectionTime")) {
                getGarbageCollectorState(result);
                out.println(result.getGcOldGenerationCollectionTime());
            }
            else if (var.equals("gc.youngGen.collectionCount")) {
                getGarbageCollectorState(result);
                out.println(result.getGcYoungGenerationCollectionCount());
            }
            else if (var.equals("gc.youngGen.collectionTime")) {
                getGarbageCollectorState(result);
                out.println(result.getGcYoungGenerationCollectionTime());
            }
            else if (var.equals("process.cpu.load")) {
                getOperatingSystemState(result);
                out.println(result.getProcessCpuLoad());
            }
            else if (var.equals("system.cpu.load")) {
                getOperatingSystemState(result);
                out.println(result.getSystemCpuLoad());
            }
            else if (var.equals("process.fileDescriptorCount.max")) {
                getOperatingSystemState(result);
                out.println(result.getProcessFileDescriptorCountMax());
            }
            else if (var.equals("process.fileDescriptorCount.open")) {
                getOperatingSystemState(result);
                out.println(result.getProcessFileDescriptorCountOpen());
            }
            else if (var.equals("threads.max")) {
                getConnectorState(result);
                out.println(result.getThreadsMax());
            }
            else if (var.equals("threads.count")) {
                getThreadPoolState(result);
                out.println(result.getThreadsCount());
            }
            else if (var.equals("threads.busy")) {
                getThreadPoolState(result);
                out.println(result.getThreadsBusy());
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
                getThreadPoolState(result);
                getThreadsState(result);
                out.println(result.getThreadsOther());
            }
            else if (var.equals("threads.ready")) {
                getThreadsState(result);
                out.println(result.getThreadsReady());
            }
            else if (var.equals("threads.http.max")) {
                getConnectorStateHttp(result);
                out.println(result.getThreadsHttpMax());
            }
            else if (var.equals("threads.http.count")) {
                getThreadPoolStateHttp(result);
                out.println(result.getThreadsHttpCount());
            }
            else if (var.equals("threads.http.busy")) {
                getThreadPoolStateHttp(result);
                out.println(result.getThreadsHttpBusy());
            }
            else if (var.equals("threads.http.service")) {
                getThreadsStateHttp(result);
                out.println(result.getThreadsHttpService());
            }
            else if (var.equals("threads.http.keepalive")) {
                getThreadsStateHttp(result);
                out.println(result.getThreadsHttpKeepalive());
            }
            else if (var.equals("threads.http.other")) {
                getThreadPoolStateHttp(result);
                getThreadsStateHttp(result);
                out.println(result.getThreadsHttpOther());
            }
            else if (var.equals("threads.http.ready")) {
                getThreadsStateHttp(result);
                out.println(result.getThreadsHttpReady());
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
            else if (var.equals("requests.http.total")) {
                getGlobalRequestProcessorStateHttp(result);
                out.println(result.getRequestsHttpTotal());
            }
            else if (var.equals("requests.http.error")) {
                getGlobalRequestProcessorStateHttp(result);
                out.println(result.getRequestsHttpError());
            }
            else if (var.equals("requests.http.successful")) {
                getGlobalRequestProcessorStateHttp(result);
                out.println(result.getRequestsHttpSuccessful());
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
            else if (var.equals("sopraPool.numIdleConnections")) {
                getContextState(result, context);
                out.println(result.getSopraPoolNumIdleConnections());
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
            else if (var.equals("dbcp.maxTotal")) {
                getDbcpState(result, context);
                out.println(result.getDbcpMaxTotal());
            }
            else if (var.equals("dbcp.maxActive")) {
                getDbcpState(result, context);
                out.println(result.getDbcpMaxActive());
            }
            else if (var.equals("dbcp.numActive")) {
                getDbcpState(result, context);
                out.println(result.getDbcpNumActive());
            }
            else if (var.equals("dbcp.maxIdle")) {
                getDbcpState(result, context);
                out.println(result.getDbcpMaxIdle());
            }
            else if (var.equals("dbcp.numIdle")) {
                getDbcpState(result, context);
                out.println(result.getDbcpNumIdle());
            }
            else if (var.equals("dbcp.minIdle")) {
                getDbcpState(result, context);
                out.println(result.getDbcpMinIdle());
            }
            else if (var.equals("hikari.maximumPoolSize")) {
                getHikariState(result, context);
                out.println(result.getHikariMaximumPoolSize());
            }
            else if (var.equals("hikari.totalConnections")) {
                getHikariState(result, context);
                out.println(result.getHikariTotalConnections());
            }
            else if (var.equals("hikari.activeConnections")) {
                getHikariState(result, context);
                out.println(result.getHikariActiveConnections());
            }
            else if (var.equals("hikari.idleConnections")) {
                getHikariState(result, context);
                out.println(result.getHikariIdleConnections());
            }
            else if (var.equals("hikari.minimumIdle")) {
                getHikariState(result, context);
                out.println(result.getHikariMinimumIdle());
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

    private void getGarbageCollectorState(Result result) {
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            ObjectName objectName = new ObjectName("java.lang:type=GarbageCollector,*");
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, null);
            for (ObjectInstance oi : set) {
                ObjectName on = oi.getObjectName();
                String name = (String)mBeanServer.getAttribute(on, "Name");
                if (name.equals("G1 Old Generation") || name.equals("PS MarkSweep")) {
                    result.setGcOldGenerationCollectionCount((Long)mBeanServer.getAttribute(on, "CollectionCount"));
                    result.setGcOldGenerationCollectionTime((Long)mBeanServer.getAttribute(on, "CollectionTime"));
                }
                else if (name.equals("G1 Young Generation") || name.equals("PS Scavenge")) {
                    result.setGcYoungGenerationCollectionCount((Long)mBeanServer.getAttribute(on, "CollectionCount"));
                    result.setGcYoungGenerationCollectionTime((Long)mBeanServer.getAttribute(on, "CollectionTime"));
                }
            }
        } catch (JMException e) {
            throw new RuntimeException(null, e);
        }
    }

    private void getOperatingSystemState(Result result) {
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            ObjectName objectName = new ObjectName("java.lang:type=OperatingSystem");
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, null);
            for (ObjectInstance oi : set) {
                ObjectName on = oi.getObjectName();
                result.setProcessCpuLoad((Double)mBeanServer.getAttribute(on, "ProcessCpuLoad"));
                result.setSystemCpuLoad((Double)mBeanServer.getAttribute(on, "SystemCpuLoad"));

                try {
                    result.setProcessFileDescriptorCountMax((Long)mBeanServer.getAttribute(on, "MaxFileDescriptorCount"));
                    result.setProcessFileDescriptorCountOpen((Long)mBeanServer.getAttribute(on, "OpenFileDescriptorCount"));
                }
                catch (AttributeNotFoundException e) {
                    // Ignorer : Ces attributs n'existent pas sous Windows
                }

                break; // Première occurrence seulement
            }
        } catch (JMException e) {
            throw new RuntimeException(null, e);
        }
    }

    private static int getIntValue(MBeanServer mBeanServer, ObjectName name, String attribute, int defaultValue) throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException {
        Object value = mBeanServer.getAttribute(name, attribute);
        if (value == null)
            return defaultValue;
        else if (value instanceof Integer)
            return (Integer)value;
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
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, query);
            for (ObjectInstance oi : set) {
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

    private void getConnectorStateHttp(Result result) {
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            ObjectName objectName = new ObjectName("Catalina:type=Connector,*");
            QueryExp query = Query.eq(Query.attr("protocol"), Query.value("HTTP/1.1"));
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, query);
            for (ObjectInstance oi : set) {
                ObjectName rpName = oi.getObjectName();

                // Avec Tomcat 6.0.32 la valeur est de type String
                // De plus la propriété vaut null si l'attribut maxThreads n'est pas explicitement défini dans server.xml
                int maxThreads = getIntValue(mBeanServer, rpName, "maxThreads", 0);
                result.setThreadsHttpMax(maxThreads);
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
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, null);
            if (set.isEmpty()) {
                onStr = "Catalina:type=RequestProcessor,worker=jk-*,*"; // Tomcat 6
                objectName = new ObjectName(onStr);
                set = mBeanServer.queryMBeans(objectName, null);
            }
            for (ObjectInstance oi : set) {
                ObjectName rpName = oi.getObjectName();

                // Voir conversion stage/état dans org.apache.catalina.manager.StatusTransformer.writeProcessorState()
                int stage = (Integer)mBeanServer.getAttribute(rpName, "stage");
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
                }
            }
        } catch (JMException e) {
            throw new RuntimeException(e);
        }
    }

    private void getThreadsStateHttp(Result result) {
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            String onStr = "Catalina:type=RequestProcessor,worker=\"http-*\",*";
            ObjectName objectName = new ObjectName(onStr);
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, null);
            for (ObjectInstance oi : set) {
                ObjectName rpName = oi.getObjectName();

                // Voir conversion stage/état dans org.apache.catalina.manager.StatusTransformer.writeProcessorState()
                int stage = (Integer)mBeanServer.getAttribute(rpName, "stage");
                switch (stage) {
                    case (3/*org.apache.coyote.Constants.STAGE_SERVICE*/):
                        result.addThreadsHttpService();
                        break;

                    case (6/*org.apache.coyote.Constants.STAGE_KEEPALIVE*/):
                        result.addThreadsHttpKeepalive();
                        break;

                    case (0/*org.apache.coyote.Constants.STAGE_NEW*/):
                        result.addThreadsHttpReady();
                        break;

                    case (7/*org.apache.coyote.Constants.STAGE_ENDED*/):
                        result.addThreadsHttpReady();
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
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, null);
            if (set.isEmpty()) {
                onStr = "Catalina:type=GlobalRequestProcessor,name=jk-*";  // Tomcat 6
                objectName = new ObjectName(onStr);
                set = mBeanServer.queryMBeans(objectName, null);
            }
            for (Iterator<ObjectInstance> iterator = set.iterator(); iterator.hasNext(); ) {
                ObjectInstance oi = iterator.next();
                ObjectName rpName = oi.getObjectName();

                result.setRequestsTotal((Integer)mBeanServer.getAttribute(rpName, "requestCount"));
                result.setRequestsError((Integer)mBeanServer.getAttribute(rpName, "errorCount"));

                // Normalement, il n'y a qu'un seul GlobalRequestProcessor par type de connecteur
                if (iterator.hasNext())
                    throw new RuntimeException("Plusieurs GlobalRequestProcessor: " + onStr);
            }
        } catch (JMException e) {
            throw new RuntimeException(e);
        }
    }

    private void getGlobalRequestProcessorStateHttp(Result result) {
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            String onStr = "Catalina:type=GlobalRequestProcessor,name=\"http-*\"";
            ObjectName objectName = new ObjectName(onStr);
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, null);
            for (Iterator<ObjectInstance> iterator = set.iterator(); iterator.hasNext(); ) {
                ObjectInstance oi = iterator.next();
                ObjectName rpName = oi.getObjectName();

                result.setRequestsHttpTotal((Integer)mBeanServer.getAttribute(rpName, "requestCount"));
                result.setRequestsHttpError((Integer)mBeanServer.getAttribute(rpName, "errorCount"));

                // Normalement, il n'y a qu'un seul GlobalRequestProcessor par type de connecteur
                if (iterator.hasNext())
                    throw new RuntimeException("Plusieurs GlobalRequestProcessor: " + onStr);
            }
        } catch (JMException e) {
            throw new RuntimeException(e);
        }
    }

    private void getThreadPoolState(Result result) {
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            String onStr = "Catalina:type=ThreadPool,name=\"ajp-*\""; // Tomcat 7
            ObjectName objectName = new ObjectName(onStr);
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, null);
            if (set.isEmpty()) {
                onStr = "Catalina:type=ThreadPool,name=jk-*";  // Tomcat 6
                objectName = new ObjectName(onStr);
                set = mBeanServer.queryMBeans(objectName, null);
            }
            for (Iterator<ObjectInstance> iterator = set.iterator(); iterator.hasNext(); ) {
                ObjectInstance oi = iterator.next();
                ObjectName rpName = oi.getObjectName();

                result.setThreadsCount((Integer)mBeanServer.getAttribute(rpName, "currentThreadCount"));
                result.setThreadsBusy((Integer)mBeanServer.getAttribute(rpName, "currentThreadsBusy"));

                // Normalement, il n'y a qu'un seul ThreadPool par type de connecteur
                if (iterator.hasNext())
                    throw new RuntimeException("Plusieurs ThreadPool: " + onStr);
            }
        } catch (JMException e) {
            throw new RuntimeException(e);
        }
    }

    private void getThreadPoolStateHttp(Result result) {
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            String onStr = "Catalina:type=ThreadPool,name=\"http-*\"";
            ObjectName objectName = new ObjectName(onStr);
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, null);
            for (Iterator<ObjectInstance> iterator = set.iterator(); iterator.hasNext(); ) {
                ObjectInstance oi = iterator.next();
                ObjectName rpName = oi.getObjectName();

                result.setThreadsHttpCount((Integer)mBeanServer.getAttribute(rpName, "currentThreadCount"));
                result.setThreadsHttpBusy((Integer)mBeanServer.getAttribute(rpName, "currentThreadsBusy"));

                // Normalement, il n'y a qu'un seul ThreadPool par type de connecteur
                if (iterator.hasNext())
                    throw new RuntimeException("Plusieurs ThreadPool: " + onStr);
            }
        } catch (JMException e) {
            throw new RuntimeException(e);
        }
    }

    private void getC3p0State(Result result, String contextPath) {
        if (contextPath == null)
            throw new IllegalArgumentException("Missing argument: context");

        String contextDataSourceName = contextPath.substring(1);
        long minimumUpTimeMillisDefaultUser = Long.MAX_VALUE;

        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            String onStr = "com.mchange.v2.c3p0:type=PooledDataSource,identityToken=*,name=" + contextDataSourceName; // c3p0 >= 0.9.2
            ObjectName objectName = new ObjectName(onStr);
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, null);
            if (set.isEmpty()) {
                onStr = "com.mchange.v2.c3p0:type=PooledDataSource[*]"; // c3p0 < 0.9.2
                objectName = new ObjectName(onStr);
                QueryExp query = Query.eq(Query.attr("dataSourceName"), Query.value(contextDataSourceName));
                set = mBeanServer.queryMBeans(objectName, query);
            }
            for (ObjectInstance oi : set) {
                ObjectName rpName = oi.getObjectName();

                // Contourner le bug des web services Harpège 6.5.0-1 :
                // Il y a deux dataSource de même nom à cause d'une mauvaise utilisation de Spring.
                // L'une est créée lors du chargement de la webapp,
                // l'autre est créée lors de la première utilisation du web service.
                // Dans ce cas, on ne prend en compte que la dataSource la plus jeune.
                long upTimeMillisDefaultUser = (Long)mBeanServer.getAttribute(rpName, "upTimeMillisDefaultUser");
                if (upTimeMillisDefaultUser < minimumUpTimeMillisDefaultUser) {
                    minimumUpTimeMillisDefaultUser = upTimeMillisDefaultUser;
                    result.setC3p0MaxPoolSize((Integer)mBeanServer.getAttribute(rpName, "maxPoolSize"));
                    result.setC3p0NumConnections((Integer)mBeanServer.getAttribute(rpName, "numConnections"));
                    result.setC3p0NumBusyConnections((Integer)mBeanServer.getAttribute(rpName, "numBusyConnections"));
                }
            }
        } catch (JMException e) {
            throw new RuntimeException(e);
        }
    }

    private void getDbcpState(Result result, String contextPath) {
        if (contextPath == null)
            throw new IllegalArgumentException("Missing argument: context");

        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();
        try {
            String onStr = "Catalina:type=DataSource,host=*,context=" + contextPath + ",class=*,name=*"; // Tomcat 9
            ObjectName objectName = new ObjectName(onStr);
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, null);
            if (set.isEmpty()) {
                onStr = "Catalina:type=DataSource,context=" + contextPath + ",host=*,class=*,name=*"; // Tomcat 7
                objectName = new ObjectName(onStr);
                set = mBeanServer.queryMBeans(objectName, null);
            }
            int count = 0;
            for (ObjectInstance oi : set) {
                ObjectName rpName = oi.getObjectName();

                String name = rpName.getKeyProperty("name");
                if (name.equals("\"jdbc/dbSiScol\"")) {
                    // VERRUE : ignorer le pool Apogée d'eCandidat
                    continue;
                }

                count++;

                // On ne supporte qu'un seul pool DBCP par contexte
                if (count > 1)
                    throw new RuntimeException("Plusieurs pools DBCP sur le contexte " + contextPath + ".");

                try {
                    result.setDbcpMaxTotal((Integer)mBeanServer.getAttribute(rpName, "maxTotal"));
                }
                catch (AttributeNotFoundException e) {
                    // Ignorer : Cet attribut n'existe pas avec DBCP 1
                }

                try {
                    result.setDbcpMaxActive((Integer)mBeanServer.getAttribute(rpName, "maxActive"));
                }
                catch (AttributeNotFoundException e) {
                    // Ignorer : Cet attribut n'existe pas avec DBCP 2
                }

                result.setDbcpNumActive((Integer)mBeanServer.getAttribute(rpName, "numActive"));
                result.setDbcpMaxIdle((Integer)mBeanServer.getAttribute(rpName, "maxIdle"));
                result.setDbcpNumIdle((Integer)mBeanServer.getAttribute(rpName, "numIdle"));
                result.setDbcpMinIdle((Integer)mBeanServer.getAttribute(rpName, "minIdle"));
            }
        } catch (JMException e) {
            throw new RuntimeException(e);
        }
    }

    private void getHikariState(Result result, String contextPath) {
        if (contextPath == null)
            throw new IllegalArgumentException("Missing argument: context");

        String contextDataSourceName = contextPath.substring(1);
        MBeanServer mBeanServer = Registry.getRegistry(null, null).getMBeanServer();

        try {
            String onStr = "com.zaxxer.hikari:type=Pool (" + contextDataSourceName + ")";
            ObjectName objectName = new ObjectName(onStr);
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, null);
            int count = 0;
            for (ObjectInstance oi : set) {
                ObjectName rpName = oi.getObjectName();
                count++;

                // On ne supporte qu'un seul pool Hikari par contexte
                if (count > 1)
                    throw new RuntimeException("Plusieurs pools Hikari sur le contexte " + contextPath + ".");

                result.setHikariActiveConnections((Integer)mBeanServer.getAttribute(rpName, "ActiveConnections"));
                result.setHikariIdleConnections((Integer)mBeanServer.getAttribute(rpName, "IdleConnections"));
                result.setHikariTotalConnections((Integer)mBeanServer.getAttribute(rpName, "TotalConnections"));
            }
        } catch (JMException e) {
            throw new RuntimeException(e);
        }

        try {
            String onStr = "com.zaxxer.hikari:type=PoolConfig (" + contextDataSourceName + ")";
            ObjectName objectName = new ObjectName(onStr);
            Set<ObjectInstance> set = mBeanServer.queryMBeans(objectName, null);
            int count = 0;
            for (ObjectInstance oi : set) {
                ObjectName rpName = oi.getObjectName();
                count++;

                // On ne supporte qu'un seul pool Hikari par contexte
                if (count > 1)
                    throw new RuntimeException("Plusieurs pools Hikari sur le contexte " + contextPath + ".");

                result.setHikariMaximumPoolSize((Integer)mBeanServer.getAttribute(rpName, "MaximumPoolSize"));
                result.setHikariMinimumIdle((Integer)mBeanServer.getAttribute(rpName, "MinimumIdle"));
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
            Class<?> poolManagerClass = loader.loadClass("com.sopragroup.fwk.util.pool.PoolManager");

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
