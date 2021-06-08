package com.infogain.gcp.poc.consumer.service;

import com.google.cloud.Timestamp;
import com.infogain.gcp.poc.consumer.component.BatchStore;
import com.infogain.gcp.poc.consumer.component.TeletypeMessageStore;
import com.infogain.gcp.poc.consumer.component.TeletypePublisher;
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

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class SubscriptionProcessingService {

    private static final String SUBSCRIBER_ID = "S1";
    private static Integer DEFAULT_SEQUENCE_NUMBER = 1;
    private final BatchStore batchStore;

    private final TeletypePublisher teletypePublisher;

    public void processMessages(List<ConvertedAcknowledgeablePubsubMessage<TeletypeEventDTO>> msgs, Timestamp batchReceivedTime) throws InterruptedException, ExecutionException, IOException, JAXBException {

        if (!msgs.isEmpty()) {
            List<TeletypeEventDTO> teletypeEventDTOList = msgs.stream().map(msg -> msg.getPayload()).collect(Collectors.toList());
            BatchRecord batchRecord = BatchRecordUtil.createBatchRecord(teletypeEventDTOList, batchReceivedTime);
            List<TeleTypeEntity> teleTypeEntityList = processSubscriptionMessagesList(batchRecord);

            //send acknowledge for all processed messages
            msgs.forEach(msg -> msg.ack());
        }
    }

    private List<TeleTypeEntity> processSubscriptionMessagesList(BatchRecord batchRecord) throws InterruptedException, ExecutionException, IOException, JAXBException {

        Instant start = Instant.now();

        List<TeletypeEventDTO> teletypeEventDTOList = null;

        if (!batchRecord.getDtoList().isEmpty())
            teletypeEventDTOList = batchRecord.getDtoList();

        log.info("Started processing subscription messages list , total records found : {}", teletypeEventDTOList.size());

        List<TeleTypeEntity> teleTypeEntityList = teletypeEventDTOList.stream()
                .map(record -> wrapTeletypeConversionException(record, DEFAULT_SEQUENCE_NUMBER++, batchRecord.getBatchMessageId()))
                .collect(Collectors.toList());

        //send all processed messages to another topic.
        teletypePublisher.processPublish(teleTypeEntityList);

        log.info("Processing stopped, all records processed  : {}", teletypeEventDTOList.size());


        log.info("Logging batch to database now.");
        BatchEventEntity batchEventEntity = BatchEventEntityUtil.createBatchEventEntity(teleTypeEntityList, batchRecord, SUBSCRIBER_ID);
        log.info("Batch entity generated : {}", batchEventEntity);

        batchStore.saveBatchEventEntity(batchEventEntity);

        Instant end = Instant.now();
        log.info("total time taken to process {} records is {} ms", teletypeEventDTOList.size(), Duration.between(start, end).toMillis());

        return teleTypeEntityList;
    }

    private TeleTypeEntity wrapTeletypeConversionException(TeletypeEventDTO teletypeEventDTO, Integer sequenceNumber, Integer batchId) {

        try {
            return TeleTypeUtil.convert(teletypeEventDTO, TeleTypeUtil.marshall(teletypeEventDTO), sequenceNumber, batchId);
        } catch (JAXBException e) {
            log.error("error occurred while converting : {}", e.getMessage());
        }
        return null;
    }

}
