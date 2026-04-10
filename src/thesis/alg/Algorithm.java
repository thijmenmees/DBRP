package thesis.alg;

import thesis.sim.*;
import thesis.sim.Parameters;

import java.util.Random;

public class Algorithm {
    private Algorithm() {

    }

    public static DBRPState newRunPlan(DBRPState oldDbrp, SimulTime oldTime) {
        System.out.println("Starting planning...");
        DBRPState dbrp = new DBRPState(oldDbrp);
        dbrp.calcVanDist();
        SimulTime sTime = new SimulTime(oldTime);

        Demand demand = new Demand(dbrp);
        demand.calcDemands(sTime);
        demand.redistributeDemand(sTime);

        double[] s_vt = new double[Parameters.numHubs];
        double[] d_vt = new double[Parameters.numHubs];
        for (int v = 0; v < Parameters.numHubs; v++) {
            Location loc = dbrp.getHubs()[v];
            s_vt[v] = Math.max(0, Math.min(loc.supply + loc.load - loc.demand, loc.load));
            d_vt[v] = Math.max(0, Math.min(loc.demand - loc.supply - loc.load, Parameters.hubCapacity - loc.load));
        }
        MasterProblem mp = new MIP1();
        Pricing labelling = new Labelling();
        Doubles duals = new Doubles1();
        mp.initRoutes();
        labelling.setState(dbrp);
        labelling.setDoubles(duals);
        labelling.setSupplyDemand(s_vt, d_vt);
        mp.optimize();

        while (!mp.getStop()) {
            mp.setDoubles(duals);
            labelling.run();
            Route[] newRoutes = labelling.getRoutes();
            mp.addRoutes(newRoutes);
            mp.optimize();
        }

        return dbrp;
    }
}
