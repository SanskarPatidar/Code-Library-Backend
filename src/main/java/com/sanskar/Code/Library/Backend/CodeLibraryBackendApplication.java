package com.sanskar.Code.Library.Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.net.InetAddress;

@SpringBootApplication
@EnableScheduling
public class CodeLibraryBackendApplication {

	public static void main(String[] args) {
        // some utility for testing
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            ip = "localhost";
        }
        String port = System.getProperty("server.port", "8080");
        System.out.println("Application origin: http://" + ip + ":" + port);
        SpringApplication.run(CodeLibraryBackendApplication.class, args);
    }

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
