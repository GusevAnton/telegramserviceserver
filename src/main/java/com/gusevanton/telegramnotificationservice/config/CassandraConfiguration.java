package com.gusevanton.telegramnotificationservice.config;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Created by antongusev on 16.10.17.
 */
@Log
@Configuration
@EnableCassandraRepositories("com.gusevanton.telegramnotificationservice.repository")
public class CassandraConfiguration extends AbstractCassandraConfiguration {

    @Value("${cassandra.contactpoints}")
    private String contactpoints;
    @Value("${cassandra.port}")
    private int port;
    @Value("${cassandra.keyspace}")
    private String keyspace;
    @Value("${cassandra.username}")
    private String username;
    @Value("${cassandra.password}")
    private String password;

    @Override
    public String getKeyspaceName() {
        return "telegram";
    }

    @Override
    public SchemaAction getSchemaAction() {
        return SchemaAction.CREATE_IF_NOT_EXISTS;
    }

    @Override
    public String[] getEntityBasePackages() {
        return new String[]{"com.gusevanton.telegramnotificationservice.entity"};
    }

    @Bean
    public CassandraOperations cassandraOperations() throws Exception {
        return new CassandraTemplate(session().getObject());
    }

    protected String getContactPoints() {
        return contactpoints;
    }

}
