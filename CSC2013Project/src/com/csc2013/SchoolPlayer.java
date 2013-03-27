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
		this.map = new PlayerMap();
		this.debugger = new SchoolPlayerDebugger(this, this.map);
		this.map.setDebugger(this.debugger);
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
		if(this.lastMove == null)
		{
			lastAction = false;
		}

		updateMap(vision, this.lastMove, lastAction);

		this.debugger.updateMap();
		
		Action move = getMove(vision, keyCount, lastAction);
		
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
		
		this.lastMove = move;
		return move;
	}
	
	private Action getMove(PlayerVision vision, int keyCount, boolean lastAction)
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
		
		Action coverSpaceAction = this.map.discoveryChannel(this.lastMove,
				keyCount);
		if(coverSpaceAction != null)
			return coverSpaceAction;
		
		System.out.println("??");
		return this.aiPlayer.nextMove(vision, keyCount, lastAction);
	}
	
	private void updateMap(PlayerVision vision, Action move, boolean lastAction)
	{
		if(lastAction)
		{
			switch(this.lastMove)
			{
				case West:
					--this.xposition;
					break;
				case East:
					++this.xposition;
					break;
				case North:
					--this.yposition;
					break;
				case South:
					++this.yposition;
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
		
		int centerHoriz = this.xposition;
		int centerVert = this.yposition;
		
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
		{
			map.set(BoxType.Key, centerHoriz, centerVert);
		}
		else
		{
			map.set(BoxType.Open, centerHoriz, centerVert);
		}
		
		map.set(current.West, centerHoriz - 1, centerVert);
		map.set(current.East, centerHoriz + 1, centerVert);
		map.set(current.North, centerHoriz, centerVert - 1);
		map.set(current.South, centerHoriz, centerVert + 1);
		
		map.setPlayerPosition(centerHoriz, centerVert);
		if(vision.CurrentPoint.hasKey())
		{
			map.set(BoxType.Key, centerHoriz, centerVert);
		}
		else
		{
			map.set(BoxType.Open, centerHoriz, centerVert);	
		}
	}
}
