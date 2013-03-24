package com.csc2013;

import java.awt.Point;

import org.newdawn.slick.Animation;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

import com.csc2013.DungeonMaze.Action;
import com.csc2013.DungeonMaze.BoxType;

public class Player
{
	
	private Animation player;
	private Point playerLocation;
	private int moveSize = 1;
	private MapBox currentMoves;
	private int keys = 0;
	private Map playerMap;
	
	public Player(Map map) throws SlickException
	{
		
		// Load character graphics, run the sprite animations, and start the player
		SpriteSheet sheet = new SpriteSheet("res/flashy.png", 16, 16);
		this.player = new Animation();
		this.player.setAutoUpdate(true);
		
		this.playerLocation = new Point(20, 14);
		
		this.currentMoves = map.getMapBox(this.playerLocation);
		
		this.playerMap = map;
		
		for(int frame = 0; frame < 3; frame++)
		{
			this.player.addFrame(sheet.getSprite(frame, 0), 150);
		}
		
	}
	
	// 
	public void setMapBox()
	{
		this.currentMoves = this.playerMap.getMapBox(this.playerLocation);
	}
	
	public Point getPlayerGridLocation()
	{
		return this.playerLocation;
	}
	
	// Return player x,y coordinates
	public Point getPlayerLocation()
	{
		Point pixelLoc = new Point((int)(this.playerLocation.getX() * 16),
				(int)(this.playerLocation.getY() * 16));
		return pixelLoc;
	}
	
	// Return player animation
	public Animation getPlayerAnimation()
	{
		return this.player;
	}
	
	// Move method, checks for validity then moves if valid
	// I made 3 move methods because the actual setting part has to be private
	public boolean move(Action action)
	{
		boolean moved = moveCheck(action);
		if(moved)
		{
			setPlayerLocation(action);
			/*
			 * System.out.print(playerLocation.getX()); System.out.print("-");
			 * System.out.print(playerLocation.getY()); System.out.print("\n");
			 */
			setMapBox();
		}
		return moved;
	}
	
	// Check the BoxType player is trying to move to, if Open:true Door:(key logic) else:false
	// Need to get the Map and etc when Brian is done
	private boolean moveCheck(Action action)
	{
		//System.out.print(currentMoves.North);
		//System.out.print("-\n");
		switch(action)
		{
			case North:
				if(this.currentMoves.North == BoxType.Open || this.currentMoves.North == BoxType.Key || this.currentMoves.North == BoxType.Exit)
					return true;
				break;
			case South:
				if(this.currentMoves.South == BoxType.Open || this.currentMoves.South == BoxType.Key || this.currentMoves.South == BoxType.Exit)
					return true;
				break;
			case East:
				if(this.currentMoves.East == BoxType.Open || this.currentMoves.East == BoxType.Key || this.currentMoves.East == BoxType.Exit)
					return true;
				break;
			case West:
				if(this.currentMoves.West == BoxType.Open || this.currentMoves.West == BoxType.Key || this.currentMoves.West == BoxType.Exit)
					return true;
				break;
			case Pickup:
				if(this.currentMoves.hasKey())
				{
					this.keys++;
					this.playerMap.pickup(this.playerLocation);
					//System.out.print("key pickup\n"); 
					return true;
				}
				break;
			case Use:
				if(this.keys <= 0)
					//System.out.print("no keys\n"); 
					return false;
				else if(this.playerMap.unlockDoor(this.playerLocation))
				{
					this.keys--;
					return true;
				}
				break;
			default:
				return false;
		}
		return false;
		
	}
	
	public MapBox getCurrentMove()
	{
		return this.playerMap.getMapBox(this.playerLocation);
	}
	
	private void setPlayerLocation(Action action)
	{
		
		switch(action)
		{
			case North:
				this.playerLocation.y -= this.moveSize;
				break;
			case South:
				this.playerLocation.y += this.moveSize;
				break;
			case East:
				this.playerLocation.x += this.moveSize;
				break;
			case West:
				this.playerLocation.x -= this.moveSize;
				break;
			default:
				break;
		}
		
	}
	
	public int getKeys()
	{
		return this.keys;
	}
	
	public boolean end()
	{
		if(this.currentMoves.isEnd())
			return true;
		return false;
	}
}
