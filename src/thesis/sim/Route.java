package thesis.sim;

import java.util.LinkedList;

public class Route {
    private Trip[]    trips;
    public final int  length;
    private Van       startState;
    private SimulTime startTime;
    private double    totalprofit;
    private double    totalcost;
    public Route(Location[] locs, int[] deliveries, Van van, SimulTime start) {
        this.startState = van;
        this.length = locs.length;
        this.startTime = new SimulTime(start);

        // Create trips
        trips = new Trip[this.length];
        Location  previousLoc  = startState;
        SimulTime previousTime = startTime;
        for (int i = 0; i < this.length; i++) {
            trips[i] = new Trip();
            trips[i].setVanTrip(previousTime, previousLoc, locs[i], deliveries[i]);
            previousLoc = locs[i];
            previousTime = trips[i].getDestinationTime();
        }
    }

    public Route(LinkedList<Integer> locs, LinkedList<Integer> deliveries, int van, SimulTime start, DBRPState dbrp) {
        this.startState = dbrp.getVan(van);
        this.length = locs.size();
        this.startTime = new SimulTime(start);

        // Create trips
        trips = new Trip[this.length];
        Location  previousLoc  = startState;
        SimulTime previousTime = startTime;
        for (int i = 0; i < this.length; i++) {
            trips[i] = new Trip();
            trips[i].setVanTrip(previousTime, previousLoc, dbrp.getHubs()[locs.get(i)], deliveries.get(i));
            previousLoc = dbrp.getHubs()[locs.get(i)];
            previousTime = trips[i].getDestinationTime();
        }
    }

    public double getDistance() {
        double ret = 0.0;
        for (int i = 0; i < this.length; i++) {
            ret += trips[i].getDistance();
        }
        return ret;
    }

    public int[] getVisitsRow() {
        int[] ret = new int[Parameters.numHubs];
        for (int i = 0; i < this.length; i++) {
            int v = trips[i].getDestination().indexOf;
            ret[v] = 1;
        }
        return ret;
    }
}
