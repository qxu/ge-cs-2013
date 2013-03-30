package com.csc2013;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.csc2013.DungeonMaze.Action;
import com.csc2013.DungeonMaze.BoxType;

public class PlayerMap
{
	private Map<MapPoint, MapPoint> grid;
	private Map<MapPoint, BoxType> typeMap;

	private MapPoint playerPosition;
	
	private final PlayerMapDebugger debugger;
	
	public PlayerMap()
	{
		this(0, 0);
	}
	
	public PlayerMap(int x, int y)
	{
		this.grid = new HashMap<>();
		this.typeMap = new HashMap<>();
		this.playerPosition = new MapPoint(0, 0, this);
		this.debugger = new PlayerMapDebugger(this);
	}
	
	public MapPoint get(MapPoint point)
	{
		return grid.get(point);
	}
	
	public MapPoint get(int x, int y)
	{
		return get(new MapPoint(x, y, this));
	}
	
	public PlayerMapDebugger getDebugger()
	{
		return this.debugger;
	}
	
	public Set<MapPoint> getGrid()
	{
		return new HashSet<>(grid.keySet());
	}

	public BoxType getTypeOf(MapPoint point)
	{
		return typeMap.get(point);
	}
	
	public void movePlayer(Action move)
	{
		this.playerPosition = this.playerPosition.execute(move);
	}
	
	public MapPoint set(int x, int y, BoxType type)
	{
		if(type == null)
			throw new NullPointerException();
		MapPoint newPoint = new MapPoint(x, y, this);
		MapPoint existingPoint = grid.get(newPoint);
		if(existingPoint == null)
		{
			grid.put(newPoint, newPoint);
			typeMap.put(newPoint, type);
			return newPoint;
		}
		else
		{
			typeMap.put(existingPoint, type);
			return existingPoint;
		}
	}
	
	public MapPoint getPlayerPosition()
	{
		return this.playerPosition;
	}
	
	public Set<MapPoint> find(BoxType type)
	{
		Set<MapPoint> found = new HashSet<>();
		for(Map.Entry<MapPoint, BoxType> entry : typeMap.entrySet())
		{
			if(entry.getValue() == type)
			{
				found.add(entry.getKey());
			}
		}
		return found;
	}
}
