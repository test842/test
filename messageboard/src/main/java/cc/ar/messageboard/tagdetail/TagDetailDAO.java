package cc.ar.messageboard.tagdetail;

import java.util.List;

public interface TagDetailDAO {

	boolean insert(Integer aid,Integer tid);
	boolean delete(Integer aid,Integer tid);
	List<TagDetailBean> selectByAid(Integer aid);
	TagDetailBean select(Integer aid,Integer tid);
}
