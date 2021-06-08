package com.infogain.gcp.poc.consumer.component;

import com.infogain.gcp.poc.consumer.entity.BatchEventEntity;
import com.infogain.gcp.poc.consumer.repository.BatchEventReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchStore {

    private final BatchEventReactiveRepository batchEventReactiveRepository;

    public void saveBatchEventEntity(BatchEventEntity batchEventEntity) {
        log.info("Saving batch event entity to database");
        batchEventReactiveRepository.save(batchEventEntity);
        log.info("batch event entity successfully saved.");
    }
}
