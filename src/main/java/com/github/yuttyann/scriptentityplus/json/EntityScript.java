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
package com.github.yuttyann.scriptentityplus.json;

import com.github.yuttyann.scriptblockplus.file.json.annotation.Alternate;
import com.github.yuttyann.scriptblockplus.file.json.basic.SingleJson.SingleElement;
import com.github.yuttyann.scriptentityplus.item.ToolMode;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EntityScript extends SingleElement {

    @Alternate("scripts")
    @SerializedName(value = "normalscripts", alternate = { "scripts" })
    private final Set<String> normalScripts = new LinkedHashSet<>();

    @Alternate("deathscript")
    @SerializedName(value = "deathscripts", alternate = { "deathscript" })
    private final Set<String> deathScripts = new LinkedHashSet<>();

    @SerializedName("invincible")
    private boolean invincible;

    @SerializedName("projectile")
    private boolean projectile;

    @NotNull
    public Set<String> getScripts(@NotNull ToolMode toolMode) {
        return toolMode == ToolMode.NORMAL_SCRIPT ? normalScripts : deathScripts;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setProjectile(boolean projectile) {
        this.projectile = projectile;
    }

    public boolean isProjectile() {
        return projectile;
    }
}