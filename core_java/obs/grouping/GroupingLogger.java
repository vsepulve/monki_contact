package obs.grouping;

import java.io.File;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.PrintWriter;

import java.sql.*;
import java.sql.SQLException;
import com.mysql.jdbc.Driver;

import java.util.Date;
import java.text.SimpleDateFormat;

public class GroupingLogger{
	
	static SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
	private Connection con = null;
	private String log_file = null;
	private PreparedStatement insert_log;
	
	private String server;
	private String db;
	private String user;
	private String pass;
	
	public GroupingLogger(String _server, String _db, String _user, String _pass, boolean iniciar){
		
		server = _server;
		db = _db;
		user = _user;
		pass = _pass;
		
		if(iniciar){
			if(!connect(server, db, user, pass)){
				System.out.println("GroupingLogger - Problems connecting");
				return;
			}
			prepareQueries();
		}
	}
	
	public void close(){
		try{
			con.close();
			insert_log = null;
		}
		catch(Exception e){
			System.out.println("GroupingLogger.close - Problems closeing");
			e.printStackTrace();
			return;
		}
	}
	
	public void open(){
		if(!connect(server, db, user, pass)){
			System.out.println("GroupingLogger - Problems connecting");
			return;
		}
		prepareQueries();
	}
	
	private boolean connect(String server, String db, String user, String pass){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con=DriverManager.getConnection("jdbc:mysql://"+server+"/"+db, user, pass);
		}
		catch(Exception e){
			System.out.println("GroupingLogger.connect - Problems connecting (user: \""+user+"\", pass: \""+pass+"\")");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private void prepareQueries(){
		String sql;
		try{
			
			sql = "insert into data_grouping_log (old_group_id, new_group_id, contact_id, module_id, message) values (?, ?, ?, ?, ?)";
			insert_log = con.prepareStatement(sql);
			
			//Fin Affinities
			
		}
		catch(SQLException e){
			System.out.println("GroupingLogger.prepareQueries - Problems preparing queries");
			e.printStackTrace();
		}
	}
	
	public void writeLog(int old_group_id, int new_group_id, int contact_id, int module_id, String message){
		//write file
//		try{
//			PrintWriter salida = new PrintWriter(new FileOutputStream(log_file, true));
//			salida.println("["+date_format.format(new Date())+"] ["+module_id+", "+contact_id+"] "+old_group_id+" -> "+new_group_id+" ("+message+")");
//			salida.close();
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
		//write db
		try{
			insert_log.setInt(1, old_group_id);
			insert_log.setInt(2, new_group_id);
			insert_log.setInt(3, contact_id);
			insert_log.setInt(4, module_id);
			if(message.length()>100){
				message = message.substring(0, 100);
			}
			insert_log.setString(5, message);
			insert_log.executeUpdate();
		}
		catch(Exception e){
			System.out.println("GroupingLogger.writeLog - Error in insert log");
			e.printStackTrace();
		}
		
	}
			
}




