package org.starloco.locos.script;

import org.classdump.luna.StateContext;
import org.classdump.luna.Table;
import org.classdump.luna.Variable;
import org.classdump.luna.compiler.CompilerChunkLoader;
import org.classdump.luna.compiler.gen.asm.helpers.ExecutionContextMethods;
import org.classdump.luna.env.RuntimeEnvironment;
import org.classdump.luna.env.RuntimeEnvironments;
import org.classdump.luna.exec.CallException;
import org.classdump.luna.exec.CallPausedException;
import org.classdump.luna.exec.DirectCallExecutor;
import org.classdump.luna.impl.StateContexts;
import org.classdump.luna.lib.*;
import org.classdump.luna.load.ChunkLoader;
import org.classdump.luna.load.LoaderException;
import org.classdump.luna.runtime.ExecutionContext;
import org.classdump.luna.runtime.LuaFunction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

public class ScriptVM {
    protected final Table env;
    protected final StateContext state;
    protected final ChunkLoader loader;
    protected final DirectCallExecutor executor = DirectCallExecutor.newExecutor();

    private ScriptVM(String name, StateContext ctx, Table state){
        this.state = ctx;
        this.env = state;
        this.loader = CompilerChunkLoader.of(name);
    }

    protected ScriptVM(String name) {
        this.state = StateContexts.newDefaultInstance();
        RuntimeEnvironment rtEnv = RuntimeEnvironments.system();

        this.loader = CompilerChunkLoader.of(name);
        this.env = state.newTable();
        BasicLib.installInto(state, env, rtEnv, null);
        ModuleLib.installInto(state, env, rtEnv, null, null);
        CoroutineLib.installInto(state, env);
        StringLib.installInto(state, env);
        MathLib.installInto(state, env);
        TableLib.installInto(state, env);
    }

    protected void runDirectory(Path path) throws IOException, LoaderException, CallException, CallPausedException, InterruptedException {
        try (Stream<Path> paths = Files.walk(path)) {
            Iterator<Path> it = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.endsWith(".lua")).iterator();
            while(it.hasNext()) {
                this.runFile(it.next());
            }
        }

    }

    protected void runFile(Path path) throws IOException, LoaderException, CallException, CallPausedException, InterruptedException {
        byte[] bytes = Files.readAllBytes(path);

        LuaFunction<?,?,?,?,?> fn = loader.loadTextChunk(new Variable(env), path.toString(), new String(bytes)); // May need to UTF-8 decode

        DirectCallExecutor.newExecutor().call(this.state, fn);
    }

}
