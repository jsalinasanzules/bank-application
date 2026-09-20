package com.devsu.hackerearth.backend.client.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

import com.devsu.hackerearth.backend.client.model.dto.ClientDto;
import com.devsu.hackerearth.backend.client.model.Client;

import com.devsu.hackerearth.backend.client.model.dto.PartialClientDto;
import com.devsu.hackerearth.backend.client.repository.ClientRepository;

@Service
public class ClientServiceImpl implements ClientService {

	private final ClientRepository clientRepository;

	public ClientServiceImpl(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	@Override
	public List<ClientDto> getAll() {
		// Get all clients
		return	clientRepository.findAll()
					.stream()
					.map(this::toDto)
					.collect(Collectors.toList());
	}

	@Override
	public ClientDto getById(Long id) {
		// Get clients by id
		Client client = clientRepository.findById(id)
						.orElseThrow(()->
							new ResponseStatusException(
								HttpStatus.NOT_FOUND,
								"Client not found"
							));
		
		return toDto(client);

	}

	@Override
	public ClientDto create(ClientDto clientDto) {
		// Create client
		Client client = toEntity(clientDto);
		Client savedClient = clientRepository.save(client);
		return toDto(savedClient);
	}

	@Override
	public ClientDto update(ClientDto clientDto) {
		// Update client
		Client client = clientRepository.findById(clientDto.getId())
							.orElseThrow(()->
								new ResponseStatusException(
									HttpStatus.NOT_FOUND,
									"Client not found"
								));
		
		client.setDni(clientDto.getDni());
		client.setName(clientDto.getName());
		client.setPassword(clientDto.getPassword());
		client.setGender(clientDto.getGender());
		client.setAge(clientDto.getAge());
		client.setAddress(clientDto.getAddress());
		client.setPhone(clientDto.getPhone());
		client.setActive(clientDto.isActive());

		return toDto(clientRepository.save(client));
	}

	@Override
    public ClientDto partialUpdate(Long id, PartialClientDto partialClientDto) {
        // Partial update account
		Client client = clientRepository.findById(id)
							.orElseThrow(()->
								new ResponseStatusException(
									HttpStatus.NOT_FOUND,
									"Client not found"
								));
		
		client.setActive(partialClientDto.isActive());
		return toDto(clientRepository.save(client));
    }

	@Override
	public void deleteById(Long id) {
		// Delete client
		Client client = clientRepository.findById(id)
							.orElseThrow(()->
								new ResponseStatusException(
									HttpStatus.NOT_FOUND,
									"Client not found"
								));
		
		clientRepository.delete(client);
	}

	private ClientDto toDto(Client client){
		return new ClientDto(
			client.getId(),
			client.getDni(),
			client.getName(),
			client.getPassword(),
			client.getGender(),
			client.getAge(),
			client.getAddress(),
			client.getPhone(),
			client.isActive()
		);
	}

	private Client toEntity(ClientDto clientDto){
		Client client = new Client();

		client.setDni(clientDto.getDni());
		client.setName(clientDto.getName());
		client.setPassword(clientDto.getPassword());
		client.setGender(clientDto.getGender());
		client.setAge(clientDto.getAge());
		client.setAddress(clientDto.getAddress());
		client.setPhone(clientDto.getPhone());
		client.setActive(clientDto.isActive());

		return client;
	}
}
