package org.starloco.locos.eventbus;

import org.starloco.locos.api.AbstractDofusMessage;
import org.starloco.locos.api.AbstractEventMessageDispatcher;

public class SyncMessageEvent<T> extends AbstractEventMessageDispatcher<T> {
    
    @Override
    public void publish(T message) {
        doPublish(message);
    }
}
