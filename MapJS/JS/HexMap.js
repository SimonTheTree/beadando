function HexMap(mapGenerator, l){
    
    /**
     * Genererates the tileId from the tiles coordinates.
     * the tiles array is indexed with this!
     * @param {AbstractHexTile} tile
     * @returns {String}
     */
    this.getTileId = function(tile){
        return this.toTileId(tile.x, tile.y);
    }
    /**
     * Genererates the tileId from the tiles coordinates.
     * the tiles array is indexed with this!
     * @param {int} x
     * @param {int} y
     * @returns {String}
     */
    this.toTileId = function(x,y){
        return x +","+ y;
    }
    
    /**
     * Adds the specified tile to the map.
     * @param {AbstractHexTile} newTile
     * @returns {Map} the map object
     */
    this.addTile = function(newTile){
        this.tiles[this.getTileId(newTile)] = newTile;
        console.log(this.getTileId(newTile));
        console.log(this.tiles[this.getTileId(newTile)]);
        return this;
    };
    
    /**
     * Gets a tile from the map by its coordinates. 
     * @param {int} x
     * @param {int} y
     * @returns {AbstractHexTile|undefined} undefined if tile not on map
     */
    this.getTile = function(x, y){
        return this.tiles[this.toTileId(x,y)];
    };
    
//  ???  this.getDimensionInTiles = function(){
//        var xMin=Integer.MAX_VALUE, xMax=Integer.MIN_VALUE, yMin=Integer.MAX_VALUE, yMax=Integer.MIN_VALUE;
//        
//        for(var i=0; i<this.tiles; i++){
//            var t = tiles[i].toPixel(layout).toPoint();
//            if(xMin > t.x) xMin = t.x;
//            if(xMax < t.x) xMax = t.x;
//            if(yMin > t.y) yMin = t.y;
//            if(yMax < t.y) yMax = t.y;
////            System.out.printf("%s,\txmin=%d, ymin=%d, xmax=%d, ymax=%d%n",t, xMin, yMin, xMax, yMax);
//        }
////        Tile p = tile.newTile(tile.fromPixel((xMax-xMin+1),(int)(0.5*(xMax-xMin+1)+(yMax-yMin+1)),layout));
//        var x = (xMax-xMin);
//        var y = (yMax-yMin);
//        var p = tile.fromPixel(x, y, layout);
////        return new Dimension(x, Math.abs(2*y+x)/2);
//        return new Dimension(p[0],Math.abs(2*p[1]+p[0])/2);
//    };
    
    /**
     * Calculates the width of the map on screen in pixels.
     * @returns {Point}
     */
    this.getDimensions = function(){
        var tile0 = this.tiles[Object.keys(this.tiles)[0]];
        var xMin=tile0.x, xMax=tile0.x, yMin=tile0.y, yMax=tile0.y;
        for(var tile in this.tiles){
//            console.log(this.tiles[tile].toPixel(this.layout));
            var t = this.tiles[tile].toPixel(this.layout);
            if(xMin > t.x) xMin = t.x;
            if(xMax < t.x) xMax = t.x;
            if(yMin > t.y) yMin = t.y;
            if(yMax < t.y) yMax = t.y;
        }
        
        return new Point(xMax-xMin+1, yMax-yMin+1);
    },
    
//    this.getCenteredLayoutCenter = function(){
//        var area = this.getDimensions();
//        var offset = this.getZeroPointOffset();
//        return new Point(area.width-offset.width,area.height-offset.height);
//    };
    
//    this.getZeroPointOffset = function(){
//        var xOffset=0, yOffset=0;
//        var p0 = this.getTile(0,0).toPixel(layout).toPoint();
//         for(var i=0; i<this.tiles; i++){
//            var p = tiles[i].toPixel(layout).toPoint();
//            if(p.x < p0.x && (p0.x-p.x) > xOffset) xOffset = p0.x-p.x;
//            if(p.y < p0.y && (p0.y-p.y) > yOffset) yOffset = p0.y-p.y;
//        }
////        xOffset = layout.size.x;
////        yOffset = layout.size.y;
//        
//        return new Dimension(xOffset, yOffset);
//    };
    
    /**
     * Gets all (max 6) neighbors of a tile on map.
     * @param {AbstractHexTile} tile
     * @returns {Tile[]}
     */
    this.getNeighborTiles = function(tile){
        var coordinates = tile.getNeighbors();
        var neighbors = [];
         for(var i=0; i<coordinates.length; i++){
            if(this.tileExists(coordinates[i])){
                neighbors.push(coordinates[i]);
            }
        }
        return neighbors;
    };
    
    /**
     * Gets all neighbors of a tile that satisfy some criterion specified by 
     * a passed function.
     * @param {Tile => bool} tileTester function
     * @param {Tile} tile
     * @returns {Tile[]}
     */
    this.getSpecNeighborTiles = function(tileTester, tile){
        var validNeighbors = [];
        var neighbors = this.getNeighborTiles(tile);
        
        for (var i=0; i<neighbors.length; i++) {
            if(tileTester(neighbors[i])){
                validNeighbors.push(neighbors[i])
            }
        }
        return validNeighbors;
    };
    /**
     * Checks wether a tile is on the map
     * @param {HexTile} tile
     * @returns {Boolean} true if and only if it is present on the map
     */
    this.tileExists = function(tile){
        if(typeof tile === "undefined") return false;
        return (typeof this.getTile(tile.x,tile.y) !== "undefined");
        
    };
    
    this.getSpecTiles = function(tileTester){
        var validTiles = [];
        for(var tile in this.tiles) {
            if(tileTester(this.tiles[tile])){
                validTiles.push(this.tiles[tile]);
            }
        }
        return validTiles;
    };
    
    /**
     * Converts on screen coordinates to a tile on the map, or undefined, if there is none.
     * @param {int} x
     * @param {int} y
     * @returns {undefined|Array|AbstractHexTile}
     */
    this.fromPixel = function(x, y){
        var t = this.tileType.fromPixel(x, y, this.layout);
        return this.getTile(t.x, t.y);
    };
    
    this.render = function(ctx) {
        ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
        for(var tile in this.tiles) {
            this.tiles[tile].render(ctx, this.layout);
        }
    };
    
    this.layout = l;
    this.tileType = mapGenerator.tileType;
    this.tiles = [];
    
    var t = mapGenerator.generate();
    for (var i=0; i< t.length; i++){
        this.tiles[this.getTileId(t[i])] = t[i];
    }
}


