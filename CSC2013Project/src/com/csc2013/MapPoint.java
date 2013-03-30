package com.csc2013;

import java.util.Arrays;

import com.csc2013.DungeonMaze.Action;
import com.csc2013.DungeonMaze.BoxType;

public class MapPoint
{
	public final int x;
	public final int y;
	
	private MapPoint west;
	private MapPoint east;
	private MapPoint north;
	private MapPoint south;
	
	private PlayerMap map;
	
	/**
	 * Constructs a new {@code MapPoint} from at the given coordinates (x, y).
	 * The point will be based from the {@code PlayerMap} given.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @param map
	 *            the map to reference
	 */
	public MapPoint(int x, int y, PlayerMap map)
	{
		if(map == null)
			throw new NullPointerException();
		this.x = x;
		this.y = y;
		this.map = map;
	}
	
	/**
	 * Gets the {@code BoxType} of this point.
	 * 
	 * @return the {@code BoxType} of this point
	 */
	public BoxType getType()
	{
		return this.map.getTypeOf(this);
	}
	
	/**
	 * Returns the point west of this point.
	 * 
	 * @return the point to the west
	 */
	public MapPoint west()
	{
		MapPoint cachedW = this.west;
		if(cachedW != null)
			return cachedW;
		MapPoint w = new MapPoint(x - 1, y, map);
		MapPoint mapW = map.get(w);
		return (mapW != null) ? (this.west = mapW) : w;
	}
	
	/**
	 * Returns the point east of this point.
	 * 
	 * @return the point to the east
	 */
	public MapPoint east()
	{
		MapPoint cachedE = this.east;
		if(cachedE != null)
			return cachedE;
		MapPoint e = new MapPoint(x + 1, y, map);
		MapPoint mapE = map.get(e);
		return (mapE != null) ? (this.east = mapE) : e;
	}
	
	/**
	 * Returns the point north of this point.
	 * 
	 * @return the point to the north
	 */
	public MapPoint north()
	{
		MapPoint cachedN = this.north;
		if(cachedN != null)
			return cachedN;
		MapPoint n = new MapPoint(x, y - 1, map);
		MapPoint mapN = map.get(n);
		return (mapN != null) ? (this.north = mapN) : n;
	}
	
	/**
	 * Returns the point south of this point.
	 * 
	 * @return the point to the south
	 */
	public MapPoint south()
	{
		MapPoint cachedS = this.south;
		if(cachedS != null)
			return cachedS;
		MapPoint s = new MapPoint(x, y + 1, map);
		MapPoint mapS = map.get(s);
		return (mapS != null) ? (this.south = mapS) : s;
	}
	
	/**
	 * Calculates the {@code Action} to get from this point to the given
	 * destination point. The action is determined as follows:<br>
	 * 
	 * <blockquote>
	 * 
	 * <pre>
	 *             Action.North
	 * Action.West (this point) Action.East
	 *             Action.South
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * Ties are currently given in favor to the vertical directions:
	 * {@code Action.North} and {@code Action.South}. If this point's
	 * coordinates equals the destinations coordinates, it returns
	 * {@code Action.North} since it yolos and doesn't give a shit.
	 * 
	 * @param dest
	 * @return
	 */
	public Action actionTo(MapPoint dest)
	{
		int dx = dest.x - this.x;
		int dy = dest.y - this.y;
		
		return (Math.abs(dx) > Math.abs(dy))
				? ((dx > 0) ? Action.East : Action.West)
				: ((dy > 0) ? Action.South : Action.North);
	}
	
	/**
	 * Returns the point after executing the move.<br>
	 * <br>
	 * <table>
	 * <tr>
	 * <td>Move</td>
	 * <td>Return value</td>
	 * </tr>
	 * <tr>
	 * <td>{@code Action.West}</td>
	 * <td>{@code west()}
	 * </tr>
	 * </td>
	 * <tr>
	 * <td>{@code Action.East}</td>
	 * <td>{@code east()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code Action.North}</td>
	 * <td>{@code north()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code Action.South}</td>
	 * <td>{@code south()}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code Action.Pickup}</td>
	 * <td>{@code this}</td>
	 * </tr>
	 * <tr>
	 * <td>{@code Action.Use}</td>
	 * <td>{@code this}</td>
	 * </tr>
	 * </table>
	 * 
	 * @param move
	 *            the move
	 * @return the point after executing the move.
	 * @throws NullPointerException
	 *             if the move given is {@code null}.
	 */
	public MapPoint execute(Action move)
	{
		switch(move)
		{
			case West:
				return west();
			case East:
				return east();
			case North:
				return north();
			case South:
				return south();
			case Pickup:
			case Use:
				return this;
			default:
				throw new AssertionError();
		}
	}
	
	/**
	 * Returns the Manhattan distance from this point to the destination point.
	 * 
	 * @param dest
	 *            the destination point
	 * @return the Manhattan distance
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Taxicab_geometry">Taxicab
	 *      geometry - Wikipedia</a>
	 */
	public int distanceTo(MapPoint dest)
	{
		return Math.abs(this.x - dest.x) + Math.abs(this.y - dest.y);
	}
	
	/**
	 * A convenience method for iterating over the neighbors of this point.
	 * 
	 * @return the neighbors of this point.
	 */
	public Iterable<MapPoint> getNeighbors()
	{
		return Arrays.asList(new MapPoint[] {west(), east(), north(), south()});
	}
	
	/**
	 * Returns the hash code for this point by multiplying the x coordinate by
	 * 31 and xor-ing it with the y coordinate.
	 * 
	 * @return the hash code for this point
	 */
	@Override
	public int hashCode()
	{
		return (31 * this.x) + this.y;
	}
	
	/**
	 * Tests two points for equality. Two points are equal if their coordinates
	 * are equal, regardless of the maps they belong to.
	 */
	@Override
	public boolean equals(Object o)
	{
		if(!(o instanceof MapPoint))
			return false;
		MapPoint point = (MapPoint)o;
		return (this.x == point.x) && (this.y == point.y);
	}
	
	/**
	 * Returns a string with the type of this point, followed by an at symbol
	 * (@), followed by the coordinates in the form (x, y). For example, a point
	 * with type {@code BoxType.Open} and coordinates x=20, y=8 will return the
	 * string: {@code "Open@(20, 8)"}.
	 * 
	 * @return the string representation of this point
	 */
	@Override
	public String toString()
	{
		return getType() + "@(" + this.x + ", " + this.y + ")";
	}
}
