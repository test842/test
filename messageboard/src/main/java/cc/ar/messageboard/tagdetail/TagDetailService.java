package cc.ar.messageboard.tagdetail;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(value="singleton",proxyMode=ScopedProxyMode.TARGET_CLASS)
public class TagDetailService {
	@Autowired
	private TagDetailDAO tagDetailDao;
	
	@Transactional(propagation = Propagation.MANDATORY)
	public boolean insert(Integer aid, Integer tid) {
		if (aid != null && tid != null) {
			return tagDetailDao.insert(aid, tid);
		}
		return false;
	}
	
	@Transactional(propagation = Propagation.MANDATORY)
	public boolean delete(Integer aid, Integer tid) {
		if (aid != null && tid != null) {
			return tagDetailDao.delete(aid, tid);
		}
		return false;
	}
	
	@Transactional(readOnly = true)
	public List<TagDetailBean> selectByAid(Integer aid) {
		if (aid != null) {
			return tagDetailDao.selectByAid(aid);
		}
		return null;
	}
	
	@Transactional(readOnly = true)
	public TagDetailBean select(Integer aid, Integer tid) {
		if (aid != null && tid != null) {
			return tagDetailDao.select(aid, tid);
		}
		return null;
	}
}
