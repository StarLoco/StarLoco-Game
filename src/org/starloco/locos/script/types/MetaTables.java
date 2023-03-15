package org.starloco.locos.script.types;

import org.classdump.luna.Metatables;
import org.classdump.luna.Table;
import org.classdump.luna.Userdata;
import org.classdump.luna.impl.DefaultUserdata;
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.lib.AbstractLibFunction;
import org.classdump.luna.lib.ArgumentIterator;
import org.classdump.luna.runtime.ExecutionContext;
import org.starloco.locos.script.ScriptVM;
import org.starloco.locos.util.Pair;

import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class MetaTables {
    private static Map<Class<?>, BiConsumer<ExecutionContext, Object>> returnValueConsumers = new HashMap<Class<?>, BiConsumer<ExecutionContext, Object>>(){{
        put(Pair.class, (ctx, v) -> {
            Pair p = (Pair)v;
            ctx.getReturnBuffer().setTo(p.first, p.second);
        });
    }};

    // Creates index table from a POJO using declared methods
    // Only methods with the following signatures are allowed:
    // static void method(T)
    // static void method(T, ArgumentIterator)
    // static ? method(T, ArgumentIterator)
    // TODO: support multiple return values
    @SuppressWarnings("unchecked")
    public static <T, S extends Userdata<T>> Table ReflectIndexTable(Class<S> c) {
        ImmutableTable.Builder b = new ImmutableTable.Builder();

        ParameterizedType genSuper = ((ParameterizedType)c.getGenericSuperclass());
        if(genSuper.getRawType() != DefaultUserdata.class) throw new IllegalArgumentException("ReflectIndexTable param must directly extend DefaultUserdata");

        Class<T> pojoClass = (Class<T>)genSuper.getActualTypeArguments()[0];

        for(Method m : c.getDeclaredMethods()) {
            Parameter[] params = m.getParameters();
            if(!Modifier.isStatic(m.getModifiers()) || params.length == 0 || params.length > 2) continue;

            // Ensure 1st param type is T
            if(params[0].getType() != pojoClass)continue;

            // Ensure 2nd param type is ArgumentIterator if present
            boolean has2ndParam = params.length == 2;
            if(has2ndParam && params[1].getType() != ArgumentIterator.class) continue;

            m.setAccessible(true); // Make the method accessible even if private
            b.add(m.getName(), new AbstractLibFunction(){
                @Override
                protected String name() { return m.getName(); }

                @Override
                protected void invoke(ExecutionContext context, ArgumentIterator args) {
                    T t = args.nextUserdata(c.getTypeName(), c).getUserValue();

                    try {
                        Object ret = has2ndParam?m.invoke(null, t, args):m.invoke(null, t);
                        if(ret==null) {
                            context.getReturnBuffer().setTo();
                            return;
                        }
                        // Allow some types to be sent back to Lua differently
                        BiConsumer<ExecutionContext, Object> consumer = returnValueConsumers.getOrDefault(ret.getClass(), (ctx, v) -> ctx.getReturnBuffer().setTo(v));
                        consumer.accept(context, ret);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            ScriptVM.logger.debug("Found script method {}:{}", c.getSimpleName(), m.getName());
        }

        return b.build();
    }

    public static ImmutableTable MetaTable(Table index) {
        return new ImmutableTable.Builder().add(Metatables.MT_INDEX, index).build();
    }
}
