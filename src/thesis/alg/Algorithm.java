package thesis.alg;

import thesis.sim.DBRPState;

import java.util.Random;

public class Algorithm {
    private Algorithm() {

    }

    public static DBRPState newRunPlan(DBRPState oldDbrp) {
        System.out.println("Redistributing...");
        DBRPState dbrp = new DBRPState(oldDbrp);
        // dbrp.redistributeDistricts(new Random());
        return dbrp;
    }
}
