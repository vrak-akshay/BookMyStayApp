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
        System.out.println(type + " Room");
        System.out.println("Beds: " + beds);
        System.out.println("Size: " + size + " sqft");
        System.out.println("Price: " + pricePerNight);
        System.out.println("Available: " + available);
        System.out.println();
    }
}

class SingleRoom extends Room {
    public SingleRoom() {
        super("Single",1,250,1500);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double",2,400,2500);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite",3,750,5000);
    }
}

class InventoryService {
    private HashMap<String,Integer> inventory = new HashMap<>();

    public void registerRoom(String type,int count) {
        inventory.put(type,count);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type,0);
    }

    public boolean allocateRoom(String type) {
        int current = inventory.getOrDefault(type,0);
        if(current<=0) return false;
        inventory.put(type,current-1);
        return true;
    }

    public void displayInventory() {
        for(String key:inventory.keySet()) {
            System.out.println(key+" rooms available: "+inventory.get(key));
        }
    }
}

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName,String roomType) {
        this.guestName=guestName;
        this.roomType=roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.add(r);
    }

    public Reservation nextRequest() {
        return queue.poll();
    }

    public boolean hasRequests() {
        return !queue.isEmpty();
    }
}

class BookingService {
    private InventoryService inventory;
    private Set<String> assignedRooms = new HashSet<>();
    private int idCounter = 1;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
    }

    private String generateRoomId(String type) {
        String id = type + "-" + idCounter++;
        assignedRooms.add(id);
        return id;
    }

    public void processQueue(BookingRequestQueue queue) {
        while(queue.hasRequests()) {
            Reservation r = queue.nextRequest();
            String type = r.getRoomType();

            if(inventory.getAvailability(type)>0) {
                boolean allocated = inventory.allocateRoom(type);
                if(allocated) {
                    String roomId = generateRoomId(type);
                    System.out.println("Reservation confirmed for "+r.getGuestName()+" Room ID: "+roomId);
                }
            } else {
                System.out.println("Reservation failed for "+r.getGuestName()+" No "+type+" rooms available");
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

        InventoryService inventory = new InventoryService();
        inventory.registerRoom("Single",2);
        inventory.registerRoom("Double",1);
        inventory.registerRoom("Suite",1);

        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Akshay","Single"));
        queue.addRequest(new Reservation("Ravi","Double"));
        queue.addRequest(new Reservation("Meena","Single"));
        queue.addRequest(new Reservation("Arun","Suite"));

        BookingService bookingService = new BookingService(inventory);

        bookingService.processQueue(queue);

        System.out.println();
        inventory.displayInventory();
    }
}