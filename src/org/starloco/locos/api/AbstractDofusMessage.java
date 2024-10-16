package org.starloco.locos.api;

import org.starloco.locos.game.GameClient;

public abstract class AbstractDofusMessage {
    
    private StringBuilder output;
    private StringBuilder input;
    private GameClient client;
    
    public AbstractDofusMessage() {
        this.output = new StringBuilder();
    }
    
    public StringBuilder getOutput() {
        return output;
    }
    
    public void setOutput(StringBuilder output) {
        this.output = output;
    }
    
    public StringBuilder getInput() {
        return input;
    }
    
    public void setInput(StringBuilder input) {
        this.input = input;
    }
    
    public GameClient getClient() {
        return client;
    }
    
    public void setClient(GameClient client) {
        this.client = client;
    }
    
    public abstract void serialize();
    
    public abstract void deserialize();
}
