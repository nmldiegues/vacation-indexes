package pt.ist.fenixframework.vacation;

import pt.ist.fenixframework.vacation.domain.VacationManager;

public enum ActionType {
	
	ACTION_MAKE_RESERVATION {
		@Override
		public Operation createOperation(VacationManager managerPtr, Random randomPtr, int numQueryPerTransaction, int queryRange, boolean readOnly) {
			return new MakeReservationOperation(managerPtr, randomPtr, numQueryPerTransaction, queryRange, readOnly);
		}
	},
	
	ACTION_DELETE_CUSTOMER {
		@Override
		public Operation createOperation(VacationManager managerPtr, Random randomPtr, int numQueryPerTransaction, int queryRange, boolean readOnly) {
			return new DeleteCustomerOperation(managerPtr, randomPtr, queryRange);
		}
	},
	
	ACTION_UPDATE_TABLES {
		@Override
		public Operation createOperation(VacationManager managerPtr, Random randomPtr, int numQueryPerTransaction, int queryRange, boolean readOnly) {
			return new UpdateTablesOperation(managerPtr, randomPtr, numQueryPerTransaction, queryRange);
		}
	};

	public abstract Operation createOperation(VacationManager managerPtr, Random randomPtr, int numQueryPerTransaction, int queryRange, boolean readOnly);

}
