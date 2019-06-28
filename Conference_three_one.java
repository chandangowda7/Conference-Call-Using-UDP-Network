import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;

import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.DataLine.Info;
import javax.swing.JApplet;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.net.SocketException;
import java.util.Dictionary;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;


public class Conference_three_one extends JApplet implements ActionListener
{
	public Conference_three_one() {
		
	}
	
	
	int count, confirm, play1;
	DatagramSocket socket1, socket2;
	DatagramPacket packet1,packet2, packet3, packet4;
	DataLine.Info speakerinfo, microphoneinfo;
	SourceDataLine speaker1, speaker2;
	TargetDataLine microphone;
	AudioFormat format;
	byte[] microphonebyte;
	String conference_two, conference_three;
	boolean disconnect_one, disconnect_two;
	JButton btnNewButton;
	JLabel lblNewLabel, lblNewLabel_1;
	boolean disconnect;
	JButton btnNewButton_1;
	String stopaddress, filesavelocation;
	
	
	public void init()
	{
		try {
			
			socket1 = new DatagramSocket(9991);
			socket2 = new DatagramSocket(9992);
			
		} 
		
		catch (SocketException e){ }
		
		
		filesavelocation = "C:\\Users\\NISHCHAY S K\\important documents\\Desktop";
		
		conference_two = conference_three = "";
		confirm = 0;	
		count = 0;
		play1 = 0;
		disconnect = false;

		stopaddress = "";
		
		disconnect_one = false;
		disconnect_two = false;
		
		getContentPane().setLayout(null);
		btnNewButton = new JButton("Start");
		setSize(450, 200);
		btnNewButton.addActionListener(this);
		btnNewButton.setBounds(77, 90, 99, 31);
		getContentPane().add(btnNewButton);
		
		btnNewButton_1 = new JButton("Disconnect");
		btnNewButton_1.addActionListener(this);
		btnNewButton_1.setBounds(268, 90, 99, 31);
		getContentPane().add(btnNewButton_1);
		btnNewButton_1.setEnabled(false);
		
		lblNewLabel = new JLabel("Connection Count: ");
		lblNewLabel.setBounds(155, 50, 115, 26);
		getContentPane().add(lblNewLabel);
		lblNewLabel.setVisible(false);
		
		lblNewLabel_1 = new JLabel("0");
		lblNewLabel_1.setBounds(264, 53, 45, 20);
		getContentPane().add(lblNewLabel_1);
		lblNewLabel_1.setVisible(false);
		
		format = new AudioFormat(12000,16,2,true,false);
		speakerinfo = new DataLine.Info(SourceDataLine.class, format);
		microphoneinfo = new DataLine.Info(TargetDataLine.class, format);
		
		try {
			
			speaker1 = (SourceDataLine) AudioSystem.getLine(speakerinfo);
			speaker2 = (SourceDataLine) AudioSystem.getLine(speakerinfo);
			microphone = (TargetDataLine) AudioSystem.getLine(microphoneinfo);
			
		} catch (Exception e) {

			e.printStackTrace();
		}
		
	}
	
