package com.cisco.wxcc.router.event.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.wxcc.router.event.model.task.TaskData;
import com.cisco.wxcc.router.event.model.task.TaskState;

public interface TaskDataStore extends JpaRepository<TaskData, String> {

	Integer countByQueueId(String queueId);

	Integer countByCurrentState(TaskState currentState);

	Integer countByQueueIdAndCurrentState(String queueId, TaskState currentState);

}
