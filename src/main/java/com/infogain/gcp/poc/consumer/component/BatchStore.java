package com.infogain.gcp.poc.consumer.component;

import com.infogain.gcp.poc.consumer.entity.BatchEventEntity;
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
public class BatchStore {

    private final SpannerOps spannerOps;

    public Flux<Object> saveBatchEventEntity(BatchEventEntity batchEventEntity) {
        log.info("Saving batch event entity to database");

        Connection connection = spannerOps.getConnection();
        return processBatchEvent(batchEventEntity, connection);
    }


    private Flux<Object> processBatchEvent(BatchEventEntity batchEventEntity, Connection connection) {

        Statement statement = connection.createStatement("INSERT INTO BATCH_EVENT_LOG (BATCH_EVENT_LOG_ID, SUBSCRIBER_ID, BATCH_MESSAGE_ID, BATCH_RECEIVED_TIME, TOTAL_MESSAGES_BATCH_COUNT) VALUES(@batchEventLogId, @subscriberId, @batchMessageId, @batchReceivedTime, @totalMessageCount)")
                .bind("batchEventLogId", batchEventEntity.getBatchEventLogId())
                    .bind("subscriberId", batchEventEntity.getSubscriberId())
                    .bind("batchMessageId", batchEventEntity.getBatchMessageId())
                    .bind("batchReceivedTime", batchEventEntity.getBatchReceivedTime())
                    .bind("totalMessageCount", batchEventEntity.getTotalMessageBatchCount());


        Flux<Object> finalFlux = Flux.concat(connection.beginTransaction(),
                Flux.from(statement.execute()).flatMapSequential(r -> Mono.from(r.getRowsUpdated())).then(),
                connection.commitTransaction(), Flux.just("BATCH RECORD SAVED.").log()
        );


        return finalFlux;
    }
}
