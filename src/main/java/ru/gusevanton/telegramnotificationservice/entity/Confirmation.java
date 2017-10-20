package ru.gusevanton.telegramnotificationservice.entity;

import lombok.Data;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

/**
 * Created by antongusev on 17.10.17.
 */
@Data
@Table
public class Confirmation {

    @PrimaryKey("user_id")
    private Integer userId;

    @Column("code")
    private String code;

}
