package gameTools.map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *  egy cella megvizsgalasajoz szolgal vmien cellaspecifikus tulajdonsag alapjan
 * @author ganter
 * @param <C>
 */
public interface Tester<C extends Tile>{
    
    abstract boolean test(C c);
}
