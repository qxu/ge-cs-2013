package com.csc2013;

import java.util.EnumSet;
import java.util.Set;

import com.csc2013.DungeonMaze.Action;
import com.csc2013.DungeonMaze.BoxType;

/**
 * Algorithms to get the best move.
 * 
 * @author jqx
 */
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
	public static Action actionTo(PlayerMap map, Action lastMove,
			MapPoint start,
			BoxType dest)
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
		
		return chooseBestMove(start, lastMove, moves);
	}
	
	/*
	 * Returns the Action to execute the path.
	 */
	private static Action getPathAction(MapPath path)
	{
		MapPoint player = path.toList().get(0);
		MapPoint point = path.getStepPath().getLastPoint();
		if(player.equals(point))
			return Action.Pickup;
		return player.actionTo(point);
	}
	
	/*
	 * Returns the action to get from a starting point to a destination point
	 * using the a* algorithm.
	 */
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
	 * @return the best action
	 */
	public static Action discoveryChannel(MapPoint start, Action lastMove,
			int keyCount)
	{
		// null is an unknown
		Set<MapPath> paths = BFSearch.search(start, null, keyCount);
		
		if(paths.isEmpty())
			return null;
		
		Set<Action> moves = EnumSet.noneOf(Action.class);
		for(MapPath path : paths)
		{
			moves.add(getPathAction(path));
		}
		
		return chooseBestMove(start, lastMove, moves);
	}
	
	/*
	 * Chooses the best move from a set of moves.
	 */
	private static Action chooseBestMove(MapPoint start, Action lastMove,
			Set<Action> moves)
	{
		// continue in the same direction, if possible
		// this way, we explore edges and corners efficiently
		// and we avoid the "zigzag effect"
		if(moves.contains(lastMove))
			return lastMove;
		
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
				return desirable.iterator().next();
			else
			{
				// prefer east and south to other directions.
				if(desirable.contains(Action.East))
					return Action.East;
				else if(desirable.contains(Action.South))
					return Action.South;
				else
					return desirable.iterator().next();
			}
		}
		else
		{
			// like above, prefer east and south to other directions.
			if(moves.contains(Action.East))
				return Action.East;
			else if(moves.contains(Action.South))
				return Action.South;
			else
				return moves.iterator().next();
		}
	}
	
	/*
	 * Finds whether continuing with the given move starting from the start
	 * point is good or not.
	 */
	private static boolean desirableEndResult(MapPoint start, Action move)
	{
		MapPoint cur = start;
		if(move == Action.Pickup)
			return cur.getType() == BoxType.Key;
		
		// this loop gets the result BoxType after continuing to
		// travel in 'move' until it hits
		while(canTravelOn(cur.getType()))
		{
			MapPoint next = cur.execute(move);
			if(next.equals(cur))
				return false;
			cur = next;
		}
		
		// if the end result is unknown, then more space is explored.
		return cur.getType() == null;
	}
	
	private static boolean canTravelOn(BoxType type)
	{
		// BoxTypes Open and Key can be traveled on.
		return type == BoxType.Open || type == BoxType.Key;
	}
}
