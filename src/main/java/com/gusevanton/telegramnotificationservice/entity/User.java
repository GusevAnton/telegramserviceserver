package com.gusevanton.telegramnotificationservice.entity;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

/**
 * Created by antongusev on 16.10.17.
 */
@Data
@Table
public final class User {

    @PrimaryKey("user_id")
    private Integer userId;

    @Column("user_name")
    private String userName;

    @Column("chat_id")
    private Long chatId;

    @Column("service_name_list")
    private List<String> serviceNameList;

    @Column("validated")
    private boolean validated;

    @Column("user_email")
    private String userEmail;

}
