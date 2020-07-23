package me.focusvity.ssh;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SSHPreLoginEvent extends Event implements Cancellable
{

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled = false;
    private String name;

    public SSHPreLoginEvent(String name)
    {
        super(!Bukkit.getServer().isPrimaryThread());
        this.name = name;
    }

    @Override
    public boolean isCancelled()
    {
        return cancelled;
    }


    @Override
    public void setCancelled(boolean cancel)
    {
        cancelled = cancel;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
