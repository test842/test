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
		if (info != null) {
			Integer uid = info.getUid();
			String password = info.getPassword();
			logger.debug("try to login, user id : {} ", uid);
			
			if (uid != null && password != null) {
				UserBean bean = userDao.select(uid);
				if (bean != null && bean.getPassword().equals(password)) {
					logger.info("user login,uid = {}", uid);
					return bean;
				}
				return null;
			}
			logger.error("user id or password is null");
			return null;
		}
		logger.error("login info is null");
		return null;
	}
	
	@Transactional(readOnly = true)
	public UserBean selectByUid(Integer uid) {
		logger.debug("select userbean by uid = {}", uid);
		if (uid != null)
			return userDao.select(uid);
		logger.error("uid is null");
		return null;
	}
}
