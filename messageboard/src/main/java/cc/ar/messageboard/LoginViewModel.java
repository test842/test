package cc.ar.messageboard;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import cc.ar.messageboard.user.UserBean;
import cc.ar.messageboard.user.UserService;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class LoginViewModel {
	@WireVariable
	private UserService userService;

	private UserBean userBean;

	public UserBean getUserBean() {
		return userBean;
	}
	
	public void setUserBean(UserBean userBean) {
		this.userBean = userBean;
	}
	
	String message;

	public String getMessage() {
		return message;
	}

	@Init 
	public void init() {
		userBean = new UserBean();
	}

	@Command
	@NotifyChange("message")
	public void login() { 
		if (userBean.getUid() == null || userBean.getPassword() == null) {
			message = "no empty";
		} else {
			UserBean user = userService.login(userBean);
			if (user != null) {
				Sessions.getCurrent().setAttribute("currentUser", user);
				Executions.sendRedirect("board.zul");
			} else {
				message = "incorrect userid or password";
			}
		}
	}	

}
