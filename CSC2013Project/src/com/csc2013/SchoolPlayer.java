package com.csc2013;

import com.csc2013.DungeonMaze.Action;
import com.csc2013.DungeonMaze.BoxType;

/**
 * Westhill's SchoolPlayer implementation with that nice GUI.
 * 
 * @author [Westhill/NullPointerException]
 * @see <a href="https://github.com/qxu/ge-cs-2013">GitHub final version
 *      (seperate classes, includes debugger)</a>
 * @see <a href="https://github.com/qxu/ge-cs-2013-oneclass">GitHub one-class
 *      version (everything is in one SchoolPlayer.java class)</a>
 */
public class SchoolPlayer
{
	/*
	 * A map to store the points with their BoxTypes.
	 */
	private final PlayerMap map;
	
	/*
	 * The last directional move.
	 * This means we ignore Action.Pickup and Action.Open
	 */
	private Action lastMove;
	
	private static SchoolPlayer latestInstance;
	
	public static PlayerMapDebugger getLatestDebugger()
	{
		return latestInstance.map.getDebugger();
	}
	
	/**
	 * Creates a {@code SchoolPlayer}.
	 */
	public SchoolPlayer()
	{
		latestInstance = this;
		this.map = new PlayerMap();
	}
	
	/**
	 * Gets the move.<br>
	 * Updates the map.<br>
	 * <br>
	 * If an exit is found, the action to the nearest exit is returned. Then the
	 * algorithm searches for keys, then for unknown spaces. Doors are unlocked
	 * as necessary to explore the unknown spaces.
	 * 
	 * @param vision
	 * @param keyCount
	 * @param lastAction
	 * @return Action
	 */
	public Action nextMove(PlayerVision vision, int keyCount, boolean lastAction)
	{
		updateMap(vision);
		
		Action move = getMove(keyCount, lastAction);
		
//		if(vision.CurrentPoint.hasKey() != (this.map.getPlayerPosition().getType() == BoxType.Key))
//			throw new AssertionError("key not detected");
		
		if(this.map.getPlayerPosition().execute(move).getType() == BoxType.Door)
		{
			move = Action.Use;
		}
		
		if(move != Action.Use && move != Action.Pickup)
		{
			this.map.movePlayer(move);
			this.lastMove = move;
		}
		
		return move;
	}
	
	/*
	 * Calculates the best move. It tries to find an exit, then it tries to find
	 * a key, then it tries to uncover space.
	 */
	private Action getMove(int keyCount, boolean lastAction)
	{
		PlayerMap map = this.map;
		MapPoint player = map.getPlayerPosition();
		
		Action exitAction = ActionAlgorithms
				.actionTo(map, this.lastMove, player, BoxType.Exit);
		if(exitAction != null)
			return exitAction;
		
		if(keyCount < 8)
		{
			Action keyAction = ActionAlgorithms.actionTo(map, this.lastMove,
					player,
					BoxType.Key);
			if(keyAction != null)
				return keyAction;
		}
		
		Action coverSpaceAction = ActionAlgorithms.discoveryChannel(player,
				this.lastMove, keyCount);
		if(coverSpaceAction != null)
			return coverSpaceAction;
		
		System.out.println("?!!?!");
		throw new RuntimeException("out of moves");
	}
	
	/*
	 * Updates the map's BoxTypes from the given vision around the current
	 * player position.
	 * 
	 * So some grid geometry got us those coordinates to update around each
	 * point.
	 * 
	 * You might wonder why we have to subtract/add 1 to the the x/y coordinate.
	 * That's because the west, east, north, and south arrays start at index 0,
	 * but at index 0, the point is distance 1 from the player.
	 */
	private void updateMap(PlayerVision vision)
	{
		MapPoint player = this.map.getPlayerPosition();
		
		int centerX = player.x;
		int centerY = player.y;
		
		int westOffset = vision.mWest;
		int eastOffset = vision.mEast;
		int northOffset = vision.mNorth;
		int southOffset = vision.mSouth;
		
		MapBox[] west = vision.West;
		MapBox[] east = vision.East;
		MapBox[] north = vision.North;
		MapBox[] south = vision.South;
		
		PlayerMap map = this.map;
		
		for(int i = westOffset - 1; i >= 0; --i)
		{
			MapBox cell = west[i];
			map.set(centerX - i - 2, centerY, cell.West);
			map.set(centerX - i - 1, centerY - 1, cell.North);
			map.set(centerX - i - 1, centerY + 1, cell.South);
		}
		for(int i = eastOffset - 1; i >= 0; --i)
		{
			MapBox cell = east[i];
			map.set(centerX + i + 2, centerY, cell.East);
			map.set(centerX + i + 1, centerY - 1, cell.North);
			map.set(centerX + i + 1, centerY + 1, cell.South);
		}
		for(int i = northOffset - 1; i >= 0; --i)
		{
			MapBox cell = north[i];
			map.set(centerX, centerY - i - 2, cell.North);
			map.set(centerX - 1, centerY - i - 1, cell.West);
			map.set(centerX + 1, centerY - i - 1, cell.East);
		}
		for(int i = southOffset - 1; i >= 0; --i)
		{
			MapBox cell = south[i];
			map.set(centerX, centerY + i + 2, cell.South);
			map.set(centerX - 1, centerY + i + 1, cell.West);
			map.set(centerX + 1, centerY + i + 1, cell.East);
		}
		
		MapBox current = vision.CurrentPoint;

		map.set(centerX, centerY, current.hasKey() ? BoxType.Key : BoxType.Open);
		
		map.set(centerX - 1, centerY, current.West);
		map.set(centerX + 1, centerY, current.East);
		map.set(centerX, centerY - 1, current.North);
		map.set(centerX, centerY + 1, current.South);
	}
}