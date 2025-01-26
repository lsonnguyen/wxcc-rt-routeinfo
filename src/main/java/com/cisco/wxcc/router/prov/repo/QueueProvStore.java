package com.cisco.wxcc.router.prov.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.wxcc.router.prov.model.queue.QueueProv;


public interface QueueProvStore  extends JpaRepository<QueueProv, String> {

	QueueProv findByName(String name);
}
