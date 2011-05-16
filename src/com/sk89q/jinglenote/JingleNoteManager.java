// $Id$
/*
 * Tetsuuuu plugin for SK's Minecraft Server
 * Copyright (C) 2010 sk89q <http://www.sk89q.com>
 * All rights reserved.
*/

package com.sk89q.jinglenote;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * A manager of play instances.
 * 
 * @author sk89q
 */
public class JingleNoteManager {
    /**
     * List of instances.
     */
    protected Map<String, JingleNotePlayer> instances
            = new HashMap<String, JingleNotePlayer>();
    
    public void play(Player player, JingleSequencer sequencer, int delay) {
        String name = player.getName();
        Location loc = findLocation(player);
        
        // Existing player found!
        if (instances.containsKey(name)) {
            JingleNotePlayer existing = instances.get(name);
            Location existingLoc = existing.getLocation();
            
            existing.stop(
                    existingLoc.getBlockX() == loc.getBlockX()
                    && existingLoc.getBlockY() == loc.getBlockY()
                    && existingLoc.getBlockZ() == loc.getBlockZ());
            
            instances.remove(name);
        }
        
        JingleNotePlayer notePlayer = new JingleNotePlayer(
                player, loc, sequencer, delay);
        Thread thread = new Thread(notePlayer);
        thread.setName("JingleNotePlayer for " + player.getName());
        thread.start();
        
        instances.put(name, notePlayer);
    }
    
    public void stop(Player player) {
        String name = player.getName();
        
        // Existing player found!
        if (instances.containsKey(name)) {
            JingleNotePlayer existing = instances.get(name);
            existing.stop(false);
            instances.remove(name);
        }
    }
    
    public void stopAll() {
        for (JingleNotePlayer notePlayer : instances.values()) {
            notePlayer.stop(false);
        }
        
        instances.clear();
    }
    
    private Location findLocation(Player player) {
        World world = player.getWorld();
        Location loc = player.getLocation();
        loc.setY(loc.getY() - 2);
        
        if (!this.canPassThrough(world.getBlockTypeIdAt(loc))) {
            return loc;
        }
        
        loc.setY(loc.getY() + 4);
        
        return loc;
    }
    public boolean canPassThrough(int id) {
        return id == 0 // Air
                || id == 8 // Water
                || id == 9 // Water
                || id == 6 // Saplings
                || id == 27 // Powered rails
                || id == 28 // Detector rails
                || id == 30 // Web <- someone will hate me for this
                || id == 37 // Yellow flower
                || id == 38 // Red flower
                || id == 39 // Brown mushroom
                || id == 40 // Red mush room
                || id == 50 // Torch
                || id == 51 // Fire
                || id == 55 // Redstone wire
                || id == 59 // Crops
                || id == 63 // Sign post
                || id == 65 // Ladder
                || id == 66 // Minecart tracks
                || id == 68 // Wall sign
                || id == 69 // Lever
                || id == 70 // Stone pressure plate
                || id == 72 // Wooden pressure plate
                || id == 75 // Redstone torch (off)
                || id == 76 // Redstone torch (on)
                || id == 77 // Stone button
                || id == 78 // Snow
                || id == 83 // Reed
                || id == 90 // Portal
                || id == 93 // Diode (off)
                || id == 94; // Diode (on)
    }
}
