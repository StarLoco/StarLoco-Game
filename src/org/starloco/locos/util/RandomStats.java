package org.starloco.locos.util;

import org.starloco.locos.common.Formulas;

import java.util.ArrayList;

public class RandomStats<Stats> {
	
	private ArrayList<Stats> randoms;
	
	public RandomStats() {
		this.randoms = new ArrayList<>();
	}
	
	public void add(int pct, Stats object) {
		for(int i = 0; i < pct; i++)
			this.randoms.add(object);
	}

    public int size() {
        return randoms.size();
    }
	
	public Stats get() {
		return this.randoms.get(Formulas.random.nextInt(this.randoms.size()));
	}
}