package pt.ist.fenixframework.vacation.domain;

import java.util.Set;

import pt.ist.fenixframework.CallableWithoutException;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.vacation.Definitions;
import pt.ist.fenixframework.vacation.VacationBenchmark;


public class VacationManager extends VacationManager_Base {

    public  VacationManager() {
	super();
    }

    public boolean setup_manager_addCar(int id, int num, int price) {
	addCars(new CarReservation(id, num, price));
	return true;
    }
    public boolean setup_manager_addRoom(int id, int num, int price) {
	addRooms(new RoomReservation(id, num, price));
	return true;
    }
    public boolean setup_manager_addFlight(int id, int num, int price) {
	addFlights(new FlightReservation(id, num, price));
	return true;
    }
    
    public boolean manager_addCar(int id, int num, int price) {
	CarReservation reservation = findCarById(id);
	if (reservation == null) {
	    /* Create new reservation */
	    if (num < 1 || price < 0) {
		return false;
	    }
	    reservation = new CarReservation(id, num, price);
	    addCars(reservation);
	} else {
	    /* Update existing reservation */
	    if (!reservation.reservation_addToTotal(num)) {
		return false;
	    }
	    if (reservation.getNumTotal() == 0) {
		removeCars(reservation);
		reservation.setId(-1);
		// hack to go along with class indexation!
		if (hasCars(reservation)) {
		    throw new RuntimeException("opacity problem");
		}
	    } else {
		reservation.reservation_updatePrice(price);
	    }
	}

	return true;
    }
    
    private CarReservation findCarById(int id) {
	return this.getCarsById(id);
    }
    
    private RoomReservation findRoomById(int id) {
	return this.getRoomsById(id);
    }
    
    private FlightReservation findFlightById(int id) {
	return this.getFlightsById(id);
    }

    public boolean manager_deleteCar(int id, int num) {
	return manager_addCar(id, -num, -1);
    }

    public boolean manager_addRoom(int id, int num, int price) {
	RoomReservation reservation = findRoomById(id);
	if (reservation == null) {
	    /* Create new reservation */
	    if (num < 1 || price < 0) {
		return false;
	    }
	    reservation = new RoomReservation(id, num, price);
	    addRooms(reservation);
	} else {
	    /* Update existing reservation */
	    if (!reservation.reservation_addToTotal(num)) {
		return false;
	    }
	    if (reservation.getNumTotal() == 0) {
		removeRooms(reservation);
		reservation.setId(-1);
		// hack to go along with class indexation!
		if (hasRooms(reservation)) {
		    throw new RuntimeException("opacity problem");
		}
	    } else {
		reservation.reservation_updatePrice(price);
	    }
	}

	return true;
    }

    public boolean manager_deleteRoom(int roomId, int numRoom) {
	return manager_addRoom(roomId, -numRoom, -1);
    }

    public boolean manager_addFlight(int id, int num, int price) {
	FlightReservation reservation = findFlightById(id);
	if (reservation == null) {
	    /* Create new reservation */
	    if (num < 1 || price < 0) {
		return false;
	    }
	    reservation = new FlightReservation(id, num, price);
	    addFlights(reservation);
	} else {
	    /* Update existing reservation */
	    if (!reservation.reservation_addToTotal(num)) {
		return false;
	    }
	    if (reservation.getNumTotal() == 0) {
		removeFlights(reservation);
		reservation.setId(-1);
		// hack to go along with class indexation!
		if (hasFlights(reservation)) {
		    throw new RuntimeException("opacity problem");
		}
	    } else {
		reservation.reservation_updatePrice(price);
	    }
	}

	return true;
    }

    public boolean manager_deleteFlight(int flightId) {
	FlightReservation reservation = findFlightById(flightId);
	if (reservation == null) {
	    return false;
	}

	if (reservation.getNumUsed() > 0) {
	    return false; /* somebody has a reservation */
	}

	return manager_addFlight(flightId, -reservation.getNumTotal(), -1);
    }

    public boolean manager_addCustomer(int customerId) {
	Customer customer;

	if (findCustomerById(customerId) != null) {
	    return false;
	}

	customer = new Customer(customerId);
	addCustomers(customer);
	if (!hasCustomers(customer)) {
	    throw new RuntimeException("opacity problem");
	}

	return true;
    }
    
    public boolean setup_manager_addCustomer(int customerId) {
	Customer customer;
	customer = new Customer(customerId);
	addCustomers(customer);

	return true;
    }

    private Customer findCustomerById(int id) {
	return this.getCustomersById(id);
    }

    public boolean manager_deleteCustomer(int customerId) {
	Customer customer;
	Set<ReservationInfo> reservationInfoList;
	boolean status;

	customer = findCustomerById(customerId);
	if (customer == null) {
	    return false;
	}

	/* Cancel this customer's reservations */
	reservationInfoList = customer.getInfos();

	for (ReservationInfo reservationInfo : reservationInfoList) {
	    Reservation reservation;
	    int type = reservationInfo.getType();
	    if (type == Definitions.RESERVATION_CAR) {
		reservation = findCarById(reservationInfo.getId());
	    } else if (type == Definitions.RESERVATION_ROOM) {
		reservation = findRoomById(reservationInfo.getId());
	    } else {
		reservation = findFlightById(reservationInfo.getId());
	    }
	    if (reservation == null) {
		throw new RuntimeException("opacity problem");
	    }
	    status = reservation.reservation_cancel();
	    if (!status) {
		throw new RuntimeException("opacity problem");
	    }
	}

	removeCustomers(customer);
	// hack to go along with class indexation!
	customer.setId(-1);
	
	if (hasCustomers(customer)) {
	    throw new RuntimeException("opacity problem");
	}

	return true;
    }

