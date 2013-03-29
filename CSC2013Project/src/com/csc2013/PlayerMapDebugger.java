package com.csc2013;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.csc2013.DungeonMaze.BoxType;

public class PlayerMapDebugger
{
	public static final boolean DEBUG_MAP = true;
	public static final boolean DEBUG_MARKS = true;
	
	private static final int tileWidth = 16;
	private static final int tileHeight = 16;
	
	private final PlayerMap map;
	
	private JFrame mapFrame;
	private MapDebugPanel mapPanel;
	private JDialog buttonsFrame;
	
	private Map<MapPoint, Color> markedPoints = new ConcurrentHashMap<>();
	private Map<List<MapPoint>, Color> markedPaths = new ConcurrentHashMap<>();
	private Map<MapPoint, String> stringMarks = new ConcurrentHashMap<>();
	
	public PlayerMapDebugger(PlayerMap map)
	{
		this.map = map;
		if(DEBUG_MAP)
		{
			initSwingComponents();
		}
	}

	private volatile long millisWasted = 0;
	
	public void sleep(double millis)
	{
		try
		{
			long stop = System.nanoTime() + (long)(millis * 1000000);
			Thread.sleep((long)millis);
			long stopTime = System.nanoTime();
			if(millisWasted <= 0 && stopTime < stop)
			{
				Thread.sleep(1);
				stopTime = System.nanoTime();
			}
			millisWasted += (stopTime - stop);
		}
		catch(InterruptedException e)
		{
		}
		
	}
	
	public void waitForMarks(double millis)
	{
		if(DEBUG_MARKS)
		{
			if(millis > 0)
			{
				sleep(millis);
			}
		}
	}
	
	public void markPoint(MapPoint point, Color color)
	{
		if(DEBUG_MARKS)
		{
			if(color == null)
			{
				color = Color.GRAY;
			}
			this.markedPoints.put(point, color);
			updateMarks();
		}
	}
	
	public void unmarkPoint(MapPoint point)
	{
		if(DEBUG_MARKS)
		{
			this.markedPoints.remove(point);
			updateMarks();
		}
	}
	
	public void unmarkAllPoints()
	{
		if(DEBUG_MARKS)
		{
			this.markedPoints.clear();
			updateMarks();
		}
	}
	
	public void markPath(MapPath path, Color color)
	{
		if(DEBUG_MARKS)
		{
			if(color == null)
			{
				color = Color.GRAY;
			}
			this.markedPaths.put(path.toList(), color);
			updateMarks();
		}
	}
	
	public void unmarkPath(MapPath path)
	{
		if(DEBUG_MARKS)
		{
			this.markedPaths.remove(path.toList());
			updateMarks();
		}
	}
	
	public void unmarkAllPaths()
	{
		if(DEBUG_MARKS)
		{
			this.markedPaths.clear();
		}
	}
	
	public void stringMark(MapPoint point, String text)
	{
		if(DEBUG_MARKS)
		{
			this.stringMarks.put(point, text);
		}
	}
	
	public void stringUnmark(MapPoint point)
	{
		if(DEBUG_MARKS)
		{
			this.stringMarks.remove(point);
		}
	}
	
	public void stringUnmarkAll()
	{
		if(DEBUG_MARKS)
		{
			this.stringMarks.clear();
		}
	}
	
	private boolean finishedMap()
	{
		MapPoint player = map.getPlayerPoint();
		
		for(MapPoint point : player.getNeighbors())
		{
			if(point.getType() == BoxType.Exit)
				return true;
		}
		return false;
	}
	
	public void updateMarks()
	{
		if(DEBUG_MAP && DEBUG_MARKS)
		{
			this.mapPanel.repaint();
		}
	}
	
	public void repaintMap()
	{
		if(DEBUG_MAP)
		{
			this.mapPanel.repaint();
			
			if(finishedMap())
			{
				this.mapFrame.dispose();
			}
		}
	}
	
