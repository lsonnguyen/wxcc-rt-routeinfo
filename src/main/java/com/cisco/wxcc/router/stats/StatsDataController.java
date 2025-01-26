package com.cisco.wxcc.router.stats;

import static org.springframework.web.servlet.function.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import com.cisco.wxcc.router.stats.model.queue.QueueStats;
import com.cisco.wxcc.router.stats.model.team.TeamStats;
import com.cisco.wxcc.router.stats.service.QueueStatsService;
import com.cisco.wxcc.router.stats.service.TeamStatsService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class StatsDataController {

	@Bean
	RouterFunction<ServerResponse> statsRoutes(QueueStatsService queueStatsService, TeamStatsService teamStatsService) {
		return RouterFunctions.route()
				.GET("/stats/queue/{name}", accept(MediaType.APPLICATION_JSON),  request -> {
					log.info("Get queue stats");

					QueueStats stats = queueStatsService.getStats(request.pathVariable("name"));
					if(stats == null) {
						return ServerResponse.status(404).body("NotFound");
					}

					return ServerResponse.status(200).body(stats);
				})
				.GET("/stats/team/{name}", accept(MediaType.APPLICATION_JSON),  request -> {
					log.info("Get team stats");

					TeamStats stats = teamStatsService.getStats(request.pathVariable("name"));
					if(stats == null) {
						return ServerResponse.status(404).body("NotFound");
					}

					return ServerResponse.status(200).body(stats);
				})
				.build();

	}
}
