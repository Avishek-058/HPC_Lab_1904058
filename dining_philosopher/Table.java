package dining_philosopher;

import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.List;

public class Table {
    private final int tableNumber;
    private final ReentrantLock[] forks;
    private Philosopher[] philosophers;
    private final List<Philosopher> movedToSixthTable = new ArrayList<>();

    public Table(int tableNumber, ReentrantLock[] forks) {
        this.tableNumber = tableNumber;
        this.forks = forks;
    }

    public boolean isDeadlocked() {
        // Check for deadlock: all philosophers at the table are trying to eat
        if (tableNumber < 5) {
            boolean allWaiting = true;
            for (Philosopher philosopher : philosophers) {
                if (!philosopher.isDeadlocked()) {
                    allWaiting = false;
                    break;
                }
            }
            return allWaiting;
        }
        return movedToSixthTable.size() >= 5; // For the sixth table
    }

    public void movePhilosopherToSixthTable(Philosopher philosopher) {
        movedToSixthTable.add(philosopher);
        System.out.println("Philosopher " + philosopher.getPhilosopherId() + " moved to the sixth table.");
        philosopher.releaseForks(); // Ensure forks are released before moving
    }

    public Philosopher getLastMovedPhilosopher() {
        return movedToSixthTable.get(movedToSixthTable.size() - 1);
    }

    public void setPhilosophers(Philosopher[] philosophers) {
        this.philosophers = philosophers;
    }
}