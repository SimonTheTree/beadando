package resources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * Put This Class into your Rescource folder packaged in the jar of your app.
 * calling getURL that way will default to that folder as "root"
 * @author ganter
 */
public class ResourceLoader {
    
    private static final ResourceLoader rl = new ResourceLoader();
    
    public static URL getURL(String fileName) throws NullPointerException{
        URL url = rl.getClass().getResource(fileName);
        if (url == null){
        	throw new NullPointerException();
        };
        return url;
    }
    
    /**
     * Loads an image file from resources folder and creates a {@link File} 
     * with its url, and returns with that File instance.
     * @param fileName
     * @return the file, when file not found, null
     */
    public static File getFile(String fileName){
    	try {
    		System.out.print("loading resource "+fileName +"... ");
    		File f = new File(getURL(fileName).toURI());
            System.out.println("OK");
    		return f;
        } catch (NullPointerException ex) {
            System.out.println("resource "+fileName+" not found");
        } catch (URISyntaxException ex) {
			System.out.println("exception when loading file "+fileName);
		}
        return null;
    }
    
    /**
     * Loads an image file from resources folder into a {@link BufferedImage}
     * @param fileName (resources/) path/to/img
     * @return the image, when file not found, null
     */
    public static BufferedImage getImage(String fileName){
        try {
        	System.out.print("loading resource "+fileName +"... ");
        	BufferedImage i =ImageIO.read(getURL(fileName)); 
        	System.out.println("OK");            
        	return i;
        } catch (IOException ex) {
            System.out.println("exception when loading image "+fileName);
        } catch (NullPointerException ex) {
        	System.out.println("resource "+fileName+" not found");
		}
        return null;
    }
    
}