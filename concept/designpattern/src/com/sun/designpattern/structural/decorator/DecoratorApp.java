package com.sun.designpattern.structural.decorator;

/**
 * The Decorator design pattern attach additional responsibilities to an object dynamically.It is wrap up at another
 * object.It will extend functionality of object without affecting any other object.Decorators provide a alternative to
 * subclassing for extending functionality.
 *
 * Sometimes a large number of independent extensions are possible and would produce an explosion of subclasses to
 * support every combination.
 *
 * io package is good example
 *
 * @author Sunil
 */
public class DecoratorApp {

    public static void main(String[] args) {
        Room room = new CurtainDecorator(new ColorDecorator(new SimpleRoom()));
        System.out.println(room.showRoom());
    }
}

interface Room {

    public String showRoom();
}

class SimpleRoom implements Room {

    @Override
    public String showRoom() {
        return "Normal Room";
    }

}

abstract class RoomDecorator implements Room {

    protected Room specialRoom;

    public RoomDecorator(Room specialRoom) {
        this.specialRoom = specialRoom;
    }

    public String showRoom() {
        return specialRoom.showRoom();
    }
}

class ColorDecorator extends RoomDecorator {

    public ColorDecorator(Room specialRoom) {
        super(specialRoom);
    }

    public String showRoom() {
        return specialRoom.showRoom() + addColors();
    }

    private String addColors() {
        return " + Blue Color";
    }
}

class CurtainDecorator extends RoomDecorator {

    public CurtainDecorator(Room specialRoom) {
        super(specialRoom);
    }

    public String showRoom() {
        return specialRoom.showRoom() + addCurtains();
    }

    private String addCurtains() {
        return " + Red Curtains";
    }
}
