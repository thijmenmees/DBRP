package thesis.alg;
import thesis.sim.Route;
public interface MasterProblem {
    void initRoutes();

    void setDoubles(Doubles doubles);

    void optimize();

    boolean getStop();

    void addRoutes(Route[] pricingRoutes);
}
