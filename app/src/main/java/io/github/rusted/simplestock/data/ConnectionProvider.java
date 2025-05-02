package io.github.rusted.simplestock.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.rusted.simplestock.util.Config;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionProvider {
    private final HikariDataSource DATA_SOURCE;

    public ConnectionProvider(@NotNull Config config) {
        HikariConfig CONFIG = new HikariConfig();
        CONFIG.setJdbcUrl(config.get("db.url"));
        CONFIG.setUsername(config.get("db.username"));
        CONFIG.setPassword(config.get("db.password"));
        CONFIG.setMaximumPoolSize(Integer.parseInt(config.get("db.pool.size")));
        DATA_SOURCE = new HikariDataSource(CONFIG);
    }

    public Connection getConnection() throws SQLException {
        return DATA_SOURCE.getConnection();
    }
}
