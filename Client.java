package DemoCuoiki;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.Timer;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.StyledEditorKit.BoldAction;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.ResultSetMetaData;
import com.mysql.jdbc.Statement;

public class Client extends JFrame implements ActionListener{
	private JButton send,clear,exit,login,logout, newgame;
    private JPanel p_login,p_chat, sms, p_caro, p1;
    private JTextField nick,nick1,message;
    private JTextArea msg,online;
    private JButton[][] bt;
    private JLabel turn, demthoigian, picTTT;

    private Socket client;
    private DataStream dataStream;
    private DataOutputStream dos;
	private DataInputStream dis;
	static boolean flat = false;
	
	private BufferedImage board;
	private BufferedImage redX;
	private BufferedImage blueCircle;
	
	int [][]matran;
	int [][]matrandanh;
	int x=3, y=3, xx, yy;
	String temp = "";
	boolean winner = false;
    String col;
    
    Timer thoigian;
    Integer second, minute;
    JMenuBar menuBar;

	public Client(){
		super("Client Login");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				exit();
			}
		});
		setBounds(30, 30, 618, 460);
		addItem();
		setVisible(true);
	}
//-----[ Tạo giao diện ]--------//
	private void addItem() {
		getContentPane().setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		


		picTTT = new JLabel(new ImageIcon("image/tictactoe.jpg"));
		picTTT.setBounds(0, 0, 600, 422);
		exit = new JButton("Thoát");
		exit.addActionListener(this);
		send = new JButton("Gửi");
		send.addActionListener(this);
		clear = new JButton("Xóa");
		clear.addActionListener(this);
		login= new JButton("Vào game");
		login.addActionListener(this);
		logout= new JButton("Thoát");
		logout.addActionListener(this);
		

		p_chat = new JPanel();
		p_chat.setBounds(0, 40, 400,500);
		p_chat.setLayout(null);

		

		p1 = new JPanel();
		p1.setLayout(new FlowLayout(FlowLayout.LEFT));
		p1.setBounds(10, 10, 380, 40);
		nick = new JTextField(20);
		p1.add(new JLabel("User name: "));
		p1.add(nick);
		p1.add(exit);
		
		add(p1);


		online = new JTextArea(10,10);
		online.setEditable(false);
		
		sms = new JPanel();
		sms.setLayout(new FlowLayout(FlowLayout.LEFT));
		sms.setBounds(10, 50, 400, 330);
		msg = new JTextArea(20,34);
		msg.setEditable(false);
		sms.add(new JScrollPane(msg),BorderLayout.CENTER);

		JPanel p3 = new JPanel();
		p3.setLayout(new FlowLayout(FlowLayout.LEFT));
		p3.setBounds(10, 380, 380, 40);
		p3.add(new JLabel("Tin nhắn"));
		message = new JTextField(25);
		p3.add(message);
		p3.add(send);
		
				
				

		p_chat.add(p1);
		p_chat.add(sms);
		p_chat.add(p3);

		p_chat.add(new JLabel("     "),BorderLayout.WEST);

		p_chat.setVisible(false);
		add(p_chat,BorderLayout.CENTER);
		//-------------------------
		p_login = new JPanel();
		p_login.setLayout(null);
		p_login.setBounds(0, 0, 600, 422);
		JLabel user = new JLabel("User name: ");
		user.setBounds(50, 350, 100, 25);
		nick1=new JTextField(20);
		nick1.setBounds(130, 350, 200, 25);
		login.setBounds(350, 350, 100, 25);
		logout.setBounds(470, 350, 100, 25);
		p_login.add(user);
		p_login.add(nick1);
		p_login.add(login);
		p_login.add(logout);
		p_login.add(picTTT);
		
		p_login.setVisible(true);
		add(p_login);
		
		p_caro = new JPanel();
		p_caro.setLayout(new GridLayout(x, y));
		p_caro.setBounds(430, 50, 400, 400);
		add(p_caro);
		
		matran = new int[x][y];
		matrandanh= new int[x][y];
		
		bt = new JButton[x][y];
		
		for (int i=0; i<x; i++) {
			for (int j=0; j<y; j++) {
				final int a=i, b=j;
				
				bt[a][b] = new JButton();
				bt[a][b].setBackground(Color.pink);
				bt[a][b].addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						flat = true;
						
						thoigian.start();
	                    second = 0;
	                    minute = 0;
	                    String temp = minute.toString()+"0";
	                    String temp1 = second.toString()+"0";
	                    demthoigian.setText("Thời Gian:" + temp + ":" + temp1);
						
						matrandanh[a][b]=1;
						bt[a][b].setIcon(new ImageIcon("C:\\image\\redX.png"));
						
						try{
                            String msgcaro="caro," + String.valueOf(a) + "," + String.valueOf(b);
                            sendcaro(msgcaro);
                            setEnableButton(false);
                            turn.setText("Lượt đối thủ");
                        }
                        catch(Exception ie)
                        {
                            ie.printStackTrace();
                        }
						thoigian.stop();
					}
				});
				p_caro.add(bt[a][b]);
			}
		}
		p_caro.setVisible(false);
		
		turn = new JLabel();
        turn.setFont(new Font("TimesRoman", Font.ITALIC, 16));
        turn.setForeground(Color.BLACK);
        add(turn);
        turn.setBounds(560,70,450,100);
        turn.setVisible(false);
        
        menuBar = new JMenuBar();
        menuBar.setBounds(0, 0, 900, 20);
        JMenu menu = new JMenu("Menu");
        JMenuItem menuItem1 = new JMenuItem("History");
        menu.add(menuItem1);
        menuBar.add(menu);
        menuBar.setVisible(false);
        this.add(menuBar);
        menuItem1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				  new History("Lịch Sử");
				
				
			}
		});
  
        
        //--------
        
        demthoigian = new JLabel("Thời Gian:");
        demthoigian.setFont(new Font("TimesRoman", Font.ITALIC, 16));
        demthoigian.setForeground(Color.BLACK);
        add(demthoigian);
        demthoigian.setBounds(600,5,450,55);
        demthoigian.setVisible(false);
        second = 0;
        minute = 0;
        thoigian = new Timer(1000, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                        String temp = minute.toString();
                        String temp1 = second.toString();
                        if (temp.length() == 1) {
                                temp = "0" + temp;
                        }
                        if (temp1.length() == 1) {
                                temp1 = "0" + temp1;
                        }
                        if (second == 15) {
                        	thoigian.stop();
        	                String msgcheck="newgame,123";
        					sendcaro(msgcheck);
                            Object[] options = { "Dong y", "Huy bo" };
                            int m = JOptionPane.showConfirmDialog(p_caro, "Bạn đã thua! Bạn có muốn chơi lại không?", "Thong bao",JOptionPane.YES_NO_OPTION);
                            if (m == JOptionPane.YES_OPTION) {

                                    setVisiblePanel(p_caro);
                                    turn.setText("Chờ đối thủ...");
                                    newgame();
                                    setEnableButton(false);
                                    if (col=="1") 
                                    	{
                                    		setEnableButton(true);
                                    		turn.setText("Lượt của bạn");
                                    	}
                            } else if (m == JOptionPane.NO_OPTION) {
                                    thoigian.stop();
                                    String msgo="out,123";
        							sendcaro(msgo);
        							exit();
                            }
                        } else {
                                demthoigian.setText("Thời Gian:" + temp + ":" + temp1);
                                second++;
                        }

                                }
        });

		
	}
