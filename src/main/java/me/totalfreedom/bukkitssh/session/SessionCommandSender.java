package me.totalfreedom.bukkitssh.session;

import com.ryanmichela.bukkitssh.BukkitSSH;
import com.ryanmichela.bukkitssh.console.ConsoleLogFormatter;
import com.ryanmichela.bukkitssh.console.ConsoleShellFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class SessionCommandSender implements ConsoleCommandSender
{
    private final String name;

    public SessionCommandSender(String name)
    {
        this.name = name;
    }

    @Override
    public void sendMessage(String s)
    {
        this.sendRawMessage(s);
    }

    @Override
    public void sendMessage(String[] strings)
    {
        Arrays.asList(strings).forEach(this::sendMessage);
    }

    @Override
    public Server getServer()
    {
        return Bukkit.getServer();
    }

    @Override
    public String getName()
    {
        return name;
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
        try
        {
            if(ConsoleShellFactory.ConsoleShell.consoleReader == null) return;
            ConsoleShellFactory.ConsoleShell.consoleReader.println(ChatColor.stripColor(s));
        }
        catch(Exception ignored)
        {
        }
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
}
