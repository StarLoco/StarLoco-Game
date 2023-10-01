package org.starloco.locos.script;

import org.classdump.luna.ByteString;
import org.classdump.luna.Table;
import org.classdump.luna.exec.CallException;
import org.classdump.luna.exec.CallPausedException;
import org.classdump.luna.impl.NonsuspendableFunctionException;
import org.classdump.luna.lib.AbstractLibFunction;
import org.classdump.luna.lib.ArgumentIterator;
import org.classdump.luna.load.LoaderException;
import org.classdump.luna.runtime.AbstractFunction1;
import org.classdump.luna.runtime.ExecutionContext;
import org.starloco.locos.anims.Animation;
import org.starloco.locos.anims.KeyFrame;
import org.starloco.locos.area.map.ScriptMapData;
import org.starloco.locos.command.administration.Command;
import org.starloco.locos.command.administration.Group;
import org.starloco.locos.database.data.game.ExperienceTables;
import org.starloco.locos.entity.npc.NpcTemplate;
import org.starloco.locos.game.world.World;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.Map;

public final class DataScriptVM extends ScriptVM {
    private static DataScriptVM instance;
    public final EventHandlers handlers = new EventHandlers(this);

    private DataScriptVM() throws LoaderException, IOException, CallException, CallPausedException, InterruptedException {
        super("Data");
    }

    @Override
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
        this.env.rawset("RegisterAdminCommand", new RegisterAdminCommand());
        this.env.rawset("RegisterAdminGroup", new RegisterAdminGroup());
        this.env.rawset("RegisterExpTables", new RegisterExpTables());
        this.env.rawset("RegisterMapDef", new RegisterMapTemplate());
        this.env.rawset("NewAnimation", new NewAnimation());
        this.env.rawset("Handlers", handlers);
    }

    public static synchronized void init() throws LoaderException, IOException, CallException, CallPausedException, InterruptedException {
        if(instance != null) return;

        Instant start = Instant.now();
        instance = new DataScriptVM();
        instance.loadData();
        Duration loadDuration = Duration.between(start, Instant.now());
        logger.info("Scripts loaded in {} ms", loadDuration.toMillis());
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
            try {
                World.world.addMapData(ScriptMapData.build(val));
            }catch (Exception e) {
                logger.error("Cannot register map #"+rawInt(val, "id"), e);
            }
            context.getReturnBuffer().setTo();
        }

        @Override
        public void resume(ExecutionContext context, Object suspendedState) {
            throw new NonsuspendableFunctionException();
        }
    }
    static class RegisterAdminCommand extends AbstractLibFunction {
        @Override
        protected String name() {
            return "RegisterAdminCommand";
        }

        @Override
        public void invoke(ExecutionContext context, ArgumentIterator  args) {
            String cName = args.nextString().toString();
            String cArgs = args.nextOptionalString(ByteString.of("")).toString();
            String cDesc = args.nextOptionalString(ByteString.of("")).toString();

            new Command(cName, cArgs, cDesc);
            context.getReturnBuffer().setTo();
        }
    }

    static class RegisterAdminGroup extends AbstractLibFunction {
        @Override
        protected String name() {
            return "RegisterAdminGroup";
        }

        @Override
        public void invoke(ExecutionContext context, ArgumentIterator  args) {
            int gId = args.nextInt();
            String gName = args.nextString().toString();
            boolean gIsPlayer = args.nextBoolean();
            Object gCommands = args.next();

            if(gCommands instanceof Boolean) {
                new Group(gId, gName, gIsPlayer, (Boolean)gCommands, Collections.emptyList());
            } else if (gCommands instanceof Table){
                new Group(gId, gName, gIsPlayer, false, listOfString((Table) gCommands));
            }else {
                throw new IllegalArgumentException("RegisterAdminGroup with invalid commands param");
            }

            context.getReturnBuffer().setTo();
        }
    }

    static class RegisterExpTables extends AbstractLibFunction {
        @Override
        protected String name() {
            return "RegisterExpTables";
        }

        @Override
        public void invoke(ExecutionContext context, ArgumentIterator  args) {
            long[] players = longArrayFromLuaTable(args.nextTable());
            long[] guilds = longArrayFromLuaTable(args.nextTable());
            long[] jobs = longArrayFromLuaTable(args.nextTable());
            long[] mounts = longArrayFromLuaTable(args.nextTable());
            long[] pvp = longArrayFromLuaTable(args.nextTable());
            long[] livitinems = longArrayFromLuaTable(args.nextTable());
            long[] tormentators = longArrayFromLuaTable(args.nextTable());
            long[] bandits = longArrayFromLuaTable(args.nextTable());


            ExperienceTables tables = new ExperienceTables(players, guilds, jobs, mounts, pvp, livitinems, tormentators, bandits);
            World.world.setExperiences(tables);

            context.getReturnBuffer().setTo();
        }
    }

    static class NewAnimation extends AbstractLibFunction {
        @Override
        protected String name() {
            return "NewAnimation";
        }
        @Override
        public void invoke(ExecutionContext context, ArgumentIterator  args) {
            int spriteID = args.nextInt();
            String defaultFrame = args.nextString().toString();
            Table tKeyFrames = args.nextTable();

            Map<String, KeyFrame> keyFrames = DataScriptVM.mapFromScript(tKeyFrames,
                Object::toString,
                v -> KeyFrame.fromScriptValue((Table)v)
            );

            if(!keyFrames.containsKey(defaultFrame)) {
                throw new IllegalArgumentException("default frame is not a key frame");
            }

            Animation anim = new org.starloco.locos.anims.Animation(spriteID, defaultFrame, keyFrames);
            World.world.addAnimation(anim);
            context.getReturnBuffer().setTo(anim);
        }
    }

}
