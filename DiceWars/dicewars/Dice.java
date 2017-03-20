/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dicewars;

import java.util.Random;

/**
 *
 * @author ganter
 */
public class Dice {
    private static final Random RAND = new Random();
    
    /**
     * returns a random integers
     * @return int x | x€[1,6]€Z
     */
    public static int roll(){
        return RAND.nextInt(6)+1;
    }
    
    /**
     * returns an array of random integers
     * @return int[] x | x[i]€[1,6]€Z
     */
    public static int[] roll(int n){
        int[] r = new int[n];
        for(int i = 0; i < n; i++){
            r[i] = roll();
        }
        return r;
    }
    
}
