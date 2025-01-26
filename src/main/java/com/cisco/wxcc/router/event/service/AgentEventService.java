package com.cisco.wxcc.router.event.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.cisco.wxcc.router.auth.model.AuthInfo;
import com.cisco.wxcc.router.event.model.agent.AgentData;
import com.cisco.wxcc.router.event.model.agent.AgentState;
import com.cisco.wxcc.router.event.repo.AgentDataStore;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AgentEventService {

	@Autowired
	private AgentDataStore agentDataStore;

	private BlockingQueue<AgentData> agentDataQueue;

	@EventListener
	public void authInfoEvent(AuthInfo authInfo) {
		log.warn("AuthInfo event for org {}, user {}",
				authInfo.getOrgId(),
				authInfo.getUserName());

		agentDataStore.deleteAll();
	}

	public void handle(AgentData data) {
		agentDataQueue.add(data);
	}

	public List<AgentData> listAgentData() {
		return agentDataStore.findAll();
	}

	public Integer getAgentCount(String teamId) {
		return agentDataStore.countByTeamId(teamId);
	}

	public Integer getAgentCount(List<String>teamIds) {
		return agentDataStore.countByTeamIds(teamIds);
	}

	public Integer getIdleCount(String teamId) {
		return agentDataStore.countByTeamIdAndCurrentState(teamId, AgentState.IDLE);
	}

	public Integer getAvailCount(String teamId) {
		return agentDataStore.countByTeamIdAndCurrentState(teamId, AgentState.AVAILABLE);
	}

	public Integer getTalkCount(String teamId) {
		return agentDataStore.countByTeamIdAndCurrentState(teamId, AgentState.CONNECTED);
	}

	@Bean
	public void agentDataQueue() {
		agentDataQueue = new LinkedBlockingQueue<>();

		new Thread(new Runnable() {
            @Override
            public void run() {
        		do {
        			try {
        				AgentData data = agentDataQueue.take();
        				if(data.getCurrentState() == AgentState.LOGGED_OUT) {
        					agentDataStore.delete(data);
        				} else {
        					Optional<AgentData> curr = agentDataStore.findById(data.getAgentId());
        					if(curr.isPresent()) {
        						AgentData d = curr.get();
        						agentDataStore.save(mergeData(data, d));
        					} else {
        						agentDataStore.save(data);
        					}
        				}
        			} catch (InterruptedException e) {
        				log.error("Exception inserting agent data.", e);
        			}
        		} while(true);
            }

			private AgentData mergeData(final AgentData data, final AgentData d) {
				ReflectionUtils.doWithFields(AgentData.class, field -> {
				    field.setAccessible(true);

				    Object value = ReflectionUtils.getField(field, data);
				    if(value != null) {
				    	ReflectionUtils.setField(field, d, value);
				    }

				});

				return d;
			}
        }).start();

	}

	/*
	public void updateAgentData(AgentData data) {
		agentStore.save(data);
		agentStore.findAll().forEach(ad -> {
			LogUtil.logObj("AgentData", ad);
		});
		log.info("Idle count: {}", agentStore.countByCurrentState(AgentState.IDLE));
		log.info("Avail count: {}", agentStore.countByCurrentState(AgentState.AVAILABLE));
	}
	*/
}
