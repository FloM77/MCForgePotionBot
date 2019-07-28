package com.example.examplemod;

import com.example.examplemod.bots.BrewingBot;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.client.Minecraft;
import com.example.examplemod.bots.*;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.BrewingStandScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import static com.example.examplemod.MainHandler.AutoMode.Stopped;
import static com.example.examplemod.MainHandler.AutoMode.Running;

public class MainHandler {

    enum ClickState { AddNetherWart, AddSugar, AddGlowstone, none };
    static ClickState clickState = ClickState.none;

    enum AutoMode { Running, Stopped };
    static AutoMode autoMode = Stopped;
    static AutoMode autoHit = Stopped;

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
            dropfinal = sb.toString();
        });
    }

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
    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent e)
    {
        if(tickC==0)
        {
            new ChatCommand("wt", s -> {
                water = m.objectMouseOver.getHitVec();
            });
        }
        tickC++;
        current = m.currentScreen;
        if(current instanceof BrewingStandScreen && !(last instanceof BrewingStandScreen))
        {
            onBrewingStandGui();
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

        if(autoMode == Running)
        {
            if(tickC%5==0) BrewingBot.dropFinal(dropfinal);
            if(tickC%200==0) {autoModeTick();}
            else if(tickC%100 == 0) {
                if(water!=null)
                {
                    ArrayList<ItemStack> bottles = new ArrayList<>();
                    m.player.inventory.mainInventory.forEach(is -> {if(is.getDisplayName().getString().equals("Water Bottle")) { bottles.add(
                            is
                    );}});
                    if(bottles.size()<10) {
                        m.player.closeScreen();
                        BrewingBot.equip("Glass Bottle");
                        BrewingBot.lookAndClick(water);
                        new Thread(){
                            @Override
                            public void run()
                            {
                                try {
                                    Thread.sleep(200l);
                                } catch(Exception e){}
                                BrewingBot.equip("x");
                            }
                        }.start();
                    }
                }
            }
        }
    }

    static Vec3d water = null;

    static String dropfinal = "a";
    static int amt = 0;
    public static void autoModeTick()
    {
        amt++;
        m.player.closeScreen();
        if(BrewingState.states.size()==0) return;
        BrewingState cur = BrewingState.states.get(amt%BrewingState.states.size());
        cur.openGUI();
    }

    public static void onBrewingStandGui()
    {
        System.out.println("Brewing Stand opened");

        BrewingStandScreen bss = (BrewingStandScreen) m.currentScreen;

        BrewingBot.removeBottles(bss);
        BrewingBot.fillResource(bss, new ArrayList<Item>()
                {{ add(Items.NETHER_WART); add(Items.SUGAR); add(Items.BLAZE_POWDER); add(Items.GLOWSTONE_DUST); }}
        );
    }
}
