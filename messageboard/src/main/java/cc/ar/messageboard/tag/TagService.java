package cc.ar.messageboard.tag;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(value="singleton",proxyMode=ScopedProxyMode.TARGET_CLASS)
public class TagService {
	@Autowired
	private TagDAO tagDao;
	
	private static final Logger logger = LoggerFactory.getLogger(TagService.class);
	
	@Transactional(readOnly = true)
	public TagBean select(Integer tid){
		logger.debug("select tagbean by tid = {}", tid);
		if (tid != null)
			return tagDao.select(tid);
		logger.error("tid is null");
		return null;
	}
	
	@Transactional(readOnly = true)
	public List<TagBean> select(){
		logger.debug("select all tagbeans");
		return tagDao.select();
	}
	
	@Transactional(readOnly = true)
	public TagBean selectByTagName(String tagname){
		logger.debug("select tagbean by tagname = {}", tagname);
		if (tagname != null)
			return tagDao.selectByTagName(tagname);
		logger.error("tagname is null");
		return null;
	}

}
