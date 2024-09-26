package dining_philosopher;

import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
    public static void main(String[] args) {
        int numTables = 6;
        int philosophersPerTable = 5;

        Table[] tables = new Table[numTables];
        Philosopher[][] philosophers = new Philosopher[numTables][philosophersPerTable];

        // Initialize tables and philosophers
        for (int i = 0; i < numTables; i++) {
            ReentrantLock[] forks = new ReentrantLock[philosophersPerTable];
            for (int j = 0; j < philosophersPerTable; j++) {
                forks[j] = new ReentrantLock();
            }
            tables[i] = new Table(i, forks);

            // Initialize philosophers
            for (int j = 0; j < philosophersPerTable; j++) {
                ReentrantLock leftFork = forks[j];
                ReentrantLock rightFork = forks[(j + 1) % philosophersPerTable];
                philosophers[i][j] = new Philosopher(j, leftFork, rightFork, tables[i]);
            }
            tables[i].setPhilosophers(philosophers[i]);
        }

        // Start philosopher threads for all tables except the sixth one
        for (int i = 0; i < numTables - 1; i++) {
            for (Philosopher philosopher : philosophers[i]) {
                philosopher.start();
            }
        }

        // Monitor the sixth table for deadlock and termination
        while (!tables[numTables - 1].isDeadlocked()) {
            if (tables[numTables - 1].isDeadlocked()) {
                break; // Exit the loop if deadlock is detected
            }

            // Sleep for a short duration to allow other threads to proceed
            try {
                Thread.sleep(100); // Adjust the sleep time as necessary
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
        }

        System.out.println("Sixth table entered deadlock.");
        System.out.println("Philosopher " + tables[numTables - 1].getLastMovedPhilosopher().getPhilosopherId() 
                           + " was the last to move before deadlock.");
    }
}
