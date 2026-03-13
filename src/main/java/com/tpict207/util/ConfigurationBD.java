package com.tpict207.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConfigurationBD {
    private static final String URL = "jdbc:mysql://localhost:3306/notes_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String AUTH_PLUGIN_CONFIG = "&authenticationPlugins=com.azure.identity.extensions.jdbc.mysql.AzureMysqlAuthenticationPlugin";
    private static final String PROPERTIES_FILE = "application.properties";
    private static final String MANAGED_IDENTITY_PROPERTY = "azure.mysql.managed-identity";
    private static final String DEFAULT_MANAGED_IDENTITY = "root";

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
        Properties props = new Properties();
        try (InputStream in = ConfigurationBD.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (in != null) {
                props.load(in);
                String identity = props.getProperty(MANAGED_IDENTITY_PROPERTY);
                if (identity != null && !identity.isBlank()) {
                    return identity;
                }
            } else {
                System.err.println("Configuration file not found: " + PROPERTIES_FILE);
            }
        } catch (IOException e) {
            System.err.println("Unable to load configuration file: " + PROPERTIES_FILE);
            e.printStackTrace();
        }
        return DEFAULT_MANAGED_IDENTITY;
    }

    public static Connection getConnection() throws SQLException {
        String urlWithPlugin = URL + AUTH_PLUGIN_CONFIG;
        String urlWithIdentity = urlWithPlugin + "&user=" + MANAGED_IDENTITY;
        return DriverManager.getConnection(urlWithIdentity);
    }
}
