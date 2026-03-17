abstract class Room {

    protected int beds;
    protected int size;
    protected double pricePerNight;
    protected int available;

    public Room(int beds, int size, double pricePerNight, int available) {
        this.beds = beds;
        this.size = size;
        this.pricePerNight = pricePerNight;
        this.available = available;
    }

    public void displayDetails() {
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sqft");
        System.out.println("Price per night: " + pricePerNight);
        System.out.println("Available: " + available);
    }
}


class SingleRoom extends Room {

    public SingleRoom() {
        super(1, 250, 1500.0, 5);
    }
}


class DoubleRoom extends Room {

    public DoubleRoom() {
        super(2, 400, 2500.0, 3);
    }
}


class SuiteRoom extends Room {

    public SuiteRoom() {
        super(3, 750, 5000.0, 2);
    }
}


public class HotelBookingApp {

    public static void main(String[] args) {

        System.out.println("Hotel Room Initialization\n");

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        System.out.println("Single Room:");
        single.displayDetails();

        System.out.println();

        System.out.println("Double Room:");
        doubleRoom.displayDetails();

        System.out.println();

        System.out.println("Suite Room:");
        suite.displayDetails();
    }
}