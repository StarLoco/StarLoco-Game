package org.starloco.locos.command.administration;

import org.starloco.locos.client.Account;
import org.starloco.locos.client.Player;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.game.GameClient;
import org.starloco.locos.kernel.Config;
import org.starloco.locos.kernel.Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class AdminUser {

    private Account account;
    private Player player;
    private GameClient client;

    private boolean timerStart = false;
    private Timer timer;

    public AdminUser(Player player) {
        if (player != null) {
            this.account = player.getAccount();
            this.player = player;
            this.client = player.getAccount().getGameClient();
        }
    }

    public Account getAccount() {
        return account;
    }

    public Player getPlayer() {
        return player;
    }

    public GameClient getClient() {
        return client;
    }

    protected boolean isTimerStart() {
        return timerStart;
    }

    protected void setTimerStart(boolean timerStart) {
        this.timerStart = timerStart;
    }

    protected Timer getTimer() {
        return timer;
    }

    protected void setTimer(Timer timer) {
        this.timer = timer;
    }

    protected Timer createTimer(final int timer) {
        ActionListener action = new ActionListener() {
            int time = timer;

            public void actionPerformed(ActionEvent event) {
                time = time - 1;
                if (time == 1)
                    SocketManager.GAME_SEND_Im_PACKET_TO_ALL("115;" + time + " minute");
                else
                    SocketManager.GAME_SEND_Im_PACKET_TO_ALL("115;" + time + " minutes");
                if (time <= 0) Main.stop("Shutdown by an administrator");
            }
        };
        return new Timer(60000, action);
    }

    public void sendMessage(String message) {
        this.player.send(buildBAT(0, message));
    }

    protected void sendErrorMessage(String message) {
        this.player.send(buildBAT(1, message));
    }

    protected void sendSuccessMessage(String message) {
        this.player.send(buildBAT(2, message));
    }

    private String buildBAT(int flag, String message) {
        return "BAT"
                .concat(String.valueOf(flag))
                .concat(Config.isVersionGreaterThan("1.35.0") ? "|12||" : "")
                .concat(message);
    }

    public abstract void apply(String packet);
}