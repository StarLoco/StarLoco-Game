package org.starloco.locos.area.map.entity;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.entity.mount.Mount;
import org.starloco.locos.game.world.World;
import org.starloco.locos.guild.Guild;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MountPark {

    private int owner;
    private int size;
    private Guild guild;
    private GameMap map;
    private int cell = -1;
    private int price, priceBase;
    private int placeOfSpawn;
    private int maxObject;
    private int door;
    private ArrayList<Integer> cellOfObject = new ArrayList<>();
    private java.util.Map<Integer, Integer> cellAndObject = new HashMap<>();
    private java.util.Map<Integer, java.util.Map<Integer, Integer>> objDurab = new HashMap<>();
    private java.util.Map<Integer, java.util.Map<Integer, Integer>> breedingObject = new HashMap<>();
    private CopyOnWriteArrayList<Integer> raising = new CopyOnWriteArrayList<>();
    private ArrayList<Mount> etable = new ArrayList<>();

    public MountPark(GameMap map, int cellid, int size, int priceBase, int placeOfSpawn, int door, String cellOfObject, int maxObject) {
        this.map = map;
        this.cell = cellid;
        this.size = size;
        this.priceBase = priceBase;
        this.placeOfSpawn = placeOfSpawn;
        this.door = door;
        this.maxObject = maxObject;

        if(!cellOfObject.isEmpty()) {
            for(String cases : cellOfObject.split(";")) {
                int cellId = Integer.parseInt(cases);
                if(cellId > 0)
                    this.cellOfObject.add(cellId);
            }
        }
    }

    public void setData(int owner, int guild, int price, String raising, String objects, String objDurab, String etable) {
        this.owner = owner;
        this.guild = World.world.getGuild(guild);
        this.price = price;
        this.parseBreedObjects(objects);
        //chargement de la liste des dragodinde dans l'Ã©table
        for(String i: raising.split(";")) {
            try {
                Mount mount = World.world.getMountById(Integer.parseInt(i));
                if(mount != null) this.etable.add(mount);
            } catch (Exception ignored) {}
        }
        this.parseDurabilityObjects(objDurab);
        if(!etable.isEmpty())
            for(String dd: etable.split(";")) {
                try {
                    this.raising.add(Integer.parseInt(dd));
                    Mount mount = World.world.getMountById(Integer.parseInt(dd));
                    mount.setMapId(this.map.getId());
                    mount.setCellId(mount.getCellId());
                } catch(Exception ignored) {}
            }
        if(this.map != null)
            this.map.setMountPark(this);
        for(String firstCut: etable.split(";"))//PosseseurID,DragoID;PosseseurID2,DragoID2;PosseseurID,DragoID3
        {
            try	{
                String[] secondCut = firstCut.split(",");
                Mount DD = World.world.getMountById(Integer.parseInt(secondCut[1]));
                if(DD == null)
                    continue;
                this.raising.add(Integer.parseInt(secondCut[1]), Integer.parseInt(secondCut[0]));
            }catch(Exception ignored) {
            }
        }
    }

    public void setInfos(GameMap map, int cellid, int size, int placeOfSpawn, int door, String cellOfObject, int maxObject) {
        this.map = map;
        this.cell = cellid;
        this.size = size;
        this.placeOfSpawn = placeOfSpawn;
        this.door = door;
        this.maxObject = maxObject;

        if(!cellOfObject.isEmpty()) {
            for(String cases : cellOfObject.split(";")) {
                int cellId = Integer.parseInt(cases);
                if(cellId > 0)
                    this.cellOfObject.add(cellId);
            }
        }
    }

    public int getPriceBase() {
        return priceBase;
    }

    public void setDoor(int id) {
        this.door = id;
    }

    public int getMountcell() {
        return placeOfSpawn;
    }

    public void setMountCell(int id) {
        this.placeOfSpawn = id;
    }

    public void setCellObject(ArrayList<Integer> array) {
        this.cellOfObject = new ArrayList<>(array);
    }

    public void setInfos(int owner, GameMap map, int cell, int size, int guild, int price, int placeOfSpawn, String raising, int door, String cellOfObject, int maxObject, String objects, String objDurab, String etable)
    {

        this.owner = owner;
        this.size = size;
        this.guild = World.world.getGuild(guild);
        this.map.setMountPark(null);
        this.map = map;
        if(this.map != null)
            this.map.setMountPark(this);
        this.cell = cell;
        this.price = price;
        this.placeOfSpawn = placeOfSpawn;
        this.door = door;
        this.maxObject = maxObject;

        this.cellAndObject.clear();
        this.breedingObject.clear();
        this.parseBreedObjects(objects);
        //chargement de la liste des dragodinde dans l'Ã©table
        this.etable.clear();
        for(String i: etable.split(";"))
        {
            try {
                Mount DD = World.world.getMountById(Integer.parseInt(i));
                if(DD != null)
                    this.etable.add(DD);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.objDurab.clear();
        this.parseDurabilityObjects(objDurab);
        this.cellOfObject.clear();
        if(!cellOfObject.isEmpty())
        {
            for(String cases : cellOfObject.split(";"))
            {
                int cellId = Integer.parseInt(cases);
                if(cellId <= 0)
                    continue;
                this.cellOfObject.add(cellId);
            }
        }
        this.raising.clear();
        if(!raising.isEmpty())
        {
            String[] dragodinde = raising.split(";");
            for(String dd: dragodinde)
                this.raising.add(Integer.parseInt(dd));
        }
        this.raising.clear();
        for(String firstCut: raising.split(";"))//PosseseurID,DragoID;PosseseurID2,DragoID2;PosseseurID,DragoID3
        {
            try	{
                String[] secondCut = firstCut.split(",");
                Mount DD = World.world.getMountById(Integer.parseInt(secondCut[1]));
                if(DD == null)
                    continue;
                this.raising.add(Integer.parseInt(secondCut[1]), Integer.parseInt(secondCut[0]));
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public int getOwner() {
        return this.owner;
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public int getSize() {
        return this.size;
    }

    public Guild getGuild() {
        return this.guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public GameMap getMap() {
        return this.map;
    }

    public int getCell() {
        return this.cell;
    }

    public int getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPlaceOfSpawn() {
        return this.placeOfSpawn;
    }

    public int getMaxObject() {
        return this.maxObject;
    }

    public int getDoor() {
        return this.door;
    }

    public ArrayList<Integer> getCellOfObject() {
        return this.cellOfObject;
    }

    public boolean hasEtableFull(int id) {
        if(this.getOwner() == -1) {
            int i = 0;
            for(Mount mount : this.getEtable())
                if(mount.getOwner() == id)
                    i++;
            return i >= 100;
        } else {
            return this.getEtable().size() >= 100;
        }
    }

    public boolean hasEnclosFull(int id) {
        if(this.getOwner() == -1) {
            int i = 0;
            for(int mountId : this.getListOfRaising())
                if(mountId == id)
                    i++;
            return i >= this.getSize();
        } else {
            return this.getListOfRaising().size() >= this.getSize();
        }
    }

    public void addCellObject(int cell) {
        if(this.cellOfObject.contains(cell))
            return;
        if(cell <= 0)
            return;
        this.cellOfObject.add(cell);
    }

    private void parseBreedObjects(String objects) {
        if(!objects.isEmpty()) {
            for(String object: objects.split("\\|")) {
                String[] info = object.split(";");
                int cellId = Integer.parseInt(info[0]);
                int objectId = Integer.parseInt(info[1]);
                int proprietor = Integer.parseInt(info[2]);
                java.util.Map<Integer, Integer> other = new HashMap<>();
                other.put(objectId, proprietor);
                this.cellAndObject.put(cellId, objectId);
                this.breedingObject.put(cellId, other);
            }
        }
    }

    private void parseDurabilityObjects(String objects) {
        if(!objects.isEmpty()) {
            for(String object : objects.split("\\|")) {
                String[] info = object.split(";");
                int cellId = Integer.parseInt(info[0]);
                int durability = Integer.parseInt(info[1]);
                int durabilityMax = Integer.parseInt(info[2]);
                java.util.Map<Integer, Integer> inDurab = new HashMap<>();
                inDurab.put(durability, durabilityMax);
                this.objDurab.put(cellId, inDurab);
            }
        }
    }

    public String parseStringCellObject() {
        String cell = "";
        boolean first = true;
        for(Integer i: this.cellOfObject)
        {
            if(first)
                cell += i;
            else
                cell += ";"+i;
            first = false;
        }
        return cell;
    }

    public java.util.Map<Integer, Integer> getCellAndObject() {
        return this.cellAndObject;
    }

    public void addObject(int cell, int object, int owner, int durability, int durabilityMax) {
        if(this.breedingObject.containsKey(cell)) {
            this.breedingObject.remove(cell);
            this.cellAndObject.remove(cell);
        }
        java.util.Map<Integer, Integer> other = new HashMap<>();
        other.put(object, owner);

        java.util.Map<Integer, Integer> inDurab = new HashMap<>();
        inDurab.put(durability, durabilityMax);

        this.cellAndObject.put(cell, object);
        this.breedingObject.put(cell, other);
        this.objDurab.put(cell, inDurab);
    }

    public boolean delObject(int cell) {
        if(!this.breedingObject.containsKey(cell) && !this.objDurab.containsKey(cell))
            return false;
        this.objDurab.remove(cell);
        this.breedingObject.remove(cell);
        this.cellAndObject.remove(cell);
        return true;
    }

    public java.util.Map<Integer, java.util.Map<Integer, Integer>> getObjDurab() {
        return this.objDurab;
    }

    public java.util.Map<Integer, java.util.Map<Integer, Integer>> getObject() {
        return this.breedingObject;
    }

    public void addRaising(int id) {
        this.raising.add(id);
    }

    public void delRaising(int id) {
        if(this.raising.contains(id))
            this.raising.remove(this.raising.indexOf(id));
    }

    public CopyOnWriteArrayList<Integer> getListOfRaising() {
        return this.raising;
    }

    public ArrayList<Mount> getEtable() {
        return this.etable;
    }

    public Mount containsMountInList(List<Mount> mounts, Mount target) {
        if(target != null) {
            for (Mount mount : mounts) {
                if (mount != null && mount.getId() == target.getId())
                    return mount;
            }
        }
        return null;
    }

    public synchronized void startMoveMounts() {
        if(this.raising.size() > 0) {
            char[] directions = { 'b', 'd', 'f', 'h' };
            for(Integer id : this.raising) {
                Mount mount = World.world.getMountById(id);
                if(mount != null) {
                    mount.moveMountsAuto(directions[Formulas.getRandomValue(0, 3)], 3, false);
                }
            }
        }
    }

    public String getStringObject() {
        String str = "";
        boolean first = false;

        if(this.breedingObject.size() == 0)
            return str;

        for(java.util.Map.Entry<Integer, java.util.Map<Integer, Integer>> entry : this.breedingObject.entrySet()) {
            if(first) str += "|";
            str += entry.getKey();

            for(java.util.Map.Entry<Integer, Integer> entry2 : entry.getValue().entrySet())
                str += ";" + entry2.getKey() + ";" + entry2.getValue();
            first = true;
        }
        return str;
    }

    public String getStringObjDurab() {
        String str = "";
        boolean first = false;

        if(this.objDurab.size() == 0)
            return str;

        for(java.util.Map.Entry<Integer, java.util.Map<Integer, Integer>> entry : this.objDurab.entrySet()) {
            if(first)
                str += "|";
            str += entry.getKey();
            for(java.util.Map.Entry<Integer, Integer> entry2 : entry.getValue().entrySet())
            {
                str += ";" + entry2.getKey() + ";" + entry2.getValue();
            }
            first = true;
        }
        return str;
    }

    public String parseRaisingToString() {
        String str = "";
        boolean first = true;

        if(this.raising.size() == 0)
            return "";

        for(Integer id : this.raising) {
            if(!first) str += ";";
            str += id;
            first = false;
        }
        return str;
    }

    public String parseEtableToString() {
        String str = "";
        for(Mount mount : this.etable) {
            if(!str.equalsIgnoreCase(""))
                str += ";";
            str += mount.getId();
        }
        return str;
    }
}
