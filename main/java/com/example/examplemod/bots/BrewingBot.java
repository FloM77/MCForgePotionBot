package com.example.examplemod.bots;

import com.example.examplemod.MainHandler;
import com.example.examplemod.util.ExecuteAfter;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.inventory.BrewingStandScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class BrewingBot {
    public static void removeBottles(BrewingStandScreen bss)
    {
        ArrayList<Slot> bottles = new ArrayList<>();
        bottles.add(bss.getContainer().inventorySlots.get(0));
        bottles.add(bss.getContainer().inventorySlots.get(1));
        bottles.add(bss.getContainer().inventorySlots.get(2));

        Slot slotResource = bss.getContainer().inventorySlots.get(3);
        for (Slot s:
             bottles) {
            boolean take = true;
            if(slotResource.getStack().getItem().equals(Items.NETHER_WART))
            {
                if(s.getStack().getDisplayName().getString().equals("Water Bottle"))
                take = false;
            } else if (s.getStack().getDisplayName().getString().equals("Awkward Potion"))
            {
                take = false;
            }
            if(take)
            MainHandler.m.playerController.windowClick(bss.getContainer().windowId, s.slotNumber, 1, ClickType.QUICK_MOVE, MainHandler.m.player);
        }
    }

    public static void lookAndClick(Vec3d vec)
    {
        MainHandler.m.player.lookAt(EntityAnchorArgument.Type.EYES, vec);
        KeyBinding.onTick(MainHandler.m.gameSettings.keyBindUseItem.getKey());
    }

    public static boolean equip(String display)
    {
        if(display.equals("x"))
        {
            equip(Items.AIR.getName().getString());
            return false;
        }
        ArrayList<ItemStack> its = new ArrayList<>();
        MainHandler.m.player.inventory.mainInventory.forEach(
                is -> { if(is.getDisplayName().getString().equals(display)){
                    its.add(is);
                } }
        );
        if(its.size()==0) return false;
        MainHandler.m.playerController.pickItem(MainHandler.m.player.inventory.mainInventory.indexOf(its.get(0)));
        return true;
    }

    public static void dropFinal(String display)
    {
        if(equip(display))
            new ExecuteAfter(200l, n -> {MainHandler.m.player.dropItem(true);});
    }

    public static void fillResource(BrewingStandScreen bss, ArrayList<Item> i)
    {
        ArrayList<Slot> slotWOR = new ArrayList<>();
        bss.getContainer().inventorySlots.forEach(s -> { if(s.slotNumber>4) slotWOR.add(s); });
        ArrayList<Slot> slotR = new ArrayList<>();
        slotWOR.forEach(s -> {
            i.forEach(it -> {
                if(s.getStack().getItem().equals(it)){ slotR.add(s); }
            });
        });
        Slot slotResource = bss.getContainer().inventorySlots.get(3);
        if(slotResource.getStack().getItem().equals(Items.NETHER_WART))
        {
            slotWOR.forEach(s -> { if(s.getStack().getDisplayName().getString().equals("Water Bottle")) slotR.add(s); });
        }
        else
        {
            slotWOR.forEach(s -> { if(s.getStack().getDisplayName().getString().equals("Awkward Potion")) slotR.add(s); });
        }
        slotR.forEach(s -> {
            int sn = s.slotNumber;
            MainHandler.m.playerController.windowClick(bss.getContainer().windowId, sn, 1, ClickType.QUICK_MOVE, MainHandler.m.player);
        });
    }

    static Vec3d water = null;
    public static void setWaterSource(Vec3d waterSource)
    {
        water = waterSource;
    }

    public static void fillGlassBottles()
    {
        if(water!=null)
        {
            ArrayList<ItemStack> bottles = new ArrayList<>();
            MainHandler.m.player.inventory.mainInventory.forEach(is -> {if(is.getDisplayName().getString().equals("Water Bottle")) { bottles.add(
                    is
            );}});
            if(bottles.size()<10) {
                MainHandler.m.player.closeScreen();
                BrewingBot.equip("Glass Bottle");
                BrewingBot.lookAndClick(water);
                new ExecuteAfter(200l , n -> { BrewingBot.equip("x"); });
            }
        }
    }

    static int amt = 0;
    public static void autoModeTick()
    {
        amt++;
        MainHandler.m.player.closeScreen();
        if(BrewingState.states.size()==0) return;
        BrewingState cur = BrewingState.states.get(amt%BrewingState.states.size());
        cur.openGUI();
    }
}
