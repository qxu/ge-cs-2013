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
		
		for(MapPath path : paths)
		{
			Action move = getPathAction(path);
			if(desirableEndResult(start, move))
				return move;
		}
		
		return getPathAction(paths.iterator().next());
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
		
		for(Action move : moves)
		{
			if(desirableEndResult(start, move))
				return move;
		}
		
		if(moves.contains(lastMove))
			return lastMove;
		
		System.out.println("not sure which");
		return moves.iterator().next();
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
