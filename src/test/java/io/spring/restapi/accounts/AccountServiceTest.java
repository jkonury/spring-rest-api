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
