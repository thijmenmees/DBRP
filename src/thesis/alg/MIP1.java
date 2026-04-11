package thesis.alg;
import ilog.concert.*;
import ilog.cplex.IloCplex;
import thesis.sim.Parameters;
import thesis.sim.Route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;


public class MIP1 implements MasterProblem {
    private IloCplex cplex;
    private ArrayList<IloNumVar>[] x_nr;
    private ArrayList<Double>[]    p_nr;
    private ArrayList<Double>[]    c_nr;
    private ArrayList<int[]>[]     A_nrv;
    private ArrayList<Route>[]     route_nr;

    private IloNumExpr target;
    private IloObjective trgt;
    private IloNumExpr[]  c2L;
    private IloNumExpr[]  c2R;
    private IloRange[] c2;
    private IloNumExpr[]  c3L;
    private IloNumExpr[]  c3R;
    private IloRange[] c3;
    private int pricingcount;

    @Override
    public boolean initRoutes() {
        try {
            cplex = new IloCplex();

            // Init decision vars
            x_nr  = new ArrayList[Parameters.numVans];
            p_nr  = new ArrayList[Parameters.numVans];
            c_nr  = new ArrayList[Parameters.numVans];
            A_nrv = new ArrayList[Parameters.numVans];
            route_nr = new ArrayList[Parameters.numVans];
            for (int n = 0; n < Parameters.numVans; n++) {
                x_nr [n] = new ArrayList<IloNumVar>();
                p_nr [n] = new ArrayList<Double>();
                c_nr [n] = new ArrayList<Double>();
                A_nrv[n] = new ArrayList<int[]>();
                route_nr[n] = new ArrayList<Route>();
                x_nr [n].add(cplex.numVar(0, 1, "x_" + n + "_" + 0));
                p_nr [n].add(0.0);
                c_nr [n].add(0.0);
                A_nrv[n].add(new int[Parameters.numHubs]);
                route_nr[n].add(null);
            }

            // Init MIP
            // Maximization target
            target = cplex.constant(0);
            for (int n = 0; n < Parameters.numVans; n++) {
                target = cplex.sum(target, cplex.prod( p_nr[n].get(0) - c_nr[n].get(0), x_nr[n].get(0) ) );
            }
            trgt = cplex.addMaximize(target);

            // Constraint 2
            c2L = new IloNumExpr[Parameters.numVans];
            c2 = new IloRange[Parameters.numVans];
            for (int n = 0; n < Parameters.numVans; n++) {
                c2L[n] = x_nr[n].get(0);
                // c2R[n] = cplex.constant(1);
                c2[n] = cplex.addLe(c2L[n], 1, "c2_" + n);
            }

            // Constraint 3
            c3L = new IloNumExpr[Parameters.numHubs];
            c3 = new IloRange[Parameters.numHubs];
            for (int v = 0; v < Parameters.numHubs; v++) {
                int r = 0;
                c3L[v] = cplex.constant(0);
                for (int n = 0; n < Parameters.numVans; n++) {
                    if ( A_nrv[n].get(r)[v] == 1 ) {
                        c3L[v] = cplex.sum(c3L[v], x_nr[n].get(r));
                    }
                }
                c3[v] = cplex.addLe(c3L[v], 1, "c3_" + v);
            }
            pricingcount = 0;
            return true;
        } catch (IloException e) {
            System.out.print(e.getStackTrace());
            return false;
        }
    }

    @Override
    public boolean setDoubles(Doubles doubles) {
        try {
            Double[] vanDual = new Double[Parameters.numVans];
            Double[] hubDual = new Double[Parameters.numHubs];
            for (int n = 0; n < Parameters.numVans; n++) {
                vanDual[n] = cplex.getDual(c2[n]);
            }
            for (int v = 0; v < Parameters.numHubs; v++) {
                hubDual[v] = cplex.getDual(c3[v]);
            }
            doubles.setVanDoubles(vanDual);
            doubles.setNodeDoubles(hubDual);
            return true;
        } catch (IloException e) {
            System.out.print(e.getStackTrace());
            return false;
        }
    }

    @Override
    public double optimize() {
        try {
            cplex.solve();
            System.out.println(" --- SOLUTION: "+cplex.getObjValue());
            System.out.println(" --- ");
            return cplex.getObjValue();
        } catch (IloException e) {
            System.out.print(e.getStackTrace());
            return -1;
        }
    }

    @Override
    public boolean getStop() {
        pricingcount ++;
        if (pricingcount > thesis.alg.Parameters.maxPricings) {
            return true;
        }
        return false;
    }

    @Override
    public void addRoutes(Route[] pricingRoutes) {
        try {
            boolean hasNewRoute = false;
            // Remove old objective
            cplex.remove(trgt);
            for (int n = 0; n < Parameters.numVans; n++) {
                // Add to routes log
                if (Objects.isNull(pricingRoutes[n])) {
                    continue;
                }
                hasNewRoute = true;
                int r = x_nr[n].size();
                route_nr[n].add(pricingRoutes[n]);

                // Create decision variable x_nr
                x_nr[n].add(cplex.numVar(0, 1, "x_" + n + "_" + r));
                // TODO Add route profitability p_nr
                // Add route costs c_nr
                c_nr[n].add(pricingRoutes[n].getDistance() * thesis.alg.Parameters.costPerVanKm);
                // Construct row for binary visits matrix A_nrv
                A_nrv[n].add(pricingRoutes[n].getVisitsRow());

                // Update objective
                target = cplex.sum(target, cplex.prod( p_nr[n].get(r) - c_nr[n].get(r), x_nr[n].get(r) ) );
                // Change constraints
                cplex.remove(c2[n]);
                c2L[n] = cplex.sum(c2L[n], x_nr[n].get(r));
                cplex.addLe(c2L[n], 1, "c2_" + n);
                for (int v = 0; v < Parameters.numHubs; v++) {
                    if (A_nrv[n].get(r)[v] == 1) {
                        cplex.remove(c3[v]);
                        c3L[v] = cplex.sum(c3L[v], x_nr[n].get(r));
                        c3[v] = cplex.addLe(c3L[v], 1, "c3_" + v);
                    }
                }
            }
            // Reinstate objective
            trgt = cplex.addMaximize(target);
            if (!hasNewRoute) {
                pricingcount = thesis.alg.Parameters.maxPricings; // No new route added, stop CG
            }
        } catch (IloException e) {
            System.out.print(e.getStackTrace());
        }
    }
}
