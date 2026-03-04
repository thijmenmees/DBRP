import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import thesis.sim.*;
import thesis.alg.MasterProblem;
import thesis.alg.MIP1;
import thesis.alg.Doubles;
import thesis.alg.Doubles1;
import thesis.alg.Pricing;
import thesis.alg.Labelling;
import thesis.vfx.SimulationApp;

public class Main extends Application {
    private Simulation simul;
    private Canvas canvas;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        simul = new Simulation();
        simul.start();

        int screenWidth = (int) (thesis.vfx.Parameters.screenHeight / thesis.sim.Parameters.townSizeY * thesis.sim.Parameters.townSizeX);
        canvas = new Canvas(screenWidth, thesis.vfx.Parameters.screenHeight);

        StackPane root = new StackPane(canvas);
        stage.setScene(new Scene(root));
        stage.setTitle("Threaded Simulation Demo");
        stage.show();
        startRenderLoop();
    }

    private void startRenderLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                SimulationApp.render(canvas, simul);
            }
        }.start();
    }
    /*
    public static void main(String[] args) {
        Random rand = new Random(1);
        DBRPState dbrp = new DBRPState(Parameters.numVans, Parameters.numHubs);
        dbrp.randomState(rand);
        SimulationApp simul = new SimulationApp(dbrp);
        simul.visualize();
        //simul.initialDraw();
    }
    */
    /*
    public static void main(String[] args) {
        Random rand = new Random(1);
        DBRPState dbrp = new DBRPState(5, 100);
        dbrp.randomState(rand);
        dbrp.calcDistances();
        Simulation sim = new Simulation();
        sim.setState(dbrp);
        SimulTime t = new SimulTime(0,0,0);
        sim.setTime(t);
        SimulTime endOfSimulation = new SimulTime(2,0,0);
        Route[] currentRoutes = new Route[dbrp.numVans];
        while (t.isBefore(endOfSimulation)) {
            sim.setDemand();
            dbrp.calcVanDist();
            MasterProblem mp = new MIP1();
            Doubles doubles = new Doubles1();
            mp.initRoutes();
            mp.setDoubles(doubles);
            mp.optimize();
            Pricing pp = new Labelling();
            pp.setState(dbrp);
            pp.setDoubles(doubles);
            while (!mp.getStop()) {
                pp.run();
                if (pp.getStop()) {
                    break;
                }
                Route[] pricingRoutes = pp.getRoutes();
                mp.addRoutes(pricingRoutes);
                mp.optimize();
            }

            sim.run(0,0,1);
            t.progress(0,0,1);
        }
    }
     */
}