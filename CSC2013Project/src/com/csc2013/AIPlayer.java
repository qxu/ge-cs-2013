package com.csc2013;

import org.newdawn.slick.SlickException;

import com.csc2013.DungeonMaze.Action;
import com.csc2013.DungeonMaze.BoxType;
import com.csc2013.DungeonMaze.MoveType;

public class AIPlayer
{
	private Action nextMove;
	private Action lastDirection;
	private String name = "Sample AI";
	
	public AIPlayer() throws SlickException
	{
		this.lastDirection = Action.East;
		this.nextMove = Action.East;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	/*
	 * vision is a class of type PlayerVision that contains information about
	 * all the directions you can see
	 * 
	 * 
	 * vision.CurrentPoint is a class of type MapBox containing what lies in
	 * each direction as: MoveType (Open, Blocked) BoxType (Open, Door, Blocked,
	 * Key, Exit)
	 * 
	 * To look in each direction past your current point use the following
	 * variables in conjunction with their repsective arrays
	 * 
	 * vision.North[vision.mNorth] vision.South[vision.mSouth]
	 * vision.East[vision.mEast] vision.West[vision.mWest]
	 * 
	 * where vision.mDirection is how many open spaces are in that direction
	 * vision.Direction is the array of MapBox objects in that direction
	 * 
	 * lastAction is a boolean of the result of your last action i.e. if you
	 * tried to move and that direction was a wall, it would be false Action is
	 * what you tell your player to do (move in a direction, pickup a key, or
	 * use it to open a door)
	 * 
	 * We have provided a very basic AI for moving around
	 * 
	 * To pickup a key, simply move your player onto the space and check if
	 * CurrentPoint.hasKey() is true, then return Action.Pickup
	 * 
	 * To open a door, simply move to a space next to it and return Action.Use
	 * and it will automatically open the door next to you Note: There will only
	 * ever be one door next to your character at a time
	 * 
	 * Lastly if you move onto the Exit space, the game will complete and the
	 * number of steps you took will be tracked
	 * 
	 * not a valid move - returns false (counts as a step)
	 * 
	 * pass in 2d array of boxes to mapbox
	 */
	public Action nextMove(final PlayerVision vision, final int keyCount,
			final boolean lastAction)
	{
		/*
		 * if(!lastAction) { System.out.print(vision.CurrentPoint.North);
		 * System.out.print("-"); System.out.print(vision.CurrentPoint.South);
		 * System.out.print("-"); System.out.print(vision.CurrentPoint.East);
		 * System.out.print("-"); System.out.print(vision.CurrentPoint.West);
		 * System.out.print("\n"); }
		 */
		if(vision.CurrentPoint.South == BoxType.Exit)
		{
			this.nextMove = Action.South;
			return this.nextMove;
		}
		if(vision.CurrentPoint.North == BoxType.Exit)
		{
			this.nextMove = Action.North;
			return this.nextMove;
		}
		if(vision.CurrentPoint.West == BoxType.Exit)
		{
			this.nextMove = Action.West;
			return this.nextMove;
		}
		if(vision.CurrentPoint.East == BoxType.Exit)
		{
			this.nextMove = Action.East;
			return this.nextMove;
		}
		
		if(vision.CurrentPoint.hasKey())
			return Action.Pickup;
		
		if(vision.CurrentPoint.South == BoxType.Key)
		{
			this.nextMove = Action.South;
			return this.nextMove;
		}
		if(vision.CurrentPoint.North == BoxType.Key)
		{
			this.nextMove = Action.North;
			return this.nextMove;
		}
		if(vision.CurrentPoint.West == BoxType.Key)
		{
			this.nextMove = Action.West;
			return this.nextMove;
		}
		if(vision.CurrentPoint.East == BoxType.Key)
		{
			this.nextMove = Action.East;
			return this.nextMove;
		}
		
		if(keyCount > 0)
		{
			if(vision.CurrentPoint.South == BoxType.Door || vision.CurrentPoint.North == BoxType.Door || vision.CurrentPoint.East == BoxType.Door || vision.CurrentPoint.West == BoxType.Door)
				return Action.Use;
		}
		
		if(vision.CurrentPoint.SouthMove == MoveType.Open)
		{
			this.nextMove = Action.South;
		}
		else if(vision.CurrentPoint.NorthMove == MoveType.Open)
		{
			this.nextMove = Action.North;
		}
		else if(vision.CurrentPoint.WestMove == MoveType.Open)
		{
			this.nextMove = Action.West;
		}
		else if(vision.CurrentPoint.EastMove == MoveType.Open)
		{
			this.nextMove = Action.East;
		}
		
		if(this.lastDirection == Action.North)
		{
			if(vision.CurrentPoint.North == BoxType.Open)
			{
				this.nextMove = Action.North;
			}
			else if(vision.CurrentPoint.West == BoxType.Open)
			{
				this.nextMove = Action.West;
			}
			else if(vision.CurrentPoint.East == BoxType.Open)
			{
				this.nextMove = Action.East;
			}
			else if(vision.CurrentPoint.South == BoxType.Open)
			{
				this.nextMove = Action.South;
			}
		}
		if(this.lastDirection == Action.South)
		{
			if(vision.CurrentPoint.South == BoxType.Open)
			{
				this.nextMove = Action.South;
			}
			else if(vision.CurrentPoint.West == BoxType.Open)
			{
				this.nextMove = Action.West;
			}
			else if(vision.CurrentPoint.East == BoxType.Open)
			{
				this.nextMove = Action.East;
			}
			else if(vision.CurrentPoint.North == BoxType.Open)
			{
				this.nextMove = Action.North;
			}
		}
		if(this.lastDirection == Action.East)
		{
			if(vision.CurrentPoint.East == BoxType.Open)
			{
				this.nextMove = Action.East;
			}
			else if(vision.CurrentPoint.North == BoxType.Open)
			{
				this.nextMove = Action.North;
			}
			else if(vision.CurrentPoint.South == BoxType.Open)
			{
				this.nextMove = Action.South;
			}
			else if(vision.CurrentPoint.West == BoxType.Open)
			{
				this.nextMove = Action.West;
			}
		}
		if(this.lastDirection == Action.West)
		{
			if(vision.CurrentPoint.West == BoxType.Open)
			{
				this.nextMove = Action.West;
			}
			else if(vision.CurrentPoint.South == BoxType.Open)
			{
				this.nextMove = Action.South;
			}
			else if(vision.CurrentPoint.North == BoxType.Open)
			{
				this.nextMove = Action.North;
			}
			else if(vision.CurrentPoint.East == BoxType.Open)
			{
				this.nextMove = Action.East;
			}
		}
		
		this.lastDirection = this.nextMove;
		
		return this.nextMove;
	}
}
