package thesis.alg;

import thesis.sim.DBRPState;
import thesis.sim.Location;
import thesis.sim.SimulTime;

import java.util.*;

public class Demand {
    private DBRPState dbrp;
    public Demand(DBRPState dbrp) {
        this.dbrp = dbrp;
    }

    public void calcDemands(SimulTime sTime) {
        double[] tempDemands = new double[thesis.sim.Parameters.numDistricts];
        double[] tempSupplys = new double[thesis.sim.Parameters.numDistricts];
        SimulTime demandTime = new SimulTime(sTime);
        for (int i = 0; i < Parameters.demandHorizon; i++) {
            for (int j = 0; j < thesis.sim.Parameters.numDistricts; j++) {
                tempDemands[j] += thesis.sim.Parameters.factorAggr(dbrp.getDistricts()[j].district, demandTime, true);
                tempSupplys[j] += thesis.sim.Parameters.factorAggr(dbrp.getDistricts()[j].district, demandTime, false);
            }
            demandTime.progress(0,0,1);
        }
        Location[] temphubs = dbrp.getHubs();
        for (int i = 0; i < thesis.sim.Parameters.numHubs; i++) {
            temphubs[i].demand  = tempDemands[temphubs[i].district] / dbrp.getDistsize(temphubs[i].district);
            temphubs[i].setSupply(tempSupplys[temphubs[i].district] / dbrp.getDistsize(temphubs[i].district));
        }
    }

    public void redistributeDemand(SimulTime sTime) {
        ArrayList<Location> pq = createPriorityQueue();
        for (int i = 0; i < pq.size(); i++) {
            Location current = pq.get(i);
            double oldPriority = current.demandratio;
            if (oldPriority < 0.0) { continue; }
            if (oldPriority >= 1.0) { break; }
            // 1. Process logic and update priority
            Location alternate = findAlternateBike(current);
            // 2. If the priority increased (moved "ahead"), relocate it
            if (current.demandratio > oldPriority) {
                System.out.println("Demand redistributed from " + current.indexOf + " to " + alternate.indexOf);
                System.out.println("Distance: " + dbrp.getDistance(current.indexOf, alternate.indexOf));
                // Remove from current position
                pq.remove(i);
                // Find the new sorted position (Binary Search is O(log n))
                int newIndex = Collections.binarySearch(pq, current,
                        Comparator.comparingDouble(n -> n.demandratio));
                // If not found, binarySearch returns (-(insertion point) - 1)
                if (newIndex < 0) {
                    newIndex = -(newIndex + 1);
                }
                // Re-insert at the new position
                pq.add(newIndex, current);
                i--; // Offset the loop's i++ to stay at the same effective slot

                // Relocate alternate hub
                pq.remove(alternate);
                int newIndex2 = Collections.binarySearch(pq, alternate,
                        Comparator.comparingDouble(n -> n.demandratio));
                if (newIndex2 < 0) {
                    newIndex2 = -(newIndex2 + 1);
                }
                pq.add(newIndex2, alternate);
            }
            // If priority decreased or stayed same, the loop continues normally
        }
    }

    private ArrayList<Location> createPriorityQueue() {
        ArrayList<Location> ret = new ArrayList<>();
        Location[] temphubs = dbrp.getHubs();
        for (int i = 0; i < thesis.sim.Parameters.numHubs; i++) {
            //if (temphubs[i].demandratio < 1.0) {
                ret.add(temphubs[i]);
            //}
        }
        Collections.sort(ret, Comparator.comparingDouble(o -> o.demandratio));
        return ret;
    }

    private Location findAlternateBike(Location loc) {
        LinkedList<Integer> nearbyHubs = dbrp.getNearbyHubs(loc.indexOf);
        Iterator<Integer> iter = nearbyHubs.iterator();
        Location nearestloc = null;
        while (iter.hasNext()) {
            int temphub = iter.next();
            Location nearbyloc = dbrp.getHubs()[temphub];
            if (nearbyloc.load < 1) {
                continue;
            } else if ( (nearbyloc.demand < nearbyloc.supply) && nearbyloc.load > 1 ) {
                nearestloc = nearbyloc;
                break;
            } else if ( (nearbyloc.demand > nearbyloc.supply) && ( (nearbyloc.load - 1) / (nearbyloc.demand - nearbyloc.supply) ) > loc.demandratio ) {
                nearestloc = nearbyloc;
                break;
            } else if ( nearbyloc.demand == nearbyloc.supply && nearbyloc.load > 0 ) {
                nearestloc = nearbyloc;
                break;
            }
        }
        if (nearestloc != null) {
            System.out.println("" + loc.indexOf + " to " + nearestloc.indexOf);
            nearestloc.draw();
            loc.deposit();
        }
        return nearestloc;
    }
}
