package com.cisco.wxcc.router.stats.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests;

import com.cisco.wxcc.router.service.WxccApiClient;
import com.cisco.wxcc.router.stats.model.Query;
import com.cisco.wxcc.router.stats.model.QueryResp;
import com.cisco.wxcc.router.stats.model.queue.Ewt;

import io.netty.handler.timeout.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class StatsApiClient extends WxccApiClient {

	public QueryResp search(Query query) {
		log.debug("Search query: {}", query);

		try {
			return CompletableFuture.supplyAsync(() -> {
				return webClient.post()
						.uri(String.format("%s/search?orgId=%s",
								serviceConfig.getApiBaseUrl(),
								authInfo().getOrgId()))
						.headers(httpHeaders())
						.bodyValue(query)
						.retrieve()
						.bodyToMono(QueryResp.class)
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

				return search(query);
			}

			log.error("API search request exception.", e);
		}

		return null;
	}

	public Ewt ewt(String queueId) {
		log.debug("EWT query: {}", queueId);

		try {
			return CompletableFuture.supplyAsync(() -> {
				return webClient.get()
						.uri(String.format("%s/v1/ewt?orgId=%s&queueId=%s&lookbackMinutes=60&minValidSamples=5",
								serviceConfig.getApiBaseUrl(),
								authInfo().getOrgId(),
								queueId))
						.headers(httpHeaders())
						.retrieve()
						.bodyToMono(Ewt.class)
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

				return ewt(queueId);
			}

			log.error("API get request exception.", e);
		}

		return null;
	}
}
