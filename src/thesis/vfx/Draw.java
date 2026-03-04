package thesis.vfx;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import thesis.sim.DBRPState;
import thesis.sim.Location;
import thesis.sim.Parameters;

public final class Draw {
    private Draw() {
        // Static class
    }

    public static void clear(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public static void hubs(GraphicsContext gc, DBRPState dbrp) {
        for (Location loc : dbrp.getHubs()) {
            double hubkleur = 0.8 * loc.district / Parameters.numDistricts + 0.1;
            double hubkleur2 = (1.0 * (loc.district % 3))/2;
            gc.setFill(Color.color(hubkleur,hubkleur2,1));
            gc.fillOval(scaleX(loc.xCoord) - 5, scaleX(loc.yCoord) - 5, 10, 10);
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(loc.load), scaleX(loc.xCoord) - 5, scaleX(loc.yCoord) + 5);
        }
    }

    public static void districts(GraphicsContext gc, DBRPState dbrp) {
        gc.setFill(Color.RED);
        gc.setStroke(Color.BLACK);
        for (Location loc : dbrp.getDistricts()) {
            gc.fillOval(scaleX(loc.xCoord) - 5, scaleX(loc.yCoord) - 5, 10, 10);
            gc.strokeText(String.valueOf(loc.district), scaleX(loc.xCoord) - 5, scaleX(loc.yCoord) + 5);
        }
    }

    public static void scalebar(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(canvas.getWidth()-scaleX(2)-10, canvas.getHeight()-20, scaleX(2), 10);
        gc.setFill(Color.WHITE);
        gc.fillRect(canvas.getWidth()-scaleX(2)-9, canvas.getHeight()-19, scaleX(1)-1, 8);
    }

    private static double scaleX(double coord) {
        return coord * thesis.vfx.Parameters.screenHeight / Parameters.townSizeY;
    }
}
