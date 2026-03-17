import java.util.*;

abstract class Room {
    protected String type;
    protected int beds;
    protected int size;
    protected double pricePerNight;

    public Room(String type,int beds,int size,double pricePerNight) {
        this.type=type;
        this.beds=beds;
        this.size=size;
        this.pricePerNight=pricePerNight;
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
    private HashMap<String,Integer> inventory=new HashMap<>();

    public void registerRoom(String type,int count) {
        inventory.put(type,count);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type,0);
    }

    public boolean allocateRoom(String type) {
        int current=inventory.getOrDefault(type,0);
        if(current<=0) return false;
        inventory.put(type,current-1);
        return true;
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

class ConfirmedReservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public ConfirmedReservation(String reservationId,String guestName,String roomType) {
        this.reservationId=reservationId;
        this.guestName=guestName;
        this.roomType=roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

class BookingRequestQueue {
    private Queue<Reservation> queue=new LinkedList<>();

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

class BookingHistory {
    private List<ConfirmedReservation> history=new ArrayList<>();

    public void addRecord(ConfirmedReservation r) {
        history.add(r);
    }

    public List<ConfirmedReservation> getHistory() {
        return history;
    }
}

class BookingReportService {
    public void showAllBookings(BookingHistory history) {
        List<ConfirmedReservation> records=history.getHistory();
        for(ConfirmedReservation r:records) {
            System.out.println(r.getReservationId()+" "+r.getGuestName()+" "+r.getRoomType());
        }
    }

    public void showRoomSummary(BookingHistory history) {
        HashMap<String,Integer> count=new HashMap<>();
        for(ConfirmedReservation r:history.getHistory()) {
            count.put(r.getRoomType(),count.getOrDefault(r.getRoomType(),0)+1);
        }
        for(String type:count.keySet()) {
            System.out.println(type+" bookings: "+count.get(type));
        }
    }
}

class BookingService {
    private InventoryService inventory;
    private Set<String> assignedRooms=new HashSet<>();
    private int idCounter=1;
    private BookingHistory history;

    public BookingService(InventoryService inventory,BookingHistory history) {
        this.inventory=inventory;
        this.history=history;
    }

    private String generateRoomId(String type) {
        String id=type+"-"+idCounter++;
        assignedRooms.add(id);
        return id;
    }

    public void processQueue(BookingRequestQueue queue) {
        while(queue.hasRequests()) {
            Reservation r=queue.nextRequest();
            String type=r.getRoomType();

            if(inventory.getAvailability(type)>0) {
                boolean allocated=inventory.allocateRoom(type);
                if(allocated) {
                    String roomId=generateRoomId(type);
                    ConfirmedReservation confirmed=new ConfirmedReservation(roomId,r.getGuestName(),type);
                    history.addRecord(confirmed);
                    System.out.println("Reservation confirmed "+roomId+" "+r.getGuestName());
                }
            } else {
                System.out.println("Reservation failed "+r.getGuestName());
            }
        }
    }
}

public class HotelBookingApp {
    public static void main(String[] args) {

        InventoryService inventory=new InventoryService();
        inventory.registerRoom("Single",2);
        inventory.registerRoom("Double",1);
        inventory.registerRoom("Suite",1);

        BookingHistory history=new BookingHistory();

        BookingRequestQueue queue=new BookingRequestQueue();
        queue.addRequest(new Reservation("Akshay","Single"));
        queue.addRequest(new Reservation("Ravi","Double"));
        queue.addRequest(new Reservation("Meena","Single"));

        BookingService bookingService=new BookingService(inventory,history);
        bookingService.processQueue(queue);

        BookingReportService reportService=new BookingReportService();

        System.out.println();
        reportService.showAllBookings(history);

        System.out.println();
        reportService.showRoomSummary(history);
    }
}