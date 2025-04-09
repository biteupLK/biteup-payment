package com.biteup.biteup_payment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class BiteupPaymentApplicationTests {

	@Test
	void contextLoads() {
	}

}
