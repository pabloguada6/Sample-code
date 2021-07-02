package cat.pkg3l.advanced.programming;

/**
 *
 * @author pablo
 */
public class User extends Person {

    private int count_activities;
    private String activity;

    public User(String id, int age, WaterPark wp) {
        super(id, age, wp);
        this.count_activities = 0;
        this.activity = null;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public void setCount_activities() {
        this.count_activities++;
    }

    public int getCount_activities() {
        return count_activities;
    }

    @Override
    public void run() {
        super.getWp().create_User();
        super.getWp().enter(this);
        super.getWp().changingRoom(this);
        int num_activities = (int) ((Math.random() * 10) + 5);
        int activity_selected;
        while (count_activities < num_activities) {
            if (this.count_activities == (num_activities - 1)) {
                activity_selected = (int) (Math.random() * 3);
            } else {
                activity_selected = (int) (Math.random() * 6);
            }

            switch (activity_selected) {
                case 0:
                    super.getWp().wavePool(this);
                case 1:
                    super.getWp().childrenPool(this);
                case 2:
                    super.getWp().sunBeds(this);
                case 3:
                    super.getWp().bigPool(this);
                case 4:
                    super.getWp().slide(super.getWp().getBig_pool().getSlideA(), this, super.getWp().getSlide_A_waiting(), super.getWp().getSlide_A_inside(), "Slide A");
                case 5:
                    super.getWp().slide(super.getWp().getBig_pool().getSlideB(), this, super.getWp().getSlide_B_waiting(), super.getWp().getSlide_B_inside(), "Slide B");
                case 6:
                    super.getWp().slide(super.getWp().getBig_pool().getSlideC(), this, super.getWp().getSlide_C_waiting(), super.getWp().getSlide_C_inside(), "Slide C");
            }
        }
        super.getWp().leave(this);
    }

}
