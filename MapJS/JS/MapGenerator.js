function AbstractMapGenerator(tileT, p) {

    this.tileType = tileT;
    this.params = p;
    
    this.generate = function(){return [];}; 
}

MapGeneratorHexRectangleFlat.prototype = new AbstractMapGenerator();
MapGeneratorHexRectangleFlat.prototype.constructor = MapGeneratorHexRectangleFlat;
function MapGeneratorHexRectangleFlat(tileT, p){
    AbstractMapGenerator.call(this, tileT, p);
    
    this.generate = function(){
        var x = p[0];
        var y = p[1];
        var a = [];
        for (var q = 0; q < x; q++) {
            var q_offset = q >> 1;
            for (var r = -q_offset; r < y - q_offset; r++) {
                a.push(new this.tileType(q, r));
            }
        }
        return a;
    }; 
}