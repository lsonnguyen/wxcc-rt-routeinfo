package com.cisco.wxcc.router.stats.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.wxcc.router.stats.model.queue.QueueStats;

public interface QueueStatsStore extends JpaRepository<QueueStats, String> {

	QueueStats findByName(String name);
}
