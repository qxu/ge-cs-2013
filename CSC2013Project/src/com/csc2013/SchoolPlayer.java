package com.csc2013;

import org.newdawn.slick.SlickException;

import com.csc2013.DungeonMaze.Action;
import com.csc2013.DungeonMaze.BoxType;

/**
 * 
 * [To be completed by students]
 * 
 * @author [Westhill/NullPointerException]
 * 
 */
public class SchoolPlayer
{
	final PlayerMap map;
	
	private Action lastMove;
	
	private static SchoolPlayer latestInstance;
	
	public static PlayerMapDebugger getLatestDebugger()
	{
		return latestInstance.map.getDebugger();
	}
	
	/**
	 * Constructor.
	 * 
	 * @throws SlickException
	 */
	public SchoolPlayer() throws SlickException
	{
		latestInstance = this;
		this.map = new PlayerMap();
	}
	
	/**
	 * To properly implement this class you simply must return an Action in the
	 * function nextMove below.
	 * 
	 * You are allowed to define any helper variables or methods as you see fit
	 * 
	 * For a full explanation of the variables please reference the instruction
	 * manual provided
	 * 
	 * @param vision
	 * @param keyCount
	 * @param lastAction
	 * @return Action
	 */
	public Action nextMove(PlayerVision vision, int keyCount, boolean lastAction)
	{
		updateMap(vision);
		
		Action move = getMove(keyCount, lastAction);
		
		if(vision.CurrentPoint.hasKey() != (this.map.getPlayerPoint().getType() == BoxType.Key))
			throw new AssertionError("key not dectected");
		
		if(this.map.getPlayerPoint().execute(move).getType() == BoxType.Door)
		{
			System.out.println("Use Action detected");
			move = Action.Use;
		}

//		if(vision.CurrentPoint.hasKey())
//		{
//			move = Action.Pickup;
//		}

		updatePlayerPosition(move);
		this.lastMove = move;
		return move;
	}
	
	private Action getMove(int keyCount, boolean lastAction)
	{
		Action exitAction = this.map.actionTo(BoxType.Exit);
		if(exitAction != null)
			return exitAction;
		
		if(keyCount < 8)
		{
			Action keyAction = this.map.actionTo(BoxType.Key);
			if(keyAction != null)
				return keyAction;
		}
		
//		if(keyCount > 0)
//		{
//			Action doorAction = this.map.actionTo(BoxType.Door);
//			if(doorAction != null)
//				return doorAction;
//		}
		
		Action coverSpaceAction = this.map.discoveryChannel(this.lastMove, keyCount);
		if(coverSpaceAction != null)
			return coverSpaceAction;
		
		System.out.println("??");
		throw new RuntimeException("don't know what to do");
	}
	
	private void updatePlayerPosition(Action move)
	{
		this.map.movePlayer(move);
	}
	
	private void updateMap(PlayerVision vision)
	{
		MapPoint player = this.map.getPlayerPoint();
		
		int centerX = player.x;
		int centerY = player.y;
		
		int westOffset = vision.mWest;
		int eastOffset = vision.mEast;
		int northOffset = vision.mNorth;
		int southOffset = vision.mSouth;
		
		MapBox[] west = vision.West;
		MapBox[] east = vision.East;
		MapBox[] north = vision.North;
		MapBox[] south = vision.South;
		
		PlayerMap map = this.map;
		
		for(int i = westOffset - 1; i >= 0; --i)
		{
			MapBox cell = west[i];
			map.set(centerX - i - 2, centerY, cell.West);
			map.set(centerX - i - 1, centerY - 1, cell.North);
			map.set(centerX - i - 1, centerY + 1, cell.South);
		}
		for(int i = eastOffset - 1; i >= 0; --i)
		{
			MapBox cell = east[i];
			map.set(centerX + i + 2, centerY, cell.East);
			map.set(centerX + i + 1, centerY - 1, cell.North);
			map.set(centerX + i + 1, centerY + 1, cell.South);
		}
		for(int i = northOffset - 1; i >= 0; --i)
		{
			MapBox cell = north[i];
			map.set(centerX, centerY - i - 2, cell.North);
			map.set(centerX - 1, centerY - i - 1, cell.West);
			map.set(centerX + 1, centerY - i - 1, cell.East);
		}
		for(int i = southOffset - 1; i >= 0; --i)
		{
			MapBox cell = south[i];
			map.set(centerX, centerY + i + 2, cell.South);
			map.set(centerX - 1, centerY + i + 1, cell.West);
			map.set(centerX + 1, centerY + i + 1, cell.East);
		}
		
		MapBox current = vision.CurrentPoint;

		map.set(centerX, centerY, current.hasKey() ? BoxType.Key : BoxType.Open);
		
		map.set(centerX - 1, centerY, current.West);
		map.set(centerX + 1, centerY, current.East);
		map.set(centerX, centerY - 1, current.North);
		map.set(centerX, centerY + 1, current.South);
	}
}
