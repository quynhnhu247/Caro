package DemoCuoiki;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CaroDB {
	
	Connection conn;
	Statement st;
	
	public void CaroDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			 conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/caro","root","");
			 st = (Statement)conn.createStatement();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public int ChangeDB(String sql) {
		int n = 0;
		try {
			CaroDB();
			st = conn.createStatement();
			n = st.executeUpdate(sql);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return n;
		
	}

}
