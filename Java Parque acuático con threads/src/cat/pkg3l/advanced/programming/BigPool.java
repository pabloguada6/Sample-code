package cat.pkg3l.advanced.programming;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class BigPool extends Activity {

    private final Slide slideA;
    private final Slide slideB;
    private final Slide slideC;

    public BigPool(Queue slideA, Queue slideB, Queue slideC, int capacity, Queue waitingQueue) {
        super(capacity, waitingQueue);
        this.slideA = new Slide(11, 14, 1, slideA);
        this.slideB = new Slide(15, 17, 1, slideB);
        this.slideC = new Slide(18, 50, 1, slideC);
    }

    @Override
    public boolean checking(User u) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(BigPool.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (u.getAge() <= 10) {
            while (super.getCapacity_control().availablePermits() < 2) {
                this.expel();
            }
        } else {
            if (super.getCapacity_control().availablePermits() < 1) {
                this.expel();
            }
        }
        return true;
    }

    public void expel() {
        Person random_user;
        Queue big_pool_inside = super.getSupervisor().getWp().getBigPoolInside();
        random_user = (Person) big_pool_inside.getList().get((int) ((Math.random() * big_pool_inside.getList().size()) - 1));
        try {
            if (random_user.getAge() <= 10 || random_user instanceof Accompanying) {
                Child_under10 child2;
                if (random_user instanceof Accompanying) {
                    Accompanying acc = (Accompanying) random_user;
                    child2 = (Child_under10) acc.getChild();
                } else {
                    child2 = (Child_under10) random_user;
                }
                big_pool_inside.pop(child2.getAcc());
                Thread.sleep((int) ((Math.random() * 500) + 500));
                big_pool_inside.pop(child2);
                Thread.sleep((int) ((Math.random() * 500) + 500));
                super.getCapacity_control().release(2);
            } else {
                big_pool_inside.pop(random_user);
                Thread.sleep((int) ((Math.random() * 500) + 500));
                super.getCapacity_control().release();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(BigPool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Slide getSlideA() {
        return slideA;
    }

    public Slide getSlideB() {
        return slideB;
    }

    public Slide getSlideC() {
        return slideC;
    }

}
