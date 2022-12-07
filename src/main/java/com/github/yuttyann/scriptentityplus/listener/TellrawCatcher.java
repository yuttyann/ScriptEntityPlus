package com.github.yuttyann.scriptentityplus.listener;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptentityplus.SEPermission;
import com.github.yuttyann.scriptentityplus.file.SEConfig;
import com.github.yuttyann.scriptentityplus.item.ScriptConnection;
import com.github.yuttyann.scriptentityplus.json.EntityScriptJson;

class TellrawCatcher {

    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}");

    static void onCatch(@NotNull String message, @NotNull Cancellable event) {
        if (StringUtils.isEmpty(message)) {
            return;
        }
        int count = count(message, '|');
        if (count != 4) {
            return;
        }
        List<String> split = StringUtils.split(message, '|');
        if (split.size() != 5 || !UUID_PATTERN.matcher(split.get(4)).matches()) {
            return;
        }
        event.setCancelled(true);
        if (ScriptConnection.KEY_TELLRAW.equals(split.get(4))) {
            try {
                Player player = Bukkit.getPlayerExact(split.get(3));
                if (player == null || !SEPermission.TOOL_SCRIPT_CONNECTION.has(player)) {
                    return;
                }
                switch (Integer.parseInt(split.get(2))) {
                    case 0: {
                        ScriptKey scriptKey = ScriptKey.valueOf(split.get(0));
                        BlockCoords blockCoords = BlockCoords.fromString(split.get(1));
                        ScriptBlock.getSBPlayer(player).getObjectMap().put(ScriptConnection.KEY_SCRIPT, new String[] { scriptKey.getName(), blockCoords.getFullCoords() });
                        SEConfig.SCRIPT_SELECT.replace(scriptKey.getName()).send(player);
                        break;
                    }
                    case 1: {
                        if (!UUID_PATTERN.matcher(split.get(1)).matches()) {
                            return;
                        }
                        EntityScriptJson entityScriptJson = EntityScriptJson.get(UUID.fromString(split.get(1)));
                        if (entityScriptJson.exists()) {
                            List<String> types = StringUtils.split(split.get(0), '=');
                            SettingType settingType = SettingType.get(types.get(0));
                            ButtonType buttonType = ButtonType.get(types.get(1));
                            if (settingType == null || buttonType == null) {
                                return;
                            }
                            switch (buttonType) {
                                case ENABLED: case DISABLED:
                                    settingType.set(entityScriptJson.load(), buttonType.isEnabled());
                                    SEConfig.SETTING_VALUE.replace(settingType.getType(), buttonType.getType()).send(player);
                                    break;
                                case VIEW:
                                    String type = ButtonType.get(settingType.is(entityScriptJson.load())).getType();
                                    SEConfig.SETTING_VIEW.replace(settingType.getType(), type).send(player);
                                    break;
                            }
                            entityScriptJson.saveJson();
                        }
                        break;
                    }
                    default:
                }
            } catch (Exception ignore) { }
        }   
    }

    private static int count(@NotNull String str, final char ch) {
        int count = 0;
        for (int i = 0, l = str.length(); i < l; i++) {
            if (str.charAt(i) == ch) {
                count++;
            }
        }
        return count;
    }
}