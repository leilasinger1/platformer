package gamelogic.level;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import gameengine.PhysicsObject;
import gameengine.graphics.Camera;
import gameengine.loaders.Mapdata;
import gameengine.loaders.Tileset;
import gamelogic.GameResources;
import gamelogic.Main;
import gamelogic.enemies.Enemy;
import gamelogic.player.Player;
import gamelogic.tiledMap.Map;
import gamelogic.tiles.Flag;
import gamelogic.tiles.Flower;
import gamelogic.tiles.Gas;
import gamelogic.tiles.SolidTile;
import gamelogic.tiles.Spikes;
import gamelogic.tiles.Tile;
import gamelogic.tiles.Water;

public class Level {

	private LevelData leveldata;
	private Map map;
	private Enemy[] enemies;
	public static Player player;
	private Camera camera;

	private boolean active;
	private boolean playerDead;
	private boolean playerWin;

	private ArrayList<Enemy> enemiesList = new ArrayList<>();
	private ArrayList<Flower> flowers = new ArrayList<>();
	private ArrayList<Water> waters  = new ArrayList<>();
	private ArrayList<Gas> gasses = new ArrayList<>();

	private List<PlayerDieListener> dieListeners = new ArrayList<>();
	private List<PlayerWinListener> winListeners = new ArrayList<>();

	private Mapdata mapdata;
	private int width;
	private int height;
	private int tileSize;
	private Tileset tileset;
	public static float GRAVITY = 70;

	public Level(LevelData leveldata) {
		this.leveldata = leveldata;
		mapdata = leveldata.getMapdata();
		width = mapdata.getWidth();
		height = mapdata.getHeight();
		tileSize = mapdata.getTileSize();
		restartLevel();
	}

	public LevelData getLevelData() {
		return leveldata;
	}

