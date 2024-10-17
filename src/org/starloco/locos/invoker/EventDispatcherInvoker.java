package org.starloco.locos.invoker;

import org.starloco.locos.api.AbstractDofusMessage;

import java.lang.reflect.Method;
import java.util.List;

public class EventDispatcherInvoker<T> {
    
    private final Object handler;
    private final Method methods;
    private final T message;
    
    public EventDispatcherInvoker(Object handler, Method methods, T message) {
        this.handler = handler;
        this.methods = methods;
        this.message = message;
    }
    
    public T getMessage() {
        return message;
    }
    
    public Method getMethods() {
        return methods;
    }
    
    public Object getHandler() {
        return handler;
    }
}
