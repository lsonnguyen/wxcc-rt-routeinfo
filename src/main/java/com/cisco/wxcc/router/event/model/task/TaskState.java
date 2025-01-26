package com.cisco.wxcc.router.event.model.task;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TaskState {

    @JsonProperty("new")
    NEW,

    @JsonProperty("parked")
    PARKED,

    @JsonProperty("connect")
    CONNECT,

    @JsonProperty("connected")
    CONNECTED,

    @JsonProperty("ended")
    ENDED,

    @JsonProperty("consulting")
    CONSULTING,

    @JsonProperty("conferencing")
    CONFERENCING
}
