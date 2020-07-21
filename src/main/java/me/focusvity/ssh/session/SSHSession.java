package me.focusvity.ssh.session;

import com.ryanmichela.bukkitssh.BukkitSSH;
import com.ryanmichela.bukkitssh.console.ConsoleShellFactory;
import me.focusvity.ssh.SSHCommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SSHSession
{

    public static final List<SSHSession> sessions = new ArrayList<>();
    private final SessionCommandSender commandSender;
    //
    private String username;

    public SSHSession()
    {
        this.commandSender = new SessionCommandSender(this);
    }

    public SessionCommandSender getSender()
    {
        return commandSender;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void writeRawLine(String message)
    {
        if (ConsoleShellFactory.ConsoleShell.consoleReader == null)
        {
            return;
        }

        try
        {
            ConsoleShellFactory.ConsoleShell.consoleReader.println(ChatColor.stripColor(message));
        }
        catch (IOException ex)
        {
            BukkitSSH.instance.getLogger().log(Level.SEVERE, "Error processing a message", ex);
        }
    }

    public void executeCommand(final String command)
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final Server server = Bukkit.getServer();

                final SSHCommandEvent event = new SSHCommandEvent(commandSender, command);
                server.getPluginManager().callEvent(event);

                if (event.isCancelled())
                {
                    return;
                }

                server.dispatchCommand(event.getSender(), (command != event.getCommand() ? event.getCommand() : command));
            }
        }.runTask(BukkitSSH.instance);
    }
}
