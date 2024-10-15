package org.starloco.locos.game;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;
import org.starloco.locos.api.AbstractDofusMessage;
import org.starloco.locos.factory.DofusMessageFactory;
import org.starloco.locos.factory.EventDispatcherFactory;
import org.starloco.locos.game.filter.PacketFilter;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Config;

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

            for(String p : s){
                if(p.charAt(0) == 'ù') {
                    if(p.split("ù").length < 3) continue;
                    p = p.split("ù")[2];
                }
                try {
                    if(p.length() > 1) {
                        AbstractDofusMessage abstractDofusMessage = DofusMessageFactory.getMessage(p.substring(0, 2));
                        if (abstractDofusMessage != null) {
                            abstractDofusMessage.setInput(new StringBuilder(p.substring(2)));
                            abstractDofusMessage.deserialize();
                            abstractDofusMessage.setClient(client);
                            LoggerFactory.getLogger(GameHandler.class).info("Receive message: {} with header: {}", abstractDofusMessage.getClass().getName(), p.substring(0, 2));
                            EventDispatcherFactory.dispatch(abstractDofusMessage);
                        } else {
                            client.parsePacket(p);
                        }
                    } else {
                        client.parsePacket(p);
                    }
                } catch(Exception e) {
                    throw new Exception("Cannot process packet: "+p, e);
                } finally {
                    if (Config.debug) {
                        World.world.logger.trace((client.getPlayer() == null ? "" : client.getPlayer().getName()) + " <-- " + p);
                    }
                }
            }
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
            World.world.logger.error("Exception connexion client : ", arg1);
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
