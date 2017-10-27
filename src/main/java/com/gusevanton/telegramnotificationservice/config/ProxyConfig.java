package com.gusevanton.telegramnotificationservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "proxy")
public class ProxyConfig {

	private boolean use;

	private String host;

	private Integer port;

	private String username;

	private String password;

}
