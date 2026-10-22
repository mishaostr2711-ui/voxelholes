package com.example.voxelholes;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue RADIUS = B
            .comment("Crater radius in blocks (1 = small hole, 3 = big crater)")
            .defineInRange("radius", 1, 1, 5);

    public static final ModConfigSpec.IntValue PENETRATION = B
            .comment("How many blocks deep the bullet tunnel goes past the crater (0 = no tunnel)")
            .defineInRange("penetrationDepth", 3, 0, 16);

    public static final ModConfigSpec.BooleanValue ROUGH_EDGES = B
            .comment("Randomly chip the crater edges so it does not look like a perfect sphere")
            .define("roughEdges", true);

    public static final ModConfigSpec.BooleanValue DROP_ITEMS = B
            .comment("Drop items from destroyed blocks")
            .define("dropItems", false);

    public static final ModConfigSpec.BooleanValue INCLUDE_VANILLA = B
            .comment("Also make holes from vanilla projectiles (arrows, snowballs, fireballs...)")
            .define("includeVanilla", false);

    public static final ModConfigSpec.BooleanValue SKIP_BLOCK_ENTITIES = B
            .comment("Do not destroy chests, furnaces and other blocks with inventories")
            .define("skipBlockEntities", true);

    public static final ModConfigSpec.DoubleValue MAX_HARDNESS = B
            .comment("Blocks harder than this are not destroyed (stone = 1.5, obsidian = 50, bedrock is never destroyed)")
            .defineInRange("maxHardness", 50.0, 0.0, 100.0);

    public static final ModConfigSpec SPEC = B.build();
}
