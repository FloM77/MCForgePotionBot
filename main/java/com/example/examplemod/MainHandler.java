package com.example.examplemod;

import com.example.examplemod.bots.BrewingBot;
import com.example.examplemod.util.ChatCommand;
import com.example.examplemod.util.ExecuteAfter;
import com.example.examplemod.util.TickedAction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.client.Minecraft;
import com.example.examplemod.bots.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.BrewingStandScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;

import static com.example.examplemod.MainHandler.AutoMode.Stopped;
import static com.example.examplemod.MainHandler.AutoMode.Running;

public class MainHandler {

    public enum ClickState { AddNetherWart, AddSugar, AddGlowstone, none };
    static ClickState clickState = ClickState.none;

    public enum AutoMode { Running, Stopped };
    public static AutoMode autoMode = Stopped;
    static AutoMode autoHit = Stopped;



    @SubscribeEvent
     public static void onChat(ClientChatEvent e) {
        if(e.getMessage().charAt(0)=='#')
        {
            ChatCommand.execute(e.getMessage().substring(1));
            e.setCanceled(true);
        }
    }
    public static Minecraft m;
    static double tickC = 0;
    static Screen current, last;

    public static void init()
    {
        new ChatCommand("wt", s -> {
            BrewingBot.setWaterSource(m.objectMouseOver.getHitVec());
        });
        //new TickedAction(79, n -> BrewingBot.fillGlassBottles(), "water");
        new TickedAction(139, n -> BrewingBot.autoModeTick(), "cycle");
        new TickedAction(70, n -> {
            if(autoHit == Running)
                KeyBinding.onTick(MainHandler.m.gameSettings.keyBindAttack.getKey());
        }, "hit");
    }
    public static long delay = 400l;
    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent e)
    {
        current = m.currentScreen;
        if(current instanceof BrewingStandScreen && !(last instanceof BrewingStandScreen))
        {
            BrewingBot.onBrewingStandGui();
        }
        last = m.currentScreen;

        if(clickState != ClickState.none) {
            if (m.objectMouseOver != null) {
                double x = m.objectMouseOver.getHitVec().x;
                double y = m.objectMouseOver.getHitVec().y;
                double z = m.objectMouseOver.getHitVec().z;
                BlockPos bp = new BlockPos(x,y,z);
                BlockState bs = m.world.getBlockState(bp);
                if(bs.getBlock().equals(Blocks.BREWING_STAND))
                {
                    BrewingStandBlock stand = (BrewingStandBlock)bs.getBlock();
                    if(clickState == ClickState.AddNetherWart) new BrewingState(stand, Items.NETHER_WART, bp, m.objectMouseOver.getHitVec());
                    if(clickState == ClickState.AddSugar) new BrewingState(stand, Items.SUGAR, bp, m.objectMouseOver.getHitVec());
                    if(clickState == ClickState.AddGlowstone) new BrewingState(stand, Items.GLOWSTONE_DUST, bp, m.objectMouseOver.getHitVec());

                    clickState = ClickState.none;
                }
            }
        }
        if(autoMode == Running){
            TickedAction.increment();
        }
    }

    static{
        m = Minecraft.getInstance();

        new ChatCommand("bbadd", s -> {
            if(s.length<2) return;
            System.out.println("add " + s[1]);
            switch(s[1])
            {
                case "nw":
                    clickState = ClickState.AddNetherWart;
                    break;
                case "sg":
                    clickState = ClickState.AddSugar;
                    break;
                case "gs":
                    clickState = ClickState.AddGlowstone;
                    break;
            }
        });

        new ChatCommand("start", s -> {
            autoMode = Running;
        });

        new ChatCommand("s", s -> {
            autoMode = Stopped;
        });

        new ChatCommand("drop", s -> {
            if(s.length<2) return;
            StringBuilder sb = new StringBuilder();
            for(int i=1;i<s.length;i++)
            {
                sb.append(s[i]+  (i == s.length -1 ? "" : " "));
            }
            System.out.println(sb.toString());
            new TickedAction(5, n-> BrewingBot.dropFinal(sb.toString()), "drop");
        });

        new ChatCommand("nodrop", s -> {
            TickedAction.clear("drop");
        });

        new ChatCommand("hit", s -> {
            autoHit = Running;
        });

        new ChatCommand("nohit", s -> {
            autoHit = Stopped;
        });

        new ChatCommand("delay", s -> {
            delay = Long.parseLong(s[1]);
        });

        new ChatCommand("drink", s -> {
            if(s.length<2) return;
            StringBuilder sb = new StringBuilder();
            for(int i=1;i<s.length;i++)
            {
                sb.append(s[i]+  (i == s.length -1 ? "" : " "));
            }
            System.out.println(sb.toString());
            BrewingBot.toDrink = sb.toString();
        });
    }
}
