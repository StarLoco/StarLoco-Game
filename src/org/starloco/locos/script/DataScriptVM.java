package org.starloco.locos.script;

import org.classdump.luna.Table;
import org.classdump.luna.exec.CallException;
import org.classdump.luna.exec.CallPausedException;
import org.classdump.luna.impl.NonsuspendableFunctionException;
import org.classdump.luna.load.LoaderException;
import org.classdump.luna.runtime.*;
import org.starloco.locos.area.map.ScriptMapData;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.game.world.World;

import java.io.IOException;
import java.nio.file.Paths;

public final class DataScriptVM extends ScriptVM {
    private static DataScriptVM instance;

    private DataScriptVM() throws LoaderException, IOException, CallException, CallPausedException, InterruptedException {
        super("Data");
    }

    public void loadData() throws CallException, LoaderException, IOException, CallPausedException, InterruptedException {
        super.loadData();
        this.customizeEnv();
        this.runFile(Paths.get("scripts", "Data.lua"));
    }

    public void safeLoadData() {
        try {
            this.loadData();
        } catch (Exception e) {
            ScriptVM.logger.error("Failed to load static data", e);
        }
    }

    private void customizeEnv() {
        this.env.rawset("RegisterNPCDef", new RegisterNpcTemplate());
        this.env.rawset("RegisterMapDef", new RegisterMapTemplate());
    }

    public static synchronized void init() throws LoaderException, IOException, CallException, CallPausedException, InterruptedException {
        if(instance != null) return;

        instance = new DataScriptVM();
    }

    public static DataScriptVM getInstance()  {
        return instance;
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

    static class RegisterMapTemplate extends AbstractFunction1<Table> {
        @Override
        public void invoke(ExecutionContext context, Table val) {
            World.world.addMapData(ScriptMapData.build(val));
            context.getReturnBuffer().setTo();
        }

        @Override
        public void resume(ExecutionContext context, Object suspendedState) {
            throw new NonsuspendableFunctionException();
        }
    }
}
