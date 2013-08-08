package pt.ist.fenixframework.vacation;

import com.arjuna.ats.arjuna.common.arjPropertyManager;
import com.arjuna.ats.internal.arjuna.objectstore.VolatileStore;

import pt.ist.fenixframework.CallableWithoutException;
import pt.ist.fenixframework.Config;
import pt.ist.fenixframework.DomainRoot;
import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.backend.infinispan.InfinispanConfig;
import pt.ist.fenixframework.vacation.domain.VacationManager;

public class VacationBenchmark {
	
    private final static int PARAM_DEFAULT_CLIENTS = 1;
    private final static int PARAM_DEFAULT_NUMBER = 10;
    private final static int PARAM_DEFAULT_QUERIES = 90;
    private final static int PARAM_DEFAULT_RELATIONS = 1 << 16;
    private final static int PARAM_DEFAULT_TRANSACTIONS = 1 << 26;
    private final static int PARAM_DEFAULT_USER = 80;

	static {
		// Set up transactional stores for JBoss TS
		arjPropertyManager.getCoordinatorEnvironmentBean().setCommunicationStore(VolatileStore.class.getName());
		arjPropertyManager.getObjectStoreEnvironmentBean().setObjectStoreType(VolatileStore.class.getName());
		arjPropertyManager.getCoordinatorEnvironmentBean().setDefaultTimeout(30000); //300 seconds == 5 min
	}
    
    public VacationBenchmark() { }

    public static void displayUsage(String appName) {
	System.out.println("Usage: %s [options]\n" + appName);
	System.out.println("\nOptions:                                             (defaults)\n");
	System.out.println("    c <UINT>   Number of [c]lients                   (%i)\n" + PARAM_DEFAULT_CLIENTS);
	System.out.println("    n <UINT>   [n]umber of user queries/transaction  (%i)\n" + PARAM_DEFAULT_NUMBER);
	System.out.println("    q <UINT>   Percentage of relations [q]ueried     (%i)\n" + PARAM_DEFAULT_QUERIES);
	System.out.println("    r <UINT>   Number of possible [r]elations        (%i)\n" + PARAM_DEFAULT_RELATIONS);
	System.out.println("    t <UINT>   Number of [t]ransactions              (%i)\n" + PARAM_DEFAULT_TRANSACTIONS);
	System.out.println("    u <UINT>   Percentage of [u]ser transactions     (%i)\n" + PARAM_DEFAULT_USER);
	System.exit(1);
    }

    public int CLIENTS;
    public int NUMBER;
    public int QUERIES;
    public int RELATIONS;
    public int TRANSACTIONS;
    public int USER;
    public int READ_ONLY_PERC;

    public void setDefaultParams() {
	CLIENTS = PARAM_DEFAULT_CLIENTS;
	NUMBER = PARAM_DEFAULT_NUMBER;
	QUERIES = PARAM_DEFAULT_QUERIES;
	RELATIONS = PARAM_DEFAULT_RELATIONS;
	TRANSACTIONS = PARAM_DEFAULT_TRANSACTIONS;
	USER = PARAM_DEFAULT_USER;
    }

    public void parseArgs(String argv[]) {
	int opterr = 0;

	setDefaultParams();
	for (int i = 0; i < argv.length; i++) {
	    String arg = argv[i];
	    if (arg.equals("-c"))
		CLIENTS = Integer.parseInt(argv[++i]);
	    else if (arg.equals("-n"))
		NUMBER = Integer.parseInt(argv[++i]);
	    else if (arg.equals("-q"))
		QUERIES = Integer.parseInt(argv[++i]);
	    else if (arg.equals("-r"))
		RELATIONS = Integer.parseInt(argv[++i]);
	    else if (arg.equals("-t"))
		TRANSACTIONS = Integer.parseInt(argv[++i]);
	    else if (arg.equals("-u"))
		USER = Integer.parseInt(argv[++i]);
	    else if (arg.equals("-ro"))
		READ_ONLY_PERC = Integer.parseInt(argv[++i]);
	    else
		opterr++;
	}

	if (opterr > 0) {
	    displayUsage(argv[0]);
	}
    }

    public boolean shouldDoSetup() {
	while (true) {
	    CallableWithoutException<Boolean> command = new CallableWithoutException<Boolean>() {
		public Boolean call() {
		    DomainRoot dr = FenixFramework.getDomainRoot();
		    VacationManager app = dr.getApplication();
		    if (app == null) {
			app = new VacationManager();
			dr.setApplication(app);
			app.setPopulated(false);
			return true;
		    } else {
			return false;
		    }
		}

	    };

	    try {
		return FenixFramework.getTransactionManager().withTransaction(command);
	    } catch (Exception e) {
	    }
	}
    }
    
