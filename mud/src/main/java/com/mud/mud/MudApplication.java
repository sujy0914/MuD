package com.mud.mud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MudApplication {
	public static void main(String[] args) {SpringApplication.run(MudApplication.class, args);
	}

}
