package thesis.alg;

import thesis.sim.DBRPState;
import thesis.sim.SimulTime;

import java.util.Random;

public class Algorithm {
    private Algorithm() {

    }

    public static DBRPState newRunPlan(DBRPState oldDbrp, SimulTime oldTime) {
        System.out.println("Starting planning...");
        DBRPState dbrp = new DBRPState(oldDbrp);
        SimulTime sTime = new SimulTime(oldTime);

        Demand demand = new Demand(dbrp);
        demand.calcDemands(sTime);
        demand.redistributeDemand(sTime);


        return dbrp;
    }
}
