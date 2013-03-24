package com.csc2013;

import java.awt.Color;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import com.csc2013.DungeonMaze.BoxType;

public class AStarSearch
{
	public static Set<MapPath> search(PlayerMap ref, MapPoint start,
			final MapPoint dest)
	{
		Map<MapPoint, Integer> gScores = new HashMap<>();
		final Map<MapPoint, Integer> fScores = new HashMap<>();
		
		Set<MapPoint> closed = new HashSet<>();
		Queue<MapPath> open = new PriorityQueue<>(11,
				new Comparator<MapPath>()
				{
					@Override
					public int compare(MapPath p1, MapPath p2)
					{
						return fScores.get(p1.getLastPoint()).compareTo(
								fScores.get(p2.getLastPoint()));
					}
				});
		
		open.add(new MapPath(start));
		gScores.put(start, 0);
		fScores.put(start, start.distanceTo(dest));
		
		Set<MapPath> found = new HashSet<>();
		while(!open.isEmpty())
		{
			MapPath cur = open.remove();
			MapPoint curPoint = cur.getLastPoint();
			
			ref.getDebugger().markPoint(cur.getLastPoint(), Color.MAGENTA);
			ref.getDebugger().markPath(cur, Color.DARK_GRAY);
			
			if(curPoint.equals(dest))
			{
				found.add(cur);
				ref.getDebugger().markPoint(curPoint, Color.GREEN);
				ref.getDebugger().markPath(cur, Color.GREEN);
				SchoolPlayerDebugger.sleep(20);
				
				ref.getDebugger().unmarkAllPoints();
				ref.getDebugger().unmarkAllPaths();
				ref.getDebugger().stringUnmarkAll();
				return found;
			}
			
			closed.add(curPoint);
			
			for(MapPoint neighbor : getNeighbors(ref, cur.getLastPoint(), dest))
			{
				int tempG = gScores.get(curPoint) + distanceTo(ref, neighbor);
				if(closed.contains(neighbor))
				{
					if(tempG >= gScores.get(neighbor))
					{
						continue;
					}
				}
				MapPath subPath = cur.subPath(neighbor);
				if(!open.contains(subPath))
				{
					gScores.put(neighbor, tempG);
					int tempH = heuristicEstimate(ref, neighbor, dest);
					fScores.put(neighbor, tempG + tempH);
					open.add(subPath);
					
					ref.getDebugger().markPoint(neighbor, Color.PINK);
					ref.getDebugger().stringMark(neighbor,
							String.valueOf(tempG + tempH));
					SchoolPlayerDebugger.sleep(5);
				}
			}
			
			if(!curPoint.equals(dest))
			{
				ref.getDebugger().unmarkPath(cur);
				ref.getDebugger().markPoint(curPoint, Color.LIGHT_GRAY);
			}
		}
		
		ref.getDebugger().unmarkAllPoints();
		ref.getDebugger().unmarkAllPaths();
		ref.getDebugger().stringUnmarkAll();
		return found;
	}
	
	private static int heuristicEstimate(PlayerMap ref, MapPoint point,
			MapPoint dest)
	{
		return point.distanceTo(dest);
	}
	
	private static int distanceTo(PlayerMap ref, MapPoint point)
	{
		return 1;
	}
	
	private static Iterable<MapPoint> getNeighbors(PlayerMap ref,
			MapPoint point, MapPoint dest)
	{
		Collection<MapPoint> neighbors = new HashSet<>(4);
		for(MapPoint neighbor : point.getNeighbors())
		{
			if(neighbor.equals(dest) || (ref.get(neighbor) == BoxType.Open))
			{
				neighbors.add(neighbor);
			}
		}
		return neighbors;
	}
}
