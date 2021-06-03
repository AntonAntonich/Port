package by.anton.port.entity;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Queue;

public class Test {
    public static void main(String[] args) {
        Runnable ship = new GantryShip();
        for (int i = 0; i < 10; i++) {
            new Thread(ship).start();
        }
    }
}
