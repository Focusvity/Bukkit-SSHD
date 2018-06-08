package com.ryanmichela.bukkitssh;

import jline.TerminalSupport;

/**
 * Copyright 2013 Ryan Michela
 */
public class SshTerminal extends TerminalSupport
{

    public SshTerminal()
    {
        super(true);
    }

    @Override
    public void init() throws Exception
    {
        setAnsiSupported(true);
        setEchoEnabled(true);
    }
}
