package com.infogain.gcp.poc.consumer.config;

import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.*;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;

import com.google.auth.oauth2.GoogleCredentials;
import io.r2dbc.spi.*;
import reactor.core.publisher.Mono;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class SpannerConfig {

    private static final String SPANNER_INSTANCE = "instance-1";
    private static final String SPANNER_DATABASE = "database-1";
    private static final String GCP_PROJECT = "sab-ors-poc-sbx-01-9096";
    private static final String SPANNER_URL = "r2dbc:spanner://spanner.googleapis.com:443/projects/" + GCP_PROJECT + "/instances/ " + SPANNER_INSTANCE + "/databases/ " + SPANNER_DATABASE;

    public static Connection spannerConnection() throws IOException {

        GoogleCredentials creds = GoogleCredentials.fromStream(new FileInputStream("C:\\Users\\SG0310427\\Downloads\\sab-ors-poc-sbx-01-9096-f8581ce293f7.json"));

        ConnectionFactory connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(GOOGLE_CREDENTIALS, creds)
                //.option(Option.valueOf("project"), GCP_PROJECT)
                //.option(DRIVER, DRIVER_NAME)
                //.option(INSTANCE, SPANNER_INSTANCE)
                //.option(DATABASE, SPANNER_DATABASE)
                .option(URL,SPANNER_URL)
                .option(Option.valueOf("client-implementation"), "client-library")
                .build());

        Connection connection = Mono.from(connectionFactory.create()).block();

        return connection;
    }
}
