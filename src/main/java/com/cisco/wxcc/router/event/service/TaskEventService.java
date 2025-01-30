package com.cisco.wxcc.router.event.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import com.cisco.wxcc.router.auth.model.AuthInfo;
import com.cisco.wxcc.router.event.model.task.TaskData;
import com.cisco.wxcc.router.event.model.task.TaskState;
import com.cisco.wxcc.router.event.repo.TaskDataStore;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TaskEventService {

	@Autowired
	private TaskDataStore taskDataStore;

	private BlockingQueue<TaskData> taskDataQueue;

	@EventListener
	public void authInfoEvent(AuthInfo authInfo) {
		log.warn("AuthInfo event for org {}, user {}",
				authInfo.getOrgId(),
				authInfo.getUserName());

		taskDataStore.deleteAll();
	}

	public void handle(TaskData data) {
		taskDataQueue.add(data);
	}

	public List<TaskData> listTaskData() {
		return taskDataStore.findAll();
	}

	public Integer getQueuedCount(String queueId) {
		return taskDataStore.countByQueueIdAndCurrentState(queueId, TaskState.PARKED);
	}

	public Integer getConnectedCount(String queueId) {
		return taskDataStore.countByQueueIdAndCurrentState(queueId, TaskState.CONNECTED);
	}

	public Integer getMaxQueueTime(String queueId) {
		Optional<TaskData> opt = taskDataStore
				.findFirstByQueueIdAndCurrentStateOrderByCreatedTimeAsc(
						queueId, TaskState.PARKED);

		if(opt.isPresent()) {
			TaskData td = opt.get();
			log.info("Longest wait - taskId: {}, createdTime: {}", td.getTaskId(), td.getCreatedTime());

			return Integer.valueOf((int) (System.currentTimeMillis() - td.getCreatedTime().longValue()));
		}

		return null;
	}

	@Bean
	public void taskDataQueue() {
		taskDataQueue = new LinkedBlockingQueue<>();

		new Thread(new Runnable() {
            @Override
            public void run() {
        		do {
        			try {
        				TaskData data = taskDataQueue.take();

        				if(data.getCurrentState() == TaskState.ENDED) {
        					taskDataStore.delete(data);
        				} else {
        					Optional<TaskData> opt = taskDataStore.findById(data.getTaskId());
        					if(opt.isPresent()) {
        						TaskData td = opt.get();
        						taskDataStore.save(mergeData(data, td));
        					} else {
        						taskDataStore.save(data);
        					}
        				}
        			} catch (InterruptedException e) {
        				log.error("Exception inserting task data.", e);
        			}
        		} while(true);
            }

			private TaskData mergeData(final TaskData data, final TaskData d) {
				ReflectionUtils.doWithFields(TaskData.class, field -> {
				    field.setAccessible(true);

				    Object value = ReflectionUtils.getField(field, data);
				    if(value != null) {
				    	ReflectionUtils.setField(field, d, value);
				    }

				});

				return d;
			}
        }).start();
	}
}
