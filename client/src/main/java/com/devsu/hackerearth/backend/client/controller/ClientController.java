package com.devsu.hackerearth.backend.client.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;


import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.devsu.hackerearth.backend.client.model.dto.ClientDto;
import com.devsu.hackerearth.backend.client.model.dto.PartialClientDto;
import com.devsu.hackerearth.backend.client.service.ClientService;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

	private final ClientService clientService;

	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}

	@GetMapping
	public ResponseEntity<List<ClientDto>> getAll(){
		// api/clients
		// Get all clients
		return ResponseEntity.ok(clientService.getAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ClientDto> get(@PathVariable Long id){
		// api/clients/{id}
		// Get clients by id
		ClientDto client = clientService.getById(id);
		if(client == null){
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(client);
	}

	@PostMapping
	public ResponseEntity<ClientDto> create(@RequestBody ClientDto clientDto){
		// api/clients
		// Create client
		ClientDto result = clientService.create(clientDto);

		return ResponseEntity.status(201).body(result);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ClientDto> update(@PathVariable Long id, @RequestBody ClientDto clientDto){
		// api/clients/{id}
		// Update client
		
		ClientDto client = clientService.getById(id);
		if(client == null){
			return ResponseEntity.notFound().build();
		}
		client.setDni(clientDto.getDni());
		client.setName(clientDto.getName());
		client.setPassword(clientDto.getPassword());
		client.setGender(clientDto.getGender());
		client.setAge(clientDto.getAge());
		client.setAddress(clientDto.getAddress());
		client.setPhone(clientDto.getPhone());
		client.setActive(clientDto.isActive());
		
		ClientDto clientUpdated = clientService.update(client);
		return ResponseEntity.ok(clientUpdated);
	}

	@PatchMapping("/{id}")
	public ResponseEntity<ClientDto> partialUpdate(@PathVariable Long id, @RequestBody PartialClientDto partialClientDto){
		// api/accounts/{id}
		// Partial update accounts
		ClientDto client = clientService.partialUpdate(id,partialClientDto);
		if(client == null){
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(
			client
		);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id){
		// api/clients/{id}
		// Delete client
		ClientDto client = clientService.getById(id);
		if(client == null){
			return ResponseEntity.notFound().build();
		}
		clientService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
