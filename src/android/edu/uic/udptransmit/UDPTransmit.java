package edu.uic.udptransmit;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.JsonWriter;
import android.webkit.WebSettings.PluginState;

public class UDPTransmit extends CordovaPlugin {

	DatagramChannel channel;
	ByteBuffer read = ByteBuffer.allocate(1024);
	ByteBuffer write = ByteBuffer.allocate(1024);

	// Constructor
	public UDPTransmit() {
		try {
			channel = DatagramChannel.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Handles and dispatches "exec" calls from the JS interface
	// (udptransmit.js)
	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		if ("recvfrom".equals(action)) {
			InetSocketAddress address = this.recvfrom();
			if (address != null) {
				JSONObject j = new JSONObject();
				byte[] bytes = new byte[read.limit()];
				read.get(bytes);
				j.put("message", new String(bytes, Charset.forName("UTF-8")));
				j.put("ip", address.getHostString());
				j.put("port", address.getPort());

				PluginResult result = new PluginResult(PluginResult.Status.OK,
						j);
				result.setKeepCallback(false);

				callbackContext.sendPluginResult(result);
				callbackContext.success();
			} else {
				callbackContext.error("ioerror");
			}
			return true;
		} else if ("sendto".equals(action)) {
			// Call the function to create the Datagram packet
			if (this.sendto(args.getString(0), args.getString(1),
					args.getInt(2))) {
				callbackContext.success();
			} else {
				callbackContext.error("ioerror");
			}
			return true;
		}

		return false;
	}

	public boolean sendto(String data, String host, int port) {

		// convert String to bytes[] for arg 0
		write.clear();
		write.put(data.getBytes());
		write.flip();

		try {
			channel.send(write, new InetSocketAddress(host, port));
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public InetSocketAddress recvfrom() {
		try {
			InetSocketAddress address = null;
			Selector selector = Selector.open();
			channel.register(selector, SelectionKey.OP_READ);
			if (selector.selectNow() > 0) {
				read.clear();
				address = (InetSocketAddress) channel.receive(read);
				read.flip();
			}
			return address;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}