package com.tpict207.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConfigurationBD {
    private static final String PROPERTIES_FILE = "application.properties";

    private static final String JDBC_URL_PROPERTY = "azure.mysql.jdbc-url";
    private static final String HOST_PROPERTY = "azure.mysql.host";
    private static final String PORT_PROPERTY = "azure.mysql.port";
    private static final String DATABASE_PROPERTY = "azure.mysql.database";
    private static final String USE_SSL_PROPERTY = "azure.mysql.use-ssl";
    private static final String TIMEZONE_PROPERTY = "azure.mysql.server-timezone";

    private static final String MANAGED_IDENTITY_PROPERTY = "azure.mysql.managed-identity";
    private static final String JDBC_USER_PROPERTY = "azure.mysql.user";
    private static final String JDBC_PASSWORD_PROPERTY = "azure.mysql.password";

    private static final String DEFAULT_MANAGED_IDENTITY = "YOUR_MANAGED_IDENTITY_NAME";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DATABASE = "notes_db";
    private static final String DEFAULT_USE_SSL = "false";
    private static final String DEFAULT_TIMEZONE = "UTC";

    private static final String AUTH_PLUGIN_CONFIG = "&authenticationPlugins=com.azure.identity.extensions.jdbc.mysql.AzureMysqlAuthenticationPlugin";

    private static final String MANAGED_IDENTITY;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Le pilote MySQL est introuvable.");
            e.printStackTrace();
        }

        MANAGED_IDENTITY = loadManagedIdentity();
    }

    private static String loadManagedIdentity() {
        Properties props = loadProperties();
        if (props != null) {
            String identity = props.getProperty(MANAGED_IDENTITY_PROPERTY);
            if (identity != null && !identity.isBlank()) {
                return identity;
            }
        }
        return DEFAULT_MANAGED_IDENTITY;
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = ConfigurationBD.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (in != null) {
                props.load(in);
            } else {
                System.err.println("Configuration file not found: " + PROPERTIES_FILE);
            }
        } catch (IOException e) {
            System.err.println("Unable to load configuration file: " + PROPERTIES_FILE);
            e.printStackTrace();
        }
        return props;
    }

    private static String buildJdbcUrl(Properties props) {
        if (props == null) {
            props = new Properties();
        }

        String configuredUrl = props.getProperty(JDBC_URL_PROPERTY);
        if (configuredUrl != null && !configuredUrl.isBlank()) {
            return configureUrlForMode(configuredUrl, props);
        }

        String host = props.getProperty(HOST_PROPERTY, DEFAULT_HOST);
        String port = props.getProperty(PORT_PROPERTY, DEFAULT_PORT);
        String database = props.getProperty(DATABASE_PROPERTY, DEFAULT_DATABASE);
        String useSsl = props.getProperty(USE_SSL_PROPERTY, DEFAULT_USE_SSL);
        String timezone = props.getProperty(TIMEZONE_PROPERTY, DEFAULT_TIMEZONE);

        String baseUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=%s&allowPublicKeyRetrieval=true&serverTimezone=%s", host, port, database, useSsl, timezone);
        return configureUrlForMode(baseUrl, props);
    }

    private static String configureUrlForMode(String url, Properties props) {
        String managedIdentity = props.getProperty(MANAGED_IDENTITY_PROPERTY, DEFAULT_MANAGED_IDENTITY);
        boolean usingManagedIdentity = managedIdentity != null && !managedIdentity.isBlank() && !DEFAULT_MANAGED_IDENTITY.equals(managedIdentity);

        if (usingManagedIdentity) {
            if (!url.contains("authenticationPlugins=")) {
                url += AUTH_PLUGIN_CONFIG;
            }
            if (!url.contains("user=")) {
                url += "&user=" + managedIdentity;
            }
            return url;
        }

        // Local / non-managed mode: allow username/password overrides
        String user = props.getProperty(JDBC_USER_PROPERTY);
        String password = props.getProperty(JDBC_PASSWORD_PROPERTY);

        if (user != null && !user.isBlank() && !url.contains("user=")) {
            url += "&user=" + user;
        }
        if (password != null && !password.isBlank() && !url.contains("password=")) {
            url += "&password=" + password;
        }

        return url;
    }
    }

    public static Connection getConnection() throws SQLException {
        Properties props = loadProperties();
        String url = buildJdbcUrl(props);
        return DriverManager.getConnection(url);
    }
}
