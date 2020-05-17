package org.acme.vertx;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;

import java.lang.invoke.MethodHandles;
import java.util.Map;

@QuarkusTestResource(PostgresTestResource.class)
public class PostgresTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private PostgreSQLContainer<?> DATABASE = new PostgreSQLContainer<>();

    @Override
    public Map<String, String> start() {
        DATABASE = new PostgreSQLContainer<>();
        DATABASE.start();
        LOG.warn("Testcontainer initialized. jdbcUrl={}, username={}, password={}", DATABASE.getJdbcUrl(),
                DATABASE.getUsername(), DATABASE.getPassword());

        return Map.of("quarkus.datasource.jdbc.url", DATABASE.getJdbcUrl(),
                "quarkus.datasource.reactive.url", DATABASE.getJdbcUrl().replace("jdbc:", ""),
                "quarkus.datasource.username", DATABASE.getUsername(),
                "quarkus.datasource.password", DATABASE.getPassword()
        );
    }

    @Override
    public void stop() {
        DATABASE.stop();
    }
}
