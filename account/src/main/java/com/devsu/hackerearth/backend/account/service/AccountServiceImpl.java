package com.devsu.hackerearth.backend.account.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


import org.springframework.stereotype.Service;

import com.devsu.hackerearth.backend.account.model.Account;

import com.devsu.hackerearth.backend.account.model.dto.AccountDto;
import com.devsu.hackerearth.backend.account.model.dto.PartialAccountDto;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;

@Service
public class AccountServiceImpl implements AccountService {

	private final AccountRepository accountRepository;

	public AccountServiceImpl(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

    @Override
    public List<AccountDto> getAll() {
        // Get all accounts
		return	accountRepository.findAll()
					.stream()
					.map(this::toDto)
					.collect(Collectors.toList());
    }

    @Override
    public AccountDto getById(Long id) {
        // Get accounts by id
		Account account = accountRepository.findById(id)
						.orElseThrow(()->
							new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Account not found"
                            ));
		
		return toDto(account);
    }

    @Override
    public AccountDto create(AccountDto accountDto) {
        // Create account
		Account account = toEntity(accountDto);
        account.setId(null);
		Account savedAccount = accountRepository.save(account);
		return toDto(savedAccount);
    }

    @Override
    public AccountDto update(AccountDto accountDto) {
        // Update account
		Account account = accountRepository.findById(accountDto.getId())
							.orElseThrow(()->
								new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Account not found"
                                ));
		
        account.setNumber(accountDto.getNumber());
        account.setType(accountDto.getType());
        account.setInitialAmount(accountDto.getInitialAmount());
        account.setActive(accountDto.isActive());
        account.setClientId(accountDto.getClientId());

		return toDto(accountRepository.save(account));
    }

    @Override
    public AccountDto partialUpdate(Long id, PartialAccountDto partialAccountDto) {
        // Partial update account
		Account account = accountRepository.findById(id)
							.orElseThrow(()->
								new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Account not found"
                                ));
		
		account.setActive(partialAccountDto.isActive());
		return toDto(accountRepository.save(account));
    }

    @Override
    public void deleteById(Long id) {
        // Delete account
        Account account = accountRepository.findById(id)
							.orElseThrow(()->
								new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Account not found"
                                ));
		
		accountRepository.delete(account);
    }

    private AccountDto toDto(Account account){
		return new AccountDto(
			account.getId(),
            account.getNumber(),
            account.getType(),
            account.getInitialAmount(),
            account.isActive(),
            account.getClientId()
		);
	}

	private Account toEntity(AccountDto accountDto){
		Account account = new Account();

		account.setId(accountDto.getId());
        account.setNumber(accountDto.getNumber());
        account.setType(accountDto.getType());
        account.setInitialAmount(accountDto.getInitialAmount());
        account.setActive(accountDto.isActive());
        account.setClientId(accountDto.getClientId());

		return account;
	}
    
}
