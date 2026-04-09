package thesis.alg;
import thesis.sim.Route;
public interface MasterProblem {
    boolean initRoutes();

    boolean setDoubles(Doubles doubles);

    double optimize();

    boolean getStop();

    void addRoutes(Route[] pricingRoutes);
}
