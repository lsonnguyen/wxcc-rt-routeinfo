package com.cisco.wxcc.router.event.model.task;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Direction {

	@JsonProperty("INBOUND")
    INBOUND,

    @JsonProperty("OUTBOUND")
    OUTBOUND

}
