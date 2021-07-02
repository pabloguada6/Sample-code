package cat.pkg3l.advanced.programming;

import java.util.concurrent.Semaphore;

/**
 *
 * @author pablo
 */
public abstract class Activity {

    private final Semaphore capacity_control;
    private Supervisor supervisor;
    private final Queue waitingQueue;
    private int capacity;

    public Activity(int capacity, Queue waitingQueue) {
        this.capacity_control = new Semaphore(capacity, true);
        this.waitingQueue = waitingQueue;
        this.capacity = capacity;
    }

    public int calculateUsersNumber() {
        int users_number = this.waitingQueue.getList().size() + (this.capacity - this.capacity_control.availablePermits());
        return users_number;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Semaphore getCapacity_control() {
        return capacity_control;
    }

    public Supervisor getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(Supervisor supervisor) {
        this.supervisor = supervisor;
    }

    public Queue getWaitingQueue() {
        return waitingQueue;
    }

    public abstract boolean checking(User u);

}
