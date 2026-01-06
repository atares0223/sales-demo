package com.waaw.token.server;

import com.alibaba.csp.sentinel.cluster.server.ClusterTokenServer;
import com.alibaba.csp.sentinel.cluster.server.SentinelDefaultTokenServer;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;

//@SpringBootApplication
public class TokenServerApplication {
    static{
        System.setProperty("csp.sentinel.dashboard.server","localhost:8080");
        System.setProperty("csp.sentinel.api.port","8719");
        System.setProperty("project.name","token-server");
        System.setProperty("csp.sentinel.log.use.pid","true");
    }
    public static void main(String[] args) throws Exception {
        ClusterTokenServer tokenServer = new SentinelDefaultTokenServer();
        ClusterServerConfigManager.loadGlobalTransportConfig(new ServerTransportConfig().setIdleSeconds(600).setPort(10217));

        tokenServer.start();
    }

}
