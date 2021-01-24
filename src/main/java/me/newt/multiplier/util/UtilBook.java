package me.newt.multiplier.util;

import me.newt.multiplier.Multiplier;
import me.newt.multiplier.MultiplierPlugin;
import me.newt.multiplier.messages.MessagesAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UtilBook {

    private final MessagesAPI msg;

    /**
     * Constructor.
     * @param multiplierPlugin Instance of the main class.
     */
    public UtilBook(MultiplierPlugin multiplierPlugin) {
        this.msg = multiplierPlugin.getMessagesAPI();
    }

    /**
     * Get a book {@link ItemStack} to show to a player.
     * @param multipliers The list of multipliers from this player.
     * @param sessionID   Player session ID.
     * @return Book item.
     */
    public ItemStack getBook(List<Multiplier> multipliers, UUID sessionID) {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK, 1);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        assert bookMeta != null : "Could not fetch book metadata. Something is seriously broken.";

        // Patch for *Invalid Book Tag* issue since 1.15:
        bookMeta.setTitle("NULL");
        bookMeta.setAuthor("NULL");

        for (BaseComponent[] page : getPages(multipliers, sessionID.toString())) {
            bookMeta.spigot().addPage(page);
        }
        book.setItemMeta(bookMeta);
        return book;
    }

    /**
     * Generate pages from a list of multipliers.
     * @param multipliers List of multipliers.
     * @param sessionID   Player session ID.
     * @return Generated pages.
     */
    private List<BaseComponent[]> getPages(List<Multiplier> multipliers, String sessionID) {
        List<BaseComponent[]> pages = new ArrayList<>();
        ComponentBuilder builder = new ComponentBuilder();
        builder.appendLegacy(msg.get("book_title") + "\n");
        builder.appendLegacy(msg.get("book_description") + "\n\n");
        builder.bold(false);
        builder.appendLegacy(msg.get("book_header") + "\n");

        int size = multipliers.size();
        if (size == 0) {
            builder.appendLegacy(msg.get("book_no_multipliers"));
            builder.bold(false);
            pages.add(builder.create());
            return pages;
        }
        if (size <= 3) {
            builder.append(multipliers.get(0).getMultiplierAsComponent(msg, sessionID));
            if (size > 1) builder.append(multipliers.get(1).getMultiplierAsComponent(msg, sessionID));
            if (size > 2) builder.append(multipliers.get(2).getMultiplierAsComponent(msg, sessionID));
            pages.add(builder.create());
            return pages;
        } else {
            String nextPage = msg.get("book_next_page");
            builder.append(multipliers.get(0).getMultiplierAsComponent(msg, sessionID));
            builder.append(multipliers.get(1).getMultiplierAsComponent(msg, sessionID));
            builder.append(multipliers.get(2).getMultiplierAsComponent(msg, sessionID));
            builder.appendLegacy("\n" + nextPage);
            pages.add(builder.create());

            builder = new ComponentBuilder();
            int onPage = 0;
            for (int shown = 3; shown <= size; shown++) {
                if (onPage == 5) {
                    builder.appendLegacy("\n" + nextPage);
                    pages.add(builder.create());
                    builder = new ComponentBuilder();
                    onPage = 0;
                }
                builder.append(multipliers.get(shown).getMultiplierAsComponent(msg, sessionID));
                onPage++;
            }
            pages.add(builder.create());
            return pages;
        }
    }
}
