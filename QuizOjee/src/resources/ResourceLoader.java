package resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Put This Class into your Rescource folder packaged in the jar of your app.
 * calling getURL that way will default to that folder as "root"
 * @author ganter
 */
public class ResourceLoader {
    
    private static final ResourceLoader rl = new ResourceLoader();
    
    public static URL getURL(String fileName){
        URL url = rl.getClass().getResource(fileName);
        if (url == null) System.out.println("resource "+fileName+" not found");
        return url;
    }
    
    /**
     * Loads an image file from resources folder into a {@link BufferedImage}
     * @param fileName (resources/) path/to/img
     * @return
     */
    public static BufferedImage getImage(String fileName){
        try {
            return ImageIO.read(getURL(fileName));
        } catch (IOException | NullPointerException ex) {
            System.out.println("image "+fileName+" not found");
        }
        return null;
    }
    
}