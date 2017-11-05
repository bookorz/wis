package com.innolux.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

import org.apache.log4j.Logger;

import com.innolux.common.ToolUtility;
import com.innolux.interfaces.ISocketService;



public class SocketService {
	
	public Logger logger = Logger.getLogger(this.getClass());
	private Socket clientSocket;
	private BufferedReader clientReader;
	private PrintStream clientWriter;
	private String ip;
	private int port;
	private boolean isDead = true;
	private ISocketService SocketListener;
	
	public SocketService(String ip,int port){
		
	}
	
	public void setSocketListener(ISocketService _SocketListener){
		SocketListener = _SocketListener;
	}
	
	public void startService(){
		if(SocketListener == null){
			logger.error("startService error: SocketListener is null");
		}else{
			try {
				connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("startService connect error: "+ ToolUtility.StackTrace2String(e));
			}
		}
	}
	
	/**
	 * 開始進行連線
	 * 
	 * @throws IOException
	 */
	public void connect() throws IOException {
		InetAddress address = InetAddress.getByName(ip);
		// System.out.printf("Start Connect Server [%s:%d]...\n", ip, port);
		logger.info("readerIp : " + ip + "Start Connect Server [" + ip + ":" + port + "]...");
		// 此方法會阻擋往下執行，直到連線成功
		clientSocket = new Socket(address, port);

		// System.out.printf("Connected to Server [%s:%d] success!\n", ip,
		// port);
		logger.info("readerIp : " + ip + "Connected to Server [" + ip + ":" + port + "] success!");
		isDead = false;

		// 取出輸出入流
		clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
		clientWriter = new PrintStream(clientSocket.getOutputStream(), false, "UTF-8");

		// 傾聽所有從 Server 送來的資料
		listenMsg();
	}

	/**
	 * 以一個新的執行緒去不斷傾聽 Server 送來的資料。
	 * 
	 * 注意，當 Server 關閉時，使用 {@link InputStream} 會拋出 {@link SocketException}。
	 * 
	 * 之後 Client 會關閉 {@link Socket Socket}
	 * 
	 */
	private void listenMsg() {
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				String str = null;
				while (checkAlive() && !isDead) {
					try {
						str = getServerMessage();
					} catch (IOException e) {
						
						logger.error("readerIp : " + ip + " connect error:" + ToolUtility.StackTrace2String(e));
						close();
					} finally {
						// close();
					}
					if (str != null) {
						
						
						logger.info("readerIp : " + ip + " Server Message:" + str);
						SocketListener.onSocketMsg(str);
						
					}
				}
				
			}
		});
		t.setDaemon(false);
		t.start();
	}

	/**
	 * 
	 * @return 回傳一行文字，不包含"\n"。若是null代表沒資料了。
	 * @throws IOException
	 */
	private String getServerMessage() throws IOException {
		return clientReader.readLine();
	}

	public void sendMessageToServer(String message) {
		clientWriter.println(message);
		clientWriter.flush();
	}

	public boolean checkAlive() {
		return clientSocket.isConnected() && !clientSocket.isClosed();
	}

	public void close() {
		System.out.println("close.");
		isDead = true;
		// timer.cancel();
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace(System.err);
			logger.error(ip + " " + "Exception:" + ToolUtility.StackTrace2String(e));
		}
	}
}
