package com.cisco.wxcc.router.event.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cisco.wxcc.router.event.model.agent.AgentData;
import com.cisco.wxcc.router.event.model.agent.AgentState;

public interface AgentDataStore extends JpaRepository<AgentData, String> {

	Integer countByTeamId(String teamId);

	@Query("SELECT COUNT(ad) FROM AgentData ad WHERE ad.teamId IN :teamIds")
	Integer countByTeamIds(List<String> teamIds);

	Integer countByCurrentState(AgentState currentState);

	Integer countByTeamIdAndCurrentState(String teamId, AgentState currentState);

}
