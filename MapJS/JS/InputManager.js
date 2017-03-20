function InputManager(HtmlObject) {
    this.src = HtmlObject;
    this.mouseX = null;
    this.mouseY = null;

    this.isMouseDown = [];
    this.isMouseDown["mouse"] = false;
    this.isMouseDown["right"] = false; //right
    this.isMouseDown["left"] = false; //left
    this.isMouseDown["middle"] = false; //middle
    this.downCount = 0;

    this.isMousePressed = [];
    this.isMousePressed["mouse"] = false;
    this.isMousePressed["right"] = false; //right
    this.isMousePressed["left"] = false; //left
    this.isMousePressed["middle"] = false; //middle
    this.pressCount = 0;

    this.isMouseUp = [];
    this.isMouseUp["mouse"] = true;
    this.isMouseUp["right"] = false; //right
    this.isMouseUp["left"] = false; //left
    this.isMouseUp["middle"] = false; //middle
    this.isMouseDblClicked = false;
    this.upCount = 0;

    this.mouseStateChanged = false;

    this.mouseOver = false;
    this.deltaWheel = 0;


    var self = this;

    /**
     * Returns the mouse position relative to the element the manager is
     * attached to
     * @returns {Point}
     */
    this.getMousePos = function () {
        return new Point(this.mouseX, this.mouseY);
    }

    /**
     * returns whether specified has been clicked (pressed and released)
     * @param {String} key (mouse(for any), left, right, middle)
     * @returns {bool|undefined} undefined, if key is invalid
     */
    this.mouseDown = function (key) {
        var ret = this.isMouseDown[key]
        if (this.isMouseDown[key]) { //if defined and true
            this.isMouseDown[key] = false;
            this.upCount = 0;
        }
        return ret;
    }

    /**
     * returns whether specified key is currently pressed
     * @param {String} key (mouse(for any), left, right, middle)
     * @returns {bool|undefined} undefined, if key is invalid
     */
    this.mousePressed = function (key) {
        var ret = this.isMousePressed[key]
        if (this.isMousePressed[key]) { //if defined and true
            this.pressCount = 0;
        }
        return ret;
    }

    /**
     * returns whether specified has been clicked (pressed and released)
     * @param {String} key (mouse(for any), left, right, middle)
     * @returns {bool|undefined} undefined, if key is invalid
     */
    this.mouseUp = function (key) {
        var ret = this.isMouseUp[key]
        if (this.isMouseUp[key]) { //if defined and true
            this.isMouseUp[key] = false;
            this.upCount = 0;
        }
        return ret;
    }

    //Fires on a mouse (R) click on the element
//    HtmlObject.addEventListener('click', function (e) {
//        self.isMouseClicked["right"] = true;
////        self.isMousePressed["mouse"] = true;
//    }, false);

    //Fires on a mouse double-click on the element
    HtmlObject.addEventListener('dblclick', function (e) {
        self.isMouseDblClicked = true;
    }, false);

    //Fires when a mouse button (Right/Lelft/Middle...) is pressed down on an element
    HtmlObject.addEventListener('mousedown', function (e) {
        self.isMousePressed["mouse"] = true;
        if ((e.buttons & 1) !== 0) {
            self.isMousePressed["left"] = true;
            if (self.pressCount == 0){
                self.isMouseDown["left"] = true;
                self.downCount++;
            }
        }
        if ((e.buttons & 2) !== 0) {
            self.isMousePressed["right"] = true;
            if (self.pressCount == 0){
                self.isMouseDown["right"] = true;
                self.downCount++;
            }
        }
        if ((e.buttons & 4) !== 0) {
            self.isMousePressed["middle"] = true;
            if (self.pressCount == 0){
                self.isMouseDown["middle"] = true;
                self.downCount++;
            }
        }
        self.pressCount++;
    }, false);

    //Fires when a mouse button (Right/Lelft/Middle...) is released
    HtmlObject.addEventListener('mouseup', function (e) {
        self.isMouseUp["mouse"] = true;
        self.isMousePressed["mouse"] = false;
        self.upCount++;
        if (e.button === 0) { //vmiért a e.buttons mindig 0. ???????!?!?!?!
            self.isMouseUp["left"] = true;
            self.isMousePressed["left"] = false;
            self.isMouseDown["left"] = false;
        }
        if (e.button === 2) {
            self.isMouseUp["right"] = true;
            self.isMousePressed["right"] = false;
            self.isMouseDown["right"] = false;
        }
        if (e.button === 1) {
            self.isMouseUp["middle"] = true;
            self.isMousePressed["middle"] = false;
            self.isMouseDown["middle"] = false;
        }
    }, false);

    //Fires when the mouse pointer is moving while it is over an element
    HtmlObject.addEventListener('mousemove', function (e) {
        self.mouseX = e.pageX - $(self.src).offset().left;
        self.mouseY = e.pageY - $(self.src).offset().top;
    }, false);

    //Fires when the mouse pointer moves out of an element
    HtmlObject.addEventListener('mouseout', function (e) {
        self.mouseOver = false;
    }, false);

    //Fires when the mouse pointer moves over an element
    HtmlObject.addEventListener('mouseover', function (e) {
        self.mouseOver = true;
    }, false);

    //Fires when wheel is scrolled
    HtmlObject.addEventListener('wheel', function (e) {
        self.deltaWheel += (e.deltaY > 0) ? 1 : -1;
    }, false);



    //Fires when a user is pressing a key
    HtmlObject.addEventListener('keydown', function (e) {
    }, false);

    //Fires when a user presses a key
    HtmlObject.addEventListener('keypress', function (e) {
    }, false);

    //Fires when a user releases a key
    HtmlObject.addEventListener('keyup', function (e) {
    }, false);
}

InputManager.Click = function (name, keyCode) {
    this.name = name;
    this.keyCode = keyCode;
    this.pressCount;
    this.pressed;
    this.clicked = false;

    this.togglePressed = function (bool) {
        if (pressed != bool) {
            pressed = bool;
        }
        if (pressed) {

        }
    }
}

InputManager.Key = function (name, keyCode) {
    this.name = name;
    this.keyCode = keyCode;
    this.pressCount;
    this.pressed = false;
    this.typed = false;

    this.togglePressed = function (bool) {
        if (pressed != bool) {
            pressed = bool;
        }
        if (pressed) {
            pressCount++;
        }
    }
}

