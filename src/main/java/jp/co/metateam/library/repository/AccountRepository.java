package jp.co.metateam.library.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import jp.co.metateam.library.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	Account findByEmail(String email);

	Optional<Account> findByEmployeeId(String employeeId);
}
