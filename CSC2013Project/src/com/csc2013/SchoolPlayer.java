package com.csc2013;

import java.util.Set;

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
	static final boolean DEBUG = true;
	
	private PlayerMap map;
	
	private int xposition = 0;
	private int yposition = 0;
	
	private SchoolPlayerDebugger debugger;
	
	private Action lastMove;
	
	/**
	 * Constructor.
	 * 
	 * @throws SlickException
	 */
	public SchoolPlayer() throws SlickException
	{
		map = new PlayerMap();
		if(DEBUG)
		{
			this.debugger = new SchoolPlayerDebugger(this, map);
		}
		map.setDebugger(debugger);
	}
	
	private AIPlayer aiPlayer = new AIPlayer();
	
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
		if(lastMove == null)
			lastAction = false;
		
		updateMap(vision, lastMove, lastAction);
		
		Action move = getMove(vision, keyCount, lastAction);
		
		//		try
		//		{
		//			Thread.sleep(500);
		//		}
		//		catch(InterruptedException e)
		//		{
		//			throw new RuntimeException(e);
		//		}
		
		if(vision.CurrentPoint.hasKey() != (map.getPlayer() == BoxType.Key))
			throw new AssertionError("key not dectected");
		
		if(map.get(map.getPlayerPoint().execute(move)) == BoxType.Door)
			move = Action.Use;
		if(map.getPlayer() == BoxType.Key)
			move = Action.Pickup;
		
		this.lastMove = move;
		return move;
	}
	
	private Action getMove(PlayerVision vision, int keyCount, boolean lastAction)
	{
		Action exitAction = map.actionTo(BoxType.Exit);
		if(exitAction != null)
			return exitAction;
		
		Action keyAction = map.actionTo(BoxType.Key);
		if(keyAction != null)
			return keyAction;
		
		if(keyCount > 0)
		{
			Action doorAction = map.actionTo(BoxType.Door);
			if(doorAction != null)
				return doorAction;
		}
		
		Action coverSpaceAction = map.discoveryChannel(lastMove, keyCount);
		if(coverSpaceAction != null)
			return coverSpaceAction;
		
		System.out.println("??");
		return aiPlayer.nextMove(vision, keyCount, lastAction);
	}
	
	private void updateMap(PlayerVision vision, Action move, boolean lastAction)
	{
		if(lastAction)
		{
			switch(lastMove)
			{
				case West:
					--xposition;
					break;
				case East:
					++xposition;
					break;
				case North:
					--yposition;
					break;
				case South:
					++yposition;
					break;
				case Pickup:
				case Use:
					break;
			}
		}
		
		int leftOffset = vision.mWest;
		int rightOffset = vision.mEast;
		int topOffset = vision.mNorth;
		int bottomOffset = vision.mSouth;
		
		int centerHoriz = xposition;
		int centerVert = yposition;
		
		MapBox[] left = vision.West;
		MapBox[] right = vision.East;
		MapBox[] top = vision.North;
		MapBox[] bottom = vision.South;
		
		PlayerMap map = this.map;
		
		for(int i = leftOffset - 1; i >= 0; --i)
		{
			MapBox cell = left[i];
			map.set(cell.West, centerHoriz - i - 2, centerVert);
			map.set(cell.North, centerHoriz - i - 1, centerVert - 1);
			map.set(cell.South, centerHoriz - i - 1, centerVert + 1);
		}
		for(int i = rightOffset - 1; i >= 0; --i)
		{
			MapBox cell = right[i];
			map.set(cell.East, centerHoriz + i + 2, centerVert);
			map.set(cell.North, centerHoriz + i + 1, centerVert - 1);
			map.set(cell.South, centerHoriz + i + 1, centerVert + 1);
		}
		for(int i = topOffset - 1; i >= 0; --i)
		{
			MapBox cell = top[i];
			map.set(cell.North, centerHoriz, centerVert - i - 2);
			map.set(cell.West, centerHoriz - 1, centerVert - i - 1);
			map.set(cell.East, centerHoriz + 1, centerVert - i - 1);
		}
		for(int i = bottomOffset - 1; i >= 0; --i)
		{
			MapBox cell = bottom[i];
			map.set(cell.South, centerHoriz, centerVert + i + 2);
			map.set(cell.West, centerHoriz - 1, centerVert + i + 1);
			map.set(cell.East, centerHoriz + 1, centerVert + i + 1);
		}
		
		MapBox current = vision.CurrentPoint;
		if(current.hasKey())
			map.set(BoxType.Key, centerHoriz, centerVert);
		else
			map.set(BoxType.Open, centerHoriz, centerVert);
		
		map.set(current.West, centerHoriz - 1, centerVert);
		map.set(current.East, centerHoriz + 1, centerVert);
		map.set(current.North, centerHoriz, centerVert - 1);
		map.set(current.South, centerHoriz, centerVert + 1);
		
		map.setPlayerPosition(centerHoriz, centerVert);
		
		if(DEBUG)
		{
			debugger.update();
		}
	}
}
