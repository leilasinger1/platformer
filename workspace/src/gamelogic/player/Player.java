package gamelogic.player;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;

import gameengine.PhysicsObject;
import gameengine.graphics.MyGraphics;
import gameengine.hitbox.RectHitbox;
import gamelogic.Main;
import gamelogic.level.Level;
import gamelogic.tiles.Tile;

public class Player extends PhysicsObject{
	public float walkSpeed = 400;
	public float jumpPower = 1350;
	public long time;
	
	private boolean keyPressed = false;
	private boolean isJumping = false;
	private boolean secondJump = true;
	

	public Player(float x, float y, Level level) {
		super(x, y, level.getLevelData().getTileSize(), level.getLevelData().getTileSize(), level);
		int offset =(int)(level.getLevelData().getTileSize()*0.1); //hitbox is offset by 10% of the player size.
		this.hitbox = new RectHitbox(this, offset,offset, width -offset, height - offset);
		time = 0;
	}

	@Override
	public void update(float tslf) {
		super.update(tslf);
		
		movementVector.x = 0;
		if(PlayerInput.isLeftKeyDown()) {
			movementVector.x = -walkSpeed;
		}
		if(PlayerInput.isRightKeyDown()) {
			movementVector.x = +walkSpeed;
		}

		
		if(PlayerInput.isJumpKeyDown()) {
			if(!isJumping){
				movementVector.y = -jumpPower;
				isJumping = true;
				keyPressed= false;
			}
			else if(secondJump==true && keyPressed==true){
				movementVector.y = -jumpPower;
				secondJump= false;
				System.out.println("second jump");
			}
		}
		if(!PlayerInput.isJumpKeyDown()){
			keyPressed = true;
		}


		isJumping = true;
		if(collisionMatrix[BOT] != null) {isJumping = false; secondJump = true;}
	}

	@Override
	public void draw(Graphics g) {

		g.setColor(Color.BLUE);
		MyGraphics.fillRectWithOutline(g, (int)getX(), (int)getY(), width, height);
		g.setFont(new Font("Arial", Font.PLAIN,  50));
		if(time != 0){
			g.drawString((System.currentTimeMillis()-time)/1000+"",(int)getX(), (int)getY());
		}
	
		
		if(Main.DEBUGGING) {
			for (int i = 0; i < closestMatrix.length; i++) {
				Tile t = closestMatrix[i];
				if(t != null) {
					g.setColor(Color.RED);
					g.drawRect((int)t.getX(), (int)t.getY(), t.getSize(), t.getSize());
				}
			}
		}
		
		hitbox.draw(g);
	}
}
