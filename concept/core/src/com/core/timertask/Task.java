package com.core.timertask;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author sunil
 */
class Task extends TimerTask {

    int count = 1;

    // run is a abstract method that defines task performed at scheduled time.
    public void run() {
        System.out.println(count + " : sunil Singh");
        count++;
    }
}

class TaskScheduling {

    public static void main(String[] args) {
        Timer timer = new Timer();


        // Schedule to run after every 3 second(3000 millisecond)
        timer.schedule(new Task(), new Date(), 24 * 60 * 60 * 60);
    }
}
