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
import com.csc2013.PlayerMap.MapPoint;

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
	static final double NEIGHBOR_SEARCH_DELAY = BFSearch.NEIGHBOR_SEARCH_DELAY;
	static final double PATH_SEARCH_DELAY = BFSearch.PATH_SEARCH_DELAY;
	static final double FOUND_DEST_MARK_DELAY = BFSearch.FOUND_DEST_MARK_DELAY;

	@SuppressWarnings("unused")
	public static MapPath search(MapPoint start, final MapPoint dest)
	{
		PlayerMapDebugger debugger = SchoolPlayer.getLatestInstance().map.debugger;
		
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
		
		while(!open.isEmpty())
		{
			MapPath cur = open.remove();
			MapPoint curPoint = cur.getLastPoint();
			
			if(PATH_SEARCH_DELAY > 0)
			{
				debugger.markPoint(cur.getLastPoint(), Color.MAGENTA);
				debugger.markPath(cur, Color.DARK_GRAY);
				debugger.waitForMarks(PATH_SEARCH_DELAY);
			}
			
			if(curPoint.equals(dest))
			{
				if(FOUND_DEST_MARK_DELAY > 0)
				{
					debugger.markPoint(curPoint, Color.GREEN);
					debugger.markPath(cur, Color.GREEN);
					debugger.waitForMarks(FOUND_DEST_MARK_DELAY);
				}
				
				debugger.unmarkAllPoints();
				debugger.unmarkAllPaths();
				debugger.stringUnmarkAll();
				return cur; //TODO fix add paths
			}
			
			closed.add(curPoint);
			
			for(MapPoint neighbor : getNeighbors(cur.getLastPoint(), dest))
			{
				if(paused)
				{
					while(paused)
					{
						debugger.sleep(200);
					}
				}
				
				int gScore = gScores.get(curPoint) + distanceTo(neighbor);
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
					int hScore = heuristicEstimate(neighbor, dest);
					fScores.put(neighbor, gScore + hScore);
					open.add(subPath);
					
					if(NEIGHBOR_SEARCH_DELAY > 0)
					{
						debugger.markPoint(neighbor, Color.PINK);
						debugger.stringMark(neighbor,
								String.valueOf(gScore + hScore));
						debugger.waitForMarks(NEIGHBOR_SEARCH_DELAY);
					}
				}
			}
			
			if(!curPoint.equals(dest))
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
		debugger.stringUnmarkAll();
		return null;
	}
	
	private static int heuristicEstimate(MapPoint point, MapPoint dest)
	{
		return point.distanceTo(dest);
	}
	
	private static int distanceTo(MapPoint point)
	{
		return 1;
	}
	
	private static Iterable<MapPoint> getNeighbors(MapPoint point, MapPoint dest)
	{
		Collection<MapPoint> neighbors = new HashSet<>(4);
		for(MapPoint neighbor : point.getNeighbors())
		{
			BoxType type = neighbor.getType();
			if(neighbor.equals(dest)
					|| (type == BoxType.Open)
					|| (type == BoxType.Key))
			{
				neighbors.add(neighbor);
			}
		}
		return neighbors;
	}
}
