package cat.pkg3l.advanced.programming;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
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
public class WaterPark {

    private final Semaphore park_entry;
    private final Queue entryWaitingQueue;
    private final Queue changingRoomWaiting;
    private final Queue changingRoomInside;
    private final Queue wavePoolWaiting;
    private final Queue wavePoolInside;
    private final Queue sunBedsWaiting;
    private final Queue sunBedsInside;
    private final Queue childrenPoolWaiting;
    private final Queue childrenPoolInside;
    private final Queue childrenPoolAdultsWaiting;
    private final Queue bigPoolWaiting;
    private final Queue bigPoolInside;
    private final Queue slide_A_waiting;
    private final Queue slide_A_inside;
    private final Queue slide_B_waiting;
    private final Queue slide_B_inside;
    private final Queue slide_C_waiting;
    private final Queue slide_C_inside;
    private final ChangingRoom changing_room;
    private final WavePool wave_pool;
    private final SunBeds sun_beds;
    private final ChildrenPool children_pool;
    private final BigPool big_pool;
    private final Lock lock;
    private final Condition stop;
    private boolean waiting;
    private int minors_number;
    private final ArrayList<User> users;
    private int users_id;

    public WaterPark(JTextField tf1, JTextField tf2, JTextField tf3, JTextField tf4, JTextField tf5, JTextField tf6, JTextField tf7,
            JTextField tf8, JTextField tf9, JTextField tf10, JTextField tf11, JTextField tf12, JTextField tf13, JTextField tf14, JTextField tf15,
            JTextField slideA, JTextField slideB, JTextField slideC) {
        this.park_entry = new Semaphore(100, true);
        this.entryWaitingQueue = new Queue(tf1);
        this.changingRoomWaiting = new Queue(tf2);
        this.changingRoomInside = new Queue(tf3);
        this.wavePoolWaiting = new Queue(tf4);
        this.wavePoolInside = new Queue(tf5);
        this.sunBedsWaiting = new Queue(tf6);
        this.sunBedsInside = new Queue(tf7);
        this.childrenPoolWaiting = new Queue(tf8);
        this.childrenPoolInside = new Queue(tf9);
        this.childrenPoolAdultsWaiting = new Queue(tf10);
        this.bigPoolWaiting = new Queue(tf11);
        this.bigPoolInside = new Queue(tf12);
        this.slide_A_waiting = new Queue(tf13);
        this.slide_B_waiting = new Queue(tf14);
        this.slide_C_waiting = new Queue(tf15);
        this.slide_A_inside = new Queue(slideA);
        this.slide_B_inside = new Queue(slideB);
        this.slide_C_inside = new Queue(slideC);
        this.changing_room = new ChangingRoom(10, 20, changingRoomWaiting);
        this.wave_pool = new WavePool(20, this.wavePoolWaiting);
        this.sun_beds = new SunBeds(20, this.sunBedsWaiting);
        this.children_pool = new ChildrenPool(15, this.childrenPoolWaiting);
        this.big_pool = new BigPool(this.slide_A_waiting, this.slide_B_waiting, this.slide_C_waiting, 50, this.bigPoolWaiting);
        this.lock = new ReentrantLock();
        this.stop = lock.newCondition();
        this.waiting = false;
        this.minors_number = 0;
        this.users = new ArrayList<>();
        this.users_id = 7;
    }

