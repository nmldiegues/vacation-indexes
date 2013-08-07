package pt.ist.fenixframework.vacation.domain;

public interface Reservation {

    public void checkReservation();
     
     public boolean reservation_addToTotal(int num);
     
     public boolean reservation_make();
     
     public boolean reservation_cancel();
     
     public boolean reservation_updatePrice(int newPrice);
     
     public int getPrice();
    
}
