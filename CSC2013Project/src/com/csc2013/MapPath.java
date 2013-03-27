package com.csc2013;

import java.util.Arrays;
import java.util.List;

import com.csc2013.PlayerMap.MapPoint;

public class MapPath
{
	private final MapPath superPath;
	private final MapPoint point;
	private final int numOfPoints;
	private final int keyCount;
	private final int turnCount;
	
	public MapPath(MapPoint start)
	{
		if(start == null)
			throw new NullPointerException();
		this.superPath = null;
		this.point = start;
		this.numOfPoints = 1;
		this.keyCount = 0;
		this.turnCount = 0;
	}
	
	private MapPath(MapPath superPath, MapPoint point)
	{
		this.superPath = superPath;
		this.point = point;
		this.numOfPoints = superPath.numOfPoints + 1;
		this.keyCount = superPath.keyCount;
		if(superPath.length() == 1)
		{
			this.turnCount = superPath.turnCount;
		}
		else
		{
			MapPoint lastlast = superPath.superPath.getLastPoint();
			MapPoint last = superPath.getLastPoint();
			this.turnCount = (lastlast.actionTo(last) == last.actionTo(point))
				? superPath.turnCount
				: superPath.turnCount + 1;
		}
	}
	
	public MapPath subPath(MapPoint subPoint)
	{
		if(subPoint == null)
			throw new NullPointerException();
		return new MapPath(this, subPoint);
	}
	
	public MapPoint getLastPoint()
	{
		return this.point;
	}
	
	public int length()
	{
		return this.numOfPoints;
	}
	
	public int getTurnCount()
	{
		return this.turnCount;
	}
	
	public boolean isBasePath()
	{
		return length() == 1;
	}
	
	public MapPath getStepPath()
	{
//		if(length() == 1)
//			return null;
		MapPath nextPath = this;
		for(int i = length() - 1; i >= 2; --i)
		{
			nextPath = nextPath.superPath;
		}
		return nextPath;
	}
	
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
	
	@Override
	public int hashCode()
	{
		int length = length();
		if(length == 1)
			return getLastPoint().hashCode();
		else if(length == 2)
			return (this.superPath.hashCode() * 31) ^ getLastPoint().hashCode();
		else
			return (getStepPath().hashCode() * 31) ^ getLastPoint().hashCode();
	}
	
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
	
	@Override
	public String toString()
	{
		int length = length();
		if(length < 2)
			return this.point.toString();
		
		List<MapPoint> path = toList();
		StringBuilder sb = new StringBuilder();
		if(length > 8)
		{
			for(int i = 0; i < 4; ++i)
			{
				sb.append(path.get(i)).append("->");
			}
			sb.append(" .. ");
			for(int i = length - 4; i < length - 1; ++i)
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
		return sb.append(this.point.toString()).toString();
	}
	
	static
	{
		
	}
}
