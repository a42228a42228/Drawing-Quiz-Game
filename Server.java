import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    public static void main(String[] args) throws IOException{
        int port = Integer.parseInt(args[0]);
        ServerSocket s = new ServerSocket(port); 	//create server socket
        System.out.println("Started : " + s);
        
        AnswerBoard serverGUI = new AnswerBoard(); 	//create server GUI
        
        //create directory for saving image
    	File dir = new File("./Server");
    	dir.mkdirs();
    	
        DataInputStream dis = null;		//input stream
        FileOutputStream fos = null;	//file output stream
        
        try{
            Socket socket = s.accept();	//accept connection
            try{
                System.out.println("Connection accepted : " + socket);
                
                //buffer reader for receiving message from client
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                //print writer for sending message to client
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                
                while(!serverGUI.closeChecker) {
                	out.println("Waiting for question");
                	String str = in.readLine();		//wait for message from client
                	
                	//client closed window
                	if(str.equals("END")) {
                		serverGUI.addText("クライアントが終了しました");
                		serverGUI.addText("コネクションが終了しました");
                		serverGUI.addText("×を押せばプログラムを閉じることできます");
                		break;
                	}
                	
                	//client send image
                	if(str.equals("Send Image")) {
                		System.out.println("Start saving image");
                		
                		//read the name of image
                		String fileName = in.readLine();
                		
                		//create input stream
                    	dis = new DataInputStream(socket.getInputStream());
                        //create file to write the image from input stream buffer
                    	File file = new File("./Server/"+ fileName +".png");
                    	file.createNewFile();
                        //create file output stream
                        fos = new FileOutputStream(file);
                        //create input stream buffer
                        byte[] inputByte = new byte[1024];
                		int length = 0;
                		
                		//receive image from client
                		while ((length = dis.read(inputByte, 0, inputByte.length)) > 0) {
                			System.out.println(length);
                			fos.write(inputByte, 0, length);
                			fos.flush();
                			if(length < inputByte.length) break;
                		}
                		System.out.println("Recieved Image");
                		
                		//show image
                		serverGUI.loadImage(fileName);
                		serverGUI.addText("問題が届きました！");
                		serverGUI.addText("回答してください");
                		
                		//Wait for server to answer question
                		while(true) {
                			if(!serverGUI.answerChecker) {		//server answered question
                				if(serverGUI.rightAnswer) {
                					out.println("Right Answer");
                				}else {
                					out.println("Wrong Answer");
                				}
                				break;
                			}
                			System.out.print("");
                		}
                	}
                }
            }finally{
                System.out.println("closing...");
                if(fos != null) fos.close();	//close file output stream
                if(dis != null) dis.close();	//close input stream
                socket.close();
            }
        }finally{
            s.close();
        }
    } 
}