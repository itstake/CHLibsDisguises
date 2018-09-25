package tk.itstake.chlibsdisguises;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.MCEntity;
import com.laytonsmith.abstraction.bukkit.entities.BukkitMCEntity;
import com.laytonsmith.abstraction.bukkit.entities.BukkitMCPlayer;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.*;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.*;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by bexco on 2016-03-03.
 */
public class Functions {

    public static String docs() {
        return "This extension contains LibsDisguises function";
    }
    static DisguisesArrayConverter dac = new DisguisesArrayConverter();

    @api
    public static class disguise_entity extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[] {
                    CREInvalidPluginException.class,
                    CREBadEntityException.class,
                    CREBadEntityTypeException.class,
                    CREFormatException.class,
                    CREPlayerOfflineException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return false;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            if(LifeCycles.isReady()) {
                if (args.length == 2) {
                    Entity entity = ((BukkitMCEntity) Static.getEntity(args[0], t)).getHandle();
                    if (args[1] instanceof CArray && dac.isValid((CArray) args[1], t)) {
                        Disguise disguise = dac.convert((CArray) args[1], t);
                        DisguiseAPI.disguiseToAll(entity, disguise);
                    } else {
                        throw new CREFormatException("Array is invalid", t);
                    }
                } else if(args.length == 3) {
                    Entity entity = ((BukkitMCEntity) Static.getEntity(args[0], t)).getHandle();
                    if (args[1] instanceof CArray && dac.isValid((CArray) args[1], t)) {
                        Disguise disguise = dac.convert((CArray) args[1], t);
                        if(args[2].toString().toUpperCase().equals("ALL")) {
                            DisguiseAPI.disguiseToAll(entity, disguise);
                        } else if(args[2].toString().toUpperCase().equals("IGNORE") || args[2].toString().toUpperCase().equals("TO")) {
                            throw new CREFormatException("IGNORE and TO option is must have playerArray", t);
                        }
                    } else {
                        throw new CREFormatException("Array is invalid", t);
                    }
                } else if(args.length == 4) {
                    Entity entity = ((BukkitMCEntity) Static.getEntity(args[0], t)).getHandle();
                    if (args[1] instanceof CArray && dac.isValid((CArray) args[1], t)) {
                        Disguise disguise = dac.convert((CArray) args[1], t);
                        if(args[2].toString().toUpperCase().equals("ALL")) {
                            DisguiseAPI.disguiseToAll(entity, disguise);
                        } else if(args[2].toString().toUpperCase().equals("IGNORE") || args[2].toString().toUpperCase().equals("TO")) {
                            if(args[3] instanceof CArray) {
                                Player[] playerlist = new Player[(int) ((CArray)args[3]).size()];
                                int i = 0;
                                for(Construct p:((CArray)args[3]).asList()) {
                                    if(Bukkit.getPlayerExact(p.toString()) != null) {
                                        playerlist[i] = Bukkit.getPlayerExact(p.toString());
                                        i++;
                                    } else {
                                        throw new CREPlayerOfflineException("Player is Offline or dosen't exist", t);
                                    }
                                }
                                if(args[2].toString().toUpperCase().equals("IGNORE"))
                                    DisguiseAPI.disguiseIgnorePlayers(entity, disguise, playerlist);
                                if(args[2].toString().toUpperCase().equals("TO"))
                                    DisguiseAPI.disguiseToPlayers(entity, disguise, playerlist);
                            }
                        }
                    } else {
                        throw new CREFormatException("Array is invalid", t);
                    }
                }
            } else {
                throw new CREInvalidPluginException("Can't Find LibsDisguises or ProtocolLib", t);
            }
            return CVoid.VOID;
        }

        @Override
        public String getName() {
            return "disguise_entity";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[]{2, 3, 4};
        }

        @Override
        public String docs() {
            return "{entityID, DisguiseArray[, targetOption[, playerArray]]} void Disguise a Player. TargetOption has three values: ALL, IGNORE and TO." +
                    "\nIf TargetOption is IGNORE or TO, playerArray is not optional.";
        }

