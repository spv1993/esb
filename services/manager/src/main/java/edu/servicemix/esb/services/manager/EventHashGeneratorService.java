package edu.servicemix.esb.services.manager;

import edu.servicemix.esb.commons.schedule.ArrayOfHashes;
import edu.servicemix.esb.commons.schedule.EventHashes;
import edu.servicemix.esb.commons.schedule.GetHashesResponse;
import org.apache.camel.Exchange;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.*;

public class EventHashGeneratorService {
    private Logger logger = Logger.getLogger(EventHashGeneratorService.class);

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final Map<String, String> hashes = new HashMap<>();
    private final GetHashesResponse hashesResponse = new GetHashesResponse();
    private int awaitTimeoutSeconds = 60;

    public EventHashGeneratorService() {
        super();
    }

    public void setAwaitTimeoutSeconds(int awaitTimeoutSeconds) {
        this.awaitTimeoutSeconds = awaitTimeoutSeconds;
    }

    public void doAsyncCall(Exchange exchange) {
        doCall(exchange);
    }

    public void doSyncCall(Exchange exchange) throws InterruptedException, ExecutionException, TimeoutException {
        Future<String> response = doCall(exchange);
        if (response != null) {
            String responseHash = response.get(awaitTimeoutSeconds, TimeUnit.SECONDS);
            exchange.getIn().setBody(responseHash);
        }
    }

    public GetHashesResponse getHashes() {
        return hashesResponse;
    }

    private Future<String> doCall(Exchange exchange) {
        String exchangeId = exchange.getExchangeId();
        String eventId = exchange.getIn().getHeader("eventId", String.class);
        String operationName = exchange.getIn().getHeader("operationName", String.class);

        logger.info("ManagerRestService: 'doCall' with " + operationName + " is processing | exId=" + exchangeId);
        if (eventId == null || operationName == null) {
            logger.error("ManagerRestService: Headers 'eventId'="+ eventId +
                    " and 'operationName'=" + operationName + " is required | exId=" + exchangeId);
            exchange.setException(new IllegalArgumentException("Missing required parameter 'eventId'="+ eventId +
                    " and 'operationName'=" + operationName));
            return null;
        }
        Future<String> response = executorService.submit(new MainWorker(eventId, exchangeId));
        logger.info("ManagerRestService: 'doCall' with '" + operationName + "' is finished | exId=" + exchangeId);
        return response;
    }

    private void putHash(String eventId, String hash) {
        boolean insertedHash = false;
        List<EventHashes> eventHashesList = hashesResponse.getEventHashes();
        for (EventHashes eventHashes : eventHashesList) {
            if (eventHashes.getEventId().equals(eventId)) {
                eventHashes.getArrayOfHashes().getHash().add(hash);
                eventHashes.setHashCount(eventHashes.getHashCount().add(BigInteger.ONE));
                insertedHash = true;
                break;
            }
        }
        if (!insertedHash) {
            ArrayOfHashes arrayOfHashes = new ArrayOfHashes();
            arrayOfHashes.getHash().add(hash);

            EventHashes newEventHashes = new EventHashes();
            newEventHashes.setEventId(eventId);
            newEventHashes.setHashCount(BigInteger.ONE);
            newEventHashes.setArrayOfHashes(arrayOfHashes);
            eventHashesList.add(newEventHashes);
        }
    }

    private class MainWorker implements Callable<String> {
        private String eventId;
        private String exchangeId;

        MainWorker(String eventId, String exchangeId) {
            this.eventId = eventId;
            this.exchangeId = exchangeId;
        }

        @Override
        public String call() {
            logger.info("ManagerRestService: EventId='" + eventId + "' start generating UUID | exId=" + exchangeId);
            String uuid;
            try {
                synchronized (hashes) {
                    while (true) {
                        uuid = UUID.randomUUID().toString();
                        if (!hashes.containsKey(uuid)) {
                            hashes.put(uuid, eventId);
                            putHash(eventId, uuid);
                            break;
                        }
                    }
                }
            } catch (Exception ex) {
                logger.error("ManagerRestService: throws exception: " + ex + " EventId='" + eventId + " | exId=" + exchangeId);
                return ex.getMessage();
            }
            logger.info("ManagerRestService: 'Insert' EventId='" + eventId + "' finished generating UUID: " + uuid + " | exId=" + exchangeId);
            return uuid;
        }
    }
}
