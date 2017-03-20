MyState.prototype = new State();
MyState.prototype.constructor = MyState;
function MyState(name) {
    State.call(this, name);
    this.layout = new HexLayout(HexOrientation.LAYOUT_FLAT, new Point(20, 20), new Point(10, 10))
    this.map = new HexMap(new MapGeneratorHexRectangleFlat(MyTile, [20, 10]), this.layout);

    this.litTile = new MyTile();
    this.val;

    var points = [];

    this.render = function (ctx) {
        var mousePos = this.input.getMousePos();
        var x = mousePos.x;
        var y = mousePos.y;
        var s = "";
        s += "ticks: " + this.tick;
        s += "\n"
        s += "fps: " + this.fpsCounter.fps();
        s += "\n"
        s += "tps: " + this.tpsCounter.fps();
        s += "\n"
        s += "x:" + x + " y:" + y + "\n"
        $("#output").html(s);

        this.map.render(ctx);
    };

    this.run = function () {
        var mousePos = this.input.getMousePos();
        var x = mousePos.x;
        var y = mousePos.y;
        var t = this.map.fromPixel(x, y);
        
        
        if (this.input.mouseDown("left")) {
            this.val = !t.selected;
        }
        if (this.input.mousePressed("left")) {
            if(!t){
                t = new MyTile(x,y);
                this.map.addTile(t);
            }
            t.selected = this.val;
        }
        if (this.litTile)
            this.litTile.lit = false;

        this.litTile = this.map.fromPixel(x, y);

        if (this.litTile)
            this.litTile.lit = true;
    }
}