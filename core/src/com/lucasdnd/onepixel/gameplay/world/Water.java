package com.lucasdnd.onepixel.gameplay.world;

import com.badlogic.gdx.graphics.Color;

public class Water extends MapObject {
	
	int depth;
	
	public Water(Disposer disposer, int x, int y, int z, int depth) {
		super(disposer, x, y, z);
		this.depth = depth;
		if (depth > 10) {
			color = new Color(0.15f, 0.29f, 0.92f, 1f);
		} else {
			color = new Color(0.11f, 0.19f, 0.74f, 1f);
		}
	}
	
	@Override
	public Object performAction() {
		return null;
	}

	@Override
	public Object actionCallback() {
		return null;
	}
	
}
