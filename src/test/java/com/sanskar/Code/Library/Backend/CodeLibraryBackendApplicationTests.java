package com.sanskar.Code.Library.Backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CodeLibraryBackendApplicationTests {

	@Test
	void contextLoads() {
		System.out.println("MONGO_URI = " + System.getenv("MONGO_URI"));
	}

}
