package com.cisco.wxcc.router.event.model.task;

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
public class TaskData {

	@Id
	private String taskId;

	private String queueId;

	private ChannelType channelType;

	private Direction direction;

	private String origin;

	private String destination;

	private Long createdTime;

	private TaskState currentState;
}
