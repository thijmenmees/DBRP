package thesis.alg;
import thesis.sim.DBRPState;
import thesis.sim.Parameters;
import thesis.sim.Route;
import thesis.sim.Van;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Labelling implements Pricing {
    private DBRPState dbrp;
    private Doubles duals;
    private double[] s_vt;
    private double[] d_vt;
    private Route[] routes;
    private List<Label>[] label_vi;

    @Override
    public void setState(DBRPState dbrp) {
        this.dbrp = dbrp;
    }

    @Override
    public void setDoubles(Doubles doubles) {
        this.duals = doubles;
    }

    @Override
    public void setSupplyDemand(double[] s_vt, double[] d_vt) {
        this.s_vt = s_vt;
        this.d_vt = d_vt;
    }

    @Override
    public boolean getStop() {
        return false;
    }

    @Override
    public void run(int vanindex) {
        Van van = dbrp.getVan(vanindex);
        // initialize labels
        label_vi = new List[Parameters.numHubs];
        Label startingpoint = new Label(-1, 0, 0, van.load, null);
        for (int v = 0; v < Parameters.numHubs; v++) {
            label_vi[v] = new ArrayList<Label>();
        }
        // loop labels

    }

    private void dfs(Label current) {
        int v = current.node;
        if (!addLabel(v, current)) {
            return; // pruned by dominance
        }
        for (int w = 0; w < Parameters.numHubs; w++) {
            if (current.isVisited(w)) { continue; } // prune loops
            Label next = extend(current, w);
            if (next.isFeasible()) {
                dfs(next);
            }
        }
    }

    private boolean addLabel(int node, Label newLabel) {
        // function to determine if label is dominated
        List<Label> labels = label_vi[node];
        Iterator<Label> it = labels.iterator();
        while (it.hasNext()) {
            Label existing = it.next();
            if (existing.dominates(newLabel)) {
                return false;
            } else if (newLabel.dominates(existing)) {
                it.remove();
            }
        }
        labels.add(newLabel);
        return true;
    }

    private Label extend(Label current, int w) {
        int pickups  = Math.min((int) s_vt[w], thesis.alg.Parameters.vanCapacity - current.load);
        int dropoffs = Math.min((int) Math.round(d_vt[w]), current.load);
        int loaddiff = pickups - dropoffs;
        double dist  = dbrp.getDistance(current.node, w);

        double time = current.time + dist / Parameters.vanSpeed + Math.abs(loaddiff) * Parameters.unloadTime;
        double rc   = current.rc   + Parameters.profitPerRide * Math.min(dropoffs, d_vt[w]) - duals.getNodeDoubles()[w] - thesis.alg.Parameters.costPerVanKm * dist;
        int    load = current.load + loaddiff;
        Label  ret  = new Label(w, time, rc, load, current);
        return ret;
    }

    @Override
    public Route[] getRoutes() {
        return new Route[0];
    }
}
