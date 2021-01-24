package me.newt.multiplier.util;

import me.newt.multiplier.MultiplierType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UtilArgumentParsers {

    /**
     * Command argument parser: Argument to UUID.
     * @return NULL if no player was found, else a valid UUID.
     */
    public static UUID parseUUID(String arg) {
        if (arg == null) return null;

        Player player = Bukkit.getPlayer(arg);
        if (player != null) return player.getUniqueId();

        try {
            return UUID.fromString(arg);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /**
     * Command argument parser: Argument to MultiplierType.
     * @return NULL if no MultiplierType was found, else a valid MultiplierType.
     */
    public static MultiplierType parseType(String arg) {
        try {
            return MultiplierType.valueOf(arg.toUpperCase());
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    /**
     * Command argument parser: Argument to Integer.
     * @return NULL if an integer could not be created, else a valid integer.
     */
    public static Integer parseInteger(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}
