package com.csc2013;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.csc2013.DungeonMaze.BoxType;
import com.csc2013.PlayerMap.MapPoint;

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
	static final double NEIGHBOR_SEARCH_DELAY = 4;
	static final double PATH_SEARCH_DELAY = 4;
	static final double FOUND_DEST_MARK_DELAY = 20;
	
	@SuppressWarnings("unused")
	public static Set<MapPath> search(MapPoint start, BoxType dest, boolean hasKey)
	{
//		if(dest != null && !ref.contains(dest))
//			return Collections.emptySet();
		SchoolPlayerDebugger debugger = SchoolPlayerDebugger.getLatestInstance();
		
		
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
			
			if(PATH_SEARCH_DELAY > 0)
			{
				debugger.markPoint(curPoint, Color.MAGENTA);
				debugger.markPath(cur, Color.DARK_GRAY);
				debugger.waitForMarks(PATH_SEARCH_DELAY);
			}
			
			if(curPoint.getType() == dest)
			{
				if(foundG == null || curEntry.getValue().compareTo(foundG) <= 0)
				{
					if(FOUND_DEST_MARK_DELAY > 0)
					{
						debugger.markPoint(curPoint, Color.GREEN);
						debugger.markPath(cur, Color.GREEN);
						debugger.waitForMarks(FOUND_DEST_MARK_DELAY);
					}

					found.add(cur);
					foundG = curEntry.getValue();
				}
			}
			
			open.remove(cur);
			gScores.remove(cur);
			closed.add(curPoint);
			
			if(found.isEmpty())
			{
				for(MapPoint neighbor : getNeighbors(curPoint, dest, hasKey))
				{
					if(paused)
					{
						while(paused)
						{
							debugger.sleep(200);
						}
					}
					
					if(closed.contains(neighbor))
					{
						continue;
					}
					
					MapPath subPath = cur.subPath(neighbor);
					
					MapPath samePath = open.get(subPath);
					int gScore = curEntry.getValue() + distanceTo(neighbor);
					if(samePath == null)
					{
						open.put(subPath, subPath);
						gScores.put(subPath, gScore);
						
						if(NEIGHBOR_SEARCH_DELAY > 0)
						{
							debugger.markPoint(neighbor, Color.PINK);
							debugger.waitForMarks(NEIGHBOR_SEARCH_DELAY);
						}
					}
					else
					{
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
			
			if(curPoint.getType() != dest)
			{
				if(PATH_SEARCH_DELAY > 0)
				{
					debugger.unmarkPath(cur);
					debugger.markPoint(curPoint, Color.LIGHT_GRAY);
				}
			}
		}
		
		debugger.unmarkAllPoints();
		debugger.unmarkAllPaths();
		return found;
	}
	
	private static int distanceTo(MapPoint point)
	{
		if(point.getType() == BoxType.Door)
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
	
	private static Iterable<MapPoint> getNeighbors(MapPoint point, BoxType dest, boolean hasKey)
	{
		Collection<MapPoint> neighbors = new HashSet<>(4);
		for(MapPoint neighbor : point.getNeighbors())
		{
			BoxType type = neighbor.getType();
			if(type == dest
					|| (type == BoxType.Open)
					|| (type == BoxType.Door && hasKey)
					|| (type == BoxType.Key))
			{
				neighbors.add(neighbor);
			}
		}
		return neighbors;
	}
}
