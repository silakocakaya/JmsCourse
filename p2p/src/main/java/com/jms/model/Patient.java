package com.jms.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String insuranceProvider;
	private Double copay;
	private Double amountToBePayed;
}
