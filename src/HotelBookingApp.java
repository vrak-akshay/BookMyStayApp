import java.util.*;

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

    public void displayDetails(int available) {
        System.out.println(type + " Room:");
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sqft");
        System.out.println("Price per night: " + pricePerNight);
        System.out.println("Available: " + available);
        System.out.println();
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

    public void registerRoom(String type, int count) {
        inventory.put(type, count);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public Map<String, Integer> getInventory() {
        return inventory;
    }
}


class SearchService {

    public void searchAvailableRooms(List<Room> rooms, RoomInventory inventory) {

        System.out.println("\nAvailable Rooms:\n");

        for (Room room : rooms) {

            int available = inventory.getAvailability(room.getType());

            // Filter unavailable rooms
            if (available > 0) {
                room.displayDetails(available);
            }
        }
    }
}


public class HotelBookingApp {

    public static void main(String[] args) {

        List<Room> rooms = new ArrayList<>();

        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

        RoomInventory inventory = new RoomInventory();

        inventory.registerRoom("Single", 5);
        inventory.registerRoom("Double", 3);
        inventory.registerRoom("Suite", 2);

        SearchService searchService = new SearchService();

        System.out.println("Guest searches for available rooms...");

        searchService.searchAvailableRooms(rooms, inventory);
    }
}