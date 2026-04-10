package thesis.sim;

import java.util.stream.DoubleStream;

public class Parameters {
    public static final int        numDistricts =  11;      // amount of districts in the network
    public static final int        businessDist =   3;      // number of districts that are part of central business district
    public static final int        recreateDist =   1;      // number of districts with nightlife and other recreation
    public static final double     expDirection =   0.5;    // fraction of traffic to be redirected in the expected direction
    // TODO check if expDirection is well implemented
    public static final double     townSizeX    =   3.0;    // size of the network East to West, in kilometers
    public static final double     townSizeY    =   3.0;    // size of the network North to South, in kilometers
    public static final int        numHubs      = 800;      // amount of virtual hubs in the network
    public static final int        numVans      =   5;      // amount of redistribution vans
    public static final int        numBikes     = 900;      // amount of bikes in the network
    public static final double     unloadTime   =   2/60;   // amount of hours it takes to unload a bike
    public static final double     vanSpeed     =  20;      // average speed of a distribution van, in km/h
    public static final double     bikeSpeed    =  11;      // average speed of a bike trip, in km/h
    public static final double     profitPerMin =   0.25;   // revenue per minute of bike rental
    public static final double     profitPerRide=   1.70;   // revenue per total bike rental
    public static final double     maxDeltaDist =   0.5;    // maximum difference between largest and smallest district (as a fraction of largest district)
    public static final double     progressionX =  30;      // progression through hours and days at what factor
    public static final double     planInterval = 600 / progressionX;      // new routeplan every X seconds
    public static final double     baseInterval =  38 / progressionX;      // an arrival every X seconds
    public static final double     maxWalkDist  =   0.150;  // maximum distance walked to a nearby bike, in kilometers. If there is no bike in this range, the revenue is lost.
    public static final double     demandShareD =   0.150;
    public static final int        hubCapacity  = 100;
    public static final int        simulWindow  =   2;      // number of weeks for the simulation to run


    public static final double[]   factorHourWD = new double[]{ 3, 2, 1, 6,14,38,80,69,49,48,51,49,48,58,77,95,84,65,46,39,37,20,17,10};
    public static final double[]   factorHourWE = new double[]{19,15, 8, 6, 5, 8,13,33,53,73,81,81,82,77,79,78,66,54,40,35,32,21,21,17};
    public static final double[]   factorDay    = new double[]{13,14,15,14,16,16,12};
    public static final double[]   fr_sa_recre  = new double[]{-1,-1,-1,-1,-1, 1, 1, 1, 1, 1, 1, 1, 1,-1,-1,-1,-1,-1, 1, 1, 1, 1, 1, 1};
    public static final double[]   week_commute = new double[]{-1,-1,-1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    public static       double     factorDistr(int distrRank) {
        return 1.0 / (2+distrRank);
    }
    public static       double     factorAggr(int distrRank, SimulTime simulTime, boolean isDeparture) {
        // Returns the total demand per hour for a district at specified simulation time.
        double baseDemandPerHour = factorBaseline(simulTime);

        double totalFreq = 0;
        for (int i = 0; i < numDistricts; i++) totalFreq += factorDepartureArrival(i, simulTime)[isDeparture ? 0 : 1];
        baseDemandPerHour *= factorDepartureArrival(distrRank, simulTime)[isDeparture ? 0 : 1] / totalFreq;

        return baseDemandPerHour;
    }
    public static       double[]   factorDepartureArrival(int distrRank, SimulTime simulTime) {
        // splits the district demand into departures and arrivals
        double totalFreqCenter = 0.0;
        double totalFreqOuter  = 0.0;
        double distrBaseline = factorDistr(distrRank);
        double retDeparture = distrBaseline;
        double retArrival   = distrBaseline;
        int centralDists = 0;
        double directionTowardsCenter = 0.0;
        if (simulTime.isWeekend()) {
            centralDists = recreateDist;
            directionTowardsCenter = fr_sa_recre[simulTime.getHour()];
        } else {
            centralDists = businessDist;
            directionTowardsCenter = week_commute[simulTime.getHour()];
        }
        for (int i = 0; i < centralDists; i++) totalFreqCenter += factorDistr(i);
        for (int i = centralDists; i < numDistricts; i++) totalFreqOuter += factorDistr(i);
        if (directionTowardsCenter < 0 && distrRank < centralDists) {
            // avondspits, centraal district
            retDeparture += -1 * directionTowardsCenter * expDirection * (distrBaseline / totalFreqCenter) * totalFreqOuter;
            retArrival   -= -1 * directionTowardsCenter * expDirection * distrBaseline;
        } else if (directionTowardsCenter < 0) {
            // avondspits, buitenwijk
            retDeparture -= -1 * directionTowardsCenter * expDirection * distrBaseline;
            retArrival   += -1 * directionTowardsCenter * expDirection * (distrBaseline / totalFreqOuter) * totalFreqCenter;
        } else if (distrRank < centralDists) {
            // ochtendspits, centraal district
            retDeparture -= directionTowardsCenter * expDirection * distrBaseline;
            retArrival   += directionTowardsCenter * expDirection * (distrBaseline / totalFreqCenter) * totalFreqOuter;
        } else {
            // ochtendspits, buitenwijk
            retDeparture += directionTowardsCenter * expDirection * (distrBaseline / totalFreqOuter) * totalFreqCenter;
            retArrival   -= directionTowardsCenter * expDirection * distrBaseline;
        }
        return new double[]{retDeparture, retArrival};
    }
    public static       double     factorBaseline(SimulTime simulTime) {
        double baseDemandPerHour = 3600.0 / baseInterval;
        double daysTotal = DoubleStream.of(factorDay).sum();
        baseDemandPerHour *= factorDay[simulTime.getDay()] / daysTotal * 7;
        if (simulTime.isWeekend()) {
            double hoursTotal = DoubleStream.of(factorHourWE).sum();
            baseDemandPerHour *= factorHourWE[simulTime.getHour()] / hoursTotal * 24;
        } else {
            double hoursTotal = DoubleStream.of(factorHourWD).sum();
            baseDemandPerHour *= factorHourWD[simulTime.getHour()] / hoursTotal * 24;
        }
        return baseDemandPerHour;
    }
}