    public int manager_queryCar(int carId) {
	int numFree = -1;
	CarReservation reservation = findCarById(carId);
	if (reservation != null) {
	    numFree = reservation.getNumFree();
	}

	return numFree;
    }
    
    public int manager_queryCarPrice(int carId) {
	int price = -1;
	CarReservation reservation = findCarById(carId);
	if (reservation != null) {
	    price = reservation.getPrice();
	}

	return price;
    }
    
    public int manager_queryRoom(int roomId) {
	int numFree = -1;
	RoomReservation reservation = findRoomById(roomId);
	if (reservation != null) {
	    numFree = reservation.getNumFree();
	}

	return numFree;
    }
    
    public int manager_queryRoomPrice(int roomId) {
	int price = -1;
	RoomReservation reservation = findRoomById(roomId);
	if (reservation != null) {
	    price = reservation.getPrice();
	}

	return price;
    }
    
    public int manager_queryFlight(int flightId) {
	int numFree = -1;
	FlightReservation reservation = findFlightById(flightId);
	if (reservation != null) {
	    numFree = reservation.getNumFree();
	}

	return numFree;
    }
    
    public int manager_queryFlightPrice(int flightId) {
	int price = -1;
	FlightReservation reservation = findFlightById(flightId);
	if (reservation != null) {
	    price = reservation.getPrice();
	}

	return price;
    }
    
    public int manager_queryCustomerBill(int customerId) {
	int bill = -1;
	Customer customer;

	customer = findCustomerById(customerId);

	if (customer != null) {
	    bill = customer.customer_getBill();
	}

	return bill;
    }
    
    public boolean reserve(Reservation reservation, int customerId, int id, int type) {
	Customer customer = findCustomerById(customerId);

	if (customer == null) {
	    return false;
	}

	if (reservation == null) {
	    return false;
	}

	if (!reservation.reservation_make()) {
	    return false;
	}

	if (!customer.customer_addReservationInfo(type, id, reservation.getPrice())) {
	    /* Undo previous successful reservation */
	    boolean status = reservation.reservation_cancel();
	    if (!status) {
		throw new RuntimeException("opacity problem");
	    }
	    return false;
	}

	return true;
    }
    
    public boolean manager_reserveCar(int customerId, int carId) {
	return reserve(findCarById(carId), customerId, carId, Definitions.RESERVATION_CAR);
    }
    
    public boolean manager_reserveRoom(int customerId, int roomId) {
	return reserve(findRoomById(roomId), customerId, roomId, Definitions.RESERVATION_ROOM);
    }
    
    public boolean manager_reserveFlight(int customerId, int flightId) {
	return reserve(findFlightById(flightId), customerId, flightId, Definitions.RESERVATION_FLIGHT);
    }
    
    public boolean cancel(Reservation reservation, int customerId, int id, int type) {
	Customer customer = findCustomerById(customerId);

	if (customer == null) {
	    return false;
	}

	if (reservation == null) {
	    return false;
	}

	if (!reservation.reservation_cancel()) {
	    return false;
	}

	if (!customer.customer_removeReservationInfo(type, id)) {
	    /* Undo previous successful cancellation */
	    boolean status = reservation.reservation_make();
	    if (!status) {
		throw new RuntimeException("opacity problem");
	    }
	    return false;
	}

	return true;
    }
    
    boolean manager_cancelCar(int customerId, int carId) {
	return cancel(findCarById(carId), customerId, carId, Definitions.RESERVATION_CAR);
    }
    
    boolean manager_cancelRoom(int customerId, int roomId) {
	return cancel(findRoomById(roomId), customerId, roomId, Definitions.RESERVATION_ROOM);
    }
    
    boolean manager_cancelFlight(int customerId, int flightId) {
	return cancel(findFlightById(flightId), customerId, flightId, Definitions.RESERVATION_FLIGHT);
    }

    public void checkTables(final VacationBenchmark benchmark) {
	CallableWithoutException<Void> callable = new CallableWithoutException<Void>(){
	    public Void call() {
		int i;
		int numRelation = benchmark.RELATIONS;

		/* Check for unique customer IDs */
		int percentQuery = benchmark.QUERIES;
		int queryRange = (int) (percentQuery / 100.0 * numRelation + 0.5);
		int maxCustomerId = queryRange + 1;
		for (i = 1; i <= maxCustomerId; i++) {
		    Customer c = findCustomerById(i);
		    if (c != null) {
			removeCustomers(c);
			if (hasCustomers(c)) {
			    assert (findCustomerById(i) == null);
			}
		    }
		}

		/* Check reservation tables for consistency and unique ids */
		for (i = 1; i <= numRelation; i++) {
		    CarReservation r = findCarById(i);
		    if (r != null) {
			boolean status = manager_addCar(i, 0, 0);
			assert(status);
			removeCars(r);
			assert(!hasCars(r));
		    }
		}
		for (i = 1; i <= numRelation; i++) {
		    RoomReservation r = findRoomById(i);
		    if (r != null) {
			boolean status = manager_addRoom(i, 0, 0);
			assert(status);
			removeRooms(r);
			assert(!hasRooms(r));
		    }
		}
		for (i = 1; i <= numRelation; i++) {
		    FlightReservation r = findFlightById(i);
		    if (r != null) {
			boolean status = manager_addFlight(i, 0, 0);
			assert(status);
			removeFlights(r);
			assert(!hasFlights(r));
		    }
		}
		
		return null;
	    }
	    
	};
	try {
	    FenixFramework.getTransactionManager().withTransaction(callable);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}
