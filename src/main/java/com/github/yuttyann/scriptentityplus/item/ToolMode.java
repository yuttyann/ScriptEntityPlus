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

import com.github.yuttyann.scriptblockplus.enums.MatchType;
import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public enum ToolMode {
    NORMAL_SCRIPT("NORMAL MODE"),
    DEATH_SCRIPT("DEATH MODE");

    private final String mode;

    ToolMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public static boolean isItem(@NotNull ItemStack item) {
        return ItemUtils.compare(MatchType.TYPE, item, Material.BONE) && ItemUtils.compare(MatchType.NAME, item, "§dScript Connection");
    }

    @NotNull
    public static ToolMode getNextMode(@NotNull ToolMode toolMode) {
        return toolMode == NORMAL_SCRIPT ? DEATH_SCRIPT : NORMAL_SCRIPT;
    }
}