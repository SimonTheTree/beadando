/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameTools.state;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;

/**
 * This class can be used for playing, looping, and stopping an audiofile.
 * @see SoundManager
 * @author ganter
 */
public class Sound {

    public String name;
    public AudioClip sound;

    private Sound(){}

    /**
     * Constructor for Sound
     * @param s The sounds name
     * @param url The audiofiles location
     * @see Sound
     */
    public Sound(String s, URL url){
        this.name = s;
        sound = Applet.newAudioClip(url);
    }
    
    public void play(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(sound != null)
                    sound.play();
            }
        }).start();
    }
    
    public void loop(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(sound != null)
                    sound.loop();
            }
        }).start();
    }
    
    public void stop(){
        if(sound != null)
            sound.stop();
    }
    
}

