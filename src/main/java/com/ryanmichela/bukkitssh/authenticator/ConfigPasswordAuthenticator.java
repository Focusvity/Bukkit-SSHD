package com.ryanmichela.bukkitssh.authenticator;

import com.ryanmichela.bukkitssh.BukkitSSH;
import me.totalfreedom.bukkitssh.SSHPreLoginEvent;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;
import org.bukkit.Bukkit;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Copyright 2013 Ryan Michela
 */
public class ConfigPasswordAuthenticator implements PasswordAuthenticator
{


    @Override
    public boolean authenticate(String username, String password, ServerSession serverSession)
    {
        InetSocketAddress socketAddress = (InetSocketAddress) serverSession.getClientAddress();
        InetAddress inetAddress = socketAddress.getAddress();
        final SSHPreLoginEvent event = new SSHPreLoginEvent(inetAddress.getHostAddress(), username, false);
        Bukkit.getServer().getPluginManager().callEvent(event);
         boolean auth;
        if (event.isCancelled() || !event.canBypassPassword())
        {
            auth = passAuth(username, password);
        }
        else
        {
            auth = event.canBypassPassword();
            username = event.getName();
        }
        if(auth)
        {
            BukkitSSH.instance.usernameMap.put(inetAddress.getHostAddress(), username);
        }
        return auth;
    }

    private Boolean passAuth(String username, String password)
    {
        if ("password".equals(password))
        {
            return true;
        }
        BukkitSSH.instance.getLogger().info("Failed login for " + username + " using password authentication.");
        return false;
    }

}
