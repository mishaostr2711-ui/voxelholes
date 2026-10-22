package com.example.voxelholes;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(VoxelHoles.MODID)
public class VoxelHoles {
    public static final String MODID = "voxelholes";

    public VoxelHoles(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
