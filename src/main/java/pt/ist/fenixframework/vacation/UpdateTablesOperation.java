package pt.ist.fenixframework.vacation;

import java.util.concurrent.Callable;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.vacation.domain.VacationManager;

public final class UpdateTablesOperation implements Operation {

    final private VacationManager managerPtr;
    final private int[] types;
    final private int[] ids;
    final private int[] ops;
    final private int[] prices;
    final private int numUpdate;

    public UpdateTablesOperation(VacationManager managerPtr, Random randomPtr, int numQueryPerTransaction, int queryRange) {
	this.managerPtr = managerPtr;
	this.types = new int[numQueryPerTransaction];
	this.ids = new int[numQueryPerTransaction];
	this.ops = new int[numQueryPerTransaction];
	this.prices = new int[numQueryPerTransaction];

	int[] baseIds = new int[20];
	for (int i = 0; i < 20; i++) {
	    baseIds[i] = (randomPtr.random_generate() % queryRange) + 1;
	}
	
	this.numUpdate = numQueryPerTransaction;
	int n;
	for (n = 0; n < numUpdate; n++) {
	    types[n] = randomPtr.posrandom_generate() % Definitions.NUM_RESERVATION_TYPE;
	    ids[n] = baseIds[n % 20];
	    ops[n] = randomPtr.posrandom_generate() % 2;
	    if (ops[n] == 1) {
		prices[n] = ((randomPtr.posrandom_generate() % 5) * 10) + 50;
	    }
	}
    }


    @Override
    public int doOperation() {
	final AbortStats s = new AbortStats();
	try {
	    FenixFramework.getTransactionManager().withTransaction(new Callable<Void>() {
	        public Void call() throws Exception {
	            s.aborts++;
	    	int n;
	    	for (n = 0; n < numUpdate; n++) {
	    	    int t = types[n];
	    	    int id = ids[n];
	    	    int doAdd = ops[n];
	    	    if (doAdd == 1) {
	    		int newPrice = prices[n];
	    		if (t == Definitions.RESERVATION_CAR) {
	    		    managerPtr.manager_addCar(id, 100, newPrice);
	    		} else if (t == Definitions.RESERVATION_FLIGHT) {
	    		    managerPtr.manager_addFlight(id, 100, newPrice);
	    		} else if (t == Definitions.RESERVATION_ROOM) {
	    		    managerPtr.manager_addRoom(id, 100, newPrice);
	    		} else {
	    		    assert (false);
	    		}
	    	    } else { /* do delete */
	    		if (t == Definitions.RESERVATION_CAR) {
	    		    managerPtr.manager_deleteCar(id, 100);
	    		} else if (t == Definitions.RESERVATION_FLIGHT) {
	    		    managerPtr.manager_deleteFlight(id);
	    		} else if (t == Definitions.RESERVATION_ROOM) {
	    		    managerPtr.manager_deleteRoom(id, 100);
	    		} else {
	    		    assert (false);
	    		}
	    	    }
	    	}
	    	return null;
	        }
	        
	    });
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return s.aborts;
    }

}
