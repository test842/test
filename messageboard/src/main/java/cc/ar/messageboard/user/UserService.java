package cc.ar.messageboard.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);
	
	@Transactional(readOnly = true)
	public UserBean login(UserBean info) {
		if (info != null && info.getUid() != null && info.getPassword() != null) {
			logger.debug("try to login, user id : {} ", info.getUid());
			UserBean bean = userDao.select(info.getUid());
			if (bean != null && bean.getPassword().equals(info.getPassword())) 
				return bean;
			return null;
		}
		logger.debug(info == null ? "login info is null" : "user id or password is null");
		return null;
	}
	
	@Transactional(readOnly = true)
	public UserBean selectByUid(Integer uid) {
		logger.debug("select userbean by uid = {}", uid);
		if (uid != null)
			return userDao.select(uid);
		logger.debug("uid is null");
		return null;
	}
}
