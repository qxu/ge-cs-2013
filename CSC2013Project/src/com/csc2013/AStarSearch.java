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
	private static volatile boolean paused = false;

	public static void pause()
	{
		paused = true;
	}
	
	public static void resume()
	{
		paused = false;
	}
	
	/*
	 * All delays are in milliseconds.
	 */
	private static final double NEIGHBOR_SEARCH_DELAY = 0;
	private static final double PATH_SEARCH_DELAY = 0.2;
	private static final double FOUND_DEST_MARK_DELAY = 0;

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
			ref.getDebugger().waitForMarks(PATH_SEARCH_DELAY);
			
			if(curPoint.equals(dest))
			{
				found.add(cur);
				ref.getDebugger().markPoint(curPoint, Color.GREEN);
				ref.getDebugger().markPath(cur, Color.GREEN);
				ref.getDebugger().waitForMarks(FOUND_DEST_MARK_DELAY);
				
				ref.getDebugger().unmarkAllPoints();
				ref.getDebugger().unmarkAllPaths();
				ref.getDebugger().stringUnmarkAll();
				return found;
			}
			
			closed.add(curPoint);
			
			if(found.isEmpty())
			{
				for(MapPoint neighbor : getNeighbors(ref, cur.getLastPoint(),
						dest))
				{
					int gScore = gScores.get(curPoint) + distanceTo(ref,
							neighbor);
					if(closed.contains(neighbor))
					{
						if(gScore >= gScores.get(neighbor))
						{
							continue;
						}
					}
					MapPath subPath = cur.subPath(neighbor);
					if(!open.contains(subPath))
					{
						gScores.put(neighbor, gScore);
						int hScore = heuristicEstimate(ref, neighbor, dest);
						fScores.put(neighbor, gScore + hScore);
						open.add(subPath);
						
						ref.getDebugger().markPoint(neighbor, Color.PINK);
						ref.getDebugger().stringMark(neighbor,
								String.valueOf(gScore + hScore));
						ref.getDebugger().waitForMarks(NEIGHBOR_SEARCH_DELAY);
					}
				}
			}
			
			if(!curPoint.equals(dest))
			{
				ref.getDebugger().unmarkPath(cur);
				ref.getDebugger().markPoint(curPoint, Color.LIGHT_GRAY);
			}
			
			if(paused)
			{
				while(paused)
				{
					ref.getDebugger().sleep(200);
				}
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