//---------[ Socket ]-----------//
	private void go() {
		try {
			client = new Socket("localhost",2207);
			dos=new DataOutputStream(client.getOutputStream());
			dis=new DataInputStream(client.getInputStream());

		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,"Lỗi kết nối, xem lại dây mạng hoặc Server chưa sẵn sàng.","Message Dialog",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		new Client().go();
	}
	private void sendMSG(String data){
		try {
			dos.writeUTF(data);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String getMSG(){
		String data=null;
		try {
			data=dis.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}

	public void getMSG(String msg1, String msg2){
		int stt = Integer.parseInt(msg1);
		switch (stt) {
		// tin nhắn của những người khác
		case 3:
			this.msg.append(msg2);
			break;
//		// update danh sách online
		case 4:
			this.online.setText(msg2);
			break;
		// server đóng cửa
		case 5:
			dataStream.stopThread();
			exit();
			break;
		// bổ sung sau
		case 6:
            String stream = msg2.toString();
            String[] data = stream.split(",");
            if (data[0].equals("caro")) {
                    thoigian.start();
                    second = 0;
                    minute = 0;
                    caro(data[1],data[2]);
                    setEnableButton(true);
                    if (winner == false) {
                    	System.out.println("aa");
                    	setEnableButton(true);
                    }
 
            }
            else if (data[0].equals("newgame")) 
            {
            	Object[] options = { "Dong y", "Huy bo" };
            	int m = JOptionPane.showConfirmDialog(this,
                        "Ban da thang.Ban co muon choi lai khong?", "Thong bao",
                        JOptionPane.YES_NO_OPTION);
            	if (m == JOptionPane.YES_OPTION) 
            	{

                    setVisiblePanel(p_caro);
                    turn.setText("Lượt đối thủ");
                    newgame();
                    setEnableButton(false);
                    String newg="newgame1,123";
                    sendcaro(newg);
            	} else if (m == JOptionPane.NO_OPTION) 
                	{
            			String msgcaro="out,123";
            			sendcaro(msgcaro);
                        thoigian.stop();
                        exit();
                	}
            } 
            else if(data[0].equals("newgame1")) 
                {
            	second = 0;
                minute = 0;
            	turn.setText("Lượt của bạn");
                setEnableButton(true);
//                thoigian.start();
                col = "1";
                }
            else if (data[0].equals("out"))
            {
            	JOptionPane.showConfirmDialog(this,
                        "Doi thu da thoat game!", "Thong bao",
                        JOptionPane.PLAIN_MESSAGE);
            	newgame();
            	setEnableButton(false);
                second = 0;
                minute = 0;
            }

                else if (data[0].equals("onl"))
                {
                	turn.setText("Lượt của bạn");
                	thoigian.start();
                	setEnableButton(true);
                }
                
	break;
case 7: int n=Integer.parseInt(msg2);
		if(n==1) 
		{
			turn.setText("Chờ đối thủ...");
			turn.setVisible(true);
			setEnableButton(false);
		}
		if (n==2) 
		{
		turn.setText("Lượt đối thủ");
		turn.setVisible(true);
		setEnableButton(false);
		String msgcaro="onl,123";
		sendcaro(msgcaro);
		}
		break;
default:
		break;
}
}
//----------------------------------------------
		
	
//----------------------------------------------
	private void checkSend(String msg){
		if(msg.compareTo("\n")!=0){
			this.msg.append("TÔI: "+msg);
			sendMSG("1");
			sendMSG(msg);
		}
	}
	private void sendcaro(String msg)
	{
		sendMSG("2");
		sendMSG(msg);
	}
	private void senDB() {
		sendMSG("3");
	}
	
	public void setEnableButton(boolean b) {
        for (int i = 0; i < x; i++)
        {
                for (int j = 0; j < y; j++) {
                        if (matrandanh[i][j] == 0)
                                bt[i][j].setEnabled(b);
                }
        }
}
	
	private boolean checkLogin(String nick){
		if(nick.compareTo("")==0)
			return false;
		else if(nick.compareTo("0")==0){
			return false;
		}
		else{
			sendMSG(nick);
			int sst = Integer.parseInt(getMSG());
			if(sst==0)
				 return false;
			else return true;
		}
	}

	private void exit(){
		try {
			sendMSG("0");
			dos.close();
			dis.close();
			client.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.exit(0);
	}
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==exit){
			try {
			String msgcaro="out,123";
			sendcaro(msgcaro);
			dataStream.stopThread();
			exit();
			}catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if(e.getSource()==newgame){
			thoigian.stop();
			String msgcaro="gg,123";
			sendcaro(msgcaro);
			newgame();
			setEnableButton(false);
			turn.setText("Lượt đối thủ");
		}
		else if(e.getSource()==clear){
			message.setText("");
		}
		else if(e.getSource()==send){
			checkSend(message.getText()+"\n");
			message.setText("");
		}
		else if(e.getSource()==login){
			if(checkLogin(nick1.getText())){
				setSize(900, 500);
				menuBar.setVisible(true);
				p_chat.setVisible(true);
				p_caro.setVisible(true);
				p1.setVisible(true);
				demthoigian.setVisible(true);
				p_login.setVisible(false);
				nick.setText(nick1.getText());
				nick.setEditable(false);
				this.setTitle(nick1.getText());
				msg.append("Đã đăng nhập thành công\n");
				dataStream = new DataStream(this, this.dis);
			}
			else{
				JOptionPane.showMessageDialog(this,"Đã tồn tại tài khoản này, bạn vui lòng nhập lại.","Message Dialog",JOptionPane.WARNING_MESSAGE);
			}
		}
		else if(e.getSource()==logout){
			
			exit();
		}
	}	
	
	public void newgame() {
        for (int i = 0; i < x; i++)
        {
                for (int j = 0; j < y; j++) {
                		bt[i][j].setIcon(null);
                        bt[i][j].setBackground(Color.pink);
                        matran[i][j] = 0;
                        matrandanh[i][j] = 0;
                }
        }
        setEnableButton(true);
        second = 0;
        minute = 0;
        thoigian.stop();
}
	
	public void setVisiblePanel(JPanel pHienthi) {
        add(pHienthi);
        pHienthi.setVisible(true);
        pHienthi.updateUI();// ......        
   }
	
	
	public int checkHang() {
        int win = 0, hang = 0, n = 0, k = 0;
        boolean check = false;
        
        for (int i = 0; i < x; i++) {
                for (int j = 0; j < y; j++) {
                        if (check) {
                                if (matran[i][j] == 1) {
                                        hang++;
                                        if (hang > 2) {
                                                win = 1;
                                                break;
                                        }
                                        continue;
                                } else {
                                        check = false;
                                        hang = 0;
                                }
                        }
                        if (matran[i][j] == 1) {
                                check = true;
                                hang++;
                        } else {
                                check = false;
                        }
                }
                hang = 0;
        }
        return win;
}

public int checkCot() {
        int win = 0, cot = 0;
        boolean check = false;
        for (int j = 0; j < y; j++) {
                for (int i = 0; i < x; i++) {
                        if (check) {
                                if (matran[i][j] == 1) {
                                        cot++;
                                        if (cot > 2) {
                                                win = 1;
                                                break;
                                        }
                                        continue;
                                } else {
                                        check = false;
                                        cot = 0;
                                }
                        }
                        if (matran[i][j] == 1) {
                                check = true;
                                cot++;
                        } else {
                                check = false;
                        }
                }
                cot = 0;
        }
        return win;
}
public int checkCheoPhai() {
        int win = 0, cheop = 0, n = 0, k = 0; int i=0, j=0;
        boolean check = false;

        while ((i<x) && (j<y)) {
        	if (matran[i][j]==1) cheop++;
        	i+=1;
        	j+=1;
        	if (cheop>2) {
            	win=1;
            	break;
            }
        }
        return win;
}
public int checkCheoTrai() {
        int win = 0, cheot = 0, n = 0; int i=2, j=0;
        boolean check = false;
        while ((j<y)) {
        	if (matran[i][j]==1) cheot++;
        	i-=1;
        	j+=1;
        	if (cheot>2) {
            	win=1;
            	break;
            }
        }
          return win;
}
	 public void caro(String x, String y)
	    {
		 	turn.setText("Lượt của bạn");
	        xx = Integer.parseInt(x);
	        yy = Integer.parseInt(y);
	        // danh dau vi tri danh
	        matran[xx][yy] = 1;
	        matrandanh[xx][yy] = 1;
	        bt[xx][yy].setEnabled(false);
	        bt[xx][yy].setDisabledIcon(new ImageIcon("C:\\image\\blueCircle.png"));
	        bt[xx][yy].setIcon(new ImageIcon("C:\\image\\blueCircle.png"));
	        // Kiem tra thang hay chua
	        winner = (checkHang() == 1 || checkCot() == 1 || checkCheoPhai() == 1 || checkCheoTrai() == 1);
	        if (checkHang() == 1 || checkCot() == 1 || checkCheoPhai() == 1
	                        || checkCheoTrai() == 1) {

	                thoigian.stop();
	                senDB();
	                String msgcheck="newgame,123";
					sendcaro(msgcheck);
                    Object[] options = { "Dong y", "Huy bo" };
                    int m = JOptionPane.showConfirmDialog(p_caro, "Bạn đã thua! Bạn có muốn chơi lại không?", "Thong bao",JOptionPane.YES_NO_OPTION);
                    if (m == JOptionPane.YES_OPTION) {
                            setVisiblePanel(p_caro);
                            turn.setText("Chờ đối thủ...");
                            newgame();
                            setEnableButton(false);
                            
                    } else if (m == JOptionPane.NO_OPTION) {
                            String msgo="out,123";
							sendcaro(msgo);
							exit();
                    }
	        }    
   }
	 
	 public class History extends JFrame
		{
			Vector vData = null, vTitle = null;
			
			public History(String s)
			{
				super(s);
				try {
					
				this.setBounds(200, 50, 500, 200);
				this.setLayout(null);
				this.getContentPane().setBackground(Color.PINK);
				
				Class.forName("com.mysql.jdbc.Driver");
				Connection conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/caro","root","");
				Statement stm = (Statement) conn.createStatement();
				ResultSet rs = stm.executeQuery("SELECT * FROM caroplay");
				
				ResultSetMetaData rsm = (ResultSetMetaData) rs.getMetaData();
				
				int col_num = rsm.getColumnCount();
				vTitle = new Vector(col_num);
				
				for (int i=1; i<=col_num; i++)
				{
					vTitle.add(rsm.getColumnLabel(i));
				}
				
				vData = new Vector(10,10);
				while (rs.next())
				{
					Vector row = new Vector(col_num);
					 for (int i=1; i<=col_num; i++)
						row.add(rs.getString(i));
					vData.add(row);
				}
				rs.close();
				stm.close();
				conn.close();
				JScrollPane tableResult = new JScrollPane(new JTable(vData, vTitle));
				this.setContentPane(tableResult);
				this.show();
				
				this.setVisible(true);
		
				}
				catch(Exception e)
				{
					System.out.println(e.getMessage());
				}
			
			}
		}
	
}
