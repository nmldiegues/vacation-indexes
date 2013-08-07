package pt.ist.fenixframework.vacation.domain;


public class Customer extends Customer_Base {

    public  Customer(int id) {
	super();
	setId(id);
    }

    public boolean customer_addReservationInfo(int type, int id, int price) {
	ReservationInfo reservationInfo = new ReservationInfo(type, id, price);
	addInfos(reservationInfo);
	return true;
    }

    public boolean customer_removeReservationInfo(int type, int id) {
	ReservationInfo reservationInfo = findInfo(type, id);

	if (reservationInfo == null) {
	    return false;
	}

	removeInfos(reservationInfo);
	reservationInfo.setId(-1);
	if (hasInfos(reservationInfo)) {
	    throw new RuntimeException("opacity problem");
	}
	return true;
    }
    
    public int customer_getBill() {
	int bill = 0;
	for (ReservationInfo it : getInfos()) {
	    bill += it.getPrice();
	}

	return bill;
    }

    private ReservationInfo findInfo(int type, int id) {
	for (ReservationInfo info : getInfos()) {
	    if (info.getType() == type && info.getId() == id) {
		return info;
	    }
	}
	return null;
    }

}
