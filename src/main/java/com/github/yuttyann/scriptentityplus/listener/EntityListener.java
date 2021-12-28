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

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.event.DelayEndEvent;
import com.github.yuttyann.scriptblockplus.event.DelayRunEvent;
import com.github.yuttyann.scriptblockplus.file.json.derived.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.item.ItemAction;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptKey;
import com.github.yuttyann.scriptblockplus.script.option.other.PlayerAction;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptblockplus.utils.collection.ObjectMap;
import com.github.yuttyann.scriptentityplus.ScriptEntity;
import com.github.yuttyann.scriptentityplus.item.ToolMode;
import com.github.yuttyann.scriptentityplus.json.EntityScript;
import com.github.yuttyann.scriptentityplus.json.EntityScriptJson;
import com.github.yuttyann.scriptentityplus.script.EntityScriptRead;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class EntityListener implements Listener {

    public static final String KEY_OFF = Utils.randomUUID();
    public static final String KEY_CLICK_ENTITY = Utils.randomUUID();

    @EventHandler(priority = EventPriority.HIGH)
    public void onWorldLoad(WorldLoadEvent event) {
        ScriptEntity scriptEntity = ScriptEntity.getInstance();
        event.getWorld().getEntities().forEach(scriptEntity::removeArmorStand);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getRemover() != null) {
            damageEvent(event, event.getRemover(), event.getEntity(), 0.0D);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            return;
        }
        damageEvent(event, event.getDamager(), event.getEntity(), event.getDamage());
    }

    private void damageEvent(@NotNull Cancellable event, @NotNull Entity damager, @NotNull Entity entity, final double damage) {
        EntityScriptJson entityScriptJson = EntityScriptJson.get(entity.getUniqueId());
        if (!entityScriptJson.exists()) {
            return;
        }
        EntityScript info = entityScriptJson.load();
        if (info.isInvincible()) {
            event.setCancelled(true);
        }
        if (info.isProjectile()) {
            if (!(damager instanceof Projectile)) {
                return;
            }
            damager = (Entity) ((Projectile) damager).getShooter();
        }
        if (damager instanceof Player) {
            ToolMode toolMode = ToolMode.NORMAL_SCRIPT;
            if (!event.isCancelled()) {
                if (entity instanceof LivingEntity) {
                    if (entity instanceof ArmorStand) {
                        entity.remove();
                    }
                    if (entity.isDead() || (((LivingEntity) entity).getHealth() - damage) <= 0.0D) {
                        toolMode = ToolMode.DEATH_SCRIPT;
                    }
                } else {
                    toolMode = ToolMode.DEATH_SCRIPT;
                }
            }
            Entity original = entity;
            Set<String> scripts = info.getScripts(toolMode);
            if (scripts.isEmpty()) {
                delete(original);
                return;
            }
            try {
                if (toolMode == ToolMode.DEATH_SCRIPT) {
                    entity = ScriptEntity.getInstance().createArmorStand(entity.getLocation());
                }
                for (String script : scripts) {
                    read((Player) damager, entity, StringUtils.split(script, '|'), Action.LEFT_CLICK_AIR);
                }
            } finally {
                if (toolMode == ToolMode.DEATH_SCRIPT) {
                    entity.remove();
                }
                delete(original);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        delete(event.getEntity());
    }

    private void delete(@NotNull Entity entity) {
        if (!entity.isDead()) {
            return;
        }
        EntityType type = entity.getType();
        if (type == EntityType.PLAYER) {
            return;
        }
        EntityScriptJson scriptJson = EntityScriptJson.get(entity.getUniqueId());
        if (scriptJson.exists()) {
            scriptJson.deleteFile();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            onPlayerInteractEntity(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            event.setCancelled(true);
            return;
        }
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        ObjectMap objectMap = SBPlayer.fromPlayer(player).getObjectMap();
        try {
            objectMap.put(KEY_CLICK_ENTITY, entity);
            ItemStack main = player.getInventory().getItemInMainHand();
            ItemStack off = player.getInventory().getItemInOffHand();
            if (ToolMode.isItem(main) && ItemAction.has(player, main, true)) {
                ItemAction.callRun(player, main, entity.getLocation(), Action.RIGHT_CLICK_AIR);
                event.setCancelled(true);
            } else if (ToolMode.isItem(off) && ItemAction.has(player, off, true)) {
                try {
                    objectMap.put(KEY_OFF, true);
                    ItemAction.callRun(player, off, entity.getLocation(), Action.RIGHT_CLICK_AIR);
                } finally {
                    objectMap.put(KEY_OFF, false);
                }
                event.setCancelled(true);
            } else {
                EntityScript entityScript = EntityScriptJson.get(entity.getUniqueId()).load();
                if (entityScript.getScripts(ToolMode.NORMAL_SCRIPT).size() > 0) {
                    if (!entityScript.isProjectile()) {
                        for (String script : entityScript.getScripts(ToolMode.NORMAL_SCRIPT)) {
                            read(player, entity, StringUtils.split(script, '|'), Action.RIGHT_CLICK_AIR);
                        }
                    }
                    event.setCancelled(true);
                }
            }
        } finally {
            objectMap.remove(KEY_CLICK_ENTITY);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDelayRun(DelayRunEvent event) {
        if (!(event.getScriptRead() instanceof EntityScriptRead)) {
            return;
        }
        EntityScriptRead scriptRead = (EntityScriptRead) event.getScriptRead();
        if (scriptRead.getEntity().isDead()) {
            scriptRead.setEntity(ScriptEntity.getInstance().createArmorStand(scriptRead.getLocation()));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDelayEnd(DelayEndEvent event) {
        ScriptRead scriptRead = event.getScriptRead();
        if (scriptRead instanceof EntityScriptRead) {
            ScriptEntity.getInstance().removeArmorStand(((EntityScriptRead) scriptRead).getEntity());
        }
    }

    private void read(@NotNull Player player, @NotNull Entity entity, @NotNull List<String> list, @NotNull Action action) {
        BlockCoords blockCoords = BlockCoords.fromString(list.get(1));
        if (!BlockScriptJson.get(ScriptKey.valueOf(list.get(0))).has(blockCoords)) {
            return;
        }
        EntityScriptRead scriptRead = new EntityScriptRead(SBPlayer.fromPlayer(player), blockCoords, ScriptKey.valueOf(list.get(0)));
        scriptRead.setEntity(entity);
        scriptRead.put(PlayerAction.KEY, action);
        scriptRead.read(0);
    }
}