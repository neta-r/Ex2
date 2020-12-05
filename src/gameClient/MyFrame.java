package gameClient;

import gameClient.util.dataPanel;

import javax.swing.*;
import java.awt.*;

/**
 * This class represents a very simple GUI class to present a
 * game on a graph - you are welcome to use this class - yet keep in mind
 * that the code is not well written in order to force you improve the
 * code and not to take it "as is".
 *
 */
public class MyFrame extends JFrame{
	private int _ind;
	private Arena _ar;
	gamePanel gP;
	dataPanel dP;
	MyFrame(String a) {
		super(a);
		//this.setLayout(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		int _ind = 0;
	}

	public void initPanel(){
		gP=new gamePanel(_ar.getGraph(),_ar);
		this.add(gP);
	}
	public void update(Arena ar) {
		this._ar = ar;
		initPanel();
		gP.update();
	}

	public void paint(Graphics g) {
		gP.updatePanel();
		gP.repaint();
	}
}
