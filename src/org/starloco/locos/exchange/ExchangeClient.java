package org.starloco.locos.exchange;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.LoggerFactory;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Main;

import java.net.InetSocketAddress;

public class ExchangeClient {

    public static Logger logger = (Logger) LoggerFactory.getLogger(ExchangeClient.class);

    private IoSession ioSession;
    private ConnectFuture connectFuture;
    private IoConnector ioConnector = new NioSocketConnector();

    public ExchangeClient() {
        this.ioConnector.setHandler(new ExchangeHandler());
        Config.exchangeClient = this;
        ExchangeClient.logger.setLevel(Level.ALL);
    }

    public void setIoSession(IoSession ioSession) {
        this.ioSession = ioSession;
    }

    public IoSession getIoSession() {
        return ioSession;
    }

    public ConnectFuture getConnectFuture() {
        return connectFuture;
    }

    public void initialize() {
        try {
            this.connectFuture = this.ioConnector.connect(new InetSocketAddress(Config.exchangeIp, Config.exchangePort));
        } catch (Exception e) {
            ExchangeClient.logger.error("The game server don't found the login server. Exception : " + e.getMessage());
            try { Thread.sleep(2000); } catch(Exception ignored) {}
            return;
        }

        try { Thread.sleep(3000); } catch(Exception ignored) {}

        if (!ioConnector.isActive()) {
            if (!Config.isRunning) return;

            ExchangeClient.logger.error("Try to connect to the login server..");
            restart();
            return;
        }
        ExchangeClient.logger.info("The exchange client was connected on address : " + Config.exchangeIp + ":" + Config.exchangePort);
    }

    public void restart() {
        if (!Config.isRunning) return;

        ExchangeClient.logger.error("The login server was not found..");

        this.stop();
        this.connectFuture = null;
        this.ioConnector = new NioSocketConnector();
        this.ioConnector.setHandler(new ExchangeHandler());
        this.initialize();
    }

    public void stop() {
        if(this.ioSession != null)
            this.ioSession.close(true);
        if (this.connectFuture != null)
            this.connectFuture.cancel();

        this.connectFuture = null;
        this.ioConnector.dispose();
        ExchangeClient.logger.info("The exchange client was stopped.");
    }

    public void send(String packet) {
        if(this.ioSession != null && !this.ioSession.isClosing() && this.ioSession.isConnected())
            this.getIoSession().write(StringToIoBuffer(packet));
    }
    public static IoBuffer StringToIoBuffer(String packet) {
        byte[] bytes = packet.getBytes();
        IoBuffer ioBuffer = IoBuffer.allocate(bytes.length);
        ioBuffer.put(bytes);
        return ioBuffer.flip();
    }
}