    public VacationManager setup() {
	final boolean setup = shouldDoSetup();
	VacationManager result = null;
	
	CallableWithoutException<VacationManager> command = new CallableWithoutException<VacationManager>() {
	    public VacationManager call() {
		int i;
		int t;

		Random randomPtr = new Random();
		randomPtr.random_alloc();
		
		VacationManager managerPtr = FenixFramework.getDomainRoot().getApplication();

		if (!setup) {
		    return managerPtr;
		}
		
		int numRelation = RELATIONS;
		int ids[] = new int[numRelation];
		for (i = 0; i < numRelation; i++) {
		    ids[i] = i + 1;
		}

		for (t = 0; t < 4; t++) {

		    /* Shuffle ids */
		    for (i = 0; i < numRelation; i++) {
			int x = randomPtr.posrandom_generate() % numRelation;
			int y = randomPtr.posrandom_generate() % numRelation;
			int tmp = ids[x];
			ids[x] = ids[y];
			ids[y] = tmp;
		    }

		    /* Populate table */
		    for (i = 0; i < numRelation; i++) {
			boolean status = false;
			int id = ids[i];
			int num = ((randomPtr.posrandom_generate() % 5) + 1) * 100;
			int price = ((randomPtr.posrandom_generate() % 5) * 10) + 50;
			if (t == 0) {
			    status = managerPtr.setup_manager_addCar(id, num, price);
			} else if (t == 1) {
			    status = managerPtr.setup_manager_addFlight(id, num, price);
			} else if (t == 2) {
			    status = managerPtr.setup_manager_addRoom(id, num, price);
			} else if (t == 3) {
			    status = managerPtr.setup_manager_addCustomer(id);
			}
			assert (status);
		    }

		} /* for t */

		managerPtr.setPopulated(true);
		return managerPtr;
	    }
	};

	while (true) {
	    try {
		result = FenixFramework.getTransactionManager().withTransaction(command);
		break;
	    } catch (Exception e) {
		e.printStackTrace();
		try {
		    Thread.sleep(500);
		} catch (InterruptedException e1) {}
	    }
	}
	
	CallableWithoutException<Boolean> command2 = new CallableWithoutException<Boolean>() {
	    public Boolean call() {
		VacationManager managerPtr = FenixFramework.getDomainRoot().getApplication();
		return managerPtr.getPopulated();
	    }
	};
	
	
	while (true) {
	    try {
		if (FenixFramework.getTransactionManager().withTransaction(command2)) {
		    break;
		}
	    } catch (Exception e) {
		try {
		    Thread.sleep(500);
		} catch (InterruptedException e1) {}
	    }
	}
	
	return result;
	
    }

    public Client[] initializeClients(VacationManager manager) {
	Random randomPtr;
	Client clients[];
	int i;
	int numClient = CLIENTS;
	int numTransaction = TRANSACTIONS;
	int numQueryPerTransaction = NUMBER;
	int numRelation = RELATIONS;
	int percentQuery = QUERIES;
	int queryRange;
	int percentUser = USER;

	randomPtr = new Random();
	randomPtr.random_alloc();

	clients = new Client[numClient];

	// numTransactionPerClient = (int) ((double) numTransaction / (double) numClient + 0.5);
	queryRange = (int) (percentQuery / 100.0 * numRelation + 0.5);

	for (i = 0; i < numClient; i++) {
	    clients[i] = new Client(i, manager, numTransaction, numQueryPerTransaction, queryRange, percentUser, READ_ONLY_PERC);
	}

	return clients;
    }

    public static void main(String argv[]) throws InterruptedException {
	VacationManager manager;
	Client clients[];
	long start;
	long stop;

	Config mem = new InfinispanConfig() {{
	    domainModelURLs = resourcesToURLArray("vacation.dml");
	}};

	if (FenixFramework.isInitialized()) {
	    System.out.println("Framework automagically initialized!!!");
	} else {
	    System.out.println("Initializing Framework manually");
	    FenixFramework.initialize(mem);
	}

	/* Initialization */
	VacationBenchmark vac = new VacationBenchmark();
	vac.parseArgs(argv);
	manager = vac.setup();
	clients = vac.initializeClients(manager);
	int numThread = vac.CLIENTS;

	long steps = 0L;
	long aborts = 0L;
	
	start = System.currentTimeMillis();
	for (int i = 1; i < numThread; i++) {
	    clients[i].start();
	}
	clients[0].run();
	steps += clients[0].steps;
	aborts += clients[0].aborts;
	for (int i = 1; i < numThread; i++) {
	    clients[i].join();
	    steps += clients[i].steps;
	    aborts += clients[i].aborts;
	}

	stop = System.currentTimeMillis();

	long diff = stop - start;
	System.out.println(steps + " " + aborts);
	//manager.checkTables(vac);

	FenixFramework.shutdown();
    }

}
