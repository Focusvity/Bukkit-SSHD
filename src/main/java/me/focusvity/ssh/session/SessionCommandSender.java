package me.focusvity.ssh.session;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.Set;

public class SessionCommandSender implements ConsoleCommandSender
{

    private final SSHSession session;

    public SessionCommandSender(SSHSession session)
    {
        this.session = session;
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
        return this.session.getUsername();
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
        this.session.writeRawLine(s);
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
