package com.csc2013;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

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
	
	private SortedSet<MapPoint> find(BoxType type)
	{
		final MapPoint player = getPlayerPoint();
		SortedSet<MapPoint> keys = new TreeSet<>(new Comparator<MapPoint>()
		{
			@Override
			public int compare(MapPoint p1, MapPoint p2)
			{
				int dist1 = player.distanceTo(p1);
				int dist2 = player.distanceTo(p2);
				return Integer.compare(dist1, dist2);
			}
		});
		for(Entry<MapPoint, BoxType> entry : this.grid.entrySet())
		{
			if(entry.getValue() == type)
			{
				keys.add(entry.getKey());
			}
		}
		return keys;
	}
	
	private MapPoint searchFor(BoxType type)
	{
		if(!this.grid.containsValue(type))
			return null;
		for(Entry<MapPoint, BoxType> e : this.grid.entrySet())
		{
			if(e.getValue() == type)
				return e.getKey();
		}
		return null;
	}
	
	public Action actionTo(BoxType type)
	{
		//		SortedSet<MapPoint> dests = find(type);
		//		if(dests.isEmpty())
		//			return null;
		//		
		//		List<MapPoint> path = AStarSearch.search(this, getPlayerPoint(), dests.first());
		
		List<MapPoint> path = BFSearch.search(this, getPlayerPoint(), type);
		if(path == null || path.isEmpty())
			return null;
		
		System.out.println(getPlayerPoint() + "-> " + path);
		return getActionToNeighbor(path.get(0));
	}
	
	private Action getActionToNeighbor(MapPoint point)
	{
		final MapPoint player = getPlayerPoint();
		if(player.west().equals(point))
			return Action.West;
		else if(player.east().equals(point))
			return Action.East;
		else if(player.north().equals(point))
			return Action.North;
		else if(player.south().equals(point))
			return Action.South;
		throw new AssertionError();
		
		//		int deltaX = point.x - player.x;
		//		int deltaY = point.y - player.y;
		//		assert (deltaX == 0) != (deltaY == 0);
		//		if(deltaX == 0)
		//		{
		//			if(deltaY > 0)
		//				return Action.South;
		//			else
		//				return Action.North;
		//		}
		//		else if(deltaX > 0)
		//			return Action.East;
		//		else
		//			return Action.West;
	}
	
	public Action discoveryChannel(Action lastMove, int keyCount)
	{
		if(canExplore(lastMove))
			return lastMove;
		
		List<MapPoint> path = BFSearch.search(this, getPlayerPoint(), null);
		if(path == null || path.isEmpty())
			return null;
		return getActionToNeighbor(path.get(0));
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
