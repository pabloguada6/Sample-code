package cat.pkg3l.advanced.programming;

import java.util.ArrayList;
import javax.swing.JTextField;

/**
 *
 * @author pablo
 */
public class Queue {

    private final ArrayList<Thread> list;
    private final JTextField tf;

    @SuppressWarnings("Convert2Diamond")
    public Queue(JTextField tf) {
        this.list = new ArrayList<Thread>();
        this.tf = tf;
    }

    public synchronized void push(Thread t) {
        if (!this.list.contains(t)) {
            this.list.add(t);
            print();
        }

    }

    public synchronized void pop(Thread t) {
        if (this.list.contains(t)) {
            this.list.remove(t);
            print();
        }
    }

    public void print() {
        String content = "";
        for (int i = 0; i < this.list.size(); i++) {
            content = content + this.list.get(i).getName() + ", ";
        }
        this.tf.setText(content);
    }

    public ArrayList<Thread> getList() {
        return list;
    }

}
