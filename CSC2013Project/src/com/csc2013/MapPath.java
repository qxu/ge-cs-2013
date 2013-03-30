package com.csc2013;

import java.util.Arrays;
import java.util.List;

import com.csc2013.DungeonMaze.Action;

public class MapPath
{
	private final MapPath superPath;
	private final MapPoint last;
	
	private final int numOfPoints;
	
	private final Action prevMove;
	private final int turnCount;
	
	// TODO: implement these
	private final int numOfSteps = 0;
	private final int keyCount = 0;
	
	/**
	 * Constructs a base path from the given point.
	 * 
	 * @param the
	 *            starting point
	 * @throws NullPointerException
	 *             if the point given is null
	 */
	public MapPath(MapPoint start)
	{
		if(start == null)
			throw new NullPointerException();
		
		this.superPath = null;
		this.last = start;
		
		this.numOfPoints = 1;
		
		this.prevMove = null;
		this.turnCount = 0;
	}
	
	/*
	 * Constructs a sub-path with the given super-path and the given sub-point.
	 * Note that this constructor is private and sub-paths should be created
	 * with the subPath method.
	 */
	private MapPath(MapPath superPath, MapPoint subPoint)
	{
		this.superPath = superPath;
		this.last = subPoint;
		
		this.numOfPoints = superPath.numOfPoints + 1;
		
		MapPoint prev = superPath.getLastPoint();
		Action prevMove = prev.actionTo(subPoint);
		this.prevMove = prevMove;
		if(superPath.prevMove == null)
		{
			this.turnCount = superPath.turnCount;
		}
		else
		{
			this.turnCount = (superPath.prevMove == prevMove)
					? superPath.turnCount
					: superPath.turnCount + 1;
		}
	}
	
	/**
	 * Constructs and returns a sub-path with this path as the super-path and
	 * the last point as the sub-point.
	 * 
	 * @param last
	 * @return
	 */
	public MapPath subPath(MapPoint last)
	{
		if(last == null)
			throw new NullPointerException();
		return new MapPath(this, last);
	}
	
	/**
	 * Returns the last point of this path.
	 * 
	 * @return the last point
	 */
	public MapPoint getLastPoint()
	{
		return this.last;
	}
	
	/**
	 * Returns the number of points in this path.
	 * 
	 * @return the length of this path
	 */
	public int length()
	{
		return this.numOfPoints;
	}
	
	/**
	 * Returns the number of switches in direction from the start point
	 * iterating to the last point.
	 * 
	 * @return the turn count
	 */
	public int getTurnCount()
	{
		return this.turnCount;
	}
	
	/**
	 * A base path is a path with only one point. This path was not created with
	 * the method {@code subPath}
	 * 
	 * @return {@code true} if this path is a base path, {@code false} otherwise
	 */
	public boolean isBasePath()
	{
		return length() == 1;
	}
	
	/**
	 * Returns the path where the first step was taken. If this path has not
	 * taken any steps (ie. it is a base path), then it returns itself.
	 * 
	 * @return the step path
	 */
	public MapPath getStepPath()
	{
		MapPath prevPath = this;
		for(int i = length() - 1; i >= 2; --i)
		{
			prevPath = prevPath.superPath;
		}
		return prevPath;
	}
	
	/**
	 * Returns a list view of this path. This list contains all of the points of
	 * this path in order from start to last.
	 * 
	 * @return the list
	 */
	public List<MapPoint> toList()
	{
		int length = length();
		MapPoint[] points = new MapPoint[length];
		
		MapPath nextPath = this;
		for(int i = length - 1; i >= 0; --i)
		{
			points[i] = nextPath.getLastPoint();
			nextPath = nextPath.superPath;
		}
		return Arrays.asList(points);
	}
	
	/**
	 * Returns the hash code. The hash code is similar to {@code MapPath.equals}
	 * in that it only considers the first point, second point, and the last
	 * point.
	 * 
	 * @return the hash code for this path
	 */
	@Override
	public int hashCode()
	{
		int length = length();
		if(length == 1)
			return getLastPoint().hashCode();
		else if(length == 2)
			return (this.superPath.hashCode() * 31) + getLastPoint().hashCode();
		else
			return (getStepPath().hashCode() * 31) + getLastPoint().hashCode();
	}
	
	/**
	 * Tests two paths for equality. Two paths are considered equal if they have
	 * equal starting points, equal second points, and equal last points. This
	 * is because those three values are the most significant parts of a MapPath
	 * given that the first two points will determine the first move taken, and
	 * considering the middle points will slow down many path-finding
	 * algorithms. This method follows the contract defined in
	 * {@code Object.equals}.
	 * 
	 * @see Object.equals
	 */
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof MapPath))
			return false;
		MapPath path = (MapPath)o;
		int length = length();
		if(isBasePath())
			return path.isBasePath()
					&& getLastPoint().equals(path.getLastPoint());
		else if(length == 2)
			return path.length() == length
					&& this.superPath.getLastPoint().equals(
							path.superPath.getLastPoint())
					&& getLastPoint().equals(path.getLastPoint());
		else
			return getStepPath().equals(path.getStepPath())
					&& getLastPoint().equals(path.getLastPoint());
	}
	
	/**
	 * Returns a string representation of this path. The string is of the form:
	 * <blockquote>
	 * 
	 * <pre>
	 * point[0]->point[1] .. point[n - 1]
	 * </pre>
	 * 
	 * </blockquote> where {@code point[i]} denotes the i'th point of the path
	 * and {@code n} is the length of this path. The string is also shortened if
	 * the path is too long, omitting the middle values since they are not as
	 * useful as the others.
	 * 
	 * @return the string representation of this path
	 */
	@Override
	public String toString()
	{
		final int beginningLength = 2;
		final int endLength = 1;
		
		int length = length();
		if(length < 2)
			return this.last.toString();
		
		List<MapPoint> path = toList();
		StringBuilder sb = new StringBuilder();
		if(length > beginningLength + endLength)
		{
			for(int i = 0; i < beginningLength; ++i)
			{
				sb.append(path.get(i)).append("->");
			}
			sb.append(" .. ");
			for(int i = length - endLength; i < length - 1; ++i)
			{
				sb.append(path.get(i)).append("->");
			}
		}
		else
		{
			for(int i = 0; i < length - 1; ++i)
			{
				sb.append(path.get(i)).append("->");
			}
		}
		return sb.append(this.last.toString()).toString();
	}
}
