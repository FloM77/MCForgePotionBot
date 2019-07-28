package com.example.examplemod.bots;

import com.example.examplemod.MainHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.BrewingStandBlock;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.compress.archivers.dump.DumpArchiveEntry;
import sun.applet.Main;

import java.util.ArrayList;

public class BrewingState {
    public static ArrayList<BrewingState> states = new ArrayList<>();
    BrewingStandBlock block;
    BlockPos pos;
    Item resource;
    Vec3d playerlook;

    public BrewingState(BrewingStandBlock bs, Item resource, BlockPos pos, Vec3d playerlook)
    {
        this.pos = pos;
        this.resource = resource;
        block = bs;
        this.playerlook = playerlook;
        System.out.println("new state added " + resource.getName().getString());

        openGUI();
        states.add(this);
    }

    public void openGUI()
    {
        BrewingBot.lookAndClick(playerlook);
    }
}
