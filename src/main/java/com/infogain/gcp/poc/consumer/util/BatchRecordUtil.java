package com.infogain.gcp.poc.consumer.util;

import com.google.cloud.Timestamp;
import com.infogain.gcp.poc.consumer.dto.BatchRecord;
import com.infogain.gcp.poc.consumer.dto.TeletypeEventDTO;

import java.util.List;
import java.util.Random;

public class BatchRecordUtil {

    private static Integer BATCH_MESSAGE_ID = 1;

    public static BatchRecord createBatchRecord(List<TeletypeEventDTO> teletypeEventDTOList, Timestamp batchReceivedTime) {

        Random random = new Random();

        BatchRecord batchRecord = new BatchRecord();
        batchRecord.setDtoList(teletypeEventDTOList);
        //batchRecord.setBatchMessageId(random.nextInt(7));
        batchRecord.setBatchMessageId(BATCH_MESSAGE_ID++);
        batchRecord.setBatchReceivedTime(batchReceivedTime);

        return batchRecord;
    }

}
