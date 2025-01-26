package com.cisco.wxcc.router.service;

import java.util.Arrays;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.cisco.wxcc.router.auth.model.AuthInfo;
import com.cisco.wxcc.router.auth.service.AuthInfoService;
import com.cisco.wxcc.router.config.ServiceConfig;

@Service
public abstract class WxccApiClient {

	@Autowired
	protected ServiceConfig serviceConfig;

	@Autowired
	protected AuthInfoService authInfoService;

	@Autowired
	protected WebClient webClient;

	public AuthInfo authInfo() {
		return authInfoService.getAuthInfo();
	}

	protected Consumer<HttpHeaders> httpHeaders() {
		return headers -> {
			headers.setBearerAuth(authInfo().getAccessToken().getToken());
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		};
	}

}
