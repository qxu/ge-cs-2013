package com.csc2013;

import java.util.EnumSet;
import java.util.Set;

import com.csc2013.DungeonMaze.Action;
import com.csc2013.DungeonMaze.BoxType;

public class ActionAlgorithms
{
	/**
	 * Gets the best action to the closest {@code BoxType} destination.
	 * 
	 * @param map
	 * @param start
	 * @param dest
	 * @return
	 */
	public static Action actionTo(PlayerMap map, MapPoint start, BoxType dest)
	{
		Set<MapPoint> destPoints = map.find(dest);
		if(destPoints.isEmpty())
			return null;
		
		if(destPoints.size() == 1) // use optimized AStar algorithm
			return aStarAction(start, destPoints.iterator().next());
		
		Set<MapPath> paths = BFSearch.search(start, dest, 0);
		
		if(paths.isEmpty())
			return null;
		
		Set<Action> moves = EnumSet.noneOf(Action.class);
		for(MapPath path : paths)
		{
			moves.add(getPathAction(path));
		}

		Set<Action> desirable = EnumSet.noneOf(Action.class);
		
		for(Action move : moves)
		{
			if(desirableEndResult(start, move))
			{
				desirable.add(move);
			}
		}
		
		if(!desirable.isEmpty())
		{
			if(desirable.size() == 1)
			{
				return desirable.iterator().next();
			}
			else
			{
				System.out.println("not sure which desiarable");
				return desirable.iterator().next();
			}
		}
		else
		{
			System.out.println("not sure which");
			return moves.iterator().next();
		}
	}
	
	private static Action getPathAction(MapPath path)
	{
		final MapPoint player = path.toList().get(0);
		final MapPoint point = path.getStepPath().getLastPoint();
		if(player.equals(point))
			return Action.Pickup;
		return player.actionTo(point);
	}
	
	private static Action aStarAction(MapPoint start, MapPoint dest)
	{
		MapPath path = AStarSearch.search(start, dest);
		return getPathAction(path);
	}
	
	/**
	 * Finds the best action to explore the unknown.
	 * 
	 * @param start
	 * @param lastMove
	 * @param keyCount
	 * @return
	 */
	public static Action discoveryChannel(MapPoint start, Action lastMove, int keyCount)
	{
		Set<MapPath> paths = BFSearch.search(start, null, keyCount);
		
		if(paths.isEmpty())
			return null;
		
		Set<Action> moves = EnumSet.noneOf(Action.class);
		for(MapPath path : paths)
		{
			moves.add(getPathAction(path));
		}

		if(moves.contains(lastMove))
		{
			return lastMove;
		}
		
		Set<Action> desirable = EnumSet.noneOf(Action.class);
		
		for(Action move : moves)
		{
			if(desirableEndResult(start, move))
			{
				desirable.add(move);
			}
		}
		
		if(!desirable.isEmpty())
		{
			if(desirable.size() == 1)
			{
				return desirable.iterator().next();
			}
			else if(desirable.contains(lastMove))
			{
				return lastMove;
			}
			else
			{
				System.out.println("not sure which desiarable");
				return desirable.iterator().next();
			}
		}
		else
		{
			System.out.println("not sure which");
			return moves.iterator().next();
		}
	}
	
	private static int endDistance(MapPoint start, Action move)
	{
		int distance = 0;
		MapPoint cur = start;
		while(cur.getType() != BoxType.Blocked && cur.getType() != null)
		{
			MapPoint next = cur.execute(move);
			cur = next;
			++distance;
		}
		return distance;
	}
	
	private static boolean desirableEndResult(MapPoint start, Action move)
	{
		MapPoint cur = start;
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
