package com.csc2013;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.csc2013.DungeonMaze.BoxType;

public class BFSearch
{
	public static List<MapPoint> search(PlayerMap ref, MapPoint start,
			BoxType dest)
	{
		if(dest != null && !ref.contains(dest))
			return null;
		
		Queue<MapPoint> open = new ArrayDeque<>();
		Set<MapPoint> closed = new HashSet<>();
		Map<MapPoint, MapPoint> cameFrom = new HashMap<>();
		
		open.add(start);
		
		while(!open.isEmpty())
		{
			SchoolPlayerDebugger.sleep(20);
			
			MapPoint cur = open.remove();
			ref.getDebugger().mark(cur, Color.BLUE);
			
			if(ref.get(cur) == dest)
			{
				ref.getDebugger().mark(cur, Color.GREEN);
				List<MapPoint> path = new ArrayList<>();
				for(MapPoint point = cur; point != null && cameFrom
						.containsKey(point); point = cameFrom.get(point))
				{
					path.add(0, point);
				}
				
				SchoolPlayerDebugger.sleep(200);
				ref.getDebugger().unmarkAll();
				return path;
			}
			closed.add(cur);
			for(MapPoint neighbor : getNeighbors(ref, cur, dest))
			{
				if(closed.contains(neighbor))
				{
					continue;
				}
				if(!open.contains(neighbor))
				{
					cameFrom.put(neighbor, cur);
					open.add(neighbor);
					ref.getDebugger().mark(neighbor, Color.RED);
				}
			}
		}
		ref.getDebugger().unmarkAll();
		throw new AssertionError();
	}
	
	private static Iterable<MapPoint> getNeighbors(PlayerMap ref,
			MapPoint point, BoxType dest)
	{
		Collection<MapPoint> neighbors = new HashSet<>(4);
		for(MapPoint neighbor : point.getNeighbors())
		{
			if(ref.get(neighbor) == dest || (ref.get(neighbor) == BoxType.Open))
			{
				neighbors.add(neighbor);
			}
		}
		return neighbors;
	}
}
