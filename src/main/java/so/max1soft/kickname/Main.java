package so.max1soft.kickname;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener, CommandExecutor {

    private List<String> keywords;

    @Override
    public void onEnable() {

        saveDefaultConfig();
        loadKeywordsFromConfig();


        getCommand("sokickname").setExecutor(this);


        getServer().getPluginManager().registerEvents(this, this);


        new BukkitRunnable() {
            @Override
            public void run() {
                checkForKeywordsAndDigits();
            }
        }.runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("sokickname")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                loadKeywordsFromConfig();
                sender.sendMessage("ПЕРЕЗАГРУЖЕНО! Я В ПОИСКЕ УЕБКОВ");
                return true;
            }
            sender.sendMessage("Usage: /sokickname reload");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (containsKeyword(player.getName()) || hasMoreThan7Digits(player.getName())) {
            getLogger().info("Подозрение на бота: " + player.getName());
            player.kickPlayer(getConfig().getString("kick-reason"));
        }
    }

    private void checkForKeywordsAndDigits() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (containsKeyword(player.getName()) || hasMoreThan7Digits(player.getName())) {
                getLogger().info("Подозрение на бота: " + player.getName());
                player.kickPlayer(getConfig().getString("kick-reason"));
            }
        }
    }

    private boolean containsKeyword(String name) {
        String lowerName = name.toLowerCase();
        for (String keyword : keywords) {
            if (lowerName.contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMoreThan7Digits(String name) {
        int digitCount = 0;
        for (char c : name.toCharArray()) {
            if (Character.isDigit(c)) {
                digitCount++;
                if (digitCount > 7) {
                    return true;
                }
            }
        }
        return false;
    }

    private void loadKeywordsFromConfig() {
        FileConfiguration config = getConfig();
        keywords = config.getStringList("keywords");
        if (keywords == null || keywords.isEmpty()) {
            getLogger().warning("No keywords found in config.yml! Using default values.");
            keywords = new ArrayList<>();
            keywords.add("bot");
        }
    }
}