function GameBoard(){
    this.data = [];      //a mező ábrázolva 2 dimenziós tömbben (Stringekkel) a 3. dimenzió az egyes objektumod id códját tartalmazza!
    this.layers = [];
    this.cellSize;       //eggy cella mérete; a játékmező felbontásával fordítottan arányos
    this.bgColor;        //háttérszín
    this.x;              //a játékmező szélessége cellamértékben -1 (maximális x cord)
    this.width;          //mezőszélesség pixelben
    this.y;              //a játékmező magassága cellamértékben -1 (maximális y cord)
    this.height;         //mezőmagasság pixelben
    
    this.cellSize = 15;
    //data initialization

    
    this.construct = function (){
        for (var i=0; i<this.layers.length; i++){
            if (this.layers[i] !== undefined){
                this.layers[i].construct();
                this.layers[i].cellSize = this.cellSize;
                this.layers[i].x = this.x;
                this.layers[i].y = this.y;
                this.layers[i].ctx.canvas.width  = this.width; 
                this.layers[i].ctx.canvas.height = this.height;
                this.layers[i].canvas.style.width  = this.width + "px"; 
                this.layers[i].canvas.style.height = this.height + "px"; 
            }
        }
        this.x = Math.floor((window.innerWidth -360) / this.cellSize) - 1; 
        this.y = Math.floor((window.innerHeight)     / this.cellSize) - 1; 
        this.width = (this.x+1)*this.cellSize;
        this.height = (this.y+1)*this.cellSize;
        this.data = [];
        for(var x = 0; x<this.x; x++){
            this.data[x] = [];
            for(var y = 0; y<this.y; y++){
                this.data[x][y] = [];
                this.data[x][y][0] = "empty";
                this.data[x][y][1] = null;
            }
        };
    };
    this.construct();
    
    this.cellIsEmpty = function (cell){
        if ( this.data[cell.x][cell.y][0] !== undefined)
        return this.data[cell.x][cell.y][0] === "empty";
    };
    
    this.getRandomCell  = function(){
        var x = Math.floor(Math.random()*(this.x));
        var y = Math.floor(Math.random()*(this.y));
        return new Cell(x,y);
    };
    
    this.getRandomEmptyCell = function(){
        var cell;
        do{
            var cell = this.getRandomCell();
        }while(! this.cellIsEmpty(cell));
        return cell;
    };
    
    this.newRenderLayer = function (id){
        id = (id === undefined)? this.layers.length : id;
        $("#graphic").append('<canvas id="'+ id +'"></canvas>');
        this.layers[id] = new Field(grab(id), this.width, this.height, this.cellSize);
//        this.layers[id].bgColor = "#cccccc";
        this.layers[id].construct();
        grab(id).style.zIndex = -id-1;
        return this.layers[id];
    };
}

