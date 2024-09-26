package dining_philosopher;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ThreadLocalRandom;

public class Philosopher extends Thread {
    private final int id;
    private final ReentrantLock leftFork;
    private final ReentrantLock rightFork;
    private final Table table;

    public Philosopher(int id, ReentrantLock leftFork, ReentrantLock rightFork, Table table) {
        this.id = id;
        this.leftFork = leftFork;
        this.rightFork = rightFork;
        this.table = table;
    }

    public void run() {
        try {
            while (true) {
                think();
                pickUpLeftFork();
                
                // Retry to pick up the right fork a limited number of times
                int attempts = 0;
                while (attempts < 5) { // Limit the number of attempts
                    if (pickUpRightFork()) {
                        eat();
                        putDownForks();
                        break; // Exit the loop after eating
                    }
                    attempts++;
                    Thread.sleep(100); // Wait before retrying to pick up the right fork
                }

                // If the table is deadlocked and the philosopher moves to the sixth table, break the loop
                if (table.isDeadlocked()) {
                    table.movePhilosopherToSixthTable(this);
                    break; // Philosopher should stop after moving to the sixth table
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void think() throws InterruptedException {
        System.out.println("Philosopher " + id + " is thinking.");
        Thread.sleep(ThreadLocalRandom.current().nextInt(0, 10000));
    }

    private void pickUpLeftFork() {
        leftFork.lock();
        System.out.println("Philosopher " + id + " picked up left fork.");
    }

    private boolean pickUpRightFork() {
        if (rightFork.tryLock()) {
            System.out.println("Philosopher " + id + " picked up right fork.");
            return true; // Successfully picked up the right fork
        } else {
            System.out.println("Philosopher " + id + " couldn't pick up right fork. Waiting...");
            return false; // Failed to pick up the right fork
        }
    }

    private void eat() throws InterruptedException {
        if (leftFork.isHeldByCurrentThread() && rightFork.isHeldByCurrentThread()) {
            System.out.println("Philosopher " + id + " is eating.");
            Thread.sleep(ThreadLocalRandom.current().nextInt(0, 5000));
        }
    }

    private void putDownForks() {
        if (leftFork.isHeldByCurrentThread()) {
            leftFork.unlock();
            System.out.println("Philosopher " + id + " put down left fork.");
        }
        if (rightFork.isHeldByCurrentThread()) {
            rightFork.unlock();
            System.out.println("Philosopher " + id + " put down right fork.");
        }
    }

    public void releaseForks() {
        putDownForks();
    }

    public boolean isDeadlocked() {
        return !rightFork.isHeldByCurrentThread() && leftFork.isHeldByCurrentThread();
    }

    public int getPhilosopherId() {
        return id;
    }
}
