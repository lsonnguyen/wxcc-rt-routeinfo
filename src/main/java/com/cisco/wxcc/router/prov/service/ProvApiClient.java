package com.cisco.wxcc.router.prov.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests;

import com.cisco.wxcc.router.prov.model.queue.Queues;
import com.cisco.wxcc.router.prov.model.team.Teams;
import com.cisco.wxcc.router.service.WxccApiClient;

import io.netty.handler.timeout.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class ProvApiClient extends WxccApiClient {

	public Teams teams(String query) {
		log.debug("API query: {}", query);

		try {
			return CompletableFuture.supplyAsync(() -> {
				return webClient.get()
						.uri(String.format("%s/organization/%s/v2/team?%s&attributes=id,name",
								serviceConfig.getApiBaseUrl(),
								authInfo().getOrgId(),
								query))
						.headers(httpHeaders())
						.retrieve()
						.bodyToMono(Teams.class)
						.retryWhen(Retry.max(3)
								.filter(throwable -> {
									log.warn("API request timed out, retrying...");

									return throwable instanceof HttpServerErrorException
										|| throwable instanceof WebClientRequestException
	                                    && throwable.getCause() instanceof TimeoutException;
								}))
						.block();
			}).get();
		} catch (Exception e) {
			if(e.getCause() instanceof TooManyRequests) {
				log.warn("API responds with too many request error, pausing for 60 seconds...");

				try {
					Thread.sleep(60000L);
				} catch (Exception e1) {
				}

				return teams(query);
			}

			log.error("API get request exception.", e);
		}

		return null;
	}

	public Queues queues(String query) {
		log.debug("API query: {}", query);

		try {
			return CompletableFuture.supplyAsync(() -> {
				return webClient.get()
						.uri(String.format("%s/organization/%s/v2/contact-service-queue?%s",
								serviceConfig.getApiBaseUrl(),
								authInfo().getOrgId(),
								query))
						.headers(httpHeaders())
						.retrieve()
						.bodyToMono(Queues.class)
						.retryWhen(Retry.max(3)
								.filter(throwable -> {
									log.warn("API request timed out, retrying...");

									return throwable instanceof HttpServerErrorException
										|| throwable instanceof WebClientRequestException
	                                    && throwable.getCause() instanceof TimeoutException;
								}))
						.block();
			}).get();
		} catch (Exception e) {
			if(e.getCause() instanceof TooManyRequests) {
				log.warn("API responds with too many request error, pausing for 60 seconds...");

				try {
					Thread.sleep(60000L);
				} catch (Exception e1) {
				}

				return queues(query);
			}

			log.error("API get request exception.", e);
		}

		return null;
	}
}
