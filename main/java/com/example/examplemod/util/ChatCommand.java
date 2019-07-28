package com.example.examplemod.util;

import java.util.HashMap;
import java.util.function.Consumer;

public class ChatCommand {
    Consumer<String[]> action;

    public ChatCommand(String trigger, Consumer<String[]> action)
    {
        this.action = action;
        commands.put(trigger, this);
    }

    static HashMap<String, ChatCommand> commands = new HashMap<>();
    public static void execute(String text)
    {
        commands.forEach((s,cc) -> {
            if(text.split(" ")[0].equals(s)) cc.action.accept(text.split(" "));
        });
    }


}
