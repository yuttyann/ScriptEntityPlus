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
package com.github.yuttyann.scriptentityplus.file;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.event.FileReloadEvent;
import com.github.yuttyann.scriptblockplus.file.SBFile;
import com.github.yuttyann.scriptblockplus.file.config.ConfigKeys;
import com.github.yuttyann.scriptblockplus.script.option.BaseOption;
import com.github.yuttyann.scriptblockplus.utils.FileUtils;
import com.github.yuttyann.scriptentityplus.ScriptEntity;
import com.github.yuttyann.scriptentityplus.script.EntityScriptRead;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.io.File;

import static com.github.yuttyann.scriptblockplus.file.SBFiles.*;

public class SEFiles implements Listener {

    static {
        Bukkit.getPluginManager().registerEvents(new SEFiles(), ScriptEntity.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFileReload(FileReloadEvent event) {
        reload();
        reloadFilters();
    }

    public static void reload() {
        Plugin plugin = ScriptEntity.getInstance();
        ConfigKeys.load(loadLang(plugin, PATH_CONFIG, "config"));
        ConfigKeys.load(loadLang(plugin, PATH_MESSAGE, "message"));

        // ファイル"json/scriptentity"を"json/entityscript"にリネーム
        File dataFolder = ScriptBlock.getInstance().getDataFolder();
        File scriptEntity = new SBFile(dataFolder, "json/scriptentity");
        if (scriptEntity.exists() && scriptEntity.isDirectory()) {
            FileUtils.move(scriptEntity, new SBFile(dataFolder, "json/entityscript"));
        }
    }

    @SuppressWarnings("unchecked")
    public static void reloadFilters() {
        try {
            EntityScriptRead.getFilters().clear();
            for (String path : SEConfig.FILTER_OPTIONS.getValue()) {
                EntityScriptRead.getFilters().add((Class<? extends BaseOption>) Class.forName(path));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}