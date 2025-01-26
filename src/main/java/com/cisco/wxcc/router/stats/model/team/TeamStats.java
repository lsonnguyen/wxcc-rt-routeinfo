package com.cisco.wxcc.router.stats.model.team;

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
//@JsonInclude(Include.NON_NULL)
public class TeamStats {

	@Id
	private String id;

	private String name;

	private Integer avgIdleTime;

	private Integer avgAvailTime;

	private Integer avgTalkTime;

	private Integer avgTotalTime;

	private Integer idleAgents;

	private Integer availAgents;

	private Integer talkAgents;

	private Long updatedTime;

}