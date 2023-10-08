package com.dnb.accountservice.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dnb.accountservice.dto.Account;
import com.dnb.accountservice.dto.User;
import com.dnb.accountservice.exceptions.AccountCloseException;
import com.dnb.accountservice.exceptions.IdNotFoundException;
import com.dnb.accountservice.exceptions.InsufficientBalanceException;
import com.dnb.accountservice.repo.AccountRepository;

@Service("accountServiceImpl")
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountRepository accountRepository;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Value("${api.auth}")
	String authUrl;
	
	@Override
	public Account createAccount(Account account) throws IdNotFoundException {
		try {
			ResponseEntity<User> responseEntity= restTemplate.getForEntity(authUrl+"/"+account.getUserId(), User.class);
			return accountRepository.save(account);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return account;
	}

	@Override
	public Optional<Account> getAccountById(String accountId) throws IdNotFoundException {
		return accountRepository.findById(accountId);
	}

	@Override
	public Iterable<Account> getAllAccounts() {
		return accountRepository.findAll();
	}

	@Override
	public Optional<Account> getAccountByUserId(Integer userId) {
		return accountRepository.findByUserId(userId);
	}

	@Override
	public boolean accountExistsById(String accountId) {
		if(accountRepository.existsById(accountId))return true;
		else return false;
	}

	@Override
	public boolean deleteAccountbyId(String accountId) throws IdNotFoundException {
		if(accountRepository.existsById(accountId)) {
			accountRepository.deleteById(accountId);
			if(accountRepository.existsById(accountId)) {
				return false;
			}
			return true;
		}
		else {
			throw new IdNotFoundException("Account Id not found");
		}
	}

	@Override
	public void deleteAccountByUserId(Integer userId) {
		accountRepository.deleteByUserId(userId);
	}

	@Override
	public Account withdrawAmount(String accountId, long amount) throws IdNotFoundException, InsufficientBalanceException, AccountCloseException {
		if(accountRepository.existsById(accountId)) {
			Optional<Account> account = accountRepository.findById(accountId);
			Account retrievedAccount = account.get();
			if(retrievedAccount.getAccountStatus()) {
				long accountBalance = retrievedAccount.getBalance();
				if((accountBalance-amount)<10000) {
					throw new InsufficientBalanceException("Insufficient balance to perform withdrawal");
				}
				else {
					retrievedAccount.setBalance(accountBalance - amount);
					return accountRepository.save(retrievedAccount);
				}
			}
			else {
				throw new AccountCloseException("Account closed can't perform fund transfer");
			}
		}
		else {
			throw new IdNotFoundException("Account Id Not found");
		}
	}

	@Override
	public Account depositAmount(String accountId, long amount) throws IdNotFoundException, AccountCloseException {
		if(accountRepository.existsById(accountId)) {
			Optional<Account> account = accountRepository.findById(accountId);
			Account retrievedAccount = account.get();
			if(retrievedAccount.getAccountStatus()) {
				long accountBalance = retrievedAccount.getBalance();
				retrievedAccount.setBalance(accountBalance + amount);
				return accountRepository.save(retrievedAccount);
			}
			else {
				throw new AccountCloseException("Account closed can't perform fund transfer");
			}
		}
		else {
			throw new IdNotFoundException("Account Id Not found");
		}
	}

	@Override
	public Account closeAccount(String accountId) throws IdNotFoundException, AccountCloseException {
		if(accountRepository.existsById(accountId)) {
			Optional<Account>account = accountRepository.findById(accountId);
			if(account.isEmpty()) {
				throw new IdNotFoundException("Id not found");
			}
			if(!account.get().getAccountStatus()) {
				throw new AccountCloseException("Account already closed");
			}
			else {
				Account account2=account.get();
				account2.setAccountStatus(false);
				return accountRepository.save(account2);
			}
		}
		else {
			throw new IdNotFoundException("Id not found");
		}
	}

}
