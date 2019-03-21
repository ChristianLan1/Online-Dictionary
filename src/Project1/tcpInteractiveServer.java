package Project1;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JLabel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import java.awt.Color;
import javax.swing.JScrollPane;
/**
 * This is project 1 Server for COMP90015
 * Name: XINJIE LAN 
 * StudentID: 910030
 * @author xinjie
 *
 */
public class tcpInteractiveServer {
	public static int currentUsers;
	public static int port;
	public static String dictPath;
	public static String dictName;
	public static JFrame frame;
	private ServerSocket listeningSocket;
	private int numOfClients;
	public static List<Word> wordset;
	private static JTextField txtNumberOfCurrent;
	private static JTextArea textArea;
	private static JTextArea textArea_1;
	private static JTextArea textArea_2;
	public tcpInteractiveServer() throws IOException{
		System.out.println("initializing the server...");
		
		listeningSocket = new ServerSocket(8088);
		
		
		
	}
	private class clientHandler implements Runnable{
		private Socket socket;
		private String host;
		
		public clientHandler(Socket clientSocket){
			this.socket = clientSocket;
			textArea_1.append("Client conection number " + numOfClients + " accepted:"+"\n"+"Remote Port: " + clientSocket.getPort()+"\n"
					+"Remote Hostname: " + clientSocket.getInetAddress().getHostName()+"\n"+"Local Port: " + clientSocket.getLocalPort()+"\n");
			/*System.out.println("Client conection number " + numOfClients + " accepted:");
			System.out.println("Remote Port: " + clientSocket.getPort());
			System.out.println("Remote Hostname: " + clientSocket.getInetAddress().getHostName());
			System.out.println("Local Port: " + clientSocket.getLocalPort());*/
		}
		public void run(){
			wordset = getDict();
			String meaning = "";
			//Get the input/output streams for reading/writing data from/to the socket
			try{
				BufferedReader in;
			
			
				in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			
			BufferedWriter out;
			
				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
			

			
			//Read the message from the client and reply
			//Notice that no other connection can be accepted and processed until the last line of 
			//code of this loop is executed, incoming connections have to wait until the current
			//one is processed unless...we use threads!
			String clientMsg = null;
			boolean isAdded;
			
			while((clientMsg = in.readLine()) != null) {
				String regex = "[^a-zA-Z_0-9.]";
				String addMeaning = "";
				String[] info = clientMsg.split(regex);
				textArea_2.append("Message from client " + numOfClients + ": " + clientMsg+"\n");
				//System.out.println("Message from client " + numOfClients + ": " + clientMsg);
				if(info[0].equals("add")){
					//System.out.println("info length "+info.length);
					if(info.length ==2){
						//System.out.println("entered");
						out.write("Please define a meaning for the word"+"\n");
						out.flush();
						//System.out.println("flushed");
					}else if(info.length ==1){
						wordset = getDict();
						meaning = checkDict(wordset,clientMsg);
						if(meaning.equals("")){
							out.write("The word does not in the dictionary! Please define it first!"+ "\n");
							out.flush();
						}else{
						out.write("The meaning of the word is: " + meaning + "\n");
						out.flush();}
						textArea.append("Response sent to client "+numOfClients+"\n");
					}
					else{
						wordset = getDict();
						for (int i = 2; i < info.length; i++) {
							addMeaning+=info[i]+" ";
						}
						//System.out.println("info[1] "+info[1]);
						//System.out.println("addMeaning "+addMeaning);
					isAdded = addDict(info[1],addMeaning,wordset);
					if(isAdded){
						out.write("Adding word successfully"+"\n");
						out.flush();}
					else{
						out.write("Adding word unsuccessfully! Duplicate word!"+"\n");
						out.flush();
					}
					
					}
				}
				else if(info[0].equals("delete")){
					wordset = getDict();
					if(info.length ==1){
						meaning = checkDict(wordset,clientMsg);
						if(meaning.equals("")){
							out.write("The word does not in the dictionary! Please define it first!"+ "\n");
							out.flush();
						}else{
						out.write("The meaning of the word is: " + meaning + "\n");
						out.flush();}
						textArea.append("Response sent to client "+numOfClients+"\n");
					}
					else if(deleteDict(info[1],wordset)){
						out.write("Deleting word successfully!"+"\n");
						out.flush();
					}else{
						out.write("The word not found!"+"\n");
						out.flush();
					};
				}
				
				else{
				wordset = getDict();
				meaning = checkDict(wordset,clientMsg);
				if(meaning.equals("")){
					out.write("The word does not in the dictionary! Please define it first!"+ "\n");
					out.flush();
				}else{
				out.write("The meaning of the word is: " + meaning + "\n");
				out.flush();}
				}
				textArea.append("Response sent to client "+numOfClients+"\n");
				//System.out.println("Response sent");
			}}
			catch(SocketException e) {
				textArea_1.append("client "+numOfClients+" closed..."+"\n");
				currentUsers -=1;
				txtNumberOfCurrent.setText(Integer.toString(currentUsers));
				
				//System.out.println("closed...");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				System.out.println("IO Exception when ack msg to client");
				textArea_1.append("IO Exception when ack msg to client"+"\n");
			}
			
		}
	}
	public synchronized boolean deleteDict(String word,List<Word> wordset){
		boolean isDelete = false;
		int id = -100;
		for (int i = 0; i < wordset.size(); i++) {
			Word checkWord = wordset.get(i);
			if(word.equals(checkWord.getWord())){
				textArea_1.append("Found the word in the dictionary! Ready to delete"+"\n");
				//System.out.println("Found the word in the dictionary! Ready to delete");
				id = i;
			}	
		}
		try{
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new FileInputStream(dictName));
			Element root =  doc.getRootElement();
			List<Element> wordList = root.elements();
			for(Element wordEle:wordList){
				//System.out.println("id: "+id);
				//System.out.println("att val: "+Integer.parseInt(wordEle.attributeValue("id")));
				if((id!=-100)&&(id == Integer.parseInt(wordEle.attributeValue("id")))){
					wordEle.detach();
					isDelete = true;
					//System.out.println("Successfully deleted the word: "+word);
				}else{
					isDelete = false;
					//System.out.println("The word does not exist in the dictionary");
				}
			}
			XMLWriter xmlWriter= null;
			try{
				xmlWriter = new XMLWriter(new FileOutputStream(dictName),OutputFormat.createPrettyPrint());
				xmlWriter.write(doc);
				textArea_1.append("Done deletion"+"\n");
				
			}catch(IOException e){
				System.out.println("IO Exception when writing XML file");
				//e.printStackTrace();
			}finally{
				if(xmlWriter!=null){
					try {
						xmlWriter.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						System.out.println("IO Exception when close xmlWriter");
					}
				}
			}
		}catch(Exception e){
			System.out.println("IO Exception when writing XML file! Please check dictionary!");
		}
		return isDelete;
		
	}
	public synchronized boolean addDict(String word, String meaning,List<Word> wordset){
		
		int id = 0;
		boolean success = true;
		for (int i = 0; i < wordset.size(); i++) {
			Word checkWord = wordset.get(i);
			if(word.equals(checkWord.getWord())){
				success = false;
				return success;
			}
			id = i+1;
		}
		wordset.add(new Word(id,word,meaning));
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("wordlist");
		for(Word wordEntity:wordset){
			Element wordEle = root.addElement("word");
			
			Element nameEle = wordEle.addElement("wordName");
			nameEle.addText(wordEntity.getWord());
			
			Element meaningEle = wordEle.addElement("wordMeaning");
			meaningEle.addText(wordEntity.getMeaning());
			
			wordEle.addAttribute("id", wordEntity.getId()+"");
				
		}
		XMLWriter xmlWriter= null;
		try{
			xmlWriter = new XMLWriter(new FileOutputStream(dictName),OutputFormat.createPrettyPrint());
			xmlWriter.write(doc);
			
		}catch(IOException e){
			System.out.println("IO Exception when writing XML file");
			//e.printStackTrace();
		}finally{
			if(xmlWriter!=null){
				try {
					xmlWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return success;
		
	}
	public synchronized String checkDict(List<Word> wordSet, String clientWord){
		String meaning = "";
		for (int i = 0; i < wordSet.size(); i++) {
			Word checkWord = wordSet.get(i);
			if(clientWord.equals(checkWord.getWord())){
				 meaning = checkWord.getMeaning();
				
			}
		}
		return meaning;
	}
	public synchronized List<Word> getDict(){
		List<Word> wordSet = new ArrayList<Word>();
		try{
			//File file = new File("."+File.separator+"dictionary.xml");
			File file = new File(dictPath);
			dictName = file.getName();
			if(!file.exists()){
				file = new File(dictName);
				if(!file.exists()){
				//file.createNewFile();
				Document doc = DocumentHelper.createDocument();
				Element root = doc.addElement("wordlist");
				Element wordEle = root.addElement("word");
				Element nameEle = wordEle.addElement("wordName");
				nameEle.addText("defaultWord");
				
				Element meaningEle = wordEle.addElement("wordMeaning");
				meaningEle.addText("Auto-generated word to initialize a dictionary");
				
				wordEle.addAttribute("id", 0+"");
				XMLWriter xmlWriter= null;
				try{
					xmlWriter = new XMLWriter(new FileOutputStream(dictName),OutputFormat.createPrettyPrint());
					xmlWriter.write(doc);
					//textArea_1.append("The dictionary is newly created, please define some word first"+"\n");
					textArea_1.append("Done creating xml file with a default word 'defaultWord'"+"\n");
					Word defaultWord = new Word(0,"defaultWord","Auto-generated word to initilize a dictionary");
					wordSet.add(defaultWord);
					return wordSet;
				}catch(IOException e){
					System.out.println("IO Exception when writing XML file");
					//e.printStackTrace();
				}finally{
					if(xmlWriter!=null){
						try {
							xmlWriter.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							System.out.println("Exception when writing XML file");
						}
					}
				}
				//System.out.println("done create xml");
			}else{
				try{
					SAXReader reader = new SAXReader();
					Document doc = reader.read(new FileInputStream(dictName));
					textArea_1.append("Done reading xml file"+dictName+"for client "+numOfClients+"\n");
						//System.out.println("Done reading xml file!");
					Element root =  doc.getRootElement();
					List<Element> wordList = root.elements();
					for(Element wordEle : wordList){
						Element wordName = wordEle.element("wordName");
						String word = wordName.getText();
						int id = Integer.parseInt(wordEle.attributeValue("id"));
						String wordMeaning = wordEle.elementText("wordMeaning");
						
						Word oneWord = new Word(id,word,wordMeaning); 
						wordSet.add(oneWord);
					}
					}catch(IOException e){
						//e.printStackTrace();
						System.out.println("Adding words into word set failure! or dictionary is empty! Please add some words into the dictionary");
						textArea_1.append("Adding words into word set failure! or dictionary is empty! Please add some words into the dictionary"+"\n");
					}	
			
			}}else{
			try{
			SAXReader reader = new SAXReader();
			Document doc = reader.read(new FileInputStream(dictName));
			textArea_1.append("Done reading xml file"+dictName+"for client "+numOfClients+"\n");
				//System.out.println("Done reading xml file!");
			Element root =  doc.getRootElement();
			List<Element> wordList = root.elements();
			for(Element wordEle : wordList){
				Element wordName = wordEle.element("wordName");
				String word = wordName.getText();
				int id = Integer.parseInt(wordEle.attributeValue("id"));
				String wordMeaning = wordEle.elementText("wordMeaning");
				
				Word oneWord = new Word(id,word,wordMeaning); 
				wordSet.add(oneWord);
			}
			}catch(IOException e){
				//e.printStackTrace();
				System.out.println("Adding words into word set failure! or dictionary is empty! Please add some words into the dictionary");
				textArea_1.append("Adding words into word set failure! or dictionary is empty! Please add some words into the dictionary"+"\n");
			}
				
		}}catch(Exception e){
			System.out.println("Adding words into word set failure!");
			textArea_1.append("Adding words into word set failure! or dictionary is empty! Please add some words into the dictionary"+"\n");
		}
		return wordSet;
	}
	public void start(int port){
		try {
			//Create a server socket listening on port 8080
			listeningSocket = new ServerSocket(port);
			Socket clientSocket = null;
			
			numOfClients = 0; //counter to keep track of the number of clients
			currentUsers = 0;
			//textField_1 = new JTextField();
			//System.out.println(textField);
			if(textArea_1 == null){
				//System.out.println("null textField");
				textArea_1 = new JTextArea();
			}
			
			
			//Listen for incoming connections for ever 
			while (true) {
				//System.out.println("port: "+ port);
				textArea_1.append("Server listening on port "+port+" for a connection"+"\n");
				//System.out.println("Server listening on port "+port+" for a connection");
				//Accept an incoming client connection request 
				clientSocket = listeningSocket.accept(); //This method will block until a connection request is received
				numOfClients++;
				currentUsers++;
				txtNumberOfCurrent.setText(Integer.toString(currentUsers));
				clientHandler handler = new clientHandler(clientSocket);
				Thread t = new Thread(handler);
				t.start();
				textArea_1.append("Initialized a thread to process the client"+"\n");
				//System.out.println("Initialized a thread to process the client");
				
				
				
				//clientSocket.close();
			}
		} catch (SocketException ex) {
			if(textArea_1 == null){
				//System.out.println("null textField");
				textArea_1 = new JTextArea();
			}
			//ex.printStackTrace();
			System.out.println("Connection request failed! The port might be in use! Please restart the program and try another port");
			textArea_1.append("Connection request failed"+"\n");
		}catch (IOException e) {
			if(textArea_1 == null){
				//System.out.println("null textField");
				textArea_1 = new JTextArea();
			}
			//e.printStackTrace();
			System.out.println("Connection request failed");
			textArea_1.append("Connection request failed"+"\n");
		} 
		finally {
			if(listeningSocket != null) {
				try {
					listeningSocket.close();
				} catch (IOException e) {
					System.out.println("Closing the listening Socket failed");
					//e.printStackTrace();
				}
			}
		}
		
	}
	/**
	 * @wbp.parser.entryPoint
	 */
	public static void serverWindow(){
		frame = new JFrame();
		frame.setBounds(100, 100, 762, 514);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		txtNumberOfCurrent = new JTextField();
		txtNumberOfCurrent.setFont(new Font("Times New Roman", Font.PLAIN, 30));
		txtNumberOfCurrent.setBounds(350, 382, 394, 85);
		txtNumberOfCurrent.setText("0");
		frame.getContentPane().add(txtNumberOfCurrent);
		txtNumberOfCurrent.setColumns(10);
		
		JLabel lblNumberOfCurrent = new JLabel("Number of current users:");
		lblNumberOfCurrent.setForeground(Color.BLACK);
		lblNumberOfCurrent.setFont(new Font("Tahoma", Font.PLAIN, 30));
		lblNumberOfCurrent.setBounds(12, 397, 377, 51);
		frame.getContentPane().add(lblNumberOfCurrent);
		
		JLabel lblClientMessage = new JLabel("Client message:");
		lblClientMessage.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblClientMessage.setBounds(12, 13, 172, 41);
		frame.getContentPane().add(lblClientMessage);
		
		JLabel lblServerProcessStatus = new JLabel("Server process status:");
		lblServerProcessStatus.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblServerProcessStatus.setBounds(12, 154, 234, 41);
		frame.getContentPane().add(lblServerProcessStatus);
		
		/*JScrollBar scrollBar = new JScrollBar();
		
	
		scrollBar.setBounds(711, 189, 21, 85);
		frame.getContentPane().add(scrollBar);*/
		
		JLabel lblServerAck = new JLabel("Server ACK:");
		lblServerAck.setFont(new Font("Tahoma", Font.PLAIN, 22));
		lblServerAck.setBounds(12, 279, 209, 27);
		frame.getContentPane().add(lblServerAck);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 188, 701, 79);
		frame.getContentPane().add(scrollPane);
		
		textArea_1 = new JTextArea();
		textArea_1.setFont(new Font("Monospaced", Font.PLAIN, 20));
		scrollPane.setViewportView(textArea_1);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 48, 700, 93);
		frame.getContentPane().add(scrollPane_1);
		
		textArea_2 = new JTextArea();
		textArea_2.setFont(new Font("Monospaced", Font.PLAIN, 20));
		scrollPane_1.setViewportView(textArea_2);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(12, 308, 701, 61);
		frame.getContentPane().add(scrollPane_2);
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
		scrollPane_2.setViewportView(textArea);
	}

	public static void main(String[] args) {
		//System.out.println("args[0] "+ args[0]);
		//System.out.println("args[1] "+ args[1]);
		try{port = Integer.parseInt(args[0]);
		dictPath = args[1];}
		catch(NumberFormatException e){
			System.out.println("Invalid input of port or path! Please restart the program with a valid input");
			System.exit(0);
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println("Invalid input of port or path! Please restart the program with a valid input");
			System.exit(0);
			
		}
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						
						serverWindow();
						frame.setVisible(true);
						
					} catch (Exception e) {
						System.out.println("Creating the frame for server failed");
						//e.printStackTrace();
					}
				}
			});
			try {
		tcpInteractiveServer server = new tcpInteractiveServer();
			server.start(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println("initializing server failed");
		}
		
		
		
	}
}
