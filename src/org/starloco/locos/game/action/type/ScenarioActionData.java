package org.starloco.locos.game.action.type;

import org.starloco.locos.player.Player;
import org.starloco.locos.game.action.ExchangeAction;

import java.util.function.BiConsumer;

public class ScenarioActionData implements ActionDataInterface {
    private final ExchangeAction<?> source;
    private final BiConsumer<Player,Boolean> onCompleted;

    public ScenarioActionData(ExchangeAction<?> source, BiConsumer<Player,Boolean> onCompleted) {
        this.source = source;
        this.onCompleted = onCompleted;
    }

    public void onCompletion(Player player, boolean succeed) {
        player.setExchangeAction(source);
        onCompleted.accept(player, succeed);
    }
}
