import java.util.HashMap;
import java.util.Map;

abstract class Room {

    protected String type;
    protected int beds;
    protected int size;
    protected double pricePerNight;

    public Room(String type, int beds, int size, double pricePerNight) {
        this.type = type;
        this.beds = beds;
        this.size = size;
        this.pricePerNight = pricePerNight;
    }

    public String getType() {
        return type;
    }

    public void displayDetails() {
        System.out.println(type + " Room:");
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sqft");
        System.out.println("Price per night: " + pricePerNight);
    }
}


class SingleRoom extends Room {

    public SingleRoom() {
        super("Single", 1, 250, 1500);
    }
}

class DoubleRoom extends Room {

    public DoubleRoom() {
        super("Double", 2, 400, 2500);
    }
}

class SuiteRoom extends Room {

    public SuiteRoom() {
        super("Suite", 3, 750, 5000);
    }
}


class RoomInventory {

    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
    }

    // Register room type
    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    // Get availability
    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    // Update availability
    public void updateAvailability(String type, int change) {
        int current = inventory.getOrDefault(type, 0);
        inventory.put(type, current + change);
    }

    // Display inventory
    public void displayInventory() {

        System.out.println("\nCurrent Room Inventory:");

        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " Rooms Available: " + entry.getValue());
        }
    }
}


public class HotelBookingApp {

    public static void main(String[] args) {

        System.out.println("Hotel Room Initialization\n");

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        single.displayDetails();
        System.out.println();

        doubleRoom.displayDetails();
        System.out.println();

        suite.displayDetails();

        // Initialize centralized inventory
        RoomInventory inventory = new RoomInventory();

        inventory.addRoomType("Single", 5);
        inventory.addRoomType("Double", 3);
        inventory.addRoomType("Suite", 2);

        inventory.displayInventory();
    }
}