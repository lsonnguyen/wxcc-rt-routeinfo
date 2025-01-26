package com.cisco.wxcc.router.event.model.agent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class AgentData {

	@Id
	private String agentId;

	private String teamId;

	private String taskId;

	private String queueId;

	private String origin;

	private String destination;

	private Long createdTime;

	private AgentState currentState;
}
