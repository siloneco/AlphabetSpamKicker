package com.github.siloneco.aspamkicker;

import com.github.siloneco.aspamkicker.config.DefaultConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class AlphabetSpamKicker extends JavaPlugin implements Listener {

    private DefaultConfig config;

    @Override

    public void onEnable() {
        config = new DefaultConfig(this);
        config.loadConfig();

        Bukkit.getPluginManager().registerEvents(this, this);

        Bukkit.getLogger().info(getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info(getName() + " disabled.");
    }

    private HashMap<UUID, Integer> violationCounter = new HashMap<>();
    private List<UUID> holdPlayerViolationList = new ArrayList<>();

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String msg = ChatColor.stripColor(e.getMessage());

        if (msg.contains(" ") || msg.length() < config.getViolateAlphabetLength()) {
            return;
        }
        if (!isHalfAlphanumeric(msg, config.isIncludeNumber())) {
            return;
        }

        int amount = violationCounter.getOrDefault(p.getUniqueId(), 0) + 1;
        violationCounter.put(p.getUniqueId(), amount);

        if (config.getKickAmount() <= amount) {
            Bukkit.getScheduler().runTask(this, () -> {
                p.kickPlayer(config.getKickMessage().replace("<count>", amount + ""));
            });
            if (config.isCancelChatOnKick()) {
                e.setCancelled(true);
            }
            if (!config.isResetOnKick()) {
                holdPlayerViolationList.add(p.getUniqueId());
            }
        } else if (config.getWarnAmounts().contains(amount)) {
            p.sendMessage(config.getWarnMessage().replace("<count>", amount + ""));

            if (config.isCancelChatOnWarn()) {
                e.setCancelled(true);
            }
        } else {
            if (config.isCancelChatOnNormal()) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onLeft(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (holdPlayerViolationList.contains(p.getUniqueId())) {
            holdPlayerViolationList.remove(p.getUniqueId());
            return;
        }

        if (config.isResetOnLeft()) {
            violationCounter.remove(p.getUniqueId());
        }
    }

    private boolean isHalfAlphanumeric(String str, boolean includeNumber) {
        if (includeNumber) {
            return Pattern.matches("^[0-9a-zA-Z]+$", str);
        } else {
            return Pattern.matches("^[a-zA-Z]+$", str);
        }
    }
}
