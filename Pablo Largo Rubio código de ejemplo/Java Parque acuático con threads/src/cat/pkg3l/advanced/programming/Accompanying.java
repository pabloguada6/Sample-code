package cat.pkg3l.advanced.programming;

/**
 *
 * @author pablo
 */
public class Accompanying extends User {

    private final Child_under10 child;

    public Accompanying(String id, int age, WaterPark wp, Child_under10 child) {
        super(id, age, wp);
        this.child = child;
    }

    public Child_under10 getChild() {
        return child;
    }

    @Override
    public int getCount_activities() {
        return child.getCount_activities();
    }

    @Override
    public String getActivity() {
        return child.getActivity();
    }

}
