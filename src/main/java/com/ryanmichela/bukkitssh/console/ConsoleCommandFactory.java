package com.ryanmichela.bukkitssh.console;

import com.ryanmichela.bukkitssh.BukkitSSH;
import me.focusvity.ssh.session.SSHSession;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copyright 2013 Ryan Michela
 */
public class ConsoleCommandFactory implements CommandFactory
{

    @Override
    public Command createCommand(String command)
    {
        return new ConsoleCommand(command);
    }

    public class ConsoleCommand implements Command
    {

        private String command;

        private InputStream in;
        private OutputStream out;
        private OutputStream err;
        private ExitCallback callback;

        public ConsoleCommand(String command)
        {
            this.command = command;
        }

        public void setInputStream(InputStream in)
        {
            this.in = in;
        }

        public void setOutputStream(OutputStream out)
        {
            this.out = out;
        }

        public void setErrorStream(OutputStream err)
        {
            this.err = err;
        }

        public void setExitCallback(ExitCallback callback)
        {
            this.callback = callback;
        }

        @Override
        public void start(Environment environment)
        {
            try
            {
                BukkitSSH.instance.getLogger()
                        .info("[" + environment.getEnv().get(Environment.ENV_USER) + "@SSH] Command executed: " + command);

                if (SSHSession.getSessionMap().containsKey(environment.getEnv().get(Environment.ENV_USER)))
                {
                    SSHSession.getSessionMap().get(environment.getEnv().get(Environment.ENV_USER)).executeCommand(command);
                }
            }
            catch (Exception e)
            {
                BukkitSSH.instance.getLogger().severe("Error processing command from SSH -" + e.getMessage());
            }
            finally
            {
                if (SSHSession.getSessionMap().containsKey(environment.getEnv().get(Environment.ENV_USER)))
                {
                    SSHSession.getSessionMap().remove(environment.getEnv().get(Environment.ENV_USER));
                }
                callback.onExit(0);
            }
        }

        @Override
        public void destroy()
        {
        }
    }
}
