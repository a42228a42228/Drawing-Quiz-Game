import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client{
    public static void main(String[] args) throws IOException{
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);
        InetAddress addr = InetAddress.getByName(serverName);
        System.out.println("addr = " + addr);
        Socket socket = new Socket(addr, port);		//create socket
		
        DataOutputStream dos = null;	//output stream
        FileInputStream fis = null;		//file input stream
        
		//connect to Server
        try{
            System.out.println("socket = " + socket);
            System.out.println("Connect successfully");
            
            //create client GUI
            Canvas clientGUI = new Canvas();
            
            //buffer reader for receiving message from server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //print writer for sending message to server
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            
            while(!clientGUI.closeChecker) {
            	//wait for message from server
            	String str = in.readLine();
            	System.out.println(str);
            	
            	//server is waiting for question
            	while(str.equals("Waiting for question")) {
            		//wait for client send image
                	if(clientGUI.sendChecker) {
                		out.println("Send Image");		//tell server image is send
                		System.out.println("Send Image");
                		out.println(clientGUI.name);   	//tell sever the name of image
                		
                		//create output stream
                        dos = new DataOutputStream(socket.getOutputStream());
                        //create file to read the image into output stream buffer
                        File file = new File( "./Client/" + clientGUI.name  + ".png");
                        //create file input stream
                        fis = new FileInputStream(file);
                        //create output stream buffer
                        byte[] sendBytes = new byte[1024];
                        int length = 0;
                        
                        //send image to server
                		while ((length = fis.read(sendBytes, 0, sendBytes.length)) > 0) {
                			System.out.println(length);
                			dos.write(sendBytes, 0, length);
                			dos.flush();
                		}
                		
                		System.out.println("Finish to send image");
                		clientGUI.addText("o‘è‚µ‚Ü‚µ‚½‚æI");
                		clientGUI.addText("‘Šè‚Íl‚¦’†‚Å‚·...");
                		clientGUI.sendChecker = false;	//reset send checker
                		break;
                	}

                	if(clientGUI.closeChecker) {	//if client closed the window
                		break;	//escape loop
                	}
                	System.out.print("");
            	}

            	
            	//server answered question
            	if(str.equals("Right Answer")) {
            		clientGUI.addText("‘Šè‚ª³‰ğ‚Ü‚µ‚½");
            		clientGUI.addText("------------------------------------------------------");
            		clientGUI.addText("Ÿ‚Ì–â‘è‚ğ‚Ç‚¤‚¼");
            		clientGUI.clearImage();
            	}
            	
            	if(str.equals("Wrong Answer")) {
            		clientGUI.addText("‘Šè‚ªŠÔˆá‚¢‚Ü‚µ‚½");
            		clientGUI.addText("------------------------------------------------------");
            		clientGUI.addText("Ÿ‚Ì–â‘è‚ğ‚Ç‚¤‚¼");
            		clientGUI.clearImage();
            	}
            }
            out.println("END");	//send "END" message to server
        }finally{
            System.out.println("closing...");
            if(fis != null) fis.close();	//close file input stream
            if(dos != null) dos.close();	//close output stream
            socket.close();
        }
    }
}