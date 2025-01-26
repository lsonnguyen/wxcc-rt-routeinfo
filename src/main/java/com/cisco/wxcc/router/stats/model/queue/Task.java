package com.cisco.wxcc.router.stats.model.queue;

import java.util.List;

import com.cisco.wxcc.router.stats.model.IdName;
import com.cisco.wxcc.router.stats.model.NameValue;
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
public class Task {

	private IdName queue;

	private IdName team;

	private List<NameValue> aggregation;

}
