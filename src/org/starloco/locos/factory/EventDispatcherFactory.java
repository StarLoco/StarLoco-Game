package org.starloco.locos.factory;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import net.engio.mbassy.listener.Handler;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.LoggerFactory;
import org.starloco.locos.api.AbstractDofusMessage;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EventDispatcherFactory {
    
    private static MBassador<AbstractDofusMessage> eventBus;
    
    public EventDispatcherFactory() {
        BusConfiguration busConfiguration = new BusConfiguration();
        busConfiguration.addPublicationErrorHandler(new IPublicationErrorHandler() {
            @Override
            public void handleError(PublicationError publicationError) {
                LoggerFactory.getLogger(EventDispatcherFactory.class).error("Error while publishing message", publicationError.getCause());
            }
        });
        busConfiguration.addFeature(Feature.SyncPubSub.Default());
        busConfiguration.addFeature(Feature.AsynchronousHandlerInvocation.Default());
        busConfiguration.addFeature(Feature.AsynchronousMessageDispatch.Default());
        eventBus = new MBassador<>(busConfiguration);
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
            eventBus.subscribe(handler);
        });
    }
    
    public static void dispatch(AbstractDofusMessage message) {
        eventBus.publish(message);
    }
}
