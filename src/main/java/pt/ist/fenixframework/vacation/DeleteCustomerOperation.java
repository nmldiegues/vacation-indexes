package pt.ist.fenixframework.vacation;

import java.util.concurrent.Callable;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.vacation.domain.VacationManager;

public class DeleteCustomerOperation extends Operation {

    final private VacationManager managerPtr;
    final private int customerId;

    public DeleteCustomerOperation(VacationManager managerPtr, Random randomPtr, int queryRange) {
	this.managerPtr = managerPtr; 
	this.customerId = randomPtr.posrandom_generate() % queryRange + 1;
    }

    @Override
    public int doOperation() {
	final AbortStats s = new AbortStats();
	s.aborts = -1;
	try {
	    FenixFramework.getTransactionManager().withTransaction(new Callable<Void>() {
		public Void call() throws Exception {
		    s.aborts++;
		    int bill = managerPtr.manager_queryCustomerBill(customerId);
		    if (bill >= 0) {
			managerPtr.manager_deleteCustomer(customerId);
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
