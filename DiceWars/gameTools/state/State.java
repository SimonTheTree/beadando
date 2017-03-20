/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gameTools.state;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final State THIS = this;
    private final Semaphore mutex = new Semaphore(1); //for rendering
    
    private volatile boolean running = false;
    protected long ticks = 0;
    
    private BufferedImage screen;
    protected Graphics2D g;
    
    private Thread renderThread, updateThread;
    public int maxFps = 100; //max fps
    public int maxTps = 100; //max ticks per second
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            do{
                try{
                    mutex.acquire();
                    update(THIS);
                } catch (InterruptedException ex) {
                } finally {
                  mutex.release();
                }
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
                try{
                    mutex.acquire();
                    render();
                } catch (InterruptedException ex) {
                } finally {
                  mutex.release();
                }
                SwingUtilities.invokeLater(() -> {
                        paintImmediately(0, 0, width, height);
                    }
                );
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
    
    public State(String s){
        this(s, 500, 500);
    }
    
    public State(String s, int width, int height){
        name = s;
        this.width = width;
        this.height = height;
        this.setPreferredSize(new Dimension(width, height));
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
    
    public void start(){
        
        if(!running){
            running = true;
            updateThread = new Thread(updateRunnable);
            renderThread = new Thread(renderRunnable);
            updateThread.start();
            renderThread.start();
        }
//        repaint();
    }
    
    public void stop(){
        running = false;
        soundManager.stopAllSounds();
    }
    
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
    * this method is called in every cycle in the States Thread. 
    * When extendig State, you propably want to overwrite this, and put your code here.
     * @param s
    * @param State is the state calling the update
    */
    public abstract void update(State s);
    
    /**
     * this method is called in every cycle in the States Thread. 
     * When extendig State, you propably want to overwrite this, and put your rendering code here.
     * <br>
     * Dont forget to call super.render() in the end in order to actually do the rendering.
     */
    public abstract void render();
    
    @Override
    public void paintComponent(Graphics g) {
        try{
            mutex.acquire();
            g.drawImage(screen, 0, 0, width, height, this);
        } catch (InterruptedException ex) {
        } finally {
          mutex.release();
        }
    }
      
}
