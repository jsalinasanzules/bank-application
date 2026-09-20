package com.devsu.hackerearth.backend.account.controller;

import java.util.List;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devsu.hackerearth.backend.account.model.dto.AccountDto;
import com.devsu.hackerearth.backend.account.model.dto.PartialAccountDto;
import com.devsu.hackerearth.backend.account.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

	private final AccountService accountService;

	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	@GetMapping
	public ResponseEntity<List<AccountDto>> getAll(){
		// api/accounts
		// Get all accounts
		return ResponseEntity.ok(accountService.getAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<AccountDto> get(@PathVariable Long id){
		// api/accounts/{id}
		// Get accounts by id
		AccountDto account = accountService.getById(id);
		if (account == null){
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(account);
	}

	@PostMapping
	public ResponseEntity<AccountDto> create(@RequestBody AccountDto accountDto){
		// api/accounts
		// Create accounts
		AccountDto created = accountService.create(accountDto);
		return ResponseEntity.status(201).body(created);
	}

	@PutMapping("/{id}")
	public ResponseEntity<AccountDto> update(@PathVariable Long id, @RequestBody AccountDto accountDto){
		// api/accounts/{id}
		// Update accounts

		AccountDto accountExist = accountService.getById(id);
		if (accountExist == null){
			return ResponseEntity.notFound().build();
		}
		accountExist.setNumber(accountDto.getNumber());
		accountExist.setType(accountDto.getType());
		accountExist.setInitialAmount(accountDto.getInitialAmount());
		accountExist.setActive(accountDto.isActive());
		accountExist.setClientId(accountDto.getClientId());

		AccountDto account = accountService.update(accountExist);
		return ResponseEntity.ok(
				account
		);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<AccountDto> partialUpdate(@PathVariable Long id, @RequestBody PartialAccountDto partialAccountDto){
		// api/accounts/{id}
		// Partial update accounts
		AccountDto updatedAccount = accountService.partialUpdate(id, partialAccountDto);

		if(updatedAccount == null){
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(updatedAccount);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id){
		// api/accounts/{id}
		// Delete accounts
		AccountDto account = accountService.getById(id);
		if (account == null){
			return ResponseEntity.notFound().build();
		}
		accountService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}

