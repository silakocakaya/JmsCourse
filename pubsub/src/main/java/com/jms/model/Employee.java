package com.jms.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee implements Serializable {

	private int id;
	private String firstName;
	private String lastName;
	private String email;
	private String designation;
	private String phone;
}
