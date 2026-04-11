package thesis.alg;
import thesis.sim.DBRPState;
import thesis.sim.Route;
import thesis.sim.SimulTime;

public interface Pricing {
    void setState(DBRPState dbrp);

    void setDoubles(Doubles doubles);
    void setStartTime(SimulTime simulTime);

    void setSupplyDemand(double[] s_vt, double[] d_vt);

    boolean getStop();

    void run(int vanIndex);

    Route getRoute();
}
