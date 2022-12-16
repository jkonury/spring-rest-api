package io.spring.restapi.configs;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.spring.restapi.accounts.AccountService;
import io.spring.restapi.common.AppProperties;
import io.spring.restapi.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthServerConfigTest extends BaseControllerTest {
  @Autowired AccountService accountService;

  @Autowired AppProperties appProperties;

  @Test
  @DisplayName("인증 토큰을 발급 받는 테스트")
  public void getAuthToken() throws Exception {

    mockMvc
        .perform(
            post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("grant_type", "password")
                .param("username", appProperties.getUserEmail())
                .param("password", appProperties.getUserPassword()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("access_token").isNotEmpty())
        .andExpect(jsonPath("token_type").value("bearer"))
        .andExpect(jsonPath("refresh_token").isNotEmpty())
        .andExpect(jsonPath("expires_in").isNumber())
        .andExpect(jsonPath("scope").value("read write"));
  }

  @Test
  @DisplayName("관리자 인증 토큰을 발급 받는 테스트")
  public void getAuthTokenAdmin() throws Exception {

    mockMvc
        .perform(
            post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("grant_type", "password")
                .param("username", appProperties.getAdminEmail())
                .param("password", appProperties.getAdminPassword()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("access_token").isNotEmpty())
        .andExpect(jsonPath("token_type").value("bearer"))
        .andExpect(jsonPath("refresh_token").isNotEmpty())
        .andExpect(jsonPath("expires_in").isNumber())
        .andExpect(jsonPath("scope").value("read write"));
  }
}
