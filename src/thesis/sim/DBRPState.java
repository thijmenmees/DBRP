package thesis.sim;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class DBRPState {
    public final int numVans;
    public final int numHubs;
    private Van[] vans;
    private Location[] hubs;
    private Double[][] dist;
    private LinkedList<Integer>[] nearbyHubs;
    private Double[][] vanDist;
    private Location[] districts;
    private LinkedList<Integer>[] hubsInDistrict;
    private Double[][] distrDist;
    private int[] distsizes;

    public DBRPState() {
        this.numVans = Parameters.numVans;
        this.numHubs = Parameters.numHubs;
        vans = new Van[numVans];
        hubs = new Location[numHubs];
        dist = new Double[numHubs][numHubs];
        nearbyHubs = new LinkedList[numHubs];
        for (int i = 0; i < numHubs; i++) {
            nearbyHubs[i] = new LinkedList<>();
        }
        vanDist = new Double[numVans][numHubs];
        distrDist = new Double[Parameters.numDistricts][numHubs];
        districts = new Location[Parameters.numDistricts];
        hubsInDistrict = new LinkedList[Parameters.numDistricts];
        for (int i = 0; i < Parameters.numDistricts; i++) {
            hubsInDistrict[i] = new LinkedList<>();
        }
        distsizes = new int[Parameters.numDistricts];
        randomState(new Random(1));
    }

    public void randomState(Random rand) {
        // Generate locations
        for (int i = 0; i < Parameters.numDistricts; i++) {
            districts[i] = new Location(Parameters.townSizeX, Parameters.townSizeY, 0, rand, i);
        }
        for (int i = 0; i < Parameters.numHubs; i++) {
            hubs[i] = new Location(Parameters.townSizeX, Parameters.townSizeY, Parameters.hubCapacity, rand, i);
        }
        calcDistances();
        distributeBikes(rand);

        // Check sizes of districts
        calcDistrDist();
        calcDistrSizes();

        // Check whether sizes are distributed equally enough
        smallestDistr = 0;
        smallestSize  = distsizes[0];
        largestSize   = distsizes[0];
        for (int i = 1; i < Parameters.numDistricts; i++) {
            if (distsizes[i] > largestSize) largestSize = distsizes[i];
            if (distsizes[i] < smallestSize) {
                smallestSize = distsizes[i];
                smallestDistr = i;
            }
        }
        bestRatio = 1.0* smallestSize / largestSize;
        System.out.println("Smallest / Largest district: " + smallestSize + " / " + largestSize);
        System.out.println("Beste ratio: " + bestRatio);

        // Relocate smallest district until distribution is fair
        while (bestRatio < 1 - Parameters.maxDeltaDist) {
            redistributeDistricts(rand);
        }

        // Determine the districts centrality
        double[] distanceToCenter = new double[Parameters.numDistricts];
        Location center = new Location(Parameters.townSizeX/2, Parameters.townSizeY/2, 0);
        for (int i = 0; i < Parameters.numDistricts; i++) {
            distanceToCenter[i] = calcEuclid(center, districts[i]);
            districts[i].district = 0;
            for (int j = 0; j < i; j++) {
                if (distanceToCenter[j] < distanceToCenter[i]) {
                    districts[i].district++;
                } else {
                    districts[j].district++;
                }
            }
        }
    }

    private int smallestDistr;
    private int smallestSize;
    private int largestSize;
    private double bestRatio;

    public void redistributeDistricts(Random rand) {
        districts[smallestDistr] = new Location(Parameters.townSizeX, Parameters.townSizeY, 0, rand, smallestDistr);
        calcDistrDist(smallestDistr);
        calcDistrSizes();

        smallestDistr = 0;
        smallestSize  = distsizes[0];
        largestSize   = distsizes[0];
        for (int i = 1; i < Parameters.numDistricts; i++) {
            if (distsizes[i] > largestSize) largestSize = distsizes[i];
            if (distsizes[i] < smallestSize) {
                smallestSize = distsizes[i];
                smallestDistr = i;
            }
        }
        double thisRatio = 1.0* smallestSize / largestSize;
        bestRatio = Math.max(thisRatio, bestRatio);
        System.out.println("Smallest / Largest district: " + smallestSize + " / " + largestSize);
        System.out.println("Beste ratio: " + bestRatio);
    }

    public void setDistricts() {

    }

    public void calcDistances() {
        for (int i = 0; i < numHubs; i++) {
            // nearbyHubs[i].add(i);
            for (int j=0; j < numHubs; j++) {
                double tempdist = calcEuclid(hubs[i], hubs[j]);
                dist[i][j] = tempdist;

                // if distance is walkable, add to nearby hubs
                if (tempdist < Parameters.maxWalkDist) {
                    Iterator<Integer> iter = nearbyHubs[i].iterator();
                    int count = 0;
                    while (iter.hasNext()) {
                        int k = iter.next();
                        if (tempdist < dist[i][k]) {
                            break;
                        }
                        count++;
                    }
                    nearbyHubs[i].add(count, j);
                }
            }
        }
    }

    public void distributeBikes(Random rand) {
        for (int i = 0; i < Parameters.numBikes; i++) {
            int hub = rand.nextInt(Parameters.numHubs);
            while (!hubs[hub].deposit()) {
                hub = rand.nextInt(Parameters.numHubs);
            }
        }
    }

    public void calcVanDist() {
        for (int i = 0; i < numVans; i++) {
            for (int j = 0; j < numHubs; j++) {
                vanDist[i][j] = calcEuclid(vans[i], hubs[j]);
            }
        }
    }

    private void calcDistrDist() {
        for (int i = 0; i < Parameters.numDistricts; i++) {
            for (int j = 0; j < numHubs; j++) {
                distrDist[i][j] = calcEuclid(districts[i], hubs[j]);
            }
        }
    }

    private void calcDistrDist(int i) {
        for (int j = 0; j < numHubs; j++) {
            distrDist[i][j] = calcEuclid(districts[i], hubs[j]);
        }
    }

    private void calcDistrSizes() {
        for (int i = 0; i < Parameters.numDistricts; i++) {
            distsizes[i] = 0;
            hubsInDistrict[i].clear();
        }
        for (int i = 0; i < Parameters.numHubs; i++) {
            double minDist = distrDist[0][i];
            int minDistr = 0;
            for (int j = 1; j < Parameters.numDistricts; j++) {
                if (distrDist[j][i] < minDist) {
                    minDist = distrDist[j][i];
                    minDistr = j;
                }
            }
            distsizes[minDistr]++;
            hubs[i].setDistrict(minDistr);
            hubsInDistrict[minDistr].add(i);
        }
    }

    public int getDistsize(int i) {
        return distsizes[i];
    }

    private double calcEuclid(Location origin, Location destination) {
//        double xdist = (origin.xCoord - destination.xCoord);
//        double ydist = (origin.yCoord - destination.yCoord);
//        return Math.sqrt(Math.pow(xdist, 2) + Math.pow(ydist, 2));
        return origin.distanceTo(destination);
    }
    private double calcManhattan(Location origin, Location destination) {
        double xdist = Math.abs(origin.xCoord - destination.xCoord);
        double ydist = Math.abs(origin.yCoord - destination.yCoord);
        return xdist + ydist;
    }

    public Location[] getHubs() {
        return hubs;
    }

    public Van[] getVans() {
        return vans;
    }

    public Van getVan(int index) {
        return vans[index];
    }

    public Location[] getDistricts() {
        return districts;
    }

    public LinkedList<Integer> getNearbyHubs(int hub) {
        return nearbyHubs[hub];
    }

    public LinkedList<Integer> getHubsInDistrict(int district) {
        return hubsInDistrict[district];
    }
    public double getDistance(int origin, int destination) {
        return dist[origin][destination];
    }

    public double getVanDistance(int van, int destination) {
        return vanDist[van][destination];
    }

    public DBRPState(DBRPState other) {
        this.numVans = other.numVans;
        this.numHubs = other.numHubs;

        // Deep copy vans
        this.vans = new Van[numVans];
        for (int i = 0; i < numVans; i++) {
            this.vans[i] = (other.vans[i] != null) ? new Van(other.vans[i]) : null;
        }

        // Deep copy hubs
        this.hubs = new Location[numHubs];
        for (int i = 0; i < numHubs; i++) {
            this.hubs[i] = (other.hubs[i] != null) ? new Location(other.hubs[i]) : null;
        }

        // Deep copy districts
        this.districts = new Location[other.districts.length];
        for (int i = 0; i < districts.length; i++) {
            this.districts[i] = new Location(other.districts[i]);
        }

        // Deep copy matrices
        this.dist = new Double[numHubs][numHubs];
        for (int i = 0; i < numHubs; i++)
            this.dist[i] = other.dist[i].clone();

        this.nearbyHubs = new LinkedList[numHubs];
        for (int i = 0; i < numHubs; i++)
            this.nearbyHubs[i] = (LinkedList<Integer>) other.nearbyHubs[i].clone(); // TODO Stress test this

        this.hubsInDistrict = new LinkedList[Parameters.numDistricts];
        for (int i = 0; i < Parameters.numDistricts; i++)
            this.hubsInDistrict[i] = (LinkedList<Integer>) other.hubsInDistrict[i].clone();

        this.vanDist = new Double[numVans][numHubs];
        for (int i = 0; i < numVans; i++)
            this.vanDist[i] = other.vanDist[i].clone();

        this.distrDist = new Double[other.distrDist.length][numHubs];
        for (int i = 0; i < other.distrDist.length; i++)
            this.distrDist[i] = other.distrDist[i].clone();

        this.distsizes = other.distsizes.clone();

        this.smallestDistr = other.smallestDistr;
        this.smallestSize = other.smallestSize;
        this.largestSize = other.largestSize;
        this.bestRatio = other.bestRatio;
    }
}
