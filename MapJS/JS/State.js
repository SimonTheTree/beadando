/**
 * constructor function for (meant to be abstract) State.
 * A state is a complex object representing some state in a game eg: gamestate, 
 * menustate, and so on, think of it as a set of what is shown on screen, what 
 * user input to react to and so on.<br>
 * You need to first create a StateManager, and create some custom states 
 * inheriting form State and add states to it for this to work.<br>
 * ToDo when inheriting:
 * <ul>
 *   <li>overwrite function this.render: this function is called periodically, 
 *   doing the rendering stuff, it recieves one argument, which is a 
 *   canvas.getContext("2d") object to draw on </li>
 *   <li>overwrite function this.run: this function is called periodically, it 
 *   should handle logical work, such ay handling user input, and game logic</li>
 *   <li>fpsCounter is an FpsCounter counting the fps (how many times render(ctx)
 *   is called per second</li>
 *   <li>tpsCounter is an FpsCounter counting the ticks per secoond (how many 
 *   times run()*   is called per second</li>
 *   <li>maxFps, maxTps: there is a mechanism delaying the fps and tps, 
 *   constraining it when it is about to exceed maxFps/maxTps, so that the CPU 
 *   is not overworked unnescessarily.</li>
 *   <li>name: a string with the States name(id)</li>
 *   <li>running: when running true, otherwise false</li>
 *   <li>tick: number of run()-s since creation</li>
 *   <li>canvas: is set by the StateManager, the canvas the State operates on.</li>
 * </ul>
 * @param {String} name
 * @returns {State}
 */
function State(name) {
    this.name = name
    this.running = false;
    this.tick = 0;
    this.rEngine = null;
    this.gEngine = null;
    this.maxFps = 60;
    this.maxTps = 200;
    this.fpsCounter;
    this.tpsCounter;
    this.canvas;
    this.input;

    this.run = function () {
    };
    this.render = function (ctx) {
    };

    this.runEngine = function () {
        this.tpsCounter.update();
        this.run();
        this.tick++;
        //handle max fps
        if (this.tpsCounter.fps() > this.maxTps) {
            clearInterval(this.rEngine);
            setTimeout(this.startRunEngine(), 1000 / this.maxTps);
        }
    };
    this.graphicsEngine = function () {
        this.fpsCounter.update();

        this.render(this.canvas.getContext("2d"));
        //handle max fps
        if (this.fpsCounter.fps() > this.maxFps) {
            clearInterval(this.gEngine);
            setTimeout(this.startGraphicsEngine(), 1000 / this.maxFps);
        }
    };

    this.start = function () {
        this.running = true;
        this.fpsCounter = new FpsCounter();
        this.tpsCounter = new FpsCounter();
        this.startGraphicsEngine();
        this.startRunEngine();

    };
    this.stop = function () {
        this.running = false;
        clearInterval(this.rEngine);
        clearInterval(this.gEngine);
    };

    this.startRunEngine = function () {
        this.rEngine = setInterval(
                (function (self) {         //Self-executing func which takes 'this' as self
                    return function () {
                        self.runEngine();
                    }
                })(this), 1);
    }
    this.startGraphicsEngine = function () {
        this.gEngine = setInterval(
                (function (self) {         //Self-executing func which takes 'this' as self
                    return function () {
                        self.graphicsEngine();
                    }
                })(this), 1);
    }






}