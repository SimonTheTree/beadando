CustomTools = {
    
    grab: function (id) {
        return document.getElementById(id);
    },
    
    setStdOutput: function (newstdo) {
        CustomTools.stdout = newstdo;
    },
    
    write: function (s) {
        try {
            if (CustomTools.CustomTools.grab(CustomTools.stdout) === null)
                throw "no CustomTools.stdout defined!";
            CustomTools.grab(CustomTools.stdout).innerHTML += s;
        } catch (err) {
            CustomTools.stdout = "out";
            alert("err: no CustomTools.stdout was defined. I set it to 'out'...");
            if (CustomTools.grab(CustomTools.stdout) !== null)
                CustomTools.grab(CustomTools.stdout).innerHTML += s;
        }
    },
    
    clrscr: function () {
        CustomTools.grab(CustomTools.stdout).innerHTML = "";
    },
    
    writeln: function (s) {
        try {
            if (CustomTools.grab(CustomTools.stdout) === null)
                throw "no CustomTools.stdout defined!";
            CustomTools.grab(CustomTools.stdout).innerHTML += "<div>" + s + "</div>";
        } catch (err) {
            CustomTools.stdout = "out";
            alert("err: no CustomTools.stdout was defined. I set it to 'out'...");
            if (CustomTools.grab(CustomTools.stdout) !== null)
                CustomTools.grab(CustomTools.stdout).innerHTML += "<div>" + s + "</div>";
        }
    },
    
    Comment: function (s, time) {
        CustomTools.comments.enqueue(this);
        this.message = s;
        this.id = CustomTools.commentId++;
        $("#" + CustomTools.stdout).append("<div id ='comment" + this.id + "' style='display:none;'>" + this.message + "</div>");
        $("#comment" + this.id).slideDown({duration: 250, queue: true});
        setTimeout(function () {
            var com = CustomTools.comments.dequeue();
            $("#comment" + com.id).fadeOut(2000, function () {
                $("#comment" + com.id).remove();
            });
        }, (time === undefined) ? 3000 : time * 1000);
    },
    Stack: function () {
        this.data = [];
        this.current = -1;
        this.length = 0;
        this.hasNext = function () {
            return this.current !== -1;
        };
        this.next = function () {
            try {
                if (!this.hasNext())
                    throw "error: attempted to extract form empty Heap";
                this.length--;
                return this.data[this.current--];
            } catch (err) {
                new Comment(err);
            }
        };
        this.add = function (d) {
            try {
                if (typeof d === undefined)
                    throw "err: wont add undefined value to Heap! :/"
                this.data[++this.current] = d;
                this.length++;
            } catch (err) {
                new Comment(err);
            }

        };
    },
    StackQueue: function () {
        this.inStack = new Stack();
        this.outStack = new Stack();
        this.state = 0;
        //0 --> set on input
        //1 --> set on output
        this.changeState = function () {
            if (this.inStack.hasNext()) {
                var s1 = this.inStack;
                var s2 = this.outStack;
            } else {
                var s1 = this.outStack;
                var s2 = this.inStack;
            }
            for (var i = 0; i < s1.data.length; i++) {
//            s2.add(s1.next());
                var d = s1.next();
//             new Comment("");
//             new Comment(d);
//             new Comment(s1.length);
                s2.add(d);
            }
            s1.data = [];
            this.state = (this.state === 1) ? 0 : 1;
        };
        this.enQueue = function (d) {
            if (this.state === 1)
                this.changeState();
            this.inStack.add(d);
        };
        this.deQueue = function () {
            if (this.state === 0)
                this.changeState();
            return this.outStack.next();
        };
    },
    Queue: function () {

        // initialise the queue and offset
        var queue = [];
        var offset = 0;
        // Returns the length of the queue.
        this.getLength = function () {
            return (queue.length - offset);
        };
        // Returns true if the queue is empty, and false otherwise.
        this.isEmpty = function () {
            return (queue.length === 0);
        };
        /* Enqueues the specified item. The parameter is:
         *
         * item - the item to enqueue
         */
        this.enqueue = function (item) {
            queue.push(item);
        };
        /* Dequeues an item and returns it. If the queue is empty, the value
         * 'undefined' is returned.
         */
        this.dequeue = function () {

            // if the queue is empty, return immediately
            if (queue.length === 0)
                return undefined;
            // store the item at the front of the queue
            var item = queue[offset];
            // increment the offset and remove the free space if necessary
            if (++offset * 2 >= queue.length) {
                queue = queue.slice(offset);
                offset = 0;
            }
            // return the dequeued item
            return item;
        };
        /* Returns the item at the front of the queue (without dequeuing it). If the
         * queue is empty then undefined is returned.
         */
        this.peek = function () {
            return (queue.length > 0 ? queue[offset] : undefined);
        };
    },
    /**
     *  compares two double values
     * @param {double} d1
     * @param {double} d2
     * @returns {Boolean} 0:"=", 1:"d1<d2", -1:"d1>d2"
     */
    doubleCompare: function (d1, d2) {
        return ((d1 - d2) < 0.00000001);
    },
    
};

CustomTools.stdout = "out";
CustomTools.commentId = 0;
CustomTools.comments = new CustomTools.Queue();