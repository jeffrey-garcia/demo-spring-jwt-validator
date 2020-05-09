package com.jeffrey.example.demospringjwtvalidator;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
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

	@Before
	public void setup() {
		this.mvc = MockMvcBuilders.webAppContextSetup(this.context)
				.addFilter(springSecurityFilterChain).build();
	}

//	public static MockWebServer mockBackEnd;
//
//	@BeforeAll
//	static void setUp() throws IOException {
//		mockBackEnd = new MockWebServer();
//		mockBackEnd.start(8081);
//	}
//
//	@AfterAll
//	static void tearDown() throws IOException {
//		mockBackEnd.shutdown();
//	}

	@Test
	public void test1() throws Exception {
		this.mvc.perform(get("/actuator/health"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("status", Matchers.containsString("UP")));
	}

	@Test
	public void test2() throws Exception {
		this.mvc.perform(get("/test1"))
				.andExpect(status().is(401));
	}

	@Test
	public void test3() throws Exception {
		this.mvc.perform(get("/test1").with(bearerToken(JWT_NO_SCOPE)))
				.andExpect(status().is(401));
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
