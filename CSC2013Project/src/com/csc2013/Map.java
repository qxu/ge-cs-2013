package com.csc2013;

import java.awt.Point;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;

import com.csc2013.DungeonMaze.BoxType;

public class Map
{
	private Point MapSize;
	private int MapBoxWidth;
	static MapBox[][] MapGrid;
	BoxType[][] Grid;
	private TiledMap map;
	private String name = "";
	
	public BoxType[][] getGrid()
	{
		return this.Grid;
	}
	
	public void setMap(String mapName) throws SlickException
	{
		this.map = null;
		try
		{
			this.map = new TiledMap("res/" + mapName, true);
			this.name = mapName;
			initMap();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String getMapName()
	{
		return this.name;
	}
	
	private void initMap()
	{
		this.MapSize = new Point(this.map.getWidth(), this.map.getHeight());
		//System.out.print(MapSize.getX());
		//System.out.print(MapSize.getY());
		
		this.MapBoxWidth = this.map.getTileHeight();
		MapGrid = new MapBox[(int)this.MapSize.getX()][(int)this.MapSize.getY()];
		this.Grid = new BoxType[(int)this.MapSize.getX()][(int)this.MapSize
				.getY()];
		
		// Get the index #s of each layer of the TMX map file.
		// * we could probably write this as a loop to get every layer in the map
		int collisionIndex = this.map.getLayerIndex("collision");
		int keyIndex = this.map.getLayerIndex("key");
		int doorIndex = this.map.getLayerIndex("door");
		int endIndex = this.map.getLayerIndex("end");
		
		for(int i = 0; i < (int)this.MapSize.getX(); i++)
		{
			for(int j = 0; j < (int)this.MapSize.getY(); j++)
			{
				BoxType curType = BoxType.Open;
				
				int tileID = this.map.getTileId(i, j, collisionIndex);
				String property = this.map.getTileProperty(tileID, "collide",
						"false");
				if(property.equals("true"))
				{
					curType = BoxType.Blocked;
				}
				
				tileID = this.map.getTileId(i, j, keyIndex);
				property = this.map.getTileProperty(tileID, "keyed", "false");
				if(property.equals("true"))
				{
					curType = BoxType.Key;
					/*
					 * System.out.print(i); System.out.print("-");
					 * System.out.print(j); System.out.print("Key\n");
					 */
				}
				
				tileID = this.map.getTileId(i, j, doorIndex);
				property = this.map.getTileProperty(tileID, "doored", "false");
				if(property.equals("true"))
				{
					curType = BoxType.Door;
					/*
					 * System.out.print(i); System.out.print("-");
					 * System.out.print(j); System.out.print("Door\n");
					 */
				}
				
				tileID = this.map.getTileId(i, j, endIndex);
				property = this.map.getTileProperty(tileID, "ended", "false");
				if(property.equals("true"))
				{
					curType = BoxType.Exit;
					/*
					 * System.out.print(i); System.out.print("-");
					 * System.out.print(j); System.out.print("Exit\n");
					 */
				}
				
				this.Grid[i][j] = curType;
			}
		}
		
		for(int i = 0; i < (int)this.MapSize.getX(); i++)
		{
			for(int j = 0; j < (int)this.MapSize.getY(); j++)
			{
				BoxType North = BoxType.Open;
				BoxType South = BoxType.Open;
				BoxType East = BoxType.Open;
				BoxType West = BoxType.Open;
				
				if(i == 0)
				{
					West = BoxType.Blocked;
				}
				else
				{
					West = this.Grid[i - 1][j];
				}
				
				if(i == (int)this.MapSize.getX() - 1)
				{
					East = BoxType.Blocked;
				}
				else
				{
					East = this.Grid[i + 1][j];
				}
				
				if(j == 0)
				{
					North = BoxType.Blocked;
				}
				else
				{
					North = this.Grid[i][j - 1];
				}
				
				if(j == (int)this.MapSize.getY() - 1)
				{
					South = BoxType.Blocked;
				}
				else
				{
					South = this.Grid[i][j + 1];
				}
				
				boolean consumable = false;
				if(this.Grid[i][j] == BoxType.Key)
				{
					consumable = true;
				}
				
				boolean end = false;
				if(this.Grid[i][j] == BoxType.Exit)
				{
					end = true;
				}
				
				MapGrid[i][j] = new MapBox(North, South, East, West,
						consumable, end);
			}
		}
	}
	
	// Constructor for Map, load map from resources folder
	public Map() throws SlickException
	{
		//map = new TiledMap("res/map02.tmx");
	}
	
	// Return the entire TMX map as a TiledMap
	public TiledMap getMap()
	{
		return this.map;
	}
	
	// Return the contents of a specific MapBox space
	public MapBox getMapBox(Point box)
	{
		return MapGrid[(int)box.getX()][(int)box.getY()];
	}
	
	// Returns the size of the overall map as X,Y coordinate
	public Point getMapSize()
	{
		return this.MapSize;
	}
	
	public void pickup(Point playerLocation)
	{ //thought key was still exsiting to south
		//MapBox Location = MapGrid[(int) playerLocation.getX()][(int) playerLocation.getY()];
		MapGrid[(int)playerLocation.getX()][(int)playerLocation.getY()]
				.consume();
		
		if(playerLocation.getX() > 0)
		{
			MapBox WestBox = MapGrid[(int)playerLocation.getX() - 1][(int)playerLocation
					.getY()];
			MapGrid[(int)playerLocation.getX() - 1][(int)playerLocation.getY()] = new MapBox(
					WestBox.North, WestBox.South, BoxType.Open, WestBox.West,
					WestBox.hasKey(), WestBox.isEnd());
		}
		if(playerLocation.getX() < this.MapSize.x)
		{
			MapBox EastBox = MapGrid[(int)playerLocation.getX() + 1][(int)playerLocation
					.getY()];
			MapGrid[(int)playerLocation.getX() + 1][(int)playerLocation.getY()] = new MapBox(
					EastBox.North, EastBox.South, EastBox.East, BoxType.Open,
					EastBox.hasKey(), EastBox.isEnd());
		}
		if(playerLocation.getY() > 0)
		{
			MapBox SouthBox = MapGrid[(int)playerLocation.getX()][(int)playerLocation
					.getY() + 1];
			MapGrid[(int)playerLocation.getX()][(int)playerLocation.getY() + 1] = new MapBox(
					BoxType.Open, SouthBox.South, SouthBox.East, SouthBox.West,
					SouthBox.hasKey(), SouthBox.isEnd());
		}
		if(playerLocation.getY() < this.MapSize.y)
		{
			MapBox NorthBox = MapGrid[(int)playerLocation.getX()][(int)playerLocation
					.getY() - 1];
			MapGrid[(int)playerLocation.getX()][(int)playerLocation.getY() - 1] = new MapBox(
					NorthBox.North, BoxType.Open, NorthBox.East, NorthBox.West,
					NorthBox.hasKey(), NorthBox.isEnd());
		}
		
		int tilesIndex = this.map.getLayerIndex("tiles");
		
		int keyIndex = this.map.getLayerIndex("key");
		int tileID = this.map.getTileId((int)playerLocation.getX(),
				(int)playerLocation.getY(), tilesIndex);
		this.map.setTileId((int)playerLocation.getX(),
				(int)playerLocation.getY(),
				keyIndex, tileID);
	}
	
	public boolean unlockDoor(Point playerLocation)
	{
		MapBox Location = MapGrid[(int)playerLocation.getX()][(int)playerLocation
				.getY()];
		Point DoorLocation = new Point();
		
		//check for each direction for a door
		if(Location.North == BoxType.Door)
		{
			DoorLocation.x = (int)playerLocation.getX();
			DoorLocation.y = (int)playerLocation.getY() - 1;
		}
		else if(Location.South == BoxType.Door)
		{
			DoorLocation.x = (int)playerLocation.getX();
			DoorLocation.y = (int)playerLocation.getY() + 1;
		}
		else if(Location.East == BoxType.Door)
		{
			DoorLocation.x = (int)playerLocation.getX() + 1;
			DoorLocation.y = (int)playerLocation.getY();
		}
		else if(Location.West == BoxType.Door)
		{
			DoorLocation.x = (int)playerLocation.getX() - 1;
			DoorLocation.y = (int)playerLocation.getY();
		}
		else
			//System.out.print("unlock failed\n");
			return false;
		
		//MapBox Door = MapGrid[DoorLocation.x][DoorLocation.y];
		
		//MapGrid[(int) playerLocation.getX()][(int) playerLocation.getY()].consume();
		
		if(DoorLocation.getX() > 0)
		{
			MapBox WestBox = MapGrid[(int)DoorLocation.getX() - 1][(int)DoorLocation
					.getY()];
			MapGrid[(int)DoorLocation.getX() - 1][(int)DoorLocation.getY()] = new MapBox(
					WestBox.North, WestBox.South, BoxType.Open, WestBox.West,
					WestBox.hasKey(), WestBox.isEnd());
		}
		if(DoorLocation.getX() < this.MapSize.x)
		{
			MapBox EastBox = MapGrid[(int)DoorLocation.getX() + 1][(int)DoorLocation
					.getY()];
			MapGrid[(int)DoorLocation.getX() + 1][(int)DoorLocation.getY()] = new MapBox(
					EastBox.North, EastBox.South, EastBox.East, BoxType.Open,
					EastBox.hasKey(), EastBox.isEnd());
		}
		if(DoorLocation.getY() < this.MapSize.y)
		{
			MapBox SouthBox = MapGrid[(int)DoorLocation.getX()][(int)DoorLocation
					.getY() + 1];
			MapGrid[(int)DoorLocation.getX()][(int)DoorLocation.getY() + 1] = new MapBox(
					BoxType.Open, SouthBox.South, SouthBox.East, SouthBox.West,
					SouthBox.hasKey(), SouthBox.isEnd());
		}
		if(DoorLocation.getY() > 0)
		{
			MapBox NorthBox = MapGrid[(int)DoorLocation.getX()][(int)DoorLocation
					.getY() - 1];
			MapGrid[(int)DoorLocation.getX()][(int)DoorLocation.getY() - 1] = new MapBox(
					NorthBox.North, BoxType.Open, NorthBox.East, NorthBox.West,
					NorthBox.hasKey(), NorthBox.isEnd());
		}
		
		//System.out.print("unlocked\n");
		
		int tilesIndex = this.map.getLayerIndex("tiles");
		
		int doorIndex = this.map.getLayerIndex("door");
		int tileID = this.map.getTileId((int)DoorLocation.getX(),
				(int)DoorLocation.getY(), tilesIndex);
		this.map.setTileId((int)DoorLocation.getX(), (int)DoorLocation.getY(),
				doorIndex, tileID);
		return true;
	}
	
	// Return the size of the MapBox
	public int getMapBoxWidth()
	{
		return this.MapBoxWidth;
	}
}
