package com.cisco.wxcc.router;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.devtools.restart.Restarter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class WxccRouterApp {

	public static void main(String[] args) {
		SpringApplication.run(WxccRouterApp.class, args);
	}

	@Scheduled(cron = "30 05 08 * * ?")
	public void restart() {
		log.warn("Restarting service...");
		Restarter.getInstance().restart();
	}
}
