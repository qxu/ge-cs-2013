package com.csc2013;

import java.awt.Color;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.csc2013.DungeonMaze.BoxType;

public class BFSearch
{
	public static Set<MapPath> search(PlayerMap ref, MapPoint start,
			BoxType dest, boolean hasKey)
	{
		if(dest != null && !ref.contains(dest))
			return Collections.emptySet();
		
		Set<MapPoint> closed = new HashSet<>();
		Map<MapPath, Integer> open = new HashMap<>();
		
		open.put(new MapPath(start), 0);
		
		Set<MapPath> found = new HashSet<>();
		Integer foundG = null;
		while(!open.isEmpty())
		{
			Entry<MapPath, Integer> curEntry = getShortestPath(open);
			MapPath cur = curEntry.getKey();
			
			MapPoint curPoint = cur.getLastPoint();
			ref.getDebugger().markPoint(curPoint, Color.MAGENTA);
			ref.getDebugger().markPath(cur, Color.DARK_GRAY);
			
			if(ref.get(curPoint) == dest)
			{
				if(foundG == null || curEntry.getValue().compareTo(foundG) <= 0)
				{
					ref.getDebugger().markPoint(curPoint, Color.GREEN);
					ref.getDebugger().markPath(cur, Color.GREEN);
					ref.getDebugger().waitForMarks(20);
					
					found.add(cur);
					foundG = curEntry.getValue();
				}
			}
			
			open.remove(cur);
			closed.add(curPoint);
			
			if(found.isEmpty())
			{
				for(MapPoint neighbor : getNeighbors(ref, curPoint, dest,
						hasKey))
				{
					if(closed.contains(neighbor))
					{
						continue;
					}
					MapPath subPath = cur.subPath(neighbor);
					if(!open.containsValue(subPath))
					{
						int gScore = curEntry.getValue() + distanceTo(ref,
								neighbor);
						open.put(subPath, gScore);
						
						ref.getDebugger().markPoint(neighbor, Color.PINK);
						ref.getDebugger().waitForMarks(0);
					}
				}
			}
			
			if(ref.get(curPoint) != dest)
			{
				ref.getDebugger().unmarkPath(cur);
				ref.getDebugger().markPoint(curPoint, Color.LIGHT_GRAY);
			}
		}
		
		ref.getDebugger().unmarkAllPoints();
		ref.getDebugger().unmarkAllPaths();
		return found;
	}
	
	private static int distanceTo(PlayerMap ref, MapPoint point)
	{
		if(ref.get(point) == BoxType.Door)
			return 2;
		else
			return 1;
	}
	
	private static Entry<MapPath, Integer> getShortestPath(
			Map<MapPath, Integer> map)
	{
		Iterator<Entry<MapPath, Integer>> iter = map.entrySet().iterator();
		Entry<MapPath, Integer> minEntry = iter.next();
		Integer min = minEntry.getValue();
		while(iter.hasNext())
		{
			Entry<MapPath, Integer> nextEntry = iter.next();
			Integer next = nextEntry.getValue();
			if(next.compareTo(min) < 0)
			{
				minEntry = nextEntry;
				min = next;
			}
		}
		return minEntry;
	}
	
	private static Iterable<MapPoint> getNeighbors(PlayerMap ref,
			MapPoint point, BoxType dest, boolean hasKey)
	{
		Collection<MapPoint> neighbors = new HashSet<>(4);
		for(MapPoint neighbor : point.getNeighbors())
		{
			BoxType type = ref.get(neighbor);
			if(type == dest
					|| (type == BoxType.Open)
					|| (hasKey && type == BoxType.Door))
			{
				neighbors.add(neighbor);
			}
		}
		return neighbors;
	}
}
