package cc.ar.messageboard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger logger = LoggerFactory.getLogger(LoginViewModel.class);
	
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
		logger.debug("login on click button");
		if (userBean.getUid() == null || userBean.getPassword() == null) {
			logger.debug("uid or password is null");
			message = "no empty";
		} else {
			UserBean user = userService.login(userBean);
			if (user != null) {
				logger.debug("set current user in session scope");
				Sessions.getCurrent().setAttribute("currentUser", user);
				Executions.sendRedirect("board.zul");
			} else {
				message = "incorrect userid or password";
			}
		}
	}	

}
