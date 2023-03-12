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
            int id = rawInt(val, "id");
            int gfxID = rawInt(val, "gfxID");
            int gender = rawInt(val, "gender");
            int scaleX = rawInt(val, "scaleX");
            int scaleY = rawInt(val, "scaleY");
            Table colors = (Table)val.rawget("colors");
            int color1 = rawInt(colors,1);
            int color2 = rawInt(colors,2);
            int color3 = rawInt(colors,3);
            Table accessories = (Table)val.rawget("accessories");
            List<Integer> accessoriesNb = ScriptVM.intsFromLuaTable(accessories);
            int customArtwork = rawInt(val,"customArtwork");

            World.world.addNpcTemplate(new NpcTemplate(id, gfxID, scaleX, scaleY, gender, color1, color2, color3, accessoriesNb, customArtwork));

            context.getReturnBuffer().setTo();
        }

        @Override
        public void resume(ExecutionContext context, Object suspendedState) {
            throw new NonsuspendableFunctionException();
        }
    }
}
