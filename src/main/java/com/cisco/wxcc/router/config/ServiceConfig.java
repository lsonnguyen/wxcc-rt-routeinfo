package com.cisco.wxcc.router.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties("wxcc.api")
@Data
public class ServiceConfig {

	@Value("${spring.security.oauth2.client.provider.wxccrouter.token-uri}")
	private String tokenUrl;

	private String apiBaseUrl;

	private String loggedInUrl;

	private String loggedOutUrl;
}
