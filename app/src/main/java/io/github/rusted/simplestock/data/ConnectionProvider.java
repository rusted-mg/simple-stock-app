package io.github.rusted.simplestock.data;

import io.github.rusted.simplestock.util.Config;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {
    @NotNull
    private final Config config;

    public ConnectionProvider(@NotNull Config config) {
        this.config = config;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                config.get("db.url"),
                config.get("db.username"),
                config.get("db.password")
        );
    }
}
