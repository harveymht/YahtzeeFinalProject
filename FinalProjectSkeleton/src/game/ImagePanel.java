package game;

import javax.swing.*;
import java.awt.*;

public class ImagePanel extends JPanel {
	private JLabel l;
	private Image[] diceImages = new Image[6];
	private int num;
	
	private ImagePanel() {

		for (int i=0;i<6;i++) {
			diceImages[i] = new ImageIcon("die"+(i+1) +".png").getImage();
		}
		//setLayout(new FlowLayout(FlowLayout.LEFT));
	}

	public ImagePanel(int num) {
		this();
		this.num = checkNum(num);
		Image img = diceImages[this.num - 1];
		l = new JLabel(new ImageIcon(img));
		add(l);
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		System.out.println("initialization: setting size to " + size.toString());
		setPreferredSize(size);
	}

	private int checkNum(int num) {
		if (num < 1) {
			num = 1;
		}
		if (num > 6) {
			num = 6;
		}
		return num;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = checkNum(num);
		l.setIcon(new ImageIcon(diceImages[this.num - 1]));
		repaint();
	}

	public void scaleImage(double factor) {
		ImageIcon imageIcon = new ImageIcon(diceImages[num - 1]);
		int height = imageIcon.getIconHeight();
		int width = imageIcon.getIconWidth();
		int newHeight = (int) (height*factor);
		int newWidth = (int) (width*factor);
		System.out.println("scaleImage: new size is  " + newWidth + ", "+ newHeight);
		for (int i = 0; i < diceImages.length; i++) {
			diceImages[i] = diceImages[i].getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
		}

		imageIcon = new ImageIcon(diceImages[num - 1]);
		l.setIcon(imageIcon);
        Dimension size = new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight());
        System.out.println("scaleImage: setting size to " + size.toString());
        setPreferredSize(size);
		repaint();
	}
	
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	l.repaint();
    }
}
