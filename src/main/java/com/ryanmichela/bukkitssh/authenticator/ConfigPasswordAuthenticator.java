package com.ryanmichela.bukkitssh.authenticator;

import com.ryanmichela.bukkitssh.BukkitSSH;
import com.sun.org.apache.xpath.internal.operations.Bool;
import me.totalfreedom.bukkitssh.SSHPreLoginEvent;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.bukkit.Bukkit;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2013 Ryan Michela
 */
public class ConfigPasswordAuthenticator implements PasswordAuthenticator
{

    private Map<String, Integer> failCounts = new HashMap<>();

    @Override
    public boolean authenticate(String username, String password, ServerSession serverSession)
    {
        InetSocketAddress socketAddress = (InetSocketAddress) serverSession.getClientAddress();
        InetAddress inetAddress = socketAddress.getAddress();
        final SSHPreLoginEvent event = new SSHPreLoginEvent(inetAddress.getHostAddress(), username, false);
        Bukkit.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled())
        {
            return passAuth(username, password, serverSession);
        }
        if(!event.canBypassPassword())
        {
            return passAuth(username, password, serverSession);
        }
        if(event.getName() != null)
        {
            serverSession.setUsername(username);
        }
        return true;
    }

    private Boolean passAuth(String username, String password, ServerSession serverSession)
    {
        if (BukkitSSH.instance.getConfig().getString("credentials." + username).equals(password))
        {
            failCounts.put(username, 0);
            return true;
        }
        BukkitSSH.instance.getLogger().info("Failed login for " + username + " using password authentication.");

        try
        {
            Thread.sleep(3000);
            if (failCounts.containsKey(username))
            {
                failCounts.put(username, failCounts.get(username) + 1);
            }
            else
            {
                failCounts.put(username, 1);
            }
            if (failCounts.get(username) >= 3)
            {
                failCounts.put(username, 0);
                serverSession.close(true);
                return false;
            }
        }
        catch (InterruptedException e)
        {
            return false;
        }
        return false;
    }

}
