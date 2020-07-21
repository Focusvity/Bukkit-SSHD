package com.ryanmichela.bukkitssh.authenticator;

import com.ryanmichela.bukkitssh.BukkitSSH;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

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
        if (BukkitSSH.instance.getConfig().getString("credentials." + username).equals(password))
        {
            failCounts.remove(username);
            return true;
        }
        BukkitSSH.instance.getLogger().info("Failed login for " + username + " using password authentication.");

        try
        {
            Thread.sleep(3000);
            int fail = failCounts.getOrDefault(username, 0) + 1;
            if (fail >= 3)
            {
                failCounts.remove(username);
                serverSession.close(true);
            }
            failCounts.replace(username, fail);
        }
        catch (InterruptedException e)
        {
            // do nothing
        }
        return false;
    }
}
