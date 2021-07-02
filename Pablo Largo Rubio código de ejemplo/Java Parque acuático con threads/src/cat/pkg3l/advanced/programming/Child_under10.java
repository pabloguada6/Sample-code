package cat.pkg3l.advanced.programming;

/**
 *
 * @author pablo
 */
public class Child_under10 extends User {

    private Accompanying acc;

    public Child_under10(String id, int age, WaterPark wp) {
        super(id, age, wp);

    }

    public Accompanying getAcc() {
        return acc;
    }

    public void setAcc(Accompanying acc) {
        this.acc = acc;
    }

}
