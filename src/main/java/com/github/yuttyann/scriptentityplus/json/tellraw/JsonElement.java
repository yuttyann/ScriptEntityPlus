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
package com.github.yuttyann.scriptentityplus.json.tellraw;

import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.simple.JSONObject;

import com.github.yuttyann.scriptblockplus.utils.Utils;

@SuppressWarnings("unchecked")
public class JsonElement {

    private final JSONObject jsonObject = new JSONObject();

    public JsonElement(@NotNull String text, @Nullable ChatColor color) {
        this(text, color, new ChatFormat[0]);
    }

    public JsonElement(@NotNull String text, @Nullable ChatColor color, @NotNull ChatFormat... format) {
        jsonObject.put("text", text);
        if (color != null) {
            jsonObject.put("color", color.name().toLowerCase());
        }
        if (format.length > 0) {
            for (ChatFormat chatFormat : format) {
                jsonObject.put(chatFormat.getFormat(), true);
            }
        }
    }

    public void setClickEvent(@NotNull ClickEventType action, @NotNull String value) {
        JSONObject clickEvent = new JSONObject();
        clickEvent.put("action", action.getType());
        clickEvent.put("value", value);
        jsonObject.put("clickEvent", clickEvent);
    }

    public void setHoverEvent(@NotNull HoverEventType action, @NotNull String value) {
        JSONObject hoverEvent = new JSONObject();
        hoverEvent.put("action", action.getType());
        hoverEvent.put(Utils.isCBXXXorLater("1.16") ? "contents" : "value", value);
        jsonObject.put("hoverEvent", hoverEvent);
    }

    @NotNull
    public JSONObject getJson() {
        return jsonObject;
    }
}