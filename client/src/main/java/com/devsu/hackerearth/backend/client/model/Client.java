package com.devsu.hackerearth.backend.client.model;

import javax.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class Client extends Person {
	private String password;
	private boolean isActive;
}
