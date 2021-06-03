package com.infogain.gcp.poc.consumer.service.impl;

import com.infogain.gcp.poc.consumer.config.SpannerConfig;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import com.infogain.gcp.poc.consumer.entity.TeleTypeEntity;
import com.infogain.gcp.poc.consumer.service.TeleTypeService;
import com.infogain.gcp.poc.consumer.util.TeleTypeUtil;
import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Statement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBException;
import java.io.IOException;


@Service
@Slf4j
@RequiredArgsConstructor
public class TeleTypeServiceImpl implements TeleTypeService {

    private static final Integer DEFAULT_SEQUENCE_NUMBER = 10;

    @Override
    public void processMessage(String message) throws JAXBException, IOException {

        //TeletypeEventDTO teletypeEventDTO = TeleTypeUtil.unmarshall(message);
        //log.info("object unmarshalled : {}", teletypeEventDTO);

       // TeleTypeEntity teleTypeEntity = TeleTypeUtil.convert(teletypeEventDTO, message, DEFAULT_SEQUENCE_NUMBER);
        //log.info("converting dto to entity : {}", teleTypeEntity);

        //saveMessage(teleTypeEntity);
    }

    private void saveMessage(TeleTypeEntity teleTypeEntity) throws IOException {

        log.info("Inside save message method.");
        log.info(teleTypeEntity.toString());

        Connection connection = SpannerConfig.spannerConnection();
        Statement statement =
                connection.createStatement("INSERT INTO TAS (TAS_ID, CARRIER_CODE, CREATED_TIMESTAMP, HOST_LOCATOR, MESSAGE_CORRELATION_ID, PAYLOAD, SEQUENCE_NUMBER) VALUES(@tasId, @carrierCode, @createdTimestamp, @hostLocator, @messageCorrelationId, @payload, @sequenceNumber)")
                .bind("tasId", teleTypeEntity.getTasId())
                .bind("carrierCode", teleTypeEntity.getCarrierCode())
                .bind("createdTimestamp", teleTypeEntity.getCreatedTimestamp())
                .bind("hostLocator", teleTypeEntity.getHostLocator())
                .bind("messageCorrelationId", teleTypeEntity.getMessageCorrelationId())
                .bind("payload", teleTypeEntity.getPayload())
                .bind("sequenceNumber", teleTypeEntity.getSequenceNumber());

        Flux.concat(connection.beginTransaction(),
                Flux.from(statement.execute()).flatMapSequential(r -> Mono.from(r.getRowsUpdated())).then(),
                connection.commitTransaction()
        ).doOnComplete(() -> System.out.println("Insert books transaction committed."))
                .blockLast();


        log.info("message has been stored in db successfully.");
    }
}
