package org.starloco.locos.game;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.starloco.locos.game.filter.PacketFilter;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Main;

public class GameHandler implements IoHandler {

    private final static PacketFilter filter = new PacketFilter().activeSafeMode();

    @Override
    public void sessionCreated(IoSession arg0) {
        if (!filter.authorizes(arg0.getRemoteAddress().toString().substring(1).split(":")[0])) {
            arg0.close(true);
        } else {
            World.world.logger.info("Session " + arg0.getId() + " created");
            arg0.setAttribute("client", new GameClient(arg0));
        }
    }

    @Override
    public void messageReceived(IoSession arg0, Object arg1) throws Exception {
        GameClient client = (GameClient) arg0.getAttribute("client");
        String packet = (String) arg1;

        if(client != null) {
            if (Config.encryption && !packet.startsWith("AT") && !packet.startsWith("Ak")) {
                packet = World.world.getCryptManager().decryptMessage(packet, client.getPreparedKeys());
                if (packet != null) packet = packet.replace("\n", "");
                else packet = (String) arg1;
            }

            String[] s = packet.split("\n");

            int i = 0;
            do {
                if(s[i].contains("ù")){
                    s[i] = s[i].split("ù")[2];
                }
                client.parsePacket(s[i]);
                if (Config.debug)
                    World.world.logger.trace((client.getPlayer() == null ? "" : client.getPlayer().getName()) + " <-- " + s[i]);

                i++;
            } while (i == s.length - 1);
        }
    }

    @Override
    public void sessionClosed(IoSession arg0) {
        this.kick(arg0);
        World.world.logger.info("Session " + arg0.getId() + " closed");
    }

    @Override
    public void exceptionCaught(IoSession arg0, Throwable arg1) {
        if(arg1 == null) return;
        if(arg1.getMessage() != null && (arg1 instanceof org.apache.mina.filter.codec.RecoverableProtocolDecoderException || arg1.getMessage().startsWith("Une connexion ") ||
                arg1.getMessage().startsWith("Connection reset by peer") || arg1.getMessage().startsWith("Connection timed out")))
            return;
        arg1.printStackTrace();
        if (Config.debug)
            World.world.logger.error("Exception connexion client : " + arg1.getMessage());
        this.kick(arg0);
    }

    @Override
    public void messageSent(IoSession arg0, Object arg1) {
        GameClient client = (GameClient) arg0.getAttribute("client");

        if (client != null) {
            if (Config.debug) {
                String packet = (String) arg1;
                if (Config.encryption && !packet.startsWith("AT") && !packet.startsWith("HG"))
                    packet = World.world.getCryptManager().decryptMessage(packet, client.getPreparedKeys()).replace("\n", "");
                if (packet.startsWith("am")) return;
                World.world.logger.trace((client.getPlayer() == null ? "" : client.getPlayer().getName()) + " --> " + packet);
            }
        }
    }

    @Override
    public void inputClosed(IoSession ioSession) {
        ioSession.close(true);
    }

    @Override
    public void sessionIdle(IoSession arg0, IdleStatus arg1) {
        World.world.logger.info("Session " + arg0.getId() + " idle");
}

    @Override
    public void sessionOpened(IoSession arg0) {
        World.world.logger.info("Session " + arg0.getId() + " opened");
    }

    void kick(IoSession arg0) {
        GameClient client = (GameClient) arg0.getAttribute("client");
        if (client != null) {
            client.disconnect();
            client.kick();
            arg0.setAttribute("client", null);
        }
    }
}
