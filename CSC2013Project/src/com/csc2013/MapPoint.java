package com.csc2013;

import java.util.Arrays;

import com.csc2013.DungeonMaze.Action;
import com.csc2013.DungeonMaze.BoxType;

public class MapPoint
{
	public final int x;
	public final int y;
	
	private MapPoint west;
	private MapPoint east;
	private MapPoint north;
	private MapPoint south;
	
	private PlayerMap map;
	
	public MapPoint(int x, int y, PlayerMap map)
	{
		if(map == null)
			throw new NullPointerException();
		this.x = x;
		this.y = y;
		this.map = map;
	}
	
	public PlayerMap getMap()
	{
		return this.map;
	}
	
	public BoxType getType()
	{
		return this.map.getTypeOf(this);
	}
	
	public MapPoint west()
	{
		MapPoint cachedW = this.west;
		if(cachedW != null)
			return cachedW;
		MapPoint w = new MapPoint(x - 1, y, map);
		MapPoint mapW = map.get(w);
		return (mapW != null) ? (this.west = mapW) : w;
	}
	
	public MapPoint east()
	{
		MapPoint cachedE = this.east;
		if(cachedE != null)
			return cachedE;
		MapPoint e = new MapPoint(x + 1, y, map);
		MapPoint mapE = map.get(e);
		return (mapE != null) ? (this.east = mapE) : e;
	}
	
	public MapPoint north()
	{
		MapPoint cachedN = this.north;
		if(cachedN != null)
			return cachedN;
		MapPoint n = new MapPoint(x, y - 1, map);
		MapPoint mapN = map.get(n);
		return (mapN != null) ? (this.north = mapN) : n;
	}
	
	public MapPoint south()
	{
		MapPoint cachedS = this.south;
		if(cachedS != null)
			return cachedS;
		MapPoint s = new MapPoint(x, y + 1, map);
		MapPoint mapS = map.get(s);
		return (mapS != null) ? (this.south = mapS) : s;
	}
	
	public Action actionTo(MapPoint point)
	{
		int dx = point.x - this.x;
		int dy = point.y - this.y;
		
		return (Math.abs(dx) > Math.abs(dy))
			? ((dx > 0) ? Action.East : Action.West)
			: ((dy > 0) ? Action.South : Action.North);
	}
	
	public MapPoint execute(Action move)
	{
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
		return Math.abs(this.x - dest.x) + Math.abs(this.y - dest.y);
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
		return getType() + "@(" + this.x + ", " + this.y + ")";
	}
}
