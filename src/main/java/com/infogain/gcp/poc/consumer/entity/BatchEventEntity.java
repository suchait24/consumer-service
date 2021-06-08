package com.infogain.gcp.poc.consumer.entity;

import com.google.cloud.Timestamp;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class BatchEventEntity {

    private String batchEventLogId;
    private String subscriberId;
    private Integer batchMessageId;
    private Timestamp batchReceivedTime;
    private Integer totalMessageBatchCount;
}
