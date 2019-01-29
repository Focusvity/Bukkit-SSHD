package me.focusvity.bukkitssh;

import com.ryanmichela.bukkitssh.console.ConsoleCommandFactory;
import com.ryanmichela.bukkitssh.console.ConsoleShellFactory;
import me.focusvity.bukkitssh.authenticator.PublicKeyAuthenticator;
import org.apache.sshd.common.io.nio2.Nio2ServiceFactoryFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class BukkitSSH extends JavaPlugin
{

    public static BukkitSSH instance;
    private SshServer sshd;
    public File keys;

    @Override
    public void onLoad()
    {
        this.instance = this;
        saveDefaultConfig();

        keys = new File(getDataFolder(), "keys");
        if (!keys.exists())
        {
            keys.mkdirs();
        }
    }

    @Override
    public void onEnable()
    {
        this.instance = this;

        sshd = SshServer.setUpDefaultServer();
        sshd.setIoServiceFactoryFactory(new Nio2ServiceFactoryFactory());
        sshd.setPort(getConfig().getInt("port", 22));
        String host = getConfig().getString("host", "0.0.0.0");
        sshd.setHost(host.equals("0.0.0.0") ? null : host);

        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File(getDataFolder(), "hostkey").toPath()));
        sshd.setShellFactory(new ConsoleShellFactory());
        sshd.setPublickeyAuthenticator(new PublicKeyAuthenticator());
        sshd.setCommandFactory(new ConsoleCommandFactory());

        try
        {
            sshd.start();
        }
        catch (IOException ex)
        {
            getLogger().log(Level.SEVERE, "Could not start the SSH server!", ex);
        }
    }

    @Override
    public void onDisable()
    {
        this.instance = null;

        try
        {
            sshd.stop();
        }
        catch (IOException ex)
        {
            getLogger().log(Level.SEVERE, "Something went wrong when stopping the SSH server!", ex);
        }
    }
}