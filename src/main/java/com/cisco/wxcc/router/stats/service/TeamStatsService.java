package com.cisco.wxcc.router.stats.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.cisco.wxcc.router.auth.model.AuthInfo;
import com.cisco.wxcc.router.event.service.AgentEventService;
import com.cisco.wxcc.router.prov.model.team.TeamProv;
import com.cisco.wxcc.router.prov.service.ProvDataService;
import com.cisco.wxcc.router.stats.model.Query;
import com.cisco.wxcc.router.stats.model.QueryResp;
import com.cisco.wxcc.router.stats.model.team.Session;
import com.cisco.wxcc.router.stats.model.team.TeamStats;
import com.cisco.wxcc.router.stats.repo.TeamStatsStore;
import com.cisco.wxcc.router.util.DatetimeUtil;
import com.cisco.wxcc.router.util.QueryUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TeamStatsService {

	@Autowired
	private StatsApiClient statsApiClient;

	@Autowired
	private TeamStatsStore teamStatsStore;

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

		teamStatsStore.deleteAll();
	}

	@Scheduled(cron = "10 */1 * * * ?")
	public void run() {
		if(statsApiClient.authInfo() != null) {
			log.info("Retrieving team stats data");

			QueryResp resp = statsApiClient.search(
					Query.builder().query(statsQuery
							.replace("FROM_EPOCH", String.valueOf(DatetimeUtil.todayStartEpoch()))
							.replace("TO_EPOCH", String.valueOf(DatetimeUtil.currentTimeEpoch())))
					.build());

			resp.getData().getAgentSession().getAgentSessions().forEach(s -> {
				storeStats(s);
			});
		}
	}

	public TeamStats getStats(String teamName) {
		log.info("Get team stats for {}", teamName);

		TeamStats stats = teamStatsStore.findByName(teamName);

		if(stats == null) {
			stats = getTeamInfo(teamName, stats);
		}
		stats = augmentStats(stats);

		return stats;
	}

	private TeamStats getTeamInfo(String teamName, TeamStats stats) {
		TeamProv prov = provDataService.getTeamProv(teamName);
		if(prov != null) {
			stats = TeamStats.builder()
					.id(prov.getId())
					.name(prov.getName())
					.build();
		}

		return stats;
	}

	private TeamStats augmentStats(TeamStats stats) {
		if(stats != null) {
			stats.setIdleAgents(agentEventService.getIdleCount(stats.getId()));
			stats.setAvailAgents(agentEventService.getAvailCount(stats.getId()));
			stats.setTalkAgents(agentEventService.getTalkCount(stats.getId()));
			stats.setUpdatedTime(System.currentTimeMillis());
		}

		return stats;
	}

	private void storeStats(Session s) {
		final TeamStats stats = TeamStats.builder()
				.id(s.getTeamId())
				.name(s.getTeamName())
				.updatedTime(System.currentTimeMillis())
				.build();

		s.getAggregation().forEach(aggr -> {
			switch(aggr.getName()) {
				case "idleAgents":
					stats.setIdleAgents(aggr.getValue());
					break;
				case "availAgents":
					stats.setAvailAgents(aggr.getValue());
					break;
				case "talkAgents":
					stats.setTalkAgents(aggr.getValue());
					break;
				case "avgIdleTime":
					stats.setAvgIdleTime(aggr.getValue());
					break;
				case "avgAvailTime":
					stats.setAvgAvailTime(aggr.getValue());
					break;
				case "avgTalkTime":
					stats.setAvgTalkTime(aggr.getValue());
					break;
				case "avgTotalTime":
					stats.setAvgTotalTime(aggr.getValue());
					break;
			}
		});

		teamStatsStore.save(stats);
	}

	@Bean
	public void teamStatsQuery() {
		statsQuery = QueryUtil.getQuery("realtime_team_stats");
	}
}
