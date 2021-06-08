package com.infogain.gcp.poc.consumer;


import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.DRIVER_NAME;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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

	@Bean
	public static ConnectionFactory spannerConnectionFactory() {
		ConnectionFactory connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
				.option(Option.valueOf("project"), GCP_PROJECT)
				.option(DRIVER, DRIVER_NAME)
				.option(INSTANCE, SPANNER_INSTANCE)
				.option(DATABASE, SPANNER_DATABASE)
				.option(Option.valueOf("client-implementation"), "client-library")
				.build());

		return connectionFactory;
	}
}
