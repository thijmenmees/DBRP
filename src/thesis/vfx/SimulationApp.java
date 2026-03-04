package thesis.vfx;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import thesis.sim.DBRPState;
import thesis.sim.Simulation;

public class SimulationApp {

    /*
    @Override
    public void start(Stage stage) throws Exception {
        int screenWidth = (int) (thesis.vfx.Parameters.screenHeight / thesis.sim.Parameters.townSizeY * thesis.sim.Parameters.townSizeX);
        //Scene scene = new Scene(createContent(), screenWidth, thesis.vfx.Parameters.screenHeight);
        canvas = new Canvas(screenWidth, thesis.vfx.Parameters.screenHeight);
        Pane root = new Pane(canvas);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Delivery Simulation");
        stage.show();

        Draw.clear(canvas);
        initialDraw();
    }
     */
    public static void render(Canvas canvas, Simulation simul) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Draw.clear(canvas);
        Draw.hubs(gc, simul.getDbrp());
        Draw.districts(gc, simul.getDbrp());
        Draw.scalebar(canvas);
    }

    /*
    public void initialDraw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Draw.hubs(gc, dbrp);
    }\
     */
}
