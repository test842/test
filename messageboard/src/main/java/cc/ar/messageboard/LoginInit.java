package cc.ar.messageboard;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Initiator;

import cc.ar.messageboard.user.UserBean;

public class LoginInit implements Initiator {

	public void doInit(Page page, Map<String, Object> args) throws Exception {
		Session session = Sessions.getCurrent();
		UserBean bean = (UserBean) session.getAttribute("currentUser");
		if (bean == null) {
			Executions.sendRedirect("login.zul");
			return;
		}
	}

}
