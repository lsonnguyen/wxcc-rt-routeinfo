package com.cisco.wxcc.router.auth;

import java.io.IOException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.cisco.wxcc.router.auth.model.AuthInfo;
import com.cisco.wxcc.router.auth.model.Token;
import com.cisco.wxcc.router.config.ServiceConfig;
import com.cisco.wxcc.router.util.DecodeUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthenticatedHandler implements AuthenticationSuccessHandler {

	private OAuth2AuthorizedClientService authClientService;

	private ServiceConfig serviceConfig;

	private ApplicationEventPublisher eventPublisher;

	public AuthenticatedHandler(OAuth2AuthorizedClientService authClientService,
			ServiceConfig serviceConfig, ApplicationEventPublisher eventPublisher) {
		this.authClientService = authClientService;
		this.serviceConfig = serviceConfig;
		this.eventPublisher = eventPublisher;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		String serviceUrl = request.getRequestURL().substring(0,
				request.getRequestURL().lastIndexOf(request.getRequestURI()));

		log.info("Service URL: {}", serviceUrl);

		OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
		OAuth2AuthorizedClient client = authClientService.loadAuthorizedClient(
				token.getAuthorizedClientRegistrationId(), token.getName());
		AuthInfo ai = AuthInfo.builder().build();

		try {
			ai = AuthInfo.builder()
					.userId(DecodeUtil.decodeOrgId(token.getPrincipal().getAttribute("id").toString()))
					.userName(token.getPrincipal().getAttribute("userName").toString())
					.displayName(token.getPrincipal().getAttribute("displayName").toString())
					.orgId(DecodeUtil.decodeOrgId(token.getPrincipal().getAttribute("orgId").toString()))
					.clientId(client.getClientRegistration().getClientId())
					.clientSecret(client.getClientRegistration().getClientSecret())
					.accessToken(Token.builder()
							.token(client.getAccessToken().getTokenValue())
							.expires(client.getAccessToken().getExpiresAt().getEpochSecond()-5)
							.build())
					.refreshToken(Token.builder()
							.token(client.getRefreshToken().getTokenValue())
							.expires(client.getRefreshToken().getExpiresAt() != null
									? client.getRefreshToken().getExpiresAt().getEpochSecond()
											: client.getAccessToken().getExpiresAt().getEpochSecond())
							.build())
					.refreshing(false)
					.serviceUrl(serviceUrl)
					.build();
		} catch(Exception e) {
			e.printStackTrace();
		}

		// Publish authentication info when user has successfully logged in to activate data services
		eventPublisher.publishEvent(ai);

		response.sendRedirect(serviceConfig.getLoggedInUrl());
	}

}
