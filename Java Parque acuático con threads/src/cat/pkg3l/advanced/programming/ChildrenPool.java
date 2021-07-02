package cat.pkg3l.advanced.programming;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class ChildrenPool extends Activity {

    public ChildrenPool(int capacity, Queue waitingQueue) {
        super(capacity, waitingQueue);
    }

    @Override
    public int calculateUsersNumber() {
        int users_number = super.getWaitingQueue().getList().size()
                + (super.getCapacity() - super.getCapacity_control().availablePermits())
                + super.getSupervisor().getWp().getChildrenPoolAdultsWaiting().getList().size();
        return users_number;
    }

    @Override
    public boolean checking(User u) {
        try {
            Thread.sleep((int) ((Math.random() * 1000) + 500));
        } catch (InterruptedException ex) {
            Logger.getLogger(ChildrenPool.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (u.getAge() > 10) {
            return false;
        }
        return true;
    }

}
