package org.starloco.locos.factory;

import org.reflections.Reflections;
import org.slf4j.LoggerFactory;
import org.starloco.locos.annotation.DofusMessage;
import org.starloco.locos.api.AbstractDofusMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DofusMessageFactory {
    
    private static final Map<String, Class<? extends AbstractDofusMessage>> messages = new HashMap<>();
    
    public void init() {
        Reflections reflections = new Reflections("org.starloco.locos.proto");
        Set<Class<? extends AbstractDofusMessage>> messageClasses = reflections.getSubTypesOf(AbstractDofusMessage.class);
        for (Class<? extends AbstractDofusMessage> messageClass : messageClasses) {
            DofusMessage dofusMessage = messageClass.getAnnotation(DofusMessage.class);
            if(dofusMessage != null) {
                String header = dofusMessage.header();
                messages.put(header, messageClass);
                LoggerFactory.getLogger(DofusMessageFactory.class).info("Register message: {} with header: {}", messageClass.getName(), header);
            }
        }
    }
    
    public static AbstractDofusMessage getMessage(String header) {
        if(!messages.containsKey(header)) {
            return null;
        }
        AbstractDofusMessage message = null;
        try {
            message = messages.get(header).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return message;
    }
}
