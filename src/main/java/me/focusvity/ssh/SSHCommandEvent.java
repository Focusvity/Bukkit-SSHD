package me.focusvity.ssh;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerEvent;

public class SSHCommandEvent extends ServerEvent implements Cancellable
{

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    private CommandSender sender;
    private String command;

    public SSHCommandEvent(CommandSender sender, String command)
    {
        this.cancelled = false;
        this.sender = sender;
        this.command = command;
    }

    public static HandlerList getHandlerList() // Required for Aero dependency
    {
        return handlers;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel)
    {
        this.cancelled = cancel;
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        if (command == null)
        {
            command = "";
        }

        this.command = command;
    }

    public CommandSender getSender()
    {
        return sender;
    }

    public void setSender(CommandSender sender)
    {
        this.sender = sender;
    }
}
