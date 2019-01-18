package com.ryanmichela.bukkitssh.console;

import com.ryanmichela.bukkitssh.BukkitSSH;
import me.totalfreedom.bukkitssh.session.SSHSession;
import org.apache.commons.lang.StringUtils;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
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
        private Environment env;

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
                if(command.isEmpty() || command.equals("") || StringUtils.isEmpty(command) || command.contains("unset LANG LANGUAGE LC_CTYPE LC_COLLATE LC_MONETARY"))
                {
                    return;
                }
                this.env = environment;

                BukkitSSH.instance.getLogger()
                        .info("[" + environment.getEnv().get(Environment.ENV_USER) + "@SSH] Command executed: " + command);
                Bukkit.dispatchCommand(new ConsoleCommandSender()
                {
                    @Override
                    public void sendMessage(String s)
                    {
                        try
                        {
                            ConsoleShellFactory.ConsoleShell.consoleReader.println(new ConsoleLogFormatter().colorize(s));
                        }
                        catch (IOException e)
                        {
                            //ignored
                        }
                    }

                    @Override
                    public void sendMessage(String[] strings)
                    {
                        for(String st : strings)
                        {
                            sendMessage(st);
                        }
                    }

                    @Override
                    public Server getServer()
                    {
                        return  Bukkit.getServer();
                    }

                    @Override
                    public String getName()
                    {
                        return environment.getEnv().get(Environment.ENV_USER);
                    }

                    @Override
                    public boolean isConversing()
                    {
                        return false;
                    }

                    @Override
                    public void acceptConversationInput(String s)
                    {

                    }

                    @Override
                    public boolean beginConversation(Conversation conversation)
                    {
                        return false;
                    }

                    @Override
                    public void abandonConversation(Conversation conversation)
                    {

                    }

                    @Override
                    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent)
                    {

                    }

                    @Override
                    public void sendRawMessage(String s)
                    {
                        sendMessage(s);
                    }

                    @Override
                    public boolean isPermissionSet(String s)
                    {
                        return true;
                    }

                    @Override
                    public boolean isPermissionSet(Permission permission)
                    {
                        return true;
                    }

                    @Override
                    public boolean hasPermission(String s)
                    {
                        return true;
                    }

                    @Override
                    public boolean hasPermission(Permission permission)
                    {
                        return true;
                    }

                    @Override
                    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b)
                    {
                        return null;
                    }

                    @Override
                    public PermissionAttachment addAttachment(Plugin plugin)
                    {
                        return null;
                    }

                    @Override
                    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i)
                    {
                        return null;
                    }

                    @Override
                    public PermissionAttachment addAttachment(Plugin plugin, int i)
                    {
                        return null;
                    }

                    @Override
                    public void removeAttachment(PermissionAttachment permissionAttachment)
                    {

                    }

                    @Override
                    public void recalculatePermissions()
                    {

                    }

                    @Override
                    public Set<PermissionAttachmentInfo> getEffectivePermissions()
                    {
                        return null;
                    }

                    @Override
                    public boolean isOp()
                    {
                        return true;
                    }

                    @Override
                    public void setOp(boolean b)
                    {

                    }
                }, command);
            }
            catch (Exception e)
            {
                BukkitSSH.instance.getLogger().severe("Error processing command from SSH -" + e.getMessage());
            }
            finally
            {
                callback.onExit(0);
                this.destroy();
            }
        }

        @Override
        public void destroy()
        {

        }
    }
}
