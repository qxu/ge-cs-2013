package com.csc2013;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.csc2013.DungeonMaze.BoxType;

public class SchoolPlayerDebugger
{
	private static final int tileWidth = 16;
	private static final int tileHeight = 16;
	
	private SchoolPlayer player;
	private PlayerMap map;
	
	private JFrame mapFrame;
	private JPanel mapPanel;
	
	private Map<MapPoint, Color> markedPoints = new ConcurrentHashMap<>();
	
	public static void sleep(long millis)
	{
		try
		{
			Thread.sleep(millis);
		}
		catch(InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
	
	public void mark(MapPoint point, Color color)
	{
		if(color == null)
		{
			color = Color.GRAY;
		}
		this.markedPoints.put(point, color);
		update();
	}
	
	public void unmark(MapPoint point)
	{
		this.markedPoints.remove(point);
		update();
	}
	
	public void unmarkAll()
	{
		this.markedPoints.clear();
		System.out.println(this.markedPoints.size());
		update();
	}
	
	public SchoolPlayerDebugger(SchoolPlayer player, PlayerMap map)
	{
		if(player == null)
			throw new NullPointerException();
		this.player = player;
		this.map = map;
		initSwingComponents();
	}
	
	public boolean finishedMap()
	{
		MapPoint player = this.map.getPlayerPoint();
		
		return (this.map.get(player) == BoxType.Exit)
				|| (this.map.get(player.west()) == BoxType.Exit)
				|| (this.map.get(player.east()) == BoxType.Exit)
				|| (this.map.get(player.north()) == BoxType.Exit)
				|| (this.map.get(player.south()) == BoxType.Exit);
	}
	
	public void update()
	{
		this.mapFrame.repaint();
		this.mapFrame.pack();
		
		if(finishedMap())
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					SchoolPlayerDebugger.this.mapFrame.dispose();
				}
			});
		}
	}
	
	private void initSwingComponents()
	{
		this.mapPanel = new JPanel()
		{
			private final int scaleMethod = Image.SCALE_SMOOTH;
			private final Image black = TileSprites.black.getScaledInstance(
					tileWidth, tileHeight, this.scaleMethod);
			private final Image lock = TileSprites.lock.getScaledInstance(
					tileWidth, tileHeight, this.scaleMethod);
			private final Image exit = TileSprites.exit.getScaledInstance(
					tileWidth, tileHeight, this.scaleMethod);
			private final Image key = TileSprites.key.getScaledInstance(
					tileWidth, tileHeight, this.scaleMethod);
			private final Image empty = TileSprites.empty.getScaledInstance(
					tileWidth, tileHeight, this.scaleMethod);
			private final Image question = TileSprites.question
					.getScaledInstance(tileWidth, tileHeight, this.scaleMethod);
			private final Image location = TileSprites.location
					.getScaledInstance(tileWidth, tileHeight, this.scaleMethod);
			
			private Image getPaintImage(BoxType tile)
			{
				if(tile == null)
					return this.question;
				
				switch(tile)
				{
					case Blocked:
						return this.black;
					case Door:
						return this.lock;
					case Exit:
						return this.exit;
					case Key:
						return this.key;
					case Open:
						return this.empty;
					default:
						return this.question;
				}
			}
			
			@Override
			protected void paintComponent(Graphics g)
			{
				PlayerMap map = SchoolPlayerDebugger.this.map;
				
				int minX = map.minX;
				int minY = map.minY;
				int maxX = map.maxX;
				int maxY = map.maxY;
				
				int lengthX = maxX - minX;
				int lengthY = maxY - minY;
				
				int panelWidth = (lengthX + 1) * tileWidth;
				int panelHeight = (lengthY + 1) * tileHeight;
				Dimension size = new Dimension(panelWidth, panelHeight);
				setPreferredSize(size);
				setSize(size);
				
				g.fillRect(0, 0, panelWidth, panelHeight);
				
				for(int x = minX; x <= maxX; x++)
				{
					for(int y = minY; y <= maxY; y++)
					{
						Image paintImage = getPaintImage(map.get(x, y));
						int scaledX = (x - minX) * tileWidth;
						int scaledY = (y - minY) * tileHeight;
						g.drawImage(paintImage, scaledX, scaledY, null);
					}
				}
				
				MapPoint player = map.getPlayerPoint();
				
				int playerScaledX = (player.x - minX) * tileWidth;
				int playerScaledY = (player.y - minY) * tileHeight;
				g.drawImage(this.location, playerScaledX, playerScaledY, null);
				
				g.drawImage(this.location, -minX * tileWidth,
						-minY * tileHeight,
						null);
				
				for(Entry<MapPoint, Color> entry : SchoolPlayerDebugger.this.markedPoints
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
				
				SchoolPlayerDebugger.this.mapFrame.pack();
			}
			
			private static final long serialVersionUID = -8172988280995869457L;
		};
		
		this.mapPanel.setForeground(Color.WHITE);
		
		this.mapFrame = new JFrame("DEBUG - player map");
		this.mapFrame
				.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.mapFrame.add(this.mapPanel);
		this.mapFrame.pack();
		this.mapFrame.setFocusableWindowState(false);
		this.mapFrame.setVisible(true);
		this.mapFrame.setFocusableWindowState(true);
		
		JFrame buttonsFrame = new JFrame("buttons");
		buttonsFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.PAGE_AXIS));
		buttons.setBorder(BorderFactory.createLineBorder(new Color(255, 255,
				255, 0), 10));
		final JButton pause = new JButton("pause");
		pause.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(pause.getText().equals("pause"))
				{
					pause.setText("resume");
					Tournament.container.pause();
				}
				else
				{
					pause.setText("pause");
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
		buttons.add(Box.createVerticalStrut(10));
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
	
	private static class TileSprites
	{
		private static final BufferedImage black;
		private static final BufferedImage lock;
		private static final BufferedImage exit;
		private static final BufferedImage key;
		private static final BufferedImage empty;
		private static final BufferedImage question;
		private static final BufferedImage location;
		
		private static final byte[] blackData =
		{71, 73, 70, 56, 57, 97, 16, 0, 16, 0,
				-16, 0, 0, 0, 0, 0, 0, 0, 0, 44, 0, 0,
				0, 0, 16, 0, 16, 0, 64, 8, 29, 0, 1, 8,
				28, 72, -80, -96, -63, -125, 8, 19, 42,
				92, -56, -80, -95, -61, -121, 16, 35,
				74, -100, 72, -79, -94, -59, -127, 1, 1,
				0, 59};
		
		private static final byte[] lockData =
		{71, 73, 70, 56, 57, 97, 16, 0, 16, 0,
				-16, 0, 0, 0, 0, 0, -1, -103, 51, 33,
				-7, 4, 1, 0, 0, 0, 0, 44, 0, 0, 0, 0,
				16, 0, 16, 0, 64, 8, 58, 0, 1, 8, 28,
				72, -80, 32, -63, 0, 8, 19, 42, 52, 40,
				80, 33, 66, -122, 0, 28, 58, -124, 72,
				-47, -32, -61, -127, 23, 11, 74, 92, 88,
				-79, -93, 71, -122, 28, 33, 38, 108,
				-104, 81, 99, -128, -125, 39, 45, 110,
				44, 73, 114, -91, 74, -105, 31, 11, 6,
				4, 0, 59};
		
		private static final byte[] exitData =
		{71, 73, 70, 56, 57, 97, 16, 0, 16, 0,
				-16, 0, 0, 0, 0, 0, 0, -64, 0, 33, -7,
				4, 1, 0, 0, 0, 0, 44, 0, 0, 0, 0, 16, 0,
				16, 0, 64, 8, 63, 0, 1, 8, 28, 72, -80,
				-96, -64, 0, 8, 19, 26, 68, 104, 16, 64,
				-128, -123, 15, 11, 50, -108, 24, -111,
				96, -62, -117, 10, 45, 86, -44, 40, -79,
				-95, -61, -115, 7, 49, 94, -12, 72,
				-110, -94, -56, -119, 3, 65, -90, -116,
				40, 18, 34, -59, -122, 40, 45, -70, 52,
				121, -78, 100, -61, -128, 0, 59};
		
		private static final byte[] keyData =
		{71, 73, 70, 56, 57, 97, 16, 0, 16, 0,
				-16, 0, 0, 0, 0, 0, 0, 0, -1, 33, -7, 4,
				1, 0, 0, 0, 0, 44, 0, 0, 0, 0, 16, 0,
				16, 0, 64, 8, 53, 0, 1, 8, 28, 72, -80,
				-96, -64, 0, 1, 14, 34, 92, -56, 112,
				96, 67, -125, 16, 35, 74, -100, -88,
				-80, -94, 68, -122, 8, 43, 102, -92,
				-56, -79, -93, 71, -116, 9, 47, -122, 4,
				-7, 112, -93, -55, -124, 27, 21, -90,
				-12, -56, 114, 98, 64, 0, 59};
		
		private static final byte[] emptyData =
		{71, 73, 70, 56, 57, 97, 16, 0, 16, 0,
				-16, 0, 0, 0, 0, 0, 0, 0, 0, 33, -7, 4,
				1, 0, 0, 0, 0, 44, 0, 0, 0, 0, 16, 0,
				16, 0, 64, 8, 29, 0, 1, 8, 28, 72, -80,
				-96, -63, -125, 8, 19, 42, 92, -56, -80,
				-95, -61, -121, 16, 35, 74, -100, 72,
				-79, -94, -59, -127, 1, 1, 0, 59};
		
		private static final byte[] questionData =
		{71, 73, 70, 56, 57, 97, 16, 0, 16, 0,
				-16, 0, 0, 0, 0, 0, -1, -1, -1, 44, 0,
				0, 0, 0, 16, 0, 16, 0, 64, 8, 53, 0, 1,
				8, 28, 72, -80, -96, 65, -127, 1, 18,
				30, 28, -104, 48, 0, 67, -121, 11, 17,
				66, -116, 40, 81, 33, 69, -126, 13, 47,
				90, -68, -56, -79, -93, -63, -122, 25,
				23, 110, 4, 48, 82, 100, -55, -125, 39,
				61, 82, 76, -87, 18, 64, 64, 0, 59};
		
		private static final byte[] locationData =
		{71, 73, 70, 56, 57, 97, 16, 0, 16, 0,
				-16, 0, 0, 0, 0, 0, 51, 102, -103, 33,
				-7, 4, 1, 0, 0, 0, 0, 44, 0, 0, 0, 0,
				16, 0, 16, 0, 64, 8, 52, 0, 1, 8, 28,
				72, -80, -96, -63, 0, 8, 19, 26, 28,
				-104, -80, 97, -128, -123, 4, 17, 66,
				44, -40, 112, 34, 0, -121, 10, 39, 86,
				-76, -56, -79, -93, 70, -121, 31, 49,
				66, -60, 40, 113, 36, 72, -117, 27, 57,
				-106, -12, 8, 49, 32, 0, 59};
		
		static
		{
			black = decode(blackData);
			lock = decode(lockData);
			exit = decode(exitData);
			key = decode(keyData);
			empty = decode(emptyData);
			question = decode(questionData);
			location = decode(locationData);
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
				throw new AssertionError(e);
			}
		}
	}
}
