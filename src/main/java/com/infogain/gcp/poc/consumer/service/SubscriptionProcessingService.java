package com.infogain.gcp.poc.consumer.service;

import com.google.cloud.Timestamp;
import com.infogain.gcp.poc.consumer.component.BatchStore;
import com.infogain.gcp.poc.consumer.component.TeletypeMessageStore;
import com.infogain.gcp.poc.consumer.dto.BatchRecord;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;
import com.infogain.gcp.poc.consumer.entity.BatchEventEntity;
import com.infogain.gcp.poc.consumer.entity.TeleTypeEntity;
import com.infogain.gcp.poc.consumer.util.BatchEventEntityUtil;
import com.infogain.gcp.poc.consumer.util.BatchRecordUtil;
import com.infogain.gcp.poc.consumer.util.TeleTypeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gcp.pubsub.support.converter.ConvertedAcknowledgeablePubsubMessage;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionProcessingService {

    private static final String SUBSCRIBER_ID = "S1";
    private static Integer DEFAULT_SEQUENCE_NUMBER = 1;
    private final TeletypeMessageStore teletypeMessageStore;
    private final BatchStore batchStore;

    public Mono<Void> processMessages(List<ConvertedAcknowledgeablePubsubMessage<TeletypeEventDTO>> msgs, LocalDateTime batchReceivedTime) throws InterruptedException, ExecutionException, IOException, JAXBException {

        if (!msgs.isEmpty()) {

            List<TeletypeEventDTO> teletypeEventDTOList = msgs.stream().map(msg -> msg.getPayload()).collect(Collectors.toList());
            Mono<BatchRecord> batchRecord = BatchRecordUtil.createBatchRecord(teletypeEventDTOList, batchReceivedTime);

            batchRecord.subscribe(batchRecord1 -> processSubscriptionMessagesList(batchRecord1));

            //send acknowledge for all processed messages
            msgs.forEach(msg -> msg.ack());

        }

        return Mono.empty();
    }

    private void processSubscriptionMessagesList(BatchRecord batchRecord) {

        Instant start = Instant.now();

        List<TeletypeEventDTO> teletypeEventDTOList = null;

        if (!batchRecord.getDtoList().isEmpty())
            teletypeEventDTOList = batchRecord.getDtoList();

        log.info("Started processing subscription messages list , total records found : {}", teletypeEventDTOList.size());

        List<TeleTypeEntity> teleTypeEntityList = teletypeEventDTOList.stream()
                .map(record -> wrapTeletypeConversionException(record, DEFAULT_SEQUENCE_NUMBER++, batchRecord.getBatchMessageId()))
                .collect(Collectors.toList());

        log.info("before calling save method - saveMessagesList()");
        teletypeMessageStore.saveMessagesList(teleTypeEntityList);
        Flux<Object> dbFlux = teletypeMessageStore.saveMessagesList(teleTypeEntityList);
        dbFlux.subscribe(System.out::println);

        log.info("Processing stopped, all records processed  : {}", teletypeEventDTOList.size());

        Instant end = Instant.now();
        log.info("total time taken to process {} records is {} ms", teletypeEventDTOList.size(), Duration.between(start, end).toMillis());

    }

    private TeleTypeEntity wrapTeletypeConversionException(TeletypeEventDTO teletypeEventDTO, Integer sequenceNumber, Integer batchId) {

        try {
            Mono<String> payloadMsgMono = TeleTypeUtil.marshall(teletypeEventDTO);

            Mono<TeleTypeEntity> teleTypeEntityMono = TeleTypeUtil.convert(teletypeEventDTO, payloadMsgMono.block(), sequenceNumber, batchId);
            return teleTypeEntityMono.block();

        } catch (JAXBException e) {
            log.error("error occurred while converting : {}", e.getMessage());
        }
        return null;
    }

}
