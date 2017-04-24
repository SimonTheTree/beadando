package gameTools.state;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * This Class is useful for handling input in games. After instantiating the
 * user can map keys to it using the addMapping method. Every mapping has a 
 * name(String) and a value(KeyCode). The manager constantly listenes to the 
 * keyboard input via KeyListener. The user can check if a key has been pressed
 * using the isPressed method, passing it the name of the mapping.
 * @author ganter (idea from The Java Hub)
 * @see addMapping#addKeyMapping
 * @see isPressed#isKeyPressed
 * @see State
 */
public class InputManager
        implements KeyListener, MouseListener, MouseMotionListener
{
    
    public ArrayList<Key> keys = new ArrayList<>();
    public ArrayList<Click> clicks = new ArrayList<>();
    private int x,y;
    
    public InputManager(JPanel c){
        c.addKeyListener(this);
        c.addMouseListener(this);
        c.addMouseMotionListener(this);
    }
    
    /**
     * Registeres a keyMapping to listen to
     * @param s the name of the mapping
     * @param keyCode the keyCode to listen to
     */
    public void addKeyMapping(String s, int keyCode){
        keys.add(new Key(s, keyCode));
    }
    /**
     * Registeres a clickMapping to listen to
     * @param s the name of the mapping
     * @param keyCode the keyCode to listen to
     */
    public void addClickMapping(String s, int keyCode){
        clicks.add(new Click(s, keyCode));
    }
    
    /**
     * @param s the keyMappings name
     * @return true if the key is pressed and false if it is not. If the mapping is not registered, it will default to false.
     */
    public boolean isKeyPressed(String s){
        for(Key k : keys){
            if (s.equals(k.name)){
                return k.pressed;
            }
        }        
        return false;
    }
    public boolean isKeyTyped(String s){
        for(Key k : keys){
            if (s.equals(k.name)){
                boolean ret = k.typed;
                k.typed = false;
                return ret;
            }
        }
        return false;
    }

    public boolean isPressed(String s){
        for(Click k : clicks){
            if (s.equals(k.name)){
                return k.pressed;
            }
        }        
        return false;
    }
    public boolean isClicked(String s){
        for(Click k : clicks){
            if (s.equals(k.name)){
                boolean ret = k.clicked;
                k.clicked = false;
                return ret;
            }
        }        
        return false;
    }
    
    public Point getMousePos(){
        return new Point(x,y);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        for(Key k : keys){
            if (e.getKeyCode() == k.keyCode){
                k.togglePressed(true);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        for(Key k : keys){
            if (e.getKeyCode() == k.keyCode){
                k.togglePressed(false);
                k.typed = true;
            }
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for(Click k : clicks){
            if (e.getButton() == k.keyCode){
                k.togglePressed(true);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for(Click k : clicks){
            if (e.getButton() == k.keyCode){
                k.togglePressed(false);
                k.clicked = true;
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x=e.getX();
        y=e.getY();
    }
    
    private class Click{
        public String name;
        public int keyCode, pressCount;
        public boolean pressed;
        private boolean clicked = false;
        
        public Click(String name, int keyCode){
            this.name = name;
            this.keyCode = keyCode;
        }
        
        public void togglePressed(boolean t){
            if(pressed != t){
                pressed = t;
            }
            if(pressed){
                pressCount++;
            }
        }
    }
    
    private class Key {
        public String name;
        public int keyCode, pressCount;
        public boolean pressed = false;
        private boolean typed = false;

        public Key(String name, int keyCode){
            this.name = name;
            this.keyCode = keyCode;
        }

        public void togglePressed(boolean t){
            if(pressed != t){
                pressed = t;
            }
            if(pressed){
                pressCount++;
            }
        }
    }
}
