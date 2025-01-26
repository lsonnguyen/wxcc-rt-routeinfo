package com.cisco.wxcc.router.stats.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cisco.wxcc.router.stats.model.team.TeamStats;

public interface TeamStatsStore extends JpaRepository<TeamStats, String> {

	TeamStats findByName(String name);

}
