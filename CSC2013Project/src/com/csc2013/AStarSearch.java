package com.csc2013;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import com.csc2013.DungeonMaze.BoxType;

public class AStarSearch
{
	public static List<MapPoint> search(PlayerMap ref, MapPoint start,
			final MapPoint dest)
	{
		Map<MapPoint, Integer> gScores = new HashMap<>();
		final Map<MapPoint, Integer> fScores = new HashMap<>();
		Map<MapPoint, MapPoint> cameFrom = new HashMap<>();
		
		Set<MapPoint> closed = new HashSet<>();
		Queue<MapPoint> open = new PriorityQueue<>(11,
				new Comparator<MapPoint>()
				{
					@Override
					public int compare(MapPoint p1, MapPoint p2)
					{
						return fScores.get(p1).compareTo(fScores.get(p2));
					}
				});
		
		open.add(start);
		gScores.put(start, 0);
		fScores.put(start, start.distanceTo(dest));
		
		while(!open.isEmpty())
		{
			MapPoint cur = open.remove();
			
			if(cur.equals(dest))
			{
				List<MapPoint> path = new ArrayList<>();
				for(MapPoint point = cur; point != null && cameFrom
						.containsKey(point); point = cameFrom.get(point))
				{
					path.add(0, point);
				}
				return path;
			}
			
			closed.add(cur);
			
			for(MapPoint neighbor : getNeighbors(ref, cur, dest))
			{
				int tempG = gScores.get(cur) + distanceTo(ref, neighbor);
				if(closed.contains(neighbor))
				{
					if(tempG >= gScores.get(neighbor))
					{
						continue;
					}
				}
				if(!open.contains(neighbor))
				{
					cameFrom.put(neighbor, cur);
					gScores.put(neighbor, tempG);
					int tempH = heuristicEstimate(ref, neighbor, dest);
					fScores.put(neighbor, tempG + tempH);
					open.add(neighbor);
				}
			}
		}
		
		return null;
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
