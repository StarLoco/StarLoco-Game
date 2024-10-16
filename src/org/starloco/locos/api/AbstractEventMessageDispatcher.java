package org.starloco.locos.api;

import org.starloco.locos.annotation.Handler;
import org.starloco.locos.invoker.EventDispatcherInvoker;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractEventMessageDispatcher<T> {
    
    protected List<EventDispatcherInvoker<T>> invokers = new ArrayList<>();
    
    public abstract void publish(T parameterObject);
    
    public void doPublish(T message) {
        for(EventDispatcherInvoker invoker : invokers) {
            if(invoker.getMessage().getClass().isAssignableFrom(message.getClass())) {
                try {
                    invoker.getMethods().invoke(invoker.getHandler(), message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void subscribe(Object handler) {
        for(Method method : handler.getClass().getDeclaredMethods()) {
            if(method.isAnnotationPresent(Handler.class)) {
                if(method.getParameterCount() != 1) {
                    throw new IllegalArgumentException("Handler method must have only one parameter");
                }
                Class<T> parameterType = (Class<T>) method.getParameterTypes()[0];
                invokers.add(new EventDispatcherInvoker(handler, method, parameterType));
            }
        }
    }
    
}
