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
	
	/**
	 * Halts the search algorithm. The algorithm will not start/resume
	 * until {@code AStarSearch.resume()} is called. This is useful for
	 * debugging purposes.
	 */
	public static void pause()
	{
		paused = true;
	}
	
	/**
	 * Used to resume the search algorithm to normal operation. This is
	 * useful for debugging purposes.
	 */
	public static void resume()
	{
		paused = false;
	}
	
	/*
	 * All delays are in milliseconds.
	 */
	static final double NEIGHBOR_SEARCH_DELAY = BFSearch.NEIGHBOR_SEARCH_DELAY;
	static final double PATH_SEARCH_DELAY = BFSearch.PATH_SEARCH_DELAY;
	static final double FINAL_DELAY = BFSearch.FINAL_DELAY + BFSearch.FOUND_DEST_MARK_DELAY;

	/**
	 * Calculates the optimal path from {@code MapPoint} start to
	 * {@code MapPoint} dest. The search algorithm used is the A* search
	 * algorithm.
	 * 
	 * @param start the starting point
	 * @param dest the destination point
	 * @return the optimal path
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/A*_search_algorithm">A* search algorithm - Wikipedia</a>
	 */
	public static MapPath search(MapPoint start, final MapPoint dest)
	{
		PlayerMapDebugger debugger = SchoolPlayer.getLatestDebugger();
		
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
			
			debugger.markPoint(cur.getLastPoint(), Color.MAGENTA);
				debugger.markPath(cur, Color.DARK_GRAY);
				debugger.waitForMarks(PATH_SEARCH_DELAY);
			
			if(curPoint.equals(dest))
			{
				debugger.markPoint(curPoint, Color.GREEN);
				debugger.markPath(cur, Color.GREEN);
				debugger.waitForMarks(FINAL_DELAY);
				
				debugger.unmarkAllPoints();
				debugger.unmarkAllPaths();
				debugger.stringUnmarkAll();
				return cur; //TODO fix add paths
			}
			
			closed.add(curPoint);

			while(paused)
			{
				debugger.sleep(200);
			}
			
			for(MapPoint neighbor : getNeighbors(cur.getLastPoint(), dest))
			{
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
					
					debugger.markPoint(neighbor, Color.PINK);
					debugger.stringMark(neighbor,
							String.valueOf(gScore + hScore));
					debugger.waitForMarks(NEIGHBOR_SEARCH_DELAY);
				}
			}
			
			debugger.unmarkPath(cur);
			debugger.markPoint(curPoint, Color.LIGHT_GRAY);
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
			if(neighbor != null)
			{
				BoxType type = neighbor.getType();
				if(neighbor.equals(dest)
						|| (type == BoxType.Open)
						|| (type == BoxType.Key))
				{
					neighbors.add(neighbor);
				}
			}
		}
		return neighbors;
	}
}
