package cc.ar.messageboard.tagdetail;

import java.util.List;

public interface TagDetailDAO {

	boolean insert(TagDetailBean bean);
	boolean delete(TagDetailBean bean);
	List<TagDetailBean> selectByAid(Integer aid);
	TagDetailBean select(TagDetailBean bean);
}
