package com.lucasdnd.loneblock.gameplay.world;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.lucasdnd.loneblock.LoneBlock;
import com.lucasdnd.loneblock.Resources;
import com.lucasdnd.loneblock.TimeController;
import com.lucasdnd.loneblock.gameplay.items.Cotton;
import com.lucasdnd.loneblock.gameplay.items.Fruit;
import com.lucasdnd.loneblock.gameplay.items.Sapling;
import com.lucasdnd.loneblock.gameplay.items.Wood;

public class Tree extends MapObject {
	
	public static final int saveId = 7;

	protected final static Color color = Resources.Color.tree;
	protected final static Color saplingColor = Resources.Color.sapling;
	
	private int hitsToChopDown;	// How many times the user will press E to chop this Tree down
	
	private boolean isGrown;	// If it's a sapling or a tree
	private boolean hasFruits = false;
	private boolean isCotton = false; // Cotton or Fruit
	
	private int fruits, saplings;
	private int saplingsToGrow;
	
	// Growth clocks
	private long fruitTicks, maxFruitTicks;
	private long growthTicks, maxGrowthTicks;
	private long saplingTicks, maxSaplingTicks;
	
	public Tree(Disposer disposer, int x, int y) {
		super(disposer, x, y);
	}
	
	public Tree(Disposer disposer, int x, int y, boolean isGrown) {
		
		this(disposer, x, y);
		
		Random r = new Random();
		this.isGrown = isGrown;
		
		// Cotton plant
		if (r.nextInt(10) == 0) {
			isCotton = true;
		}
		
		if (isGrown == false) {
			// A sapling
			hitsToChopDown = 0;
			fruits = 0;
			calculateMaxGrowthTicks();
		} else {
			// A tree 
			refreshSaplings();
			refreshHits();
			hasFruits = r.nextInt(20) == 0;
			calculateMaxGatherableTicks();
			if (hasFruits) {
				refreshFruits();
			} else {
				fruits = 0;
			}
		}
	}
	
	private void refreshSaplings() {
		saplings = 1;
		saplingsToGrow = 1;
	}
	private void refreshFruits() {
		fruits = 2;
	}
	private void refreshHits() {
		hitsToChopDown = 3;
	}
	
	private void calculateMaxGatherableTicks() {
		Random r = new Random();
		long maxTicks = TimeController.ONE_DAY + (r.nextInt((int)TimeController.ONE_DAY));
		maxTicks *= TimeController.FPS;
		maxFruitTicks = maxTicks;
		maxSaplingTicks = maxTicks;
	}
	
	private void calculateMaxGrowthTicks() {
		Random r = new Random();
		maxGrowthTicks = TimeController.ONE_DAY * (r.nextInt(2) + 1) + (r.nextInt((int)TimeController.ONE_DAY));
		maxGrowthTicks *= TimeController.FPS;
	}
	
	public void update() {
		
		if (isGrown == false) {
			// Sapling growth
			growthTicks++;
			if (growthTicks >= maxGrowthTicks) {
				isGrown = true;
				hasFruits = new Random().nextInt(2) == 0;
				if (hasFruits) {
					refreshFruits();
				}
				refreshSaplings();
				refreshHits();
				calculateMaxGatherableTicks();
			}
		} else {
			// Fruit renewal
			if (hasFruits) {
				fruitTicks++;
				if (fruitTicks % maxFruitTicks == 0) {
					refreshFruits();
				}
			}
			// Sapling renewal
			if (saplingsToGrow > 0 && saplings <= 0) {
				saplingTicks++;
				if (saplingTicks % maxSaplingTicks == 0) {
					saplings++;
					saplingsToGrow--;
				}
			}
		}
	}
	
