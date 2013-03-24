package com.csc2013;

import java.awt.Point;

import com.csc2013.DungeonMaze.MoveType;

public class PlayerVision
{
	MapBox CurrentPoint;
	
	MapBox[] North;
	MapBox[] South;
	MapBox[] East;
	MapBox[] West;
	
	int mNorth;
	int mSouth;
	int mEast;
	int mWest;
	public static int distanceToView = 9;
	
	public PlayerVision(Map map, Point playerLoc)
	{
		this.mNorth = 0;
		this.mSouth = 0;
		this.mEast = 0;
		this.mWest = 0;
		
		this.CurrentPoint = map.getMapBox(playerLoc);
		
		/* West */
		MapBox curPoint = map.getMapBox(playerLoc);
		while(curPoint.WestMove == MoveType.Open && this.mWest < distanceToView)
		{
			this.mWest++;
			curPoint = map.getMapBox(new Point(
					(int)playerLoc.getX() - this.mWest,
					(int)playerLoc.getY()));
		}
		
		this.West = new MapBox[this.mWest];
		for(int i = 1; i <= this.mWest; i++)
		{
			curPoint = map.getMapBox(new Point((int)playerLoc.getX() - i,
					(int)playerLoc.getY()));
			this.West[i - 1] = curPoint;
		}
		
		/* East */
		curPoint = map.getMapBox(playerLoc);
		while(curPoint.EastMove == MoveType.Open && this.mEast < distanceToView)
		{
			this.mEast++;
			curPoint = map.getMapBox(new Point(
					(int)playerLoc.getX() + this.mEast,
					(int)playerLoc.getY()));
		}
		
		this.East = new MapBox[this.mEast];
		for(int i = 1; i <= this.mEast; i++)
		{
			curPoint = map.getMapBox(new Point((int)playerLoc.getX() + i,
					(int)playerLoc.getY()));
			this.East[i - 1] = curPoint;
		}
		
		/* North */
		curPoint = map.getMapBox(playerLoc);
		while(curPoint.NorthMove == MoveType.Open && this.mNorth < distanceToView)
		{
			this.mNorth++;
			curPoint = map.getMapBox(new Point((int)playerLoc.getX(),
					(int)playerLoc.getY() - this.mNorth));
		}
		
		this.North = new MapBox[this.mNorth];
		for(int i = 1; i <= this.mNorth; i++)
		{
			curPoint = map.getMapBox(new Point((int)playerLoc.getX(),
					(int)playerLoc.getY() - i));
			this.North[i - 1] = curPoint;
		}
		
		/* South */
		curPoint = map.getMapBox(playerLoc);
		while(curPoint.SouthMove == MoveType.Open && this.mSouth < distanceToView)
		{
			this.mSouth++;
			curPoint = map.getMapBox(new Point((int)playerLoc.getX(),
					(int)playerLoc.getY() + this.mSouth));
		}
		
		this.South = new MapBox[this.mSouth];
		for(int i = 1; i <= this.mSouth; i++)
		{
			curPoint = map.getMapBox(new Point((int)playerLoc.getX(),
					(int)playerLoc.getY() + i));
			this.South[i - 1] = curPoint;
		}
	}
}
