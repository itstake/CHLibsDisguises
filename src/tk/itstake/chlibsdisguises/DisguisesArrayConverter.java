package tk.itstake.chlibsdisguises;

import com.laytonsmith.abstraction.bukkit.BukkitMCItemStack;
import com.laytonsmith.commandhelper.CommandHelperPlugin;
import com.laytonsmith.core.ObjectGenerator;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.CRE.CREBadEntityTypeException;
import com.laytonsmith.core.exceptions.CRE.CREFormatException;
import me.libraryaddict.disguise.disguisetypes.*;
import me.libraryaddict.disguise.disguisetypes.watchers.LivingWatcher;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

/**
 * Created by bexco on 2016-03-03.
 */
public class DisguisesArrayConverter {


    public Disguise convert(CArray array, Target t) {
        String type = array.get("type", t).toString();
        CArray optionArray = (CArray) array.get("option", t);
        Disguise disguise;
        if(type.toUpperCase().equals("PLAYER")) {
            if(optionArray.containsKey("skin")) {
                disguise = new PlayerDisguise(optionArray.get("name", t).toString(), optionArray.get("skin", t).toString());
            } else {
                disguise = new PlayerDisguise(optionArray.get("name", t).toString());
            }
        } else if(EntityType.valueOf(type.toUpperCase()).isAlive()) {
            if(optionArray.get("adult", t).toString().equals("true")) {
                disguise = new MobDisguise(DisguiseType.valueOf(type.toUpperCase()), true);
            } else {
                disguise = new MobDisguise(DisguiseType.valueOf(type.toUpperCase()), true);
            }
        } else {
            if(optionArray.containsKey("data")) {
                disguise = new MiscDisguise(DisguiseType.valueOf(type.toUpperCase()), Integer.valueOf(optionArray.get("id", t).toString()), Integer.valueOf(optionArray.get("data", t).toString()));
            } else {
                disguise = new MiscDisguise(DisguiseType.valueOf(type.toUpperCase()), Integer.valueOf(optionArray.get("id", t).toString()));
            }
        }

        if(array.containsKey("watcher")) {
            disguise = watcherConvert(disguise, (CArray) array.get("watcher", t), t);
        }
        return disguise;
    }

    private Disguise watcherConvert(Disguise disguise, CArray watcherArray, Target t) {
        FlagWatcher watcher = disguise.getWatcher();
        if(watcherArray.containsKey("burn"))
            watcher.setBurning(Boolean.valueOf(watcherArray.get("burn", t).toString()));
        if(watcherArray.containsKey("animation"))
            watcher.setAddEntityAnimations(Boolean.valueOf(watcherArray.get("animation", t).toString()));
        if(watcherArray.containsKey("invisible"))
            watcher.setInvisible(Boolean.valueOf(watcherArray.get("invisible", t).toString()));
        if(watcherArray.containsKey("sneak"))
            watcher.setSneaking(Boolean.valueOf(watcherArray.get("sneak", t).toString()));
        if(watcherArray.containsKey("rightclick"))
            watcher.setRightClicking(Boolean.valueOf(watcherArray.get("rightclick", t).toString()));
        if(watcherArray.containsKey("sprint"))
            watcher.setSprinting(Boolean.valueOf(watcherArray.get("sprint", t).toString()));
        if(watcherArray.containsKey("customname"))
            watcher.setCustomName(watcherArray.get("customname", t).toString());
        if(watcherArray.containsKey("customnamevisible"))
            watcher.setSprinting(Boolean.valueOf(watcherArray.get("customnamevisible", t).toString()));
        if(watcherArray.containsKey("armor")) {
            if(watcherArray.get("armor", t) instanceof CArray) {
                CArray armor = (CArray) watcherArray.get("armor", t);
                for(String key:armor.stringKeySet()) {
                    if(key.toLowerCase().equals("boots")) {
                        BukkitMCItemStack is = (BukkitMCItemStack) ObjectGenerator.GetGenerator().item(armor.get(key, t), t);
                        watcher.setItemStack(FlagWatcher.SlotType.BOOTS, is.asItemStack());
                    } else if(key.toLowerCase().equals("chestplate")) {
                        BukkitMCItemStack is = (BukkitMCItemStack) ObjectGenerator.GetGenerator().item(armor.get(key, t), t);
                        watcher.setItemStack(FlagWatcher.SlotType.CHESTPLATE, is.asItemStack());
                    } else if(key.toLowerCase().equals("leggings")) {
                        BukkitMCItemStack is = (BukkitMCItemStack) ObjectGenerator.GetGenerator().item(armor.get(key, t), t);
                        watcher.setItemStack(FlagWatcher.SlotType.LEGGINGS, is.asItemStack());
                    } else if(key.toLowerCase().equals("helmet")) {
                        BukkitMCItemStack is = (BukkitMCItemStack) ObjectGenerator.GetGenerator().item(armor.get(key, t), t);
                        watcher.setItemStack(FlagWatcher.SlotType.HELMET, is.asItemStack());
                    } else if(key.toLowerCase().equals("held")) {
                        BukkitMCItemStack is = (BukkitMCItemStack) ObjectGenerator.GetGenerator().item(armor.get(key, t), t);
                        watcher.setItemStack(FlagWatcher.SlotType.HELD_ITEM, is.asItemStack());
                    }
                }
            }
        }
        if(watcher instanceof LivingWatcher) {
            LivingWatcher lwatcher = (LivingWatcher)watcher;
            if(watcherArray.containsKey("health"))
                lwatcher.setHealth(Float.parseFloat(watcherArray.get("health", t).toString()));
            if(watcherArray.containsKey("maxhealth"))
                lwatcher.setMaxHealth(Double.parseDouble(watcherArray.get("maxhealth", t).toString()));
            if(watcherArray.containsKey("potionparticle"))
                lwatcher.removePotionParticles(Boolean.valueOf(watcherArray.get("potionparticle", t).toString()));
        }
        //TODO: Add Watcher
        return disguise;
    }


    public boolean isValid(CArray array, Target t) {
        if(array.containsKey("type") && array.containsKey("option") && array.get("option", t) instanceof CArray) {
            String type = array.get("type", t).toString();
            CArray optionArray = (CArray) array.get("option", t);
            try {
                EntityType.valueOf(type.toUpperCase());
            } catch(Exception e) {
                throw new CREBadEntityTypeException("That type is doesn't exist.", t);
            }
            if(type.toUpperCase().equals("PLAYER") && optionArray.containsKey("name")) {
                return true;
            } else if(EntityType.valueOf(type.toUpperCase()).isAlive() && optionArray.containsKey("adult")) {
                return true;
            } else if(optionArray.containsKey("id")) {
                return true;
            }
        }
        throw new CREFormatException("Array is Invalid", t);
    }

    public Construct convert(Disguise disguise) {
        //TODO: Convert Disguise to CArray
        return null;
    }
}
