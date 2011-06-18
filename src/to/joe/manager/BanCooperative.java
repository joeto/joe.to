package to.joe.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import to.joe.J2;
import to.joe.util.Flag;
import to.joe.util.BanCooperative.BanCoopBan;
import to.joe.util.BanCooperative.BanCoopBanMCBans;
import to.joe.util.BanCooperative.BanCoopBanMCBouncer;
import to.joe.util.BanCooperative.BanCoopDossier;
import to.joe.util.BanCooperative.BanCoopType;

public class BanCooperative {
	private J2 j2;
	private String mcbans_version="3.0.1.3.3.7";
	private String mcbans_host="http://72.10.39.172";
	private String mcbouncer_host="http://www.mcbouncer.com/api/";
	private HashMap<String,BanCoopDossier> record;
	public BanCooperative(J2 j2){
		this.j2=j2;
		this.record=new HashMap<String,BanCoopDossier>();
	}

	public void processJoin(Player player){
		String name=player.getName();
		HashMap<String,String> mcbans=j2.banCoop.mcbans_user_connect(name);
		this.dox(name);
		String mcbans_disputes=mcbans.get("disputes");
		String is_mcbans_mod=mcbans.get("is_mcbans_mod");
		BanCoopDossier dox=this.record.get("name");
		if(dox.totalBans()>0){
			j2.chat.msgByFlag(Flag.ADMIN, ChatColor.LIGHT_PURPLE+"Player "+ChatColor.WHITE+name+ChatColor.LIGHT_PURPLE+" has "+ChatColor.WHITE+dox.totalBans()+ChatColor.LIGHT_PURPLE+" bans. MCBans rep "+ChatColor.WHITE+dox.getMCBansRep()+ChatColor.LIGHT_PURPLE+"/10");
			j2.chat.msgByFlag(Flag.ADMIN, ChatColor.LIGHT_PURPLE+"To see the bans: /lookup "+ChatColor.WHITE+name);
			if(!j2.hasFlag(name, Flag.QUIETERJOIN)){
				j2.irc.ircAdminMsg("[BANS] "+name+": Bans: "+dox.totalBans()+". MCBans Rep "+dox.getMCBansRep()+"/10");
			}
		}
		if(is_mcbans_mod.equals("y")){
			j2.chat.msgByFlag(Flag.ADMIN, ChatColor.RED+"Note to admins: "+name+" is an mcbans.com staffer");
			j2.irc.ircAdminMsg("[MCBANS] "+name+" is an mcbans.com staffer");
			j2.log(ChatColor.RED+"[MCBANS] "+name+" is an mcbans.com staffer");
			player.sendMessage(ChatColor.GREEN+"MCBANS staff: You have "+ChatColor.AQUA+mcbans_disputes+ChatColor.GREEN+" disputes");
		}
		else if(j2.hasFlag(name,Flag.SRSTAFF)&&(Integer.parseInt(mcbans_disputes)>0)){
			player.sendMessage(ChatColor.GREEN+"[MCBANS] We have "+ChatColor.AQUA+mcbans_disputes+ChatColor.GREEN+" active disputes");
		}
	}



	private String makeUTF8(String toConvert) throws UnsupportedEncodingException{
		return URLEncoder.encode(toConvert, "UTF-8");
	}

