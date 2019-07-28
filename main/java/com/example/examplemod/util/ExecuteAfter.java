package com.example.examplemod.util;

import javax.swing.*;
import java.util.function.Consumer;

public class ExecuteAfter extends Thread {

    long millis;
    Consumer action;
    public ExecuteAfter(long millis, Consumer action)
    {
        this.millis = millis;
        this.action = action;
        start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(millis);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        action.accept(null);
    }
}
