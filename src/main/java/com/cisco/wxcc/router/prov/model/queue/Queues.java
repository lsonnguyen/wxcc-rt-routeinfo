package com.cisco.wxcc.router.prov.model.queue;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Queues {

	private List<QueueProv> data;

}
