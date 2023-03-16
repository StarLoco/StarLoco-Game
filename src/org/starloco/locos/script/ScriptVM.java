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
import org.classdump.luna.impl.ImmutableTable;
import org.classdump.luna.impl.StateContexts;
import org.classdump.luna.lib.*;
import org.classdump.luna.load.ChunkLoader;
import org.classdump.luna.load.LoaderException;
import org.classdump.luna.runtime.ExecutionContext;
import org.classdump.luna.runtime.LuaFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.starloco.locos.game.world.World.Couple;
import org.starloco.locos.object.GameObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class ScriptVM {
    public static final Logger logger = LoggerFactory.getLogger("Script");
    protected final Table env;
    protected final StateContext state;
    protected final ChunkLoader loader;
    protected final DirectCallExecutor executor = DirectCallExecutor.newExecutor();

    protected final Object _vmLock = new Object(); // Find a better way if performances become an issue

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
        this.loadData();
    }

    protected void loadData() throws CallException, LoaderException, IOException, CallPausedException, InterruptedException{
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
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void runFile(Path path) throws IOException, LoaderException, CallException, CallPausedException, InterruptedException {
        byte[] bytes = Files.readAllBytes(path);

        LuaFunction<?,?,?,?,?> fn = loader.loadTextChunk(new Variable(env), path.toString(), new String(bytes)); // May need to UTF-8 decode

        this.executor.call(this.state, fn);
    }
    public Object[] call(Object val, Object... args) {
        synchronized (_vmLock) {
            try {
                return this.executor.call(this.state, val, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Object recursiveGet(Table t, Object key) {
        if(t == null) return null;

        Object v = t.rawget(key);
        if(v != null) return v;
        return recursiveGet(t.getMetatable(), key);
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

    public static int rawOptionalInt(Table v, Object key, int val) {
        Object n = v.rawget(key);
        if(n==null) return val;
        return ((Long)v.rawget(key)).intValue();
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

    public static int[] intArrayFromLuaTable(Table t) {
        if(t == null) return null;

        long len = t.rawlen();
        int[] out = new int[(int)len];

        for(int i=0;i<len;i++){
            out[i] = rawInt (t, i+1);
        }

        return out;
    }

    public static Table ItemStack(Couple<Integer,Integer> stack) {
        return new ImmutableTable.Builder()
            .add("itemID", stack.first)
            .add("quantity", stack.second)
            .build();
    }
    public static Couple<Integer,Integer> ItemStackFromLua(Table t) {
        int id = rawInt(t, "itemID");
        int qua = rawInt(t, "quantity");
        return new Couple<>(id, qua);
    }
}
