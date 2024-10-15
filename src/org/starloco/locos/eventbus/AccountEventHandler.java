package org.starloco.locos.eventbus;

import net.engio.mbassy.listener.Handler;
import org.starloco.locos.proto.AccountQueuePositionMessage;

public class AccountEventHandler {
    
    @Handler
    public void onQueue(AccountQueuePositionMessage message) {
        message.getClient().send(new AccountQueuePositionMessage(1, 1, 1, 1));
    }
}
