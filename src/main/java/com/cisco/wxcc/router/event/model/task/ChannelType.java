package com.cisco.wxcc.router.event.model.task;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ChannelType {

	@JsonProperty("telephony")
    TELEPHONY,

    @JsonProperty("chat")
    CHAT,

	@JsonProperty("email")
    EMAIL,

	@JsonProperty("social")
    SOCIAL
}
