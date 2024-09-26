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
                Thread.sleep(4000); // Simulate waiting for 4 seconds before trying to pick the right fork
                pickUpRightFork();
                eat();
                putDownForks();
                
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

    private void pickUpRightFork() {
        if (rightFork.tryLock()) {
            System.out.println("Philosopher " + id + " picked up right fork.");
        } else {
            System.out.println("Philosopher " + id + " couldn't pick up right fork. Waiting...");
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

    // Add this method to return the philosopher's ID
    public int getPhilosopherId() {
        return id;
    }
}
