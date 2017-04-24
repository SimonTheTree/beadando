/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameTools.state;

/**
 *
 * @author ganter
 */
public class FPSCounter extends Thread{
    private long lastTime;
    private long fps;

    @Override
    public void run(){
        while (true){
            lastTime = System.nanoTime();
            try{
                Thread.sleep(2000); // longer than one frame
            }
            catch (InterruptedException e){

            }
            long newFps = 1000000000 / (System.nanoTime() - lastTime); //one second(nano) divided by amount of time it takes for one frame to finish
            fps =(long) (fps * 0.99 + newFps * 0.01); //smooths fps
//            lastTime = System.nanoTime();
        }
    }
    public long fps(){
        return fps;
    }
    
}
