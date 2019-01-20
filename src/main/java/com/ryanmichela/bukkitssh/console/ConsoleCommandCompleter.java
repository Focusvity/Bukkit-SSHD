package com.ryanmichela.bukkitssh.console;

/**
 * Copyright 2013 Ryan Michela
 */

import com.ryanmichela.bukkitssh.util.ReflectionUtil;
import com.ryanmichela.bukkitssh.util.Waitable;
import jline.console.completer.Completer;
import me.focusvity.bukkitssh.BukkitSSH;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

public class ConsoleCommandCompleter implements Completer
{

    public int complete(final String buffer, final int cursor, final List<CharSequence> candidates)
    {
        Waitable<List<String>> waitable = new Waitable<List<String>>()
        {
            @Override
            protected List<String> evaluate()
            {
                CommandMap commandMap = ReflectionUtil.getProtectedValue(Bukkit.getServer(), "commandMap");
                return commandMap.tabComplete(Bukkit.getServer().getConsoleSender(), buffer);
            }
        };
        Bukkit.getScheduler().runTask(BukkitSSH.instance, waitable);
        try
        {
            List<String> offers = waitable.get();
            if (offers == null)
            {
                return cursor;
            }
            candidates.addAll(offers);

            final int lastSpace = buffer.lastIndexOf(' ');
            if (lastSpace == -1)
            {
                return cursor - buffer.length();
            }
            else
            {
                return cursor - (buffer.length() - lastSpace - 1);
            }
        }
        catch (ExecutionException e)
        {
            BukkitSSH.instance.getLogger().log(Level.WARNING, "Unhandled exception when tab completing", e);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        return cursor;
    }
}

