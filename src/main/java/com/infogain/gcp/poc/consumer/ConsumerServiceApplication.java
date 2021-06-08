package com.infogain.gcp.poc.consumer;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;


@EnableR2dbcRepositories
@SpringBootApplication
public class ConsumerServiceApplication {

	private static final String SPANNER_INSTANCE = "instance-1";
	private static final String SPANNER_DATABASE = "database-1";
	private static final String GCP_PROJECT = "sab-ors-poc-sbx-01-9096";

	public static void main(String[] args) {
		SpringApplication.run(ConsumerServiceApplication.class, args);
	}

}
