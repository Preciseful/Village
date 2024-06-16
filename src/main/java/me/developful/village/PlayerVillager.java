package me.developful.village;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static me.developful.village.Village.VILLAGERS;

public class PlayerVillager implements Listener {
    public String username;
    public double balance = 0;
    public Job job = new Job("NONE");

    public PlayerVillager(String username, double balance, Job job) {
        this.username = username;
        this.balance = balance;
        this.job = job;
    }

    public PlayerVillager(String username, double balance) {
        this.username = username;
        this.balance = balance;
    }

    public PlayerVillager(String username) {
        this.username = username;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!VILLAGERS.containsKey(player.getName()))
            VILLAGERS.put(player.getName(), new PlayerVillager(player.getName()));
    }

    @EventHandler
    public void chatEvent(AsyncChatEvent event) {
        event.setCancelled(true);

        var player = event.getPlayer();
        var job = VILLAGERS.get(player.getName()).job;

        var text = event.message().color(NamedTextColor.WHITE);
        var title = Component.text("[" + job.type + " " + (int)job.level + "] ", NamedTextColor.GREEN);
        var message = Component.text(player.getName() + ": ", NamedTextColor.WHITE);

        player.sendMessage(title.append(message).append(text));
    }
}
