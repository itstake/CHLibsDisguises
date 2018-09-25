package tk.itstake.chlibsdisguises;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.commandhelper.CommandHelperPlugin;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Created by bexco on 2016-03-03.
 */
@MSExtension("CHLibsDisguises")
public class LifeCycles extends AbstractExtension {
    @Override
    public Version getVersion() {
        return new SimpleVersion(1, 0, 0);
    }

    @Override
    public void onStartup() {
        Bukkit.getConsoleSender().sendMessage("[CHLibDisguises] CHLibDisguises 1.0.0 enabled.");
    }

    @Override
    public void onShutdown() {
        Bukkit.getConsoleSender().sendMessage("[CHLibDisguises] CHLibDisguises 1.0.0 disabled.");
    }

    public static boolean isReady() {
        if(CommandHelperPlugin.self.getServer().getPluginManager().isPluginEnabled("LibsDisguises") && CommandHelperPlugin.self.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            return true;
        } else {
            Bukkit.getConsoleSender().sendMessage("[CHLibDisguises] LibsDisguises or ProtocolLib is missing! You can't use this extension.");
            return false;
        }
    }
}
