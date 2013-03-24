package com.csc2013;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import com.csc2013.DungeonMaze.BoxType;

public class BFSearch
{
	public static Set<MapPath> search(PlayerMap ref, MapPoint start,
			BoxType dest)
	{
		if(dest != null && !ref.contains(dest))
			return Collections.emptySet();
		
		Set<MapPoint> closed = new HashSet<>();
		Queue<MapPath> open = new ArrayDeque<>();
		Queue<MapPath> next = new ArrayDeque<>();
		
		open.add(new MapPath(start));
		
		Set<MapPath> found = new HashSet<>();
		while(!open.isEmpty())
		{
			MapPath cur = open.remove();
			MapPoint curPoint = cur.getLastPoint();
			ref.getDebugger().markPoint(curPoint, Color.MAGENTA);
			ref.getDebugger().markPath(cur, Color.DARK_GRAY);
			
			if(ref.get(curPoint) == dest)
			{
				ref.getDebugger().markPoint(curPoint, Color.GREEN);
				ref.getDebugger().markPath(cur, Color.GREEN);
				SchoolPlayerDebugger.sleep(8);
				found.add(cur);
			}
			
			closed.add(curPoint);
			
			if(found.isEmpty())
			{
				for(MapPoint neighbor : getNeighbors(ref, curPoint, dest))
				{
					if(closed.contains(neighbor))
					{
						continue;
					}
					MapPath subPath = cur.subPath(neighbor);
					if(!next.contains(subPath))
					{
						next.add(subPath);
						ref.getDebugger().markPoint(neighbor, Color.PINK);
						SchoolPlayerDebugger.sleep(2);
					}
				}
				
				if(open.isEmpty())
				{
					open = next;
					next = new ArrayDeque<>();
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
