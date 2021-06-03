package com.infogain.gcp.poc.consumer.entity;

import com.google.cloud.Timestamp;
import lombok.*;


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
    private Timestamp createdTimestamp;
    private Timestamp updatedTimestamp;
    private Long sequenceNumber;
    private String payload;
}
