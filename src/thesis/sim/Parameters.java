package thesis.sim;
public class Parameters {
    public static final int        numDistricts =  11;      // amount of districts in the network
    public static final double     townSizeX    =   3.0;    // size of the network East to West, in kilometers
    public static final double     townSizeY    =   3.0;    // size of the network North to South, in kilometers
    public static final int        numHubs      = 800;      // amount of virtual hubs in the network
    public static final int        numVans      =   5;      // amount of redistribution vans
    public static final int        numBikes     = 900;      // amount of bikes in the network
    public static final double     vanSpeed     =  30;      // average speed of a distribution van, in km/h
    public static final double     bikeSpeed    =  11;      // average speed of a bike trip, in km/h
    public static final double     profitPerMin =   0.25;   // revenue per minute of bike rental
    public static final double     maxDeltaDist =   0.5;    // maximum difference between largest and smallest district (as a fraction of largest district)
    public static final double     planInterval =3600;      // new routeplan every X seconds
    public static final double     baseInterval =  10;      // an arrival every X seconds
    public static final double     progressionX =   1;      // progression through hours and days at what factor
    public static final double     maxWalkDist  =   0.150;  // maximum distance walked to a nearby bike, in kilometers. If there is no bike in this range, the revenue is lost.
    public static final double     demandShareD =   0.150;
    public static final int        hubCapacity  = 100;


    public static final double[]   factorHourWD = new double[]{ 3, 2, 1, 6,14,38,80,69,49,48,51,49,48,58,77,95,84,65,46,39,37,20,17,10};
    public static final double[]   factorHourWE = new double[]{19,15, 8, 6, 5, 8,13,33,53,73,81,81,82,77,79,78,66,54,40,35,32,21,21,17};
    public static final double[]   factorDay    = new double[]{13,14,15,14,16,16,12};
    public static       double     factorDistr(int distrRank) {
        return 1.0 / (2+distrRank);
    }
}
