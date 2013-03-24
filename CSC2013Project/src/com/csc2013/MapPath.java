package com.csc2013;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MapPath
{
	private final MapPath superPath;
	private final MapPoint point;
	
	public MapPath(MapPoint start)
	{
		if(start == null)
			throw new NullPointerException();
		this.superPath = null;
		this.point = start;
	}
	
	private MapPath(MapPath superPath, MapPoint point)
	{
		if(superPath == null || point == null)
			throw new NullPointerException();
		this.superPath = superPath;
		this.point = point;
	}
	
	public MapPath subPath(MapPoint subPoint)
	{
		return new MapPath(this, subPoint);
	}
	
	public MapPoint getLastPoint()
	{
		return point;
	}
	
	public List<MapPoint> toList()
	{
		List<MapPoint> path = new ArrayList<>();
		MapPath next = superPath;
		while(next != null)
		{
			path.add(next.point);
			next = next.superPath;
		}
		Collections.reverse(path);
		return path;
	}
}
