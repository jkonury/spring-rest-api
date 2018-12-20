package io.spring.restapi.accounts;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Autowired
  AccountService accountService;

  @Autowired
  AccountRepository accountRepository;

  @Test
  public void findByUsername() {
    // given
    final String email = "test@email.com";
    final String password = "password";

    final Account account = Account.builder()
      .email(email)
      .password(password)
      .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
      .build();

    accountRepository.save(account);

    // when
    final UserDetails userDetails = accountService.loadUserByUsername(email);

    // then
    assertThat(userDetails.getPassword()).isEqualTo(password);
  }

  @Test(expected = UsernameNotFoundException.class)
  public void findByUsernameFail() {
    accountService.loadUserByUsername("");
  }

  @Test
  public void expectedExceptionTest() {
    // expected
    String username = "test@email.com";
    expectedException.expect(UsernameNotFoundException.class);
    expectedException.expectMessage(Matchers.containsString(username));

    // when
    accountService.loadUserByUsername(username);
  }
}