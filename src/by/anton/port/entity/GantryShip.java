package by.anton.port.entity;

import by.anton.port.exception.PortException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
;
import java.util.concurrent.TimeUnit;

public class GantryShip implements Runnable {
    private static final String OCCUPIED_MESSAGE = "Pier occupied";
    private static final String PROCESSING_MESSAGE = "Ship is loading";
    private static final String LOADED_MESSAGE = "Has been loaded";
    private static final Logger logger = LogManager.getLogger();
    private ShipState state;

    public GantryShip() {
        state = ShipState.FREE;
    }

    @Override
    public void run() {
        Port port = Port.getInstance();
        try {
            if (portCreated(port)) {
                throw new PortException();
            }
        } catch (PortException e) {
            logger.log(Level.ERROR, "Port hasn't been created");
        }
        Pier pier = port.takePier();
        shipWait();
        logger.log(Level.INFO, OCCUPIED_MESSAGE);
        shipWait();
        load(pier);
        port.releasePier(pier);
        shipWait();
        logger.log(Level.INFO, LOADED_MESSAGE);
        state = ShipState.LOADED;

    }

    private void load(Pier pier) {
        state = ShipState.PROCESSING;
        logger.log(Level.INFO, PROCESSING_MESSAGE);

    }

    private void shipWait() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean portCreated(Port port) {
        return port == null;
    }
}
