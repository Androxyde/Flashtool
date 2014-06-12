package com.btr.proxy.search.wpad.dhcp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * This class represents a Socket for sending DHCP Messages
 * 
 * @author Jason Goldschmidt and Simon Frankenberger
 * 
 * @see java.net.DatagramSocket
 */

public class DHCPSocket extends DatagramSocket {
	/**
	 * Default socket timeout (1 second)
	 */
	private int SOCKET_TIMEOUT = 1000;
	
	/**
	 * Default MTU (Maximum Transmission Unit) for ethernet (in bytes)
	 */
	private int mtu = 1500;
	
	/** 
	 * Constructor for creating DHCPSocket on a specific port on the local
	 * machine.
	 * 
	 * @param inPort The port for the application to bind.
	 * 
	 * @throws SocketException As thrown by the {@link Socket} constructor
	 */
	public DHCPSocket(int inPort) throws SocketException {
		super(inPort);
		setSoTimeout(this.SOCKET_TIMEOUT);
	}
	
	/**
	 * Sets the Maximum Transfer Unit for the UDP DHCP Packets to be set.
	 * 
	 * @param inSize Integer representing desired MTU
	 */
	public void setMTU(int inSize) {
		this.mtu = inSize;
	}
	
	/**
	 * Returns the set MTU for this socket
	 * 
	 * @return The Maximum Transfer Unit set for this socket
	 */
	public int getMTU() {
		return this.mtu;
	}
	
	/**
	 * Sends a DHCPMessage object to a predifined host.
	 * 
	 * @param inMessage Well-formed DHCPMessage to be sent to a server
	 * 
	 * @throws IOException If the message could not be sent.
	 */
	public synchronized void send(DHCPMessage inMessage) throws IOException {
		byte data[] = new byte[this.mtu];
		data = inMessage.externalize();
		InetAddress dest = null;
		try {
			dest = InetAddress.getByName(inMessage.getDestinationAddress());
		}
		catch (UnknownHostException e) {
		}
		
		DatagramPacket outgoing = new DatagramPacket(data, data.length, dest,
		    inMessage.getPort());
		
		send(outgoing); // send outgoing message
	}
	
	/** 
	 * Receives a datagram packet containing a DHCP Message into
	 * a DHCPMessage object.
	 * 
	 * @return <code>true</code> if message is received, 
	 *         <code>false</code> if timeout occurs.  
	 * @param outMessage DHCPMessage object to receive new message into
	 */
	public synchronized boolean receive(DHCPMessage outMessage) {
		try {
			DatagramPacket incoming = new DatagramPacket(new byte[this.mtu],
			    this.mtu);
			//gSocket.
			receive(incoming); // block on receive for SOCKET_TIMEOUT
			
			outMessage.internalize(incoming.getData());
		}
		catch (java.io.IOException e) {
			return false;
		} // end catch
		return true;
	}
}
