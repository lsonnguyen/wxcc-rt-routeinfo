package com.cisco.wxcc.router.stats.model.queue;

import java.util.List;

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
public class QueueStats {

	@Id
	private String id;

	private String name;

	private Integer queuedCount;

	private Integer connectedCount;

	private Integer abandonedCount;

	private Integer agentCount;

	private Integer avgQueueTime;

	private Integer avgTalkTime;

	private Integer estWaitTime;

	private Integer maxWaitTime;

	private List<String> teams;

	private Long updatedTime;
}
