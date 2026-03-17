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
}

class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName,String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
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

    public List<String> processQueue(BookingRequestQueue queue) {
        List<String> confirmedReservations = new ArrayList<>();

        while(queue.hasRequests()) {
            Reservation r = queue.nextRequest();
            String type = r.getRoomType();

            if(inventory.getAvailability(type) > 0) {
                boolean allocated = inventory.allocateRoom(type);
                if(allocated) {
                    String roomId = generateRoomId(type);
                    confirmedReservations.add(roomId);
                    System.out.println("Reservation confirmed for " + r.getGuestName() + " Room ID: " + roomId);
                }
            } else {
                System.out.println("Reservation failed for " + r.getGuestName());
            }
        }

        return confirmedReservations;
    }
}

class AddOnService {
    private String name;
    private double price;

    public AddOnService(String name,double price) {
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }
}

class AddOnServiceManager {
    private Map<String,List<AddOnService>> reservationServices = new HashMap<>();

    public void addService(String reservationId, AddOnService service) {
        reservationServices.putIfAbsent(reservationId,new ArrayList<>());
        reservationServices.get(reservationId).add(service);
    }

    public double calculateCost(String reservationId) {
        double total = 0;

        List<AddOnService> services = reservationServices.getOrDefault(reservationId,new ArrayList<>());

        for(AddOnService s : services) {
            total += s.getPrice();
        }

        return total;
    }

    public void showServices(String reservationId) {
        List<AddOnService> services = reservationServices.getOrDefault(reservationId,new ArrayList<>());

        System.out.println("Add-on services for reservation " + reservationId);

        for(AddOnService s : services) {
            System.out.println(s.getName() + " : " + s.getPrice());
        }

        System.out.println("Total add-on cost: " + calculateCost(reservationId));
    }
}

public class HotelBookingApp {
    public static void main(String[] args) {

        InventoryService inventory = new InventoryService();
        inventory.registerRoom("Single",2);
        inventory.registerRoom("Double",1);
        inventory.registerRoom("Suite",1);

        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Akshay","Single"));
        queue.addRequest(new Reservation("Ravi","Double"));

        BookingService bookingService = new BookingService(inventory);

        List<String> reservations = bookingService.processQueue(queue);

        AddOnService wifi = new AddOnService("Premium WiFi",200);
        AddOnService breakfast = new AddOnService("Breakfast",300);
        AddOnService spa = new AddOnService("Spa Access",500);

        AddOnServiceManager manager = new AddOnServiceManager();

        String reservationId = reservations.get(0);

        manager.addService(reservationId,wifi);
        manager.addService(reservationId,breakfast);
        manager.addService(reservationId,spa);

        manager.showServices(reservationId);
    }
}