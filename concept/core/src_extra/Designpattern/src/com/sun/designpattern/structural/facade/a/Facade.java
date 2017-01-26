package com.sun.designpattern.structural.facade.a;

/**
 * A facade is an object that provides a simplified interface to a larger body of code, such as a class library.
 *
 * An Adapter is used when the wrapper must respect a particular interface and must support a polymorphic behavior. On
 * the other hand, a facade is used when one wants an easier or simpler interface to work with.
 *
 */

/* Client */
public class Facade {

    public static void main(String[] args) {
        Computer facade = new Computer();
        facade.startComputer();
    }
}


/* Complex parts */
class CPU {

    public void freeze() {
    }

    public void jump(long position) {
    }

    public void execute() {
    }
}

class Memory {

    public void load(long position, byte[] data) {
    }
}

class HardDrive {

    public byte[] read(long lba, int size) {
        return new byte[10];
    }
}

/* Facade */
class Computer {

    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;

    public Computer() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
    }

    public void startComputer() {
        cpu.freeze();
        //  memory.load(BOOT_ADDRESS, hardDrive.read(BOOT_SECTOR, SECTOR_SIZE));
        // cpu.jump(BOOT_ADDRESS);
        cpu.execute();
    }
}