function Field(canvas,w,h,cS){                  //egy-egy vászonréteg egy-egy objektum saját rajzolórétege. Hogy ne zavarják egymást.
    this.canvas     = canvas;                   //
    this.ctx        = canvas.getContext("2d");  //
    this.cellSize   = cS;                       //eggy cella mérete | a játékmező felbontásával arányos
    this.bgColor    = undefined;                //a játékmező mérete
    this.x          = undefined;                //a játékmező szélessége cellaméretben
    this.y          = undefined;                //a játékmező magassága cellaméretben
    this.width      = w;                        //a játékmező szélessége cellaméretben
    this.height     = h;                        //a játékmező magassága cellaméretben
    
    this.construct = function(){
        //adjusting the canvas properties
        
        this.canvas.style.width  = this.width + "px"; 
        this.canvas.style.height = this.height + "px"; 
        if (this.bgColor !== undefined) this.canvas.style.backgroundColor = this.bgColor;
        this.canvas.style.position = "fixed";
        this.canvas.style.top = "5px";
        this.canvas.style.left = "5px";
        this.ctx.canvas.width  = this.width; 
        this.ctx.canvas.height = this.height;
        
        //calculating the number of cells
        this.x = Math.floor(this.width /this.cellSize)-1; 
        this.y = Math.floor(this.height /this.cellSize)-1; 
    };
    this.construct();   
    
    this.clearCell = function(cell){
            this.ctx.clearRect(cell.x*this.cellSize, cell.y*this.cellSize, this.cellSize, this.cellSize);
    };
//    this.clearCell = function(cell){
//        try{
//            if ( !(cell instanceof Cell) ) throw "badInput";
//            if (cell.x > this.x || cell.y > this.y) throw "coordinatesOutOfBoundException";
//            this.ctx.clearRect(cell.x*this.cellSize, cell.y*this.cellSize, this.cellSize, this.cellSize);
//        } catch (err){
//            new Comment("clearCell(" +cell.x + "," + cell.y+ ")" + err);
//        }
//    };
    
    this.clear = function(){
        this.ctx.clearRect ( 0 , 0 , this.canvas.width, this.canvas.height );
    };
    
    this.printCell = function(cell, str){
        this.ctx.beginPath();
        this.ctx.font = 'bold '+Number(this.cellSize-2)+'pt "Comic Sans MS"';
        this.ctx.fillStyle = 'white';
        this.ctx.fillText(str, (cell.x+.15)*this.cellSize, (cell.y+.9)*this.cellSize);
        this.ctx.stroke();
//        alert(Number((cell.x+0.5)*this.cellSize)+" "+Number((cell.y+0.5)*this.cellSize)+" "+str);
    };
    
    this.paintCell = function(cell, color){
            this.ctx.fillStyle=color;
            this.ctx.fillRect(cell.x*this.cellSize, cell.y*this.cellSize, this.cellSize, this.cellSize);
    };
//    this.paintCell = function(cell, color){
//        try{
//            if ( !(cell instanceof Cell) ) throw "badInput";
//            if (cell.x > this.x || cell.y > this.y) throw "coordinatesOutOfBoundException";
//            this.ctx.fillStyle=color;
//            this.ctx.fillRect(cell.x*this.cellSize, cell.y*this.cellSize, this.cellSize, this.cellSize);
//        } catch (err){
//            new Comment("paintCell(" +cell.x + "," + cell.y+ ")" + err);
//        }
//    };
    
    this.setCellSize = function(newSize){
        this.cellSize = newSize;
        this.construct();
    };

    this.incSize = function(){
        this.cellSize++;
        this.construct();
    };

    this.decSize = function(){
        this.cellSize--;
        this.construct();
    };
    
    this.cellIsEmpty = function (cell){
        var imgd = this.ctx.getImageData(cell.x*this.cellSize, cell.y*this.cellSize, this.cellSize, this.cellSize);
        var pix = imgd.data;

        for (var i = 0, n = pix.length; i < n; i += 4) {
            if (pix[i  ] !== 0) return false; // red
            if (pix[i+1] !== 0) return false; // green
            if (pix[i+2] !== 0) return false; // blue
            // i+3 is alpha (the fourth element)
        };
        return true;
    };
    
    this.getRandomCell  = function(){
        var x = Math.floor((Math.random()*this.x)+1);
        var y = Math.floor((Math.random()*this.y)+1);
        return new Cell(x,y);
    };
    
    this.getRandomEmptyCell = function(){
        do{
            var cell = this.getRandomCell();
        }while(! this.cellIsEmpty(cell));
        return cell;
    };
 
};


function startDebugRender(){
    debugRender = mező.newRenderLayer(100);
        grab("100").style.zIndex=100;
        for(i=0; i<snake.length; i++){
            snake[i].render.paintCell = function(cell, color){};
        }
        for(i=0; i<fruit.length; i++){
            fruit[i].render.paintCell = function(cell, color){};
        };
        

        setInterval(function(){
        debugRender.clear();
        for(var y = 0; y < mező.data[0].length; y++){
            for(var x = 0; x < mező.data.length; x++){
                if (mező.data[x][y][0] !== "empty"){
                    debugRender.paintCell(new Cell(x,y),"black");
                    debugRender.printCell(new Cell(x,y),mező.data[x][y][1]);
                }
            }
        }
//                var sto = stdout;
//                stdout = "graphic";
//                grab(stdout).innerHTML="";
//                for(var y = 0; y < mező.data[0].length; y++){
//                    for(var x = 0; x < mező.data.length; x++){
//                        write("|"+ mező.data[x][y][0][0]+"|");
//                    }
//                    writeln("");
//                }
//                writeln(snake[0].cell[0].x+","+snake[0].cell[0].y);
//                stdout = sto;
            },100);
}
