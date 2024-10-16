package org.starloco.locos.eventbus;

import org.starloco.locos.annotation.Handler;
import org.starloco.locos.proto.AccountQueuePositionMessage;

public class AccountEventHandler {
    
    @Handler
    public void onQueue(AccountQueuePositionMessage message) {
        message.getClient().send(new AccountQueuePositionMessage(1, 1, 1, 1));
    }
}
