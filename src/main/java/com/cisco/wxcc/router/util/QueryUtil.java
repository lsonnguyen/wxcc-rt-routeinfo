package com.cisco.wxcc.router.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.springframework.core.io.ClassPathResource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueryUtil {
	public static String getQuery(String name) {
		ClassPathResource resource = new ClassPathResource(
				String.format("queries/%s.graphql", name));
		try (Scanner scanner = new Scanner(
				resource.getInputStream(), StandardCharsets.UTF_8.name())) {
	        return scanner.useDelimiter("\\A").next();
	    } catch (IOException e) {
	    	log.error("Exception reading query {}. Message: {}", name, e.getMessage());
		}

		return null;
	}
}
