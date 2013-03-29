package com.csc2013;

import java.util.EnumSet;
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

	private MapPoint player;
	
	private final PlayerMapDebugger debugger;
	
	public PlayerMap()
	{
		this(0, 0);
	}
	
	public PlayerMap(int x, int y)
	{
		this.grid = new HashMap<>();
		this.typeMap = new HashMap<>();
		this.player = new MapPoint(0, 0, this);
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
		this.player = this.player.execute(move);
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
	
	public MapPoint getPlayerPoint()
	{
		return this.player;
	}
	
	/* Important stuff */
	
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
	
	public Action actionTo(BoxType type)
	{
		Set<MapPoint> destPoints = find(type);
		if(destPoints.isEmpty())
			return null;
		
		if(destPoints.size() == 1) // use optimized AStar algorithm
			return aStarAction(destPoints.iterator().next());
		
		Set<MapPath> paths = BFSearch.search(getPlayerPoint(), type, false);
		
		if(paths.isEmpty())
			return null;
		
		for(MapPath path : paths)
		{
			Action move = getPathAction(path);
			if(desirableEndResult(move))
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
	
	private Action aStarAction(MapPoint dest)
	{
		MapPath path = AStarSearch.search(getPlayerPoint(), dest);
		return getPathAction(path);
	}
	
	public Action discoveryChannel(Action lastMove, int keyCount)
	{
//		if(lastMove != null && desirableEndResult(lastMove))
//			return lastMove;
		MapPoint player = getPlayerPoint();
		System.out.println(player.east());
		
		
		Set<MapPath> paths = BFSearch.search(getPlayerPoint(), null,
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
			if(desirableEndResult(move))
				return move;
		}
		
		if(moves.contains(lastMove))
			return lastMove;
		
		System.out.println("not sure which");
		return moves.iterator().next();
	}
	
	private boolean desirableEndResult(Action move)
	{
		MapPoint cur = getPlayerPoint();
		if(move == Action.Pickup)
		{
			return cur.getType() == BoxType.Key;
		}
		while(cur.getType() != BoxType.Blocked && cur.getType() != null)
		{
			MapPoint next = cur.execute(move);
			if(next.equals(cur))
				return false;
			cur = next;
		}
		return cur.getType() == null;
	}
}
