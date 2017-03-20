/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Doule percision point class
 * @param {double} x
 * @param {double} y
 * @returns {Point}
 */
function Point(x, y) {
    this.x = x;
    this.y = y;
    /**
     * Gets rounded y coordinate
     * @returns {Number}
     */
    this.getIntx = function () {
        return Math.round(this.x);
    };
    /**
     * Gets rounded y coordinate
     * @returns {Number}
     */
    this.getInty = function () {
        return Math.round(this.y);
    };

    /**
     * Add two points coordinates (as vektors)
     * @param {Point} b
     * @returns {Point}
     */
    this.add = function (b) {
        return new Point(this.x + b.x, this.y + b.y);
    };
    
    /**
     * Substract two points coordinates
     * @param {Point} b
     * @returns {Point}
     */
    this.subtract = function (b) {
        return new Point(this.x - b.x, this.y - b.y);
    };
    
    this.multiply = function (b) {
        return new Point(this.x * b.x, this.y * b.y);
    };
    
    /**
     * Rounds this Points coordinates
     * @returns {Point}
     */
    this.toIntPoint = function () {
        return new Point(this.getIntx(), this.getInty());
    };
    
    /**
     * Determines wether this point and another have (about) the same coordinates.
     * @param {Pont} point
     * @returns {Boolean} true if coordinates are close enough.
     */
    this.equals = function (point) {
        return ((CustomTools.doubleCompare(point.x , this.x) === 0) && (CustomTools.doubleCompare(point.y, this.y) === 0));
    };
    
    /**
     * Determines wether this point is inside or eoutside of given polygon.
     * @param {Point[]} points the Vertices of the polygon
     * @returns {Boolean} true if inside
     */
    this.isInPolygon = function (points) {
        var ret = 0;
        for (var i = 0; i < points.length - 1; i++) {
            ret += this.getArea(this, points[i], points[i + 1]);
        }
        ret += this.getArea(this, points[i], points[0]);
        return (this.getArea(points) - ret) < 0.00001;
    };

    /**
     * Calculates the area of a polygon, the polygon itself can be convex or concave.
     * @param {Point[]} points the Vertices of the polygon
     * @returns {Number}
     */
    this.getArea = function (points) {
        var ret = 0;
        for (var i = 0; i < points.length - 1; i++) {
            ret += points[i].x * points[i + 1].y - points[i].y * points[i + 1].x;
        }
        ret += points[i].x * points[0].y - points[i].y * points[0].x;
        return Math.abs(ret / 2);
    };

    /**
     * Calculates the euclidean dispance between this point and another.
     * @param {Point} b The other point
     * @returns {Number}
     */
    this.distance = function (b) {
        return Math.sqrt(Math.abs(this.x - b.x) * Math.abs(this.x - b.x) + Math.abs(this.y - b.y) * Math.abs(this.y - b.y));
    };
}