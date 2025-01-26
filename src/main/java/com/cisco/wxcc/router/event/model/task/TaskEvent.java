package com.cisco.wxcc.router.event.model.task;

import com.cisco.wxcc.router.event.model.EventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskEvent {

	private String id;

	private EventType type;

	private TaskData data;

}
