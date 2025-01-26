package com.cisco.wxcc.router.auth;

import java.io.IOException;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationFilter extends GenericFilterBean {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		log.debug("Request URL: {}", httpReq.getRequestURL());

		if(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
				|| httpReq.getRequestURI().contains("events")
				|| httpReq.getRequestURI().contains("stats")) {
			String orgId = httpReq.getHeader("OrgID");

			//log.info("OrgID: {}", orgId);

			HttpServletResponse httpResp = (HttpServletResponse) response;
			chain.doFilter(httpReq, httpResp);
		}
	}

}
