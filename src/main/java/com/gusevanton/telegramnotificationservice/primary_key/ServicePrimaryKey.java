package com.gusevanton.telegramnotificationservice.primary_key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

/**
 * Created by antongusev on 19.10.17.
 */
@Data
@PrimaryKeyClass
@AllArgsConstructor
@NoArgsConstructor
public class ServicePrimaryKey implements Serializable {

    @PrimaryKeyColumn(name = "service_name", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String serviceName;

    @PrimaryKeyColumn(name = "profile", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private String profile;

}
