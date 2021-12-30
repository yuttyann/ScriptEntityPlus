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
package com.github.yuttyann.scriptentityplus.listener;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.enums.Permission;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptentityplus.SEPermission;
import com.github.yuttyann.scriptentityplus.ScriptEntity;
import com.github.yuttyann.scriptentityplus.file.SEConfig;
import com.github.yuttyann.scriptentityplus.json.EntityScript;
import com.github.yuttyann.scriptentityplus.json.EntityScriptJson;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public class PlayerListener implements Listener {

    public static final String KEY_TOOL = UUID.nameUUIDFromBytes("KEY_TOOL".getBytes()).toString();
    public static final String KEY_SETTINGS = UUID.nameUUIDFromBytes("KEY_SETTINGS".getBytes()).toString();
    public static final String KEY_SCRIPT = Utils.randomUUID();

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage().startsWith("/") ? event.getMessage().substring(1) : event.getMessage();
        if (Permission.COMMAND_CHECKVER.has(player) && (command.equals("sbp checkver") || command.equals("scriptblockplus checkver"))) {
            ScriptEntity.getInstance().checkUpdate(player, true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerCommand(ServerCommandEvent event) {
        CommandSender sender = event.getSender();
        String command = event.getCommand().startsWith("/") ? event.getCommand().substring(1) : event.getCommand();
        if (Permission.COMMAND_CHECKVER.has(sender) && (command.equals("sbp checkver") || command.equals("scriptblockplus checkver"))) {
            ScriptEntity.getInstance().checkUpdate(sender, true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String chat = event.getMessage();
        if (chat.lastIndexOf("/" + KEY_TOOL) != -1 && SEPermission.TOOL_SCRIPT_CONNECTION.has(player)) {
            String[] array = { StringUtils.split(chat, '/').get(0) };
            String type = ScriptKey.valueOf(StringUtils.split(array[0], '|').get(0)).getName();
            ScriptBlock.getSBPlayer(player).getObjectMap().put(KEY_SCRIPT, array);
            SEConfig.SCRIPT_SELECT.replace(type).send(player);
            event.setCancelled(true);
        } else if (chat.lastIndexOf("/" + KEY_SETTINGS) != -1 && SEPermission.TOOL_SCRIPT_CONNECTION.has(player)) {
            List<String> list = StringUtils.split(StringUtils.split(chat, '/').get(0), '=');
            EntityScriptJson entityScriptJson = EntityScriptJson.get(UUID.fromString(list.get(1)));
            if (entityScriptJson.exists()) {
                setting(player, list, entityScriptJson.load());
                entityScriptJson.saveJson();
            }
            event.setCancelled(true);
        }
    }

    private void setting(@NotNull Player player, @NotNull List<String> list, @NotNull EntityScript entityScript) {
        SettingType settingType = SettingType.get(list.get(0));
        ButtonType buttonType = ButtonType.get(list.get(2));
        if (settingType != null && buttonType != null) {
            switch (buttonType) {
                case ENABLED:
                case DISABLED:
                    settingType.set(entityScript, buttonType.isEnabled());
                    SEConfig.SETTING_VALUE.replace(list.get(0), buttonType.getType()).send(player);
                    break;
                case VIEW:
                    String type = ButtonType.get(settingType.is(entityScript)).getType();
                    SEConfig.SETTING_VIEW.replace(list.get(0), type).send(player);
                    break;
            }
        }
    }
}