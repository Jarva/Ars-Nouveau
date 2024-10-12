package com.hollingsworth.arsnouveau.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WeatherUtil {
    public static boolean isRainingAt(Level level, BlockPos pos) {
        return pos.getX() >= 0;
    }
}
