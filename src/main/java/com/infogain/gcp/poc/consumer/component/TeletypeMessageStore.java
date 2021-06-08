package com.infogain.gcp.poc.consumer.component;

import com.infogain.gcp.poc.consumer.entity.TeleTypeEntity;
import com.infogain.gcp.poc.consumer.repository.TASReactiveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TeletypeMessageStore {

    //private final TASRepository tasRepository;
    private final TASReactiveRepository tasReactiveRepository;

    public void saveMessagesList(List<TeleTypeEntity> teleTypeEntityList) {
        log.info("Saving all messages");
         Flux<TeleTypeEntity> teleTypeEntityFlux = tasReactiveRepository.saveAll(teleTypeEntityList);

         teleTypeEntityFlux.doOnComplete(() -> log.info("all messages saved")).blockLast();

    }
}
