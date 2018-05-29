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

//============ ������ ���� �����ϴ� classes, For repaint at Frame fade out

class Shape_info implements Serializable {

	public static final int ShapeList = 0;
	// ---������ ������ ������ Ŭ����, ���������� �� ���� ��ü ������
	// --- �迭, ���� ���� ������� �ȿ� ��ü�� ���� ����

	// --- �� ������ ������ : id, �������� ���� => ShapeList�迭�� ����

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

// ---shapeList Serializable�ϴ� class, ShapeList�� Box�� �ڷ��� ����
class Box<Shape_info> extends Vector implements Serializable {

}

// =====Main Frame

class shapeDraw extends JFrame {

	// ��ư ���� ���� class
	class SetColor {
		SetColor() { 
			Draw_Rectangle.setBackground(OriginalBtnColor);
			Draw_Circle.setBackground(OriginalBtnColor);
			Draw_Line.setBackground(OriginalBtnColor);
			CurrentButton.setBackground(Color.ORANGE);
		}
	}

	// RightPanel Ŭ����
	class RightPanelC extends JPanel {

		public Box ShapeList = new Box();

		// ---�׸��� �׸��� rightPanel

		public void paintComponent(Graphics g) {

			for (int i = 0; i < ShapeList.size(); i++) {
				Shape_info sp = (Shape_info) ShapeList.get(i);
				// �簢�� ID
				if (sp.id == 1)
					g.drawRect(sp.start_X, sp.start_Y, 100, 100);

				// ���� ID
				else if (sp.id == 2)
					g.drawOval(sp.start_X, sp.start_Y, 100, 100);

				// ���� ID
				else if (sp.id == 3)
					g.drawLine(sp.start_X, sp.start_Y, ((Line_info) sp).X2, ((Line_info) sp).Y2);

			}
		}

		RightPanelC() { //--- �׸��׸��� Panel, RightPanel Creator

			// ---Lectangle, Circle : ���콺 ��ġ ������� �����׸��� �� ShapeList�� �׸� �� ���� ��ü�� ����
			this.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {

					if (CurrentButton == Draw_Rectangle) {
						Graphics g = rightPanel.getGraphics();
						Rectangle_info R = new Rectangle_info(e.getX() - 50, e.getY() - 50, 100, 100); // ��ü��������
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

			// ---Line : ������ ��� �� �߱�(������ǥ, ������ǥ �ʿ�) �� ShapeList�� �׸� �� ���� ��ü�� ����
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

	shapeDraw() { // === Main Frame ������

		// --- �޴�, �޴���, �޴������� ���� �� Frame �� �߰�
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");

		// --- menu -> File ���� �޴�
		JMenuItem Save = new JMenuItem("Save");
		JMenuItem Load = new JMenuItem("Load");
		JMenuItem Exit = new JMenuItem("Exit");
		String Filename = null;

		// --- MenuBar �� Menu, Menu�� MenuItem �߰�
		menubar.add(menu);
		menu.add(Save);
		menu.addSeparator();
		menu.add(Load);
		menu.add(Exit);

		// Save���
		Save.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				// --- �����̸� ����
				String Filename = JOptionPane.showInputDialog("Input File name");

				// --- ��ü �����ϴ�(Out) ��Ʈ�� ����
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

		// Load ���
		Load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				RightPanelC RC = new RightPanelC();
				String Filename = JOptionPane.showInputDialog(null, "Input the File name");

				try {
					// --- �Է¹���(IN) ���ϸ��� ��ο��� ������
					fis = new FileInputStream(DIRECTORY + Filename + EXTENSION);
					ois = new ObjectInputStream(fis);
					// --- ��ü�� read��
					rightPanel.ShapeList = (Box) ois.readObject();

					ois.close();
					fis.close();
					repaint();

				} catch (Exception e1) {

					e1.printStackTrace();
				} finally { // ---��Ʈ�� ����
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

		// Exit ���
		Exit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				System.exit(0);

			}
		});

		// Panel �߰�
		this.getContentPane().setLayout(new GridLayout(0, 2));

		rightPanel = new RightPanelC();
		leftPanel = new JPanel(); // ��ư�� �״� Panel

		this.add(leftPanel);
		this.add(rightPanel);

		leftPanel.setLayout(new GridLayout(3, 0)); // ��ư �״� GridLayout

		// Button ���� �� Panel �� �߰�
		Draw_Rectangle = new JButton("�簢");
		Draw_Circle = new JButton("����");
		Draw_Line = new JButton("����");
		OriginalBtnColor = Draw_Rectangle.getBackground();

		leftPanel.add(Draw_Rectangle);
		leftPanel.add(Draw_Circle);
		leftPanel.add(Draw_Line);

		// --- ���� ���ø� �ϴ� MouseListener (������ �׸� �׸��� ���� X)
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

		// Frame ����
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
