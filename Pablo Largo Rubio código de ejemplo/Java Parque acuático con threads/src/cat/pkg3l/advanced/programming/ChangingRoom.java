package cat.pkg3l.advanced.programming;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class ChangingRoom extends Activity {

    private final Semaphore numChildren;

    public ChangingRoom(int numChildren, int capacity, Queue waitingQueue) {
        super(capacity, waitingQueue);
        this.numChildren = new Semaphore(numChildren);
        super.setCapacity(super.getCapacity() + numChildren);
    }

    @Override
    public int calculateUsersNumber() {
        int users_number = super.getWaitingQueue().getList().size()
                + (super.getCapacity() - (super.getCapacity_control().availablePermits() 
                + this.numChildren.availablePermits()));
        return users_number;
    }

    @Override
    public boolean checking(User u) {
        try {
            Thread.sleep(1000);
            if (u.getAge() <= 10) {
                Child_under10 child = (Child_under10) u;
                if (child.getAcc().getAge() < 18) {
                    return false;
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(ChangingRoom.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public Semaphore getNumChildren() {
        return numChildren;
    }

}
