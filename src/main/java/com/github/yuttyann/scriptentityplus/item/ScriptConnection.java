/**
 * ScriptEntityPlus - Allow you to add script to any entities.
 * Copyright (C) 2021 yuttyann44581
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */
package com.github.yuttyann.scriptentityplus.item;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.file.json.derived.element.BlockScript;
import com.github.yuttyann.scriptblockplus.item.ChangeSlot;
import com.github.yuttyann.scriptblockplus.item.ItemAction;
import com.github.yuttyann.scriptblockplus.item.RunItem;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.chat.ActionBar;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptblockplus.utils.collection.ObjectMap;
import com.github.yuttyann.scriptentityplus.ScriptEntity;
import com.github.yuttyann.scriptentityplus.SEPermission;
import com.github.yuttyann.scriptentityplus.file.SEConfig;
import com.github.yuttyann.scriptentityplus.json.EntityScript;
import com.github.yuttyann.scriptentityplus.json.EntityScriptJson;
import com.github.yuttyann.scriptentityplus.json.tellraw.*;
import com.github.yuttyann.scriptentityplus.listener.EntityListener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.permissions.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScriptConnection extends ItemAction {

    private static final String KEY_MODE = Utils.randomUUID();

    public static final String KEY_SCRIPT = Utils.randomUUID(), KEY_TELLRAW = Utils.randomUUID();

    public ScriptConnection() {
        super(Material.BONE, () -> "§dScript Connection", SEConfig.SCRIPT_CONNECTION::setListColor);
        setItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }

    @Override
    public boolean hasPermission(@NotNull Permissible permissible) {
        return SEPermission.TOOL_SCRIPT_CONNECTION.has(permissible);
    }

    @Override
    public void slot(@NotNull ChangeSlot changeSlot) {
        SBPlayer sbPlayer = changeSlot.getSBPlayer();
        ToolMode toolMode = sbPlayer.getObjectMap().get(KEY_MODE, ToolMode.NORMAL_SCRIPT);
        ActionBar.send(sbPlayer, "§6§lToolMode: §b§l" + toolMode.getMode());
    }

    @Override
    public void run(@NotNull RunItem runItem) {
        SBPlayer sbPlayer = runItem.getSBPlayer();
        ToolMode toolMode = sbPlayer.getObjectMap().get(KEY_MODE, ToolMode.NORMAL_SCRIPT);
        switch (runItem.getAction()) {
            case RIGHT_CLICK_BLOCK:
                break;
            case RIGHT_CLICK_AIR:
                if (sbPlayer.getObjectMap().getBoolean(EntityListener.KEY_OFF)) {
                    off(runItem, sbPlayer, toolMode);
                } else {
                    main(runItem, sbPlayer, toolMode);
                }
                break;
            case LEFT_CLICK_BLOCK:
                left(runItem, sbPlayer, toolMode);
                break;
            case LEFT_CLICK_AIR:
                sbPlayer.getObjectMap().put(KEY_MODE, toolMode = ToolMode.getNextMode(toolMode));
                ActionBar.send(sbPlayer, "§6§lToolMode: §b§l" + toolMode.getMode());
                break;
            default:
        }
    }

    @NotNull
    public Optional<Entity> getEntity(@NotNull SBPlayer sbPlayer) {
        ObjectMap objectMap = sbPlayer.getObjectMap();
        return Optional.ofNullable(objectMap.get(EntityListener.KEY_CLICK_ENTITY));
    }

    // <scriptkey>|<fullCoords>|0|<playerId>|<randomUUID>
    private void left(@NotNull RunItem runItem, @NotNull SBPlayer sbPlayer, @NotNull ToolMode toolMode) {
        BlockCoords blockCoords = Objects.requireNonNull(runItem.getBlockCoords());
        if (runItem.isSneaking()) {
            String fullCoords = blockCoords.getFullCoords();
            StringBuilder textBuilder = new StringBuilder();
            JsonBuilder jsonBuilder = new JsonBuilder();
            jsonBuilder.add(new JsonElement("ScriptKeys: ", ChatColor.GOLD, ChatFormat.BOLD));
            for (ScriptKey scriptKey : ScriptKey.iterable()) {
                if (BlockScriptJson.get(scriptKey).has(blockCoords)) {
                    textBuilder.append(scriptKey.toString()).append('|');
                    textBuilder.append(fullCoords).append('|');
                    textBuilder.append('0').append('|');
                    textBuilder.append(sbPlayer.getName()).append('|');
                    textBuilder.append(KEY_TELLRAW);
                    JsonElement element = new JsonElement(scriptKey.toString(), ChatColor.GREEN, ChatFormat.BOLD);
                    element.setClickEvent(ClickEventType.RUN_COMMAND, tellraw(textBuilder.toString()));
                    element.setHoverEvent(HoverEventType.SHOW_TEXT, getTexts(scriptKey, blockCoords));
                    jsonBuilder.add(element);
                    textBuilder.setLength(0);
                } else {
                    jsonBuilder.add(new JsonElement(scriptKey.toString(), ChatColor.RED));
                }
                if (scriptKey.ordinal() != ScriptKey.size() - 1) {
                    jsonBuilder.add(new JsonElement(", ", ChatColor.GRAY));
                }
            }
            ScriptEntity.dispatchCommand(sbPlayer.getWorld(), "tellraw " + sbPlayer.getName() + " " + jsonBuilder.toJson());
        } else {
            sbPlayer.getObjectMap().put(KEY_MODE, toolMode = ToolMode.getNextMode(toolMode));
            ActionBar.send(sbPlayer, "§6§lToolMode: §b§l" + toolMode.getMode());
        }
    }

    private void main(@NotNull RunItem runItem, @NotNull SBPlayer sbPlayer, @NotNull ToolMode toolMode) {
        Optional<Entity> entity = getEntity(sbPlayer);
        if (!entity.isPresent()) {
            return;
        }
        if (runItem.isSneaking()) {
            EntityScriptJson entityScriptJson = EntityScriptJson.get(entity.get().getUniqueId());
            if (!entityScriptJson.exists()) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
                return;
            }
            entityScriptJson.deleteFile();
            SEConfig.SCRIPT_REMOVE_ENTITY.replace(entity.get().getType().name()).send(sbPlayer);
        } else {
            ObjectMap objectMap = sbPlayer.getObjectMap();
            if (!objectMap.has(KEY_SCRIPT)) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
                return;
            }
            EntityScriptJson entityScriptJson = EntityScriptJson.get(entity.get().getUniqueId());
            EntityScript entityScript = entityScriptJson.load();
            String[] data = objectMap.get(KEY_SCRIPT, new String[0]);
            if (data.length != 2) {
                return;
            }
            if (BlockScriptJson.get(ScriptKey.valueOf(data[0])).has(BlockCoords.fromString(data[1]))) {
                entityScript.getScripts(toolMode).add(data[0] + '|' + data[1]);
            }
            entityScript.setInvincible(true);
            try {
                entityScriptJson.saveJson();
            } finally {
                objectMap.remove(KEY_SCRIPT);
            }
            SEConfig.SCRIPT_SETTING_ENTITY.replace(toolMode.getMode(), entity.get().getType().name()).send(sbPlayer);
        }
    }

    private void off(@NotNull RunItem runItem, @NotNull SBPlayer sbPlayer, @NotNull ToolMode toolMode) {
        Optional<Entity> entity = getEntity(sbPlayer);
        if (!entity.isPresent()) {
            return;
        }
        EntityScriptJson entityScriptJson = EntityScriptJson.get(entity.get().getUniqueId());
        EntityScript entityScript = entityScriptJson.load();
        if (runItem.isSneaking()) {
            if (!entityScriptJson.exists()) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
                return;
            }
            String uuid = entity.get().getUniqueId().toString();
            JsonBuilder builder = new JsonBuilder();
            JsonElement element = new JsonElement("Invincible", ChatColor.AQUA, ChatFormat.BOLD);
            element.setHoverEvent(HoverEventType.SHOW_TEXT, StringUtils.setColor(SEConfig.INVINCIBLE_TEXT.getValue()));
            builder.add(element);
            setButton(builder, sbPlayer, "Invincible", uuid);
            builder.add(new JsonElement("\n", ChatColor.WHITE));

            element = new JsonElement("Projectile", ChatColor.AQUA, ChatFormat.BOLD);
            element.setHoverEvent(HoverEventType.SHOW_TEXT, StringUtils.setColor(SEConfig.PROJECTILE_TEXT.toString()));
            builder.add(element);
            setButton(builder, sbPlayer, "Projectile", uuid);

            sbPlayer.sendMessage("--------- [ Entity Settings ] ---------");
            ScriptEntity.dispatchCommand(sbPlayer.getWorld(), "minecraft:tellraw " + sbPlayer.getName() + " " + builder.toJson());
            sbPlayer.sendMessage("------------------------------------");
        } else {
            if (entityScript.getScripts(toolMode).size() < 1) {
                SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
                return;
            }
            sbPlayer.sendMessage("----- [ Scripts ] -----");
            int index = 0;
            for (String script : entityScript.getScripts(toolMode)) {
                List<String> list = StringUtils.split(script, '|');
                ScriptKey scriptKey = ScriptKey.valueOf(list.get(0));
                JsonBuilder builder = new JsonBuilder();
                builder.add(new JsonElement("Index" + (index++) + "=", ChatColor.WHITE));

                JsonElement element = new JsonElement(scriptKey.toString(), ChatColor.GREEN, ChatFormat.BOLD);
                String command = "/sbp " + scriptKey.getName() + " run " + removeBlank(list.get(1)).replace(',', ' ');
                element.setClickEvent(ClickEventType.SUGGEST_COMMAND, command);
                element.setHoverEvent(HoverEventType.SHOW_TEXT, getTexts(scriptKey, BlockCoords.fromString(list.get(1))));
                builder.add(element);

                ScriptEntity.dispatchCommand(sbPlayer.getWorld(), "minecraft:tellraw " + sbPlayer.getName() + " " + builder.toJson());
            }
            sbPlayer.sendMessage("---------------------");
        }
    }

    @NotNull
    private String removeBlank(@NotNull String source) {
        return StringUtils.remove(source, ' ');
    }

    // <type>=<value>|<uuid>|1|<playerId>|<randomUUID>
    private void setButton(@NotNull JsonBuilder builder, @NotNull SBPlayer sbPlayer, @NotNull String name, @NotNull String uuid) {
        JsonElement element = new JsonElement(" [", ChatColor.GOLD);
        builder.add(element);
        element = new JsonElement("Enabled", ChatColor.GREEN);
        element.setClickEvent(ClickEventType.RUN_COMMAND, tellraw(name + "=Enabled|" + uuid + "|1|" + sbPlayer.getName() + '|' + KEY_TELLRAW));
        builder.add(element);
        element = new JsonElement("]", ChatColor.GOLD);
        builder.add(element);

        element = new JsonElement("  [", ChatColor.GOLD);
        builder.add(element);
        element = new JsonElement("Disabled", ChatColor.RED);
        element.setClickEvent(ClickEventType.RUN_COMMAND, tellraw(name + "=Disabled|" + uuid + "|1|" + sbPlayer.getName() + '|' + KEY_TELLRAW));
        builder.add(element);
        element = new JsonElement("]", ChatColor.GOLD);
        builder.add(element);

        element = new JsonElement("  [", ChatColor.GOLD);
        builder.add(element);
        element = new JsonElement("View", ChatColor.LIGHT_PURPLE);
        element.setClickEvent(ClickEventType.RUN_COMMAND, tellraw(name + "=View|" + uuid + "|1|" + sbPlayer.getName() + '|' + KEY_TELLRAW));
        builder.add(element);
        element = new JsonElement("]", ChatColor.GOLD);
        builder.add(element);
    }

    @NotNull
    private String getTexts(@NotNull ScriptKey scriptKey, @NotNull BlockCoords blockCoords) {
        if (!BlockScriptJson.get(scriptKey).has(blockCoords)) {
            return "null";
        }
        BlockScript blockScript = BlockScriptJson.get(scriptKey).load(blockCoords);
        StringBuilder builder = new StringBuilder();
        StringJoiner joiner = new StringJoiner("\n§6- §b");
        blockScript.getScripts().forEach(joiner::add);
        Stream<String> author = blockScript.getAuthors().stream().map(Utils::getName);
        builder.append("§eAuthor: §a").append(author.collect(Collectors.joining(", ")));
        builder.append("\n§eCoords: §a").append(blockCoords.getFullCoords());
        builder.append("\n§eScripts:§e\n§6- §b").append(joiner.toString());
        return builder.toString();
    }

    @NotNull
    private String tellraw(@NotNull String text) {
        return "/tellraw @s \"" + text + "\"";
    }
}