package com.ryanmichela.bukkitssh.console;

import com.ryanmichela.bukkitssh.BukkitSSH;
import me.totalfreedom.bukkitssh.SSHCommandEvent;
import me.totalfreedom.bukkitssh.session.SessionCommandSender;
import org.apache.commons.lang.StringUtils;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.command.CommandFactory;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

/**
 * Copyright 2013 Ryan Michela
 */
public class ConsoleCommandFactory implements CommandFactory
{

    @Override
    public Command createCommand(String command)
    {
        return new ConsoleCommand(command);
    }

    public class ConsoleCommand implements Command
    {

        private String command;

        private InputStream in;
        private OutputStream out;
        private OutputStream err;
        private ExitCallback callback;

        public ConsoleCommand(String command)
        {
            this.command = command;
        }

        public void setInputStream(InputStream in)
        {
            this.in = in;
        }

        public void setOutputStream(OutputStream out)
        {
            this.out = out;
        }

        public void setErrorStream(OutputStream err)
        {
            this.err = err;
        }

        public void setExitCallback(ExitCallback callback)
        {
            this.callback = callback;
        }

        @Override
        public void start(Environment environment) throws IOException
        {
            try
            {

                if(!StringUtils.isEmpty(command))
                BukkitSSH.instance.getLogger()
                        .info("[" + BukkitSSH.instance.usernameMap.get(environment.getEnv().get(Environment.ENV_IP)) + "@SSH] Command executed: " + command);
                SessionCommandSender sender = new SessionCommandSender(BukkitSSH.instance.usernameMap.get(environment.getEnv().get(Environment.ENV_IP)));
                SSHCommandEvent event = new SSHCommandEvent(sender, command);
                if(!event.isCancelled())
                Bukkit.dispatchCommand(sender, command);
                try
                {
                    ConsoleShellFactory.ConsoleShell.consoleReader.println(" ");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            catch (Exception e)
            {
                BukkitSSH.instance.getLogger().severe("Error processing command from SSH -" + e.getMessage());
            }
            finally
            {
                callback.onExit(0);
            }
        }

        @Override
        public void destroy()
        {

        }
    }
}