package com.gusevanton.telegramnotificationservice.entity;

import com.gusevanton.telegramnotificationservice.primary_key.ServicePrimaryKey;
import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.Set;

/**
 * Created by antongusev on 16.10.17.
 */
@Data
@Table
public class Service {

    @PrimaryKey
    private ServicePrimaryKey servicePrimaryKey;

    @Column("action_name")
    private String actionName;

    @Column("chat_id_list")
    private Set<Long> chatIdSet;

    @Column("active")
    private boolean active;

}
