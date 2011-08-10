package tcms.API;

import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.xmlrpc.XmlRpcException;

public class Session extends com.redhat.qe.xmlrpc.Session {

	// protected HttpState httpState = null;
	protected Integer userid;

	public Session(String userName, String password, URL url) {
		super(userName, password, url);
	}

	public Object login() throws XmlRpcException, GeneralSecurityException,
			IOException {
		return login("Auth.login", "login", userName, "password", password,
				"id");
	}

	public Object login(String loginMethod, String loginKey, String login,
			String passKey, String password, String returnKey)
			throws XmlRpcException, GeneralSecurityException, IOException {
		init();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put(loginKey, login);
		map.put(passKey, password);
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(map);

		HashMap<String, Object> hash = (HashMap<String, Object>) client
				.execute(loginMethod, params);
		this.userid = (Integer) hash.get(returnKey);
		return hash;

	}

	/**
	 * @return the userid
	 */
	public Integer getUserid() {
		return userid;
	}

	public void setUserid(Integer userid) {
		this.userid = userid;
	}

	public static void main(String... args) throws Exception {
		Session session = new Session("jweiss", "", new URL(
				"https://tcms.engineering.redhat.com/xmlrpc/"));
		session.login();
	}

}
