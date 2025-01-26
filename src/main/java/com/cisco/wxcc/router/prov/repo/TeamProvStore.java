package com.cisco.wxcc.router.prov.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cisco.wxcc.router.prov.model.team.TeamProv;


public interface TeamProvStore  extends JpaRepository<TeamProv, String> {

	TeamProv findByName(String name);

	@Query("SELECT name FROM TeamProv tp WHERE tp.id IN :ids")
	List<String> getNameByIds(List<String> ids);
}
