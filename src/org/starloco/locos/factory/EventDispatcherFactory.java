package org.starloco.locos.factory;


import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.LoggerFactory;
import org.starloco.locos.annotation.Handler;
import org.starloco.locos.api.AbstractDofusMessage;
import org.starloco.locos.eventbus.SyncMessageEvent;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventDispatcherFactory {
    
    private static SyncMessageEvent<AbstractDofusMessage> syncDofusMessageEvent;
    
    public EventDispatcherFactory() {
        syncDofusMessageEvent = new SyncMessageEvent<>();
    }
    
    public void init() {
        Reflections reflections = new Reflections(new ConfigurationBuilder().forPackage("org.starloco.locos.eventbus").setScanners(new SubTypesScanner(false), new MethodAnnotationsScanner()));
        Set<Method> handlerMethods = reflections.getMethodsAnnotatedWith(Handler.class);
        Set<Class<?>> handlerClasses = handlerMethods.stream().map(Method::getDeclaringClass).collect(Collectors.toSet());
        
        handlerClasses.forEach(handlerClass -> {
            Object handler = null;
            try {
                handler = handlerClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            LoggerFactory.getLogger(EventDispatcherFactory.class).debug("Register handler: {}", handler.getClass());
            syncDofusMessageEvent.subscribe(handler);
        });
    }
    
    public static void dispatch(AbstractDofusMessage message) {
        syncDofusMessageEvent.publish(message);
    }
}
