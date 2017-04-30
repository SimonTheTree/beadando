package gameTools.state;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Semaphore;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
/**
 * <p>This Abstract Class represents a graphical state in a game. for example: menu state, game state, settings state...</p>
 * <p></p>
 * @param name The states name
 * @param inputManager Each state comes with its own InputManager. This is it.
 * @param soundManager Each state comes with its own SoundManager. This is it. 
 * All sounds stop playing when states change.
 * @author ganter (idea from The Java Hub)
 * @see InputManager
 * @see SoundManager
 * @see StateManager
 */
public abstract class State extends JPanel{
//    private final Semaphore mutex = new Semaphore(1); //for rendering
    
    private volatile boolean running = false;
    protected long ticks = 0;
    
    protected BufferedImage screen;
    protected final ScreenLock screenLock = new ScreenLock();
    static class ScreenLock{};
    protected Graphics2D g;
    
    private Thread renderThread, updateThread;
    public int maxFps = 60; //max fps
    public int maxTps = 100; //max ticks per second
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            do{
                update(); 
                ticks++;
                tpsCounter.interrupt();
                
                //handle Tps
                try {
                    Thread.sleep(1000/maxTps);
                } catch (InterruptedException ex) {}
            }while(running);
        }
    };    
    private final Runnable renderRunnable = new Runnable() {
        @Override
        public void run() {
            do{
                synchronized (screenLock) {
                	render();					
				}
                try {
					SwingUtilities.invokeAndWait(() -> {paintImmediately(0, 0, width, height);});
				} catch (InvocationTargetException | InterruptedException e) {
					e.printStackTrace();
				}
                fpsCounter.interrupt(); 
                
                //handle max fps
                if (fpsCounter.fps() > maxFps){
                    try {
                        Thread.sleep(1000/(maxFps));
                    } catch (InterruptedException ex) {}
                }
            }while(running);
        }
    };
    
    public InputManager inputManager = new InputManager(this);
    public SoundManager soundManager = new SoundManager();
    public FPSCounter fpsCounter = new FPSCounter();
    public FPSCounter tpsCounter = new FPSCounter();
    
    public String name;
    protected int width, height;
    
    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
    
    public State(){
    	this("unnamed");
    }
    
    public State(String s){
        this(s, 500, 500);
    }
    
    public State(String s, int width, int height){
        name = s;
        this.width = width;
        this.height = height;
        this.setPreferredSize(new Dimension(width, height));
        this.setSize(new Dimension(width, height));
        running=false;
        setDoubleBuffered(true);
        createNewGraphics();
        fpsCounter.start();
        tpsCounter.start();
    }
    
    private void createNewGraphics(){
        screen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g = screen.createGraphics();
    }
    
    public final void start(){
    	onStart();
        if(!running){
            running = true;
            updateThread = new Thread(updateRunnable);
            updateThread.setName(this.name+"-update-cycle");
            renderThread = new Thread(renderRunnable);
            renderThread.setName(this.name+"-render-cycle");
            updateThread.start();
            renderThread.start();
        }
    }
    
    public final void stop(){
        running = false;
        soundManager.stopAllSounds();
        onStop();
    }
    
    /**
     * This method will be called every time before the State gets started
     */
    protected void onStart(){}
    /**
     * This method will be called every time after the State was stopped
     */
    protected void onStop(){}
    
    public Graphics2D getGraphics2D(){
        return g;
    }

    public boolean isRunning() {
        return running;
    }

    public long getTicks() {
        return ticks;
    }
    
    /**
    * this method is called repeatedly, over and over in every cycle in a separate Thread. 
    * When extendig State, you propably want to overwrite this, and put your code here.
    * @param s is the state calling the update
    */
    public abstract void update();
    
    /**
     * this method is called repeatedly, over and over in every cycle in a separate Thread. 
     * When extendig State, you propably want to overwrite this, and put your rendering code here.
     * <br>
     * Dont forget to call super.render() in the end in order to actually do the rendering.
     */
    public void render(){
    	g.setColor(new Color(0xdd, 0xdd, 0xff));
		g.fillRect(0, 0, getWidth(), getHeight());
    };
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        synchronized (screenLock) {
        	//draws the screen image(that which the render function painted on) onto the panel
        	g.drawImage(screen, 0, 0, width, height, this);				
		};
        
    }
      
}
