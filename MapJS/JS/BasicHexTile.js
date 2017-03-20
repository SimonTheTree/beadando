/**
 * Abstract Hex-cell. HexField can handle these. Extend this Prototype to suit
 * your specific needs. coordinates are stored in cubic coordinate system (for convenience), but
 * axial is used everywhere (simply ommiting the z=-x-y coordinate).
 * @param {int} x
 * @param {int} y
 * @param {int} z
 * @returns {HexCell}
 */
BasicHexTile = function (x, y, z) {
    this.x = x;
    this.y = y;
    this.z = -x - y;

    /**
     * Cubic coordinate getter
     * @returns {double[]}
     */
    this.getCubic = function () {
        return [this.x, this.y, this.z];
    };

    /**
     * Axial coordinate getter
     * @returns {Array}
     */
    this.getAxial = function () {
        return [this.x, this.y];
    };
    /**
     * Calculate the center point coordinates on screen
     * @param {HexLayout} layout
     * @returns {Point}
     */
    this.toPixel = function (layout) {
        var M = layout.orientation;
        var X = (M.F0 * this.x + M.F1 * this.y) * layout.size.x;
        var Y = (M.F2 * this.x + M.F3 * this.y) * layout.size.y;
        return new Point(X + layout.origin.x, Y + layout.origin.y);
    }

    /**
     * String representation of fields
     * @returns {String}
     */
    this.toString = function () {
        return "[" + x + "," + y + "," + z + "]";
    }

    /**
     * get offset of a corner from the center point on screen
     * @param {HexLayout} layout
     * @param {int} corner number of the corner (0-5)
     * @returns {Point} vektor from center to corner
     */
    BasicHexTile.hexCornerOffset = function (layout, corner)
    {
        var M = layout.orientation;
        var size = layout.size;
        var angle = (2 * Math.PI / 6) * (corner + M.START_ANGLE);
        var X = (size.x + 1) * Math.cos(angle);
        var Y = (size.y) * Math.sin(angle);
        return new Point(X, Y);
    }

    /**
     * Calculates the vertices of the hexagon tile on screen
     * @param {HexLayout} layout
     * @returns {Point[]} containing the 6 veritces
     */
    this.polygonCorners = function (layout) {
        var corners = [];
        var center = this.toPixel(layout);
        for (var i = 0; i < 6; i++) {
            var p = center.add(BasicHexTile.hexCornerOffset(layout, i));
            corners.push(p);
        }
        return corners;
    }

    /**
     * Tile arithmetic addition (vektor add)
     * @param {TileHex} t
     * @returns {BasicHexTile}
     */
    this.add = function (t) {
        return new this.constructor(x + t.x, y + t.y);
    }

    /**
     * Tile arithmetic subtraction (vektor sub)
     * @param {type} t
     * @returns {BasicHexTile}
     */
    this.subtract = function (t) {
        return new this.constructor(t.x - x, t.y - y);
    }

    /**
     * Gets the unit-tile in given (0-6) direction
     * @param {type} dir
     * @returns {Array}
     */
    this.HexDirection = function (dir) {
        if (!(0 <= dir && dir < 6))
            dir = 6;
        return BasicHexTile.HEX_DIRECTIONS[dir];
    }

    /**
     * gets the neighbor tile in given direction
     * @param {type} dir
     * @returns {BasicHexTile}
     */
    this.getNeighbor = function (dir) {
        return this.add(this.HexDirection(dir));
    }

    /**
     * Gets all neighbors of the Tile in an array
     * @returns {Array|h}
     */
    this.getNeighbors = function () {
        h = [];
        for (var i = 0; i < 6; i++) {
            h[i] = this.getNeighbor(i);
        }
        return h;
    }
    
    /**
     * Draw yerself!!
     * @param {DOM canvas} canvas
     * @param {HexLayout} layout
     * @returns {this}
     */
    this.render = function (canvas, layout) {
        return this;
    }

    this.hashCode = function () {
        var A = (x >= 0) ? 2 * x : -2 * x - 1;
        var B = (y >= 0) ? 2 * y : -2 * y - 1;
        return (A >= B) ? A * A + A + B : B * B + A;
    }

    this.equals = function (c) {
        try {
            if (!(c instanceof BasicHexTile))
                throw "err: need BasicHexTile as comparison!"
            return(
                    c.x === this.x
                    && c.y === this.y
                    && c.z === this.z
                    );
        } catch (err) {
            new CustomTools.Comment(err);
        }
    };

};

BasicHexTile.HEX_DIRECTIONS = [
    new BasicHexTile(1, 0, -1),
    new BasicHexTile(1, -1, 0),
    new BasicHexTile(0, -1, 1),
    new BasicHexTile(-1, 0, 1),
    new BasicHexTile(-1, 1, 0),
    new BasicHexTile(0, 1, -1),
    new BasicHexTile(0, 0, 0) //nullvektor
];

/**
 * convert axial coordinates to cubic
 * @param {int} x
 * @param {int} y
 * @returns {int[]}
 */
BasicHexTile.axialToCubic = function (x, y) {
    return [x, y, -x - y];
};

/**
 * convert cubic coordinates to axial
 * @param {int} x
 * @param {int} y
 * @param {int} z
 * @returns {int[]}
 */
BasicHexTile.cubicToAxial = function (x, y, z) {
    return [x, y];
};

/**
 * takes pixel coordinates and a layout, and calculates the axial coordinates of
 * the tile on screen that pixel belongs to.
 * @param {int} x
 * @param {int} y
 * @param {HexLayout} layoRut
 * @returns {int[]} [x,y]
 */
BasicHexTile.pixelToAxial = function (x, y, layout) {
    var M = layout.orientation;
    var X = (x - layout.origin.x) / layout.size.x;
    var Y = (y - layout.origin.y) / layout.size.y;

    var q = M.B0 * X + M.B1 * Y;
    var r = M.B2 * X + M.B3 * Y;

    var rx = Math.round(q);
    var ry = Math.round(r);
    var rz = Math.round(-q - r);

    var x_diff = Math.abs(rx - q);
    var y_diff = Math.abs(ry - r);
    var z_diff = Math.abs(rz - (-q - r));

    //eredmény javítása - normalizálás
    if (CustomTools.doubleCompare(x_diff, y_diff) > 0 && CustomTools.doubleCompare(x_diff, z_diff) > 0) {
        rx = -ry - rz;
    } else if (CustomTools.doubleCompare(y_diff, z_diff) > 0) {
        ry = -rx - rz;
    }
    return [rx, ry];
}
/**
 * 
 * @param {int} x
 * @param {int} y
 * @param {HexLayout} layout
 * @returns {BasicHexTile}
 */
BasicHexTile.fromPixel = function (x, y, layout) {
    var cord = BasicHexTile.pixelToAxial(x, y, layout);
    return new this(cord[0], cord[1]);
}