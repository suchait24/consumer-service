package com.infogain.gcp.poc.consumer.component;

import io.r2dbc.spi.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.DRIVER_NAME;
import static com.google.cloud.spanner.r2dbc.SpannerConnectionFactoryProvider.INSTANCE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;

@Getter
@Setter
@Component
public class SpannerOps {

    private final ConnectionFactory connectionFactory;
    //private final Connection connection;


    private static final String sampleInstance = "instance-1";
    private static final String sampleDatabase = "database-1";
    private static final String sampleProjectId = "sab-ors-poc-sbx-01-9096";


    public SpannerOps() {
        this.connectionFactory = ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(Option.valueOf("project"), sampleProjectId)
                .option(DRIVER, DRIVER_NAME)
                .option(INSTANCE, sampleInstance)
                .option(DATABASE, sampleDatabase)
                .build());

    }

    public Connection getConnection() {
        return Mono.from(this.connectionFactory.create()).block();
    }

}
