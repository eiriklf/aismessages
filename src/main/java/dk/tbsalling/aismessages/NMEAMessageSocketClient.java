/*
 * AISMessages
 * - a java-based library for decoding of AIS messages from digital VHF radio traffic related
 * to maritime navigation and safety in compliance with ITU 1371.
 * 
 * (C) Copyright 2011 by S-Consult ApS, DK31327490, http://s-consult.dk, Denmark.
 * 
 * Released under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * For details of this license see the nearby LICENCE-full file, visit http://creativecommons.org/licenses/by-nc-sa/3.0/
 * or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 * 
 * NOT FOR COMMERCIAL USE!
 * Contact sales@s-consult.dk to obtain a commercially licensed version of this software.
 * 
 */

package dk.tbsalling.aismessages;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class NMEAMessageSocketClient {

	private static final Logger log = Logger.getLogger(NMEAMessageSocketClient.class.getName());

	@SuppressWarnings("unused")
	private NMEAMessageSocketClient() {
		this.source = null;
		this.socketAddress = null;
		this.decodedMessageHandler = null;
	}

	public NMEAMessageSocketClient(String source, String host, Integer port, DecodedAISMessageHandler decodedMessageHandler) throws UnknownHostException {
		this.source = source;
		InetAddress inetAddress = InetAddress.getByName(host);
		this.socketAddress = new InetSocketAddress(inetAddress, port);
		this.decodedMessageHandler = decodedMessageHandler;
	}

	public void requestStop() {
		if (streamReader != null)
			streamReader.requestStop();
	}

	public void run() throws IOException {
	    log.info("NMEAMessageSocketClient running.");
	    Socket socket = new Socket();
	    socket.connect(socketAddress);
	    log.info("Connected to AIS server on " + socketAddress.toString());
	    InputStream inputStream = socket.getInputStream();
	    streamReader = new NMEAMessageInputStreamReader(source, inputStream, decodedMessageHandler);
	    streamReader.run();
	    // TODO: Close socket
	    log.info("NMEAMessageSocketClient stopping.");
	}

	private NMEAMessageInputStreamReader streamReader;
	private final String source;
	private final SocketAddress socketAddress;
	private final DecodedAISMessageHandler decodedMessageHandler;
}
