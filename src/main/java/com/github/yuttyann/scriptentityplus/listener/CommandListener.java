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

import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptentityplus.item.ScriptConnection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandListener implements Listener {

    private static final String TELLRAW_PREFIX = "tellraw @s \"";

    static {
        org.apache.logging.log4j.Logger rootLogger = LogManager.getRootLogger();
        if (rootLogger instanceof Logger) {
            ((Logger) rootLogger).addFilter(new CommandLogFilter());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().startsWith("/") ? event.getMessage().substring(1) : event.getMessage();
        if (command.startsWith(TELLRAW_PREFIX)) {
            TellrawCatcher.onCatch(command.substring(TELLRAW_PREFIX.length(), command.length() - 1), event);
        }
    }

    private static class CommandLogFilter extends AbstractFilter {
    
        private static final boolean USE_RAW_STRING = false;

        private CommandLogFilter() {
            super(Filter.Result.DENY, Filter.Result.NEUTRAL);
        }
    
        @Override
        public Result filter(LogEvent event) {
            Message message = event == null ? null : event.getMessage();
            return doFilter(message == null ? null : (USE_RAW_STRING ? message.getFormat() : message.getFormattedMessage()));
        }
    
        @Override
        public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
            return doFilter(msg == null ? null : msg.toString());
        }
    
        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
            return doFilter(msg);
        }
    
        @Override
        public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
            return doFilter(msg == null ? null : (USE_RAW_STRING ? msg.getFormat() : msg.getFormattedMessage()));
        }
    
        @NotNull
        private Result doFilter(@Nullable String message) {
            return StringUtils.isNotEmpty(message) && message.contains(TELLRAW_PREFIX) && message.contains(ScriptConnection.KEY_TELLRAW) ? onMatch : onMismatch;
        }
    }
}