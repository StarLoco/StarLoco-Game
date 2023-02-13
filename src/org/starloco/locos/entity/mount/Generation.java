package org.starloco.locos.entity.mount;

public class Generation {
		
	static int getPods(int generation, int level) {
		return (100 + 50 * generation - 1) + (5 + 5 * (generation / 2)) * level;
	}
	
	public static int getEnergy(int generation) {
		return (1000 + 100 * generation - 1) + (10 + 5 * (generation / 2));
	}
	
	static int getMaturity(int generation) {
		return generation * 1000;
	}
	
	static short getTimeGestation(int generation) {
		return (short) ((36 + 12 * generation) / 2);
	}
	
	public static short getLearningRate(int generation) {
		switch(generation) {
		default:
			return (short) 100;
		case 2:
		case 3:
		case 4:
			return (short) 80;
		case 5:
		case 6:
			return (short) 60;
		case 7:
		case 8:
			return (short) 40;
		case 9:
		case 10:
			return (short) 20;
		}
	}	
}