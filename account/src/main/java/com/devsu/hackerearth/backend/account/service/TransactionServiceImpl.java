package com.devsu.hackerearth.backend.account.service;

import java.util.Date;
import java.util.List;

import java.util.stream.Collectors;
import java.util.Calendar;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;



import org.springframework.stereotype.Service;

import com.devsu.hackerearth.backend.account.model.dto.BankStatementDto;
import com.devsu.hackerearth.backend.account.model.dto.TransactionDto;
import com.devsu.hackerearth.backend.account.model.Account;
import com.devsu.hackerearth.backend.account.model.Transaction;
import com.devsu.hackerearth.backend.account.repository.TransactionRepository;
import com.devsu.hackerearth.backend.account.repository.AccountRepository;

import com.devsu.hackerearth.backend.account.exception.InsufficientBalanceException;

import org.springframework.web.client.RestTemplate;
import com.devsu.hackerearth.backend.account.model.dto.ClientResponseDto;


@Service
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final RestTemplate restTemplate;

	public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepository) {
		this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.restTemplate = new RestTemplate();
	}

    @Override
    public List<TransactionDto> getAll() {
        // Get all transactions
		return transactionRepository.findAll()
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
    }

    @Override
    public TransactionDto getById(Long id) {
        // Get transactions by id
        Transaction transaction = transactionRepository.findById(id)
                                    .orElseThrow(()->
                                    new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Transaction not found"
                                    ));

		return toDto(transaction);
    }

    @Override
    public TransactionDto create(TransactionDto transactionDto) {
        // Create transaction
        //JSA: validate account 
        Account account = accountRepository.findById(transactionDto.getAccountId())
                            .orElseThrow(()->
                            new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Account not found"
                            ));


        //current balance
        double currentBalance = transactionRepository
                                    .findTopByAccountIdOrderByDateDescIdDesc(transactionDto.getAccountId())
                                    .map(Transaction::getBalance)
                                    .orElse(account.getInitialAmount());

        //amount + o -
        double newBalance = currentBalance + transactionDto.getAmount();

        //validate amount
        if(newBalance < 0){
            throw new InsufficientBalanceException("Saldo no disponible");
        }

        //add transaction
        Transaction transaction = new Transaction();

        transaction.setDate(transactionDto.getDate()!= null
                                ? transactionDto.getDate()
                                : new Date()
                            );
        
        transaction.setType(transactionDto.getType());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setBalance(newBalance);
        transaction.setAccountId(transactionDto.getAccountId());

        //save
        Transaction savedTransaction = transactionRepository.save(transaction);


		return toDto(savedTransaction);
    }

    @Override
    public List<BankStatementDto> getAllByAccountClientIdAndDateBetween(Long clientId, Date dateTransactionStart,
            Date dateTransactionEnd) {
        // Report

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTransactionEnd);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);

        Date endOfDay = calendar.getTime();

        ClientResponseDto client = restTemplate.getForObject(
            "http://localhost:8001/api/clients/"+clientId,
            ClientResponseDto.class
        );

        List<Account> accounts = accountRepository.findByClientId(clientId);
        
        return accounts.stream()
                .flatMap(account ->
                    transactionRepository
                        .findByAccountIdAndDateBetweenOrderByDateAsc(
                            account.getId(),
                            dateTransactionStart,
                            endOfDay
                        )
                        .stream()
                        .map(transaction ->
                                new BankStatementDto(
                                    transaction.getDate(),
                                    client != null ? client.getName() : null,
                                    account.getNumber(),
                                    account.getType(),
                                    account.getInitialAmount(),
                                    account.isActive(),
                                    transaction.getType(),
                                    transaction.getAmount(),
                                    transaction.getBalance()
                                    
                                )

                        )
                )
                .collect(Collectors.toList());


    }

    @Override
    public TransactionDto getLastByAccountId(Long accountId) {
        // If you need it
		Transaction transaction = transactionRepository.findTopByAccountIdOrderByDateDescIdDesc(accountId)
                                    .orElseThrow(()->
                                    new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Transaction not found"
                                    ));

                                       

		return toDto(transaction);
    }

    private TransactionDto toDto(Transaction transaction){
        return new TransactionDto(
            transaction.getId(),
            transaction.getDate(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getBalance(),
            transaction.getAccountId()

        );
    }
    
    
}
