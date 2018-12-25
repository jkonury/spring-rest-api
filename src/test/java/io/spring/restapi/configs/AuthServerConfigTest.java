package io.spring.restapi.configs;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.spring.restapi.accounts.Account;
import io.spring.restapi.accounts.AccountRole;
import io.spring.restapi.accounts.AccountService;
import io.spring.restapi.common.BaseControllerTest;
import io.spring.restapi.common.TestDescription;
import java.util.Set;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthServerConfigTest extends BaseControllerTest {
  @Autowired
  AccountService accountService;
  
  @Test
  @TestDescription("인증 토큰을 발급 받는 테스트")
  public void getAuthToken() throws Exception {
    String clientId = "myApp";
    String clientSecret = "pass";

    final String email = "test@email.com";
    final String password = "password";

    final Account account = Account.builder()
      .email(email)
      .password(password)
      .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
      .build();

    accountService.saveAccount(account);

    mockMvc.perform(post("/oauth/token")
      .with(httpBasic(clientId, clientSecret))
      .param("grant_type","password")
      .param("username", email)
      .param("password", password))
      .andDo(print())
      .andExpect(status().isOk())
      .andExpect(jsonPath("access_token").isNotEmpty())
      .andExpect(jsonPath("token_type").value("bearer"))
      .andExpect(jsonPath("refresh_token").isNotEmpty())
      .andExpect(jsonPath("expires_in").isNumber())
      .andExpect(jsonPath("scope").value("read write"))
    ;
  } 
}