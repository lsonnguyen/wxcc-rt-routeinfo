package com.cisco.wxcc.router.event.model.agent;

import com.cisco.wxcc.router.event.model.EventType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentEvent {

	private String id;

	private EventType type;

	private AgentData data;

}
