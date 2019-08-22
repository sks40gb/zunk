//Which keywords are used to handle exceptions?
//------------------------------------------------------------------------------------------------------------------------

try {
    //Code
} catch (exp) {
    //Code to throw an exception
} finally {
    //Code runs either it finishes successfully or after catch
}

/*
 What is variable typing?
 ------------------------------------------------------------------------------------------------------------------------
 Variable typing is used to assign a number to a variable and then assign string to the same variable. Example is as follows:
 */

i = 8;
i = "john";




//Client Operating system
//------------------------------------------------------------------------------------------------------------------------*/
navigator.platform


//How to find complete system information in the client machine using JavaScript?
//------------------------------------------------------------------------------------------------------------------------
navigator.appVersion

"5.0 (Macintosh; Intel Mac OS X 10_14_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36"


//push, unshift() and pop(), shift()
//------------------------------------------------------------------------------------------------------------------------
//All there are arrays functions

push()     //-> it add the element at the bottom of array
unshift()  //-> add the item at the start of array

pop()      //-> remove the item from the end of array
shift()   //-> remove the item the starting of array




//What is the 'Strict' mode in JavaScript and how can it be enabled?
//------------------------------------------------------------------------------------------------------------------------
//Strict Mode adds certain compulsions to JavaScript. Under the strict mode, JavaScript shows errors for a piece of codes, which did not show an error before, but might be problematic and potentially unsafe. Strict mode also solves some mistakes that hamper the JavaScript engines to work efficiently.

//Strict mode can be enabled by adding the string literal "use strict" above the file. This can be illustrated by the given example:

function myfunction() {
    "use strict";
    var v = "This is a strict mode function";
}


//Explain window.onload and onDocumentReady?
//------------------------------------------------------------------------------------------------------------------------
//The onload function is not run until all the information on the page is loaded. This leads to a substantial delay before any code is executed.

//onDocumentReady loads the code just after the DOM is loaded. This allows early manipulation of the code.


//Creating Objects
//------------------------------------------------------------------------------------------------------------------------

//1. Factory Function 

function createCircle(radius) {
    return {
        radius,
        draw: function () {
            console.log("draw with radius : " + this.radius);
        }
    }
}

let circle = createCircle(5);   //<-- not there is no new operator here.
circle.draw();


//2. Constructor Function

function Circle(radius) {
    this.radius = radius;
    this.draw = function () {
        console.log("Circle draw : " + this.radius);
    }
}

var circle = new Circle(5);
circle.draw();


//Create private variable and functions
//------------------------------------------------------------------------------------------------------------------------

/*There is no direct way to make it private however local variable or function can act as private variable and function.
 This is possible because of closures.*/

function Circle(radius) {

    let colors = ['red', 'green'];  //local or private function

    function printWithColor(size) {  //local or private function
        console.log("size is " + size + " with colors " + colors);
    }

    this.radius = radius;

    this.draw = function () {
        printWithColor(this.radius); //<-- not this keyword is used since it is instance variable.
    };
}

var circle = new Circle(5);
circle.draw(5);


//Setters and Getters
//------------------------------------------------------------------------------------------------------------------------

// Method 1

function Circle(radius) {

    let colors = ['red', 'green'];

    this.getAvailableColors = function () {   //<--- Getters which is class level function accessing the local variable
        return colors;
    };

    this.radius = radius;

    this.draw = function () {
        console.log("draw " + this.radius);
    };

}

var circle = new Circle(5);
circle.draw(5);


// Method 2

function Circle(radius) {

    let colors = ['red', 'green'];
    //Getters
    this.getAvailableColors = function () {
        return colors;
    };

    Object.defineProperty(this, "colors", {
        get: function () {
            console.log("getting colors " + colors);
            return colors;
        },
        set: function (value) {
            console.log("setting colors " + value);
            colors = value;
        }
    });

}

var circle = new Circle(5);
circle.colors;              // Getter method is called
circle.colors = ["yellow"]; // Setter method is called


//delete property

delete circle.radius;

//call vs apply vs bind
//------------------------------------------------------------------------------------------------------------------------
//The function .call() and .apply() are very similar in their usage except a little difference. .call() is used when 
//the number of the function's arguments are known to the programmer, as they have to be mentioned as arguments in the 
//call statement. On the other hand, .apply() is used when the number is not known. The function .apply() expects the 
//argument to be an array.

Circle.call({}, 4);     //<-- first argument decides the context this, second arguments are arguments of called function Circle.

Circle.apply({}, [4]);  // functions are arguments are passed as an array. that is the only difference from the call.

var newCircle = Circle.bind({}); //<-- Bind will create new function attached to the current object which can be called later.
newCircle(4);



//Object important functions
//------------------------------------------------------------------------------------------------------------------------

//Loop over the each key of object

for (let key in circle) {
    if (typeof circle[key] !== 'function') {
        console.log(key, circle[key]);
    }
}

//returns array of keys

Object.keys(circle);


//returns the array of values

Object.values(circle);


//check if the property exist in the object

if ('radius' in circle) {
    console.log("Circle has the radius property or function");
}


//Closures
//----------------------------------------------------------------------------------------------------------------------
//Closures determines which variables will be accessible in inner functions.


/**
 * Array Operators
 */
//----------------------------------------------------------------------------------------------------------------------

let items = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"];

//For each - array.forEach(function(currentValue, index, arr), thisValue)
items.forEach((item, index) => {
    console.log(item, index);
});

//Map - array.map(function(currentValue, index, arr), thisValue)
let modifiedDays = items.map(item => {
    return {name: "sunil", day: item};
});

//Filter - array.filter(function(currentValue, index, arr), thisValue)
let holidays = items.filter(item => {
    return item.startsWith("S");
});

//reduce - array.reduce(function(total, currentValue, currentIndex, arr), initialValue)
let concateDays = items.reduce((acc, item) => {
    acc += " " + item;
    return acc;
}, "Days ");  //<-- initial value


/**
 * 
 * Inheritance
 * --------------------------------------------------------------------------------------------------------------------
 */

//Method 1
function Vehicle(name) {

    this.name = name;

    this.print = function () {
        console.log(this.name);
    }
}

function Car(name) {
    Vehicle.call(this, name);         //<-- all methods and propeties will be copied to the Car.
}

let vehicle = new Vehicle("BMW");
vehicle.print();

let car = new Car("Maruti");
car.print();


//Method 2
function Vehicle(name) {
    this.name = name;
    this.print = function () {
        console.log(this.name);
    };
}

function Car() {
}

Car.prototype = new Vehicle("Ferrari");  //<-- the properties and methods will go under __proto__ not at the class.
let car = new Car();

car.print();
