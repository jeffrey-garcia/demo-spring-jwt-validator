package com.jeffrey.example.demospringjwtvalidator;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class DemoSpringJwtValidatorApplicationIT {

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	@Autowired
	MockMvc mvc;

	private static final String JWT_NO_SCOPE = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzdWJqZWN0IiwiZXhwIjoyMTY0MjQ1ODgwLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiMDFkOThlZWEtNjc0MC00OGRlLTk4ODAtYzM5ZjgyMGZiNzVlIiwiY2xpZW50X2lkIjoibm9zY29wZXMiLCJzY29wZSI6WyJub25lIl19.VOzgGLOUuQ_R2Ur1Ke41VaobddhKgUZgto7Y3AGxst7SuxLQ4LgWwdSSDRx-jRvypjsCgYPbjAYLhn9nCbfwtCitkymUKUNKdebvVAI0y8YvliWTL5S-GiJD9dN8SSsXUla9A4xB_9Mt5JAlRpQotQSCLojVSKQmjhMpQWmYAlKVjnlImoRwQFPI4w3Ijn4G4EMTKWUYRfrD0-WNT9ZYWBeza6QgV6sraP7ToRB3eQLy2p04cU40X-RHLeYCsMBfxsMMh89CJff-9tn7VDKi1hAGc_Lp9yS9ZaItJuFJTjf8S_vsjVB1nBhvdS_6IED_m_fOU52KiGSO2qL6shxHvg";
	private static final String JWT_READ_SCOPE = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzdWJqZWN0IiwiZXhwIjoyMTY0MjQ1NjQ4LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiY2I1ZGMwNDYtMDkyMi00ZGJmLWE5MzAtOGI2M2FhZTYzZjk2IiwiY2xpZW50X2lkIjoicmVhZGVyIiwic2NvcGUiOlsibWVzc2FnZTpyZWFkIl19.Pre2ksnMiOGYWQtuIgHB0i3uTnNzD0SMFM34iyQJHK5RLlSjge08s9qHdx6uv5cZ4gZm_cB1D6f4-fLx76bCblK6mVcabbR74w_eCdSBXNXuqG-HNrOYYmmx5iJtdwx5fXPmF8TyVzsq_LvRm_LN4lWNYquT4y36Tox6ZD3feYxXvHQ3XyZn9mVKnlzv-GCwkBohCR3yPow5uVmr04qh_al52VIwKMrvJBr44igr4fTZmzwRAZmQw5rZeyep0b4nsCjadNcndHtMtYKNVuG5zbDLsB7GGvilcI9TDDnUXtwthB_3iq32DAd9x8wJmJ5K8gmX6GjZFtYzKk_zEboXoQ";
	private static final String JWT_WRITE_SCOPE = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJzdWJqZWN0IiwiZXhwIjoyMTY0MjQzOTA0LCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiZGI4ZjgwMzQtM2VlNy00NjBjLTk3NTEtMDJiMDA1OWI5NzA4IiwiY2xpZW50X2lkIjoid3JpdGVyIiwic2NvcGUiOlsibWVzc2FnZTp3cml0ZSJdfQ.USvpx_ntKXtchLmc93auJq0qSav6vLm4B7ItPzhrDH2xmogBP35eKeklwXK5GCb7ck1aKJV5SpguBlTCz0bZC1zAWKB6gyFIqedALPAran5QR-8WpGfl0wFqds7d8Jw3xmpUUBduRLab9hkeAhgoVgxevc8d6ITM7kRnHo5wT3VzvBU8DquedVXm5fbBnRPgG4_jOWJKbqYpqaR2z2TnZRWh3CqL82Orh1Ww1dJYF_fae1dTVV4tvN5iSndYcGxMoBaiw3kRRi6EyNxnXnt1pFtZqc1f6D9x4AHiri8_vpBp2vwG5OfQD5-rrleP_XlIB3rNQT7tu3fiqu4vUzQaEg";

	@Before
	public void setup() {
		this.mvc = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilter(springSecurityFilterChain).build();
	}

	@Test
	public void verifyActautorHealthEndpointWithNoJwt() throws Exception {
		this.mvc.perform(get("/actuator/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("status", Matchers.containsString("UP")));
	}

	@Test
	public void verifyProtectedEndpointWithNoJwt() throws Exception {
		this.mvc.perform(get("/test1"))
				.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
	}

	@Test
	public void verifyProtectedEndpointWithNoScopeJwt() throws Exception {
		this.mvc.perform(get("/test1").with(bearerToken(JWT_NO_SCOPE)))
				.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
	}

	@Test
	public void verifyProtectedEndpointWithReadScopeJwt() throws Exception {
		this.mvc.perform(get("/test1").with(bearerToken(JWT_READ_SCOPE)))
				.andExpect(status().is(HttpStatus.OK.value()));
	}

	private static class BearerTokenRequestPostProcessor implements RequestPostProcessor {
		private String token;

		BearerTokenRequestPostProcessor(String token) {
			this.token = token;
		}

		@Override
		public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
			request.addHeader("Authorization", "Bearer " + this.token);
			return request;
		}
	}

	private static BearerTokenRequestPostProcessor bearerToken(String token) {
		return new BearerTokenRequestPostProcessor(token);
	}
}
