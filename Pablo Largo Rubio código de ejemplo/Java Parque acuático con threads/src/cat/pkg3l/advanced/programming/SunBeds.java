package cat.pkg3l.advanced.programming;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class SunBeds extends Activity {

    private final Lock lock;
    private final Condition sun_bed_free;

    public SunBeds(int capacity, Queue waitingQueue) {
        super(capacity, waitingQueue);
        this.lock = new ReentrantLock();
        this.sun_bed_free = lock.newCondition();
    }

    public void race(User u) {
        try {
            lock.lock();
            if (super.getWaitingQueue().getList().contains(u)) {
                while (!super.getCapacity_control().tryAcquire()) {
                    this.sun_bed_free.await();
                }
            } else {
                super.getCapacity_control().release();
                this.sun_bed_free.signalAll();

            }

        } catch (InterruptedException ex) {
            Logger.getLogger(WaterPark.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean checking(User u) {
        try {
            Thread.sleep((int) ((Math.random() * 400) + 500));
        } catch (InterruptedException ex) {
            Logger.getLogger(SunBeds.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (u.getAge() < 15) {
            return false;
        }
        return true;
    }

}
