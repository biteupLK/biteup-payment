package com.biteup.biteup_payment;

import org.springframework.boot.SpringApplication;

public class TestBiteupPaymentApplication {

	public static void main(String[] args) {
		SpringApplication.from(BiteupPaymentApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
