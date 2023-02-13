package org.starloco.locos.hdv;

import org.starloco.locos.game.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HdvTemplate {

    private int templateId;
    private Map<Integer, HdvLine> lines = new HashMap<Integer, HdvLine>();

    public HdvTemplate(int templateId, HdvEntry toAdd) {
        this.templateId = templateId;
        addEntry(toAdd);
    }

    public int getTemplateId() {
        return templateId;
    }

    public Map<Integer, HdvLine> getLines() {
        return lines;
    }

    public HdvLine getLine(int lineId) {
        return lines.get(lineId);
    }

    public void addEntry(HdvEntry toAdd) {
        for (HdvLine line : this.getLines().values())
            //Boucle dans toutes les lignes pour essayer de trouver des objets de m�mes stats
            if (line.addEntry(toAdd))//Si une ligne l'accepte, arr�te la m�thode.
                return;
        int lineId = World.world.getNextLineHdvId();
        this.getLines().put(lineId, new HdvLine(lineId, toAdd));
    }

    public boolean delEntry(HdvEntry toDel) {
        boolean toReturn = this.getLines().get(toDel.getLineId()).delEntry(toDel);
        if (this.getLines().get(toDel.getLineId()).isEmpty())//Si la ligne est devenue vide
            this.getLines().remove(toDel.getLineId());
        return toReturn;
    }

    public ArrayList<HdvEntry> getAllEntry() {
        ArrayList<HdvEntry> toReturn = new ArrayList<HdvEntry>();
        for (HdvLine line : this.getLines().values())
            toReturn.addAll(line.getAll());
        return toReturn;
    }

    public boolean isEmpty() {
        return this.getLines().size() == 0;
    }

    public String parseToEHl() {
        String toReturn = this.getTemplateId() + "|";
        boolean isFirst = true;
        for (HdvLine line : this.getLines().values()) {
            if (!isFirst)
                toReturn += "|";
            toReturn += line.parseToEHl();
            isFirst = false;
        }
        return toReturn;
    }
}