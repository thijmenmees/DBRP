package thesis.sim;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static thesis.alg.Algorithm.newRunPlan;

public class Simulation {
    // private volatile DrivePlan currentPlan = new DrivePlan();
    private volatile DBRPState dbrp = new DBRPState();

    private boolean running = true;
    private double simulatedTime = 0;
    private double nextPlanningTime = Parameters.planInterval;
    private double nextArrivalTime = getNextArrivalTime();
    private LinkedList<Trip> ongoingBikeTrips = new LinkedList<>();
    private SimulTime sTime = new SimulTime(0,0,0);
    private final ExecutorService plannerExecutor = Executors.newSingleThreadExecutor();
    public void start() {
        Thread simThread = new Thread(this::runLoop);
        simThread.setDaemon(true);
        simThread.start();
    }

    private void runLoop() {
        long lastTime = System.nanoTime();

        while (running) {

            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1e9;
            lastTime = now;

            update(deltaTime);

            try {
                Thread.sleep(16);
            } catch (InterruptedException ignored) {}
        }
    }

    private void update(double deltaTime) {
        simulatedTime += deltaTime; // in seconds
        sTime.setCustom(simulatedTime);

        // Trigger planner at interval
        if (simulatedTime >= nextPlanningTime) {
            requestNewPlan(); // runs Alg

            nextPlanningTime += Parameters.planInterval;
        }

        if (simulatedTime >= nextArrivalTime) {
            int dep = getDepartureDistrict(new Random());
            System.out.println("Departure at district " + dbrp.getDistricts()[dep].district);
            int hub1 = getDepartureLocation(dep, new Random());
            System.out.println("Departure at hub " + hub1);
            int hub2 = findClosestBike(hub1);
            System.out.println("Bike found at hub " + hub2);
            int arr = getDestinationDistrict(new Random());
            System.out.println("Arrival at district " + dbrp.getDistricts()[arr].district);
            int hub3 = getDestinationLocation(arr, new Random());
            System.out.println("Arrival at hub " + hub3);
            //moveBike(hub1, hub2, hub3); // should check whether there is actually a bike
            nextArrivalTime += getNextArrivalTime();
        }

        // Perform a simulation-step
        // simulate(dbrp, deltaTime);
    }

    private int getDepartureDistrict(Random rand) {
        double totalFreq = 0.0;
        for (int i = 0; i < Parameters.numDistricts; i++) {
            totalFreq += Parameters.factorDistr(i);
        }
        double randFreq = rand.nextDouble(totalFreq);
        double cumulFreq = 0.0;
        int ret = -1;
        while (cumulFreq < randFreq) {
            ret++;
            cumulFreq += Parameters.factorDistr(dbrp.getDistricts()[ret].district);
        }
        return ret;
    }

    private int getDepartureLocation(int district, Random rand) {
        LinkedList<Integer> possibleHubs = dbrp.getHubsInDistrict(district);
        int amountOfPossibleHubs = possibleHubs.size();
        int numberInList = rand.nextInt(amountOfPossibleHubs);
        return possibleHubs.get(numberInList);
    }

    private int findClosestBike(int hub) {
        LinkedList<Integer> nearbyHubs = dbrp.getNearbyHubs(hub);
        Iterator<Integer> iter = nearbyHubs.iterator();
        int origin = -1;
        while (iter.hasNext()) {
            int temphub = iter.next();
            if (dbrp.getHubs()[temphub].draw()) {
                origin = temphub;
                break;
            }
        }
        return origin;
    }

    private int getDestinationDistrict(Random rand) {
        return getDepartureDistrict(rand); // TODO aanpassen naar werkelijkheid
    }
    private int getDestinationLocation(int district, Random rand) {
        return getDepartureLocation(district, rand); // TODO aanpassen naar werkelijkheid
    }
    private double moveBike(int origin, int bikeLocation, int destination) {
        if (bikeLocation < 0) {
            double distance = dbrp.getDistance(origin, destination);
            double travelminutes = distance / Parameters.bikeSpeed * 60;
            double tripRevenue = travelminutes * Parameters.profitPerMin;
            return tripRevenue;
        }
        Trip newTrip = new Trip();
        newTrip.setBikeTrip(sTime, bikeLocation, destination, dbrp);
        Iterator<Trip> iter = ongoingBikeTrips.iterator();
        int

        return 0;
    }

    private double getNextArrivalTime() {
        return Parameters.baseInterval;
    }

    private void requestNewPlan() {
        plannerExecutor.submit(() -> {
            dbrp = newRunPlan(dbrp);
        });
    }

    public DBRPState getDbrp() {
        return dbrp;
    }
}
