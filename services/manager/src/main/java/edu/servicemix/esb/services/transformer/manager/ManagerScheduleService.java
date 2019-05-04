package edu.servicemix.esb.services.transformer.manager;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import java.util.Arrays;

public class ManagerScheduleService {
    private Logger logger = Logger.getLogger(ManagerScheduleService.class);

    private static ThreadArray workerThreads;
    private String exchangeId;

    public ManagerScheduleService() {
        workerThreads = new ThreadArray();
    }

    public static void interruptWorkingThreads() {
        ThreadArray.interruptAll();
    }

    public void doCall(Exchange exchange) {
        exchangeId = exchange.getExchangeId();
        String eventId = exchange.getIn().getHeader("eventId", String.class);
        String operationName = exchange.getIn().getHeader("operationName", String.class);

        logger.info("ManagerRestService: 'doCall' with " + operationName + " is processing | exId=" + exchangeId);

        if (eventId == null || operationName == null) {
            logger.error("ManagerRestService: Headers 'eventId'="+ eventId +
                    " and 'operationName'=" + operationName + " is required | exId=" + exchangeId);
            return;
        }
        String newThreadName = "Event" + eventId;
        Thread currentThread = workerThreads.get(newThreadName);

        switch (operationName) {
            case "stop":
                doStop(currentThread, newThreadName);
                break;
            case "start":
                doStart(currentThread, newThreadName);
                break;
            default:
                logger.error("ManagerRestService: operation '" + operationName + "' is not supported | exId=" + exchangeId);
                break;
        }
        logger.info("ManagerRestService: 'doCall' with '" + operationName + "' is finished | exId=" + exchangeId);
    }

    private void doStop(Thread currentThread, String newThreadName) {
        if (currentThread == null) {
            logger.error("ManagerRestService: Thread " + newThreadName + " is not running | exId=" + exchangeId);
            return;
        }
        workerThreads.remove(currentThread);
        logger.warn("ManagerRestService: Thread " + newThreadName + " is stopped | exId=" + exchangeId);
    }

    private void doStart(Thread currentThread, String newThreadName) {
        if (currentThread != null) {
            logger.error("ManagerRestService: Thread " + newThreadName + " is already started | exId=" + exchangeId);
            return;
        }
        MainWorker workerThread = new MainWorker();
        workerThread.setName(newThreadName);
        workerThreads.add(workerThread);
        logger.info("ManagerRestService: Thread " + newThreadName + " is started | exId=" + exchangeId);
    }

    private class MainWorker extends Thread {
        @Override
        public void run() {
            logger.info("ManagerRestService: Thread " + this.getName() + " is started | exId=" + exchangeId);
            try {
                for (int i = 0; i < 10; i++) {
                    logger.info("ManagerRestService: Thread " + this.getName() + " await " + i + " | exId=" + exchangeId);
                    Thread.sleep(10000);
                }
            } catch (InterruptedException e) {
                logger.warn("ManagerRestService: Thread " + this.getName() + " is interrupted | exId=" + exchangeId);
            }
            workerThreads.remove(this);
            logger.info("ManagerRestService: Thread " + this.getName() + " is finished | exId=" + exchangeId);
        }
    }

    private static class ThreadArray {
        private static Thread[] threads;
        private static int length;

        ThreadArray() {
            threads = new Thread[0];
        }

        void add(Thread thread) {
            if (this.getIndex(thread) >= 0) {
                return;
            }
            Thread[] newThreads = Arrays.copyOf(threads, ++length);
            newThreads[length - 1] = thread;
            threads = newThreads;
            thread.start();
        }

        void remove(Thread thread) {
            int removedIdx = this.getIndex(thread);
            if (removedIdx < 0) {
                return;
            }
            Thread removedThread = threads[removedIdx];

            if (removedThread != null && removedThread.getState().compareTo(Thread.State.TERMINATED) != 0) {
                removedThread.interrupt();
            }
            Thread[] newTreads = new Thread[--length];

            for (int i = 0, j = 0; i < length; j++) {
                if (threads[j].getName().equals(thread.getName())) {
                    continue;
                }
                newTreads[i++] = threads[j];
            }
            threads = newTreads;
        }

        int getIndex(Thread thread) {
            for (int i = 0; i < length; i++) {
                if (threads[i].getName().equals(thread.getName()))
                    return i;
            }
            return -1;
        }

        Thread get(String threadName) {
            for (int i = 0; i < length; i++) {
                if (threads[i].getName().equals(threadName))
                    return threads[i];
            }
            return null;
        }

        static void interruptAll() {
            for (Thread thread : threads)
                thread.interrupt();
        }
    }
}
