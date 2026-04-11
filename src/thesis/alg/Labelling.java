package thesis.alg;
import thesis.sim.*;
import thesis.sim.Parameters;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Labelling implements Pricing {
    private DBRPState dbrp;
    private SimulTime startTime;
    private Doubles duals;
    private double[] s_vt;
    private double[] d_vt;
    private Route route;
    private List<Label>[] label_vi;
    private Label bestLabel;
    private int vanIndex;
    private double bestProfit;

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
    public void setStartTime(SimulTime simulTime) {
        this.startTime = simulTime;
    }

    @Override
    public boolean getStop() {
        return false;
    }

    @Override
    public void run(int vanIndex) {
        this.vanIndex = vanIndex;
        Van van = dbrp.getVan(vanIndex);
        // reset trackers
        bestLabel = null;
        bestProfit = Double.NEGATIVE_INFINITY;
        route = null;
        // initialize labels
        label_vi = new List[Parameters.numHubs];
        Label startingpoint = new Label(-1, 0, 0, van.load);
        for (int v = 0; v < Parameters.numHubs; v++) {
            label_vi[v] = new ArrayList<Label>();
        }
        // loop labels
        firstDfs(startingpoint, vanIndex);
    }

    private void firstDfs(Label vanLabel, int vanIndex) {
        for (int w = 0; w < Parameters.numHubs; w++) {
            Label next = firstExtend(vanLabel, vanIndex, w);
            if (next.isFeasible()) {
                dfs(next);
            }
        }
    }

    private void dfs(Label current) {
        int v = current.node;
        if (!addLabel(v, current)) {
            return; // pruned by dominance
        }
        if (current.rc > bestProfit) {
            bestProfit = current.rc;
            bestLabel = current;
            route = makeRoute(current, vanIndex, startTime);
        } else if (current.rc + estRemainingRc(current) <= bestProfit) {
            return; // pruned by bounding
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

    private Label firstExtend(Label current, int vanIndex, int w) {
        int pickups  = Math.min((int) s_vt[w], thesis.alg.Parameters.vanCapacity - current.load);
        int dropoffs = Math.min((int) Math.round(d_vt[w]), current.load);
        int loaddiff = pickups - dropoffs;
        double dist  = dbrp.getVanDistance(vanIndex, w);

        double time = current.time + dist / Parameters.vanSpeed + Math.abs(loaddiff) * Parameters.unloadTime;
        double rc   = current.rc   + Parameters.profitPerRide * Math.min(dropoffs, d_vt[w]) - duals.getNodeDoubles()[w] - thesis.alg.Parameters.costPerVanKm * dist;
        int    load = current.load + loaddiff;
        Label  ret  = new Label(w, time, rc, load, current);
        return ret;
    }

    private double estRemainingRc(Label current) {
        double timeLeft = thesis.alg.Parameters.labelHorizon - current.time;
        int loadsLeft = (int) (timeLeft / Parameters.unloadTime);
        int fastProfits = Math.min(loadsLeft, current.load);
        int slowProfits = (loadsLeft - fastProfits) / 2;
        return (fastProfits + slowProfits) * Parameters.profitPerRide;
    }

    private Route makeRoute(Label label, int vanIndex, SimulTime simulTime) {
        LinkedList<Integer> path = new LinkedList<>();
        LinkedList<Integer> deliveries = new LinkedList<>();
        int nextLoad = label.load;
        while (label.prev != null) {
            path.addFirst(label.node);
            label = label.prev;
            deliveries.addFirst(label.load - nextLoad);
        }

        Route ret = new Route(path, deliveries, vanIndex, simulTime, dbrp);
        return ret;
    }

    @Override
    public Route getRoute() {
        return route;
    }
}
