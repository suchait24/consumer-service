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
@Table("TAS")
public class TeleTypeEntity {

    @Column("TAS_ID")
    @Id
    private String tasId;

    @Column("HOST_LOCATOR")
    private String hostLocator;

    @Column("MESSAGE_CORRELATION_ID")
    private String messageCorrelationId;

    @Column("CARRIER_CODE")
    private String carrierCode;

    @Column("CREATED_TIMESTAMP")
    private Timestamp createdTimestamp;

    @Column("UPDATED_TIMESTAMP")
    private Timestamp updatedTimestamp;

    @Column("SEQUENCE_NUMBER")
    private Long sequenceNumber;

    @Column("PAYLOAD")
    private String payload;

    @Column("BATCH_ID")
    private Integer batchId;
}
