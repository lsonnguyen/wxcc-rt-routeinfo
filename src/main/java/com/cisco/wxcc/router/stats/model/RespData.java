package com.cisco.wxcc.router.stats.model;

import com.cisco.wxcc.router.stats.model.queue.TaskDetails;
import com.cisco.wxcc.router.stats.model.team.SessionDetails;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class RespData {

	private TaskDetails taskDetails;

	private SessionDetails agentSession;

}