	public void actionPerformed(ActionEvent arg0) {
		
		if(arg0.getActionCommand().equals("Start"))
		{
		
			btnNewButton.setEnabled(false);
			lblNewLabel.setVisible(true);
			lblNewLabel_1.setVisible(true);
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					try {
						
						speaker1.open();
						speaker2.open();
						speaker1.start();
						speaker2.start();
						
					} catch (Exception e1) { }
					
					new Thread(new Runnable() {
						public void run()
						{
							byte[] b = new byte[7];
							packet1 = new DatagramPacket(b, b.length);
							try
							{
								
								socket1.receive(packet1);	
								if(new String(b).equals("Connect"))
								{
									//conference_two = packet1.getAddress().getHostAddress();
									conference_two = "192.168.49.3";
									System.out.println("Address is: "+conference_two);
									count++;
									lblNewLabel_1.setText(count+"");
									
									if(count == 2)
									{
										sendconfirm();
										play();
									}
								}
								
							}catch(Exception e) { }
					
						}
					}).start();
						
					new Thread(new Runnable() {
						
						@Override
						public void run() {
	
							byte[] b1 = new byte[7];
							packet2 = new DatagramPacket(b1, b1.length);
							try
							{
								socket2.receive(packet2);
								if(new String(b1).equals("Connect"))
								{
									//conference_three = packet2.getAddress().getHostAddress();
									conference_three = "192.168.49.4";
									System.out.println("Address is: "+conference_three);
									count++;
									lblNewLabel_1.setText(count+"");
									
									if(count == 2)
									{
										sendconfirm();
										play();
									}
								}
								
							}catch(Exception e) { }
					
						}
					}).start();
					
				}
			}).start();
		}
		else if(arg0.getActionCommand().equals("Disconnect"))
		{
			
			disconnect = true;
			btnNewButton_1.setEnabled(false);
			lblNewLabel.setText("Disconnected");
			byte[] disconnectbyte = "Disconnect".getBytes();
			try
			{
				packet3 = new DatagramPacket(disconnectbyte, 0 , disconnectbyte.length, InetAddress.getByName(conference_two), 9993);
				socket1.send(packet3);
				packet4 = new DatagramPacket(disconnectbyte, 0 , disconnectbyte.length, InetAddress.getByName(conference_three), 9995); //9996 before
				socket2.send(packet4);
				System.out.println("Sent");
				socket1.close();
				socket2.close();
				
			}catch(Exception e) { }
			
			speaker1.close();
			speaker2.close();
			microphone.close();
		}
	
	}	
	public void sendconfirm()
	{
		confirm++;
		if(confirm == 1)
		{
			btnNewButton_1.setEnabled(true);
			try
			{
				
				packet1 = new DatagramPacket(conference_three.getBytes(),0, conference_three.getBytes().length,InetAddress.getByName(conference_two), 9993); // send conference_two, ip of conference_three
				packet2 = new DatagramPacket(conference_two.getBytes(),0, conference_three.getBytes().length,InetAddress.getByName(conference_three), 9996); // send conference_three, ip of conference_two
				socket1.send(packet1);
				socket1.send(packet2);
				
				microphone.open();
				microphone.start();
				
			}
			catch(Exception e) { }
		}
	}
	
	public void play()
	{
		play1++;
		
		if(play1 == 1)
		{
		
		new Thread(new Runnable() {
			
			public void run() {
				{	
					
					byte[] b = new byte[10000];
					packet1 = new DatagramPacket(b, b.length);
					ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
					
					while(!disconnect_one)
					{
						System.out.println("Entering "+disconnect);
						
						if(disconnect)
						{
							System.out.println("Entered");
							byte[] b1 = baos1.toByteArray();
							ByteArrayInputStream bais1 = new ByteArrayInputStream(b1);
							AudioInputStream ais = new AudioInputStream(bais1, format, b1.length);
							try
							{
								AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filesavelocation +"/audio1.wav"));
							}catch(Exception e) { }
							
							break;
							
						}
						
						try {
											
							socket1.receive(packet1);
							System.out.println("Receiving socket 1");
							if(packet1.getLength() == 10)
							{
								disconnect_one = true;
								System.out.println("Receiving stopped in socket 1");
								stopaddress = packet1.getAddress().getHostAddress();
								byte[] b1 = baos1.toByteArray();
								ByteArrayInputStream bais1 = new ByteArrayInputStream(b1);
								AudioInputStream ais = new AudioInputStream(bais1, format, b1.length);
								AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filesavelocation + "/audio1.wav"));
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
								AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filesavelocation + "/audio2.wav"));
							}catch(Exception e) { }
							
							break;
							
						}
						
							try {
								
								socket2.receive(packet2);
								System.out.println("Receiving socket 2");
								if(packet2.getLength() == 10)
								{
									disconnect_two = true;
									System.out.println("Receiving stopped in socket 2");
									stopaddress = packet2.getAddress().getHostAddress();
									byte[] b1 = baos1.toByteArray();
									ByteArrayInputStream bais1 = new ByteArrayInputStream(b1);
									AudioInputStream ais = new AudioInputStream(bais1, format, b1.length);
									AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filesavelocation + "/audio2.wav"));
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
						System.out.println("Closed");
						byte[] b1 = baos1.toByteArray();
						ByteArrayInputStream bais1 = new ByteArrayInputStream(b1);
						AudioInputStream ais = new AudioInputStream(bais1, format, b1.length);
						try
						{
							AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filesavelocation + "/audio3.wav"));
						}catch(Exception e) { }
						
						break;
					}
					if(disconnect_one && disconnect_two)
					{
						socket1.close();
						socket2.close();
						lblNewLabel.setText("Disconnected");
						btnNewButton_1.setEnabled(false);
					}
					
					int read = microphone.read(microphonebyte, 0 ,microphonebyte.length);
					try
					{
							if(stopaddress.equals(conference_two))
							{
								i1++;
							}
							
							if(i1 == 0)
							{
								packet3 = new DatagramPacket(microphonebyte, 0 , microphonebyte.length, InetAddress.getByName(conference_two), 9993);
								socket1.send(packet3);
								System.out.println("sending socket 1");
							}
							
							if(stopaddress.equals(conference_three))
							{
								i2++;
							}
							if(i2 == 0)
							{
								packet4 = new DatagramPacket(microphonebyte, 0 , microphonebyte.length, InetAddress.getByName(conference_three), 9995); //9996 before
								socket2.send(packet4);
								System.out.println("Sending socket 2");
							}
						
							if(i1!=0 && i2!=0)
							{
								byte[] b1 = baos1.toByteArray();
								ByteArrayInputStream bais1 = new ByteArrayInputStream(b1);
								AudioInputStream ais = new AudioInputStream(bais1, format, b1.length);
								AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filesavelocation + "/audio3.wav"));
							}
							
							baos1.write(microphonebyte);
							
					}catch(Exception e) { }

				}
				
			}
		}).start();
		
		}
	}
}
