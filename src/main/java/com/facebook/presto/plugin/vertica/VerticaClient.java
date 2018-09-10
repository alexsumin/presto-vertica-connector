package com.facebook.presto.plugin.vertica;

import com.facebook.presto.plugin.jdbc.*;
import com.facebook.presto.spi.PrestoException;
import com.facebook.presto.spi.SchemaTableName;
import com.google.common.collect.ImmutableSet;
import com.vertica.jdbc.Driver;

import javax.inject.Inject;
import java.sql.*;
import java.util.Properties;
import java.util.Set;

import static com.facebook.presto.plugin.jdbc.DriverConnectionFactory.basicConnectionProperties;
import static com.facebook.presto.plugin.jdbc.JdbcErrorCode.JDBC_ERROR;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Locale.ENGLISH;


public class VerticaClient extends BaseJdbcClient {

    @Inject
    public VerticaClient(JdbcConnectorId connectorId, BaseJdbcConfig config) {
        super(connectorId, config, "", connectionFactory(config));
    }

    private static ConnectionFactory connectionFactory(BaseJdbcConfig config) {
        checkArgument(config.getConnectionUrl() != null, "Missing JDBC URL for vertica connector");
        checkArgument(config.getConnectionUser() != null, "Invalid JDBC User for vertica connector");
        checkArgument(config.getConnectionPassword() != null, "Invalid JDBC Password for vertica connector");
        Properties connectionProperties = basicConnectionProperties(config);
        connectionProperties.setProperty("user", config.getConnectionUser());
        connectionProperties.setProperty("url", config.getConnectionUrl());
        connectionProperties.setProperty("password", config.getConnectionPassword());
        return new DriverConnectionFactory(new Driver(), config.getConnectionUrl(), connectionProperties);
    }

    @Override
    public Set<String> getSchemaNames() {
        try (Connection connection = connectionFactory.openConnection();
             ResultSet resultSet = connection.getMetaData().getSchemas()) {
            ImmutableSet.Builder<String> schemaNames = ImmutableSet.builder();
            while (resultSet.next()) {
                String schemaName = resultSet.getString("TABLE_SCHEM").toLowerCase(ENGLISH);
                if (!schemaName.equals("v_monitor") && !schemaName.equals("v_txtindex") && !schemaName.equals("v_catalog")) {
                    schemaNames.add(schemaName);
                }
            }
            return schemaNames.build();
        } catch (SQLException e) {
            throw new PrestoException(JDBC_ERROR, e);
        }
    }

    @Override
    protected ResultSet getTables(Connection connection, String schemaName, String tableName) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        String escape = metadata.getSearchStringEscape();
        return metadata.getTables(
                connection.getCatalog(),
                escapeNamePattern(schemaName, escape),
                escapeNamePattern(tableName, escape),
                new String[]{"TABLE"});
    }

    @Override
    public PreparedStatement getPreparedStatement(Connection connection, String sql) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement(sql);
        return statement;
    }

    @Override
    protected SchemaTableName getSchemaTableName(ResultSet resultSet) throws SQLException {
        return new SchemaTableName(
                resultSet.getString("TABLE_SCHEM").toLowerCase(ENGLISH),
                resultSet.getString("TABLE_NAME").toLowerCase(ENGLISH));
    }

}
