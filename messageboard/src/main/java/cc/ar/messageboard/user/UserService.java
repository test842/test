package cc.ar.messageboard.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(value="singleton",proxyMode=ScopedProxyMode.TARGET_CLASS)
public class UserService {
	@Autowired
	private UserDAO userDao;
	
	@Transactional(readOnly = true)
	public UserBean login(UserBean info) {
		if (info != null && info.getUid() != null && info.getPassword() != null) {
			UserBean bean = userDao.select(info.getUid());
			if (bean != null && bean.getPassword().equals(info.getPassword()))
				return bean;
		}
		return null;
	}
}
