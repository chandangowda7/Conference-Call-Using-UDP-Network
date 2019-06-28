import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;

public class Conference_three_two extends JApplet implements ActionListener
{
	public Conference_three_two() {
	}
	
	DatagramSocket socket1, socket2;
	DatagramPacket packet1, packet2, packet3, packet4;
	DataLine.Info speakerinfo, microphoneinfo;
	SourceDataLine speaker1, speaker2;
	TargetDataLine microphone;
	AudioFormat format;
	byte[] microphonebyte;
	String conference_three;
	JLabel lblConnected;
	JButton btnNewButton;
	boolean disconnect, disconnect_one, disconnect_two;
	JButton btnNewButton_1;
	String stopaddress, serverip, filesavelocation;
	
	public void init()
	{
		try {
			
			socket1 = new DatagramSocket(9993);
			socket2 = new DatagramSocket(9994);
			
		} catch (SocketException e) {
			
		}
		filesavelocation = "C:\\Users\\NISHCHAY S K\\important documents\\Desktop";
		serverip = "192.168.49.2";
		stopaddress = "";
		disconnect = false;
		getContentPane().setLayout(null);
		btnNewButton = new JButton("Start");
		setSize(450, 200);
		btnNewButton.addActionListener(this);
		btnNewButton.setBounds(68, 90, 110, 31);
		getContentPane().add(btnNewButton);
		
		lblConnected = new JLabel("Connected");
		lblConnected.setBounds(193, 61, 99, 19);
		getContentPane().add(lblConnected);
		lblConnected.setVisible(false);
		
		btnNewButton_1 = new JButton("Disconnect");
		btnNewButton_1.addActionListener(this);
		btnNewButton_1.setBounds(268, 90, 99, 31);
		getContentPane().add(btnNewButton_1);
		btnNewButton_1.setEnabled(false);
		
		format = new AudioFormat(12000,16,2,true,false);
		speakerinfo = new DataLine.Info(SourceDataLine.class, format);
		microphoneinfo = new DataLine.Info(TargetDataLine.class, format);
		
		try {
			
			speaker1 = (SourceDataLine) AudioSystem.getLine(speakerinfo);
			speaker2 = (SourceDataLine) AudioSystem.getLine(speakerinfo);
			microphone = (TargetDataLine) AudioSystem.getLine(microphoneinfo);
			
		} catch (LineUnavailableException e) {

			e.printStackTrace();
		}
	
	}
	
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getActionCommand().equals("Start"))
		{
			
			btnNewButton.setEnabled(false);
			new Thread(new Runnable() {
					
				public void run() {
					{
						
						try {
							
							speaker1.open();
							speaker2.open();
							speaker1.start();
							speaker2.start();
							
						} catch (Exception e1) { }
						
					
						byte[] conn = "Connect".getBytes();
						
						try {
							
							packet1 = new DatagramPacket(conn, 0,conn.length,InetAddress.getByName(serverip), 9991); // send to conference_one
							socket1.send(packet1);
							byte[] repip = new byte[20];
							packet1 = new DatagramPacket(repip, repip.length);
							socket1.receive(packet1);
							
							conference_three = new String(repip,0,repip.length).trim();
							lblConnected.setVisible(true);
							btnNewButton_1.setEnabled(true);
							
							System.out.println("Recieved from one "+conference_three);
							
							microphone.open();
							microphone.start();
							
							
						} catch (Exception e1) { }
						
						
						
						new Thread(new Runnable() {
							
							public void run() {
								{	
									
									byte[] b = new byte[10000];
									packet1 = new DatagramPacket(b, b.length);
									ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
									
									while(!disconnect_one)
									{
										if(disconnect)
										{
											byte[] b1 = baos1.toByteArray();
											ByteArrayInputStream bais1 = new ByteArrayInputStream(b1);
											AudioInputStream ais = new AudioInputStream(bais1, format, b1.length);
											try
											{
												AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filesavelocation + "/audio4.wav"));
											}catch(Exception e) { }
											break;
										
										}
										
										try {
															
											socket1.receive(packet1);
											System.out.println("Receiving in socket 1");
											if(packet1.getLength() == 10)
											{
												disconnect_one = true;
												System.out.println("Disconnected in  socket 1");
												stopaddress = packet1.getAddress().getHostAddress();
												byte[] b1 = baos1.toByteArray();
												ByteArrayInputStream bais1 = new ByteArrayInputStream(b1);
												AudioInputStream ais = new AudioInputStream(bais1, format, b1.length);
												AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filesavelocation + "/audio4.wav"));
											}
											
											baos1.write(b);
											speaker1.write(b, 0, b.length);
												
											} catch (IOException e) {
												System.out.println(e);
											}
										
									}
								}
							}
						}).start();
						
						
						new Thread(new Runnable() {
							
							public void run() {
								{
									
									byte[] b = new byte[10000];
									packet2 = new DatagramPacket(b, b.length);
									ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
									
									while(!disconnect_two)
									{
										if(disconnect)
										{
											byte[] b1 = baos1.toByteArray();
											ByteArrayInputStream bais1 = new ByteArrayInputStream(b1);
											AudioInputStream ais = new AudioInputStream(bais1, format, b1.length);
											try
											{
												AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filesavelocation + "/audio5.wav"));
											}catch(Exception e) { }
											break;
											
										}
										
										try {
											
											socket2.receive(packet2);
											System.out.println("Receiving in socket 2");
											
											if(packet2.getLength() == 10)
											{
												disconnect_two = true;
												System.out.println("Disconnected in  socket 2");
												stopaddress = packet2.getAddress().getHostAddress();
												byte[] b1 = baos1.toByteArray();
												ByteArrayInputStream bais1 = new ByteArrayInputStream(b1);
												AudioInputStream ais = new AudioInputStream(bais1, format, b1.length);
												AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filesavelocation + "/audio5.wav"));
											}
											
											baos1.write(b);
											speaker2.write(b, 0, b.length);

										} catch (IOException e) {
											
											System.out.println(e);
											
										}
										

										
									}
									
								}
							}
						}).start();	
						
						
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								
								microphonebyte = new byte[10000];
								int i1 = 0, i2 = 0;
								ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
								
								while(true)
								{
									if(disconnect)
									{
										byte[] b1 = baos1.toByteArray();
										ByteArrayInputStream bais1 = new ByteArrayInputStream(b1);
										AudioInputStream ais = new AudioInputStream(bais1, format, b1.length);
										try
										{
											AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filesavelocation + "/audio6.wav"));
										}catch(Exception e) { }
										break;
									}
									
									if(disconnect_one && disconnect_two)
									{
										socket1.close();
										socket2.close();
										lblConnected.setText("Disconnected");
										btnNewButton_1.setEnabled(false);
									}
									
									int read = microphone.read(microphonebyte, 0 ,microphonebyte.length);
									
									try
									{
										
										if(stopaddress.equals(serverip))
										{
											i1++;
										}
										if(i1==0)
										{
											packet3 = new DatagramPacket(microphonebyte, 0 , microphonebyte.length, InetAddress.getByName(serverip), 9992);
											socket1.send(packet3);
											System.out.println("Sending socket 1");
										}
										
										if(stopaddress.equals(conference_three))
										{
											i2++;
										}
										if(i2==0)
										{
											packet4 = new DatagramPacket(microphonebyte, 0 , microphonebyte.length, InetAddress.getByName(conference_three), 9996); //before was 9995
											socket2.send(packet4);
											System.out.println("Sending socket 2");
										}
										
										if(i1!=0 && i2!=0)
										{
											byte[] b1 = baos1.toByteArray();
											ByteArrayInputStream bais1 = new ByteArrayInputStream(b1);
											AudioInputStream ais = new AudioInputStream(bais1, format, b1.length);
											AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filesavelocation + "/audio6.wav"));
										}
										
										baos1.write(microphonebyte);
										
									}catch(Exception e) { }

									
								}
								
							}
						}).start();
					
						
						
						
						
						
