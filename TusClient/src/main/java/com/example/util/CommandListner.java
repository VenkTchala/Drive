package com.example.util;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;


public class CommandListner implements ProtocolCommandListener {

    @Override
    public void protocolCommandSent(ProtocolCommandEvent protocolCommandEvent) {
        System.out.printf("[%s][%d] Command sent : [%s]-%s", Thread.currentThread().getName(),
                System.currentTimeMillis(), protocolCommandEvent.getCommand(),
                protocolCommandEvent.getMessage());
    }

    @Override
    public void protocolReplyReceived(ProtocolCommandEvent protocolCommandEvent) {
        System.out.printf("[%s][%d] Reply received : %s", Thread.currentThread().getName(),
                System.currentTimeMillis(), protocolCommandEvent.getMessage());
    }
}
