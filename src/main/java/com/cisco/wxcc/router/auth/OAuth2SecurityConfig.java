package com.cisco.wxcc.router.auth;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.reactive.function.client.WebClient;

import com.cisco.wxcc.router.config.ServiceConfig;

import reactor.netty.http.client.HttpClient;

@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {

	@Autowired
	private ServiceConfig serviceConfig;

	@Autowired
	private OAuth2AuthorizedClientService authClientService;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.authorizeHttpRequests(request -> request
					.requestMatchers(new AntPathRequestMatcher("/events/**")).permitAll()
					.requestMatchers(new AntPathRequestMatcher("/stats/**")).permitAll()
					.anyRequest()
					.hasAnyAuthority("SCOPE_cjp:config",
							"SCOPE_cjp:config_read",
							"SCOPE_cjp:config_write")
			)
			.oauth2Login(oauth2 -> oauth2
					.defaultSuccessUrl(serviceConfig.getLoggedInUrl(), true)
					.successHandler(new AuthenticatedHandler(authClientService, serviceConfig, eventPublisher))
			)
			.addFilterAfter(new AuthenticationFilter(), AuthorizationFilter.class)
			.csrf(AbstractHttpConfigurer::disable);

		return http.build();
	}

	@Bean
	public WebClient webClient(WebClient.Builder builder) {
		HttpClient client = HttpClient.create()
				  .responseTimeout(Duration.ofSeconds(30));

		return builder
				.clientConnector(new ReactorClientHttpConnector(client))
				.build();
	}
}
