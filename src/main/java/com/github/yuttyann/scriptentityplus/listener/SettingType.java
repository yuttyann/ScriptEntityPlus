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

import com.github.yuttyann.scriptblockplus.enums.server.reflect.ClassType;
import com.github.yuttyann.scriptentityplus.json.EntityScript;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

public enum SettingType {
    INVINCIBLE("Invincible"),
    PROJECTILE("Projectile");

    private final String type;

    SettingType(@NotNull String type) {
        this.type = type;
    }

    @NotNull
    public String getType() {
        return type;
    }

    public void set(@NotNull EntityScript info, @NotNull Object value) {
        try {
            info.getClass().getMethod("set" + type, ClassType.getPrimitive(value.getClass())).invoke(info, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public boolean is(@NotNull EntityScript info) {
        try {
            return (boolean) info.getClass().getMethod("is" + type).invoke(info);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Nullable
    public static SettingType get(@NotNull String name) {
        String upper = name.toUpperCase();
        return Stream.of(values()).filter(s -> s.name().equals(upper)).findFirst().orElse(null);
    }
}
