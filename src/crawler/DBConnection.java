package crawler;
import java.net.URL;
import java.sql.*;
public class DBConnection {

	public static Connection connect(){
		try{

			try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				// TODO Auto-generated catch block

			}
			Connection connection = DriverManager.getConnection("jdbc:mysql://crawler.cq2sriivd3k3.us-west-2.rds.amazonaws.com:3306/mycrawler","dinorahcarrion","121password");
			return connection;

		}catch(SQLException sqle){
			System.out.println("Connection failed: "+sqle.getSQLState());
			return null;
		}

	}

	/*Adds subdomains after we have populated our db*/
	public static void addSubDomains(Connection c){

		Statement st = null;
		try{
			st = c.createStatement();
			st = c.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			ResultSet rs= st.executeQuery("SELECT * FROM urldb");

			while (rs.next()) {
				String s = (String) ProcessResults.subDomains(rs.getString("url"));
				rs.updateString("subdomain",s);
				rs.updateRow();
			}

		}catch(SQLException e){}
		finally{

			try {
				if(st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}
}
