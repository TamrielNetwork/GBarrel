package me.newt.multiplier;

import me.newt.multiplier.messages.MessagesAPI;
import me.newt.multiplier.util.UtilTimeFormat;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.util.UUID;

public class Multiplier {

    /**
     * Defined in the database.
     */
    private int id;
    private final MultiplierType type;
    private final int duration;
    private final int multiplier;

    /**
     * Used to keep track of how long this multiplier should last.
     */
    private long timeExpires;
    private UUID activator;

    /**
     * Constructor. (ID of -999 for when the ID has yet to be determined).
     * @param id         In-database id for this multiplier.
     * @param type       In-database type for this multiplier.
     * @param duration   In-database duration for this multiplier.
     * @param multiplier In-database multiplier strength for this multiplier.
     */
    public Multiplier(int id, MultiplierType type, int duration, int multiplier) {
        this.id = id;
        this.type = type;
        this.duration = duration;
        this.multiplier = multiplier;
    }

    /**
     * Get the in-database ID of this multiplier. Multipliers that have literally just been added and multipliers that have been started by an admin do not have an ID!
     * @return The ID of the multiplier.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the database ID of this multiplier. (After it has been determined. Should be used in a callback function).
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the in-database MultiplierType of this multiplier.
     * @return The MultiplierType of the multiplier.
     */
    public MultiplierType getType() {
        return type;
    }

    /**
     * Get the in-database duration of this multiplier.
     * @return The duration of the multiplier.
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Get the in-database multiplier strength of this multiplier.
     * @return The multiplier strength of the multiplier.
     */
    public int getMultiplierStrength() {
        return multiplier;
    }

    /**
     * Get the time when this multiplier should expire.
     * @return The time when this multiplier should expire.
     */
    public long getTimeExpires() {
        return timeExpires;
    }

    /**
     * Set the time when this multiplier should expire.
     * @param timeExpires The time when this multiplier should expire.
     */
    public void setTimeExpires(long timeExpires) {
        this.timeExpires = timeExpires;
    }

    /**
     * Get who activated this multiplier and should receive the thanking notifications.
     * @return The UUID of the activating player.
     */
    public UUID getActivator() {
        return activator;
    }

    /**
     * Set who activated this multiplier and should receive the thanking notifications.
     * @param activator The UUID of the activating player.
     */
    public void setActivator(UUID activator) {
        this.activator = activator;
    }

    /**
     * Get the multiplier's information as text.
     * @return A description of the multiplier.
     */
    public String getMultiplierAsText() {
        return "§2§l#" + id + " §a" + type.getCapitalizedName() + " §7(§f" + multiplier + "x§7) (§f" + UtilTimeFormat.formatDuration((long) duration * 1000) + "§7)";
    }

    /**
     * Get the multiplier's information as text for in the GUI.
     * @param msg       Instance of the message api.
     * @param sessionID Session ID of the player trying to activate.
     * @return A description of the multiplier.
     */
    public BaseComponent[] getMultiplierAsComponent(MessagesAPI msg, String sessionID) {
        Text hoverText = new Text(msg.get("book_activate"));
        HoverEvent onHover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);
        ClickEvent onClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/multiplier run " + sessionID + " " + id);

        ComponentBuilder builder = new ComponentBuilder("* ")
                .color(ChatColor.DARK_GREEN)
                .bold(false)
                .appendLegacy(type.getCapitalizedName() + " multiplier\n(" + multiplier + " x) (" + UtilTimeFormat.formatDuration((long) duration * 1000) + ")\n")
                .color(ChatColor.DARK_GRAY)
                .bold(false)
                .event(onClick)
                .event(onHover);
        return builder.create();
    }
}
