package cc.ar.messageboard.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope(value="singleton",proxyMode=ScopedProxyMode.TARGET_CLASS)
public class TagService {
	@Autowired
	private TagDAO tagDao;
	
	@Transactional(readOnly = true)
	public TagBean select(Integer tid){
		if (tid != null)
			return tagDao.select(tid);
		return null;
	}
	
	@Transactional(readOnly = true)
	public TagBean selectByTagName(String tagname){
		if (tagname != null)
			return tagDao.selectByTagName(tagname);
		return null;
	}
	
	@Transactional(propagation = Propagation.MANDATORY)
	public TagBean insert(String tagname){
		if (tagname != null)
			return tagDao.insert(tagname);
		return null;
	}

}
