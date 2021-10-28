import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Canvas extends JPanel implements ChangeListener, ActionListener, KeyListener{
	//Size of draw panel
    private final int width = 500;
    private final int height = 500;
    //Coordinate of mouse 
    private int startX, startY, endX, endY;
    //Pen width and color
    private int currentWidth = 5;
    private Color currentColor = Color.black;
    //Button
    private final JButton ButtonSave = new JButton("出題する");
    private final JButton ButtonClean = new JButton("クリア");
    private final JButton ButtonColor = new JButton("カラーパネル");
    //Text field
	private JTextField nameText = new JTextField("お題をここに書いてください");
    //Label
	private JLabel strokeLabel = new JLabel("ペンの太さ： " + currentWidth);
    private JLabel colorTextLabel = new JLabel("ペンの色： ");
    private JLabel colorLabel = new JLabel();
    private JLabel textLabel = new JLabel("問題");
    //Color Chooser
    private JColorChooser colorChooser = new JColorChooser(Color.black);
    //Stroke size Chooser
    private JSlider strokeChooser = new JSlider(0,30,5);
    //Text log
    private JTextArea textList = new JTextArea(5,10);
    private JScrollPane textLog = new JScrollPane(textList);
    //Buffer for drawing image
    private BufferedImage bufferImage;
    private Graphics2D bufferGraphics;

    public boolean sendChecker = false; 	 //Check if the image is send or not
    public boolean closeChecker = false; 	 //Check if the window is closed or not
    //Name of the image
    public String name;
    
    public Canvas() {
        //set the size of draw panel
        setPreferredSize(new Dimension(width, height));
    	
    	//make a control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setPreferredSize(new Dimension(320, height));
        controlPanel.setLayout(null);
        
        //set text log
        textList.setEditable(false);
        initText();
        
        //set the font of "問題" text label
        textLabel.setFont(new Font("MS明朝" , Font.BOLD, 20));
        
        //set the color label
        colorLabel.setOpaque(true);
        colorLabel.setBackground(currentColor);
        
        //set the layout of control panel
        strokeLabel.setBounds(10,10,150,20);
        strokeChooser.setBounds(10,30,300,30);  
        colorTextLabel.setBounds(10,60,150,20);
        colorLabel.setBounds(80,60,20,20);
        ButtonColor.setBounds(35,100,250,30);    
        ButtonClean.setBounds(35,150,250,30);
        textLabel.setBounds(10,360,300,30);
		nameText.setBounds(10,400,300,30);        
        ButtonSave.setBounds(150,450,150,30);
        textLog.setBounds(10, 200, 300, 150);

        //add component into control panel
        controlPanel.add(ButtonClean);
        controlPanel.add(ButtonSave);
        controlPanel.add(ButtonColor);
        controlPanel.add(strokeChooser);
        controlPanel.add(strokeLabel);
        controlPanel.add(colorTextLabel);
        controlPanel.add(colorLabel);
		controlPanel.add(textLabel);
		controlPanel.add(nameText);	
		controlPanel.add(textLog);

        //create buffer image
        bufferImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //create buffer graphic
        bufferGraphics = bufferImage.createGraphics();
        bufferGraphics.setBackground(Color.white);
        bufferGraphics.clearRect(0, 0, width, height);

        //record the position where mouse pressed
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }
        });

        //record the position where mouse dragged
        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();
                
                //draw line on the graphic
                bufferGraphics.setColor(currentColor);
                bufferGraphics.setStroke(new BasicStroke(currentWidth,BasicStroke.CAP_ROUND,BasicStroke.JOIN_MITER));
                bufferGraphics.drawLine(startX, startY, endX, endY);

                startX = endX;
                startY = endY;
                
                //repaint the buffer image
                repaint();
            }
        });

        //set button
        ButtonSave.addActionListener(this);
        ButtonClean.addActionListener(this);
        ButtonColor.addActionListener(this);
        
        //set name text field
        nameText.addKeyListener(this);
        
        //set chooser
    	strokeChooser.addChangeListener(this);
        colorChooser.getSelectionModel().addChangeListener(this);
     
        //set frame
        JFrame frame = new JFrame();
        frame.setLocation(400, 200);
        //if the window of this frame is closed
        frame.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		closeChecker = true;
        		frame.dispose();	//close the frame
        	}
        });
        frame.setTitle("お絵描きクイズ（出題側）");
        frame.add(this, BorderLayout.CENTER);
        frame.add(controlPanel, BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
    }
    
    //initialize the text log
    private void initText() {
    	textList.append("こんにちは！お描きクイズへようこそ\n");
    	textList.append("早速絵をかきましょう！\n");
    }
    
    //add text into text log
    public void addText(String text) {
    	textList.append(text + "\n");
    	//scroll the text log
        JScrollBar textBar = textLog.getVerticalScrollBar();    
        if (textBar != null) textBar.setValue(textBar.getMaximum());
    }
    
    //save image
    private void saveImage(String path) {
        try {
            ImageIO.write(bufferImage, "png", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

	//send image
    private void sendImage() {
		name = nameText.getText();
		if(name.equals("お題をここに書いてください") || name.equals("")) {
			addText("あれ？お題が入力されていないようですね");
    	}else {
			File dir = new File("./Client");
			dir.mkdirs();
			saveImage("./Client/" + name + ".png");  //save the image into "./Client" directory
			sendChecker = true;
        }
    }
    
    //open color chooser
    private void openColorChooser() {
    	//make the frame of color chooser
        JFrame colorFrame = new JFrame();
        colorFrame.setTitle("カラーパネル");
        colorFrame.setLocation(1050,250);  
        colorFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        colorFrame.add(colorChooser);
        colorFrame.pack();
        colorFrame.setVisible(true);
    }
    
    //clear image
    public void clearImage() {
        bufferGraphics.clearRect(0, 0, width, height);	//fill the graphic with the background color 
        repaint();
    }
    
    //draw buffer graphic into buffer image
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bufferImage, 0, 0, this);
    }

    //action of each button
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == ButtonSave && sendChecker == false) {
        	sendImage();
        }else if(e.getSource() == ButtonClean) {
        	clearImage();
        }else if(e.getSource() == ButtonColor) {
        	openColorChooser();
        }
    }
    
    //change event of color chooser and stroke chooser
	@Override
	public void stateChanged(ChangeEvent e) {
		//set current color to the selected color
		currentColor = colorChooser.getColor();
		colorLabel.setBackground(currentColor);
		
		//set current stroke to the selected stroke size
		currentWidth = strokeChooser.getValue();
		strokeLabel.setText("ペンの太さ： " + currentWidth);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	//press "Enter" key to send image
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER && sendChecker == false) {
			sendImage();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}