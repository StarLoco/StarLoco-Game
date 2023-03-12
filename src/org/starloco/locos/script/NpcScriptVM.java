package org.starloco.locos.script;

import org.classdump.luna.Table;
import org.classdump.luna.Userdata;
import org.classdump.luna.exec.CallException;
import org.classdump.luna.exec.CallPausedException;
import org.classdump.luna.exec.DirectCallExecutor;
import org.classdump.luna.impl.NonsuspendableFunctionException;
import org.classdump.luna.load.LoaderException;
import org.classdump.luna.runtime.*;
import org.starloco.locos.client.Player;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.game.world.World;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public final class NpcScriptVM extends ScriptVM {
    private static NpcScriptVM instance;
    private final Object _vmLock = new Object(); // Find a better way if performances become an issue

    @SuppressWarnings("unchecked")
    private NpcScriptVM() throws LoaderException, IOException, CallException, CallPausedException, InterruptedException {
        super("NPC");

        this.customizeEnv();
        this.runFile(Paths.get("scripts", "Npc.lua"));
        this.runDirectory(Paths.get("scripts", "npcs"));
    }

    private void customizeEnv() {
        this.env.rawset("RegisterNPCDef", new RegisterNpcTemplate());
    }

    public static synchronized void init() throws LoaderException, IOException, CallException, CallPausedException, InterruptedException {
        if(instance != null) return;

        instance = new NpcScriptVM();
    }

    public static NpcScriptVM getInstance()  {
        return instance;
    }

    public void call(Object val, Object... args) {
        synchronized (_vmLock) {
            try {
                this.executor.call(this.state, val, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class RegisterNpcTemplate extends AbstractFunction1<Table> {
        @Override
        public void invoke(ExecutionContext context, Table val) {
            World.world.addNpcTemplate(new NpcTemplate(val));
            context.getReturnBuffer().setTo();
        }

        @Override
        public void resume(ExecutionContext context, Object suspendedState) {
            throw new NonsuspendableFunctionException();
        }
    }
}
