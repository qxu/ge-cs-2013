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
			BoxType dest, boolean hasKey)
	{
		if(dest != null && !ref.contains(dest))
			return Collections.emptySet();
		
		Set<MapPoint> closed = new HashSet<>();
		Map<MapPath, MapPath> open = new HashMap<>();
		Map<MapPath, Integer> gScores = new HashMap<>();
		
		MapPath base = new MapPath(start);
		open.put(base, base);
		gScores.put(base, 0);
		
		Set<MapPath> found = new HashSet<>();
		Integer foundG = null;
		while(!open.isEmpty())
		{
			Entry<MapPath, Integer> curEntry = getShortestPath(gScores);
			MapPath cur = curEntry.getKey();
			
			MapPoint curPoint = cur.getLastPoint();
			ref.getDebugger().markPoint(curPoint, Color.MAGENTA);
			ref.getDebugger().markPath(cur, Color.DARK_GRAY);
			ref.getDebugger().waitForMarks(PATH_SEARCH_DELAY);

			if(ref.get(curPoint) == dest)
			{
				if(foundG == null || curEntry.getValue().compareTo(foundG) <= 0)
				{
					ref.getDebugger().markPoint(curPoint, Color.GREEN);
					ref.getDebugger().markPath(cur, Color.GREEN);
					ref.getDebugger().waitForMarks(FOUND_DEST_MARK_DELAY);

					found.add(cur);
					foundG = curEntry.getValue();
				}
			}
			
			open.remove(cur);
			gScores.remove(cur);
			closed.add(curPoint);
			
			if(found.isEmpty())
			{
				for(MapPoint neighbor : getNeighbors(ref, curPoint, dest,
						hasKey))
				{
					if(paused)
					{
						while(paused)
						{
							ref.getDebugger().sleep(200);
						}
					}
					
					if(closed.contains(neighbor))
					{
						continue;
					}
					
					MapPath subPath = cur.subPath(neighbor);
					
					MapPath samePath = open.get(subPath);
					if(samePath == null)
					{
						int gScore = curEntry.getValue() + distanceTo(ref,
								neighbor);
						open.put(subPath, subPath);
						gScores.put(subPath, gScore);
						
						ref.getDebugger().markPoint(neighbor, Color.PINK);
						ref.getDebugger().waitForMarks(NEIGHBOR_SEARCH_DELAY);
					}
					else
					{
						int gScore = curEntry.getValue() + distanceTo(ref,
							neighbor);
						if(subPath.getTurnCount() < samePath.getTurnCount()
								&& gScore <= gScores.get(samePath))
						{
							// samePath.equals(subPath)
							// so, need to explicitly remove samePath/subPath to re-map value
							open.remove(samePath);
							gScores.remove(samePath);
							open.put(subPath, subPath);
							gScores.put(subPath, gScore);
						}
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
