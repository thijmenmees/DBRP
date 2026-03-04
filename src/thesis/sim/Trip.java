package thesis.sim;

public class Trip {
    private SimulTime departureTime;
    private SimulTime destinationTime;
    private Location origin;
    private Location destination;
    private boolean isDepositTrip;
    private int load;
    public Trip() {

    }

    public void setBikeTrip(SimulTime departureTime, int o, int d, DBRPState dbrp) {
        this.departureTime = new SimulTime(departureTime);
        this.origin = dbrp.getHubs()[o];
        this.destination = dbrp.getHubs()[d];
        this.isDepositTrip = true;
        this.load = 1;
        double distance = dbrp.getDistance(o,d);
        double travelhours = distance / Parameters.bikeSpeed;
        this.destinationTime = new SimulTime(departureTime);
        this.destinationTime.progress(travelhours);
    }

    public SimulTime getDestinationTime() {
        return destinationTime;
    }

    public SimulTime getDepartureTime() {
        return departureTime;
    }

    public Location getOrigin() {
        return origin;
    }
    public Location getDestination() {
        return destination;
    }

    public boolean unload() {
        if (isDepositTrip) {
            boolean ret = true;
            for (int i = 0; i < load; i++) {
                ret = destination.deposit();
            }
            load = 0;
            return ret;
        }
        return true;
    }
}
