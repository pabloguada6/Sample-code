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
public class WavePool extends Activity {

    private int time_inside;
    private int count_users;
    private final Lock lock;
    private final Condition checking_queue;
    private final Condition pair;

    public WavePool(int capacity, Queue waitingQueue) {
        super(capacity, waitingQueue);
        this.time_inside = 0;
        lock = new ReentrantLock();
        checking_queue = lock.newCondition();
        pair = lock.newCondition();
    }

    public int getTime_inside() {
        return time_inside;
    }

    public boolean inPairs(User u) {
        boolean user_pair_checked = false;
        try {
            super.getWaitingQueue().push(u);
            lock.lock();
            while (this.count_users > 1) {
                checking_queue.await();
            }
            if (this.count_users == 0) {
                this.count_users = 1;
                checking_queue.signal();
                try {
                    pair.await();
                    this.count_users = 0;
                    checking_queue.signal();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Supervisor.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                user_pair_checked = false;
            } else {
                this.count_users = 2;
                user_pair_checked = true;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(WaterPark.class
                    .getName()).log(Level.SEVERE, null, ex);
        } finally {
            lock.unlock();
        }
        return user_pair_checked;
    }

    public void signal_to_pair() {
        try {
            lock.lock();
            pair.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean checking(User u) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(WavePool.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (u.getAge() <= 5) {
            return false;
        }
        this.time_inside = (int) ((Math.random() * 3000) + 2000);
        return true;
    }
}