	private void initSwingComponents()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1)
		{
			// watermelons
		}
		
		this.mapPanel = new MapDebugPanel();
		
		this.mapFrame = new JFrame("DEBUG - player map");
		this.mapFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.mapFrame.add(this.mapPanel);
		this.mapFrame.pack();
		this.mapFrame.setFocusableWindowState(false);
		this.mapFrame.setVisible(true);
		this.mapFrame.setFocusableWindowState(true);
		
		buttonsFrame = new JDialog(mapFrame, "buttons");
		buttonsFrame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.PAGE_AXIS));
		final JButton pause = new JButton("pause");
		pause.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(pause.getText().equals("pause"))
				{
					pause.setText("resume");
					BFSearch.pause();
					AStarSearch.pause();
					Tournament.container.pause();
				}
				else
				{
					pause.setText("pause");
					BFSearch.resume();
					AStarSearch.resume();
					Tournament.container.resume();
				}
			}
		});
		pause.setAlignmentX(Component.CENTER_ALIGNMENT);
		final JButton terminate = new JButton("terminate");
		terminate.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});
		terminate.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttons.add(pause);
		buttons.add(terminate);
		buttonsFrame.add(buttons);
		buttonsFrame.pack();
		Rectangle rect = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
		int x = (int)rect.getMaxX() - buttonsFrame.getWidth() - 200;
		int y = (int)rect.getMaxY() - buttonsFrame.getHeight() - 100;
		buttonsFrame.setLocation(x, y);
		buttonsFrame.setFocusableWindowState(false);
		buttonsFrame.setVisible(true);
	}
	
	private class MapDebugPanel extends JPanel
	{
		private int minX = 0;
		private int minY = 0;
		private int maxX = 0;
		private int maxY = 0;
		
		public MapDebugPanel()
		{
			super();
			try
			{
				InputStream fontStream = PlayerMapDebugger.class
						.getResourceAsStream("ProggyTinySZ.ttf");
				Font font = Font.createFont(Font.PLAIN, fontStream)
						.deriveFont(16F);
				fontStream.close();
				setFont(font);
			}
			catch(IOException | FontFormatException e)
			{
				e.printStackTrace();
			}
		}
		
		@Override
		protected void paintComponent(Graphics g0)
		{
			Graphics g = g0.create();
			boolean failedToPaint = true;
			do
			{
				try
				{
					updatePaint(g);
					failedToPaint = false;
				}
				catch(ConcurrentModificationException e)
				{
					sleep(200);
					continue;
				}
			}
			while(failedToPaint);
			g.dispose();
		}
		
		private void updatePaint(Graphics g) throws ConcurrentModificationException
		{
			Set<MapPoint> grid = PlayerMapDebugger.this.map.getGrid();
			
			for(MapPoint point : grid)
			{
				checkBounds(point);
			}
			
			PlayerMap map = PlayerMapDebugger.this.map;
			
			int lengthX = maxX - minX;
			int lengthY = maxY - minY;
			
			int panelWidth = (lengthX + 1) * tileWidth;
			int panelHeight = (lengthY + 1) * tileHeight;
			Dimension size = new Dimension(panelWidth, panelHeight);
			setPreferredSize(size);
			setSize(size);
			PlayerMapDebugger.this.mapFrame.pack();
			
			for(int x = minX; x <= maxX; x++)
			{
				for(int y = minY; y <= maxY; y++)
				{
					int scaledX = (x - minX) * tileWidth;
					int scaledY = (y - minY) * tileHeight;
					g.drawImage(TileSprites.UNKNOWN, scaledX, scaledY, Color.WHITE, null);
				}
			}
			
			for(MapPoint point : grid)
			{
				Image paintImage = getPaintImage(point.getType());
				int scaledX = (point.x - minX) * tileWidth;
				int scaledY = (point.y - minY) * tileHeight;
				g.fillRect(scaledX, scaledY, tileWidth, tileHeight);
				g.drawImage(paintImage, scaledX, scaledY, Color.WHITE, null);
			}
			
			MapPoint player = map.getPlayerPoint();
			
			int playerScaledX = (player.x - minX) * tileWidth;
			int playerScaledY = (player.y - minY) * tileHeight;
			g.drawImage(TileSprites.LOCATION, playerScaledX, playerScaledY, Color.WHITE, null);
			g.drawImage(TileSprites.LOCATION, -minX * tileWidth, -minY * tileHeight, null);
			
			if(DEBUG_MARKS)
			{
				for(Map.Entry<List<MapPoint>, Color> entry : PlayerMapDebugger.this.markedPaths
						.entrySet())
				{
					List<MapPoint> path = entry.getKey();
					g.setColor(entry.getValue());
					int numOfPoints = path.size();
					MapPoint prev = path.get(0);
					for(int i = 1; i < numOfPoints; ++i)
					{
						MapPoint cur = path.get(i);
						int x1 = (prev.x - minX) * tileWidth + tileWidth / 2;
						int y1 = (prev.y - minY) * tileHeight + tileHeight / 2;
						int x2 = (cur.x - minX) * tileWidth + tileWidth / 2;
						int y2 = (cur.y - minY) * tileHeight + tileHeight / 2;
						g.drawLine(x1, y1, x2, y2);
						prev = cur;
					}
				}
				
				for(Map.Entry<MapPoint, Color> entry : PlayerMapDebugger.this.markedPoints
						.entrySet())
				{
					MapPoint point = entry.getKey();
					int x = (point.x - minX) * tileWidth + tileWidth / 8;
					int y = (point.y - minY) * tileHeight + tileHeight / 8;
					int markWidth = tileWidth / 3;
					int markHeight = tileHeight / 3;
					g.setColor(entry.getValue());
					g.fillRect(x, y, markWidth, markHeight);
				}
				
				g.setColor(Color.BLACK);
				for(Map.Entry<MapPoint, String> entry : PlayerMapDebugger.this.stringMarks
						.entrySet())
				{
					
					MapPoint point = entry.getKey();
					int x = (point.x - minX) * tileWidth + tileWidth / 16;
					int y = (point.y - minY) * tileHeight + tileHeight / 2;
					g.drawString(entry.getValue(), x, y);
				}
			}
		}
		

		private Image getPaintImage(BoxType tile)
		{
			if(tile == null)
				return TileSprites.UNKNOWN;
			
			switch(tile)
			{
				case Open:
					return TileSprites.OPEN;
				case Blocked:
					return TileSprites.BLOCKED;
				case Door:
					return TileSprites.DOOR;
				case Exit:
					return TileSprites.EXIT;
				case Key:
					return TileSprites.KEY;
				default:
					throw new AssertionError();
			}
		}
		
		private void checkBounds(MapPoint point)
		{
			int x = point.x;
			int y = point.y;
			if(x < this.minX)
			{
				this.minX = x;
			}
			if(x > this.maxX)
			{
				this.maxX = x;
			}
			if(y < this.minY)
			{
				this.minY = y;
			}
			if(y > this.maxY)
			{
				this.maxY = y;
			}
		}
	}
	private static class TileSprites
	{
		private static final Image OPEN;
		private static final Image BLOCKED;
		private static final Image KEY;
		private static final Image DOOR;
		private static final Image LOCATION;
		private static final Image EXIT;
		private static final Image UNKNOWN;
		
		private static final byte[] spritesData = 
			{71, 73, 70, 56, 57, 97, 32, 0, 64, 0,
			-14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1,
			51, 102, -103, 0, -64, 0, -1, -103, 51,
			0, 0, 0, 0, 0, 0, 33, -7, 4, 1, 0, 0, 0,
			0, 44, 0, 0, 0, 0, 32, 0, 64, 0, 66, 8,
			-1, 0, 1, 8, 28, 72, -80, 32, -128, 0,
			8, 19, 42, 92, 24, -64, -96, -61, -127,
			12, 35, 38, 124, 72, -79, -94, 65, 1, 2,
			4, 98, -36, -56, 49, -29, -64, 2, 32,
			67, -118, -76, 72, -78, 36, -63, 1, 40,
			83, 26, 36, -64, -78, -27, 74, 2, 7, 37,
			70, 52, 41, 112, 102, -51, -123, 52,
			115, -58, -108, -119, -109, 38, 79,
			-122, 26, 55, -46, 20, 9, 82, -89, 67,
			-94, 68, 29, -90, 92, 58, -96, 34, -52,
			-126, 44, 31, -94, 36, 25, 21, 42, 76,
			-123, 16, 17, -34, -44, -22, 19, -24,
			78, -84, 70, 75, -2, -20, 105, 114, 44,
			-40, -78, 102, -71, -94, 77, 27, -42,
			36, -58, -96, 37, -117, 126, 44, 80,
			-80, -93, -57, -73, 111, 9, 34, 29, -39,
			-74, -19, 82, -117, 45, 3, -69, 60, -55,
			116, -86, -61, -86, 86, -107, -86, -92,
			-8, -12, 101, 95, 0, -126, 5, 127, 85,
			59, -71, 97, 87, -101, 95, 115, -38,
			-60, 44, 54, -83, -27, -57, 15, 61, 127,
			-18, -52, -10, -78, 89, -51, -98, 81,
			-105, 94, 123, -38, -12, 88, -43, -83,
			65, -53, 46, -56, -73, 110, -57, -72,
			114, 67, -38, 14, 106, 55, -17, 92, -67,
			116, 9, -26, 29, -98, -47, -73, -64,
			-67, -70, -123, 11, 37, -119, 92, 110,
			-37, -26, -63, 103, 75, 15, 91, -40,
			105, -28, -63, 3, 11, 27, 94, -7, 16,
			-79, 64, -19, 77, 9, 70, 50, 118, 92,
			-80, 122, -9, -58, -30, -47, 103, 95,
			-20, -108, -68, -63, -19, -116, -81,
			123, -97, -66, 53, 54, 105, -54, -11,
			71, -109, 4, -53, 127, -94, 107, -1,
			-109, -63, -26, 31, 103, -5, -91, -10,
			-97, 101, 4, 90, 36, 90, 78, 1, 1, 0,
			59};

		private static final int scaleMethod = Image.SCALE_SMOOTH;
		private static final int tileWidth = 16;
		private static final int tileHeight = 16;
		
		static
		{
			BufferedImage spriteSheet = decode(spritesData);
			OPEN = spriteSheet.getSubimage(0 * tileWidth, 0 * tileHeight, tileWidth, tileHeight)
					.getScaledInstance(tileWidth, tileHeight, scaleMethod);
			BLOCKED = spriteSheet.getSubimage(1 * tileWidth, 0 * tileHeight, tileWidth, tileHeight)
					.getScaledInstance(tileWidth, tileHeight, scaleMethod);
			KEY = spriteSheet.getSubimage(0 * tileWidth, 1 * tileHeight, tileWidth, tileHeight)
					.getScaledInstance(tileWidth, tileHeight, scaleMethod);;
			DOOR = spriteSheet.getSubimage(1 * tileWidth, 1 * tileHeight, tileWidth, tileHeight)
					.getScaledInstance(tileWidth, tileHeight, scaleMethod);;
			LOCATION = spriteSheet.getSubimage(0 * tileWidth, 2 * tileHeight, tileWidth, tileHeight)
					.getScaledInstance(tileWidth, tileHeight, scaleMethod);;
			EXIT = spriteSheet.getSubimage(1 * tileWidth, 2 * tileHeight, tileWidth, tileHeight)
					.getScaledInstance(tileWidth, tileHeight, scaleMethod);;
			UNKNOWN = spriteSheet.getSubimage(0 * tileWidth, 3 * tileHeight, tileWidth, tileHeight)
					.getScaledInstance(tileWidth, tileHeight, scaleMethod);;
		}
		
		private static BufferedImage decode(byte[] data)
		{
			InputStream dataStream = new ByteArrayInputStream(data);
			try
			{
				return ImageIO.read(dataStream);
			}
			catch(IOException e)
			{
				throw new ExceptionInInitializerError("failed to initialize sprites");
			}
		}
	}
}