    public void enter(User u) {
        u.setActivity("Entry waiting queue");
        try {
            if (u.getAge() <= 10) {
                Child_under10 child = (Child_under10) u;
                this.entryWaitingQueue.push(child.getAcc());
                this.entryWaitingQueue.push(child);
                park_entry.acquire(2);
                this.entryWaitingQueue.pop(child.getAcc());
                this.entryWaitingQueue.pop(child);
                this.minors_number++;
            } else {
                this.entryWaitingQueue.push(u);
                park_entry.acquire();
                this.entryWaitingQueue.pop(u);
                if (u.getAge() < 18) {
                    this.minors_number++;
                }
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(WaterPark.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void changingRoom(User u) {
        u.setActivity("Changing room");
        this.tryStop();
        if (changing_room.getSupervisor().checking(u)) {          
            if (u.getAge() <= 10) {
                Child_under10 child = (Child_under10) u;
                this.activity_process(changingRoomWaiting, changingRoomInside, 6000, this.changing_room.getNumChildren(), child.getAcc(), child);
            } else {
                if (u.getAge() < 18) {
                    this.activity_process(changingRoomWaiting, changingRoomInside, 3000, this.changing_room.getNumChildren(), u);
                } else {
                    this.activity_process(changingRoomWaiting, changingRoomInside, 3000, this.changing_room.getCapacity_control(), u);
                }
            }
        }
    }

    public void wavePool(User u) {
        u.setActivity("Wave pool");
        this.tryStop();
        if (u.getAge() <= 10) {
            if (this.wave_pool.getSupervisor().checking(u)) {
                Child_under10 child = (Child_under10) u;
                this.activity_process(this.wavePoolWaiting, this.wavePoolInside, (int) ((Math.random() * 3000) + 2000), this.wave_pool.getCapacity_control(), child.getAcc(), child);
                u.setCount_activities();
            }
        } else {
            if (this.wave_pool.inPairs(u)) {
                this.wave_pool.getSupervisor().checking(u);
                this.wave_pool.signal_to_pair();
            }
            this.activity_process(this.wavePoolWaiting, this.wavePoolInside, this.wave_pool.getTime_inside(), this.wave_pool.getCapacity_control(), u);
            u.setCount_activities();
        }
    }

    public void childrenPool(User u) {
        u.setActivity("Children Pool");
        this.tryStop();
        if (this.children_pool.getSupervisor().checking(u)) {
            Child_under10 child = (Child_under10) u;
            if (u.getAge() <= 5) {
                this.activity_process(childrenPoolWaiting, childrenPoolInside, (int) ((Math.random() * 2000) + 1000), this.children_pool.getCapacity_control(), child.getAcc(), child);
                u.setCount_activities();
            } else {
                try {
                    this.children_pool.getCapacity_control().acquire();
                } catch (InterruptedException ex) {
                    Logger.getLogger(WaterPark.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                this.childrenPoolWaiting.pop(child);
                this.childrenPoolWaiting.pop(child.getAcc());
                this.childrenPoolAdultsWaiting.push(child.getAcc());
                this.childrenPoolInside.push(child);
                try {
                    Thread.sleep((int) ((Math.random() * 2000) + 1000));

                } catch (InterruptedException ex) {
                    Logger.getLogger(WaterPark.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                this.tryStop();
                this.childrenPoolInside.pop(child);
                this.childrenPoolAdultsWaiting.pop(child.getAcc());
                this.children_pool.getCapacity_control().release();
                u.setCount_activities();
            }
        }
    }

    public void sunBeds(User user) {
        user.setActivity("Sun beds");
        this.tryStop();
        if (this.sun_beds.getSupervisor().checking(user)) {
            try {
                this.sun_beds.race(user);
                this.sunBedsWaiting.pop(user);
                this.sunBedsInside.push(user);
                Thread.sleep((int) ((Math.random() * 2000) + 2000));
                this.tryStop();
                this.sunBedsInside.pop(user);
                this.sun_beds.race(user);
                user.setCount_activities();
            } catch (InterruptedException ex) {
                Logger.getLogger(WaterPark.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void bigPool(User u) {
        u.setActivity("Big Pool");
        this.tryStop();
        if (u.getAge() <= 10) {
            Child_under10 child = (Child_under10) u;
            this.big_pool.getSupervisor().checking(child);
            this.activity_process(bigPoolWaiting, bigPoolInside, (int) ((Math.random() * 2000) + 3000), this.big_pool.getCapacity_control(), child.getAcc(), child);
        } else {
            this.big_pool.getSupervisor().checking(u);
            this.activity_process(bigPoolWaiting, bigPoolInside, (int) ((Math.random() * 2000) + 3000), this.big_pool.getCapacity_control(), u);
        }
        u.setCount_activities();
    }

    public void slide(Slide slide, User u, Queue slideWaiting, Queue slideInside, String slide_name) {
        u.setActivity(slide_name);
        this.tryStop();
        try {
            if (slide.getSupervisor().checking(u)) {
                slide.getCapacity_control().acquire();
                this.big_pool.getCapacity_control().acquire();
                slideWaiting.pop(u);
                slideInside.push(u);
                Thread.sleep((int) (Math.random() * 1000) + 2000);
                slide.setSlide_uses_number();
                this.tryStop();
                slide.getCapacity_control().release();
                slideInside.pop(u);
                u.setCount_activities();
                bigPoolInside.push(u);
                Thread.sleep((int) ((Math.random() * 2000) + 3000));
                this.tryStop();
                bigPoolInside.pop(u);
                this.big_pool.getCapacity_control().release();
                u.setCount_activities();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(WaterPark.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void leave(User u) {
        u.setActivity("Finished");
        this.tryStop();
        this.changingRoom(u);
        if (u.getAge() < 18) {
            if (u.getAge() <= 10) {
                this.park_entry.release();
            }
            this.minors_number--;
        }
        this.park_entry.release();
    }

    public void activity_process(Queue q1, Queue q2, int time, Semaphore capacity, User user) {
        try {
            capacity.acquire();
            q1.pop(user);
            q2.push(user);
            Thread.sleep(time);
            this.tryStop();
            q2.pop(user);
            capacity.release();
        } catch (InterruptedException ex) {
            Logger.getLogger(WaterPark.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void activity_process(Queue q1, Queue q2, int time, Semaphore capacity, User user1, User user2) {
        try {
            capacity.acquire(2);
            q1.pop(user1);
            q1.pop(user2);
            q2.push(user1);
            q2.push(user2);
            Thread.sleep(time);
            this.tryStop();
            q2.pop(user1);
            q2.pop(user2);
            capacity.release(2);
        } catch (InterruptedException ex) {
            Logger.getLogger(WaterPark.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tryStop() {
        if (waiting) {
            try {
                lock.lock();
                try {
                    stop.await();
                } catch (InterruptedException ex) {

                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void resume() {
        try {
            lock.lock();
            this.waiting = false;
            stop.signalAll();

        } finally {
            lock.unlock();
        }
    }

    public void create_User() {
        if (this.users_id < 5000) {
            this.users_id++;
            try {
                Thread.sleep((int) (300 * Math.random()) + 400);
            } catch (InterruptedException e) {
            }
            String id, id_acc;
            int age_user, alternative = 0;
            User p;
            age_user = ((int) Math.round(Math.random() * 49)) + 1;
            if (age_user <= 10) {
                Child_under10 child;
                this.users_id++;
                id = "ID" + (this.users_id - 1) + "-" + age_user + "-" + this.users_id;
                alternative = ((int) Math.round(Math.random() * 32)) + 18;
                id_acc = "ID" + this.users_id + "-" + alternative + "-" + (this.users_id - 1);
                child = new Child_under10(id, age_user, this);
                p = new Accompanying(id_acc, alternative, this, child);
                child.setAcc((Accompanying) p);
                child.start();
                this.users.add(child);
            } else {
                id = "ID" + this.users_id + "-" + age_user;
                p = new User(id, age_user, this);
                p.start();
            }
            this.users.add(p);
        }
    }

    public int getMinors_number() {
        return minors_number;
    }

    public void setWaiting(boolean waiting) {
        this.waiting = waiting;
    }

    public ChangingRoom getChanging_room() {
        return changing_room;
    }

    public ChildrenPool getChildren_pool() {
        return children_pool;
    }

    public WavePool getWave_pool() {
        return wave_pool;
    }

    public SunBeds getSun_beds() {
        return sun_beds;
    }

    public BigPool getBig_pool() {
        return big_pool;
    }

    public Queue getBigPoolInside() {
        return bigPoolInside;
    }

    public Queue getSlide_A_waiting() {
        return slide_A_waiting;
    }

    public Queue getSlide_A_inside() {
        return slide_A_inside;
    }

    public Queue getSlide_B_waiting() {
        return slide_B_waiting;
    }

    public Queue getSlide_B_inside() {
        return slide_B_inside;
    }

    public Queue getSlide_C_waiting() {
        return slide_C_waiting;
    }

    public Queue getSlide_C_inside() {
        return slide_C_inside;
    }

    public Queue getChildrenPoolAdultsWaiting() {
        return childrenPoolAdultsWaiting;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public int getUsers_id() {
        return users_id;
    }

    public void setUsers_id() {
        this.users_id++;
    }

}
