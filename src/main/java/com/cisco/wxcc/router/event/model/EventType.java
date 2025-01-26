package com.cisco.wxcc.router.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EventType {

	@JsonProperty("agent:login")
    AGENT_LOGIN,

    @JsonProperty("agent:logout")
    AGENT_LOGOUT,

	@JsonProperty("agent:state_change")
    AGENT_STATE_CHANGE,

    @JsonProperty("task:new")
    TASK_NEW,

    @JsonProperty("task:parked")
    TASK_PARKED,

    @JsonProperty("task:connect")
    TASK_CONNECT,

    @JsonProperty("task:connected")
    TASK_CONNECTED,

    @JsonProperty("task:ended")
    TASK_ENDED,

    @JsonProperty("task:consulting")
    TASK_CONSULTING,

    @JsonProperty("task:conferencing")
    TASK_CONFERENCING
}
