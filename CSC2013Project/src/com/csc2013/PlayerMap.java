package com.csc2013;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.csc2013.DungeonMaze.Action;
import com.csc2013.DungeonMaze.BoxType;

public class PlayerMap
{
	private MapPoint player;
	private Map<MapPoint, BoxType> grid;
	private final Object lock = new Object();
	
	/*
	 * For use in debugger, allows the JFrame to properly display the map.
	 * Otherwise, these variables serve no purpose.
	 */
	int minX;
	int minY;
	int maxX;
	int maxY;
	
	private SchoolPlayerDebugger debugger;
	
	public PlayerMap()
	{
		reset();
	}
	
	public void setDebugger(SchoolPlayerDebugger debug)
	{
		this.debugger = debug;
	}
	
	public SchoolPlayerDebugger getDebugger()
	{
		return this.debugger;
	}
	
	public void reset()
	{
		this.player = new MapPoint(0, 0);
		this.grid = new HashMap<>();
	}
	
	public void setPlayerPosition(int x, int y)
	{
		setPlayerPosition(new MapPoint(x, y));
	}
	
	public void setPlayerPosition(MapPoint point)
	{
		synchronized(this.lock)
		{
			this.player = point;
		}
	}
	
	/*
	 * For use in debugger, allows the JFrame to properly display the map.
	 */
	private void cacheBounds(MapPoint point)
	{
		int x = point.x;
		int y = point.y;
		if(x < this.minX)
		{
			this.minX = x;
		}
		if(x > this.maxX)
		{
			this.maxX = x;
		}
		if(y < this.minY)
		{
			this.minY = y;
		}
		if(y > this.maxY)
		{
			this.maxY = y;
		}
	}
	
	public void set(BoxType type, int x, int y)
	{
		set(type, new MapPoint(x, y));
	}
	
	public void set(BoxType type, MapPoint point)
	{
		synchronized(this.lock)
		{
			cacheBounds(point);
			this.grid.put(point, type);
		}
	}
	
	public BoxType get(int x, int y)
	{
		return get(new MapPoint(x, y));
	}
	
	public BoxType get(MapPoint point)
	{
		synchronized(this.lock)
		{
			return this.grid.get(point);
		}
	}
	
	public BoxType getPlayer()
	{
		return get(getPlayerPoint());
	}
	
	public MapPoint getPlayerPoint()
	{
		return this.player;
	}
	
	public boolean contains(BoxType type)
	{
		return this.grid.containsValue(type);
	}
	
	/* Important stuff */
	
	private Set<MapPoint> find(BoxType type)
	{
		if(type == null)
			return null;
		
		Set<MapPoint> found = new HashSet<>();
		for(Entry<MapPoint, BoxType> entry : this.grid.entrySet())
		{
			if(entry.getValue() == type)
			{
				found.add(entry.getKey());
			}
		}
		return found;
	}
	
	public Action actionTo(BoxType type)
	{
		Set<MapPoint> destPoints = find(type);
		if(destPoints.isEmpty())
			return null;
		
		Set<MapPath> paths = (destPoints.size() == 1)
				? AStarSearch.search(this, getPlayerPoint(), destPoints
						.iterator().next())
				: BFSearch.search(this, getPlayerPoint(), type, false);
		
		if(paths.isEmpty())
			return null;
		
		for(MapPath path : paths)
		{
			if(path.isBasePath())
				return Action.Pickup;
			Action move = getPathAction(path);
			if(canExplore(move))
				return move;
		}
		
		return getPathAction(paths.iterator().next());
	}
	
	private Action getPathAction(MapPath path)
	{
		final MapPoint player = getPlayerPoint();
		final MapPoint point = path.getStepPath().getLastPoint();
		if(player.equals(point))
			return Action.Pickup;
		return player.actionTo(point);
	}
	
	public Action discoveryChannel(Action lastMove, int keyCount)
	{
		if(canExplore(lastMove))
			return lastMove;
		
		Set<MapPath> paths = BFSearch.search(this, getPlayerPoint(), null,
				keyCount > 0);
		
		if(paths.isEmpty())
			return null;
		
		Set<Action> moves = EnumSet.noneOf(Action.class);
		for(MapPath path : paths)
		{
			moves.add(getPathAction(path));
		}
		
		for(Action move : moves)
		{
			if(canExplore(move))
				return move;
		}
		
		if(moves.contains(lastMove))
			return lastMove;
		
		return moves.iterator().next();
	}
	
	private boolean canExplore(Action move)
	{
		if(move == null)
			return false;
		MapPoint cur = getPlayerPoint();
		while(get(cur) == BoxType.Open)
		{
			MapPoint next = cur.execute(move);
			if(next.equals(cur))
				return false;
			cur = next;
		}
		return get(cur) == null;
	}
}