        @Override
        public Version since() {
            return new SimpleVersion(1,0,0);
        }
    }

    @api
    public static class is_disguised extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREInvalidPluginException.class,
                    CREBadEntityException.class,
                    CREPlayerOfflineException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return false;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            if(args.length == 2) {
                BukkitMCEntity e = (BukkitMCEntity)Static.getEntity(args[1], t);
                Entity entity = e.getHandle();
                if(Bukkit.getPlayerExact(args[0].toString()) != null) {
                    Player p = Bukkit.getPlayerExact(args[0].toString());
                    return CBoolean.get(DisguiseAPI.isDisguised(p, entity));
                } else {
                    throw new CREPlayerOfflineException("Observer Player is offline or doesn't exist", t);
                }
            } else {
                BukkitMCEntity e = (BukkitMCEntity)Static.getEntity(args[1], t);
                Entity entity = e.getHandle();
                return CBoolean.get(DisguiseAPI.isDisguised(entity));
            }
        }

        @Override
        public String getName() {
            return "is_disguised";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[]{1, 2};
        }

        @Override
        public String docs() {
            return "{[observer,] entityID} boolean If Entity is Disguised, return true." +
                    "\nIf observer is exist, then check entity is disguised on observer's sight";
        }

        @Override
        public Version since() {
            return new SimpleVersion(1,0,0);
        }
    }
    /*
    @api
    public static class get_disguise extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREInvalidPluginException.class,
                    CREBadEntityException.class,
                    CREPlayerOfflineException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return false;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            if(args.length == 2) {
                BukkitMCEntity e = (BukkitMCEntity)Static.getEntity(args[1], t);
                Entity entity = e.getHandle();
                if(Bukkit.getPlayerExact(args[0].toString()) != null) {
                    Player p = Bukkit.getPlayerExact(args[0].toString());
                    return dac.convert(DisguiseAPI.getDisguise(p, entity));
                } else {
                    throw new CREPlayerOfflineException("Observer Player is offline or doesn't exist", t);
                }
            } else {
                BukkitMCEntity e = (BukkitMCEntity)Static.getEntity(args[1], t);
                Entity entity = e.getHandle();
                return dac.convert(DisguiseAPI.getDisguise(entity));
            }
        }

        @Override
        public String getName() {
            return "get_disguise";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[]{1, 2};
        }

        @Override
        public String docs() {
            return "{[observer,] entityID} DisguiseArray If Entity is Disguised, return DisguiseArray of disguised entity." +
                    "\nIf observer is exist, then get entity's disguise of observer's sight";
        }

        @Override
        public Version since() {
            return new SimpleVersion(1, 0, 0);
        }
    }
    */
    @api
    public static class undisguise_entity extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[] {
                    CREInvalidPluginException.class,
                    CREBadEntityException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return false;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            BukkitMCEntity e = (BukkitMCEntity)Static.getEntity(args[0], t);
            Entity entity = e.getHandle();
            DisguiseAPI.undisguiseToAll(entity);
            return CVoid.VOID;
        }

        @Override
        public String getName() {
            return "undisguise_entity";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[]{1};
        }

        @Override
        public String docs() {
            return "{entityID} void Undisguise entity.";
        }

        @Override
        public Version since() {
            return new SimpleVersion(1, 0, 0);
        }
    }

    @api
    public static class set_self_visiblity extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[] {
                    CREInvalidPluginException.class,
                    CREBadEntityException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return false;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            BukkitMCEntity e = (BukkitMCEntity)Static.getEntity(args[0], t);
            Entity entity = e.getHandle();
            DisguiseAPI.setViewDisguiseToggled(entity, Boolean.valueOf(args[1].toString()));
            return CVoid.VOID;
        }

        @Override
        public String getName() {
            return "set_self_visiblity";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[]{2};
        }

        @Override
        public String docs() {
            return "{entityID, boolean} void If it's true, entity can see entity self.";
        }

        @Override
        public Version since() {
            return new SimpleVersion(1, 0, 0);
        }
    }

    @api
    public static class is_self_visible extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[] {
                    CREInvalidPluginException.class,
                    CREBadEntityException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return false;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            BukkitMCEntity e = (BukkitMCEntity)Static.getEntity(args[0], t);
            Entity entity = e.getHandle();
            return CBoolean.get(DisguiseAPI.isViewSelfToggled(entity));
        }

        @Override
        public String getName() {
            return "is_self_visible";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[]{1};
        }

        @Override
        public String docs() {
            return "{entityID} boolean If it's true, entity can see entity self.";
        }

        @Override
        public Version since() {
            return new SimpleVersion(1, 0, 0);
        }
    }
}
