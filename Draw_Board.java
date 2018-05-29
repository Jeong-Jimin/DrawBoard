package Draw_Board;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

//============ 도형의 정보 저장하는 classes, For repaint at Frame fade out

class Shape_info implements Serializable {

	public static final int ShapeList = 0;
	// ---도형의 정보를 저장할 클래스, 도형이찍힐 때 마다 객체 생성됨
	// --- 배열, 벡터 등의 저장공간 안에 객체의 정보 저장

	// --- 각 도형의 공통점 : id, 시작점을 가짐 => ShapeList배열에 넣음

	int start_X, start_Y;
	int id;
}

class Rectangle_info extends Shape_info {
	int width;
	int height;

	Rectangle_info(int argX, int argY, int argwidth, int argheight) {
		this.id = 1;
		this.start_X = argX;
		this.start_Y = argY;
		this.width = argwidth;
		this.height = argheight;
	}
}

class Circle_info extends Shape_info {
	int width;
	int height;

	Circle_info(int argX, int argY, int argwidth, int argheight) {
		this.id = 2;

		this.start_X = argX;
		this.start_Y = argY;
		this.width = argwidth;
		this.height = argheight;
	}
}

class Line_info extends Shape_info {
	int X2;
	int Y2;

	Line_info(int argX1, int argY1, int argX2, int argY2) {
		this.id = 3;
		this.start_X = argX1;
		this.start_Y = argY1;
		this.X2 = argX2;
		this.Y2 = argY2;
	}
}

// ---shapeList Serializable하는 class, ShapeList는 Box의 자료형 가짐
class Box<Shape_info> extends Vector implements Serializable {

}

// =====Main Frame

class shapeDraw extends JFrame {

	// 버튼 색깔 조정 class
	class SetColor {
		SetColor() { 
			Draw_Rectangle.setBackground(OriginalBtnColor);
			Draw_Circle.setBackground(OriginalBtnColor);
			Draw_Line.setBackground(OriginalBtnColor);
			CurrentButton.setBackground(Color.ORANGE);
		}
	}

	// RightPanel 클래스
	class RightPanelC extends JPanel {

		public Box ShapeList = new Box();

		// ---그림을 그리는 rightPanel

		public void paintComponent(Graphics g) {

			for (int i = 0; i < ShapeList.size(); i++) {
				Shape_info sp = (Shape_info) ShapeList.get(i);
				// 사각형 ID
				if (sp.id == 1)
					g.drawRect(sp.start_X, sp.start_Y, 100, 100);

				// 원형 ID
				else if (sp.id == 2)
					g.drawOval(sp.start_X, sp.start_Y, 100, 100);

				// 라인 ID
				else if (sp.id == 3)
					g.drawLine(sp.start_X, sp.start_Y, ((Line_info) sp).X2, ((Line_info) sp).Y2);

			}
		}

		RightPanelC() { //--- 그림그리는 Panel, RightPanel Creator

			// ---Lectangle, Circle : 마우스 위치 기반으로 도형그리기 및 ShapeList에 그린 선 정보 객체로 저장
			this.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {

					if (CurrentButton == Draw_Rectangle) {
						Graphics g = rightPanel.getGraphics();
						Rectangle_info R = new Rectangle_info(e.getX() - 50, e.getY() - 50, 100, 100); // 객체정보저장
						ShapeList.add(R);
						g.drawRect(e.getX() - 50, e.getY() - 50, 100, 100);
					}

					if (CurrentButton == Draw_Circle) {
						Graphics g = rightPanel.getGraphics();
						Circle_info C = new Circle_info(e.getX() - 50, e.getY() - 50, 100, 100);
						ShapeList.add(C);
						g.drawOval(e.getX() - 50, e.getY() - 50, 100, 100);
					}
				}
			});

