import java.util.*;

abstract class Room {
    protected String type;
    protected int beds;
    protected int size;
    protected double pricePerNight;

    public Room(String type,int beds,int size,double pricePerNight){
        this.type=type;
        this.beds=beds;
        this.size=size;
        this.pricePerNight=pricePerNight;
    }

    public String getType(){
        return type;
    }
}

class SingleRoom extends Room{
    public SingleRoom(){
        super("Single",1,250,1500);
    }
}

class DoubleRoom extends Room{
    public DoubleRoom(){
        super("Double",2,400,2500);
    }
}

class SuiteRoom extends Room{
    public SuiteRoom(){
        super("Suite",3,750,5000);
    }
}

class InventoryService{
    private HashMap<String,Integer> inventory=new HashMap<>();

    public void registerRoom(String type,int count){
        inventory.put(type,count);
    }

    public int getAvailability(String type){
        return inventory.getOrDefault(type,0);
    }

    public boolean allocateRoom(String type){
        int current=inventory.getOrDefault(type,0);
        if(current<=0) return false;
        inventory.put(type,current-1);
        return true;
    }

    public void releaseRoom(String type){
        int current=inventory.getOrDefault(type,0);
        inventory.put(type,current+1);
    }

    public boolean roomTypeExists(String type){
        return inventory.containsKey(type);
    }
}

class Reservation{
    private String guestName;
    private String roomType;

    public Reservation(String guestName,String roomType){
        this.guestName=guestName;
        this.roomType=roomType;
    }

    public String getGuestName(){
        return guestName;
    }

    public String getRoomType(){
        return roomType;
    }
}

class ConfirmedReservation{
    private String reservationId;
    private String guestName;
    private String roomType;
    private boolean cancelled=false;

    public ConfirmedReservation(String reservationId,String guestName,String roomType){
        this.reservationId=reservationId;
        this.guestName=guestName;
        this.roomType=roomType;
    }

    public String getReservationId(){
        return reservationId;
    }

    public String getGuestName(){
        return guestName;
    }

    public String getRoomType(){
        return roomType;
    }

    public boolean isCancelled(){
        return cancelled;
    }

    public void cancel(){
        cancelled=true;
    }
}

class BookingRequestQueue{
    private Queue<Reservation> queue=new LinkedList<>();

    public void addRequest(Reservation r){
        queue.add(r);
    }

    public Reservation nextRequest(){
        return queue.poll();
    }

    public boolean hasRequests(){
        return !queue.isEmpty();
    }
}

class BookingHistory{
    private List<ConfirmedReservation> history=new ArrayList<>();

    public void addRecord(ConfirmedReservation r){
        history.add(r);
    }

    public ConfirmedReservation find(String id){
        for(ConfirmedReservation r:history){
            if(r.getReservationId().equals(id)) return r;
        }
        return null;
    }

    public List<ConfirmedReservation> getHistory(){
        return history;
    }
}

class BookingReportService{
    public void showAllBookings(BookingHistory history){
        for(ConfirmedReservation r:history.getHistory()){
            String status=r.isCancelled()?"CANCELLED":"ACTIVE";
            System.out.println(r.getReservationId()+" "+r.getGuestName()+" "+r.getRoomType()+" "+status);
        }
    }
}

class InvalidBookingValidator{
    public void validate(Reservation r,InventoryService inventory){
        if(r.getGuestName()==null || r.getGuestName().trim().isEmpty()){
            throw new IllegalArgumentException("Guest name is invalid");
        }

        if(r.getRoomType()==null || r.getRoomType().trim().isEmpty()){
            throw new IllegalArgumentException("Room type is invalid");
        }

        if(!inventory.roomTypeExists(r.getRoomType())){
            throw new IllegalArgumentException("Room type does not exist");
        }
    }
}

class BookingService{
    private InventoryService inventory;
    private Set<String> assignedRooms=new HashSet<>();
    private int idCounter=1;
    private BookingHistory history;
    private InvalidBookingValidator validator=new InvalidBookingValidator();

    public BookingService(InventoryService inventory,BookingHistory history){
        this.inventory=inventory;
        this.history=history;
    }

    private String generateRoomId(String type){
        String id=type+"-"+idCounter++;
        assignedRooms.add(id);
        return id;
    }

    public void processQueue(BookingRequestQueue queue){
        while(queue.hasRequests()){
            Reservation r=queue.nextRequest();

            try{
                validator.validate(r,inventory);

                String type=r.getRoomType();

                if(inventory.getAvailability(type)>0){
                    boolean allocated=inventory.allocateRoom(type);

                    if(allocated){
                        String roomId=generateRoomId(type);
                        ConfirmedReservation confirmed=new ConfirmedReservation(roomId,r.getGuestName(),type);
                        history.addRecord(confirmed);
                        System.out.println("Reservation confirmed "+roomId+" "+r.getGuestName());
                    }
                }else{
                    System.out.println("Reservation failed "+r.getGuestName()+" No rooms available");
                }

            }catch(Exception e){
                System.out.println("Booking error: "+e.getMessage());
            }
        }
    }
}

class CancellationService{
    private InventoryService inventory;
    private BookingHistory history;
    private Stack<String> rollback=new Stack<>();

    public CancellationService(InventoryService inventory,BookingHistory history){
        this.inventory=inventory;
        this.history=history;
    }

    public void cancel(String reservationId){
        ConfirmedReservation reservation=history.find(reservationId);

        if(reservation==null){
            System.out.println("Cancellation failed Reservation not found");
            return;
        }

        if(reservation.isCancelled()){
            System.out.println("Cancellation failed Reservation already cancelled");
            return;
        }

        rollback.push(reservationId);

        inventory.releaseRoom(reservation.getRoomType());

        reservation.cancel();

        System.out.println("Reservation cancelled "+reservationId);
    }
}

public class HotelBookingApp{
    public static void main(String[] args){

        InventoryService inventory=new InventoryService();
        inventory.registerRoom("Single",2);
        inventory.registerRoom("Double",1);
        inventory.registerRoom("Suite",1);

        BookingHistory history=new BookingHistory();

        BookingRequestQueue queue=new BookingRequestQueue();
        queue.addRequest(new Reservation("Akshay","Single"));
        queue.addRequest(new Reservation("Ravi","Double"));

        BookingService bookingService=new BookingService(inventory,history);
        bookingService.processQueue(queue);

        CancellationService cancellationService=new CancellationService(inventory,history);

        cancellationService.cancel("Single-1");

        BookingReportService reportService=new BookingReportService();

        System.out.println();
        reportService.showAllBookings(history);
    }
}