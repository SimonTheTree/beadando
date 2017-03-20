
/**
 * Constructor for StateManager.
 * This class handles a multiple of states, coordinating them, setting the 
 * currently active state, switching them out on  demand.
 * @param {Canvas} canvas the canvas to operate on.
 * @returns {StateManager}
 */
function StateManager(canvas) {
    
    var states  = [];
    
    var currentState;
    
    var canvas = canvas;
    var input = new InputManager(canvas);
    
    this.addState = function(state){
        state.canvas = canvas;
        state.input = input;
        states.push(state);
    };
    
    this.startCurrentState = function(){
            currentState.start();
    }
    
    this.stopCurrentState = function(){
            currentState.stop();
    }
    
    this.setCurrentState = function(s){
        for(var i = 0; i < states.length; i++){
            if(states[i].name == s){
                currentState = states[i];
            }
        }
    }
    
}