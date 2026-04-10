package thesis.alg;
import thesis.sim.DBRPState;
import thesis.sim.Route;
public interface Pricing {
    void setState(DBRPState dbrp);

    void setDoubles(Doubles doubles);

    void setSupplyDemand(double[] s_vt, double[] d_vt);

    boolean getStop();

    void run(int vanIndex);

    Route[] getRoutes();
}
