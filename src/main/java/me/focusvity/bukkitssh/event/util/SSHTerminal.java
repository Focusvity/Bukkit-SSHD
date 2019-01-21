package me.focusvity.bukkitssh.event.util;

import jline.TerminalSupport;

public class SSHTerminal extends TerminalSupport
{

    public SSHTerminal()
    {
        super(true);
    }

    @Override
    public void init()
    {
        setAnsiSupported(true);
        setEchoEnabled(true);
    }
}
