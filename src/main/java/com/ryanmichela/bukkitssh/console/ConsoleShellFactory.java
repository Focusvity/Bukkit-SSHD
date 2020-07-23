package com.ryanmichela.bukkitssh.console;

import com.ryanmichela.bukkitssh.BukkitSSH;
import com.ryanmichela.bukkitssh.SshTerminal;
import com.ryanmichela.bukkitssh.util.FlushyOutputStream;
import com.ryanmichela.bukkitssh.util.FlushyStreamHandler;
import com.ryanmichela.bukkitssh.util.StreamHandlerAppender;
import jline.console.ConsoleReader;
import me.focusvity.ssh.SSHPreLoginEvent;
import me.focusvity.ssh.session.SSHSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.StreamHandler;

public class ConsoleShellFactory implements Factory<Command>
{

    public Command get()
    {
        return this.create();
    }

    public Command create()
    {
        return new ConsoleShell();
    }

    public static class ConsoleShell implements Command, Runnable
    {

        public static ConsoleReader consoleReader;
        StreamHandlerAppender streamHandlerAppender;
        private InputStream in;
        private OutputStream out;
        private OutputStream err;
        private ExitCallback callback;
        private Environment environment;
        private Thread thread;

        public InputStream getIn()
        {
            return in;
        }

        public OutputStream getOut()
        {
            return out;
        }

        public OutputStream getErr()
        {
            return err;
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

        public void start(Environment env) throws IOException
        {
            try
            {
                SSHPreLoginEvent event = new SSHPreLoginEvent(env.getEnv().get(Environment.ENV_USER));
                Bukkit.getServer().getPluginManager().callEvent(event);
                if (event.isCancelled())
                {
                    return;
                }
                consoleReader = new ConsoleReader(in, new FlushyOutputStream(out), new SshTerminal());
                consoleReader.setExpandEvents(true);
                consoleReader.addCompleter(new ConsoleCommandCompleter());

                StreamHandler streamHandler = new FlushyStreamHandler(out, new ConsoleLogFormatter(), consoleReader);
                streamHandlerAppender = new StreamHandlerAppender(streamHandler);

                ((Logger) LogManager.getRootLogger()).addAppender(streamHandlerAppender);

                environment = env;
                thread = new Thread("Connected to " + Bukkit.getServer().getName());
                thread.start();
                SSHSession session = new SSHSession();
                session.setUsername(env.getEnv().get(Environment.ENV_USER));
                SSHSession.getSessionMap().put(env.getEnv().get(Environment.ENV_USER), session);
            }
            catch (Exception e)
            {
                throw new IOException("Error starting shell", e);
            }
        }

        public void destroy()
        {
            ((Logger) LogManager.getRootLogger()).removeAppender(streamHandlerAppender);
        }

        public void run()
        {
            try
            {
                printPreamble(consoleReader);
                while (true)
                {
                    String command = consoleReader.readLine("\r>", null);
                    if (command == null)
                    {
                        continue;
                    }

                    if (command.equals("ssh.exit") || command.equals("ssh.quit"))
                    {
                        break;
                    }

                    Bukkit.getScheduler().runTask(BukkitSSH.instance, () ->
                    {
                        BukkitSSH.instance.getLogger().info("[" + environment.getEnv().get(Environment.ENV_USER) + "@SSH] Command executed: " + command);

                        if (SSHSession.getSessionMap().containsKey(environment.getEnv().get(Environment.ENV_USER)))
                        {
                            SSHSession.getSessionMap().get(environment.getEnv().get(Environment.ENV_USER)).executeCommand(command);
                        }
                    });
                }
            }
            catch (IOException e)
            {
                BukkitSSH.instance.getLogger().log(Level.SEVERE, "Error processing command from SSH", e);
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

        private void printPreamble(ConsoleReader consoleReader) throws IOException
        {
            consoleReader.println("Connected to: " + Bukkit.getServer().getName() + "\r");
            consoleReader.println(" - " + Bukkit.getServer().getMotd() + "\r");
            consoleReader.println("\r");
            consoleReader.println("Type 'ssh.exit' or 'ssh.quit' to exit the shell." + "\r");
            consoleReader.println("================================================" + "\r");
        }
    }
}