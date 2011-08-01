package to.joe.util.Runnables.BanCooperative;

import java.util.HashMap;

import to.joe.J2;
import to.joe.manager.BanCooperative;

public class CoopRunnerDisconnect extends CoopRunner{
	
	public CoopRunnerDisconnect(J2 j2, BanCooperative coop, String name) {
		super(j2, coop, name);
	}
	
	private void mcbans_disconnect() {
		HashMap<String,String> postVars = new HashMap<String,String>();
		postVars.put("player", name);
		postVars.put("exec", "playerDisconnect");
		this.mcbans_api(postVars);
	}

	@Override
	public void run() {
		this.mcbans_disconnect();
	}
}
