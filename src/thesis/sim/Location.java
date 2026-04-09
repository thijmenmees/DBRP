package thesis.sim;

import java.util.Random;

public class Location {
    public double xCoord;
    public double yCoord;
    public int capacity;
    public int load;
    public int district;
    public int indexOf;
    public double demand;
    public double supply;
    public double demandratio;

    public Location(double xCoord, double yCoord, int capacity) {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.capacity = capacity;
        this.load = 0;
        this.district = -1;
        this.demand = 0.0;
        this.supply = 0.0;
        this.demandratio = 0.0;
    }

    public Location(double maxX, double maxY, int capacity, Random rand, int indexOf) {
        this.xCoord = rand.nextDouble() * maxX;
        this.yCoord = rand.nextDouble() * maxY;
        this.capacity = capacity;
        this.load = 0;
        this.indexOf = indexOf;
    }

    public Location(Location other) {
        this.xCoord = other.xCoord;
        this.yCoord = other.yCoord;
        this.capacity = other.capacity;
        this.load = other.load;
        this.district = other.district;
        this.demand = other.demand;
        this.supply = other.supply;
        this.indexOf = other.indexOf;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    public void setDemand(double demand) {
        this.demand = demand;
        calcDemandRatio();
    }

    public void setSupply(double supply) {
        this.supply = supply;
        calcDemandRatio();
    }

    public void calcDemandRatio() {
        if (demand != supply) {
            demandratio = load / (demand - supply);
        } else {
            demandratio = 1.0;
        }
    }

    public boolean deposit() {
        if (load == capacity) {
            return false;
        }
        load += 1;
        calcDemandRatio();
        return true;
    }

    public boolean draw() {
        if (load == 0) {
            return false;
        }
        load -= 1;
        calcDemandRatio();
        return true;
    }

    public double distanceTo(Location other) {
        double xdist = (this.xCoord - other.xCoord);
        double ydist = (this.yCoord - other.yCoord);
        return Math.sqrt(Math.pow(xdist, 2) + Math.pow(ydist, 2));
    }
}
