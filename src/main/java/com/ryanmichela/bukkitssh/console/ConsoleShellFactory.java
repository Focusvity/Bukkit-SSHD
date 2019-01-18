package com.ryanmichela.bukkitssh.console;

import com.ryanmichela.bukkitssh.BukkitSSH;
import com.ryanmichela.bukkitssh.SshTerminal;
import com.ryanmichela.bukkitssh.util.FlushyOutputStream;
import com.ryanmichela.bukkitssh.util.FlushyStreamHandler;
import com.ryanmichela.bukkitssh.util.StreamHandlerAppender;
import jline.console.ConsoleReader;
import me.totalfreedom.bukkitssh.session.SSHSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
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

        public Environment getEnvironment()
        {
            return environment;
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
                consoleReader = new ConsoleReader(in, new FlushyOutputStream(out), new SshTerminal());
                consoleReader.setExpandEvents(true);
                consoleReader.addCompleter(new ConsoleCommandCompleter());

                StreamHandler streamHandler = new FlushyStreamHandler(out, new ConsoleLogFormatter(), consoleReader);
                streamHandlerAppender = new StreamHandlerAppender(streamHandler);

                ((Logger) LogManager.getRootLogger()).addAppender(streamHandlerAppender);

                environment = env;
                thread = new Thread(this, "BukkitSSH " + env.getEnv().get(Environment.ENV_USER));
                thread.start();
                SSHSession session = new SSHSession();
                session.setUsername(env.getEnv().get(Environment.ENV_USER));
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
                      Bukkit.dispatchCommand(new ConsoleCommandSender()
                      {
                          @Override
                          public void sendMessage(String s)
                          {
                              try
                              {
                                  ConsoleShell.consoleReader.println(new ConsoleLogFormatter().colorize(s));
                              }
                              catch (IOException e)
                              {
                                  //ignored
                              }
                          }

                          @Override
                          public void sendMessage(String[] strings)
                          {
                              for(String st : strings)
                              {
                                  sendMessage(st);
                              }
                          }

                          @Override
                          public Server getServer()
                          {
                              return  Bukkit.getServer();
                          }

                          @Override
                          public String getName()
                          {
                              return environment.getEnv().get(Environment.ENV_USER);
                          }

                          @Override
                          public boolean isConversing()
                          {
                              return false;
                          }

                          @Override
                          public void acceptConversationInput(String s)
                          {

                          }

                          @Override
                          public boolean beginConversation(Conversation conversation)
                          {
                              return false;
                          }

                          @Override
                          public void abandonConversation(Conversation conversation)
                          {

                          }

                          @Override
                          public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent)
                          {

                          }

                          @Override
                          public void sendRawMessage(String s)
                          {
                                sendMessage(s);
                          }

                          @Override
                          public boolean isPermissionSet(String s)
                          {
                              return true;
                          }

                          @Override
                          public boolean isPermissionSet(Permission permission)
                          {
                              return true;
                          }

                          @Override
                          public boolean hasPermission(String s)
                          {
                              return true;
                          }

                          @Override
                          public boolean hasPermission(Permission permission)
                          {
                              return true;
                          }

                          @Override
                          public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b)
                          {
                              return null;
                          }

                          @Override
                          public PermissionAttachment addAttachment(Plugin plugin)
                          {
                              return null;
                          }

                          @Override
                          public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i)
                          {
                              return null;
                          }

                          @Override
                          public PermissionAttachment addAttachment(Plugin plugin, int i)
                          {
                              return null;
                          }

                          @Override
                          public void removeAttachment(PermissionAttachment permissionAttachment)
                          {

                          }

                          @Override
                          public void recalculatePermissions()
                          {

                          }

                          @Override
                          public Set<PermissionAttachmentInfo> getEffectivePermissions()
                          {
                              return null;
                          }

                          @Override
                          public boolean isOp()
                          {
                              return false;
                          }

                          @Override
                          public void setOp(boolean b)
                          {

                          }
                      }, command);

                    });

                     BukkitSSH.instance.getLogger().info("[" + environment.getEnv().get(Environment.ENV_USER) + "@SSH] Command executed: " + command);
                }
            }
            catch (IOException e)
            {
                BukkitSSH.instance.getLogger().log(Level.SEVERE, "Error processing command from SSH", e);
            }
            finally
            {
                callback.onExit(0);
                this.destroy();
                this.thread.stop();
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