package br.com.duxusdesafio.config;

import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class SqlServerDatabaseInitializer {

    private static final Pattern DATABASE_PROPERTY =
            Pattern.compile("(?i)(;databaseName=)([^;]+)");

    @Bean
    public static BeanFactoryPostProcessor createSqlServerDatabaseIfNecessary(Environment environment) {
        return beanFactory -> {
            String url = environment.getProperty("spring.datasource.url");
            if (url == null || !url.startsWith("jdbc:sqlserver:")) {
                return;
            }

            String username = requiredProperty(environment, "spring.datasource.username");
            String password = requiredProperty(environment, "spring.datasource.password");
            createDatabase(url, username, password);
        };
    }

    static void createDatabase(String applicationUrl, String username, String password) {
        Matcher matcher = DATABASE_PROPERTY.matcher(applicationUrl);
        if (!matcher.find()) {
            throw new IllegalStateException(
                    "A URL do SQL Server deve informar a propriedade databaseName."
            );
        }

        String databaseName = matcher.group(2);
        String masterUrl = matcher.replaceFirst("$1master");

        try (Connection connection = DriverManager.getConnection(masterUrl, username, password)) {
            if (!databaseExists(connection, databaseName)) {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate(
                            "CREATE DATABASE [" + databaseName.replace("]", "]]") + "]"
                    );
                }
            }
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Não foi possível verificar ou criar o banco SQL Server '" + databaseName + "'.",
                    exception
            );
        }
    }

    private static boolean databaseExists(Connection connection, String databaseName)
            throws SQLException {
        try (PreparedStatement statement =
                     connection.prepareStatement("SELECT 1 FROM sys.databases WHERE name = ?")) {
            statement.setString(1, databaseName);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static String requiredProperty(Environment environment, String propertyName) {
        String value = environment.getProperty(propertyName);
        if (value == null || value.trim().isEmpty() || value.startsWith("${")) {
            throw new IllegalStateException(
                    "A propriedade '" + propertyName + "' deve ser informada por variável de ambiente."
            );
        }
        return value;
    }
}
