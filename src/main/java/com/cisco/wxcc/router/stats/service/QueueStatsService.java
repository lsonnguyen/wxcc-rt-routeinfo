package com.cisco.wxcc.router.stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cisco.wxcc.router.auth.model.AuthInfo;
import com.cisco.wxcc.router.event.service.AgentEventService;
import com.cisco.wxcc.router.event.service.TaskEventService;
import com.cisco.wxcc.router.prov.model.queue.QueueProv;
import com.cisco.wxcc.router.prov.service.ProvDataService;
import com.cisco.wxcc.router.stats.model.Query;
import com.cisco.wxcc.router.stats.model.QueryResp;
import com.cisco.wxcc.router.stats.model.queue.Ewt;
import com.cisco.wxcc.router.stats.model.queue.QueueStats;
import com.cisco.wxcc.router.stats.model.queue.Task;
import com.cisco.wxcc.router.stats.repo.QueueStatsStore;
import com.cisco.wxcc.router.util.DatetimeUtil;
import com.cisco.wxcc.router.util.QueryUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QueueStatsService {

	@Autowired
	private StatsApiClient statsApiClient;

	@Autowired
	private QueueStatsStore queueStatsStore;

	@Autowired
	private TaskEventService taskEventService;

	@Autowired
	private AgentEventService agentEventService;

	@Autowired
	private ProvDataService provDataService;

	private String statsQuery;

	@EventListener
	public void authInfoEvent(AuthInfo authInfo) {
		log.warn("AuthInfo event for org {}, user {}",
				authInfo.getOrgId(),
				authInfo.getUserName());

		queueStatsStore.deleteAll();
	}

	@Scheduled(cron = "10 */5 * * * ?")
	public void run() {
		if(statsApiClient.authInfo() != null) {
			log.info("Retrieving queue stats data");

			QueryResp resp = statsApiClient.search(
					Query.builder().query(statsQuery
							.replace("FROM_EPOCH", String.valueOf(DatetimeUtil.todayStartEpoch()))
							.replace("TO_EPOCH", String.valueOf(DatetimeUtil.currentTimeEpoch())))
					.build());

			resp.getData().getTaskDetails().getTasks().forEach(task -> {
				storeStats(task);
			});
		}
	}

	public QueueStats getStats(String queueName) {
		log.info("Get queue stats for {}", queueName);

		QueueStats stats = queueStatsStore.findByName(queueName);
		QueueProv prov = provDataService.getQueueProv(queueName);

		if(stats == null) {
			if(prov != null) {
				stats = QueueStats.builder()
						.id(prov.getId())
						.name(prov.getName())
						.teams(prov.getTeamIds())
						.updatedTime(System.currentTimeMillis())
						.build();
			}
		} else {
			stats.setTeams(prov.getTeamIds());
		}

		stats = augmentStats(stats);

		return stats;
	}

	private QueueStats augmentStats(QueueStats stats) {
		if(stats != null) {
			stats.setQueuedCount(taskEventService.getQueuedCount(stats.getId()));
			stats.setConnectedCount(taskEventService.getConnectedCount(stats.getId()));
			stats.setAgentCount(agentEventService.getAgentCount(stats.getTeams()));
			stats.setTeams(provDataService.getTeamNames(stats.getTeams()));

			if(stats.getUpdatedTime()!= null && 
					DatetimeUtil.secondsElapsed(stats.getUpdatedTime(), 60)) {
				Ewt ewt = statsApiClient.ewt(stats.getId());
				if(ewt != null) {
					stats.setEstWaitTime(ewt.getEstimatedWaitTime());
				}

				stats.setUpdatedTime(System.currentTimeMillis());
			}
		}

		return stats;
	}

	private void storeStats(Task task) {
		final QueueStats stats = QueueStats.builder()
				.id(task.getQueue().getId())
				.name(task.getQueue().getName())
				.updatedTime(System.currentTimeMillis())
				.build();

		task.getAggregation().forEach(aggr -> {
			switch(aggr.getName()) {
				case "queuedCount":
					stats.setQueuedCount(aggr.getValue());
					break;
				case "connectedCount":
					stats.setConnectedCount(aggr.getValue());
					break;
				case "abandonedCount":
					stats.setAbandonedCount(aggr.getValue());
					break;
				case "avgQueueTime":
					stats.setAvgQueueTime(aggr.getValue());
					break;
				case "avgTalkTime":
					stats.setAvgTalkTime(aggr.getValue());
					break;
			}
		});

		queueStatsStore.save(stats);
	}

	@Bean
	public void queueStatsQuery() {
		statsQuery = QueryUtil.getQuery("realtime_queue_stats");
	}
}
