MyTile.prototype = new BasicHexTile();
MyTile.prototype.constructor = MyTile;
function MyTile(x, y) {
    BasicHexTile.call(this, x, y);
    this.lit = false;
    this.selected = false;

    this.render = function (ctx, layout) {
        var corners = this.polygonCorners(layout);
        ctx.beginPath();
        ctx.moveTo(corners[0].x, corners[0].y);
        for (var i = 1; i < corners.length; i++) {
            ctx.lineTo(corners[i].x, corners[i].y);
        }
//        context.lineTo(corners[0].x, corners[0].y);
        ctx.closePath();

        if (this.lit || this.selected) {
            if (this.lit) {
                ctx.fillStyle = "#000000";
            } else {
                ctx.fillStyle = "#dd0066";
            }
            ctx.fill();
        }
        ctx.stroke();

    }

    this.light = function () {
        this.lit = true;
    }
    this.unLight = function () {
        this.lit = false;
    }
}

/**
 * 
 * @param {int} x
 * @param {int} y
 * @param {HexLayout} layout
 * @returns {BasicHexTile}
 */
MyTile.fromPixel = function (x, y, layout) {
    var cord = BasicHexTile.pixelToAxial(x, y, layout);
    return new this(cord[0], cord[1]);
}
