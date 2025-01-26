package com.cisco.wxcc.router.event.service;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException.TooManyRequests;

import com.cisco.wxcc.router.auth.model.AuthInfo;
import com.cisco.wxcc.router.event.model.Subscription;
import com.cisco.wxcc.router.event.model.Subscriptions;
import com.cisco.wxcc.router.service.WxccApiClient;
import com.cisco.wxcc.router.util.ObjectUtil;

import io.netty.handler.timeout.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import reactor.util.retry.Retry;

@Service
@Slf4j
public class EventApiClient extends WxccApiClient {

	private String orgId;

	@EventListener
	public void authInfoEvent(AuthInfo authInfo) {
		log.warn("AuthInfo event for org {}, user {}",
				authInfo.getOrgId(), authInfo.getUserName());

		if(!authInfo.getOrgId().equals(orgId)) {
			stopSubscriptions();
		}

		orgId = authInfo.getOrgId();

		startSubscriptions();
	}

	public void startSubscriptions() {
		Object resp = subscribe(Subscription.builder()
				.name("AgentEvents")
				.eventTypes(Arrays.asList("agent:login",
						"agent:logout", "agent:state_change"))
				.destinationUrl(authInfo().getServiceUrl() + "/events/agent")
				.orgId(authInfo().getOrgId())
				.resourceVersion("agent:1.0.0")
				.build());

		ObjectUtil.logObj("AgentSubscriptionResponse", resp);

		resp = subscribe(Subscription.builder()
				.name("TaskEvents")
				.eventTypes(Arrays.asList("task:parked",
						"task:connected", "task:ended"))
				.destinationUrl(authInfo().getServiceUrl() + "/events/task")
				.orgId(authInfo().getOrgId())
				.resourceVersion("task:1.0.0")
				.build());

		ObjectUtil.logObj("TaskSubscriptionResponse", resp);
	}

	public void stopSubscriptions() {
		list().getData().forEach(subscription -> {
			ObjectUtil.logObj("Subscription", subscription);

			delete(subscription.getId());
		});
	}

	public Object subscribe(Subscription subscription) {
		ObjectUtil.logObj("SubscriptionRequest", subscription);

		try {
			return CompletableFuture.supplyAsync(() -> {
				return webClient.post()
						.uri(String.format("%s/v2/subscriptions",
								serviceConfig.getApiBaseUrl()))
						.headers(httpHeaders())
						.bodyValue(subscription)
						.retrieve()
						.bodyToMono(Subscription.class)
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

				return subscribe(subscription);
			}

			log.error("API create subscription request exception.", e);
		}

		return null;
	}

	public Subscriptions list() {
		log.info("List subscriptions");

		try {
			return CompletableFuture.supplyAsync(() -> {
				return webClient.get()
						.uri(String.format("%s/v2/subscriptions",
								serviceConfig.getApiBaseUrl()))
						.headers(httpHeaders())
						.retrieve()
						.bodyToMono(Subscriptions.class)
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

				return list();
			}

			log.error("API list subscriptions request exception.", e);
		}

		return null;
	}

	public Object delete(String id) {
		log.info("Delete subscription {}", id);

		try {
			return CompletableFuture.supplyAsync(() -> {
				return webClient.delete()
						.uri(String.format("%s/v2/subscriptions/%s",
								serviceConfig.getApiBaseUrl(), id))
						.headers(httpHeaders())
						.retrieve()
						.bodyToMono(Object.class)
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

				return delete(id);
			}

			log.error("API delete subscription request exception.", e);
		}

		return null;
	}

}
