package org.starloco.locos.script;

import org.classdump.luna.Table;
import org.classdump.luna.Userdata;
import org.classdump.luna.exec.CallException;
import org.classdump.luna.exec.CallPausedException;
import org.classdump.luna.impl.NonsuspendableFunctionException;
import org.classdump.luna.load.LoaderException;
import org.classdump.luna.runtime.*;
import org.starloco.locos.client.Player;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.game.world.World;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public final class NpcScriptVM extends ScriptVM {
    private static NpcScriptVM instance;
    private final Object _vmLock = new Object(); // Find a better way if performances become an issue
    private final AbstractFunction3<Userdata<Player>,Integer,Integer> onNpcDialog;

    @SuppressWarnings("unchecked")
    private NpcScriptVM() throws LoaderException, IOException, CallException, CallPausedException, InterruptedException {
        super("NPC");

        this.customizeEnv();
        this.runFile(Paths.get("scripts", "Npc.lua"));
        this.runDirectory(Paths.get("scripts", "npcs"));

        try {
            onNpcDialog = (AbstractFunction3<Userdata<Player>,Integer,Integer>)env.rawget("onNpcDialog");
        } catch(ClassCastException e) {
            throw new RuntimeException("onNpcDialog is not a Function<Userdata<Player>,Integer,Integer>", e);
        }
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

    public void OnNPCDialog(int npcID, Player player, int answer) {
        // Lazy thread safety. The Lua VM is not concurrent
        synchronized (_vmLock) {
            try {
                this.executor.call(this.state, onNpcDialog, player.Scripted(), npcID, answer);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class RegisterNpcTemplate extends AbstractFunction1<Table> {
        @Override
        public void invoke(ExecutionContext context, Table val) {
            int id = Math.toIntExact((Long)val.rawget("id"));
            int gfxID = Math.toIntExact((Long)val.rawget("gfxID"));
            int gender = Math.toIntExact((Long)val.rawget("gender"));
            int scaleX = Math.toIntExact((Long)val.rawget("scaleX"));
            int scaleY = Math.toIntExact((Long)val.rawget("scaleY"));
            Table colors = (Table)val.rawget("colors");
            int color1 = Math.toIntExact((Long)colors.rawget(1));
            int color2 = Math.toIntExact((Long)colors.rawget(2));
            int color3 = Math.toIntExact((Long)colors.rawget(3));
            Table accessories = (Table)val.rawget("accessories");
            List<Integer> accessoriesNb = ScriptVM.intsFromLuaTable(accessories);
            int customArtwork = Math.toIntExact((Long)val.rawget("customArtwork"));

            World.world.addNpcTemplate(new NpcTemplate(id, gfxID, scaleX, scaleY, gender, color1, color2, color3, accessoriesNb, customArtwork));

            context.getReturnBuffer().setTo();
        }

        @Override
        public void resume(ExecutionContext context, Object suspendedState) {
            throw new NonsuspendableFunctionException();
        }
    }
}
