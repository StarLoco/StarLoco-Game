package org.starloco.locos.script;

import org.classdump.luna.Userdata;
import org.classdump.luna.exec.CallException;
import org.classdump.luna.exec.CallPausedException;
import org.classdump.luna.load.LoaderException;
import org.classdump.luna.runtime.AbstractFunction0;
import org.classdump.luna.runtime.AbstractFunction3;
import org.classdump.luna.runtime.ExecutionContext;
import org.classdump.luna.runtime.LuaFunction;
import org.starloco.locos.client.Player;

import java.io.IOException;
import java.nio.file.Paths;

public final class NpcScriptVM extends ScriptVM {
    private static NpcScriptVM instance;
    private final Object _lock = new Object(); // Find a better way if performances become an issue
    private final AbstractFunction3<Userdata<Player>,Integer,Integer> onNpcDialog;

    @SuppressWarnings("unchecked")
    private NpcScriptVM() throws LoaderException, IOException, CallException, CallPausedException, InterruptedException {
        super("NPC");

        this.runFile(Paths.get("scripts", "Npc.lua"));
        this.runDirectory(Paths.get("scripts", "npcs"));

        Object raw = env.rawget("onNpcDialog");
        try{
            onNpcDialog = (AbstractFunction3<Userdata<Player>,Integer,Integer>)raw;
        }catch(ClassCastException e) {
            throw new RuntimeException("onNpcDialog is not a Function<,Integer,Integer>", e);
        }
    }

    public static synchronized void init() throws LoaderException, IOException, CallException, CallPausedException, InterruptedException {
        if(instance != null) return;

        instance = new NpcScriptVM();
    }

    public static NpcScriptVM getInstance()  {
        return instance;
    }

    public void OnNPCDialog(int npcID, Player player, int answer) {
        // Lazy thread safety. The Lua VM is not concurrent
        synchronized (_lock) {
            try {
                this.executor.call(this.state, onNpcDialog, player.Scripted(), npcID, answer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
