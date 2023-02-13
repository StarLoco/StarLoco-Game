package org.starloco.locos.hdv;

import java.util.ArrayList;
import java.util.Collections;

public class HdvLine {

    private int lineId;
    private int templateId;
    private ArrayList<ArrayList<HdvEntry>> entries = new ArrayList<ArrayList<HdvEntry>>(3);    //La premi�re ArrayList est un tableau de 3 (0=1 1=10 2=100 de quantit�)
    private String strStats;

    public HdvLine(int lineId, HdvEntry toAdd) {
        this.lineId = lineId;
        this.templateId = toAdd.getGameObject().getTemplate().getId();
        this.strStats = toAdd.getGameObject().parseStatsString();
        for (int i = 0; i < 3; i++)
            //Boucle 3 fois pour ajouter 3 List vide dans la SuperList
            this.getEntries().add(new ArrayList<HdvEntry>());
        addEntry(toAdd);
    }

    public int getLineId() {
        return lineId;
    }

    public int getTemplateId() {
        return templateId;
    }

    public ArrayList<ArrayList<HdvEntry>> getEntries() {
        return entries;
    }

    public String getStrStats() {
        return this.strStats;
    }

    public boolean haveSameStats(HdvEntry toAdd) {
        return this.getStrStats().equalsIgnoreCase(toAdd.getGameObject().parseStatsStringSansUserObvi())
                && toAdd.getGameObject().getTemplate().getType() != 85;//R�cup�re les stats de l'objet et compare avec ceux de la ligne
    }

    public void sort(byte index) {
        Collections.sort(this.getEntries().get(index));
    }

    public boolean addEntry(HdvEntry toAdd) {
        if (!haveSameStats(toAdd) && !isEmpty())
            return false;

        toAdd.setLineId(this.getLineId());
        byte index = (byte) (toAdd.getAmount(false) - 1);
        this.getEntries().get(index).add(toAdd);
        this.sort(index);
        return true;//Anonce que l'objet � �t� accept�
    }

    public boolean delEntry(HdvEntry toDel) {
        byte index = (byte) (toDel.getAmount(false) - 1);
        boolean toReturn = this.getEntries().get(index).remove(toDel);
        this.sort(index);
        return toReturn;
    }

    public HdvEntry doYouHave(int amount, int price) {
        int index = amount - 1;
        for (int i = 0; i < this.getEntries().get(index).size(); i++)
            if (this.getEntries().get(index).get(i).getPrice() == price)
                return this.getEntries().get(index).get(i);
        return null;
    }

    public int[] getFirsts() {
        int[] toReturn = new int[3];
        for (int i = 0; i < this.getEntries().size(); i++) {
            try {
                toReturn[i] = this.getEntries().get(i).get(0).getPrice();//R�cup�re le premier objet de chaque liste
            } catch (IndexOutOfBoundsException e) {
                // ok
                toReturn[i] = 0;
            }
        }
        return toReturn;
    }

    public ArrayList<HdvEntry> getAll() {
        int totalSize = this.getEntries().get(0).size()
                + this.getEntries().get(1).size()
                + this.getEntries().get(2).size();//Additionne le nombre d'objet de chaque quantit�
        ArrayList<HdvEntry> toReturn = new ArrayList<HdvEntry>(totalSize);

        for (int qte = 0; qte < this.getEntries().size(); qte++)
            //Boucler dans les quantit�
            toReturn.addAll(this.getEntries().get(qte));
        return toReturn;
    }

    public boolean isEmpty() {
        if (this.getEntries().isEmpty())
            return true;
        for (int i = 0; i < this.getEntries().size(); i++) {
            try {
                if (this.getEntries().get(i).isEmpty())
                    continue;
                if (this.getEntries().get(i).get(0) != null)//V�rifie s'il existe un objet dans chacune des 3 quantit�
                    return false;
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public String parseToEHl() {
        StringBuilder toReturn = new StringBuilder();
        int[] price = getFirsts();
        toReturn.append(this.getLineId()).append(";").append(this.getStrStats()).append(";").append((price[0] == 0 ? "" : price[0])).append(";").append((price[1] == 0 ? "" : price[1])).append(";").append((price[2] == 0 ? "" : price[2]));

        return toReturn.toString();
    }

    public String parseToEHm() {
        StringBuilder toReturn = new StringBuilder();

        int[] prix = getFirsts();
        toReturn.append(this.getLineId()).append("|").append(this.getTemplateId()).append("|").append(this.getStrStats()).append("|").append((prix[0] == 0 ? "" : prix[0])).append("|").append((prix[1] == 0 ? "" : prix[1])).append("|").append((prix[2] == 0 ? "" : prix[2]));

        return toReturn.toString();
    }
}
