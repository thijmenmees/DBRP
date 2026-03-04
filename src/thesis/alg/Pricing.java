package thesis.alg;
import thesis.sim.DBRPState;
import thesis.sim.Route;
public interface Pricing {
    void setState(DBRPState dbrp);

    void setDoubles(Doubles doubles);

    boolean getStop();

    void run();

    Route[] getRoutes();
}