	public void render(ShapeRenderer sr, float x, float y) {
		
		if (isGrown) {
			
			// Tree
			sr.setColor(color);
			sr.rect(x, y, LoneBlock.blockSize, LoneBlock.blockSize);
			
			if (saplings > 0 && fruits > 0) {

				// Fruits and Sapling
					
				sr.setColor(saplingColor);
				sr.rect(x + LoneBlock.blockSize / 8f,
						y + LoneBlock.blockSize - LoneBlock.blockSize / 2.5f,
						LoneBlock.blockSize / 4f,
						LoneBlock.blockSize / 4f);
				
				if (isCotton) {
					sr.setColor(Resources.Color.cotton);
				} else {
					sr.setColor(Resources.Color.fruit);
				}
				sr.rect(x + (LoneBlock.blockSize / 8f * 5f),
						y + LoneBlock.blockSize / 8f,
						LoneBlock.blockSize / 4f,
						LoneBlock.blockSize / 4f);
			
			} else if (saplings > 0 && fruits <= 0) {
				
				// Sapling only
				
				sr.setColor(saplingColor);
				sr.rect(x + LoneBlock.blockSize - LoneBlock.blockSize / 2f - LoneBlock.blockSize / 8f,
						y + LoneBlock.blockSize - LoneBlock.blockSize / 2f - LoneBlock.blockSize / 8f,
						LoneBlock.blockSize / 4f,
						LoneBlock.blockSize / 4f);
			
			} else if (saplings <= 0 && fruits > 0) {
				
				// Fruits only
				if (isCotton) {
					sr.setColor(Resources.Color.cotton);
				} else {
					sr.setColor(Resources.Color.fruit);
				}
				sr.rect(x + LoneBlock.blockSize - LoneBlock.blockSize / 2f - LoneBlock.blockSize / 8f,
						y + LoneBlock.blockSize - LoneBlock.blockSize / 2f - LoneBlock.blockSize / 8f,
						LoneBlock.blockSize / 4f,
						LoneBlock.blockSize / 4f);
			}
			
		} else {
			
			// Growing sapling
			
			sr.setColor(saplingColor);
			sr.rect(x + LoneBlock.blockSize / 2f - LoneBlock.blockSize / 4f,
					y + LoneBlock.blockSize / 4f,
					LoneBlock.blockSize / 2f,
					LoneBlock.blockSize / 2f);
			
		}
	}
	
	@Override
	public Object performAction() {
		
		// Sapling
		
		if (isGrown == false) {
			Resources.get().randomLeavesSound().play(0.4f);
			disposer.dispose(this);
			return new Sapling();
		}
		
		// Tree
		
		if (saplings > 0) {
			
			saplings--;
			Resources.get().randomLeavesSound().play(0.4f);
			
			if (isGrown == false) {
				disposer.dispose(this);
			}
			
			return new Sapling();
		}
		
		if (fruits > 0) {
			fruits--;
			Resources.get().randomLeavesSound().play(0.4f);
			if (isCotton) {
				return new Cotton();
			} else {
				return new Fruit();
			}
			
		}
		
		if (hitsToChopDown > 0) {
			hitsToChopDown--;
			Resources.get().woodcuttingSound.play(0.3f);
		
			if (hitsToChopDown == 0) {
				disposer.dispose(this);
				return new Wood();
			}
		}
		
		return null;
	}

	@Override
	public int getSaveId() {
		return saveId;
	}

	public int getHitsToChopDown() {
		return hitsToChopDown;
	}

	public boolean isGrown() {
		return isGrown;
	}
	
	public boolean isCotton() {
		return isCotton;
	}

	public boolean hasFruits() {
		return hasFruits;
	}

	public int getFruits() {
		return fruits;
	}

	public int getSaplings() {
		return saplings;
	}

	public int getSaplingsToGrow() {
		return saplingsToGrow;
	}

	public long getFruitTicks() {
		return fruitTicks;
	}

	public long getGrowthTicks() {
		return growthTicks;
	}

	public long getSaplingTicks() {
		return saplingTicks;
	}

	public void setGrown(boolean isGrown) {
		this.isGrown = isGrown;
		
	}

	public void setHasFruits(boolean hasFruits) {
		this.hasFruits = hasFruits;
	}

	public void setSaplings(int saplings) {
		this.saplings = saplings;
	}

	public void setSaplingsToGrow(int saplingsToGrow) {
		this.saplingsToGrow = saplingsToGrow;
	}

	public void setFruits(int fruits) {
		this.fruits = fruits;
	}

	public void setHitsToChopDown(int hitsToChopDown) {
		this.hitsToChopDown = hitsToChopDown;
	}

	public void setFruitTicks(long fruitTicks) {
		this.fruitTicks = fruitTicks;
	}

	public void setGrowthTicks(long growthTicks) {
		this.growthTicks = growthTicks;
	}

	public void setSaplingTicks(long saplingTicks) {
		this.saplingTicks = saplingTicks;
	}

	public long getMaxFruitTicks() {
		return maxFruitTicks;
	}

	public void setMaxFruitTicks(long maxFruitTicks) {
		this.maxFruitTicks = maxFruitTicks;
	}

	public long getMaxGrowthTicks() {
		return maxGrowthTicks;
	}

	public void setMaxGrowthTicks(long maxGrowthTicks) {
		this.maxGrowthTicks = maxGrowthTicks;
	}

	public long getMaxSaplingTicks() {
		return maxSaplingTicks;
	}

	public void setMaxSaplingTicks(long maxSaplingTicks) {
		this.maxSaplingTicks = maxSaplingTicks;
	}
	
	public void setCotton(boolean cotton) {
		this.isCotton = cotton;
	}
}
