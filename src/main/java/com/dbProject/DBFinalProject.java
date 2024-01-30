package com.dbProject;


import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@ComponentScan(basePackages = "com.dbProject")
@SpringBootApplication

public class DBFinalProject {

	public static void main(String[] args) {
		SpringApplication.run(DBFinalProject.class, args);
	}

}
