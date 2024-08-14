package com.burak.safa_bank_app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Safe Bank Application",
				description = "Banking backend project with RESTFUL APIs",
				version = "v0.1",
				contact = @Contact(
						name = "Ali Burak KAYA",
						email = "aliburakkayas@hotmail.com",
						url = "https://burakthedeveloper.vercel.app/"
				)
		)
)
public class SafaBankAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SafaBankAppApplication.class, args);
	}

}
