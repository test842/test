package cc.ar.messageboard.tagdetail;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger logger = LoggerFactory.getLogger(TagDetailService.class);
	
	@Transactional(propagation = Propagation.MANDATORY)
	public boolean insert(Integer aid, Integer tid) {
		logger.debug("insert tag, tid = {} for aid = {}", tid, aid);
		if (aid != null && tid != null) 
			return tagDetailDao.insert(aid, tid);
		logger.debug("tid or aid is null");
		return false;
	}
	
	@Transactional(propagation = Propagation.MANDATORY)
	public boolean delete(Integer aid, Integer tid) {
		logger.debug("delete tag, tid = {} for aid = {}", tid, aid);
		if (aid != null && tid != null) {
			return tagDetailDao.delete(aid, tid);
		}
		logger.debug("tid or aid is null");
		return false;
	}
	
	@Transactional(readOnly = true)
	public List<TagDetailBean> selectByAid(Integer aid) {
		logger.debug("select tags of article, aid = {}", aid);
		if (aid != null) {
			return tagDetailDao.selectByAid(aid);
		}
		logger.debug("aid is null");
		return null;
	}
}
