package Project1;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.plaf.synth.SynthScrollBarUI;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JScrollPane;
/**
 * This is project 1 CLIENT for COMP90015
 * Name: XINJIE LAN 
 * StudentID: 910030
 * @author xinjie
 *
 */
//Interactive client that reads input from the command line and sends it to 
//a server 
public class tcpInteractiveClient implements WindowListener{
	/*public String getMsgFromUI(String msg){
		
	}*/
	public static String hostAddress;
	public static int hostPort;
	public static Socket socket = null;
	public static BufferedReader in;
	public static BufferedWriter out;
	public static String word;
	public static boolean closed = false; 
	public static  JFrame frame;
	private static JTextField textField;
	private static JTextField textField_1;
	private static JTextField textField_2;
	public void windowClosing(WindowEvent e){
		closed = true;
	}
	/*public static String getWord(String word){
		this.word = word;
		return word;
	}*/
	public static void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 762, 514);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.setForeground(Color.BLACK);
		btnSearch.setFont(new Font("Tahoma", Font.PLAIN, 25));
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String word = textField.getText().trim();
				String regex = "[^0-9\\W+]";
				String[] input = word.split(regex);
				try {
					if(input.length>0){
						textField_2.setText("Invalid input! Please type a single word!");
					}else{
				word = textField.getText().trim();
				System.out.println("done get word");
				System.out.println(word);
					sendMsg(word);
					
					textField_2.setText(recieveMsg());
				} }catch (UnsupportedEncodingException e1) {
					System.out.println("UnsupportedEncoding Exception! Please enter an valid input!");
					textField_2.setText("UnsupportedEncoding Exception! Please enter an valid input!");
					// TODO Auto-generated catch block
					//e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("Failed sending message");
					textField_2.setText("Failed sending message! Please check client!");
				}catch(Exception e1){
					System.out.println("Failed sending message");
					textField_2.setText("Failed sending message! Please check client!");
				}
				//System.out.println(value);
			
			}
		});
		btnSearch.setBounds(12, 193, 137, 46);
		frame.getContentPane().add(btnSearch);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.setForeground(Color.BLACK);
		btnAdd.setFont(new Font("Tahoma", Font.PLAIN, 25));
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String word = textField.getText().trim();
				String regex = "[^0-9\\W+]";
				String[] input = word.split(regex);
				/*for (int i = 0; i < input.length; i++) {
					
					System.out.println("input "+ input[i]);
				}*/
				try {
				if(input.length>0){
					textField_2.setText("Invalid input! Please type a single word!");
				}else{
				word = "add "+textField.getText().trim()+" "+textField_1.getText().trim();
				//System.out.println(textField.getText().trim());
				
					sendMsg(word);
					
					textField_2.setText(recieveMsg());}
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					System.out.println("UnsupportedEncoding Exception! Please enter an valid input!");
					textField_2.setText("UnsupportedEncoding Exception! Please enter an valid input!");
					//e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("Failed sending message");
					textField_2.setText("Failed sending message! Please check client!");
				}catch(Exception e1){
					System.out.println("Failed sending message");
					textField_2.setText("Failed sending message! Please check client!");
				}
				//System.out.println(value);
			}
		});
		btnAdd.setBounds(307, 193, 129, 46);
		frame.getContentPane().add(btnAdd);
		
		JButton btnDelete = new JButton("Delete");
		btnDelete.setForeground(Color.BLACK);
		btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 25));
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String word = textField.getText().trim();
				String regex = "[^0-9\\W+]";
				String[] input = word.split(regex);
				
				word = "delete "+textField.getText().trim();
				try {
					if(input.length>0){
						textField_2.setText("Invalid input! Please type a single word!");
					}
					else {
					sendMsg(word);
					
					textField_2.setText(recieveMsg());
				}} catch (UnsupportedEncodingException e1) {
					System.out.println("UnsupportedEncoding Exception! Please enter an valid input!");
					textField_2.setText("UnsupportedEncoding Exception! Please enter an valid input!");
					//e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					System.out.println("Failed sending message");
					textField_2.setText("Failed sending message! Please check client!");
				}catch(Exception e1){
					System.out.println("Failed sending message");
					textField_2.setText("Failed sending message! Please check client!");
				}
				//System.out.println(value);
			}
		});
		btnDelete.setBounds(603, 193, 129, 46);
		frame.getContentPane().add(btnDelete);
		
		JLabel lblEnterTheWord = new JLabel("Enter the word here:");
		lblEnterTheWord.setFont(new Font("Tahoma", Font.PLAIN, 26));
		lblEnterTheWord.setBounds(12, 0, 251, 65);
		frame.getContentPane().add(lblEnterTheWord);
		
		JLabel lblEnterTheMeaning = new JLabel("Enter the meaning here:");
		lblEnterTheMeaning.setFont(new Font("Tahoma", Font.PLAIN, 26));
		lblEnterTheMeaning.setBounds(12, 60, 463, 54);
		frame.getContentPane().add(lblEnterTheMeaning);
		
		JLabel lblNewLabel = new JLabel("The Results:");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 26));
		lblNewLabel.setBounds(12, 244, 251, 54);
		frame.getContentPane().add(lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 289, 720, 165);
		frame.getContentPane().add(scrollPane);
		
		textField_2 = new JTextField();
		scrollPane.setViewportView(textField_2);
		textField_2.setFont(new Font("Tahoma", Font.PLAIN, 24));
		textField_2.setColumns(10);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 111, 709, 62);
		frame.getContentPane().add(scrollPane_1);
		
		textField_1 = new JTextField();
		scrollPane_1.setViewportView(textField_1);
		textField_1.setFont(new Font("Tahoma", Font.PLAIN, 26));
		textField_1.setColumns(10);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(263, 13, 458, 46);
		frame.getContentPane().add(scrollPane_2);
		
		textField = new JTextField();
		scrollPane_2.setViewportView(textField);
		textField.setFont(new Font("Tahoma", Font.PLAIN, 26));
		textField.setColumns(10);
	}
	 
	public static void main(String[] args){
		try{
		hostAddress = args[0];
		hostPort = Integer.parseInt(args[1]);}
		
		catch(NumberFormatException e){
			
			System.out.println("Invalid input of address or port! Please restart");
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Invalid input of address or port! Please restart the program with a valid input");
			System.exit(0);
			
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					initialize();
					frame.setVisible(true);
					
				} catch (Exception e) {
					System.out.println("Creating GUI for client failed");
					//e.printStackTrace();
				}
			}
		});
		try{
		tcpInteractiveClient client = new tcpInteractiveClient();
		client.runClient();}
		catch(NullPointerException e){
			if(textField_2 == null){
				textField_2 = new JTextField();
			}
			//System.out.println(textField_2);
			textField_2.setText("null pointer exception! invalid address or port! Please restart");
			textField_2.setText("null pointer exception! invalid address or port! Please restart");
			System.out.println("null pointer exception! invalid address or port!");
			
		}catch(Exception e){
			System.out.println("Invalid input of address or port! Please restart");
			if(textField_2 == null){
				textField_2 = new JTextField();
			}
			//System.out.println(textField_2);
			textField_2.setText("Invalid address or port! Please restart");
			//textField_2.setText("Invalid address or port!");

		}
	}
	public static String recieveMsg(){
		String received = "";
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			 received = in.readLine(); // This method blocks until there
			System.out.println("Message received: " + received);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			System.out.println("UnsupportedEncoding Exception! Please enter an valid input!");
			textField_2.setText("UnsupportedEncoding Exception! Please enter an valid input!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			if(textField_2 == null){
				textField_2 = new JTextField();
			}
			System.out.println("Can't read from server");
			//textField_2.setText("Something wrong with the server! Can't get feedback from server");
			textField_2.setText("Something wrong with the server! Can't get feedback from server");
		}/*catch(Exception e){
			System.out.println("Something wrong with the server");
			textField_2.setText("Something wrong with the server! Can't get feedback from server");
		}*/
		// is something to read from the
		// input stream
		return received;
	}
	public static void sendMsg(String word) throws UnsupportedEncodingException, IOException{
		try{
		 
		 out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));

		//Scanner scanner = new Scanner(System.in);
		//String inputStr = getMsg();

		//While the user input differs from "exit"
		//while ((!closed)&&(w!=null)) {
			
			// Send the input string to the server by writing to the socket output stream
			out.write(word + "\n");
			out.flush();
			System.out.println("Message sent");
			
			// Receive the reply from the server by reading from the socket input stream
			
		}catch(IOException e){
			if(textField_2 == null){
				textField_2 = new JTextField();
			}
			//System.out.println(textField_2.getText());
			//textField_2.setText("Message sending failure! IO Exception!");
			textField_2.setText("Message sending failure! IO Exception!");
			System.out.println("Message sending failure! IO Exception! The server might be down");
			
			//e.printStackTrace();
		}catch(Exception e){
			System.out.println("Message sending failure! ");
			
			textField_2.setText("Message sending failure! Please check if the system running!");}
		}
		//catch(bigger exception)
		
	//}

	public void runClient () {

		
		try {
			// Create a stream socket bounded to any port and connect it to the
			// socket bound to localhost on port 4444
			//System.out.println("hostadd "+ hostAddress);
			//System.out.println("port"+hostPort);
			socket = new Socket(hostAddress, hostPort);
			//System.out.println(socket);
			System.out.println("Connection established");
			if(textField_2 == null){
				textField_2 = new JTextField();
			}
			textField_2.setText("Connection established");

			// Get the input/output streams for reading/writing data from/to the socket
			
			//scanner.close();
			
		}catch(ConnectException e){
			/*if(textField_2 == null){
				textField_2 = new JTextField();
			}
			textField_2.setText("Connection established");
*/
			

			System.out.println("Invalid host address or port! Please close the window and try again!");
			textField_2.setText("Invalid host address or port! Please close the window and try again!");
			
		}
		catch (UnknownHostException e) {
			if(textField_2 == null){
				textField_2 = new JTextField();
			}
			textField_2.setText("UnknownHost Exception! Please use a valid address or port!");

			System.out.println("UnknownHost Exception! Please use a valid address or port!");
			//e.printStackTrace();
			textField_2.setText("UnknownHost Exception! Please use a valid address or port!");
		} catch (IOException e) {
			
			//e.printStackTrace();
			System.out.println("Something wrong with the network");
		} catch(Exception e){
			if(textField_2 == null){
				textField_2 = new JTextField();
			}
			System.out.println("Connection Error");
		}
		finally {
			// Close the socket
			if ((socket != null)&&closed) {
				try {
					socket.close();
				} catch (IOException e) {
					//e.printStackTrace();
					System.out.println("Failed close the socket");
				}
			}
		}

	}
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
