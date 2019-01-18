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
        return event.canBypassPassword();
    }

    private Boolean passAuth(String username, String password, ServerSession serverSession)
    {
        if (BukkitSSH.instance.getConfig().getString("credentials." + username).equals(password))
        {
            return true;
        }
        BukkitSSH.instance.getLogger().info("Failed login for " + username + " using password authentication.");
        return false;
    }

}
