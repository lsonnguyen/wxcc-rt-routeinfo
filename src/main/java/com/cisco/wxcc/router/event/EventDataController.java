package com.cisco.wxcc.router.event;

import static org.springframework.web.servlet.function.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import com.cisco.wxcc.router.event.model.agent.AgentEvent;
import com.cisco.wxcc.router.event.model.task.TaskEvent;
import com.cisco.wxcc.router.event.model.task.TaskState;
import com.cisco.wxcc.router.event.service.AgentEventService;
import com.cisco.wxcc.router.event.service.EventApiClient;
import com.cisco.wxcc.router.event.service.TaskEventService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class EventDataController {

	@Bean
	RouterFunction<ServerResponse> eventsRoutes(EventApiClient eventApiClient,
			AgentEventService agentEventService, TaskEventService taskEventService) {
		return RouterFunctions.route()
				.POST("/events/agent", accept(MediaType.APPLICATION_JSON),  event -> {
					AgentEvent ae = event.body(AgentEvent.class);
					agentEventService.handle(ae.getData());

					return ServerResponse.status(200).body("OK");
				})
				.POST("/events/task", accept(MediaType.APPLICATION_JSON),  event -> {
					TaskEvent te = event.body(TaskEvent.class);
					te.getData().setCurrentState(TaskState.valueOf(
							te.getType().toString().split("_")[1]));
					taskEventService.handle(te.getData());

					return ServerResponse.status(200).body("OK");
				})
				.POST("/events/start", accept(MediaType.APPLICATION_JSON),  event -> {
					log.info("Start event subscriptions");

					eventApiClient.startSubscriptions();

					return ServerResponse.status(200).body("OK");
				})
				.POST("/events/stop", accept(MediaType.APPLICATION_JSON),  event -> {
					log.info("Stop event subscriptions");

					eventApiClient.stopSubscriptions();

					return ServerResponse.status(200).body("OK");
				})
				.GET("/events/agent", accept(MediaType.APPLICATION_JSON),  event -> {
					log.info("Get agent data");

					return ServerResponse.status(200).body(agentEventService.listAgentData());
				})
				.GET("/events/task", accept(MediaType.APPLICATION_JSON),  event -> {
					log.info("Get agent data");

					return ServerResponse.status(200).body(taskEventService.listTaskData());
				})
				.build();

	}
}
