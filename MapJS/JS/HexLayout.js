/**
 * Ez az osztály meghatározza hogy az egyes cellákat egy map milyen layouttal rajzolja ki
 * @param {Orientation} orientation
 * @param {Point} size
 * @param {Point} origin
 * @returns {Layout}
 * @author ganter
 */
function HexLayout(orientation, size, origin){
    this.orientation = orientation;
    this.size = size;
    this.origin = origin;
};

/**
 * Egy hexagon cella kétféleképpen állhat csúccsal fölfelé, vagy oldallal felfelé.
 * Ezen osztály statikus paraméterei ezt a kétfajta beállítást tartalmazzák/jelentik.
 * @param {double} F0
 * @param {double} F1
 * @param {double} F2
 * @param {double} F3
 * @param {double} B0
 * @param {double} B1
 * @param {double} B2
 * @param {double} B3
 * @param {double} START_ANGLE
 * @returns {Orientation}
 * @author ganter
 */
function HexOrientation( F0, F1, F2, F3, B0, B1, B2, B3, START_ANGLE){ 
    
    this.F0 = F0;
    this.F1 = F1;
    this.F2 = F2;
    this.F3 = F3;
    this.B0 = B0;
    this.B1 = B1;
    this.B2 = B2;
    this.B3 = B3;
    this.START_ANGLE = START_ANGLE;
};


    GYOK3 = Math.sqrt(3);
    HexOrientation.LAYOUT_POINTY = new HexOrientation(GYOK3, GYOK3/2.0, 0, 1.5, GYOK3/3.0, -1.0/3.0, 0, 2/3.0, 0.5);
    HexOrientation.LAYOUT_FLAT = new HexOrientation(1.5, 0, GYOK3/2.0, GYOK3, 2/3.0, 0, -1/3.0, GYOK3/3.0, 0);