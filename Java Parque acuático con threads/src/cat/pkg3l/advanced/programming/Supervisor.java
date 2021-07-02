package cat.pkg3l.advanced.programming;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextField;

/**
 *
 * @author pablo
 */
public class Supervisor extends Person {

    private final Activity activity;
    private boolean checked;
    private final ArrayList<User> users_waiting_checking;
    private boolean checking_result;
    private final JTextField tf_supervisor;
    private final Lock lock = new ReentrantLock();
    private final Condition waiting = lock.newCondition();

    public Supervisor(String id, int age, WaterPark wp, Activity activity, JTextField tf) {
        super(id, age, wp);
        this.checked = false;
        this.users_waiting_checking = new ArrayList<>();
        this.tf_supervisor = tf;
        this.tf_supervisor.setText(this.getName());
        this.activity = activity;
    }

    public synchronized boolean checking(User p) {
        this.users_waiting_checking.add(p);
        if (p.getAge() <= 10) {
            Child_under10 child = (Child_under10) p;
            this.activity.getWaitingQueue().push(child.getAcc());
        }
        this.activity.getWaitingQueue().push(p);
        while (!p.equals(this.users_waiting_checking.get(0))) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Supervisor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.checked = false;
        while ((!checked)) {
            try {
                this.svUserArrive();
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(Supervisor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.checked = false;
        this.users_waiting_checking.remove(p);
        if (this.users_waiting_checking.size() > 0) {
            notifyAll();
        }
        if (!this.checking_result) {
            if (p.getAge() <= 10) {
                Child_under10 child = (Child_under10) p;
                this.activity.getWaitingQueue().pop(child.getAcc());
            }
            this.activity.getWaitingQueue().pop(p);
        }
        return this.checking_result;
    }

    public synchronized void checking_finished() {
        this.checking_result = activity.checking(this.users_waiting_checking.get(0));
        System.out.println(this.users_waiting_checking.get(0).getName() + this.activity);
        super.getWp().tryStop();
        this.checked = true;
        this.notifyAll();
    }

    public void svWait() {
        try {
            lock.lock();
            try {
                waiting.await();
            } catch (InterruptedException ex) {
                Logger.getLogger(Supervisor.class.getName()).log(Level.SEVERE, null, ex);
            }
        } finally {
            lock.unlock();
        }
    }
    
    public void svUserArrive() {
        try {
            lock.lock();
            waiting.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void run() {
        while (true) {
            this.svWait();      
            super.getWp().tryStop();
            this.checking_finished();
        }
    }
}
