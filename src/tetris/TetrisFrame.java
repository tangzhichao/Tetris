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
 * ����˹����������<br>
 * ��δʵ�֣� �����������á��ܴ�͸�ķ��顢ը�����顢����ǹ��������㣩�����а�����߷ּ�¼��ʾ�� ��Ϸ��ʼ��������Ϸover����<br>
 * 
 * @note����ʱ����д����д�������������<br>
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
	 * ��Ԫ���С����λ�����أ�
	 */
	private int cellSize = 30;
	/**
	 * ����ͼ��������
	 */
	private int rows = 20;
	/**
	 * ����ͼ��������
	 */
	private int cols = 10;

	/**
	 * ����ı���
	 */
	private Color windowBackground = new Color(100, 100, 100);

	/**
	 * ����ͼ���ı���
	 */
	private Color plotBackground = Color.WHITE;

	/**
	 * �¸�����ı���
	 */
	private Color nextBackground = windowBackground;

	/**
	 * �����е�ÿ��С����Ľ���ɫ��������ʼ��ɫ
	 */
	private Color blockCellTopColor = Color.WHITE;
	/**
	 * ��ǰ�����е�ÿ��С����ı߿���ɫ
	 */
	private Color blockCellBorderColor = Color.GRAY;

	/**
	 * ���ⷽ���ÿ��С����ı߿���ɫ
	 */
	private Color vertualBlockCellBorderColor = new Color(128, 128, 128, 20);

	/**
	 * ����ͼ�����е���������ɫ���������������ָ�������
	 */
	private Color plotGridColor = Color.LIGHT_GRAY;
	/**
	 * ����ͼ�����е������߹��������������������ָ�������
	 */
	private Stroke plotGridStroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 1.5f, 4f }, 0f);

	/**
	 * ����ͼ�����
	 */
	private JPanel blockPlotPanel;
	/**
	 * �¸��������
	 */
	private JPanel nextBlockPanel;

	/**
	 * ��ʾ�����ı�ǩ
	 */
	private JLabel scoreLabel;

	/**
	 * ��ʾ�ۼ����������е����
	 */
	private JLabel wipeOutRowsLabel;

	/**
	 * ����ͼ��ͼƬ
	 */
	private BufferedImage blockPlotImage;
	/**
	 * �¸�����ͼƬ
	 */
	private BufferedImage nextBlockImage;

	/**
	 * ����ͼ�����
	 */
	private Graphics2D blockPlotGraphics;
	/**
	 * �¸�������
	 */
	private Graphics2D nextBlockGraphics;

	/**
	 * ����ͼ����ǣ�ʹ��1��ʾ�Ƿ��鵥Ԫ��0��ʾ��Ĭ�ϵ�Ԫ��
	 */
	private int[][] plotBlockMarks;
	/**
	 * ��ɫͼ����ǣ�����ͼ�������ֵΪ1�ĵ�Ԫ�����ڴ��������ҵ����Ӧ����ɫ
	 */
	private Color[][] plotColorMarks;

	/**
	 * ��ǰ����
	 */
	private AbstractBlock currentBlock;
	/**
	 * ���ⷽ��
	 */
	private AbstractBlock vertualBlock;
	/**
	 * ��һ������
	 */
	private AbstractBlock nextBlock;

	/**
	 * �������ƶ�ʱ��
	 */
	private Timer dropBlockTimer;
	/**
	 * �������ƶ�ʱ������ĺ�����
	 */
	private int dropBlockVelocity = 500;

	/**
	 * ����������ʱ��
	 */
	private Timer growBlockTimer;

	/**
	 * ����������ʱ������ĺ�����
	 */
	private int growBlockVelocity = 50000;

	/**
	 * ��"���ƶ�ʱ��"��"������ʱ��"���ٵĶ�ʱ��
	 */
	private Timer speedupTimer;
	/**
	 * ��"���ƶ�ʱ��"��"������ʱ��"���ٵĶ�ʱ������ĺ�����
	 */
	private int speedupVelocity = 5000;

	/**
	 * ����
	 */
	private int score = 0;

	/**
	 * �ۼ�����������
	 */
	private int wipeOutRows = 0;

	/**
	 * ��Ϸ�Ƿ���������
	 */
	private boolean isGameRunning = false;
	/**
	 * ��Ϸ�Ƿ�����ͣ��
	 */
	private boolean isGamePause = false;

	/**
	 * ������Ϸ������״̬
	 */
	private boolean isGameOver = false;

	/**
	 * ���������ʱ������Ϊ�˽�Լ�ڴ������
	 */
	private final Rectangle tempBlockRect = new Rectangle();

	/**
	 * �����ļ�·��
	 */
	private String audioRootPath = System.getProperty("user.dir") + File.separator + "audio" + File.separator;
	private String audioPathMove = audioRootPath + "move.wav";
	private String audioPathDrop = audioRootPath + "drop.wav";
	private String audioPathChange = audioRootPath + "change.wav";
	private String audioPathWipeOut = audioRootPath + "wipeout.wav";
	private String audioPathSilence = audioRootPath + "silence.wav";
	/**
	 * ���������ļ��Ƿ�ɹ�
	 */
	private boolean isLoadAudioSuccess = false;

	public TetrisFrame() {
		this(20, 10, 30);
	}

	public TetrisFrame(int rows, int cols, int cellSize) {

		super("Swing�����˹����");

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
	 * ��ʼ������
	 */
	private void initAudios() {
		try {
			AudioManager.getAudioManager().addAudio(audioPathMove);
			AudioManager.getAudioManager().addAudio(audioPathDrop);
			AudioManager.getAudioManager().addAudio(audioPathChange);
			AudioManager.getAudioManager().addAudio(audioPathWipeOut);
			AudioManager.getAudioManager().playAudio(audioPathSilence);//�Ȳ���һ�������������ļ�������������һ�β��ź�ʱ���ã�����һ��hack����취��
			isLoadAudioSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
			isLoadAudioSuccess = false;
		}
	}

	/**
	 * ��ʼ�����
	 */
	private void initComponents() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);

		JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		contentPanel.setBackground(windowBackground);
		setContentPane(contentPanel);

		JPanel blockPlotPackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));//��ֹblockPlotPanelû�л��Ƶĵط����������߰��������
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

		JPanel nextBlockPackPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));//��ֹnextBlockPanelû�л��Ƶĵط����������߰��������
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

		JLabel scoreMessageLabel = new JLabel("������");
		scoreMessageLabel.setFont(scoreMessageLabel.getFont().deriveFont(20f));
		scoreMessageLabel.setForeground(Color.BLACK);
		scorePanel.add(scoreMessageLabel);

		scoreLabel = new JLabel(score + "");
		scoreLabel.setFont(scoreLabel.getFont().deriveFont(20f));
		scoreLabel.setForeground(Color.BLACK);
		scorePanel.add(scoreLabel);

		JPanel wipeOutRowsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		controlPanel.add(wipeOutRowsPanel);

		JLabel wipeOutRowsMessageLabel = new JLabel("������");
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

		JLabel gameLevelLabel = new JLabel("�Ѷȣ�");
		levelPanel.add(gameLevelLabel);

		String[] difficultyLevelItems = { "����", "��", "һ��", "����", "����" };
		final int[] difficultyLevelDropVelocitys = { 2000, 1000, 500, 250, 125 };
		final int[] difficultyLevelGrowVelocitys = { 200000, 100000, 50000, 25000, 12500 };

		final JComboBox<String> comboBox = new JComboBox<>(difficultyLevelItems);
		comboBox.setPreferredSize(new Dimension(80, 25));
		comboBox.setSelectedIndex(2);
		comboBox.setFocusable(false);
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				if (ItemEvent.SELECTED == e.getStateChange()) {//ѡ��ʱ�Ŵ�����û�д��ж�����������
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

		JButton startGameButton = new JButton("��ʼ��Ϸ");
		setButtonStyle(startGameButton);
		startGameButton.requestFocus();
		startGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startGame();
			}
		});
		controlPanel.add(startGameButton);

		JButton stopGameButton = new JButton("������Ϸ");
		setButtonStyle(stopGameButton);
		stopGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopGame();
			}
		});
		controlPanel.add(stopGameButton);

		JButton pauseGameButton = new JButton("��ͣ��Ϸ");
		setButtonStyle(pauseGameButton);
		pauseGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pauseGame();
			}
		});
		controlPanel.add(pauseGameButton);

		JButton recoverGameButton = new JButton("�ָ���Ϸ");
		setButtonStyle(recoverGameButton);
		recoverGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				recoverGame();
			}
		});
		controlPanel.add(recoverGameButton);

		JButton restartGameButton = new JButton("���¿�ʼ");
		setButtonStyle(restartGameButton);
		restartGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restartGame();
			}
		});
		controlPanel.add(restartGameButton);

		JButton exitGameButton = new JButton("�˳���Ϸ");
		setButtonStyle(exitGameButton);
		exitGameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		controlPanel.add(exitGameButton);

		addAllChildComponentTetrisKeyListener(getContentPane());//��Ӽ����¼�����

		pack();
		setLocationRelativeTo(null);//����
	}

	private void setButtonStyle(JButton button) {
		//	button.setFocusable(false);
		button.setPreferredSize(new Dimension(100, 25));
	}

	/**
	 * ��ʼ����ʱ��
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
	 * �ݹ齫����˹����İ����¼�ע�ᵽ���������
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
	 * Ϊ�����Ӷ���˹���鰴���¼�
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
	 * �������¼�
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
			if (!isInvisibleCurrentBlock()) {//����û�г��ֲ���˲��
				moveSwiftness();
			}
		} else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
			moveRight();
		}
		updatePlotPanel();
	}

	/**
	 * ����ͼ�����
	 */
	private void updatePlotPanel() {
		blockPlotPanel.repaint();
	}

	/**
	 * ������һ���������
	 */
	private void updateNextPanel() {
		nextBlockPanel.repaint();
	}

	/**
	 * ��ʼ��Ϸ
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
	 * ֹͣ��Ϸ
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
	 * ��ͣ��Ϸ
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
	 * �ָ���Ϸ
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
	 * ���¿�ʼ��Ϸ
	 */
	public void restartGame() {
		if (!isGameRunning) {
			return;
		}
		stopGame();
		startGame();
	}

	/**
	 * ��Ϸ�Ƿ���������
	 * 
	 * @return
	 */
	public boolean isGameRunning() {
		return isGameRunning;
	}

	/**
	 * ��Ϸ�Ƿ�����ͣ��
	 * 
	 * @return
	 */
	public boolean isGamePause() {
		return isGamePause;
	}

	/**
	 * �����Ϸ����
	 * 
	 * @return
	 */
	public int getScore() {
		return score;
	}

	/**
	 * ��ȡ��Ϸ���ۼ�����������
	 * 
	 * @return
	 */
	public int getWipeOutRows() {
		return wipeOutRows;
	}

	/**
	 * ��ʼ��ͼƬ�ͻ���
	 */
	private void initImageAndGraphisc() {

		blockPlotImage = new BufferedImage(cellSize * cols + 1, cellSize * rows + 1, BufferedImage.TYPE_4BYTE_ABGR);
		nextBlockImage = new BufferedImage(cellSize * 4, cellSize * 4, BufferedImage.TYPE_4BYTE_ABGR);

		blockPlotGraphics = blockPlotImage.createGraphics();
		nextBlockGraphics = nextBlockImage.createGraphics();

		//�����������Ȼ��ƣ�desktophints��map�ڲ������˿���ݣ����ֲ�����ֻ�ǿ����
		Map desktopHints = (Map) (Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints"));
		if (desktopHints != null) {
			blockPlotGraphics.addRenderingHints(desktopHints);
			nextBlockGraphics.addRenderingHints(desktopHints);
		}
	}

	/**
	 * �����·���
	 */
	private void createBlock() {
		if (nextBlock == null) {
			createNextBlock();
		}
		transferBlock();
	}

	/**
	 * ����һ�����鸳ֵ����ǰ���飬�������¸���������ⷽ��
	 */
	public void transferBlock() {
		currentBlock = nextBlock;
		createNextBlock();
		createVertualBlock();
	}

	/**
	 * �������ⷽ��
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
	 * ������һ���·���
	 */
	private void createNextBlock() {

		nextBlock = AbstractBlock.createRandomBlock();

		drawNextBlock();

		updateNextPanel();
	}

	/**
	 * ������ϷͼƬ
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
	 * ������ {@link TetrisFrame#tempBlockRect}������������С2�����أ��˷���û���ر����壬������Ϊ�˼��ٴ�����
	 */
	private void subRectangle() {
		subRectangle(tempBlockRect, 2);
	}

	/**
	 * ������ rectangle������������С2�����أ��˷���û���ر����壬������Ϊ�˼��ٴ�����
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
	 * ����ͼ��ͼƬ
	 */
	private void drawPlotBackground() {

		blockPlotGraphics.setPaint(plotBackground);
		blockPlotGraphics.fillRect(0, 0, cellSize * cols, cellSize * rows);

		//	����������
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

		//	���Ʊ���
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
	 * ���Ƶ�ǰ����
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
	 * �������ⷽ��
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
	 * ������һ����
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
	 * ����һ����ɫ���䲢�б߿�ķ��鵥Ԫ��
	 * 
	 * @param g2d
	 * @param rect
	 *            Ҫ���Ƶľ���
	 * @param color1
	 * @param color2
	 * @param rotation
	 *            ֻ��Ϊ����ֵ��0��1��2��3������0��ʾcolor1��color2Ϊ���ϵ��£�1��ʾ�����ң�2��ʾ���µ��ϣ�3��ʾ���ҵ���
	 *            ��
	 * @param borderPaint
	 *            �߿���ɫ
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
		g2d.fill(rr);// ������

		g2d.setPaint(borderPaint);
		g2d.draw(rr);// ���߿�

		if (rotation != 0) {
			g2d.rotate(-rotation * Math.PI / 2);
		}
		g2d.translate(-(rect.x + rect.width / 2), -(rect.y + rect.height / 2));
	}

	/**
	 * ���³ɼ������������
	 */
	private void updateScoreView() {
		scoreLabel.setText(score + "");
		wipeOutRowsLabel.setText(wipeOutRows + "");
	}

	/**
	 * ��������
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
	 * ��������������
	 */
	private void moveLeftUncheck() {
		currentBlock.moveLeft();
	}

	/**
	 * ��������
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
	 * ��������������
	 */
	private void moveRightUncheck() {
		currentBlock.moveRight();
	}

	/**
	 * ��������
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
	 * ��������������
	 */
	private void moveUpUncheck() {
		currentBlock.moveUp();
	}

	/**
	 * ��������
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
	 * ��������������
	 */
	private void moveDownUncheck() {
		currentBlock.moveDown();
	}

	/**
	 * ����˲�ƣ��������ƣ�
	 */
	private void moveSwiftness() {
		playMoveSwiftnessSound();
		while (moveDown()) {
		}
	}

	/**
	 * �������
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
	 * �������ⷽ������
	 */
	private void updateVertualBlockIndex() {

		vertualBlock.setColIndex(currentBlock.getColIndex());//	�������ⷽ��������
		vertualBlock.setFormIndex(currentBlock.getFormIndex());//	�������ⷽ����״����

		//��������ⷽ���������
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
	 * ��ǰ�����Ƿ񻹴���û����ʾ��״̬
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
	 * �̶���ǰ���鵽ͼ��
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
	 * ����
	 */
	private void wipeOutFullRows() {
		List<Integer> fillRows = new LinkedList<>();
		for (int i = rows - 1; i > 0; i--) {
			for (int j = 0; j < cols; j++) {
				if (plotBlockMarks[i][j] != 1) {
					break;
				}
				if (j == (cols - 1)) {//��һ�����ˣ���Ҫ����
					fillRows.add(i);
				}
			}
		}
		boolean play = true;
		for (int i = 0; i < fillRows.size(); i++) {

			int rowIndex = fillRows.get(i) + i;

			if (play) {//��������Ҳֻ����һ����������ʾһ�ζ���
				play = false;

				pauseGame();
				playWipeOutFullRowSound();

				startDrawWipeOutRowAnimeThread(blockPlotGraphics, rowIndex);//���ƶ���

				startRecoverGameThread(1000 / 7);//1/7���ָ���Ϸ
			}

			wipeOutFullRow(rowIndex);//��������
		}

		if (!fillRows.isEmpty()) {//�޸ĳɼ�

			int sumScore = cols * fillRows.size();
			int accoladeScore = (fillRows.size() - 1) * cols;

			score = score + sumScore + accoladeScore;
			wipeOutRows += fillRows.size();
		}
	}

	/**
	 * ����һ���������ж������߳�
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
	 * �����ָ���Ϸ�߳�
	 * 
	 * @param time
	 *            �ڻָ���Ϸ֮ǰ��ͣ�ĺ�����
	 */
	private void startRecoverGameThread(final int time) {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(time);//time�����ָ���Ϸ
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				recoverGame();
			}
		}.start();
	}

	/**
	 * �����Ľ��ǣ���С�����λ�������������
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
	 * ����һ�У�ͼ���ϴӵ�ǰ��row��ʼ�����з����³�һ��
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
	 * ��ͼ���ϵ����һ�п�ʼ����һ�з���
	 */
	private void growBlock() {
		//	��������ͼ���ϵı��
		for (int i = 0; i < rows - 1; i++) {
			for (int j = 0; j < cols; j++) {
				plotBlockMarks[i][j] = plotBlockMarks[i + 1][j];
				plotColorMarks[i][j] = plotColorMarks[i + 1][j];
			}
		}
		//	���һ����Ԫ��ΪĬ�ϵ�Ԫ������Ϊ����
		int nulIndex = random.nextInt(cols);
		for (int j = 0; j < cols; j++) {
			plotBlockMarks[rows - 1][j] = (j == nulIndex) ? 0 : 1;
			if (j != nulIndex) {
				//	ÿ��С�������ɫ�����з������ɫ�����
				plotColorMarks[rows - 1][j] = AbstractBlock.createRandomBlockColor();
			}
		}
		updatePlotPanel();
	}

	/**
	 * ��ǰ�����Ƿ�Խ��
	 * 
	 * @return
	 */
	private boolean isCurrentBlockOverstep() {
		return isBlockMarkOverstep(currentBlock.getBlockMark(), currentBlock.getRowIndex(), currentBlock.getColIndex());
	}

	/**
	 * ���ⷽ���Ƿ�Խ��
	 * 
	 * @return
	 */
	private boolean isVertualBlockOverstep() {
		return isBlockMarkOverstep(vertualBlock.getBlockMark(), vertualBlock.getRowIndex(), vertualBlock.getColIndex());
	}

	/**
	 * �Ƿ�Խ��
	 * 
	 * @return
	 */
	private boolean isBlockMarkOverstep(int[][] blockMark, int rowIndex, int colIndex) {

		for (int i = 0; i < blockMark.length; i++) {
			for (int j = 0; j < blockMark[i].length; j++) {

				if (blockMark[i][j] == 1) {// ����ͼ����Ϊ1

					int tempRowIndex = i + rowIndex;
					int tempColIndex = j + colIndex;

					// �����������Ϊ�����߽緶Χ
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

					// �����������Ϊײ�ϱ�ķ���
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
	 * ��Ϸ�Ƿ����
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
	 * ������ʱ��������
	 */
	private void playChangeFormSound() {
		if (isLoadAudioSuccess) {
			AudioManager.getAudioManager().playAudio(audioPathChange);
		}
	}

	/**
	 * ������ʱ��������
	 */
	private void playMoveLeftSound() {
		if (isLoadAudioSuccess) {
			AudioManager.getAudioManager().playAudio(audioPathMove);
		}
	}

	/**
	 * ������ʱ��������
	 */
	private void playMoveRightSound() {
		if (isLoadAudioSuccess) {
			AudioManager.getAudioManager().playAudio(audioPathMove);
		}
	}

	/**
	 * ������ʱ��������
	 */
	private void playMoveUpSound() {
	}

	/**
	 * ������ʱ��������
	 */
	private void playMoveDownSound() {
	}

	/**
	 * ��˲��ʱ��������
	 */
	private void playMoveSwiftnessSound() {
		if (isLoadAudioSuccess) {
			AudioManager.getAudioManager().playAudio(audioPathDrop);
		}
	}

	/**
	 * ������ʱ��������
	 */
	private void playWipeOutFullRowSound() {
		if (isLoadAudioSuccess) {
			AudioManager.getAudioManager().playAudio(audioPathWipeOut);
		}
	}

	/**
	 * ���û���ʼ���ʱ�򷢳�����
	 */
	private void playGameStartSound() {
		if (isLoadAudioSuccess) {
			//play
		}
	}

	/**
	 * ���û���Over��ʱ�򷢳�����
	 */
	private void playGameOverSound() {
		if (isLoadAudioSuccess) {
			//	Toolkit.getDefaultToolkit().beep();
		}
	}
}

// //////////---------------------------------------------------

/**
 * һ���򵥵Ĵ�ֱ���֣����ڴ���BoxLayout��ֱ����
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
 * һ���򵥵����������࣬��Ҫ��;�ǽ������ļ������ݻ��浽�ڴ棬������ÿ�β��Ŷ�ȥ���ļ���
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
 * ���з���ö��
 * 
 * @author Tang
 */
enum BlockEnum {

	//	Point(BlockPoint.class.getName(),new Color(200, 150, 100)),
	/**
	 * �����Σ� 1 1<br>
	 * 1 1
	 */
	Square(BlockSquare.class.getName(), new Color(100, 200, 200)),

	/**
	 * �����Σ� 1 1 1 1
	 */
	Line(BlockLine.class.getName(), new Color(200, 100, 200)),

	/**
	 * �����Σ� 0 1 0<br>
	 * 1 1 1
	 */
	Trident(BlockTrident.class.getName(), new Color(200, 200, 100)),

	/**
	 * ˳ʱ��¥���Σ� 1 1 0<br>
	 * 0 1 1
	 */
	StairCW(BlockStairClockwise.class.getName(), new Color(200, 100, 100)),

	/**
	 * ��ʱ��¥���Σ� 0 1 1<br>
	 * 1 1 0
	 */
	StairCCW(BlockStairCounterClockwise.class.getName(), new Color(100, 200, 100)),

	/**
	 * ˳ʱ��ֱ���Σ� 0 0 1<br>
	 * 1 1 1
	 */
	AngleCW(BlockAngleClockwise.class.getName(), new Color(100, 100, 200)),

	/**
	 * ��ʱ��ֱ���Σ� 1 0 0<br>
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
 * ���������
 * 
 * @author Tang
 */
abstract class AbstractBlock implements Cloneable {

	/**
	 * ����������������������һ�������Լ����һ���������״
	 */
	protected static Random random = TetrisFrame.random;

	/**
	 * ������ɫ
	 */
	protected Color color;
	/**
	 * ��ͼ���ϵĴ�ֱ����λ��
	 */
	protected int rowIndex = -4;// ��ʼ-4��ʾһ��ʼ����ȫ��������
	/**
	 * ��ͼ���ϵ�ˮƽ����λ��
	 */
	protected int colIndex = 3;// ��ʼΪ3ֻ�Ǹ�Ϊ���÷���һ����ʱ����ԱȽϾ��е�λ�ã���Ȼ��ʼ4Ҳ��
	/**
	 * �������״����
	 */
	protected int formIndex = random.nextInt(getFormCount());//���һ����״

	/**
	 * ��������������飬��һά����״���ڶ�ά���У�����ά���У�����ֻ��Ϊ0��1���������Ϊ0��ʾ���Ƿ��飬Ϊ1��ʾ�Ƿ��顣
	 */
	protected int[][][] blockMarks = new int[getFormCount()][4][4];

	public static AbstractBlock createRandomBlock() throws RuntimeException {
		BlockEnum[] values = BlockEnum.values();
		int randomBlockIndex = random.nextInt(values.length);//���һ������
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
		int randomBlockIndex = random.nextInt(values.length);//���һ����ɫ
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
	 * ����״������Ϊ��һ����״
	 */
	public void nextFormIndex() {
		formIndex++;
		if (formIndex >= getFormCount()) {
			formIndex = 0;
		}
	}

	/**
	 * ����״������Ϊ��һ����״
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
	 * ��ȡ�˷���ı��
	 * 
	 * @return
	 */
	public int[][] getBlockMark() {
		return blockMarks[formIndex];
	}

	public abstract int getFormCount();// �˷���ı�����״�ܸ���

	@Override
	public AbstractBlock clone() throws CloneNotSupportedException {
		AbstractBlock clone = (AbstractBlock) super.clone();
		clone.color = new Color(color.getRGB());
		//		��ά�����������������������clone�����¡��
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
 * С�� <br>
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
 * �����Σ� <br>
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
 * �����Σ� 1 1 1 1
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
 * �����Σ�<br>
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
 * ˳ʱ��¥���Σ� <br>
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
 * ��ʱ��¥���Σ� <br>
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
 * ˳ʱ��ֱ���Σ�<br>
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
 * ��ʱ��ֱ���Σ� <br>
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
