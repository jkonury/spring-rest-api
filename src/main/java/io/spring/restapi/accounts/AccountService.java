package io.spring.restapi.accounts;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;

  public Account saveAccount(Account account) {
    return accountRepository.save(account);
  }

}
