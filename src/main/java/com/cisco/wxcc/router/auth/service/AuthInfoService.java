package com.cisco.wxcc.router.auth.service;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import com.cisco.wxcc.router.auth.model.AuthInfo;
import com.cisco.wxcc.router.auth.model.Token;
import com.cisco.wxcc.router.config.ServiceConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class AuthInfoService {

	private static File aiFile = new File(System.getProperty("user.dir") + "/ai.json");

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Autowired
	private ServiceConfig serviceConfig;

	@Autowired
	private WebClient webClient;

	@Autowired
	private ObjectMapper objectMapper;

	private ThreadPoolTaskScheduler taskScheduler;

	private AuthInfo authInfo;

	public AuthInfo getAuthInfo() {
		do {
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {}
		} while(authInfo != null && authInfo.getRefreshing());

		return authInfo;
	}

	@EventListener
	public void authInfoEvent(AuthInfo authInfo) {
		log.warn("AuthInfo event for org {}, user {}",
				authInfo.getOrgId(), authInfo.getUserName());

		if(this.authInfo != null && !this.authInfo.getOrgId().equals(authInfo.getOrgId())) {
			log.warn("User logged in from different org");
		}

		this.authInfo = authInfo;

		scheduleRefresh();
	}

	@EventListener
	public void appStarted(ApplicationStartedEvent event) {
		log.warn("Application started event");

		try {
			// Load and republish user authentication info after service restart
			if(aiFile.exists()) {
				AuthInfo ai = objectMapper.readValue(aiFile, AuthInfo.class);

				if(ai != null) {
					eventPublisher.publishEvent(ai);
				}

				aiFile.delete();
			}
		} catch (Exception e) {
			log.error("Exception reading authInfo", e);
		}
	}

	@EventListener(ContextClosedEvent.class)
	public void contextClosed(ContextClosedEvent event) {
		log.warn("Context closed event");

		try {
			unscheduleRefresh();

			// Temporarily save user authentication info before service restart
			if(authInfo != null) {
				objectMapper.writeValue(aiFile, authInfo);
			}
		} catch(Exception e) {
			log.error("Exception writing authInfo", e);
		}
	}

	private void unscheduleRefresh() {
		if(taskScheduler != null) {
			log.warn("Cancel access token refresh task");

			try {
				taskScheduler.stop();
				taskScheduler = null;
			} catch(Exception e) {
				log.warn("Exception canceling scheduled task", e);
			}
		}
	}

	private void scheduleRefresh() {
		unscheduleRefresh();

		log.info("Schedule access token refresh task");

		taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(1);
		taskScheduler.initialize();
		taskScheduler.scheduleWithFixedDelay(new Runnable() {
			@Override
		    public void run() {
				log.info("Refresh access token");

				authInfo.setRefreshing(true);

				try {
					refreshToken();
				} catch(Exception e) {
					log.error("Failed to execute scheduled token refresh", e);
				}
			}
		}, Instant.ofEpochSecond(
				authInfo.getAccessToken().getExpires()-10),
				Duration.ofSeconds(43190));
	}

	private void refreshToken() {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("refresh_token", authInfo.getRefreshToken().getToken());
		params.add("client_id", authInfo.getClientId());
		params.add("client_secret", authInfo.getClientSecret());
		params.add("grant_type", "refresh_token");

		webClient.post()
			.uri(serviceConfig.getTokenUrl())
			.headers((hdrs) -> {
				hdrs.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			})
			.bodyValue(params)
			.retrieve()
			.bodyToMono(JSONObject.class)
			.retryWhen(Retry.max(3)
					.filter(throwable -> {
						log.warn("API request timed out, retrying...");

						return throwable instanceof HttpServerErrorException
							|| throwable instanceof WebClientRequestException
		                    && throwable.getCause() instanceof TimeoutException;
					}))
			.subscribe(token -> {
				authInfo.setAccessToken(Token.builder()
						.token(token.getAsString("access_token"))
						.expires((System.currentTimeMillis() / 1000)
								+ token.getAsNumber("expires_in").longValue())
						.build());
				authInfo.setRefreshToken(Token.builder()
						.token(token.getAsString("refresh_token"))
						.expires((System.currentTimeMillis() / 1000)
								+ token.getAsNumber("refresh_token_expires_in").longValue())
						.build());

				log.info("Token expires {}. Refresh token expires {}",
						authInfo.getAccessToken().getExpires(),
						authInfo.getRefreshToken().getExpires());

				eventPublisher.publishEvent(authInfo);

				authInfo.setRefreshing(false);
			});
	}
}
