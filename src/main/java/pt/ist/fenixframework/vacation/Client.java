package pt.ist.fenixframework.vacation;

import pt.ist.fenixframework.vacation.domain.VacationManager;

public class Client extends Thread {
    final int id;
    final VacationManager managerPtr;
    final Random randomPtr;
    final int numOperation;
    final int numQueryPerTransaction;
    final int queryRange;
    final int percentUser;
    public long steps;
    public long aborts;
    final int readOnlyPerc;
    

    public Client(int id, VacationManager managerPtr, int numOperation, int numQueryPerTransaction, int queryRange, int percentUser, int readOnlyPerc) {
	this.randomPtr = new Random();
	this.randomPtr.random_alloc();
	this.id = id;
	this.managerPtr = managerPtr;
	randomPtr.random_seed(id);
	this.numOperation = numOperation;
	this.numQueryPerTransaction = numQueryPerTransaction;
	this.queryRange = queryRange;
	this.percentUser = percentUser;
	this.readOnlyPerc = readOnlyPerc;
    }

	private static ActionType selectAction(int r, int percentUser) {
		if (r < percentUser) {
			return ActionType.ACTION_MAKE_RESERVATION;
		} else if ((r & 1) == 1) {
			return ActionType.ACTION_DELETE_CUSTOMER;
		} else {
			return ActionType.ACTION_UPDATE_TABLES;
		}
	}

	@Override
	public void run() {
		long start = System.nanoTime();
		java.util.Random ran = new java.util.Random();
		while (true) {
			int r = randomPtr.posrandom_generate() % 100;
			boolean readOnly = ran.nextInt(100) < readOnlyPerc;
			final ActionType action = selectAction(r, percentUser);
			final Operation op = action.createOperation(managerPtr, randomPtr, numQueryPerTransaction, queryRange, readOnly);
			aborts += op.doOperation();
			steps++;
			long end = System.nanoTime();
			if (((end - start) / 1000000000) > numOperation) {
				break;
			}
		}

    }

}

