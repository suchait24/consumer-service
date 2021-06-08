package com.infogain.gcp.poc.consumer.repository;


import com.infogain.gcp.poc.consumer.entity.BatchEventEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BatchEventReactiveRepository extends ReactiveCrudRepository<BatchEventEntity, String> {
}
