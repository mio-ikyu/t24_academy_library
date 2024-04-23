package jp.co.metateam.library.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.metateam.library.model.Account;
import jp.co.metateam.library.model.AccountDto;
import jp.co.metateam.library.repository.AccountRepository;

@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder){
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = this.accountRepository.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new AccountPrincipal(account);
    }

    public Account findByEmail(String email) {
        return this.accountRepository.findByEmail(email);
    }

    public Account findByEmployeeId(String employeeId) {
        return this.accountRepository.findByEmployeeId(employeeId).orElse(null);
    }

    public List<Account> findAll() {
        return this.accountRepository.findAll();
    }

    @Transactional
    public void save(AccountDto accountDto) {
        try {
            // AccountDtoからAccountへの変換
            Account account = new Account();

            account.setName(accountDto.getName());
            account.setEmployeeId(accountDto.getEmployeeId());
            account.setAuthorizationType(accountDto.getAuthorizationType());
            account.setPassword(this.passwordEncoder.encode(accountDto.getPassword())); // パスワードをハッシュ化してから保存
            account.setEmail(accountDto.getEmail());

            // データベースへの保存
            this.accountRepository.save(account);
        } catch (Exception e) {
            throw e;
        }
    }
}



