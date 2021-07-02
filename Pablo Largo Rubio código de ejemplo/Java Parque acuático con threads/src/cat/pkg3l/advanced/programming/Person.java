package cat.pkg3l.advanced.programming;

/**
 *
 * @author pablo
 */
public abstract class Person extends Thread {

    private final int age;
    private final WaterPark wp;

    public Person(String id, int age, WaterPark wp) {
        super.setName(String.valueOf(id));
        this.age = age;
        this.wp = wp;
    }

    public int getAge() {
        return age;
    }

    public WaterPark getWp() {
        return wp;
    }

}
