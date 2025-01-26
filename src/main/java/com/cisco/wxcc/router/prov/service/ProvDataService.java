package com.cisco.wxcc.router.prov.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.cisco.wxcc.router.auth.model.AuthInfo;
import com.cisco.wxcc.router.prov.model.queue.QueueProv;
import com.cisco.wxcc.router.prov.model.queue.Queues;
import com.cisco.wxcc.router.prov.model.team.TeamProv;
import com.cisco.wxcc.router.prov.model.team.Teams;
import com.cisco.wxcc.router.prov.repo.QueueProvStore;
import com.cisco.wxcc.router.prov.repo.TeamProvStore;
import com.cisco.wxcc.router.util.ObjectUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProvDataService {

	@Autowired
	private ProvApiClient provApiClient;

	@Autowired
	private QueueProvStore queueProvStore;

	@Autowired
	private TeamProvStore teamProvStore;

	@EventListener
	public void authInfoEvent(AuthInfo authInfo) {
		log.warn("AuthInfo event for org {}, user {}",
				authInfo.getOrgId(),
				authInfo.getUserName());

		teamProvStore.deleteAll();
		queueProvStore.deleteAll();
		loadTeamProv();
		loadQueueProv();
	}

	public QueueProv getQueueProv(String name) {
		log.info("Get queue prov for {}", name);
		return queueProvStore.findByName(name);
	}

	public TeamProv getTeamProv(String name) {
		log.info("Get team prov for {}", name);
		return teamProvStore.findByName(name);
	}

	public List<String> getTeamNames(List<String> teamIds) {
		return teamProvStore.getNameByIds(teamIds);
	}

	private void loadTeamProv() {
		Teams teams = provApiClient.teams("filter=(active==true)&page=0&pageSize=200");
		//teams = service.teams("filter=(id==68ed110b-fee4-44f2-bf37-b5441b90f6ea)");

		teamProvStore.saveAll(teams.getData());
		ObjectUtil.logObj("TeamProv", teamProvStore.findAll());
	}

	private void loadQueueProv() {
		Queues queues = provApiClient.queues("filter=(active==true)&page=0&pageSize=200");
		//queues = service.queues("filter=(id==318cc5e0-5592-4c28-8c18-0a102eb0d3a5)");

		queues.getData().forEach(q -> {
			if(q.getCallDistributionGroups() != null) {
				q.getCallDistributionGroups().forEach(cdg -> {
					if(cdg.getAgentGroups() != null) {
						q.setTeamIds(new ArrayList<>());
						cdg.getAgentGroups().forEach(ag -> {
							q.getTeamIds().add(ag.getTeamId());
						});
					}
				});

				q.setCallDistributionGroups(null);
			}
		});

		queueProvStore.saveAll(queues.getData());
		ObjectUtil.logObj("QueueProv", queueProvStore.findAll());
	}
}