	private String postVariable(HashMap<String, String> variables) {
		try {
			StringBuilder stringBuilder=new StringBuilder();
			for(Map.Entry<String,String> entry:variables.entrySet()) {
				if(stringBuilder.length()==0){
					stringBuilder.append(makeUTF8(entry.getKey())+"="+makeUTF8(entry.getValue()));
				}
				else{
					stringBuilder.append("&"+makeUTF8(entry.getKey())+"="+makeUTF8(entry.getValue()));
				}
			}
			return stringBuilder.toString();
		}
		catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	private JSONObject apiGet(String host,String path,HashMap<String, String> POSTData) {
		String preprocessed;
		try {
			String POSTstring=this.postVariable(POSTData);
			URL url=new URL(host+path);
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			connection.setConnectTimeout(8000);
			connection.setReadTimeout(15000);
			connection.setRequestProperty("User-agent","meow");
			OutputStreamWriter writer=new OutputStreamWriter(connection.getOutputStream());
			writer.write(POSTstring);
			writer.flush();
			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while((line=reader.readLine())!=null){
				stringBuilder.append(line);
			}
			writer.close();
			reader.close();
			preprocessed=stringBuilder.toString();
		} catch (Exception e) {
			j2.logWarn("Error communicating to API at "+host);
			preprocessed="";
		}
		JSONObject result=null;
		try {
			result=new JSONObject(preprocessed);
		} catch (JSONException e) {
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, String> JSONToHashMap(JSONObject result){
		HashMap<String,String> output=new HashMap<String,String>();
		if (result != null)
		{
			Iterator i=result.keys();
			if(i!=null){
				while(i.hasNext()){
					String next=(String)i.next();
					output.put(next, result.optString(next,""));
				}
			}
		}
		return output;
	}

	public void processUnban(String name){
		this.mcbans_unban(name);
	}

	public void processBan(String name, String Sender, String Reason){
		this.mcbans_ban(name, Sender, Reason);
	}

	public void lookup(String name, Player player){
		if(!this.record.containsKey(name)){
			this.dox(name);
		}
		BanCoopDossier dossier= this.record.get(name);
		for(String line:dossier.full()){
			player.sendMessage(line);
		}
	}
	
	private void dox(String name){
		
		EnumMap<BanCoopType, Integer> count=new EnumMap<BanCoopType,Integer>(BanCoopType.class);
		EnumMap<BanCoopType, ArrayList<BanCoopBan>> allBans=new EnumMap<BanCoopType, ArrayList<BanCoopBan>>(BanCoopType.class);
		
		
		ArrayList<BanCoopBan> mcbans_bans=new ArrayList<BanCoopBan>();
		HashMap<String,String> postVars = new HashMap<String,String>();
		postVars.put("player", name);
		postVars.put("exec", "lookup_user");
		JSONObject mcbans_json = this.mcbans_api(postVars);
		try {
			JSONArray local=mcbans_json.optJSONArray("ban_reasons_local");
			for (int i=0; i<local.length(); i++) {
				mcbans_bans.add(new BanCoopBanMCBans(local.getString(i),"l"));
			}
			JSONArray global=mcbans_json.optJSONArray("ban_reasons_global");
			for (int i=0; i<global.length(); i++) {
				mcbans_bans.add(new BanCoopBanMCBans(global.getString(i),"g"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		double mcbans_rep=mcbans_json.optDouble("ban_rep",10.0);
		int mcbans_count=mcbans_json.optInt("ban_num",0);
		if(mcbans_count<mcbans_bans.size()){
			mcbans_count=mcbans_bans.size();
		}
		count.put(BanCoopType.MCBANS, mcbans_count);
		allBans.put(BanCoopType.MCBANS, mcbans_bans);
		
		JSONObject mcbouncer=this.mcbouncer_getBans(name);
		ArrayList<BanCoopBan> mcbouncer_bans=new ArrayList<BanCoopBan>();
		int mcbouncer_count=mcbouncer.optInt("totalcount",0);
		if(this.mcbouncer_success(mcbouncer)){
			JSONArray daBans=(JSONArray) mcbouncer.opt("data");
			if(daBans.length()>mcbouncer_count){
				mcbouncer_count=daBans.length();
			}
			for(int x=0;x<daBans.length();x++){
				JSONObject ban= (JSONObject) daBans.opt(x);
				mcbouncer_bans.add(new BanCoopBanMCBouncer(String.valueOf(ban.optString("server","")),String.valueOf(ban.optString("reason",""))));
			}
		}
		count.put(BanCoopType.MCBOUNCER, mcbouncer_count);
		allBans.put(BanCoopType.MCBOUNCER, mcbouncer_bans);
		
		this.record.put(name, new BanCoopDossier(name, count, allBans, mcbans_rep));
	}
	
	private void mcbans_unban(String name){
		HashMap<String,String> postVars = new HashMap<String,String>();
		postVars.put("player", name);
		postVars.put("exec", "unban_user");
		HashMap<String,String> result = JSONToHashMap(this.mcbans_api(postVars));
		if ((result.get("result")).equalsIgnoreCase("y")){
			j2.log(ChatColor.RED+"[mcbans] Unbanned "+name);
		}
		else{
			j2.log(ChatColor.RED+"[mcbans] Failed to unban "+name);
		}
	}

	private void mcbans_ban(String name, String admin, String reason) {
		String banType="ban_local_user";
		String reason_lower=reason.toLowerCase();
		if(admin.toLowerCase().contains("vigilant")||((reason_lower.contains("grief"))
				&&!(reason_lower.contains("fuck")||reason_lower.contains("shit")||reason_lower.contains("bitch")))){
			banType="ban_user";
		}
		if(admin.toLowerCase().contains("bob")){
			admin="mbaxter";
		}
		HashMap<String, String> postVars = new HashMap<String, String>();
		postVars.put("player", name);
		postVars.put("admin", admin);
		postVars.put("reason", reason);
		String ip = j2.mysql.IPGetLast(name);
		postVars.put("playerip", ip);
		postVars.put("duration", "0");
		postVars.put("exec", banType);
		HashMap<String, String> result = JSONToHashMap(this.mcbans_api(postVars));
		if (result.get("result").equalsIgnoreCase("y")) {
			j2.log(ChatColor.RED+"[mcbans] Added "+name);
		}
		else if (result.get("result").equalsIgnoreCase("a")) {
			j2.log(ChatColor.RED+"[mcbans] Player "+name+" already on list");
		} else if (result.get("result").equalsIgnoreCase("n")) {
			j2.log(ChatColor.RED+"[mcbans] Could not add "+name);
		}
	}

	public HashMap<String,String> mcbans_user_connect(String name) {
		HashMap<String, String> postVars=new HashMap<String, String>();
		postVars.put("player", name.toLowerCase());
		postVars.put("version", mcbans_version);
		postVars.put("exec", "user_connect");
		return JSONToHashMap(this.mcbans_api(postVars));
	}
	private JSONObject mcbans_api(HashMap<String, String> postVars) {
		return apiGet(this.mcbans_host,"/"+j2.mcbansapi,postVars);
	}

	
	private JSONObject mcbouncer_getBans(String name){
		return this.mcbouncer_api("getBans", name+"/0/100");
	}

	@SuppressWarnings("unused")
	private JSONObject mcbouncer_getIPBans(String ip){
		return this.mcbouncer_api("getIPBans", ip+"/0/100");
	}

	private boolean mcbouncer_success(JSONObject result){
		return result.optBoolean("success");
	}

	private JSONObject mcbouncer_api(String action, String parameters){
		return this.apiGet(this.mcbouncer_host, action+"/"+this.j2.mcbouncerapi+"/"+parameters, new HashMap<String,String>());
	}
}
