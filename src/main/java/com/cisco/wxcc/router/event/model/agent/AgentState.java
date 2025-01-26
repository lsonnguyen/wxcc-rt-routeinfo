package com.cisco.wxcc.router.event.model.agent;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AgentState {
	/*
	@JsonProperty("login")
    LOGIN,

	@JsonProperty("logout")
    LOGOUT,
	*/

	@JsonProperty("idle")
    IDLE,

    @JsonProperty("available")
    AVAILABLE,

	@JsonProperty("ringing")
    RINGING,

	@JsonProperty("connected")
    CONNECTED,

	@JsonProperty("on-hold")
    ON_HOLD,

	@JsonProperty("hold-done")
    HOLD_DONE,

	@JsonProperty("wrapup")
    WRAPUP,

	@JsonProperty("wrapup-done")
    WRAPUP_DONE,

	@JsonProperty("logged-out")
    LOGGED_OUT,
}
