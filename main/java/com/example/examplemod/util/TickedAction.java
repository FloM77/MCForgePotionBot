package com.example.examplemod.util;

import java.util.ArrayList;
import java.util.function.Consumer;

public class TickedAction {
    static int currentTick = 0;
    static ArrayList<TickedAction> actions = new ArrayList<>();

    int onTick;
    Consumer action;
    String name;

    public TickedAction(int onTick, Consumer action, String name)
    {
        this.onTick = onTick;
        this.action = action;
        this.name = name;

        actions.add(this);
    }

    public static void increment()
    {
        currentTick++;
        actions.forEach(
                a -> {
                    if(currentTick%a.onTick==0)
                    {
                        a.action.accept(null);
                    }
                }
        );
    }

    public static void clear(String name)
    {
        if(name == null) {actions.clear(); return;}
        actions.removeIf(ta -> ta.name.equals(name));
    }
}
