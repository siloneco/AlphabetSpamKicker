package com.github.siloneco.aspamkicker.config;

import com.github.siloneco.aspamkicker.AlphabetSpamKicker;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DefaultConfig extends Config {

    private int violateAlphabetLength = 10;
    private String warnMessage;
    private String kickMessage;
    private List<Integer> warnAmounts;
    private int kickAmount = 0;
    private boolean includeNumber;

    private boolean cancelChatOnNormal = false;
    private boolean cancelChatOnWarn = true;
    private boolean cancelChatOnKick = true;

    private boolean resetOnKick = false;
    private boolean resetOnLeft = false;

    public DefaultConfig(@NonNull AlphabetSpamKicker plugin) {
        super(plugin, "config.yml", "config.yml");
    }

    @SneakyThrows(value = {Exception.class})
    @Override
    public void loadConfig() {
        super.loadConfig();

        violateAlphabetLength = config.getInt("ViolateAlphabetLength", 20);

        try {
            warnAmounts = config.getStringList("WarnAmounts").stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            warnAmounts = new ArrayList<>();
        }
        kickAmount = config.getInt("KickAmount", 5);

        warnMessage = ChatColor.translateAlternateColorCodes('&', config.getString("WarnMessage", "&e<count>回スパムを行っています！ &cおやめください！"));
        kickMessage = ChatColor.translateAlternateColorCodes('&', config.getString("KickMessage", "&cあなたは<count>回スパムを行ったためKickされました。"));

        includeNumber = config.getBoolean("IncludeNumber", true);
        resetOnKick = config.getBoolean("ResetViolationOnKick", false);
        resetOnLeft = config.getBoolean("ResetViolationOnLeft", true);

        cancelChatOnNormal = config.getBoolean("CancelChatOnNormal", false);
        cancelChatOnWarn = config.getBoolean("CancelChatOnWarn", true);
        cancelChatOnKick = config.getBoolean("CancelChatOnKick", true);
    }
}