package io.spring.restapi.configs;

import io.spring.restapi.accounts.Account;
import io.spring.restapi.accounts.AccountRepository;
import io.spring.restapi.accounts.AccountRole;
import io.spring.restapi.accounts.AccountService;
import io.spring.restapi.common.AppProperties;
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

      @Autowired
      AppProperties appProperties;

      @Override
      public void run(ApplicationArguments args) throws Exception {
        final Account admin = Account.builder()
          .email(appProperties.getAdminEmail())
          .password(appProperties.getAdminPassword())
          .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
          .build();

        Optional<Account> findAccount = accountRepository.findByEmail(admin.getEmail());

        if (findAccount.isEmpty()) {
          accountService.saveAccount(admin);
        }

        final Account user = Account.builder()
          .email(appProperties.getUserEmail())
          .password(appProperties.getUserPassword())
          .roles(Set.of(AccountRole.USER))
          .build();

        findAccount = accountRepository.findByEmail(user.getEmail());

        if (findAccount.isEmpty()) {
          accountService.saveAccount(user);
        }
      }
    };
  }
}
