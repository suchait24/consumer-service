package com.infogain.gcp.poc.consumer.entity;

import com.google.cloud.Timestamp;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Table("BATCH_EVENT_LOG")
public class BatchEventEntity {

    @Column("BATCH_EVENT_LOG_ID")
    @Id
    private String batchEventLogId;

    @Column("SUBSCRIBER_ID")
    private String subscriberId;

    @Column("BATCH_MESSAGE_ID")
    private Integer batchMessageId;

    @Column("BATCH_RECEIVED_TIME")
    private Timestamp batchReceivedTime;

    @Column("TOTAL_MESSAGES_BATCH_COUNT")
    private Integer totalMessageBatchCount;
}
