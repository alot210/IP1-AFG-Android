package renderer;

import ui.Window;
import utils.*;
import utils.algebra.Vec2;
import utils.io.Log;

import java.awt.image.BufferedImage;

public class Renderer {

    private BufferedImage mBufferedImage;

    private Window mRenderWindow;


    public Renderer(Window renderWindow){
        Log.print(this, "Init");
        mBufferedImage = renderWindow.getBufferedImage();
        mRenderWindow = renderWindow;
    }

    public void renderScene(){
        Log.print(this, "Start rendering");

        RgbColor boringBackground = RgbColor.RED;

        // Rows
        for (int y = 0; y < mBufferedImage.getHeight(); y++) {
            // Columns
            for (int x = 0; x < mBufferedImage.getWidth(); x++) {

                    mRenderWindow.setPixel(mBufferedImage, boringBackground, new Vec2(x,y));
            }
        }
    }
}
