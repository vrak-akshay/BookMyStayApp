import java.util.*;
import java.io.*;

abstract class Room implements Serializable {
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

class InventoryService implements Serializable{
    private HashMap<String,Integer> inventory=new HashMap<>();

    public void registerRoom(String type,int count){
        inventory.put(type,count);
    }

    public synchronized int getAvailability(String type){
        return inventory.getOrDefault(type,0);
    }

    public synchronized boolean allocateRoom(String type){
        int current=inventory.getOrDefault(type,0);
        if(current<=0) return false;
        inventory.put(type,current-1);
        return true;
    }

    public synchronized void releaseRoom(String type){
        int current=inventory.getOrDefault(type,0);
        inventory.put(type,current+1);
    }

    public boolean roomTypeExists(String type){
        return inventory.containsKey(type);
    }

    public HashMap<String,Integer> getInventory(){
        return inventory;
    }
}

class Reservation implements Serializable{
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

class ConfirmedReservation implements Serializable{
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

class BookingHistory implements Serializable{
    private List<ConfirmedReservation> history=new ArrayList<>();

    public synchronized void addRecord(ConfirmedReservation r){
        history.add(r);
    }

    public synchronized List<ConfirmedReservation> getHistory(){
        return history;
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

class BookingService implements Serializable{
    private InventoryService inventory;
    private BookingHistory history;
    private Set<String> assignedRooms=new HashSet<>();
    private int idCounter=1;
    private transient InvalidBookingValidator validator=new InvalidBookingValidator();

    public BookingService(InventoryService inventory,BookingHistory history){
        this.inventory=inventory;
        this.history=history;
    }

    private synchronized String generateRoomId(String type){
        String id=type+"-"+idCounter++;
        assignedRooms.add(id);
        return id;
    }

    public void processReservation(Reservation r){
        validator.validate(r,inventory);

        String type=r.getRoomType();

        if(inventory.allocateRoom(type)){
            String roomId=generateRoomId(type);
            ConfirmedReservation confirmed=new ConfirmedReservation(roomId,r.getGuestName(),type);
            history.addRecord(confirmed);
            System.out.println("Reservation confirmed "+roomId+" "+r.getGuestName());
        }else{
            System.out.println("Reservation failed "+r.getGuestName());
        }
    }
}

class SystemState implements Serializable{
    public InventoryService inventory;
    public BookingHistory history;

    public SystemState(InventoryService inventory,BookingHistory history){
        this.inventory=inventory;
        this.history=history;
    }
}

class PersistenceService{
    private String file="hotel_state.dat";

    public void save(SystemState state) throws Exception{
        ObjectOutputStream out=new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(state);
        out.close();
    }

    public SystemState load() throws Exception{
        ObjectInputStream in=new ObjectInputStream(new FileInputStream(file));
        SystemState state=(SystemState)in.readObject();
        in.close();
        return state;
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

public class HotelBookingApp{
    public static void main(String[] args) throws Exception{

        PersistenceService persistence=new PersistenceService();

        InventoryService inventory;
        BookingHistory history;

        File f=new File("hotel_state.dat");

        if(f.exists()){
            SystemState state=persistence.load();
            inventory=state.inventory;
            history=state.history;
            System.out.println("System state restored");
        }else{
            inventory=new InventoryService();
            inventory.registerRoom("Single",2);
            inventory.registerRoom("Double",1);
            inventory.registerRoom("Suite",1);

            history=new BookingHistory();
        }

        BookingService bookingService=new BookingService(inventory,history);

        bookingService.processReservation(new Reservation("Akshay","Single"));
        bookingService.processReservation(new Reservation("Ravi","Double"));

        persistence.save(new SystemState(inventory,history));

        System.out.println("System state saved");

        BookingReportService report=new BookingReportService();
        report.showAllBookings(history);
    }
}