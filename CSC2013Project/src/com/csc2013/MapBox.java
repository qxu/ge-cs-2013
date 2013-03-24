package com.csc2013;

import org.newdawn.slick.SlickException;

import com.csc2013.DungeonMaze.BoxType;
import com.csc2013.DungeonMaze.MoveType;

public class MapBox
{
	
	final BoxType North;
	final BoxType South;
	final BoxType East;
	final BoxType West;
	
	final MoveType NorthMove;
	final MoveType SouthMove;
	final MoveType EastMove;
	final MoveType WestMove;
	private boolean hasConsumable;
	private boolean isEnd;
	String ConsumableType = null;
	
	public MapBox() throws SlickException
	{
		this.North = BoxType.Open;
		this.South = BoxType.Open;
		this.East = BoxType.Open;
		this.West = BoxType.Open;
		
		this.NorthMove = MoveType.Open;
		this.SouthMove = MoveType.Open;
		this.EastMove = MoveType.Open;
		this.WestMove = MoveType.Open;
		
		this.hasConsumable = false;
		this.isEnd = false;
	}
	
	public MapBox(BoxType N, BoxType S, BoxType E, BoxType W,
			boolean consumable, boolean end)
	{
		this.North = N;
		this.South = S;
		this.East = E;
		this.West = W;
		
		if(N == BoxType.Open || N == BoxType.Key)
		{
			this.NorthMove = MoveType.Open;
		}
		else
		{
			this.NorthMove = MoveType.Blocked;
		}
		if(S == BoxType.Open || S == BoxType.Key)
		{
			this.SouthMove = MoveType.Open;
		}
		else
		{
			this.SouthMove = MoveType.Blocked;
		}
		if(E == BoxType.Open || E == BoxType.Key)
		{
			this.EastMove = MoveType.Open;
		}
		else
		{
			this.EastMove = MoveType.Blocked;
		}
		if(W == BoxType.Open || W == BoxType.Key)
		{
			this.WestMove = MoveType.Open;
		}
		else
		{
			this.WestMove = MoveType.Blocked;
		}
		
		this.hasConsumable = consumable;
		if(consumable)
		{
			this.ConsumableType = "hi";
		}
		this.isEnd = end;
	}
	
	public boolean hasKey()
	{
		return this.hasConsumable;
	}
	
	public void consume()
	{
		this.hasConsumable = false;
	}
	
	public boolean isEnd()
	{
		return this.isEnd;
	}
	
}
