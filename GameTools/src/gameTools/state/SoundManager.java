/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameTools.state;

import java.util.ArrayList;

/**
 * This class makes handling audio files easy. Each soundclip has to be mapped
 * to it with a name(String) and a filename(URL).<br>
 * Afterwards the sound can played, looped, or stopped, using the methods 
 * playSound(name), loopSound(name), stopSound(name), stopAllSounds()<br>
 * It is possible to remove a sound: removeSound(name)
 * @author ganter
 * @see Sound
 * @see State
 */
public class SoundManager {
    
    private ArrayList<Sound> sounds = new ArrayList<>();
    
    public void addSound(Sound sound){
        sounds.add(sound);
    }
    
    public void removeSound(Sound sound){
        sounds.remove(sound);
    }
    
    public void playSound(String name){
        for(Sound s : sounds){
            if (name.equals(s.name))
                s.play();
        }
    }
    
    public void loopSound(String name){
        for(Sound s : sounds){
            if (name.equals(s.name))
                s.loop();
        }
    }
    
    public void stopSound(String name){
        for(Sound s : sounds){
            if (name.equals(s.name))
                s.stop();
        }
    }
    
    public void stopAllSounds(){
        for(Sound s : sounds){
            s.stop();
        }
    }
    
}
