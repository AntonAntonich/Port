package by.anton.port.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Port {
    private static final Logger logger = LogManager.getLogger();
    private static final int MAX_PIERS_NUMBER = 10;
    private static Port port;
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();
    private static AtomicBoolean isCreated = new AtomicBoolean(false);
    //private int allowablePiersAmount = 10;
    private Deque<Pier> freePiers;
    private Deque<Pier> occupiedPiers;

    private Port(){
        // reading max value of piers
        // compare with default
        init();
    }

    private void init() {
        freePiers = new ArrayDeque<>();
        occupiedPiers = new ArrayDeque<>();

        fillPort();
    }

    private void fillPort(){
        for (int i = 0; i < MAX_PIERS_NUMBER; i++) {
            freePiers.add(new Pier());
        }
    }


    public static Port getInstance(){
        if(!isCreated.get()) {
            try {
                lock.lock();
                if (port == null) {
                    port = new Port();
                    isCreated.set(true);
                }
            } finally {
                lock.unlock();
            }
        }
        return port;
    }

    public Pier takePier(){
        Pier pier = null;
        try{
            lock.lock();
            while(freePiers.isEmpty()){
                condition.await();
            }
                pier = freePiers.poll();
                occupiedPiers.addFirst(pier);

        } catch (InterruptedException e) {
            logger.log(Level.ERROR, "Thread has been interrupted in \"taking piers\"" + Thread.currentThread().getName());
        } finally {
            lock.unlock();
        }
        return pier;
    }

    public void releasePier(Pier pier){
        try{
            lock.lock();
            while(occupiedPiers.isEmpty()){
                condition.await();
            }
            occupiedPiers.remove(pier);
            freePiers.addFirst(pier);
        } catch (InterruptedException e) {
            logger.log(Level.ERROR, "Thread has been interrupted in \"realising piers\"" + Thread.currentThread().getName());
        } finally {
            lock.unlock();
        }
    }
}
