package pt.ist.fenixframework.vacation.domain;

public class FlightReservation extends FlightReservation_Base implements Reservation {
    
    public  FlightReservation(int id, int numTotal, int price) {
        super();
        setId(id);
        setNumUsed(0);
        setNumFree(numTotal);
        setNumTotal(numTotal);
        setPrice(price);
        checkReservation();
    }
    
    public void checkReservation() {
	int numUsed = getNumUsed();
	if (numUsed < 0) {
	    throw new RuntimeException("opacity problem");
	}

	int numFree = getNumFree();
	if (numFree < 0) {
	    throw new RuntimeException("opacity problem");
	}

	int numTotal = getNumTotal();
	if (numTotal < 0) {
	    throw new RuntimeException("opacity problem");
	}

	if ((numUsed + numFree) != numTotal) {
	    throw new RuntimeException("opacity problem");
	}

	int price = getPrice();
	if (price < 0) {
	    throw new RuntimeException("opacity problem");
	}
    }
    
    public boolean reservation_addToTotal(int num) {
	if (getNumFree() + num < 0) {
	    return false;
	}

	setNumFree(getNumFree() + num);
	setNumTotal(getNumTotal() + num);
	checkReservation();
	return true;
    }
    
    public boolean reservation_make() {
	if (getNumFree() < 1) {
	    return false;
	}
	setNumUsed(getNumUsed() + 1);
	setNumFree(getNumFree() - 1);
	checkReservation();
	return true;
    }
    
    public boolean reservation_cancel() {
	if (getNumUsed() < 1) {
	    return false;
	}
	setNumUsed(getNumUsed() - 1);
	setNumFree(getNumFree() + 1);
	checkReservation();
	return true;
    }
    
    public boolean reservation_updatePrice(int newPrice) {
	if (newPrice < 0) {
	    return false;
	}
	
	setPrice(newPrice);
	checkReservation();
	return true;
    }
    
}
