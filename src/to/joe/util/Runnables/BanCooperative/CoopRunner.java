package to.joe.util.Runnables.BanCooperative;

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

import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import to.joe.J2;
import to.joe.manager.BanCooperative;
import to.joe.util.Flag;
import to.joe.util.BanCooperative.BanCoopBan;
import to.joe.util.BanCooperative.BanCoopBanMCBans;
import to.joe.util.BanCooperative.BanCoopBanMCBouncer;
import to.joe.util.BanCooperative.BanCoopDossier;
import to.joe.util.BanCooperative.BanCoopType;

public abstract class CoopRunner implements Runnable {

    protected J2 j2;
    protected BanCooperative coop;

    protected String name;
    protected Player admin;

    private final String mcbans_host = "http://72.10.39.172/v2";
    private final String mcbouncer_host = "http://www.mcbouncer.com/api/";

    public CoopRunner(J2 j2, BanCooperative coop, String name) {
        this.j2 = j2;
        this.coop = coop;
        this.name = name;
    }

    @Override
    public abstract void run();

    private String makeUTF8(String toConvert) throws UnsupportedEncodingException {
        return URLEncoder.encode(toConvert, "UTF-8");
    }

    private String postVariable(HashMap<String, String> variables) {
        try {
            final StringBuilder stringBuilder = new StringBuilder();
            for (final Map.Entry<String, String> entry : variables.entrySet()) {
                if (stringBuilder.length() == 0) {
                    stringBuilder.append(this.makeUTF8(entry.getKey()) + "=" + this.makeUTF8(entry.getValue()));
                } else {
                    stringBuilder.append("&" + this.makeUTF8(entry.getKey()) + "=" + this.makeUTF8(entry.getValue()));
                }
            }
            return stringBuilder.toString();
        } catch (final UnsupportedEncodingException e) {
            return "";
        }
    }

    private JSONObject apiGet(String host, String path, HashMap<String, String> POSTData) {
        String preprocessed;
        try {
            final String POSTstring = this.postVariable(POSTData);
            final URL url = new URL((host + path).replace(" ", "%20"));
            final URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(15000);
            connection.setRequestProperty("User-agent", "meow");
            final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(POSTstring);
            writer.flush();
            final StringBuilder stringBuilder = new StringBuilder();
            final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            writer.close();
            reader.close();
            preprocessed = stringBuilder.toString();
        } catch (final Exception e) {
            this.j2.logWarn("Error communicating to API at " + host);
            preprocessed = "";
        }
        JSONObject result = null;
        try {
            result = new JSONObject(preprocessed);
        } catch (final JSONException e) {
        }
        return result;
    }

    @SuppressWarnings("rawtypes")
    protected HashMap<String, String> JSONToHashMap(JSONObject result) {
        final HashMap<String, String> output = new HashMap<String, String>();
        if (result != null) {
            final Iterator i = result.keys();
            if (i != null) {
                while (i.hasNext()) {
                    final String next = (String) i.next();
                    output.put(next, result.optString(next, ""));
                }
            }
        }
        return output;
    }

    protected void dox() {

        final EnumMap<BanCoopType, Integer> count = new EnumMap<BanCoopType, Integer>(BanCoopType.class);
        final EnumMap<BanCoopType, ArrayList<BanCoopBan>> allBans = new EnumMap<BanCoopType, ArrayList<BanCoopBan>>(BanCoopType.class);

        int sigCount = 0;
        final double mcbans_rep;
        if (!this.j2.config.bans_mcbans_api.equals("")) {
            final ArrayList<BanCoopBan> mcbans_bans = new ArrayList<BanCoopBan>();
            final HashMap<String, String> postVars = new HashMap<String, String>();
            postVars.put("player", this.name);
            postVars.put("admin", "BobTheCurious");
            postVars.put("exec", "playerLookup");
            final JSONObject mcbans_json = this.mcbans_api(postVars);
            if (mcbans_json == null) {
                this.j2.logWarn("MCBans is DOWN");
                return;
            }
            try {
                final JSONArray local = mcbans_json.optJSONArray("local");
                for (int i = 0; i < local.length(); i++) {
                    mcbans_bans.add(new BanCoopBanMCBans(local.getString(i), "l"));
                }
                final JSONArray global = mcbans_json.optJSONArray("global");
                for (int i = 0; i < global.length(); i++) {
                    mcbans_bans.add(new BanCoopBanMCBans(global.getString(i), "g"));
                    sigCount++;
                }
            } catch (final JSONException e) {
                e.printStackTrace();
            }
            mcbans_rep = mcbans_json.optDouble("reputation", 10.0);
            int mcbans_count = mcbans_json.optInt("total", 0);
            if (mcbans_count < mcbans_bans.size()) {
                mcbans_count = mcbans_bans.size();
            }
            count.put(BanCoopType.MCBANS, mcbans_count);
            allBans.put(BanCoopType.MCBANS, mcbans_bans);
        } else {
            mcbans_rep = 0;
        }
        if (!this.j2.config.bans_mcbouncer_api.equals("")) {
            final JSONObject mcbouncer = this.mcbouncer_getBans(this.name);
            final ArrayList<BanCoopBan> mcbouncer_bans = new ArrayList<BanCoopBan>();
            int mcbouncer_count = mcbouncer.optInt("totalcount", 0);
            if (this.mcbouncer_success(mcbouncer)) {
                final JSONArray daBans = (JSONArray) mcbouncer.opt("data");
                if (daBans.length() > mcbouncer_count) {
                    mcbouncer_count = daBans.length();
                }
                for (int x = 0; x < daBans.length(); x++) {
                    final JSONObject ban = (JSONObject) daBans.opt(x);
                    mcbouncer_bans.add(new BanCoopBanMCBouncer(String.valueOf(ban.optString("server", "")), String.valueOf(ban.optString("reason", ""))));
                }
            }
            count.put(BanCoopType.MCBOUNCER, mcbouncer_count);
            sigCount += mcbouncer_count;
            allBans.put(BanCoopType.MCBOUNCER, mcbouncer_bans);
        }
        this.coop.record.put(this.name, new BanCoopDossier(this.name, count, sigCount, allBans, mcbans_rep));
    }

    protected HashMap<String, String> mcbans_user_connect(String name, String ip) {
        if(j2.hasFlag(name, Flag.ADMIN)){
            return null;
        }
        final HashMap<String, String> postVars = new HashMap<String, String>();
        postVars.put("player", name.toLowerCase());
        postVars.put("playerip", ip);
        postVars.put("exec", "playerConnect");
        return this.JSONToHashMap(this.mcbans_api(postVars));
    }

    protected JSONObject mcbans_api(HashMap<String, String> postVars) {
        return this.apiGet(this.mcbans_host, "/" + this.j2.config.bans_mcbans_api, postVars);
    }

    private JSONObject mcbouncer_getBans(String name) {
        return this.mcbouncer_api("getBans", name + "/0/100");
    }
    
    private boolean mcbouncer_success(JSONObject result) {
        return result.optBoolean("success");
    }

    protected JSONObject mcbouncer_api(String action, String parameters) {
        return this.apiGet(this.mcbouncer_host, action + "/" + this.j2.config.bans_mcbouncer_api + "/" + parameters, new HashMap<String, String>());
    }

}
