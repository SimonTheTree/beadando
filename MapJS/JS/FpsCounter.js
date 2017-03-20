function FpsCounter() {
    var lastTime = 0;
    var fps = 0;

    this.update = function () {
        var newFps = 1000 / (performance.now() - lastTime); //one second(milli) divided by amount of time it takes for one frame to finish
        fps = (fps * 0.99 + newFps * 0.01); //smooths fps
        lastTime = performance.now();
    }
    
    this.fps = function () {
        return Math.round(fps);
    }
}