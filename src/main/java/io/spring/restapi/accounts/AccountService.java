package io.spring.restapi.accounts;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

  private final AccountRepository accountRepository;
  private final PasswordEncoder passwordEncoder;

  public Account saveAccount(Account account) {
    account.setPassword(passwordEncoder.encode(account.getPassword()));
    return accountRepository.save(account);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final Account account =
        accountRepository
            .findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
    return new AccountAdapter(account);
  }
}
