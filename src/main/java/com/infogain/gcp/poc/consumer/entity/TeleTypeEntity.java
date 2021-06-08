package com.infogain.gcp.poc.consumer.entity;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TeleTypeEntity {

    private String tasId;
    private String hostLocator;
    private String messageCorrelationId;
    private String carrierCode;
    private LocalDateTime createdTimestamp;
    private LocalDateTime updatedTimestamp;
    private Long sequenceNumber;
    private String payload;
    private Integer batchId;
}
