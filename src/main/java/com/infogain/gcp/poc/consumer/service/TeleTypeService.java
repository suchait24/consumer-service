package com.infogain.gcp.poc.consumer.service;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public interface TeleTypeService {

    public void processMessage(String message) throws JAXBException, IOException;

}
