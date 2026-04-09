package thesis.alg;
import thesis.sim.DBRPState;
import thesis.sim.Parameters;
import thesis.sim.Route;

import java.util.ArrayList;

public class Labelling implements Pricing {
    private DBRPState dbrp;
    private Doubles duals;
    private Route[] routes;

    @Override
    public void setState(DBRPState dbrp) {
        this.dbrp = dbrp;
    }

    @Override
    public void setDoubles(Doubles doubles) {
        this.duals = doubles;
    }

    @Override
    public boolean getStop() {
        return false;
    }

    @Override
    public void run() {
        // initialize labels
        ArrayList<Label>[] label_;
        for (int v = 0; v < Parameters.numHubs; v++) {

        }
    }

    @Override
    public Route[] getRoutes() {
        return new Route[0];
    }
}