	public void restartLevel() {
		int[][] values = mapdata.getValues();
		Tile[][] tiles = new Tile[width][height];
		waters = new ArrayList();
		gasses = new ArrayList();

		for (int x = 0; x < width; x++) {
			int xPosition = x;
			for (int y = 0; y < height; y++) {
				int yPosition = y;

				tileset = GameResources.tileset;

				tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this);
				if (values[x][y] == 0)
					tiles[x][y] = new Tile(xPosition, yPosition, tileSize, null, false, this); // Air
				else if (values[x][y] == 1)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid"), this);

				else if (values[x][y] == 2)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_DOWNWARDS, this);
				else if (values[x][y] == 3)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.HORIZONTAL_UPWARDS, this);
				else if (values[x][y] == 4)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_LEFTWARDS, this);
				else if (values[x][y] == 5)
					tiles[x][y] = new Spikes(xPosition, yPosition, tileSize, Spikes.VERTICAL_RIGHTWARDS, this);
				else if (values[x][y] == 6)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Dirt"), this);
				else if (values[x][y] == 7)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Grass"), this);
				else if (values[x][y] == 8)
					enemiesList.add(new Enemy(xPosition * tileSize, yPosition * tileSize, this)); // TODO: objects vs
																									// tiles
				else if (values[x][y] == 9)
					tiles[x][y] = new Flag(xPosition, yPosition, tileSize, tileset.getImage("Flag"), this);
				else if (values[x][y] == 10) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower1"), this, 1);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 11) {
					tiles[x][y] = new Flower(xPosition, yPosition, tileSize, tileset.getImage("Flower2"), this, 2);
					flowers.add((Flower) tiles[x][y]);
				} else if (values[x][y] == 12)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_down"), this);
				else if (values[x][y] == 13)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_up"), this);
				else if (values[x][y] == 14)
					tiles[x][y] = new SolidTile(xPosition, yPosition, tileSize, tileset.getImage("Solid_middle"), this);
				else if (values[x][y] == 15)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasOne"), this, 1);
				else if (values[x][y] == 16)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasTwo"), this, 2);
				else if (values[x][y] == 17)
					tiles[x][y] = new Gas(xPosition, yPosition, tileSize, tileset.getImage("GasThree"), this, 3);
				else if (values[x][y] == 18) {
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Falling_water"), this, 0);
					waters.add((Water)tiles[x][y]);
				}
				else if (values[x][y] == 19){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Full_water"), this, 3);
					waters.add((Water)tiles[x][y]);
				}
				else if (values[x][y] == 20){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Half_water"), this, 2);
					waters.add((Water)tiles[x][y]);
				}
				else if (values[x][y] == 21){
					tiles[x][y] = new Water(xPosition, yPosition, tileSize, tileset.getImage("Quarter_water"), this, 1);
					waters.add((Water)tiles[x][y]);
				}
			}

		}
		enemies = new Enemy[enemiesList.size()];
		map = new Map(width, height, tileSize, tiles);
		camera = new Camera(Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT, 0, map.getFullWidth(), map.getFullHeight());
		for (int i = 0; i < enemiesList.size(); i++) {
			enemies[i] = new Enemy(enemiesList.get(i).getX(), enemiesList.get(i).getY(), this);
		}
		player = new Player(leveldata.getPlayerX() * map.getTileSize(), leveldata.getPlayerY() * map.getTileSize(),
				this);
		camera.setFocusedObject(player);

		active = true;
		playerDead = false;
		playerWin = false;
	}

	public void onPlayerDeath() {
		active = false;
		playerDead = true;
		throwPlayerDieEvent();
	}

	public void onPlayerWin() {
		active = false;
		playerWin = true;
		throwPlayerWinEvent();
	}

	public void update(float tslf) {
		if (active) {
			// Update the player
			player.update(tslf);

			// Player death
			if (map.getFullHeight() + 100 < player.getY())
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.BOT] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.TOP] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.LEF] instanceof Spikes)
				onPlayerDeath();
			if (player.getCollisionMatrix()[PhysicsObject.RIG] instanceof Spikes)
				onPlayerDeath();

			for (int i = 0; i < flowers.size(); i++) {
				if (flowers.get(i).getHitbox().isIntersecting(player.getHitbox())) {
					if (flowers.get(i).getType() == 1)
						water(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 3);
					else
						addGas(flowers.get(i).getCol(), flowers.get(i).getRow(), map, 20, new ArrayList<Gas>());
					flowers.remove(i);
					i--;
				}
			}
			boolean didITouchWater = false;
			for (Water w: waters){
				if (w.getHitbox().isIntersecting(player.getHitbox())){
					//System.out.println("Touching water");
					didITouchWater = true;
					player.walkSpeed = 100;
				}
			}

			boolean didItTouchGas = false;
			for (Gas g: gasses){
				if (g.getHitbox().isIntersecting(player.getHitbox())){
					System.out.println("Touching gas");
					didItTouchGas = true;
				}
			}

			if (!didItTouchGas){
				player.time = 0;
			}
			else{
				if(player.time ==0){
					player.time = System.currentTimeMillis();
				}
				else{
					 double newTime = (System.currentTimeMillis()-player.time)/1000;
					 if (newTime >= 13){
						onPlayerDeath();
					 }
				}

			}




			if (!didITouchWater){
				//System.out.println("never touched water");
				player.walkSpeed=900;
			}
			// Update the enemies
			for (int i = 0; i < enemies.length; i++) {
				enemies[i].update(tslf);
				if (player.getHitbox().isIntersecting(enemies[i].getHitbox())) {
					onPlayerDeath();
				}
			}

			// Update the map
			map.update(tslf);

			// Update the camera
			camera.update(tslf);
		}
	}
	//precondition: Gas flowers are present in the game, level is not null, and player is present and not null
	//postcondition: 20 gas tiles form around the place that the player hit the flower. Gas tiles may vary in pattern, but will form a overall square/rectangle form as it fills up.
	private void addGas(int col, int row, Map map, int numSquaresToFill, ArrayList<Gas> placedThisRound) {
		Gas g = new Gas(col, row, tileSize, tileset.getImage("GasOne"), this, 0);
		map.addTile(col, row, g);
		numSquaresToFill--;
		placedThisRound.add(g);

		while (placedThisRound.size() > 0 && numSquaresToFill >= 0) {
			// draw desired pattern at start location
			// of the tile in placedthisround.get(0)
			// be sure to add each tile you draw to placedthisround
			int r = placedThisRound.get(0).getRow();
			int c = placedThisRound.get(0).getCol();
			placedThisRound.remove(0);
			for (int rowIndex = r - 1; rowIndex < r + 2; rowIndex++) {
				for (int colIndex = c; colIndex > c - 2; colIndex -= 2) {
					if (rowIndex >= 0 && rowIndex < map.getTiles()[0].length && colIndex >= 0
							&& colIndex < map.getTiles().length && numSquaresToFill>0) {
						if (!(map.getTiles()[colIndex][rowIndex] instanceof Gas)
								&& !(map.getTiles()[colIndex][rowIndex].isSolid())) {
							g = new Gas(colIndex, rowIndex, tileSize, tileset.getImage("GasOne"), this, 0);
							map.addTile(colIndex, rowIndex, g);
							numSquaresToFill--;
							placedThisRound.add(g);
							gasses.add(g);

						}
					}
					if(colIndex == c){
						colIndex+=3;
					}
				}

			}
		}

	}




	// #############################################################################################################
	// Your code goes here!
	// Please make sure you read the rubric/directions carefully and implement the
	// solution recursively!\
	// Precondition: Water does not go out of bounds, character has not died yet,
	// flowers implemented correctly
	// Postcondition: Creates different types of water based on the boundary of the
	// blocks, (ie: if there is no block, falling water, full water if there is,
	// with half next to it, and then a quarter)
	private void water(int col, int row, Map map, int fullness) {
		
		String size = "Full_water";
		if (fullness == 2) {
			size = "Half_water";
		}

		if (fullness == 1) {
			size = "Quarter_water";
		}

		if (fullness == 0) {
			size = "Falling_water";
		}
		// make water (You’ll need modify this to make different kinds of water such as
		// half water and quarter water)
		Water w = new Water(col, row, tileSize, tileset.getImage(size), this, fullness);
		waters.add(w);

		map.addTile(col, row, w);

		// check if we can go down
		if (row + 1 < map.getTiles()[0].length && !(map.getTiles()[col][row + 1] instanceof Water)
				&& !(map.getTiles()[col][row + 1].isSolid())) {
			if (row + 2 < map.getTiles()[0].length && !(map.getTiles()[col][row + 2] instanceof Water)
					&& (map.getTiles()[col][row + 2].isSolid())) {
				water(col, row + 1, map, 3);
			} else {
				water(col, row + 1, map, 0);
			}
		} else if (row + 1 < map.getTiles()[0].length && !(map.getTiles()[col][row + 1] instanceof Water)
				&& (map.getTiles()[col][row + 1].isSolid())) {
			int firstFullness = fullness;
					System.out.println("go left and right");
			if (firstFullness != 1) {
				firstFullness -= 1;
			}
			// if we can’t go down go left and right.
			// right
			if (col + 1 < map.getTiles().length && !(map.getTiles()[col + 1][row] instanceof Water)
					&& !(map.getTiles()[col + 1][row].isSolid())) {
				water(col + 1, row, map, firstFullness);
			}
			// left
			if (col - 1 >= 0 && !(map.getTiles()[col - 1][row] instanceof Water)
					&& !(map.getTiles()[col - 1][row].isSolid())) {
				water(col - 1, row, map, firstFullness);
			}
		}

	}

	public void draw(Graphics g) {
		g.translate((int) -camera.getX(), (int) -camera.getY());
		// Draw the map
		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				Tile tile = map.getTiles()[x][y];
				if (tile == null)
					continue;
				if (tile instanceof Gas) {

					int adjacencyCount = 0;
					for (int i = -1; i < 2; i++) {
						for (int j = -1; j < 2; j++) {
							if (j != 0 || i != 0) {
								if ((x + i) >= 0 && (x + i) < map.getTiles().length && (y + j) >= 0
										&& (y + j) < map.getTiles()[x].length) {
									if (map.getTiles()[x + i][y + j] instanceof Gas) {
										adjacencyCount++;
									}
								}
							}
						}
					}
					if (adjacencyCount == 8) {
						((Gas) (tile)).setIntensity(2);
						tile.setImage(tileset.getImage("GasThree"));
					} else if (adjacencyCount > 5) {
						((Gas) (tile)).setIntensity(1);
						tile.setImage(tileset.getImage("GasTwo"));
					} else {
						((Gas) (tile)).setIntensity(0);
						tile.setImage(tileset.getImage("GasOne"));
					}
				}
				if (camera.isVisibleOnCamera(tile.getX(), tile.getY(), tile.getSize(), tile.getSize()))
					tile.draw(g);
			}
		}

		// Draw the enemies
		for (int i = 0; i < enemies.length; i++) {
			enemies[i].draw(g);
		}

		// Draw the player
		player.draw(g);

		// used for debugging
		if (Camera.SHOW_CAMERA)
			camera.draw(g);
		g.translate((int) +camera.getX(), (int) +camera.getY());
	}

	// --------------------------Die-Listener
	public void throwPlayerDieEvent() {
		for (PlayerDieListener playerDieListener : dieListeners) {
			playerDieListener.onPlayerDeath();
		}
	}

	public void addPlayerDieListener(PlayerDieListener listener) {
		dieListeners.add(listener);
	}

	// ------------------------Win-Listener
	public void throwPlayerWinEvent() {
		for (PlayerWinListener playerWinListener : winListeners) {
			playerWinListener.onPlayerWin();
		}
	}

	public void addPlayerWinListener(PlayerWinListener listener) {
		winListeners.add(listener);
	}

	// ---------------------------------------------------------Getters
	public boolean isActive() {
		return active;
	}

	public boolean isPlayerDead() {
		return playerDead;
	}

	public boolean isPlayerWin() {
		return playerWin;
	}

	public Map getMap() {
		return map;
	}

	public Player getPlayer() {
		return player;
	}
}