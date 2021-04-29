package io.spring.restapi.accounts;

import static io.spring.restapi.accounts.AccountRole.ADMIN;
import static io.spring.restapi.accounts.AccountRole.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

  @Autowired
  AccountService accountService;

  @Autowired
  AccountRepository accountRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Test
  public void findByUsername() {
    // given
    final String email = "test@email.com";
    final String password = "password";

    final Account account = Account.builder()
      .email(email)
      .password(password)
      .roles(Set.of(ADMIN, USER))
      .build();

    accountService.saveAccount(account);

    // when
    final UserDetails userDetails = accountService.loadUserByUsername(email);

    // then
    assertThat(passwordEncoder.matches(password, userDetails.getPassword())).isTrue();
  }

  @Test
  public void findByUsernameFail() {
    assertThrows(UsernameNotFoundException.class, () -> {
      accountService.loadUserByUsername("");
    });

  }

  @Test
  public void expectedExceptionTest() {
    final String username = "test@mail.com";
    assertThrows(UsernameNotFoundException.class, () -> {
      accountService.loadUserByUsername("");
    }, username);
  }

  @Test
  public void equalsAccount() {
    EqualsVerifier
      .forClass(Account.class)
      .suppress(Warning.SURROGATE_KEY)
      .verify();
  }

  @Test
  public void simpleEqualsAccount() {
    EqualsVerifier
      .simple()
      .forClass(Account.class)
      .suppress(Warning.SURROGATE_KEY)
      .verify();
  }
}