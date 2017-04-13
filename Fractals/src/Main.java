import renderer.Renderer;
import ui.Window;

// Main application class. This is the routine called by the JVM to run the program.
public class Main {

    /** BOX_DIMENSION **/

    static int IMAGE_WIDTH = 800;
    static int IMAGE_HEIGHT = 600;

    /**
        Initial method. This is where the show begins.
     **/
    public static void main(String[] args){
        long tStart = System.currentTimeMillis();

        Window renderWindow = new Window(IMAGE_WIDTH, IMAGE_HEIGHT);

        Renderer renderer = new Renderer(renderWindow);

        renderer.renderScene();

        renderWindow.exportRendering(String.valueOf(stopTime(tStart)));
    }

    /**
        Stop the time for debug only
     **/
    private static double stopTime(long tStart){
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - tStart;
        return tDelta / 1000.0;
    }
}