package com.sanskar.Code.Library.Backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("dev")
@SpringBootTest
class CodeLibraryBackendApplicationTests {

	@Test
	void contextLoads() {
		assertTrue(true);
	}

}
