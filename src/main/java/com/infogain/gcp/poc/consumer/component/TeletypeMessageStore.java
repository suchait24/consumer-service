package com.infogain.gcp.poc.consumer.component;

import com.google.cloud.spanner.DatabaseClient;
import com.infogain.gcp.poc.consumer.entity.TeleTypeEntity;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeletypeMessageStore {

    private final SpannerOps spannerOps;

    public void saveMessagesList(List<TeleTypeEntity> teleTypeEntityList) {
        log.info("Saving all messages");

        Connection connection = spannerOps.getConnection();

        processAll(teleTypeEntityList, connection);

        //teleTypeEntityList.forEach(record -> process(connection, record));

        /*
        List<Statement> statementList = teleTypeEntityList.stream()
                .map(entity -> createStatement(connection, entity))
                .collect(Collectors.toList());
         */


        log.info("All messages saved in database.");

    }

    private Statement createStatement(Connection connection, TeleTypeEntity teleTypeEntity) {
        Statement statement =
                connection.createStatement("INSERT INTO TAS (TAS_ID, BATCH_ID, CARRIER_CODE, CREATED_TIMESTAMP, HOST_LOCATOR, MESSAGE_CORRELATION_ID, PAYLOAD, SEQUENCE_NUMBER) VALUES(@tasId, @batchId ,@carrierCode, @createdTimestamp, @hostLocator, @messageCorrelationId, @payload, @sequenceNumber)")
                        .bind("tasId", teleTypeEntity.getTasId())
                        .bind("batchId", teleTypeEntity.getBatchId())
                        .bind("carrierCode", teleTypeEntity.getCarrierCode())
                        .bind("createdTimestamp", teleTypeEntity.getCreatedTimestamp())
                        .bind("hostLocator", teleTypeEntity.getHostLocator())
                        .bind("messageCorrelationId", teleTypeEntity.getMessageCorrelationId())
                        .bind("payload", teleTypeEntity.getPayload())
                        .bind("sequenceNumber", teleTypeEntity.getSequenceNumber());

        log.info("statement : {}", statement.toString());

        return statement;
    }

    private void processAll(List<TeleTypeEntity> teleTypeEntityList, Connection connection) {

            Statement statement = connection.createStatement("INSERT INTO TAS (TAS_ID, BATCH_ID, CARRIER_CODE, CREATED_TIMESTAMP, HOST_LOCATOR, MESSAGE_CORRELATION_ID, PAYLOAD, SEQUENCE_NUMBER) VALUES(@tasId, @batchId ,@carrierCode, @createdTimestamp, @hostLocator, @messageCorrelationId, @payload, @sequenceNumber)")
                    .returnGeneratedValues("id");

            for (TeleTypeEntity teleTypeEntity : teleTypeEntityList) {
                statement.bind("tasId", teleTypeEntity.getTasId())
                        .bind("batchId", teleTypeEntity.getBatchId())
                        .bind("carrierCode", teleTypeEntity.getCarrierCode())
                        .bind("createdTimestamp", teleTypeEntity.getCreatedTimestamp())
                        .bind("hostLocator", teleTypeEntity.getHostLocator())
                        .bind("messageCorrelationId", teleTypeEntity.getMessageCorrelationId())
                        .bind("payload", teleTypeEntity.getPayload())
                        .bind("sequenceNumber", teleTypeEntity.getSequenceNumber())
                        .add();
            }

        Flux.concat(connection.beginTransaction(),
                Flux.from(statement.execute()).flatMapSequential(r -> Mono.from(r.getRowsUpdated())).then(),
                connection.commitTransaction()
        ).doOnComplete(() -> log.info("Insert books transaction committed."));
                //.blockLast();
    }


    private void process(Connection connection, TeleTypeEntity teleTypeEntity) {

        Statement statement = createStatement(connection, teleTypeEntity);

        Flux.concat(connection.beginTransaction(),
                Flux.from(statement.execute()).flatMapSequential(r -> Mono.from(r.getRowsUpdated())).then(),
                connection.commitTransaction()
        ).doOnComplete(() -> log.info("Insert books transaction committed."))
                .blockLast();
    }
}
