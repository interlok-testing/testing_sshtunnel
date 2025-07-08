package com.adaptris.sshtunnel.test;

import com.adaptris.testing.DockerComposeFunctionalTest;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.time.Duration;

public class DefaultFunctionalTest extends DockerComposeFunctionalTest {
    protected CloseableHttpClient client = HttpClients.createDefault();
    protected static String INTERLOK_SERVICE_NAME = "interlok-1";
    protected static String SSHTUNNEL_SERVICE_NAME = "sshtunnel-1";
    protected static int INTERLOK_PORT = 8080;
    protected static int MARIADB_PORT = 3306;
    protected static int SSHTUNNEL_PORT = 22;
    protected static WaitStrategy defaultWaitStrategy = Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30));

    @Override
    protected ComposeContainer setupContainers() throws Exception {
        return new ComposeContainer(new File("docker-compose.yaml"))
                .withExposedService(INTERLOK_SERVICE_NAME, INTERLOK_PORT, defaultWaitStrategy)
                .withExposedService(SSHTUNNEL_SERVICE_NAME, MARIADB_PORT)
                .withExposedService(SSHTUNNEL_SERVICE_NAME, SSHTUNNEL_PORT);
    }

    @Test
    public void test() throws Exception {
        Thread.sleep(30000);
        InetSocketAddress address = getHostAddressForService(INTERLOK_SERVICE_NAME, INTERLOK_PORT);
        HttpGet httpGet = new HttpGet(String.format("http://%s:%d%s", address.getHostName(), address.getPort(), "/api/liverpool_transfers"));
        try (CloseableHttpResponse response = client.execute(httpGet)) {
            Assertions.assertEquals(200, response.getCode());
        }
    }
}