//						try {
//							
//							File f = new File("C:\\Users\\NISHCHAY S K\\important documents\\Desktop/wavfile1.wav");
//							FileInputStream fin = new FileInputStream(f);
//							
//							while(fin.read(b, 0, b.length)!=-1)
//							{
//								packet1 = new DatagramPacket(b, 0,b.length,InetAddress.getLocalHost(),9991);
//								socket1.send(packet1);
//								speaker.write(b1, 0, b1.length);
//								
//								System.out.println("Sending");
//								
//							}
//							
//							
//						} catch (Exception e) {
//							
//							System.out.println(e.getStackTrace());
//						}
						
						
				}
				
			}
		}).start();

		}
		
		else if(arg0.getActionCommand().equals("Disconnect"))
		{
			
			disconnect = true;
			lblConnected.setText("Disconnected");
			try
			{
				byte[] disconnectbyte = "Disconnect".getBytes();
				
				packet3 = new DatagramPacket(disconnectbyte, 0 , disconnectbyte.length, InetAddress.getByName(serverip), 9992);
				socket1.send(packet3);
				
				packet4 = new DatagramPacket(disconnectbyte, 0 , disconnectbyte.length, InetAddress.getByName(conference_three), 9996); //before was 9995
				socket2.send(packet4);
				
				System.out.println("Sent");
				socket1.close();
				socket2.close();
				
			}catch(Exception e) { }
			
			btnNewButton_1.setEnabled(false);
			microphone.close();
			speaker1.close();
			speaker2.close();
			
		}
		
	}
}
