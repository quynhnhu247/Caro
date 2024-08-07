package DemoCuoiki;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ClientConnect extends Thread {
	public Socket client;
	public Server server;
	private String nickName;
	private DataOutputStream dos;
	private DataInputStream dis;
	private boolean run;

	public ClientConnect(Server server, Socket client){
		try {
			this.server=server;
			this.client=client;
			dos= new DataOutputStream(client.getOutputStream());
			dis= new DataInputStream(client.getInputStream());
			run=true;
			this.start();
		} catch (IOException e) {
			
		}

	}
	public void run(){
		// xử lí đăng nhập
		String msg=null;
		while(run){
			nickName=getMSG();
			if(nickName.compareTo("0")==0){
				logout();
			}
			else {
				if(checkNick(nickName)){
					sendMSG("0");
				}
				else{
					server.user.append(nickName+" đã kết nối với máy chủ.\n");
					server.sendAll(nickName,nickName+" đã vào phòng.\n");
					server.listUser.put(nickName, this);
					server.sendAllUpdate(nickName);
					sendMSG("1");
					diplayAllUser();
					while(run){
						int stt = Integer.parseInt(getMSG());
						if (stt==3) {
							server.update(nickName);
						}
						switch(stt){
							case 0:
								run=false;
								server.listUser.remove(this.nickName);
								exit();
								break;
							case 1:
								msg = getMSG();
								server.sendAll(nickName,nickName+" : "+msg);
								break;
							case 2:
								msg = getMSG();
								server.sendCaro(nickName, msg);
						}
					}
				}
			}
		}
	}
	private void logout() {
		try {
			dos.close();
			dis.close();
			client.close();
		} catch (IOException e) {
			
		}
	}
	private void exit(){
		try {
			server.sendAllUpdate(nickName);
			dos.close();
			dis.close();
			client.close();
			server.user.append(nickName+" đã thoát\n");
			server.sendAll(nickName,nickName+" đã thoát\n");
		} catch (IOException e) {
			
		}
	}
	private boolean checkNick(String nick){
		return server.listUser.containsKey(nick);
	}
	private void sendMSG(String data){
		try {
			dos.writeUTF(data);
			dos.flush();
		} catch (IOException e) {
			
		}

	}
	public void sendMSG(String msg1,String msg2){
		sendMSG(msg1);
		sendMSG(msg2);
	}
	private String getMSG(){
		String data=null;
		try {
			data=dis.readUTF();
		} catch (IOException e) {
			
		}
		return data;
	}
	private void diplayAllUser(){
		String name = server.getAllName();
		sendMSG("4");
		sendMSG(name);
	}
}

