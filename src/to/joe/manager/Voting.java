package to.joe.manager;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import to.joe.J2;
import to.joe.util.Flag;

public class Voting {
	private J2 j2;
	private String[] currentOptions;
	private String currentQuestion;
	private boolean voteInProgress;
	private HashMap<String,Integer> votes;
	
	public Voting(J2 j2){
		this.j2=j2;
	}
	
	/**
	 * Called when a player does /voteadmin
	 * Player is already known to be an admin if this fires.
	 * @param player
	 * @param args
	 */
	public void voteAdminCommand(Player player, String[] args){
		String name=player.getName();
		if(args.length<1){
			player.sendMessage(ChatColor.RED+"Usage: /voteadmin start|cancel|stop|confirm");
			return ;
		}
		else{ 
			if(args.length>=3&&args[0].equalsIgnoreCase("start")){
				if(this.voteInProgress){
					player.sendMessage(ChatColor.RED+"Vote already in progress");
				}
				String combined=this.j2.combineSplit(1,args," ");
				if(!combined.startsWith("\"")||!combined.endsWith("\"")){
					this.usageVoteAdmin(player);
					return;
				}

				combined=combined.substring(1,combined.length()-2);
				String[] bits=combined.split("\" \"");

				if(bits.length>6){
					player.sendMessage(ChatColor.RED+"Too many options");
					return;
				}
				if(bits.length<2){
					String q=bits[0];
					bits=new String[3];
					bits[0]=q;
					bits[1]="Yes";
					bits[2]="No";
				}
				this.voteInProgress=true;
				this.currentQuestion=bits[0];
				this.j2.chat.muteAll=true;
				this.j2.chat.messageByFlag(Flag.ADMIN,ChatColor.AQUA + name + " has started a vote.");
				this.j2.chat.messageByFlagless(Flag.ADMIN,ChatColor.AQUA + "Admin has started a vote.");
				this.j2.chat.messageAll(ChatColor.AQUA + "Question: " + bits[0]);
				this.currentOptions=new String[bits.length-1];
				for(int x=1;x<bits.length;x++){
					this.currentOptions[x-1]=bits[x];
					this.j2.chat.messageAll(ChatColor.DARK_AQUA.toString() + x + ". " + bits[x]);
				}
				this.j2.chat.messageAll(ChatColor.AQUA+"Say "+ChatColor.DARK_AQUA+"/vote x"+ChatColor.AQUA+" where x is the question #");
				//Run the run() method of VoteTally in 30 seconds
				this.j2.getServer().getScheduler().scheduleAsyncDelayedTask(j2, new VoteTally(this.j2), 30000L);
			}
		}
	}
	/**
	 * Tell player the usage.
	 * @param player
	 */
	public void usageVoteAdmin(Player player){
		player.sendMessage(ChatColor.RED+"Usage: /voteadmin start|cancel|stop|confirm");
	}
	
	/**
	 * Called when a player says /vote
	 * Record to this.votes
	 * String = playername
	 * Integer = option they voted for
	 * @param player
	 * @param args
	 */
	public void voteCommand(Player player, String[] args){
		
	}
	
	/**
	 * @return the current question
	 */
	public String getCurrentQuestion(){
		return this.currentQuestion;
	}
	
	/**
	 * @return The current options
	 */
	public String[] getCurrentOptions(){
		return this.currentOptions;
	}
	
	/**
	 * @return the current votes.
	 */
	public HashMap<String,Integer> getVotes(){
		return this.votes;
	}
	
	/**
	 * vote done! :)
	 */
	public void cleanUp(){
		this.voteInProgress=false;
	}
	
	private class VoteTally implements Runnable{
		private J2 j2;
		private Voting voting;
		public VoteTally(J2 j2){
			this.j2=j2;
			this.voting=j2.voting;
		}
		@Override
		public void run() {
			// In here goes tallying. 
			//Access via this.voting.getCurrentQuestion() and this.voting.getCurrentOptions()
			//and this.voting.getVotes()
			//In here is where you announce winners
			
			j2.chat.messageAll("A WINNAR IS OPTION X");
			//Lastly, vote is no longer in progress
			this.voting.cleanUp();
		}
		
	}
}
