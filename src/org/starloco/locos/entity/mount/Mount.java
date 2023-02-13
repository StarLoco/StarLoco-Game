package org.starloco.locos.entity.mount;

import org.starloco.locos.area.map.GameMap;
import org.starloco.locos.area.map.entity.MountPark;
import org.starloco.locos.client.Player;
import org.starloco.locos.client.other.Stats;
import org.starloco.locos.common.Formulas;
import org.starloco.locos.common.PathFinding;
import org.starloco.locos.common.SocketManager;
import org.starloco.locos.database.DatabaseManager;
import org.starloco.locos.database.data.login.MountData;
import org.starloco.locos.game.scheduler.Updatable;
import org.starloco.locos.game.world.World;
import org.starloco.locos.kernel.Constant;
import org.starloco.locos.object.GameObject;
import org.starloco.locos.util.TimerWaiter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mount {

    public final static Updatable updatable = new Updatable(3600000) {
        @Override
        public void update() {
            if(this.verify()) {
                for (Mount mount : World.world.getMounts().values()) {
                    if (mount.getFatigue() <= 0) continue;
                    mount.setFatigue(mount.getFatigue() - 10);
                    if (mount.getFatigue() < 0) mount.setFatigue(0);
                }
            }
        }

        @Override
        public Object get() {
            return null;
        }
    };

	private int id, color, sex, size;
	private String name;
	private int level;
	private long exp;
	
	private int owner;
	private short mapId;
	private int cellId, orientation;
	
	private int fatigue, energy, reproduction;

	private int amour, endurance, maturity;
	private int state, savage;
	
	private String ancestors = "?,?,?,?,?,?,?,?,?,?,?,?,?,?";
	
	private long fecundatedDate = -1;
    private int couple;
	
	private Stats stats = new Stats();
	private java.util.Map<Integer, GameObject> objects = new HashMap<>();
	private List<Integer> capacitys = new ArrayList<>(2);

	public Mount(int color, int owner, boolean savage) {
		this.color = color;
		this.sex = Formulas.getRandomValue(0, 1);
		this.level = 1;
		this.exp = 0;
		this.name = "SansNom";
		this.fatigue = 0;
		this.energy = 0;
		this.reproduction = ((color == 75 || color == 88) ? -1 : 0);
		this.maturity = 0;
		this.state = 0;
		this.stats = Constant.getMountStats(this.color, this.level);
		this.ancestors = "?,?,?,?,?,?,?,?,?,?,?,?,?,?";
		this.size = 100;
		this.owner = owner;
		this.cellId = -1;
		this.mapId = -1;
		this.orientation = 1;
		this.savage = (savage ? 1 : 0);
		
		World.world.addMount(this);
        ((MountData) DatabaseManager.get(MountData.class)).insert(this);
	}

	public Mount(int color, Mount mother, Mount father) {
		this.color = color;
		this.sex = Formulas.getRandomValue(0, 1);
		this.level = 1;
		this.exp = 0;
		this.name = "SansNom";
		this.fatigue = 0;
		this.energy = 0;
		this.reproduction = 0;
		this.maturity = 0;
		this.state = Formulas.getRandomValue(-10000, 10000);
        this.stats = Constant.getMountStats(this.color, this.level);
		
		String[] fatherStr = father.ancestors.split(","), motherStr = mother.ancestors.split(",");
		String firstFather = fatherStr[0] + "," + fatherStr[1],
				firstMother = motherStr[0] + "," + motherStr[1],
				secondFather = fatherStr[2] + "," + fatherStr[3] + "," + fatherStr[4] + "," + fatherStr[5],
				secondMother = motherStr[2] + "," + motherStr[3] + "," + motherStr[4] + "," + motherStr[5];
		
		this.ancestors = father.getColor() + "," + mother.getColor() + "," + firstFather + "," + firstMother + "," + secondFather + "," + secondMother;
		
		if(Formulas.getRandomValue(0, 20) == 0)
			this.capacitys.add(Formulas.getRandomValue(1, 8));
		
		if(!father.getCapacitys().isEmpty() || !mother.getCapacitys().isEmpty()) {
			if(Formulas.getRandomValue(0, 10) == 0) {
				if(Formulas.getRandomValue(0, 1) == 0)
					if(!father.getCapacitys().isEmpty())
						this.capacitys.add(father.getCapacitys().get(Formulas.getRandomValue(0, father.getCapacitys().size() - 1)));
				else
					if(!mother.getCapacitys().isEmpty())
						this.capacitys.add(mother.getCapacitys().get(Formulas.getRandomValue(0, mother.getCapacitys().size() - 1)));
			}
		}

		this.cellId = -1;
		this.mapId = -1;
		this.owner = mother.getOwner();
		this.size = 50;
		this.orientation = 1;
		this.fecundatedDate = -1;
		this.couple = -1;
		this.savage = 0;
		World.world.addMount(this);
		((MountData) DatabaseManager.get(MountData.class)).insert(this);
	}
	
	public Mount(int id, int color, int sexe, int amour, int endurance, int level, long exp, String name, int fatigue,
			int energy, int reproduction, int maturity, int state, String objects, String ancestors, String capacitys, int size,
			int cellId, short mapId, int owner, int orientation, long fecundatedHour, int couple, int savage) {
		this.id = id;
		this.color = color;
		this.sex = sexe;
		this.amour = amour;
		this.endurance = endurance;
		this.level = level;
		this.exp = exp;
		this.name = name;
		this.fatigue = fatigue;
		this.energy = energy;
		this.reproduction = reproduction;
		this.maturity = maturity;
		this.state = state;
		this.ancestors = ancestors;
		this.stats = Constant.getMountStats(this.color, this.level);
		this.size = size;
		this.cellId = cellId;
		this.mapId = mapId;
		this.owner = owner;
		this.orientation = orientation;
		this.fecundatedDate = fecundatedHour;
		this.couple = couple;
		this.savage = savage;

		for(String str : objects.split(";")) {
			if(str.isEmpty()) continue;
			try {
				GameObject gameObject = World.world.getGameObject(Integer.parseInt(str));
				if(gameObject != null)
					this.objects.put(gameObject.getGuid(), gameObject);
			} catch (Exception e) {
			 	e.printStackTrace();
			}
		}

        for(String str : capacitys.split(",", 2))
            if(str != null)
                try { this.capacitys.add(Integer.parseInt(str)); }catch (Exception e) { e.printStackTrace(); }
    }

	private static int checkCanKen(MountPark park, Mount mount, int cellTest, int action) {
		if(park.getListOfRaising().size() > 1) {
			for(Integer arg : park.getListOfRaising()) {
				Mount mountArg = World.world.getMountById(arg);
				if(mountArg == null) continue;
                if(mountArg.getSex() !=	mount.getSex() && mountArg.isFecund() != 0 && mount.isFecund() != 0 && mountArg.getCellId() == cellTest) {
					if(mountArg.getReproduction() < 20 && mount.getReproduction() < 20 && !mountArg.isCastrated() && !mount.isCastrated()) {
						if(mountArg.getSex() == 1) {
							mountArg.fecundatedDate = System.currentTimeMillis();
							mountArg.setCouple(mount.id);
							mountArg.resAmor(7500);
							mountArg.resEndurance(7500);
							mount.resAmor(7500);
							mount.resEndurance(7500);
							mount.aumReproduction();
							if (mount.getSavage() == 1) {
								park.getListOfRaising().remove(mount.id);
								park.getMap().send("GM|-" + mount.getId());
								Player player = World.world.getPlayer(mount.getOwner());
								if (player != null && player.isOnline()) {
									player.send("Im0111;~" + park.getMap().getX() + "," + park.getMap().getY());
									SocketManager.GAME_SEND_Ee_PACKET(player, '-', String.valueOf(mount.getId()));
								}
								mount.setMapId((short) -1);
								mount.setCellId(-1);
								mount.setOwner(-1);
							}
						} else if(mount.getSex() == 1) {
							mount.fecundatedDate = System.currentTimeMillis();
							mount.setCouple(mountArg.getId());
							mount.resAmor(7500);
							mount.resEndurance(7500);
							mountArg.resAmor(7500);
							mountArg.resEndurance(7500);
							mountArg.aumReproduction();
							if (mountArg.getSavage() == 1) {
								park.getListOfRaising().remove(mountArg.id);
								park.getMap().send("GM|-" + mountArg.getId());
								Player player = World.world.getPlayer(mountArg.getOwner());
								if (player != null && player.isOnline()) {
									player.send("Im0111;~" + park.getMap().getX() + "," + park.getMap().getY());
									SocketManager.GAME_SEND_Ee_PACKET(player, '-', String.valueOf(mountArg.getId()));
								}
								mountArg.setMapId((short) -1);
								mountArg.setCellId(-1);
								mountArg.setOwner(-1);
							}
						}
						return 4;
					}
				}
			}
		}
		return action;
	}

	public synchronized void checkBaby(Player player, MountPark mp) {
        if(this.fecundatedDate == -1) return;
		int time = Generation.getTimeGestation(Constant.getGeneration(this.getColor()));
        int actualHours = (int) ((System.currentTimeMillis() - this.getFecundatedDate()) / 3600000) + 1;

        if(time < actualHours && actualHours < time + 24 * 7) {
            boolean coupleReprod = World.world.getMountById(this.getCouple()) != null && World.world.getMountById(this.getCouple()).getCapacitys().contains(3);
            boolean reproductrice = this.getCapacitys().contains(3) || coupleReprod;
			int offspring = 1 + (reproductrice ? 1 : 0), value = Formulas.getRandomValue(0, 16), max = 3 + (reproductrice ? 1 : 0);

			if(value >= 5 && value <= 10)
				offspring = (reproductrice ? 3 : 2);
			else if(value < 1)
				offspring = max;
			if(this.getCapacitys().contains(3) && coupleReprod)
				offspring *= 2;

			SocketManager.GAME_SEND_Im_PACKET(player, "1111;" + offspring);
			Mount father = World.world.getMountById(this.getCouple());
			for(int i = 0; i < offspring; i++) {
                int color = Constant.colorToEtable(player, this, (father == null ? this : father));
				Mount baby = new Mount(color, this, (father == null ? this : father));
                player.getCurMap().getMountPark().getEtable().add(baby);
			}

            this.aumReproduction();
            this.setFecundatedDate(-1);

            if (player.getCurMap().getMountPark().hasEtableFull(player.getId()))
				player.send("Im1105");
			if(father != null && father.getSavage() == 1) {
                ((MountData) DatabaseManager.get(MountData.class)).delete(father);
				World.world.removeMount(father.getId());
			}
			if(this.getSavage() == 1) {
                ((MountData) DatabaseManager.get(MountData.class)).delete(this);
				World.world.removeMount(this.getId());

				mp.delRaising(this.getId());
               	player.send("Im0112; " + this.getName());
			}
		} else if(actualHours >= time + 24 * 7) {
			SocketManager.GAME_SEND_Im_PACKET(player, "1112");
			this.aumReproduction();
			this.resAmor(7500);
			this.resEndurance(7500);
			this.setFecundatedDate(-1);
		}
	}

	//region getter/setter
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getSex() {
		return sex;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getExp() {
		return exp;
	}

	public int getOwner() {
		return owner;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public short getMapId() {
		return mapId;
	}

	public void setMapId(short mapId) {
		this.mapId = mapId;
	}

	public int getCellId() {
		return cellId;
	}

	public void setCellId(int cellId) {
		this.cellId = cellId;
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}

    public int getFatigue() {
        return fatigue;
    }

	private synchronized void setFatigue(int fatigue) {
        this.fatigue = fatigue;
    }

    public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public int getReproduction() {
		return reproduction;
	}

	public int getAmour() {
		return amour;
	}

	public int getEndurance() {
		return endurance;
	}

	public int getMaturity() {
		return maturity;
	}

	public int getState() {
		if(this.state > 10000) this.state = 10000;
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getSavage() {
		return savage;
	}

	public String getAncestors() {
		return ancestors;
	}

	public long getFecundatedDate() {
		return fecundatedDate;
	}

	private void setFecundatedDate(int fecundatedDate) {
		if(this.reproduction != -1)
			this.fecundatedDate = fecundatedDate;
	}

	public int getCouple() {
		return couple;
	}

	public void setCouple(int couple) {
		this.couple = couple;
	}

	public Stats getStats() {
		return stats;
	}
	//endregion getter/setter
	
	public java.util.Map<Integer, GameObject> getObjects() {
		return objects;
	}
	
	public List<Integer> getCapacitys() {
		return capacitys;
	}

	public String getStringColor(String color) {
		String secondColor = "";
		if(this.capacitys.contains(9))
			secondColor = "," + color;
		if(this.color == 75)
			secondColor = "," + Constant.getStringColorDragodinde(Formulas.getRandomValue(1, 87));
		return this.color + secondColor;
	}
	
	public int isMontable() {
        int mountable = ((this.maturity < this.getMaxMaturity() || this.fatigue == 240 || this.savage == 1) ? 0 : 1);
        if(mountable == 1 && this.size == 50)
            this.size = 100;
		return mountable;
	}

	private int isFecund() {
		if(this.reproduction != -1 && this.amour >= 7500 && this.endurance >= 7500 && this.maturity == this.getMaxMaturity() && (this.savage == 1 || (this.savage == 0 && this.level >= 5)))
			return 10;
		return 0;
	}
	
	public void setCastrated() {
		this.reproduction = -1;
	}

	private boolean isCastrated() {
		return this.reproduction == -1;
	}
	
	public int getActualPods() {
		int pods = 0;
		for(GameObject gameObject : this.objects.values())
			pods += gameObject.getTemplate().getPod() * gameObject.getQuantity();
		return pods;
	}
	
	public int getMaxPods() {
		return Generation.getPods(Constant.getGeneration(this.color), this.level);
	}
	
	public void addXp(long amount) {
		this.exp += amount;
		while(this.exp >= World.world.getExpLevel(this.level+1).mount && this.level < 100)
			this.addLvl();
	}

	private void addLvl() {
		this.level++;
		this.stats = Constant.getMountStats(this.color, this.level);
	}

	private void stateMale() {
		this.state -= 2;
		if(this.state < -10000) this.state = -10000;
	}

	private void stateFemale() {
		this.state += 2;
		if(this.state < -10000)	this.state = -10000;
	}

	private void setMaxEnergy() {
		this.energy = this.getMaxEnergy();
	}
	
	private int getMaxEnergy() {
		return Generation.getEnergy(Constant.getGeneration(this.color));
	}

	private void setMaxMaturity() {
		this.maturity = this.getMaxMaturity();
	}

	private int getMaxMaturity() {
		return Generation.getMaturity(Constant.getGeneration(this.color));
	}

	private void aumFatige() {
		this.fatigue += 1;
		if(this.fatigue > 240) this.fatigue = 240;
	}
	
	private void aumEndurance(int endurance) {
		this.endurance += (endurance / 100) * this.getBonusFatigue() /* * Generation.getLearningRate(Constant.getGeneration(this.color))*/;
		if(this.capacitys.contains(5)) this.endurance += 1;
		if(this.endurance > 10000) this.endurance = 10000;
	}
	
	private void aumMaturity(int Resist) {
		if(this.maturity < this.getMaxMaturity()) {
			this.maturity += (Resist / 100) * this.getBonusFatigue();
			if(this.capacitys.contains(7))
				this.maturity += Resist / 100;
			if(this.size < 100) {
                GameMap map = World.world.getMap(this.mapId);
				if((this.getMaxMaturity() / this.maturity) <= 1) {
					this.size = 100;
					SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(map, this.id);
					SocketManager.GAME_SEND_GM_MOUNT_TO_MAP(map, this);
					return;
				} else
				if(this.size < 75 && (this.getMaxMaturity() / this.maturity) == 2) {
					this.size = 75;
					SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(map, this.id);
					SocketManager.GAME_SEND_GM_MOUNT_TO_MAP(map, this);
					return;
				} else
				if(this.size < 50 && (this.getMaxMaturity() / this.maturity) == 3) {
					this.size = 50;
					SocketManager.GAME_SEND_ERASE_ON_MAP_TO_MAP(map, this.id);
					SocketManager.GAME_SEND_GM_MOUNT_TO_MAP(map, this);
					return;
				}
			}
		}
		if(this.maturity > this.getMaxMaturity()) this.setMaxMaturity();
	}
	
	private void aumAmor(int amour) {
		this.amour += (amour / 100) * this.getBonusFatigue();
		if(this.capacitys.contains(6)) this.amour += amour / 500;
		if(this.amour > 10000) this.amour = 10000;
	}
	
	private void aumState(int state) {
		this.state += (state / 100) * this.getBonusFatigue();
		if(this.state > 10000) this.state = 10000;
	}

	public void aumEnergy(int energy) {
		this.energy += (energy / 500) * this.getBonusFatigue();
		if(this.capacitys.contains(1)) this.energy += energy / 500;
		if(this.energy > this.getMaxEnergy()) this.setMaxEnergy();
	}
	
	private void aumReproduction() {
		if(this.reproduction != -1)	this.reproduction += 1;
	}
	
	private void resFatige() {
		this.fatigue -= 20;
		if(this.fatigue < 0) this.fatigue = 0;
	}
	
	private void resAmor(int amor) {
		this.amour -= amor * this.getBonusFatigue();
		if(this.amour < 0) this.amour = 0;
	}
	
	private void resEndurance(int endurance) {
		this.endurance -= endurance * this.getBonusFatigue();
		if(this.endurance < 0) this.endurance = 0;
	}

	private void resState(int state) {
		this.state -= (state / 100) * this.getBonusFatigue();
		if(this.state < -10000)	this.state = -10000;
	}

    public void setToMax() {
		this.addXp(World.world.getExpLevel(100).mount);
		this.amour = 10000;
        this.endurance = 10000;
		this.energy = this.getMaxEnergy();
        this.setMaxMaturity();
		((MountData) DatabaseManager.get(MountData.class)).update(this);
    }

	private double getBonusFatigue() {
		if(this.fatigue > 160 && this.fatigue <= 170)
			return 1.15;
		if(this.fatigue > 170 && this.fatigue <= 180)
			return 1.30;
		if(this.fatigue > 180 && this.fatigue <= 200)
			return 1.50;
		if(this.fatigue > 200 && this.fatigue <= 210)
			return 1.80;
		if(this.fatigue > 210 && this.fatigue <= 220)
			return 2.10;
		if(this.fatigue > 220 && this.fatigue <= 230)
			return 2.50;
		if(this.fatigue > 230 && this.fatigue <= 239)
			return 3.00;
		return fatigue == 240 ? 0 : 1;
	}
	
	public synchronized void moveMounts(Player player, int cellules, boolean remove) {
		int action = 0;
		if(player == null)
			return;
		if(player.getCurCell().getId() == this.cellId)
			return;
		String path = "";
        GameMap map = player.getCurMap();
		if(map.getMountPark() == null)
			return;
		MountPark MP = map.getMountPark();
		char dir;
		int azar = Formulas.getRandomValue(1, 10);
		dir = PathFinding.getDirEntreDosCeldas(map, this.cellId, player.getCurCell().getId());
		if(remove)
			dir = PathFinding.getOpositeDirection(dir);
		int cell = this.cellId;
		int cellTest = this.cellId;
		for(int i = 0; i < cellules; i++) 
		{
			cellTest = PathFinding.GetCaseIDFromDirrection(cellTest, dir, map.getMountPark().getMap(), false);
			if(map.getCase(cellTest) == null)
				return;
            if(MP.getCellAndObject().containsKey(cellTest) && (this.fatigue >= 240 || this.isFecund() == 10))
                break;
			if(MP.getCellAndObject().containsKey(cellTest))
			{
				int item = MP.getCellAndObject().get(cellTest);
				// liste objet elevage
				if(item == 7755 || item == 7756 || item == 7757 || item == 7758 || item == 7759 || item == 7760 || item == 7761 || item == 7762 || item == 7763 || item == 7764 || item == 7765 || item == 7766 || item == 7767 || item == 7768 || item == 7769 || item == 7770 || item == 7771 || item == 7772 || item == 7773 || item == 7774 || item == 7625 || item == 7626 || item == 7627 || item == 7629)
				{// Baffeur
					resState(GameMap.getObjResist(player, cellTest, item));
					if(this.sex == 0)
					{
						this.stateMale();
					}else 
					{
						this.stateFemale();
					}
					aumFatige();
				}else 	
				if(item == 7775 || item == 7776 || item == 7777 || item == 7778 || item == 7779 || item == 7780 || item == 7781 || item == 7782 || item == 7783 || item == 7784 || item == 7785 || item == 7786 || item == 7787 || item == 7788 || item == 7789 || item == 7790 || item == 7791 || item == 7792 || item == 7793 || item == 7794 || item == 7795 || item == 7796 || item == 7797 || item == 7798)
				{//Foudroyeur
					if(this.state < 0)
						this.aumEndurance(GameMap.getObjResist(player, cellTest, item));
					if(this.sex == 0)
					{
						this.stateMale();
					}else 
					{
						this.stateFemale();
					}
					aumFatige();
				}else
				if(item == 7606 || item == 7607 || item == 7608 || item == 7609 || item == 7610 || item == 7611 || item == 7612 || item == 7613 || item == 7614 || item == 7615 || item == 7616 || item == 7617 || item == 7618 || item == 7619 || item == 7620 || item == 7621 || item == 7683 || item == 7684 || item == 7685 || item == 7686 || item == 7687 || item == 7688 || item == 7689 || item == 7690) 
				{// Mangeoire
					resFatige();
					this.aumEnergy(GameMap.getObjResist(player, cellTest, item));
					if(this.sex == 0)
					{
						this.stateMale();
					}else 
					{
						this.stateFemale();
					}
				}else 
				if(item == 7634 || item == 7635 || item == 7636 || item == 7637 || item == 7691 || item == 7692 || item == 7693 || item == 7694 || item == 7695 || item == 7696 || item == 7697 || item == 7698 || item == 7699 || item == 7700) 
				{// Dragofesse
					if(this.state > 0)
						this.aumAmor(GameMap.getObjResist(player, cellTest, item));
					if(this.sex == 0)
					{
						this.stateMale();
					}else 
					{
						this.stateFemale();
					}
					aumFatige();
				}else 
				if(item == 7628 || item == 7622 || item == 7623 || item == 7624 || item == 7733 || item == 7734 || item == 7735 || item == 7736 || item == 7737 || item == 7738 || item == 7739 || item == 7740 || item == 7741 || item == 7742 || item == 7743 || item == 7744 || item == 7745 || item == 7746)
				{// Caresseur
					aumState(GameMap.getObjResist(player, cellTest, item));
					if(this.sex == 0)	
					{
						this.stateMale();
					}else 
					{
						this.stateFemale();
					}
					aumFatige();
				}else 
				if(item == 7590 || item == 7591 || item == 7592 || item == 7593 || item == 7594 || item == 7595 || item == 7596 || item == 7597 || item == 7598 || item == 7599 || item == 7600 || item == 7601 || item == 7602 || item == 7603 || item == 7604 || item == 7605 || item == 7673 || item == 7674 || item == 7675 || item == 7676 || item == 7677 || item == 7678 || item == 7679 || item == 7682)
				{// Abreuvoir
					if(this.state <= 2000 && this.state >= -2000)
						this.aumMaturity(GameMap.getObjResist(player, cellTest, item));
					if(this.sex == 0)	
					{
						this.stateMale();
					}else 
					{
						this.stateFemale();
					}
					aumFatige();
				}
				if(item != 7590 && item != 7591 && item != 7592 && item != 7593 && item != 7594 && item != 7595 && item != 7596 && item != 7597 
				&& item != 7598 && item != 7599 && item != 7600 && item != 7601 && item != 7602 && item != 7603 && item != 7604 && item != 7605 
				&& item != 7673 && item != 7674 && item != 7675 && item != 7676 && item != 7677 && item != 7678 && item != 7679 && item != 7682 
				&& item != 7606 && item != 7607 && item != 7608 && item != 7609 && item != 7610 && item != 7611 && item != 7612 && item != 7613 
				&& item != 7614 && item != 7615 && item != 7616 && item != 7617 && item != 7618 && item != 7619 && item != 7620 && item != 7621 
				&& item != 7683 && item != 7684 && item != 7685 && item != 7686 && item != 7687 && item != 7688 && item != 7689 && item != 7690 
				&& item != 7755 && item != 7756 && item != 7757 && item != 7758 && item != 7759 && item != 7760 && item != 7761 && item != 7762 
				&& item != 7763 && item != 7764 && item != 7765 && item != 7766 && item != 7767 && item != 7768 && item != 7769 && item != 7770 
				&& item != 7771 && item != 7772 && item != 7773 && item != 7774 && item != 7625 && item != 7626 && item != 7627 && item != 7629 
				&& item != 7628 && item != 7622 && item != 7623 && item != 7624 && item != 7733 && item != 7734 && item != 7735 && item != 7736 
				&& item != 7737 && item != 7738 && item != 7739 && item != 7740 && item != 7741 && item != 7742 && item != 7743 && item != 7744 
				&& item != 7745 && item != 7746 && item != 7634 && item != 7635 && item != 7636 && item != 7637 && item != 7691 && item != 7692 
				&& item != 7693 && item != 7694 && item != 7695 && item != 7696 && item != 7697 && item != 7698 && item != 7699 && item != 7700 
				&& item != 7775 && item != 7776 && item != 7777 && item != 7778 && item != 7779 && item != 7780 && item != 7781 && item != 7782 
				&& item != 7783 && item != 7784 && item != 7785 && item != 7786 && item != 7787 && item != 7788 && item != 7789 && item != 7790 
				&& item != 7791 && item != 7792 && item != 7793 && item != 7794 && item != 7795 && item != 7796 && item != 7797 && item != 7798)
				{

				}
				break;
			}
			if(map.getCase(cellTest).isWalkable(true) && MP.getDoor() != cellTest && !map.cellSide(cell, cellTest))
			{
				cell = cellTest;
				path += dir + World.world.getCryptManager().cellID_To_Code(cell);
			}else
			{
				break;
			}
		}
		if(cell == this.cellId) 
		{
			this.orientation = World.world.getCryptManager().getIntByHashedValue(dir);
			SocketManager.GAME_SEND_eD_PACKET_TO_MAP(map, this.id, this.orientation);
			SocketManager.SEND_GDE_FRAME_OBJECT_EXTERNAL(map, cellTest + ";4");
			SocketManager.GAME_SEND_eUK_PACKET_TO_MAP(map, this.id, action);
			return;
		}
		if(azar == 5)
			action = 8;
		int nb = Mount.checkCanKen(MP, this, cellTest, action);
        if(nb == 4) action = 4;
		SocketManager.GAME_SEND_GA_PACKET_TO_MAP(map, "" + 0, 1, this.id + "", "a" + World.world.getCryptManager().cellID_To_Code(this.cellId) + path);
		this.cellId = cell;
		this.orientation = World.world.getCryptManager().getIntByHashedValue(dir);
		int ID = this.id;

        final int finalCell = cellTest, finalAction = action;

		TimerWaiter.addNext(() -> {
            SocketManager.SEND_GDE_FRAME_OBJECT_EXTERNAL(map, finalCell + ";4");
            if(finalAction != 0)
                SocketManager.GAME_SEND_eUK_PACKET_TO_MAP(map, ID, finalAction);
        }, action == 4 ? 2500 : 1500);
	}
	
	public synchronized void moveMountsAuto(char direction, int cellules, boolean remove) 
	{
		int action = 0;
		String path = "";
        GameMap map = World.world.getMap(this.mapId);
		if(map == null)
			return;
		if(map.getMountPark() == null)
			return;
		MountPark MP = map.getMountPark();
		char dir = direction;
		int random = Formulas.getRandomValue(1, 10);
		int cell = this.cellId;
		int cellTest = this.cellId;
		for(int i = 0; i < cellules; i++) 
		{
			cellTest = PathFinding.getCellArroundByDir(cellTest, dir, World.world.getMap(this.mapId));
			if(map.getCase(cellTest) == null)
				return;
			if(MP.getCellAndObject().containsKey(cellTest) && (this.fatigue >= 240 || this.isFecund() == 10))
				break;
			if(MP.getCellAndObject().containsKey(cellTest) && this.fatigue < 240) 
			{
				int item = MP.getCellAndObject().get(cellTest);
				if(item == 7755 || item == 7756 || item == 7757 || item == 7758 || item == 7759 || item == 7760 || item == 7761 || item == 7762 || item == 7763 || item == 7764 || item == 7765 || item == 7766 || item == 7767 || item == 7768 || item == 7769 || item == 7770 || item == 7771 || item == 7772 || item == 7773 || item == 7774 || item == 7625 || item == 7626 || item == 7627 || item == 7629)// 
				{	
					resState(GameMap.getObjResist(MP, cellTest, item));
					if(this.sex == 0)	
					{
						this.stateMale();
					}else 
					{
						this.stateFemale();
					}
					aumFatige();
				}else
				if(item == 7775 || item == 7776 || item == 7777 || item == 7778 || item == 7779 || item == 7780 || item == 7781 || item == 7782 || item == 7783 || item == 7784 || item == 7785 || item == 7786 || item == 7787 || item == 7788 || item == 7789 || item == 7790 || item == 7791 || item == 7792 || item == 7793 || item == 7794 || item == 7795 || item == 7796 || item == 7797 || item == 7798)
				{// Baffeur
					if(this.state < 0)
						this.aumEndurance(GameMap.getObjResist(MP, cellTest, item));
					if(this.sex == 0)
					{
						this.stateMale();
					}else 
					{
						this.stateFemale();
					}
					aumFatige();
				}else
				if(item == 7606 || item == 7607 || item == 7608 || item == 7609 || item == 7610 || item == 7611 || item == 7612 || item == 7613 || item == 7614 || item == 7615 || item == 7616 || item == 7617 || item == 7618 || item == 7619 || item == 7620 || item == 7621 || item == 7683 || item == 7684 || item == 7685 || item == 7686 || item == 7687 || item == 7688 || item == 7689 || item == 7690)
				{// Foudroyeur
					aumFatige();
					this.aumEnergy(GameMap.getObjResist(MP, cellTest, item));
					if(this.sex == 0)	
					{
						this.stateMale();
					}else
					{
						this.stateFemale();
					}
				}else 
				if(item == 7634 || item == 7635 || item == 7636 || item == 7637 || item == 7691 || item == 7692 || item == 7693 || item == 7694 || item == 7695 || item == 7696 || item == 7697 || item == 7698 || item == 7699 || item == 7700)
				{// Mangeoire
					if(this.state > 0)
						this.aumAmor(GameMap.getObjResist(MP, cellTest, item));
					if(this.sex == 0)	
					{
						this.stateMale();
					}else 
					{
						this.stateFemale();
					}
					aumFatige();
				}else 
				if(item == 7628 || item == 7622 || item == 7623 || item == 7624 || item == 7733 || item == 7734 || item == 7735 || item == 7736 || item == 7737 || item == 7738 || item == 7739 || item == 7740 || item == 7741 || item == 7742 || item == 7743 || item == 7744 || item == 7745 || item == 7746)
				{// Dragofesse
					aumState(GameMap.getObjResist(MP, cellTest, item));
					if(this.sex == 0)	
					{
						this.stateMale();
					}else 
					{
						this.stateFemale();
					}
					aumFatige();
				}else 
				if(item == 7590 || item == 7591 || item == 7592 || item == 7593 || item == 7594 || item == 7595 || item == 7596 || item == 7597 || item == 7598 || item == 7599 || item == 7600 || item == 7601 || item == 7602 || item == 7603 || item == 7604 || item == 7605 || item == 7673 || item == 7674 || item == 7675 || item == 7676 || item == 7677 || item == 7678 || item == 7679 || item == 7682)		
				{// Abreuvoir
					if(this.state <= 2000 && this.state >= -2000)
						this.aumMaturity(GameMap.getObjResist(MP, cellTest, item));
					if(this.sex == 0)	
					{
						this.stateMale();
					}else 
					{
						this.stateFemale();
					}
					aumFatige();
				}
				break;
			}
			if(map.getCase(cellTest).isWalkable(false) && MP.getDoor() != cellTest && !map.cellSide(cell, cellTest)) 
			{
				cell = cellTest;
				path += dir + World.world.getCryptManager().cellID_To_Code(cell);
			}else
			{
				break;
			}
		}
		if(cell == this.cellId) 
		{
			this.orientation = World.world.getCryptManager().getIntByHashedValue(dir);
			SocketManager.GAME_SEND_eD_PACKET_TO_MAP(map, this.id, this.orientation);
			SocketManager.SEND_GDE_FRAME_OBJECT_EXTERNAL(map, cellTest + ";4");
			SocketManager.GAME_SEND_eUK_PACKET_TO_MAP(map, this.id, action);
			return;
		}
		if(random == 5)
			action = 8;
		int id = this.id;
		action = Mount.checkCanKen(MP, this, cellTest, action);		
		SocketManager.GAME_SEND_GA_ACTION_TO_MAP(map, "" + 0, 1, this.id + "", "a" + World.world.getCryptManager().cellID_To_Code(this.cellId) + path);
		this.cellId = cell;
		this.orientation = World.world.getCryptManager().getIntByHashedValue(dir);

        final int finalCell = cellTest, finalAction = action;

        if(map.getPlayers().size() > 0) {
			Player player = null;
			for(Player target : map.getPlayers())
				player = target;
			if (player != null)
				TimerWaiter.addNext(() -> {
					SocketManager.SEND_GDE_FRAME_OBJECT_EXTERNAL(map, finalCell + ";4");
					if (finalAction != 0) SocketManager.GAME_SEND_eUK_PACKET_TO_MAP(map, id, finalAction);
				}, 2000);
        }
    }
	
	public void addObject(int guid, int qua, Player P) {
		if(qua <= 0)
			return;
		GameObject playerObj = World.world.getGameObject(guid);
		if(playerObj == null) return;
		//Si le joueur n'a pas l'item dans son sac ...
		if(P.getItems().get(guid) == null)
		{
			return;
		}
		
		String str = "";
		
		//Si c'est un item equipe ...
		if(playerObj.getPosition() != Constant.ITEM_POS_NO_EQUIPED)return;
		
		GameObject TrunkObj = this.getSimilarObject(playerObj);
		int newQua = playerObj.getQuantity() - qua;
		if(TrunkObj == null)//S'il n'y pas d'item du meme Template
		{
			//S'il ne reste pas d'item dans le sac
			if(newQua <= 0)
			{
				//On enleve l'objet du sac du joueur
				P.removeItem(playerObj.getGuid());
				//On met l'objet du sac dans le coffre, avec la meme quantite
				this.objects.put(playerObj.getGuid() ,playerObj);
				str = "O+"+playerObj.getGuid()+"|"+playerObj.getQuantity()+"|"+playerObj.getTemplate().getId()+"|"+playerObj.parseStatsString();
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(P, guid);
			}else//S'il reste des objets au joueur
			{
				//on modifie la quantite d'item du sac
				playerObj.setQuantity(newQua);
				//On ajoute l'objet au coffre et au monde
				TrunkObj = playerObj.getClone(qua, true);
				World.world.addGameObject(TrunkObj);
				this.objects.put(TrunkObj.getGuid() ,TrunkObj);
				
				//Envoie des packets
				str = "O+"+TrunkObj.getGuid()+"|"+TrunkObj.getQuantity()+"|"+TrunkObj.getTemplate().getId()+"|"+TrunkObj.parseStatsString();
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P, playerObj);
			}
		}else // S'il y avait un item du meme template
		{
			//S'il ne reste pas d'item dans le sac
			if(newQua <= 0)
			{
				//On enleve l'objet du sac du joueur
				P.removeItem(playerObj.getGuid());
				//On enleve l'objet du monde
				World.world.removeGameObject(playerObj.getGuid());
				//On ajoute la quantite a l'objet dans le coffre
				TrunkObj.setQuantity(TrunkObj.getQuantity() + playerObj.getQuantity());
				//On envoie l'ajout au coffre de l'objet
			    str = "O+"+TrunkObj.getGuid()+"|"+TrunkObj.getQuantity()+"|"+TrunkObj.getTemplate().getId()+"|"+TrunkObj.parseStatsString();
				//On envoie la supression de l'objet du sac au joueur
				SocketManager.GAME_SEND_REMOVE_ITEM_PACKET(P, guid);
			}else //S'il restait des objets
			{
				//On modifie la quantite d'item du sac
				playerObj.setQuantity(newQua);
				TrunkObj.setQuantity(TrunkObj.getQuantity() + qua);
				str = "O+"+TrunkObj.getGuid()+"|"+TrunkObj.getQuantity()+"|"+TrunkObj.getTemplate().getId()+"|"+TrunkObj.parseStatsString();
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P, playerObj);
			}
		}

		SocketManager.GAME_SEND_Ew_PACKET(P, this.getActualPods(), this.getMaxPods());
		SocketManager.GAME_SEND_EL_MOUNT_PACKET(P, this);
	}
	
	public void removeObject(int guid, int qua, Player P) {
		if(qua <= 0)
			return;
		GameObject TrunkObj = World.world.getGameObject(guid);
		//Si le joueur n'a pas l'item dans son coffre
		if(this.objects.get(guid) == null)
		{
			return;
		}
		
		GameObject playerObj = P.getSimilarItem(TrunkObj);
		String str = "";
		int newQua = TrunkObj.getQuantity() - qua;
		
		if(playerObj == null)//Si le joueur n'avait aucun item similaire
		{
			//S'il ne reste rien dans le coffre
			if(newQua <= 0)
			{
				//On retire l'item du coffre
				this.objects.remove(guid);
				//On l'ajoute au joueur
				P.getItems().put(guid, TrunkObj);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OAKO_PACKET(P,TrunkObj);
				SocketManager.GAME_SEND_Ew_PACKET(P, this.getActualPods(), this.getMaxPods());
				str = "O-"+guid;
			}else //S'il reste des objets dans le coffre
			{
				//On cree une copy de l'item dans le coffre
				playerObj = TrunkObj.getClone(qua, true);
				//On l'ajoute au monde
				World.world.addGameObject(playerObj);
				//On retire X objet du coffre
				TrunkObj.setQuantity(newQua);
				//On l'ajoute au joueur
				P.getItems().put(playerObj.getGuid(), playerObj);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OAKO_PACKET(P,playerObj);
				SocketManager.GAME_SEND_Ew_PACKET(P, this.getActualPods(), this.getMaxPods());
				str = "O+"+TrunkObj.getGuid()+"|"+TrunkObj.getQuantity()+"|"+TrunkObj.getTemplate().getId()+"|"+TrunkObj.parseStatsString();
			}
		}else // Le joueur avait deja� un item similaire
		{
			//S'il ne reste rien dans le coffre
			if(newQua <= 0)
			{
				//On retire l'item du coffre
				this.objects.remove(TrunkObj.getGuid());
				World.world.removeGameObject(TrunkObj.getGuid());
				//On Modifie la quantite de l'item du sac du joueur
				playerObj.setQuantity(playerObj.getQuantity() + TrunkObj.getQuantity());
				
				//On envoie les packets
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P, playerObj);
				SocketManager.GAME_SEND_Ew_PACKET(P, this.getActualPods(), this.getMaxPods());
				str = "O-"+guid;
			}else//S'il reste des objets dans le coffre
			{
				//On retire X objet du coffre
				TrunkObj.setQuantity(newQua);
				//On ajoute X objets au joueurs
				playerObj.setQuantity(playerObj.getQuantity() + qua);
				
				//On envoie les packets
				SocketManager.GAME_SEND_OBJECT_QUANTITY_PACKET(P,playerObj);
				SocketManager.GAME_SEND_Ew_PACKET(P, this.getActualPods(), this.getMaxPods());
				str = "O+"+TrunkObj.getGuid()+"|"+TrunkObj.getQuantity()+"|"+TrunkObj.getTemplate().getId()+"|"+TrunkObj.parseStatsString();

			}
		}

		SocketManager.GAME_SEND_EsK_PACKET(P, str);
	}
	
	private GameObject getSimilarObject(GameObject obj) {
		for(GameObject gameObject : this.objects.values())
			if(gameObject.getTemplate().getType() != 85)
				if(gameObject.getTemplate().getId() == obj.getTemplate().getId() && World.world.getConditionManager().stackIfSimilar(gameObject, obj, true) && gameObject.getStats().isSameStats(obj.getStats()))
					return gameObject;
		return null;
	}
	
	private String convertStatsToString() {
		StringBuilder stats = new StringBuilder();
		for(java.util.Map.Entry<Integer, Integer> entry : this.stats.getEffects().entrySet()) {
			if(entry.getValue() <= 0)
				continue;
			if(stats.length() > 0)
				stats.append(",");
			stats.append(Integer.toHexString(entry.getKey())).append("#").append(Integer.toHexString(entry.getValue())).append("#0#0");
		}
		return stats.toString();
	}
		
	public String parse() {
        return String.valueOf(this.id) + ":" + this.color + ":" + this.ancestors + ":" + ",," + this.parseCapacitysToString() + ":" + this.name + ":" + this.sex + ":" + this.parseExp() + ":" + this.level + ":" + this.isMontable() + ":" + this.getMaxPods() + ":" + this.savage + ":" + this.endurance + ",10000:" + this.maturity + "," + this.getMaxMaturity() + ":" + this.energy + "," + this.getMaxEnergy() + ":" + this.state + ",-10000,10000:" + this.amour + ",10000:" + (this.fecundatedDate == -1 ? this.fecundatedDate : ((System.currentTimeMillis() - this.fecundatedDate) / 3600000) + 1) + ":" + this.isFecund() + ":" + this.convertStatsToString() + ":" + this.fatigue + ",240:" + this.reproduction + ",20:";
	}
	
	public String parseToGM() {
		StringBuilder str = new StringBuilder();
		str.append("GM|+");
		str.append(this.cellId).append(";");
		str.append(this.orientation).append(";0;").append(this.id).append(";").append(this.name).append(";-9;");
		str.append((this.color == 88) ? 7005 : 7002);
		str.append("^").append(this.size).append(";");
		if(World.world.getPlayer(this.owner) == null)
			str.append("Sans Maitre");
		else
			str.append(World.world.getPlayer(this.owner).getName());
		str.append(";").append(this.level).append(";").append(this.color);
		return str.toString();
	}
	
	public String parseToMountObjects() {
		StringBuilder packet = new StringBuilder();
		for(GameObject obj : this.objects.values())
			packet.append("O").append(obj.parseItem()).append(";");
		return packet.toString();
	}
	
	public String parseObjectsToString() {
		String str = "";
		for(GameObject gameObject : this.objects.values())
			str += (str.isEmpty() ? "" : ";") + gameObject.getGuid();
		return str;
	}
	
	public String parseCapacitysToString() {  
		String str = "";
		for(int capacity : this.capacitys)
			str += (str.isEmpty() ? "" : ",") + capacity;
		return (str.isEmpty() ? "0" : str);
	}
	
	private String parseExp() {
		return this.exp + "," + World.world.getExpLevel(this.level).mount + "," + World.world.getExpLevel(this.level + 1).mount;
	}
}
