package cat.pkg3l.advanced.programming;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pablo
 */
public class Slide extends Activity {

    private final int min_age;
    private final int max_age;
    private int slide_uses_number;

    public Slide(int min_age, int max_age, int capacity, Queue waitingQueue) {
        super(capacity, waitingQueue);
        this.min_age = min_age;
        this.max_age = max_age;
    }

    public int getSlide_uses_number() {
        return slide_uses_number;
    }

    public void setSlide_uses_number() {
        this.slide_uses_number++;
    }

    @Override
    public boolean checking(User u) {
        try {
            Thread.sleep((int) ((Math.random() * 100) + 400));
        } catch (InterruptedException ex) {
            Logger.getLogger(Slide.class.getName()).log(Level.SEVERE, null, ex);
        }
        if ((u.getAge() >= this.min_age) && (u.getAge() <= this.max_age)) {
            return true;
        }
        return false;
    }

}
