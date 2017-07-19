package com.mesaeva.viktorines.servlet;

import com.mesaeva.viktorines.domain.entities.CurrentVikt;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This class will run daemon thread for each viktorine
 * than will remove it from ViktorineServlet fields automatically
 * after 30 min for complete viktorine and 3 hours for incomplete.
 */
class ViktorinesCleaner {
    private Map<Integer, Boolean> complete = new HashMap<>();

    void completed(Integer i) {
        complete.put(i, true);
    }

    Logger logger = Logger.getLogger(ViktorinesCleaner.class);

    public void add(Integer i) {
        complete.put(i, false);
        new Cleaner(180, i);
    }

    private class Cleaner implements Runnable {
        private long time;
        private int id;

        private Cleaner(long time, int id) {
            this.time = time;
            this.id = id;
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.start();
        }

        @Override
        public void run() {
            try {
                long timeLoc = 0;
                while (timeLoc < this.time) {
                    TimeUnit.MINUTES.sleep(10);
                    timeLoc += 10;
                    if (complete.get(id)) {
                        complete.remove(id);
                        this.time = 30;
                        timeLoc = 0;
                    }
                }

                if (ViktorineServlet.ansvers.keySet().contains(id)) {
                    Iterator iterator = ViktorineServlet.current.iterator();
                    while (iterator.hasNext()) {
                        CurrentVikt c = (CurrentVikt) iterator.next();
                        if (c.getViktId() == id) {
                            iterator.remove();
                            break;
                        }
                    }
                    ViktorineServlet.ansvers.remove(id);
                    ViktorineServlet.trueAnswersCount.remove(id);
                    ViktorineServlet.completed.remove(id);
                }
            } catch (InterruptedException e) {
                logger.error(e);
                Thread.currentThread().interrupt();
            }
        }
    }
}
