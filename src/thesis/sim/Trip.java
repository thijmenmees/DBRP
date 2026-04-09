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

    public void setVanTrip(SimulTime departureTime, Location o, Location d, int delivery) {
        this.departureTime = new SimulTime(departureTime);
        this.origin = o;
        this.destination = d;
        this.isDepositTrip = true;
        if (delivery < 0) { this.isDepositTrip = false; }
        this.load = delivery; // negative load for pickup trips
        double distance = o.distanceTo(d);
        double travelhours = distance / Parameters.vanSpeed;
        // TODO travelhours += Math.abs(delivery) * Parameters.unloadTime // in hours
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
        boolean ret = true;
        if (isDepositTrip) {
            for (int i = 0; i < load; i++) {
                ret = destination.deposit();
            }
        } else {
            for (int i = 0; i > load; i--) {
                ret = destination.draw();
            }
        }
        load = 0;
        return ret;
    }
}
