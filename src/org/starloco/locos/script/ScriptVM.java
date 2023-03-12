package org.starloco.locos.script;

import org.classdump.luna.ByteString;
import org.classdump.luna.StateContext;
import org.classdump.luna.Table;
import org.classdump.luna.Variable;
import org.classdump.luna.compiler.CompilerChunkLoader;
import org.classdump.luna.env.RuntimeEnvironment;
import org.classdump.luna.env.RuntimeEnvironments;
import org.classdump.luna.exec.CallException;
import org.classdump.luna.exec.CallPausedException;
import org.classdump.luna.exec.DirectCallExecutor;
import org.classdump.luna.impl.NonsuspendableFunctionException;
import org.classdump.luna.impl.StateContexts;
import org.classdump.luna.lib.*;
import org.classdump.luna.load.ChunkLoader;
import org.classdump.luna.load.LoaderException;
import org.classdump.luna.runtime.AbstractFunction1;
import org.classdump.luna.runtime.AbstractFunctionAnyArg;
import org.classdump.luna.runtime.ExecutionContext;
import org.classdump.luna.runtime.LuaFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

public class ScriptVM {
    public static final Logger logger = LoggerFactory.getLogger("Script");
    protected final Table env;
    protected final StateContext state;
    protected final ChunkLoader loader;
    protected final DirectCallExecutor executor = DirectCallExecutor.newExecutor();

    protected ScriptVM(String name) throws CallException, LoaderException, IOException, CallPausedException, InterruptedException {
        this.state = StateContexts.newDefaultInstance();
        RuntimeEnvironment rtEnv = RuntimeEnvironments.system();

        this.loader = CompilerChunkLoader.of(name);
        this.env = state.newTable();
        BasicLib.installInto(state, env, rtEnv, null);
        ModuleLib.installInto(state, env, rtEnv, this.loader, null);
        CoroutineLib.installInto(state, env);
        StringLib.installInto(state, env);
        MathLib.installInto(state, env);
        TableLib.installInto(state, env);

        this.env.rawset("JLogF", new LogF());
        runFile(Paths.get("scripts", "Common.lua"));
    }

    protected void runDirectory(Path path) throws IOException, LoaderException, CallException, CallPausedException, InterruptedException {
        try (Stream<Path> paths = Files.walk(path)) {
            Iterator<Path> it = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".lua")).iterator();
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

    static class LogF extends AbstractLibFunction {
        @Override
        protected String name() { return "JLogF"; }

        @Override
        public void invoke(ExecutionContext context, ArgumentIterator args) {
            ByteString fmt = args.nextString();
            logger.info(fmt.toString(), args.copyRemaining());
        }
    }

    public static int rawInt(Table v, Object key) {
        return ((Long)v.rawget(key)).intValue();
    }
    public static int rawInt(Table v, long key) {
        return ((Long)v.rawget(key)).intValue();
    }

    @SuppressWarnings("unchecked")
    public static <T> ArrayList<T> fromLuaTable(Table t) {
        ArrayList<T> out = new ArrayList<>();

        long len = t.rawlen();
        for(int i=1;i<=len;i++){
            out.add((T)t.rawget(i));
        }

        return out;
    }

    public static ArrayList<Integer> intsFromLuaTable(Table t) {
        ArrayList<Integer> out = new ArrayList<>();
        if(t == null) return out;

        long len = t.rawlen();
        for(int i=1;i<=len;i++){
            int a = rawInt (t, i);
            out.add(a);
        }

        return out;
    }
}
