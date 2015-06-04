package tetris;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import sun.audio.AudioData;
import sun.audio.AudioDataStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * 俄罗斯方法主窗体<br>
 * 暂未实现： 声音开关设置、能穿透的方块、炸弹方块、机关枪（能喷射点）、排行榜、新最高分记录提示、 游戏开始动画，游戏over动画<br>
 * 
 * @note：有时间再写，先写到这基本就行啦<br>
 * 
 * @author Tang
 * 
 */
@SuppressWarnings({ "serial", "rawtypes", "restriction" })
public class TetrisFrame extends JFrame {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(com.sun.java.swing.plaf.windows.WindowsLookAndFeel.class.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				TetrisFrame tetrisFrame = new TetrisFrame();
				tetrisFrame.setVisible(true);
			}
		});
	}

	protected static Random random = new Random();

	/**
	 * 单元格大小（单位：像素）
	 */
	private int cellSize = 30;
	/**
	 * 方块图基的行数
	 */
	private int rows = 20;
	/**
	 * 方块图基的列数
	 */
	private int cols = 10;

	/**
	 * 窗体的背景
	 */
	private Color windowBackground = new Color(100, 100, 100);

	/**
	 * 方块图基的背景
	 */
	private Color plotBackground = Color.WHITE;

	/**
	 * 下个方块的背景
	 */
	private Color nextBackground = windowBackground;

	/**
	 * 方块中的每个小方块的渐变色的上面起始颜色
	 */
	private Color blockCellTopColor = Color.WHITE;
	/**
	 * 当前方块中的每个小方块的边框颜色
	 */
	private Color blockCellBorderColor = Color.GRAY;

	/**
	 * 虚拟方块的每个小方块的边框颜色
	 */
	private Color vertualBlockCellBorderColor = new Color(128, 128, 128, 20);

	/**
	 * 方块图基的中的网格线颜色，网格线是用来分割行与列
	 */
	private Color plotGridColor = Color.LIGHT_GRAY;
	/**
	 * 方块图基的中的网格线规则器，网格线是用来分割行与列
	 */
	private Stroke plotGridStroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 1.5f, 4f }, 0f);

	/**
	 * 方块图基面板
	 */
	private JPanel blockPlotPanel;
	/**
	 * 下个方块面板
	 */
	private JPanel nextBlockPanel;

	/**
	 * 显示分数的标签
	 */
	private JLabel scoreLabel;

	/**
	 * 显示累计消掉多少行的组件
	 */
	private JLabel wipeOutRowsLabel;

	/**
	 * 方块图基图片
	 */
	private BufferedImage blockPlotImage;
	/**
	 * 下个方块图片
	 */
	private BufferedImage nextBlockImage;

	/**
	 * 方块图基绘笔
	 */
	private Graphics2D blockPlotGraphics;
	/**
	 * 下个方块绘笔
	 */
	private Graphics2D nextBlockGraphics;

	/**
	 * 方块图基标记，使用1表示是方块单元格，0表示是默认单元格
	 */
	private int[][] plotBlockMarks;
	/**
	 * 颜色图基标记，方块图基标记中值为1的单元格都能在此数组中找到相对应的颜色
	 */
	private Color[][] plotColorMarks;

	/**
	 * 当前方块
	 */
	private AbstractBlock currentBlock;
	/**
	 * 虚拟方块
	 */
	private AbstractBlock vertualBlock;
	/**
	 * 下一个方块
	 */
	private AbstractBlock nextBlock;

	/**
	 * 方块下移定时器
	 */
	private Timer dropBlockTimer;
	/**
	 * 方块下移定时器间隔的毫秒数
	 */
	private int dropBlockVelocity = 500;

	/**
	 * 方块生长定时器
	 */
	private Timer growBlockTimer;

	/**
	 * 方块生长定时器间隔的毫秒数
	 */
	private int growBlockVelocity = 50000;

	/**
	 * 让"下移定时器"和"生长定时器"加速的定时器
	 */
	private Timer speedupTimer;
	/**
	 * 让"下移定时器"和"生长定时器"加速的定时器间隔的毫秒数
	 */
	private int speedupVelocity = 5000;

	/**
	 * 分数
	 */
	private int score = 0;

	/**
	 * 累计消掉多少行
	 */
	private int wipeOutRows = 0;

	/**
	 * 游戏是否在运行中
	 */
	private boolean isGameRunning = false;
	/**
	 * 游戏是否在暂停中
	 */
	private boolean isGamePause = false;

	/**
	 * 缓存游戏结束的状态
	 */
	private boolean isGameOver = false;

	/**
	 * 无意义的临时变量，为了节约内存而定义
	 */
	private final Rectangle tempBlockRect = new Rectangle();

	/**
	 * 声音文件路径
	 */
	private String audioRootPath = System.getProperty("user.dir") + File.separator + "audio" + File.separator;
	private String audioPathMove = audioRootPath + "move.wav";
	private String audioPathDrop = audioRootPath + "drop.wav";
	private String audioPathChange = audioRootPath + "change.wav";
	private String audioPathWipeOut = audioRootPath + "wipeout.wav";
	private String audioPathSilence = audioRootPath + "silence.wav";
	/**
	 * 加载声音文件是否成功
	 */
	private boolean isLoadAudioSuccess = false;

	public TetrisFrame() {
		this(20, 10, 30);
	}

	public TetrisFrame(int rows, int cols, int cellSize) {

		super("Swing版俄罗斯方块");

		this.rows = rows;
		this.cols = cols;
		this.cellSize = cellSize;

		plotBlockMarks = new int[rows][cols];
		plotColorMarks = new Color[rows][cols];

		initAudios();

		initComponents();

		intiTimers();

		initImageAndGraphisc();

		drawPlotBackground();

		createBlock();
	}

	/**
	 * 初始化声音
	 */
	private void initAudios() {
		try {
			AudioManager.getAudioManager().addAudio(audioPathMove);
			AudioManager.getAudioManager().addAudio(audioPathDrop);
			AudioManager.getAudioManager().addAudio(audioPathChange);
			AudioManager.getAudioManager().addAudio(audioPathWipeOut);
			AudioManager.getAudioManager().playAudio(audioPathSilence);//先播放一个无声的声音文件，避免真正第一次播放耗时过久，算是一种hack解决办法吧
			isLoadAudioSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			isLoadAudioSuccess = false;
		}
	}

	/**
	 * 初始化组件
	 */
	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		contentPanel.setBackground(windowBackground);
		setContentPane(contentPanel);

		JPanel blockPlotPackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));//防止blockPlotPanel没有绘制的地方被绘制乱七八糟的现象
		blockPlotPackPanel.setOpaque(false);
		contentPanel.add(blockPlotPackPanel, BorderLayout.WEST);

		blockPlotPanel = new JPanel() {
			public void paint(Graphics g) {
				g.drawImage(blockPlotImage, 0, 0, this);
			}
		};
		blockPlotPanel.setPreferredSize(new Dimension(cellSize * cols + 1, cellSize * rows + 1));
		blockPlotPanel.setFocusable(true);
		blockPlotPackPanel.add(blockPlotPanel);

		JPanel eastPanel = new JPanel(new BorderLayout(0, 40));
		eastPanel.setOpaque(false);
		contentPanel.add(eastPanel, BorderLayout.CENTER);

		JPanel nextBlockPackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));//防止nextBlockPanel没有绘制的地方被绘制乱七八糟的现象
		nextBlockPackPanel.setOpaque(false);
		eastPanel.add(nextBlockPackPanel, BorderLayout.NORTH);

		nextBlockPanel = new JPanel() {
			public void paint(Graphics g) {
				g.drawImage(nextBlockImage, 0, 0, this);
			}
		};
		nextBlockPanel.setPreferredSize(new Dimension(cellSize * 4, cellSize * 4));
		nextBlockPackPanel.add(nextBlockPanel, BorderLayout.NORTH);

		JPanel controlPanel = new JPanel(new VerticalLayout(false, 20));
		controlPanel.setOpaque(false);
		eastPanel.add(controlPanel, BorderLayout.CENTER);

		JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		controlPanel.add(scorePanel);

		JLabel scoreMessageLabel = new JLabel("分数：");
		scoreMessageLabel.setFont(scoreMessageLabel.getFont().deriveFont(20f));
		scoreMessageLabel.setForeground(Color.BLACK);
		scorePanel.add(scoreMessageLabel);

		scoreLabel = new JLabel(score + "");
		scoreLabel.setFont(scoreLabel.getFont().deriveFont(20f));
		scoreLabel.setForeground(Color.BLACK);
		scorePanel.add(scoreLabel);

		JPanel wipeOutRowsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		controlPanel.add(wipeOutRowsPanel);

		JLabel wipeOutRowsMessageLabel = new JLabel("行数：");
		wipeOutRowsMessageLabel.setFont(wipeOutRowsMessageLabel.getFont().deriveFont(20f));
		wipeOutRowsMessageLabel.setForeground(Color.BLACK);
		wipeOutRowsPanel.add(wipeOutRowsMessageLabel);

		wipeOutRowsLabel = new JLabel(wipeOutRows + "");
		wipeOutRowsLabel.setFont(wipeOutRowsLabel.getFont().deriveFont(20f));
		wipeOutRowsLabel.setForeground(Color.BLACK);
		wipeOutRowsPanel.add(wipeOutRowsLabel);

		controlPanel.add(Box.createVerticalStrut(0));

		JPanel levelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		controlPanel.add(levelPanel);

		JLabel gameLevelLabel = new JLabel("难度：");
		levelPanel.add(gameLevelLabel);

		String[] difficultyLevelItems = { "极易", "简单", "一般", "困难", "极难" };
		final int[] difficultyLevelDropVelocitys = { 2000, 1000, 500, 250, 125 };
		final int[] difficultyLevelGrowVelocitys = { 200000, 100000, 50000, 25000, 12500 };

		final JComboBox<String> comboBox = new JComboBox<>(difficultyLevelItems);
		comboBox.setPreferredSize(new Dimension(80, 25));
		comboBox.setSelectedIndex(2);
		comboBox.setFocusable(false);
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				if (ItemEvent.SELECTED == e.getStateChange()) {//选中时才触发，没有此判断则会调用两次
					int selectedIndex = comboBox.getSelectedIndex();

					dropBlockVelocity = difficultyLevelDropVelocitys[selectedIndex];
					dropBlockTimer.setDelay(dropBlockVelocity);
					growBlockVelocity = difficultyLevelGrowVelocitys[selectedIndex];
					growBlockTimer.setDelay(growBlockVelocity);

					restartGame();
				}
			}
		});
		levelPanel.add(comboBox);

		JButton startGameButton = new JButton("开始游戏");
		setButtonStyle(startGameButton);
		startGameButton.requestFocus();
		startGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
		});
		controlPanel.add(startGameButton);

		JButton stopGameButton = new JButton("结束游戏");
		setButtonStyle(stopGameButton);
		stopGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopGame();
			}
		});
		controlPanel.add(stopGameButton);

		JButton pauseGameButton = new JButton("暂停游戏");
		setButtonStyle(pauseGameButton);
		pauseGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pauseGame();
			}
		});
		controlPanel.add(pauseGameButton);

		JButton recoverGameButton = new JButton("恢复游戏");
		setButtonStyle(recoverGameButton);
		recoverGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				recoverGame();
			}
		});
		controlPanel.add(recoverGameButton);

		JButton restartGameButton = new JButton("重新开始");
		setButtonStyle(restartGameButton);
		restartGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restartGame();
			}
		});
		controlPanel.add(restartGameButton);

		JButton exitGameButton = new JButton("退出游戏");
		setButtonStyle(exitGameButton);
		exitGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		controlPanel.add(exitGameButton);

		addAllChildComponentTetrisKeyListener(getContentPane());//添加键盘事件监听

		pack();
		setLocationRelativeTo(null);//居中
	}

	private void setButtonStyle(JButton button) {
		//	button.setFocusable(false);
		button.setPreferredSize(new Dimension(100, 25));
	}

	/**
	 * 初始化定时器
	 */
	private void intiTimers() {
		dropBlockTimer = new Timer(dropBlockVelocity, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveDown();
				updatePlotPanel();
			}
		});
		growBlockTimer = new Timer(growBlockVelocity, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				growBlock();
			}
		});
		speedupTimer = new Timer(speedupVelocity, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dropBlockVelocity -= 1;
				if (dropBlockVelocity < 1) {
					dropBlockVelocity = 1;
				}
				dropBlockTimer.setDelay(dropBlockVelocity);
				if (dropBlockTimer.isRunning()) {
					dropBlockTimer.restart();
				}
				growBlockVelocity -= 10;
				if (growBlockVelocity < 1) {
					growBlockVelocity = 1;
				}
				growBlockTimer.setDelay(growBlockVelocity);
				if (growBlockTimer.isRunning()) {
					growBlockTimer.restart();
				}
			}
		});
	}

	/**
	 * 递归将俄罗斯方块的按键事件注册到所有组件中
	 * 
	 * @param container
	 */
	private void addAllChildComponentTetrisKeyListener(Container container) {
		addTetrisKeyListener(container);
		Component[] components = container.getComponents();
		for (int i = 0; i < components.length; i++) {
			Component childComponent = components[i];
			if (childComponent instanceof Container) {
				addAllChildComponentTetrisKeyListener((Container) childComponent);
			} else {
				addTetrisKeyListener(childComponent);
			}
		}
	}

	/**
	 * 为组件添加俄罗斯方块按键事件
	 * 
	 * @param component
	 */
	private void addTetrisKeyListener(Component component) {
		component.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				keyEventHandle(e);
			}
		});
	}

	/**
	 * 处理按键事件
	 * 
	 * @param e
	 */
	private void keyEventHandle(KeyEvent e) {

		if (!isGameRunning || isGamePause || isGameOver) {
			return;
		}

		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
			changeBlockForm();
		} else if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
			moveLeft();
		} else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
			if (!isInvisibleCurrentBlock()) {//方块没有出现不让瞬移
				moveSwiftness();
			}
		} else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
			moveRight();
		}
		updatePlotPanel();
	}

	/**
	 * 更新图基面板
	 */
	private void updatePlotPanel() {
		blockPlotPanel.repaint();
	}

	/**
	 * 更新下一个方块面板
	 */
	private void updateNextPanel() {
		nextBlockPanel.repaint();
	}

	/**
	 * 开始游戏
	 */
	public void startGame() {
		if (isGameRunning && !isGameOver) {
			return;
		}
		isGameRunning = true;
		isGameOver = false;
		isGamePause = false;

		transferBlock();

		dropBlockTimer.start();
		growBlockTimer.start();
		speedupTimer.start();

		playGameStartSound();
	}

	/**
	 * 停止游戏
	 */
	public void stopGame() {
		if (!isGameRunning || isGameOver) {
			return;
		}
		dropBlockTimer.stop();
		growBlockTimer.stop();
		speedupTimer.stop();

		isGameOver = true;
		isGameRunning = false;
		isGamePause = false;
		currentBlock = null;
		score = 0;

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				plotBlockMarks[i][j] = 0;
			}
		}
		drawPlotBackground();
		updatePlotPanel();
		updateScoreView();
	}

	/**
	 * 暂停游戏
	 */
	public void pauseGame() {
		if (!isGameRunning || isGamePause) {
			return;
		}
		isGamePause = true;
		dropBlockTimer.stop();
		growBlockTimer.stop();
		speedupTimer.stop();
	}

	/**
	 * 恢复游戏
	 */
	public void recoverGame() {
		if (!isGameRunning || !isGamePause) {
			return;
		}
		isGamePause = false;
		dropBlockTimer.start();
		growBlockTimer.start();
		speedupTimer.start();
	}

	/**
	 * 重新开始游戏
	 */
	public void restartGame() {
		if (!isGameRunning) {
			return;
		}
		stopGame();
		startGame();
	}

	/**
	 * 游戏是否在运行中
	 * 
	 * @return
	 */
	public boolean isGameRunning() {
		return isGameRunning;
	}

	/**
	 * 游戏是否在暂停中
	 * 
	 * @return
	 */
	public boolean isGamePause() {
		return isGamePause;
	}

	/**
	 * 获得游戏分数
	 * 
	 * @return
	 */
	public int getScore() {
		return score;
	}

	/**
	 * 获取游戏中累计消掉多少行
	 * 
	 * @return
	 */
	public int getWipeOutRows() {
		return wipeOutRows;
	}

	/**
	 * 初始化图片和画笔
	 */
	private void initImageAndGraphisc() {

		blockPlotImage = new BufferedImage(cellSize * cols + 1, cellSize * rows + 1, BufferedImage.TYPE_4BYTE_ABGR);
		nextBlockImage = new BufferedImage(cellSize * 4, cellSize * 4, BufferedImage.TYPE_4BYTE_ABGR);

		blockPlotGraphics = blockPlotImage.createGraphics();
		nextBlockGraphics = nextBlockImage.createGraphics();

		//开启高清晰度绘制，desktophints的map内部包含了抗锯齿，而又不单单只是抗锯齿
		Map desktopHints = (Map) (Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints"));
		if (desktopHints != null) {
			blockPlotGraphics.addRenderingHints(desktopHints);
			nextBlockGraphics.addRenderingHints(desktopHints);
		}
	}

	/**
	 * 创建新方块
	 */
	private void createBlock() {
		if (nextBlock == null) {
			createNextBlock();
		}
		transferBlock();
	}

	/**
	 * 将下一个方块赋值给当前方块，并更新下个方块和虚拟方块
	 */
	public void transferBlock() {
		currentBlock = nextBlock;
		createNextBlock();
		createVertualBlock();
	}

	/**
	 * 创建虚拟方块
	 */
	private void createVertualBlock() {
		try {
			vertualBlock = currentBlock.clone();

			Color color = vertualBlock.getColor();
			vertualBlock.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 10));

			updateVertualBlockIndex();

		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 创建下一个新方块
	 */
	private void createNextBlock() {

		nextBlock = AbstractBlock.createRandomBlock();

		drawNextBlock();

		updateNextPanel();
	}

	/**
	 * 更新游戏图片
	 */
	private void updateGameImange() {

		if (isGameOver()) {
			return;
		}
		drawPlotBackground();
		drawVertualBlock();
		drawCurrentBlock();
	}

	/**
	 * 将矩形 {@link TetrisFrame#tempBlockRect}从外周向内缩小2个像素，此方法没有特别意义，纯粹是为了减少代码量
	 */
	private void subRectangle() {
		subRectangle(tempBlockRect, 2);
	}

	/**
	 * 将矩形 rectangle从外周向内缩小2个像素，此方法没有特别意义，纯粹是为了减少代码量
	 * 
	 * @param rectangle
	 * @param offset
	 */
	private void subRectangle(Rectangle rectangle, int offset) {
		rectangle.x += offset;
		rectangle.y += offset;
		int temp = offset * 2;
		rectangle.width -= temp;
		rectangle.height -= temp;
	}

	/**
	 * 更新图基图片
	 */
	private void drawPlotBackground() {

		blockPlotGraphics.setPaint(plotBackground);
		blockPlotGraphics.fillRect(0, 0, cellSize * cols, cellSize * rows);

		//	绘制网格线
		final Stroke oldStroke = blockPlotGraphics.getStroke();
		blockPlotGraphics.setPaint(plotGridColor);
		blockPlotGraphics.setStroke(plotGridStroke);
		for (int i = 0; i <= rows; i++) {
			int x1 = 0;
			int y1 = i * cellSize;
			int x2 = cellSize * cols;
			int y2 = y1;
			blockPlotGraphics.drawLine(x1, y1, x2, y2);
		}
		for (int i = 0; i <= cols; i++) {
			int x1 = i * cellSize;
			int y1 = 0;
			int x2 = x1;
			int y2 = cellSize * rows;
			blockPlotGraphics.drawLine(x1, y1, x2, y2);
		}
		blockPlotGraphics.setStroke(oldStroke);

		//	绘制背景
		for (int i = 0; i < plotBlockMarks.length; i++) {
			for (int j = 0; j < plotBlockMarks[i].length; j++) {
				if (plotBlockMarks[i][j] == 1) {

					tempBlockRect.x = j * cellSize;
					tempBlockRect.y = i * cellSize;

					tempBlockRect.width = cellSize;
					tempBlockRect.height = cellSize;

					subRectangle();

					Color blockCellBottomColor = plotColorMarks[i][j];

					drawBlockCell(blockPlotGraphics, tempBlockRect, blockCellTopColor, blockCellBottomColor, 0, blockCellBorderColor);
				}
			}
		}
	}

	/**
	 * 绘制当前方块
	 */
	private void drawCurrentBlock() {

		int[][] currentBlockMark = currentBlock.getBlockMark();

		for (int i = 0; i < currentBlockMark.length; i++) {
			for (int j = 0; j < currentBlockMark[i].length; j++) {
				if (currentBlockMark[i][j] == 1) {

					tempBlockRect.x = j * cellSize + currentBlock.getColIndex() * cellSize;
					tempBlockRect.y = i * cellSize + currentBlock.getRowIndex() * cellSize;

					tempBlockRect.width = cellSize;
					tempBlockRect.height = cellSize;

					subRectangle();

					Color blockCellBottomColor = currentBlock.getColor();

					drawBlockCell(blockPlotGraphics, tempBlockRect, blockCellTopColor, blockCellBottomColor, 0, blockCellBorderColor);
				}
			}
		}
	}

	/**
	 * 绘制虚拟方块
	 */
	private void drawVertualBlock() {

		int[][] vertualBlockMark = vertualBlock.getBlockMark();
		for (int i = 0; i < vertualBlockMark.length; i++) {
			for (int j = 0; j < vertualBlockMark[i].length; j++) {
				if (vertualBlockMark[i][j] == 1) {

					tempBlockRect.x = j * cellSize + vertualBlock.getColIndex() * cellSize;
					tempBlockRect.y = i * cellSize + vertualBlock.getRowIndex() * cellSize;

					tempBlockRect.width = cellSize;
					tempBlockRect.height = cellSize;

					subRectangle();

					Color blockCellBottomColor = vertualBlock.getColor();

					drawBlockCell(blockPlotGraphics, tempBlockRect, blockCellTopColor, blockCellBottomColor, 0, vertualBlockCellBorderColor);
				}
			}
		}
	}

	/**
	 * 绘制下一方块
	 */
	private void drawNextBlock() {

		int[][] nextBlockMark = nextBlock.getBlockMark();
		nextBlockGraphics.setPaint(nextBackground);
		nextBlockGraphics.fillRect(0, 0, cellSize * 4, cellSize * 4);

		for (int i = 0; i < nextBlockMark.length; i++) {
			for (int j = 0; j < nextBlockMark[i].length; j++) {
				if (nextBlockMark[i][j] == 1) {

					tempBlockRect.x = j * cellSize;
					tempBlockRect.y = i * cellSize;

					tempBlockRect.width = cellSize;
					tempBlockRect.height = cellSize;

					subRectangle();

					Color blockCellBottomColor = nextBlock.getColor();

					drawBlockCell(nextBlockGraphics, tempBlockRect, blockCellTopColor, blockCellBottomColor, 0, blockCellBorderColor);
				}
			}
		}
	}

	/**
	 * 绘制一个颜色渐变并有边框的方块单元格
	 * 
	 * @param g2d
	 * @param rect
	 *            要绘制的矩形
	 * @param color1
	 * @param color2
	 * @param rotation
	 *            只能为以下值：0、1、2、3，其中0表示color1到color2为从上到下，1表示从左到右，2表示从下到上，3表示从右到左
	 *            。
	 * @param borderPaint
	 *            边框颜色
	 */
	private void drawBlockCell(Graphics2D g2d, Rectangle rect, Color color1, Color color2, int rotation, Paint borderPaint) {

		g2d.translate(rect.x + rect.width / 2, rect.y + rect.height / 2);
		if (rotation != 0) {
			g2d.rotate(rotation * Math.PI / 2);
		}
		RoundRectangle2D rr = new RoundRectangle2D.Double(-rect.width / 2 + (rotation == 2 || rotation == 3 ? -1 : 0), -rect.height / 2
				+ (rotation == 1 || rotation == 2 ? -1 : 0), rect.width, rect.height, 4, 4);
		Rectangle bounds = rr.getBounds();

		g2d.setPaint(new GradientPaint(0, bounds.y, color1, 0, bounds.y + bounds.height, color2));
		g2d.fill(rr);// 填充矩形

		g2d.setPaint(borderPaint);
		g2d.draw(rr);// 画边框

		if (rotation != 0) {
			g2d.rotate(-rotation * Math.PI / 2);
		}
		g2d.translate(-(rect.x + rect.width / 2), -(rect.y + rect.height / 2));
	}

	/**
	 * 更新成绩到界面组件上
	 */
	private void updateScoreView() {
		scoreLabel.setText(score + "");
		wipeOutRowsLabel.setText(wipeOutRows + "");
	}

	/**
	 * 方块左移
	 * 
	 * @return
	 */
	private boolean moveLeft() {

		moveLeftUncheck();
		playMoveLeftSound();

		if (isCurrentBlockOverstep()) {
			moveRightUncheck();
			return false;
		}
		updateVertualBlockIndex();
		updateGameImange();
		return true;
	}

	/**
	 * 无条件方块左移
	 */
	private void moveLeftUncheck() {
		currentBlock.moveLeft();
	}

	/**
	 * 方块右移
	 * 
	 * @return
	 */
	private boolean moveRight() {

		moveRightUncheck();
		playMoveRightSound();

		if (isCurrentBlockOverstep()) {
			moveLeftUncheck();
			return false;
		}
		updateVertualBlockIndex();
		updateGameImange();
		return true;
	}

	/**
	 * 无条件方块右移
	 */
	private void moveRightUncheck() {
		currentBlock.moveRight();
	}

	/**
	 * 方块上移
	 * 
	 * @return
	 */
	private boolean moveUp() {

		moveUpUncheck();
		playMoveUpSound();

		if (isCurrentBlockOverstep()) {
			moveDownUncheck();
			return false;
		}
		updateGameImange();
		return true;
	}

	/**
	 * 无条件方块上移
	 */
	private void moveUpUncheck() {
		currentBlock.moveUp();
	}

	/**
	 * 方块下移
	 * 
	 * @return
	 */
	private boolean moveDown() {

		moveDownUncheck();
		playMoveDownSound();

		if (isCurrentBlockOverstep()) {
			moveUpUncheck();

			fixCurrentBlockToPlot();
			createBlock();
			return false;
		}
		updateGameImange();
		return true;
	}

	/**
	 * 无条件方块下移
	 */
	private void moveDownUncheck() {
		currentBlock.moveDown();
	}

	/**
	 * 方块瞬移（连续下移）
	 */
	private void moveSwiftness() {
		playMoveSwiftnessSound();
		while (moveDown()) {
		}
	}

	/**
	 * 方块变形
	 */
	private void changeBlockForm() {

		playChangeFormSound();

		currentBlock.nextFormIndex();
		if (isCurrentBlockOverstep()) {
			currentBlock.prevFormIndex();
		}
		updateVertualBlockIndex();
		updateGameImange();
	}

	/**
	 * 更新虚拟方块索引
	 */
	private void updateVertualBlockIndex() {

		vertualBlock.setColIndex(currentBlock.getColIndex());//	重设虚拟方块列索引
		vertualBlock.setFormIndex(currentBlock.getFormIndex());//	重设虚拟方块形状索引

		//计算出虚拟方块的行索引
		int vertualBlockRowIndex = currentBlock.getRowIndex();
		vertualBlock.setRowIndex(++vertualBlockRowIndex);

		while (!isVertualBlockOverstep()) {
			vertualBlockRowIndex = vertualBlock.getRowIndex();
			vertualBlock.setRowIndex(++vertualBlockRowIndex);
		}
		vertualBlockRowIndex = vertualBlock.getRowIndex();
		vertualBlock.setRowIndex(--vertualBlockRowIndex);
	}

	/**
	 * 当前方块是否还处于没有显示的状态
	 * 
	 * @return
	 */
	private boolean isInvisibleCurrentBlock() {
		int[][] currentBlockMark = currentBlock.getBlockMark();
		for (int i = 0; i < currentBlockMark.length; i++) {
			for (int j = 0; j < currentBlockMark[i].length; j++) {
				if (currentBlockMark[i][j] == 1) {
					if (i + currentBlock.getRowIndex() >= 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 固定当前方块到图基
	 */
	private void fixCurrentBlockToPlot() {
		int[][] currentBlockMark = currentBlock.getBlockMark();
		for (int i = 0; i < currentBlockMark.length; i++) {
			for (int j = 0; j < currentBlockMark[i].length; j++) {
				if (currentBlockMark[i][j] == 1) {
					if (i + currentBlock.getRowIndex() >= 0) {
						plotBlockMarks[i + currentBlock.getRowIndex()][j + currentBlock.getColIndex()] = 1;
						plotColorMarks[i + currentBlock.getRowIndex()][j + currentBlock.getColIndex()] = currentBlock.getColor();
					}
				}
			}
		}
		updatePlotPanel();
		wipeOutFullRows();
		updateScoreView();
	}

	/**
	 * 消行
	 */
	private void wipeOutFullRows() {
		List<Integer> fillRows = new LinkedList<>();
		for (int i = rows - 1; i > 0; i--) {
			for (int j = 0; j < cols; j++) {
				if (plotBlockMarks[i][j] != 1) {
					break;
				}
				if (j == (cols - 1)) {//这一行满了，需要消掉
					fillRows.add(i);
				}
			}
		}
		boolean play = true;
		for (int i = 0; i < fillRows.size(); i++) {

			int rowIndex = fillRows.get(i) + i;

			if (play) {//消掉多行也只播放一次声音，显示一次动画
				play = false;

				pauseGame();
				playWipeOutFullRowSound();

				startDrawWipeOutRowAnimeThread(blockPlotGraphics, rowIndex);//绘制动画

				startRecoverGameThread(1000 / 7);//1/7秒后恢复游戏
			}

			wipeOutFullRow(rowIndex);//消掉这行
		}

		if (!fillRows.isEmpty()) {//修改成绩

			int sumScore = cols * fillRows.size();
			int accoladeScore = (fillRows.size() - 1) * cols;

			score = score + sumScore + accoladeScore;
			wipeOutRows += fillRows.size();
		}
	}

	/**
	 * 启动一个绘制消行动画的线程
	 * 
	 * @param g
	 * @param rowIndex
	 */
	private void startDrawWipeOutRowAnimeThread(final Graphics2D g, final int rowIndex) {
		new Thread() {
			public void run() {
				int w = cols * cellSize;
				for (int i = 0; i < w; i += (cellSize / 2)) {
					Color color = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).brighter().brighter().brighter();
					drawWipeOutCellAnime(g, i, rowIndex * cellSize, color);
					updatePlotPanel();
					try {
						Thread.sleep(1000 / 60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/**
	 * 启动恢复游戏线程
	 * 
	 * @param time
	 *            在恢复游戏之前暂停的毫秒数
	 */
	private void startRecoverGameThread(final int time) {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(time);//time毫秒后恢复游戏
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				recoverGame();
			}
		}.start();
	}

	/**
	 * 绘制四角星，大小随机，位置随机，填充随机
	 * 
	 * @param g
	 * @param x
	 * @param y
	 * @param paint
	 */
	private void drawWipeOutCellAnime(Graphics2D g, int x, int y, Paint paint) {

		Paint oldPaint = g.getPaint();
		g.setPaint(paint);

		int quarter = cellSize / 4;
		int half = cellSize / 2;

		int sizeOffset = random.nextInt(7) + 2;
		int locationOffset = random.nextInt(sizeOffset);
		y += locationOffset;

		Polygon polygon = new Polygon();

		polygon.addPoint(0 + x + (sizeOffset), 0 + y + (sizeOffset));

		polygon.addPoint(half + x, quarter + y + (sizeOffset));

		polygon.addPoint(cellSize + x + (-sizeOffset * 2), 0 + y + (sizeOffset));

		polygon.addPoint(quarter * 3 + x + (-sizeOffset), half + y);

		polygon.addPoint(cellSize + x + (-sizeOffset * 2), cellSize + y + (-sizeOffset * 2));

		polygon.addPoint(half + x, quarter * 3 + y + (-sizeOffset));

		polygon.addPoint(0 + x + (sizeOffset), cellSize + y + (-sizeOffset * 2));

		polygon.addPoint(quarter + x + (sizeOffset), half + y);

		polygon.addPoint(0 + x + (sizeOffset), 0 + y + (sizeOffset));

		if (random.nextBoolean()) {
			g.fill(polygon);
		} else {
			g.draw(polygon);
		}
		g.setPaint(oldPaint);
	}

	/**
	 * 消掉一行，图基上从当前行row开始，所有方块下沉一行
	 * 
	 * @param row
	 */
	private void wipeOutFullRow(int row) {
		for (int i = row; i > 0; i--) {
			for (int j = 0; j < cols; j++) {
				plotBlockMarks[i][j] = plotBlockMarks[i - 1][j];
				plotColorMarks[i][j] = plotColorMarks[i - 1][j];
			}
		}
	}

	/**
	 * 从图基上的最后一行开始生长一行方块
	 */
	private void growBlock() {
		//	上移所有图基上的标记
		for (int i = 0; i < rows - 1; i++) {
			for (int j = 0; j < cols; j++) {
				plotBlockMarks[i][j] = plotBlockMarks[i + 1][j];
				plotColorMarks[i][j] = plotColorMarks[i + 1][j];
			}
		}
		//	随机一个单元格为默认单元格，其余为方块
		int nulIndex = random.nextInt(cols);
		for (int j = 0; j < cols; j++) {
			plotBlockMarks[rows - 1][j] = (j == nulIndex) ? 0 : 1;
			if (j != nulIndex) {
				//	每个小方块的颜色从所有方块的颜色中随机
				plotColorMarks[rows - 1][j] = AbstractBlock.createRandomBlockColor();
			}
		}
		updatePlotPanel();
	}

	/**
	 * 当前方块是否越界
	 * 
	 * @return
	 */
	private boolean isCurrentBlockOverstep() {
		return isBlockMarkOverstep(currentBlock.getBlockMark(), currentBlock.getRowIndex(), currentBlock.getColIndex());
	}

	/**
	 * 虚拟方块是否越界
	 * 
	 * @return
	 */
	private boolean isVertualBlockOverstep() {
		return isBlockMarkOverstep(vertualBlock.getBlockMark(), vertualBlock.getRowIndex(), vertualBlock.getColIndex());
	}

	/**
	 * 是否越界
	 * 
	 * @return
	 */
	private boolean isBlockMarkOverstep(int[][] blockMark, int rowIndex, int colIndex) {

		for (int i = 0; i < blockMark.length; i++) {
			for (int j = 0; j < blockMark[i].length; j++) {

				if (blockMark[i][j] == 1) {// 方块图基中为1

					int tempRowIndex = i + rowIndex;
					int tempColIndex = j + colIndex;

					// 出现以下情况为超出边界范围
					if (tempRowIndex < -blockMark.length) {
						return true;
					}
					if (tempRowIndex >= rows) {
						return true;
					}
					if (tempColIndex < 0) {
						return true;
					}
					if (tempColIndex >= cols) {
						return true;
					}

					// 出现以下情况为撞上别的方块
					if (tempRowIndex > 0) {
						if (plotBlockMarks[tempRowIndex][tempColIndex] == 1) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 游戏是否结束
	 * 
	 * @return
	 */
	private boolean isGameOver() {
		if (isGameOver) {
			return isGameOver;
		}
		for (int j = 0; j < cols; j++) {
			if (plotBlockMarks[0][j] == 1) {
				stopGame();
				playGameOverSound();
				return true;
			}
		}
		return false;
	}

	/**
	 * 当变形时发出声音
	 */
	private void playChangeFormSound() {
		if (isLoadAudioSuccess) {
			AudioManager.getAudioManager().playAudio(audioPathChange);
		}
	}

	/**
	 * 当左移时发出声音
	 */
	private void playMoveLeftSound() {
		if (isLoadAudioSuccess) {
			AudioManager.getAudioManager().playAudio(audioPathMove);
		}
	}

	/**
	 * 当右移时发出声音
	 */
	private void playMoveRightSound() {
		if (isLoadAudioSuccess) {
			AudioManager.getAudioManager().playAudio(audioPathMove);
		}
	}

	/**
	 * 当上移时发出声音
	 */
	private void playMoveUpSound() {
	}

	/**
	 * 当下移时发出声音
	 */
	private void playMoveDownSound() {
	}

	/**
	 * 当瞬移时发出声音
	 */
	private void playMoveSwiftnessSound() {
		if (isLoadAudioSuccess) {
			AudioManager.getAudioManager().playAudio(audioPathDrop);
		}
	}

	/**
	 * 当消行时发出声音
	 */
	private void playWipeOutFullRowSound() {
		if (isLoadAudioSuccess) {
			AudioManager.getAudioManager().playAudio(audioPathWipeOut);
		}
	}

	/**
	 * 当用户开始玩的时候发出声音
	 */
	private void playGameStartSound() {
		if (isLoadAudioSuccess) {
			//play
		}
	}

	/**
	 * 当用户玩Over的时候发出声音
	 */
	private void playGameOverSound() {
		if (isLoadAudioSuccess) {
			//	Toolkit.getDefaultToolkit().beep();
		}
	}
}

// //////////---------------------------------------------------

/**
 * 一个简单的垂直布局，用于代替BoxLayout垂直布局
 * 
 * @author Tang
 * 
 */
class VerticalLayout implements LayoutManager {

	protected boolean useSizeFromParent;
	protected int gap;

	public VerticalLayout() {
		this(true);
	}

	public VerticalLayout(final boolean useParent) {
		this(useParent, 5);
	}

	public VerticalLayout(final boolean useParent, int gap) {
		this.useSizeFromParent = useParent;
		this.gap = gap;
	}

	public void addLayoutComponent(final String name, final Component comp) {
		// ignored
	}

	public void removeLayoutComponent(final Component comp) {
		// ignored
	}

	public Dimension preferredLayoutSize(final Container parent) {
		synchronized (parent.getTreeLock()) {
			final Insets ins = parent.getInsets();
			final Component[] comps = parent.getComponents();
			int height = ins.top + ins.bottom;
			int width = ins.left + ins.right;
			for (int i = 0; i < comps.length; i++) {
				if (comps[i].isVisible() == false) {
					continue;
				}
				final Dimension pref = comps[i].getPreferredSize();
				height += pref.height;
				height += gap;
				if (pref.width > width) {
					width = pref.width;
				}
			}
			return new Dimension(width + ins.left + ins.right, height + ins.top + ins.bottom);
		}
	}

	public Dimension minimumLayoutSize(final Container parent) {
		synchronized (parent.getTreeLock()) {
			final Insets ins = parent.getInsets();
			final Component[] comps = parent.getComponents();
			int height = ins.top + ins.bottom;
			int width = ins.left + ins.right;
			for (int i = 0; i < comps.length; i++) {
				if (comps[i].isVisible() == false) {
					continue;
				}
				final Dimension min = comps[i].getMinimumSize();
				height += min.height;
				height += gap;
				if (min.width > width) {
					width = min.width;
				}
			}
			return new Dimension(width + ins.left + ins.right, height + ins.top + ins.bottom);
		}
	}

	public boolean isUseSizeFromParent() {
		return this.useSizeFromParent;
	}

	public int getGap() {
		return gap;
	}

	public void layoutContainer(final Container parent) {
		synchronized (parent.getTreeLock()) {
			final Insets ins = parent.getInsets();
			final int insHorizontal = ins.left + ins.right;

			final int width;
			if (isUseSizeFromParent()) {
				final Rectangle bounds = parent.getBounds();
				width = bounds.width - insHorizontal;
			} else {
				width = preferredLayoutSize(parent).width - insHorizontal;
			}
			final Component[] comps = parent.getComponents();

			int y = ins.top;
			for (int i = 0; i < comps.length; i++) {
				final Component c = comps[i];
				if (c.isVisible() == false) {
					continue;
				}
				final Dimension dim = c.getPreferredSize();
				c.setBounds(ins.left, y, width, dim.height);
				y += dim.height;
				y += gap;
			}
		}
	}
}

/**
 * 一个简单的声音管理类，主要用途是将音乐文件的数据缓存到内存，而不用每次播放都去读文件。
 * 
 * @author Tang
 * 
 */
class AudioManager {

	private static AudioManager audioManager = new AudioManager();

	private Map<String, AudioData> audioMap = new HashMap<>();

	private AudioManager() {
	}

	public static AudioManager getAudioManager() {
		return audioManager;
	}

	public boolean contains(String path) {
		return audioMap.containsKey(path);
	}

	public void addAudio(String path) {
		if (!audioMap.containsKey(path)) {
			try (InputStream is = new FileInputStream(path); AudioStream as = new AudioStream(is)) {
				audioMap.put(path, as.getData());
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void addAudio(Collection<String> paths) {
		for (String path : paths) {
			addAudio(path);
		}
	}

	public void removeAudio(String path) {
		audioMap.remove(path);
	}

	public void removeAllAudio() {
		audioMap.clear();
	}

	public void playAudio(String path) {
		if (!contains(path)) {
			addAudio(path);
		}
		try {
			AudioPlayer.player.start(new AudioDataStream(audioMap.get(path)));
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public void playAllAudio() {
		for (String key : audioMap.keySet()) {
			playAudio(key);
		}
	}
}

/**
 * 所有方块枚举
 * 
 * @author Tang
 */
enum BlockEnum {

	//	Point(BlockPoint.class.getName(),new Color(200, 150, 100)),
	/**
	 * 正方形： 1 1<br>
	 * 1 1
	 */
	Square(BlockSquare.class.getName(), new Color(100, 200, 200)),

	/**
	 * 线条形： 1 1 1 1
	 */
	Line(BlockLine.class.getName(), new Color(200, 100, 200)),

	/**
	 * 三角形： 0 1 0<br>
	 * 1 1 1
	 */
	Trident(BlockTrident.class.getName(), new Color(200, 200, 100)),

	/**
	 * 顺时针楼梯形： 1 1 0<br>
	 * 0 1 1
	 */
	StairCW(BlockStairClockwise.class.getName(), new Color(200, 100, 100)),

	/**
	 * 逆时针楼梯形： 0 1 1<br>
	 * 1 1 0
	 */
	StairCCW(BlockStairCounterClockwise.class.getName(), new Color(100, 200, 100)),

	/**
	 * 顺时针直角形： 0 0 1<br>
	 * 1 1 1
	 */
	AngleCW(BlockAngleClockwise.class.getName(), new Color(100, 100, 200)),

	/**
	 * 逆时针直角形： 1 0 0<br>
	 * 1 1 1
	 */
	AngleCCW(BlockAngleCounterClockwise.class.getName(), new Color(150, 150, 150)), ;

	private String className;
	private Color color;

	private BlockEnum(String className, Color color) {
		this.className = className;
		this.color = color;
	}

	public String getClassName() {
		return className;
	}

	public Color getColor() {
		return color;
	}
}

/**
 * 方块抽象类
 * 
 * @author Tang
 */
abstract class AbstractBlock implements Cloneable {

	/**
	 * 方块随机器，用于随机创建一个方块以及随机一个方块的形状
	 */
	protected static Random random = TetrisFrame.random;

	/**
	 * 方块颜色
	 */
	protected Color color;
	/**
	 * 在图基上的垂直方向位置
	 */
	protected int rowIndex = -4;// 初始-4表示一开始是完全看不见的
	/**
	 * 在图基上的水平方向位置
	 */
	protected int colIndex = 3;// 初始为3只是个为了让方块一出来时是相对比较居中的位置，当然初始4也行
	/**
	 * 方块的形状索引
	 */
	protected int formIndex = random.nextInt(getFormCount());//随机一个形状

	/**
	 * 标记这个方块的数组，第一维是形状，第二维是行，第三维是列；数据只能为0或1，数据如果为0表示不是方块，为1表示是方块。
	 */
	protected int[][][] blockMarks = new int[getFormCount()][4][4];

	public static AbstractBlock createRandomBlock() throws RuntimeException {
		BlockEnum[] values = BlockEnum.values();
		int randomBlockIndex = random.nextInt(values.length);//随机一个方块
		String className = values[randomBlockIndex].getClassName();
		Class<?> forName;
		try {
			forName = Class.forName(className);
			AbstractBlock block = (AbstractBlock) forName.newInstance();
			return block;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static Color createRandomBlockColor() {
		BlockEnum[] values = BlockEnum.values();
		int randomBlockIndex = random.nextInt(values.length);//随机一个颜色
		return values[randomBlockIndex].getColor();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public int getColIndex() {
		return colIndex;
	}

	public void setColIndex(int colIndex) {
		this.colIndex = colIndex;
	}

	public int getFormIndex() {
		return formIndex;
	}

	public void setFormIndex(int formIndex) {
		this.formIndex = formIndex;
	}

	/**
	 * 将形状索引变为下一个形状
	 */
	public void nextFormIndex() {
		formIndex++;
		if (formIndex >= getFormCount()) {
			formIndex = 0;
		}
	}

	/**
	 * 将形状索引变为上一个形状
	 */
	public void prevFormIndex() {
		formIndex--;
		if (formIndex < 0) {
			formIndex = getFormCount() - 1;
		}
	}

	public void moveLeft() {
		setColIndex(getColIndex() - 1);
	}

	public void moveRight() {
		setColIndex(getColIndex() + 1);
	}

	public void moveUp() {
		setRowIndex(getRowIndex() - 1);
	}

	public void moveDown() {
		setRowIndex(getRowIndex() + 1);
	}

	/**
	 * 获取此方块的标记
	 * 
	 * @return
	 */
	public int[][] getBlockMark() {
		return blockMarks[formIndex];
	}

	public abstract int getFormCount();// 此方块的变形形状总个数

	@Override
	public AbstractBlock clone() throws CloneNotSupportedException {
		AbstractBlock clone = (AbstractBlock) super.clone();
		clone.color = new Color(color.getRGB());
		//		多维数组必须这样才能是真正的clone（深克隆）
		clone.blockMarks = blockMarks.clone();
		for (int i = 0; i < blockMarks.length; i++) {
			clone.blockMarks[i] = blockMarks[i].clone();
			for (int j = 0; j < blockMarks[i].length; j++) {
				clone.blockMarks[i][j] = blockMarks[i][j].clone();
			}
		}
		return clone;
	}
}

/**
 * 小点 <br>
 */
//class BlockPoint extends AbstractBlock {
//	{
//		color = BlockEnum.Point.getColor();
//
//		int[][] block = new int[4][4];
//		block[0] = new int[] { 0, 0, 0, 0 };
//		block[1] = new int[] { 0, 0, 1, 0 };
//		block[2] = new int[] { 0, 0, 0, 0 };
//		block[3] = new int[] { 0, 0, 0, 0 };
//
//		blockMarks[0] = block;
//	}
//
//	@Override
//	public int getFormCount() {
//		return 1;
//	}
//}

/**
 * 正方形： <br>
 * 1 1<br>
 * 1 1
 */
class BlockSquare extends AbstractBlock {
	{
		color = BlockEnum.Square.getColor();

		int[][] block = new int[4][4];
		block[0] = new int[] { 0, 0, 0, 0 };
		block[1] = new int[] { 0, 1, 1, 0 };
		block[2] = new int[] { 0, 1, 1, 0 };
		block[3] = new int[] { 0, 0, 0, 0 };

		blockMarks[0] = block;
	}

	@Override
	public int getFormCount() {
		return 1;
	}
}

/**
 * 线条形： 1 1 1 1
 */
class BlockLine extends AbstractBlock {
	{
		color = BlockEnum.Line.getColor();

		int[][] horizontalBlock = new int[4][4];
		horizontalBlock[0] = new int[] { 0, 0, 0, 0 };
		horizontalBlock[1] = new int[] { 1, 1, 1, 1 };
		horizontalBlock[2] = new int[] { 0, 0, 0, 0 };
		horizontalBlock[3] = new int[] { 0, 0, 0, 0 };

		int[][] verticalBlock = new int[4][4];
		verticalBlock[0] = new int[] { 0, 0, 1, 0 };
		verticalBlock[1] = new int[] { 0, 0, 1, 0 };
		verticalBlock[2] = new int[] { 0, 0, 1, 0 };
		verticalBlock[3] = new int[] { 0, 0, 1, 0 };

		blockMarks[0] = horizontalBlock;
		blockMarks[1] = verticalBlock;
	}

	@Override
	public int getFormCount() {
		return 2;
	}
}

/**
 * 三角形：<br>
 * 0 1 0<br>
 * 1 1 1
 */
class BlockTrident extends AbstractBlock {
	{
		color = BlockEnum.Trident.getColor();

		int[][] topBlock = new int[4][4];
		topBlock[0] = new int[] { 0, 0, 1, 0 };
		topBlock[1] = new int[] { 0, 1, 1, 1 };
		topBlock[2] = new int[] { 0, 0, 0, 0 };
		topBlock[3] = new int[] { 0, 0, 0, 0 };

		int[][] rightBlock = new int[4][4];
		rightBlock[0] = new int[] { 0, 0, 1, 0 };
		rightBlock[1] = new int[] { 0, 0, 1, 1 };
		rightBlock[2] = new int[] { 0, 0, 1, 0 };
		rightBlock[3] = new int[] { 0, 0, 0, 0 };

		int[][] bottomBlock = new int[4][4];
		bottomBlock[0] = new int[] { 0, 0, 0, 0 };
		bottomBlock[1] = new int[] { 0, 1, 1, 1 };
		bottomBlock[2] = new int[] { 0, 0, 1, 0 };
		bottomBlock[3] = new int[] { 0, 0, 0, 0 };

		int[][] leftBlock = new int[4][4];
		leftBlock[0] = new int[] { 0, 0, 1, 0 };
		leftBlock[1] = new int[] { 0, 1, 1, 0 };
		leftBlock[2] = new int[] { 0, 0, 1, 0 };
		leftBlock[3] = new int[] { 0, 0, 0, 0 };

		blockMarks[0] = topBlock;
		blockMarks[1] = rightBlock;
		blockMarks[2] = bottomBlock;
		blockMarks[3] = leftBlock;
	}

	@Override
	public int getFormCount() {
		return 4;
	}
}

/**
 * 顺时针楼梯形： <br>
 * 1 1 0<br>
 * 0 1 1
 */
class BlockStairClockwise extends AbstractBlock {
	{
		color = BlockEnum.StairCW.getColor();

		int[][] horizontalBlock = new int[4][4];
		horizontalBlock[0] = new int[] { 0, 0, 0, 0 };
		horizontalBlock[1] = new int[] { 1, 1, 0, 0 };
		horizontalBlock[2] = new int[] { 0, 1, 1, 0 };
		horizontalBlock[3] = new int[] { 0, 0, 0, 0 };

		int[][] verticalBlock = new int[4][4];
		verticalBlock[0] = new int[] { 0, 0, 1, 0 };
		verticalBlock[1] = new int[] { 0, 1, 1, 0 };
		verticalBlock[2] = new int[] { 0, 1, 0, 0 };
		verticalBlock[3] = new int[] { 0, 0, 0, 0 };

		blockMarks[0] = horizontalBlock;
		blockMarks[1] = verticalBlock;
	}

	@Override
	public int getFormCount() {
		return 2;
	}
}

/**
 * 逆时针楼梯形： <br>
 * 0 1 1<br>
 * 1 1 0
 */
class BlockStairCounterClockwise extends AbstractBlock {
	{
		color = BlockEnum.StairCCW.getColor();

		int[][] horizontalBlock = new int[4][4];
		horizontalBlock[0] = new int[] { 0, 0, 0, 0 };
		horizontalBlock[1] = new int[] { 0, 1, 1, 0 };
		horizontalBlock[2] = new int[] { 1, 1, 0, 0 };
		horizontalBlock[3] = new int[] { 0, 0, 0, 0 };

		int[][] verticalBlock = new int[4][4];
		verticalBlock[0] = new int[] { 0, 1, 0, 0 };
		verticalBlock[1] = new int[] { 0, 1, 1, 0 };
		verticalBlock[2] = new int[] { 0, 0, 1, 0 };
		verticalBlock[3] = new int[] { 0, 0, 0, 0 };

		blockMarks[0] = horizontalBlock;
		blockMarks[1] = verticalBlock;
	}

	@Override
	public int getFormCount() {
		return 2;
	}
}

/**
 * 顺时针直角形：<br>
 * 0 0 1<br>
 * 1 1 1
 */
class BlockAngleClockwise extends AbstractBlock {
	{
		color = BlockEnum.AngleCW.getColor();

		int[][] topBlock = new int[4][4];
		topBlock[0] = new int[] { 0, 1, 0, 0 };
		topBlock[1] = new int[] { 0, 1, 0, 0 };
		topBlock[2] = new int[] { 0, 1, 1, 0 };
		topBlock[3] = new int[] { 0, 0, 0, 0 };

		int[][] rightBlock = new int[4][4];
		rightBlock[0] = new int[] { 0, 0, 0, 0 };
		rightBlock[1] = new int[] { 0, 1, 1, 1 };
		rightBlock[2] = new int[] { 0, 1, 0, 0 };
		rightBlock[3] = new int[] { 0, 0, 0, 0 };

		int[][] bottomBlock = new int[4][4];
		bottomBlock[0] = new int[] { 0, 0, 0, 0 };
		bottomBlock[1] = new int[] { 0, 1, 1, 0 };
		bottomBlock[2] = new int[] { 0, 0, 1, 0 };
		bottomBlock[3] = new int[] { 0, 0, 1, 0 };

		int[][] leftBlock = new int[4][4];
		leftBlock[0] = new int[] { 0, 0, 0, 0 };
		leftBlock[1] = new int[] { 0, 0, 1, 0 };
		leftBlock[2] = new int[] { 1, 1, 1, 0 };
		leftBlock[3] = new int[] { 0, 0, 0, 0 };

		blockMarks[0] = topBlock;
		blockMarks[1] = rightBlock;
		blockMarks[2] = bottomBlock;
		blockMarks[3] = leftBlock;
	}

	@Override
	public int getFormCount() {
		return 4;
	}
}

/**
 * 逆时针直角形： <br>
 * 1 0 0<br>
 * 1 1 1
 */
class BlockAngleCounterClockwise extends AbstractBlock {
	{
		color = BlockEnum.AngleCCW.getColor();

		int[][] topBlock = new int[4][4];
		topBlock[0] = new int[] { 0, 0, 1, 0 };
		topBlock[1] = new int[] { 0, 0, 1, 0 };
		topBlock[2] = new int[] { 0, 1, 1, 0 };
		topBlock[3] = new int[] { 0, 0, 0, 0 };

		int[][] rightBlock = new int[4][4];
		rightBlock[0] = new int[] { 0, 0, 0, 0 };
		rightBlock[1] = new int[] { 0, 1, 0, 0 };
		rightBlock[2] = new int[] { 0, 1, 1, 1 };
		rightBlock[3] = new int[] { 0, 0, 0, 0 };

		int[][] bottomBlock = new int[4][4];
		bottomBlock[0] = new int[] { 0, 0, 0, 0 };
		bottomBlock[1] = new int[] { 0, 1, 1, 0 };
		bottomBlock[2] = new int[] { 0, 1, 0, 0 };
		bottomBlock[3] = new int[] { 0, 1, 0, 0 };

		int[][] leftBlock = new int[4][4];
		leftBlock[0] = new int[] { 0, 0, 0, 0 };
		leftBlock[1] = new int[] { 1, 1, 1, 0 };
		leftBlock[2] = new int[] { 0, 0, 1, 0 };
		leftBlock[3] = new int[] { 0, 0, 0, 0 };

		blockMarks[0] = topBlock;
		blockMarks[1] = rightBlock;
		blockMarks[2] = bottomBlock;
		blockMarks[3] = leftBlock;
	}

	@Override
	public int getFormCount() {
		return 4;
	}
}
