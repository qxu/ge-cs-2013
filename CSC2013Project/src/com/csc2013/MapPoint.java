package com.csc2013;

import java.util.Arrays;

import com.csc2013.DungeonMaze.Action;

public class MapPoint
{
	public final int x;
	public final int y;
	
	public MapPoint(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public MapPoint(MapPoint point)
	{
		this(point.x, point.y);
	}
	
	//	public Action actionTo(MapPoint dest)
	//	{
	//		int xDiff = dest.x - x;
	//		int yDiff = dest.y - y;
	//		
	//		if(xDiff >= 0)
	//		{
	//			if(yDiff >= 0)
	//				return (xDiff >= yDiff) ? Action.East : Action.North;
	//			else
	//				return (xDiff >= yDiff) ? Action.East : Action.South;
	//		}
	//		else
	//		{
	//			if(yDiff >= 0)
	//				return (xDiff >= yDiff) ? Action.West : Action.North;
	//			else
	//				return (xDiff >= yDiff) ? Action.West : Action.South;
	//		}
	//	}
	
	public MapPoint west()
	{
		return new MapPoint(this.x - 1, this.y);
	}
	
	public MapPoint east()
	{
		return new MapPoint(this.x + 1, this.y);
	}
	
	public MapPoint north()
	{
		return new MapPoint(this.x, this.y - 1);
	}
	
	public MapPoint south()
	{
		return new MapPoint(this.x, this.y + 1);
	}
	
	public MapPoint execute(Action move)
	{
		if(move == null)
			return this;
		switch(move)
		{
			case West:
				return west();
			case East:
				return east();
			case North:
				return north();
			case South:
				return south();
			case Pickup:
			case Use:
				return this;
			default:
				throw new AssertionError();
		}
	}
	
	public int distanceTo(MapPoint dest)
	{
		return Math.abs(this.x - dest.x) + Math.abs(this.y + dest.y);
	}
	
	public Iterable<MapPoint> getNeighbors()
	{
		return Arrays.asList(new MapPoint[] {west(), east(), north(), south()});
	}
	
	@Override
	public int hashCode()
	{
		return (31 * this.x) ^ this.y;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof MapPoint))
			return false;
		MapPoint point = (MapPoint)o;
		return (this.x == point.x) && (this.y == point.y);
	}
	
	@Override
	public String toString()
	{
		return "(" + this.x + ", " + this.y + ")";
	}
}
