package com.ryanmichela.bukkitssh;

import com.ryanmichela.bukkitssh.authenticator.ConfigPasswordAuthenticator;
import com.ryanmichela.bukkitssh.authenticator.PublicKeyAuthenticator;
//import com.ryanmichela.bukkitssh.console.ConsoleCommandFactory;
import com.ryanmichela.bukkitssh.console.ConsoleCommandFactory;
import com.ryanmichela.bukkitssh.console.ConsoleShellFactory;
import lombok.Getter;
import lombok.Setter;
import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.common.channel.ChannelListener;
import org.apache.sshd.common.session.helpers.AbstractSession;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Copyright 2013 Ryan Michela
 */
public class BukkitSSH extends JavaPlugin
{

    public static BukkitSSH instance;
    @Getter @Setter
    private SshServer sshd;
    private File authorizedKeys;
    public  HashMap<String, String> usernameMap = new HashMap<>(); //ip, username


    @Override
    public void onLoad()
    {
        instance = this;
        saveDefaultConfig();

        authorizedKeys = new File(getDataFolder(), "authorized_keys");
        if (!authorizedKeys.exists())
        {
            authorizedKeys.mkdirs();
        }

        // Don't go any lower than INFO or SSHD will cause a stack overflow exception.
        // SSHD will log that it wrote bites to the output stream, which writes
        // bytes to the output stream - ad nauseaum.
        getLogger().setLevel(Level.INFO);
    }

    @Override
    public void onEnable()
    {
        instance = this;

        sshd = SshServer.setUpDefaultServer();
        sshd.setPort(getConfig().getInt("port", 22));
        String host = getConfig().getString("listenAddress", "0.0.0.0");
        sshd.setHost(host.equals("0.0.0.0") ? null : host);

        File hostKey = new File(getDataFolder(), "hostkey");

        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(hostKey.toPath()));
        sshd.setShellFactory(new ConsoleShellFactory());
        sshd.setPasswordAuthenticator(new ConfigPasswordAuthenticator());
        sshd.setPublickeyAuthenticator(new PublicKeyAuthenticator(authorizedKeys));
        sshd.setCommandFactory(new ConsoleCommandFactory());


        try
        {
            sshd.start();
        }
        catch (IOException e)
        {
            getLogger().log(Level.SEVERE, "Failed to start SSH server! ", e);
        }
    }

    @Override
    public void onDisable()
    {
        try
        {
            sshd.stop();
        }
        catch (Exception e)
        {
            // do nothing
        }
    }
}