			// ---Line : 지정한 대로 선 긋기(시작좌표, 종료좌표 필요) 및 ShapeList에 그린 선 정보 객체로 저장
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					FirstX = e.getX();
					FirstY = e.getY();
				}

				public void mouseReleased(MouseEvent e) {
					LastX = e.getX();
					LastY = e.getY();

					if (CurrentButton == Draw_Line) {
						Graphics g = rightPanel.getGraphics();
						Line_info L = new Line_info(FirstX, FirstY, LastX, LastY);
						ShapeList.add(L);
						g.drawLine(FirstX, FirstY, LastX, LastY);
					}
				}
			}); // ---End of DrawLine function
		}
	} // ---End of RightPanelC

	private JPanel leftPanel;
	private RightPanelC rightPanel;
	private JButton Draw_Rectangle, Draw_Circle, Draw_Line;
	private JButton CurrentButton;
	private Color OriginalBtnColor;
	private int FirstX, FirstY;
	private int LastX, LastY;
	
	final String EXTENSION = ".txt";
	final String DIRECTORY = "C://Savepath//";
	
	FileOutputStream fos = null;
	ObjectOutputStream oos = null;
	
	FileInputStream fis = null;
	ObjectInputStream ois = null;

	shapeDraw() { // === Main Frame 생성자

		// --- 메뉴, 메뉴바, 메뉴아이템 생성 및 Frame 에 추가
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");

		// --- menu -> File 세부 메뉴
		JMenuItem Save = new JMenuItem("Save");
		JMenuItem Load = new JMenuItem("Load");
		JMenuItem Exit = new JMenuItem("Exit");
		String Filename = null;

		// --- MenuBar 에 Menu, Menu에 MenuItem 추가
		menubar.add(menu);
		menu.add(Save);
		menu.addSeparator();
		menu.add(Load);
		menu.add(Exit);

		// Save기능
		Save.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				// --- 파일이름 받음
				String Filename = JOptionPane.showInputDialog("Input File name");

				// --- 객체 저장하는(Out) 스트림 생성
				try {
					fos = new FileOutputStream(DIRECTORY + Filename + EXTENSION);
					oos = new ObjectOutputStream(fos);
					oos.writeObject(rightPanel.ShapeList);
					oos.close();
					oos.flush();
					fos.close();

				} catch (IOException e1) {
					e1.printStackTrace();
				} finally {
					if (fos != null)
						try {
							fos.close();
						} catch (IOException e1) {
						}
					if (oos != null)
						try {
							oos.close();
						} catch (IOException e1) {
						}
				}
			}
		});

		// Load 기능
		Load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RightPanelC RC = new RightPanelC();
				String Filename = JOptionPane.showInputDialog(null, "Input the File name");

				try {
					// --- 입력받은(IN) 파일명을 경로에서 가져옴
					fis = new FileInputStream(DIRECTORY + Filename + EXTENSION);
					ois = new ObjectInputStream(fis);
					// --- 객체를 read함
					rightPanel.ShapeList = (Box) ois.readObject();

					ois.close();
					fis.close();
					repaint();

				} catch (Exception e1) {

					e1.printStackTrace();
				} finally { // ---스트림 닫음
					if (fis != null)
						try {
							fis.close();
						} catch (IOException e1) {
						}
					if (ois != null)
						try {
							ois.close();
						} catch (IOException e1) {
						}
				}
			}
		});

		// Exit 기능
		Exit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				System.exit(0);

			}
		});

		// Panel 추가
		this.getContentPane().setLayout(new GridLayout(0, 2));

		rightPanel = new RightPanelC();
		leftPanel = new JPanel(); // 버튼을 쌓는 Panel

		this.add(leftPanel);
		this.add(rightPanel);

		leftPanel.setLayout(new GridLayout(3, 0)); // 버튼 쌓는 GridLayout

		// Button 생성 및 Panel 에 추가
		Draw_Rectangle = new JButton("사각");
		Draw_Circle = new JButton("원형");
		Draw_Line = new JButton("직선");
		OriginalBtnColor = Draw_Rectangle.getBackground();

		leftPanel.add(Draw_Rectangle);
		leftPanel.add(Draw_Circle);
		leftPanel.add(Draw_Line);

		// --- 도형 선택만 하는 MouseListener (실제로 그림 그리는 역할 X)
		Draw_Rectangle.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				CurrentButton = (JButton) e.getSource();
				new SetColor();
			}
		});

		Draw_Circle.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				CurrentButton = (JButton) e.getSource();
				new SetColor();
			}
		});

		Draw_Line.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {
				CurrentButton = (JButton) e.getSource();
				new SetColor();
			}
		});

		// Frame 설정
		this.setTitle("shape Board");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		this.setBounds(450, 250, 1000, 700);
		this.setJMenuBar(menubar);
	}
}// --- End of shapeDraw

public class Draw_Board {
	public static void main(String arsgp[]) {
		shapeDraw SD = new shapeDraw();
	}
}
