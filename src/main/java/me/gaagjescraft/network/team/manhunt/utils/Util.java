package me.gaagjescraft.network.team.manhunt.utils;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import me.gaagjescraft.network.team.manhunt.Manhunt;
import me.gaagjescraft.network.team.manhunt.games.Game;
import me.gaagjescraft.network.team.manhunt.games.GameSetup;
import me.gaagjescraft.network.team.manhunt.games.PlayerType;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Util {

    public static List<String> r(List<String> items, String search, String replaceAll) {
        items = new ArrayList<>(items);
        for (int i = 0; i < items.size(); i++) {
            items.set(i, items.get(i).replaceAll(search, replaceAll));
        }
        return items;
    }

    public static String c(String a) {
        return ChatColor.translateAlternateColorCodes('&', a);
    }

    public static void sendTitle(Player player, String cfgMsg, int in, int stay, int out) {
        String[] titles = cfgMsg.split("\\\\n");
        if (titles.length == 0) return;
        player.sendTitle(c(titles[0]), titles.length >= 2 ? c(titles[1]) : "", in, stay, out);
    }

    public void createGameServer(GameSetup setup, String targetGameServer) {
        Player host = setup.getHost();
        String json = "{'server_name': '" + Manhunt.get().getCfg().serverName + "', 'game_server':'" + targetGameServer + "', 'host':'" + host.getName() + "', 'host_uuid':'" + setup.getHost().getUniqueId().toString() + "', " +
                "'max_players':" + setup.getMaxPlayers() + ", 'headstart':'" + setup.getHeadstart().name() + "', " +
                "'allow_twists':" + setup.isAllowTwists() + ", 'daylight_cycle':" + setup.isDoDaylightCycle() + ", 'friendly_fire':" + setup.isAllowFriendlyFire() + "}";

        if (!Manhunt.get().getBungeeSocketManager().sendMessage("createGame", json)) {
            setup.getHost().sendMessage(ChatColor.RED + "We had some trouble connecting to the other servers. Please inform a staff member.");
        }
    }

    public void createGameReadyMessage(Game game) {
        String json = "{'server_name': '" + Manhunt.get().getCfg().serverName + "', 'game':'" + game.getIdentifier() + "', 'host_uuid':'" + game.getHostUUID().toString() + "'}";
        Manhunt.get().getBungeeSocketManager().sendMessage("gameReady", json);
    }

    public void createGameEndedMessage(Game game) {
        String json = "{'game':'" + game.getIdentifier() + "'}";
        Manhunt.get().getBungeeSocketManager().sendMessage("gameEnded", json);
    }

    public void createEndGameMessage(Game game, boolean forceStop) {
        String json = "{'game':'" + game.getIdentifier() + "', 'force_stop':" + forceStop + "}";
        Manhunt.get().getBungeeSocketManager().sendMessage("endGame", json);
    }

    public void createUpdateGameMessage(Game game) {
        JsonObject object = new JsonObject();
        object.addProperty("server", Manhunt.get().getCfg().serverName);
        object.addProperty("id", game.getIdentifier());
        object.addProperty("host_uuid", game.getHostUUID().toString());
        object.addProperty("allow_twists", game.isTwistsAllowed());
        object.addProperty("max_players", game.getMaxPlayers());
        object.addProperty("headstart", game.getHeadStart().name());
        object.addProperty("daylight_cycle", game.isDoDaylightCycle());
        object.addProperty("friendly_fire", game.isAllowFriendlyFire());
        object.addProperty("status", game.getStatus().name());
        object.addProperty("runner_count", game.getOnlinePlayers(PlayerType.RUNNER).size());
        object.addProperty("hunter_count", game.getOnlinePlayers(PlayerType.HUNTER).size());

        Manhunt.get().getBungeeSocketManager().sendMessage("updateGame", object.toString());
    }

    public void spawnAcidParticles(Location loc, boolean hit) {
        for (int i1 = 0; i1 < 50; i1++) {
            if (hit) {
                loc.getWorld().spawnParticle(org.bukkit.Particle.SPELL_MOB, loc.add(0, 0.2, 0), 0, 181 / 255D, 5 / 255D, 38 / 255D, 1);
            } else {
                if (i1 % 2 == 0) {
                    loc.getWorld().spawnParticle(org.bukkit.Particle.SPELL_MOB, loc.add(0, 0.2, 0), 0, 26 / 255D, 102 / 255D, 14 / 255D, 1);
                } else {
                    loc.getWorld().spawnParticle(Particle.SPELL_MOB, loc.add(0, 0.2, 0), 0, 66 / 255D, 161 / 255D, 51 / 255D, 1);
                }
            }
        }
    }

    public String secondsToTimeString(long seconds, String format) {
        if (format == null) format = "simplified";

        long secs;
        int mins = 0;
        int hours = 0;

        if (seconds < 60) {
            secs = seconds;
        } else if (seconds < 3600) {
            mins = (int) seconds / 60;
            secs = (int) seconds % 60;
        } else {
            hours = (int) seconds / 3600;
            int remainder = (int) seconds - hours * 3600;
            mins = (int) remainder / 60;
            remainder = remainder - mins * 60;
            secs = (int) remainder;
        }

        if (format.equals("simplified")) {
            if (hours > 0) {
                return hours + ":" + (mins < 10 ? "0" + mins : mins) + ":" + (secs < 10 ? "0" + secs : secs);
            } else if (mins > 0) {
                return mins + ":" + (secs < 10 ? "0" + secs : secs);
            } else {
                return secs + "";
            }
        }
        if (format.equals("simplified-zeros")) {
            if (hours > 0) {
                return (hours < 10 ? "0" + hours : hours) + ":" + (mins < 10 ? "0" + mins : mins) + ":" + (secs < 10 ? "0" + secs : secs);
            } else if (mins > 0) {
                return (mins < 10 ? "0" + mins : mins) + ":" + (secs < 10 ? "0" + secs : secs);
            } else {
                return secs + "";
            }
        }
        if (format.equals("string")) {
            if (hours > 0) {
                if (mins == 0 && secs == 0) {
                    return hours + " hour" + (hours > 1 ? "s" : "");
                } else if (secs == 0) {
                    return hours + " hour" + (hours > 1 ? "s" : "") + " and " + mins + " minute" + (mins > 1 ? "s" : "");
                }
                return hours + " hours, " + mins + " minutes, and " + seconds + " seconds";
            } else if (mins > 0) {
                if (secs == 0) {
                    return mins + " minute" + (mins > 1 ? "s" : "");
                }
                return mins + " minute" + (mins > 1 ? "s" : "") + " and " + secs + " second" + (secs > 1 ? "s" : "");
            } else {
                return secs + " second" + (secs > 1 ? "s" : "");
            }
        }

        format = format.replaceAll("(?<!\\\\)mm", mins < 10 ? "0" + mins : mins + "");
        format = format.replaceAll("(?<!\\\\)m", mins + "");
        format = format.replaceAll("(?<!\\\\)ss", secs < 10 ? "0" + secs : secs + "");
        format = format.replaceAll("(?<!\\\\)s", secs + "");

        return format;
    }

    public ItemStack getCustomTextureHead(String value) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", value));
        Field profileField = null;
        try {
            profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        head.setItemMeta(meta);
        return head;
    }

    public int getProtocol(Player player) {
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            return com.viaversion.viaversion.api.Via.getAPI().getPlayerVersion(player.getUniqueId());
        }
        return -1;
    }

}
