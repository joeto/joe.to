package to.joe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;





public class BlockLogger implements Runnable // start
{
	public static LinkedBlockingQueue<BlockRow> bqueue = new LinkedBlockingQueue<BlockRow>();
	private boolean stop = false;
	private Connection conn;
	private int servnum;
	BlockLogger(Connection db, int sn) { stop = false; conn=db; servnum=sn;}
	public void stop() { stop = true; }
	public void run()
	{
		System.out.println("BlockLogger runnable started?\n");
		PreparedStatement ps = null;
		//Connection conn = null;
		BlockRow b;
		int delay = 10;
		
		while(!stop)
		{
			long start = System.currentTimeMillis()/1000L;
			int count = 0;
			try {
				ps = conn.prepareStatement("INSERT INTO blocks_? (date, player, replaced, type, x, y, z) VALUES (now(),?,?,?,?,?,?)");
				while(count < 100 && start+delay > (System.currentTimeMillis()/1000L))
				{
					/*if(count == 0)
						System.out.println("womg");*/
					
					b = bqueue.poll(1L, TimeUnit.SECONDS);

					if(b==null)
						continue;
					
					ps.setInt(1, servnum);
					ps.setString(2, b.name);
					ps.setInt(3, b.replaced);
					ps.setInt(4, b.type);
					ps.setInt(5, b.x);
					ps.setInt(6, b.y);
					ps.setInt(7, b.z);
					ps.addBatch();
					count++;
				}
				//if (debug && count > 0)
				//	lblog.info("Commiting " + count + " inserts.");
				ps.executeBatch();
			}catch(SQLException ex) {
	  	    	   System.out.println("SQLException: " + ex.getMessage());
	  	    	   System.out.println("SQLState: " + ex.getSQLState());
	  	    	   System.out.println("VendorError: " + ex.getErrorCode());
	  	    	   
	      	}catch(InterruptedException ev) {
	      		System.out.println("InterruptedException caused by bqueue.poll");
	      	}
		}
	}
	
	public void showBlockHistory(Player player, Block b)
	{
		player.sendMessage(ChatColor.BLUE + "Block history (" + b.getX() + ", " + b.getY() + ", " + b.getZ() + "): ");
		boolean hist = false;
		//Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Timestamp date;
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd hh:mm:ss");

		try {
			//conn = getConnection();
			//conn.setAutoCommit(false);
			System.out.println("At "+b.getX()+" "+b.getY()+" "+b.getZ());
			ps = this.conn.prepareStatement("SELECT * from blocks_? where y = ? and x = ? and z = ? order by date desc limit 10");
			ps.setInt(1, servnum);
			ps.setInt(2, b.getY());
			ps.setInt(3, b.getX());
			ps.setInt(4, b.getZ());
			rs = ps.executeQuery();
			while (rs.next())
			{
				date = rs.getTimestamp("date");
				String datestr = formatter.format(date);
				String msg = datestr + " " + rs.getString("player") + " ";
				if (rs.getInt("type") == 0)
					msg = msg + "destroyed " + Material.getMaterial(rs.getInt("replaced"));
				else if (rs.getInt("replaced") == 0)
				{
					if (rs.getInt("type") == 323) // sign
						msg = msg + "created " + rs.getString("extra");
					else
						msg = msg + "created " + Material.getMaterial(rs.getInt("type"));
				}
				else
					msg = msg + "replaced " + Material.getMaterial(rs.getInt("replaced")) + " with " + Material.getMaterial(rs.getInt("type"));
				player.sendMessage(ChatColor.GOLD + msg);
				hist = true;
			}
		} catch (SQLException ex) {
			//log.log(Level.SEVERE, name + " SQL exception", ex);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
//				if (conn != null)
//					conn.close();
			} catch (SQLException ex) {
				//log.log(Level.SEVERE, name + " SQL exception on close", ex);
			}
		}
		if (!hist)
			player.sendMessage(ChatColor.BLUE + "None.");
	}


}
