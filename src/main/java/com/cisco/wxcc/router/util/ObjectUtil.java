package com.cisco.wxcc.router.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectUtil {

	public static void logObj(String name, Object obj) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			log.info("{}: {}", name, objectMapper.writeValueAsString(obj));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
