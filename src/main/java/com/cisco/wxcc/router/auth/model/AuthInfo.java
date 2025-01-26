package com.cisco.wxcc.router.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthInfo {

	private String userId;

	private String userName;

	private String displayName;

	private String orgId;

	private String clientId;

	private String clientSecret;

	private Token accessToken;

	private Token refreshToken;

	private Boolean refreshing;

	private String loggedInUrl;

	private String loggedOutUrl;

	private String serviceUrl;
}
