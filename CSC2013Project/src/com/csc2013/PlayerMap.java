package com.csc2013;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.csc2013.DungeonMaze.Action;
import com.csc2013.DungeonMaze.BoxType;

/**
 * A 2-dimensional player map that stores MapPoints and their corresponding
 * BoxTypes. This is implemented using a {@link HashMap} mapping the MapPoint to
 * its BoxType. The player map also stores the location of the player.
 * 
 * @author jqx
 */
public class PlayerMap {
	/*
	 * The gridRef is needed to check if a MapPoint already exists. If it does,
	 * then it can be retrieved using gridRef.get(point), where point is any
	 * point holding the desired coordinates.
	 */
	private Map<MapPoint, MapPoint> gridRef;

	/*
	 * The typeMap maps a MapPoint to its BoxType.
	 */
	private Map<MapPoint, BoxType> typeMap;

	private MapPoint playerPosition;

	private final PlayerMapDebugger debugger;

	/**
	 * Constructs a new player map with the player starting at coordinates (0,
	 * 0).
	 */
	public PlayerMap() {
		this(0, 0);
	}

	/**
	 * Constructs a new player map with the player starting at the given
	 * coordinates.
	 * 
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 */
	public PlayerMap(int x, int y) {
		this.gridRef = new HashMap<>();
		this.typeMap = new HashMap<>();
		this.playerPosition = new MapPoint(0, 0, this);
		this.debugger = new PlayerMapDebugger(this);
	}

	/**
	 * Returns a localized version of the given point. If the given point exists
	 * in this player map, then {@code get(point)} returns the a
	 * {@code MapPoint} that is backed by this map. If there is no point in this
	 * map with the same coordinates of the given point, then this method
	 * returns {@code null}.
	 * 
	 * @param point
	 *            the point to get the localized version of
	 * @return the localized point, or {@code null} if none
	 */
	public MapPoint get(MapPoint point) {
		return gridRef.get(point);
	}

	/**
	 * Returns the point from this map with the given coordinates. If there is
	 * no point in this map with the given coordinates, then this method returns
	 * null. {@code null}.
	 * 
	 * @param point
	 *            the point to get the localized version of
	 * @return the localized point
	 */
	public MapPoint get(int x, int y) {
		return get(new MapPoint(x, y, this));
	}

	/**
	 * Gets the debugger of this map to debug pathfinding, or other things.
	 * 
	 * @return
	 */
	public PlayerMapDebugger getDebugger() {
		return this.debugger;
	}

	/**
	 * Returns a set of points that this player map contains.
	 * 
	 * @return the set of points
	 */
	public Set<MapPoint> getGrid() {
		return new HashSet<>(gridRef.keySet());
	}

	/**
	 * 
	 * @param point
	 * @return
	 */
	public BoxType getTypeOf(MapPoint point) {
		return typeMap.get(point);
	}

	public void movePlayer(Action move) {
		this.playerPosition = this.playerPosition.execute(move);
	}

	public MapPoint set(int x, int y, BoxType type) {
		if (type == null)
			throw new NullPointerException();
		MapPoint newPoint = new MapPoint(x, y, this);
		MapPoint existingPoint = gridRef.get(newPoint);
		if (existingPoint == null) {
			gridRef.put(newPoint, newPoint);
			typeMap.put(newPoint, type);
			return newPoint;
		} else {
			typeMap.put(existingPoint, type);
			return existingPoint;
		}
	}

	public MapPoint getPlayerPosition() {
		return this.playerPosition;
	}

	public Set<MapPoint> find(BoxType type) {
		Set<MapPoint> found = new HashSet<>();
		for (Map.Entry<MapPoint, BoxType> entry : typeMap.entrySet()) {
			if (entry.getValue() == type) {
				found.add(entry.getKey());
			}
		}
		return found;
	}
}
