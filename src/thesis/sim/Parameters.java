package thesis.sim;
public class Parameters {
    public static final int        numDistricts =  11;
    public static final double     townSizeX    =   3.0;
    public static final double     townSizeY    =   3.0;
    public static final int        numHubs      = 800;
    public static final int        numVans      =   5;
    public static final int        numBikes     = 900;
    public static final double     vanSpeed     =  30;
    public static final double     bikeSpeed    =  11;
    public static final double     profitPerMin =   0.25;
    public static final double     maxDeltaDist =   0.5;
    public static final double     planInterval =3600;      // new routeplan every X seconds
    public static final double     baseInterval =  10;      // an arrival every X seconds
    public static final double     progressionX =   1;      // progression through hours and days at what factor
    public static final double     maxWalkDist  =   0.150;
    public static final double     demandShareD =   0.150;
    public static final int        hubCapacity  = 100;


    public static final double[]   factorHourWD = new double[]{ 3, 2, 1, 6,14,38,80,69,49,48,51,49,48,58,77,95,84,65,46,39,37,20,17,10};
    public static final double[]   factorHourWE = new double[]{19,15, 8, 6, 5, 8,13,33,53,73,81,81,82,77,79,78,66,54,40,35,32,21,21,17};
    public static final double[]   factorDay    = new double[]{13,14,15,14,16,16,12};
    public static       double     factorDistr(int distrRank) {
        return 1.0 / (2+distrRank);
    }
}
