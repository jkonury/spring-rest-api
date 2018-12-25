package io.spring.restapi.configs;

import io.spring.restapi.accounts.Account;
import io.spring.restapi.accounts.AccountRepository;
import io.spring.restapi.accounts.AccountRole;
import io.spring.restapi.accounts.AccountService;
import java.util.Optional;
import java.util.Set;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

  @Bean
  public ModelMapper modelMapper() {
    return new ModelMapper();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public ApplicationRunner applicationRunner() {
    return new ApplicationRunner() {

      @Autowired
      AccountService accountService;

      @Autowired
      AccountRepository accountRepository;

      @Override
      public void run(ApplicationArguments args) throws Exception {
        final Account account = Account.builder()
          .email("test@test.com")
          .password("pass")
          .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
          .build();

        final Optional<Account> findAccount = accountRepository.findByEmail(account.getEmail());

        if (findAccount.isEmpty()) {
          accountService.saveAccount(account);
        }
      }
    };
  }
}
