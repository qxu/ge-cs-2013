package com.csc2013;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.csc2013.DungeonMaze.Action;
import com.csc2013.DungeonMaze.BoxType;


public class PlayerMap
{
	private MapPoint player;
	private Map<MapPoint, MapPoint> grid;
	private Map<BoxType, Set<MapPoint>> typeMap;
	
	final PlayerMapDebugger debugger;
	
	public PlayerMap()
	{
		this(0, 0);
	}
	
	public PlayerMap(int x, int y)
	{
		reset();
		this.player = new MapPoint(0, 0);
		this.debugger = new PlayerMapDebugger(this);
	}
	
//	public void setDebugger(SchoolPlayerDebugger debug)
//	{
//		this.debugger = debug;
//	}
	
	private void reset()
	{
		this.grid = new HashMap<>();
		this.typeMap = new EnumMap<>(BoxType.class);
		for(BoxType type : BoxType.values())
		{
			this.typeMap.put(type, new HashSet<MapPoint>());
		}
	}

	public PlayerMapDebugger getDebugger()
	{
		return this.debugger;
	}
	
	public Set<MapPoint> getGrid()
	{
		return new HashSet<>(grid.keySet());
	}
	
	public void setPlayerPosition(int x, int y)
	{
		// TODO if getLocalized returns null
		this.player = getLocalized(x, y);
	}
	
	private MapPoint getLocalized(int x, int y)
	{
		return grid.get(new MapPoint(x, y));
	}
	
	public MapPoint set(BoxType type, int x, int y)
	{
		if(type == null)
			throw new NullPointerException();
		MapPoint localized = getLocalized(x, y);
		if(localized == null)
		{
			localized = new MapPoint(x, y);
			localized.init(type);
		}
		else if(localized.getType() == null)
		{
			localized.init(type);
		}
		else
		{
			localized.setType(type);
		}
		return localized;
	}
	
	public MapPoint getPlayerPoint()
	{
		return this.player;
	}
	
	/* Important stuff */
	
	public Set<MapPoint> find(BoxType type)
	{
		return typeMap.get(type);
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
		System.out.println(paths);
		
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
		
		System.out.println("not sure which o");
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
	
	public class MapPoint
	{
		public final int x;
		public final int y;
	
		private BoxType type;
		
		private MapPoint west;
		private MapPoint east;
		private MapPoint north;
		private MapPoint south;
		
		private boolean initialized;
		
		MapPoint(int x, int y)
		{
			this.x = x;
			this.y = y;
			this.initialized = false;
		}
		
		void init(BoxType type)
		{
			if(initialized)
				throw new IllegalStateException(this + " already initialized ");
			if(type == null)
				throw new NullPointerException();
			this.initialized = true;
			this.type = type;
			grid.remove(this);
			grid.put(this, this);
			Set<MapPoint> typeSet = typeMap.get(type);
			typeSet.add(this);
			if(this.west == null)
			{
				MapPoint point = getLocalized(this.x - 1, this.y);
				if(point == null)
				{
					point = new MapPoint(this.x - 1, this.y);
					grid.remove(point);
					grid.put(point, point);
				}
				point.east = this;
				this.west = point;
			}
			if(this.east == null)
			{
				MapPoint point = getLocalized(this.x + 1, this.y);
				if(point == null)
				{
					point = new MapPoint(this.x + 1, this.y);
					grid.remove(point);
					grid.put(point, point);
				}
				point.west = this;
				this.east = point;
			}
			if(this.north == null)
			{
				MapPoint point = getLocalized(this.x, this.y - 1);
				if(point == null)
				{
					point = new MapPoint(this.x, this.y - 1);
					grid.remove(point);
					grid.put(point, point);
				}
				point.south = this;
				this.north = point;
			}
			if(this.south == null)
			{
				MapPoint point = getLocalized(this.x, this.y + 1);
				if(point == null)
				{
					point = new MapPoint(this.x, this.y + 1);
					grid.remove(point);
					grid.put(point, point);
				}
				point.north = this;
				this.south = point;
			}
		}
		
		void setType(BoxType type)
		{
			BoxType oldType = getType();
			if(type != oldType)
			{
				typeMap.get(oldType).remove(this);
				typeMap.get(type).add(this);
				this.type = type;
			}
		}
		
		public BoxType getType()
		{
			return this.type;
		}
		
		public MapPoint west()
		{
			return west;
		}
		
		public MapPoint east()
		{
			return east;
		}
		
		public MapPoint north()
		{
			return north;
		}
		
		public MapPoint south()
		{
			return south;
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
}
