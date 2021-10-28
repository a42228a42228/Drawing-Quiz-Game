import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AnswerBoard extends JPanel implements ActionListener, KeyListener{
	//Size of image panel
    private final int width = 500;
    private final int height = 500;
    //Frame
    private JFrame frame = new JFrame();
    //Button
    private final JButton buttonAnswer = new JButton("解答する");
    //Label
    private final JLabel textLabel = new JLabel("答え ：");    
	//Text field
    private final JTextField answerText = new JTextField(20);
    //Text log
    private JTextArea textList = new JTextArea(5,5);
    private JScrollPane textLog = new JScrollPane(textList);
	//Received image
    private Image image;
    
    public boolean answerChecker = false;	//can answer or not
    public boolean closeChecker = false;	//Check if the window is closed or not
    public boolean rightAnswer = false;		//right answer or not
    //Name of the image
    private String filename;
    
    public AnswerBoard() {
        //set image panel
        setPreferredSize(new Dimension(width, height));
    	
    	//make a control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        //set text log
        textLog.setPreferredSize(new Dimension(480,100));
        textList.setEditable(false);
        initText();
        
        //add component into control panel
		controlPanel.add(textLabel);
		controlPanel.add(answerText);	
        controlPanel.add(buttonAnswer);
        controlPanel.setPreferredSize(new Dimension(450, 40));

        //set button
        buttonAnswer.addActionListener(this);
        answerText.addKeyListener(this);
        
        //set frame
        frame.setLocation(400, 200);
        //if the window of this frame is closed
        frame.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		closeChecker = true;
        		frame.dispose();	//close the frame
        	}
        });
        frame.setTitle("お絵描きクイズ（解答側）");
        frame.setSize(new Dimension(width,700));
        frame.setLayout(new FlowLayout(FlowLayout.CENTER));
        frame.add(this, BorderLayout.NORTH);
        frame.add(textLog, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
    
    //initialize the text log
    private void initText() {
    	textList.append("こんにちは！お描きクイズへようこそ\n");
    	textList.append("相手は出題中...\n");
    }
    
    //add text into text log
    public void addText(String text) {
    	textList.append(text + "\n");
        JScrollBar textBar = textLog.getVerticalScrollBar();
        if (textBar != null) textBar.setValue(textBar.getMaximum());
    }
    
    //check answer is right or not
    private void checkAns() {
    	String answer = answerText.getText();
    	if(answer.equals("")) {
    		addText("答えが入力されてないようですね");
    	}else if(answer.equals(filename)) {
			addText("正解です！");
			addText("------------------------------------------------------");
			addText("相手は出題中です...");
	    	answerChecker = false;
	    	rightAnswer = true;
		}else {
			addText("残念...不正解です");
			addText("正解は "+filename+" でした");
			addText("------------------------------------------------------");
			addText("相手は出題中...");
	    	answerChecker = false;
	    	rightAnswer = false;
		}
    }
    
    //load image
    public void loadImage(String fileName) {
    	filename = fileName;
        try {
            image = ImageIO.read(new File("./Server/"+ fileName +".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        repaint();    //refresh image
        answerChecker = true;
    }
    
    //refresh image
    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(0, 0, width, height);
        g.drawImage(image, 0, 0, null);
    }
    
    //action of button
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == buttonAnswer && answerChecker == true) {
        	checkAns();
        }
    }

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	//press "Enter" key to answer question
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER && answerChecker == true) {
			checkAns();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}