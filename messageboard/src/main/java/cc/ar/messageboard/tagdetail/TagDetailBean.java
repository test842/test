package cc.ar.messageboard.tagdetail;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "TAGDETAIL")
public class TagDetailBean {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int tdid;
	
	private int aid;
	
	private int tid;

	public int getTdid() {
		return tdid;
	}

	public void setTdid(int tdid) {
		this.tdid = tdid;
	}

	public int getAid() {
		return aid;
	}

	public void setAid(int aid) {
		this.aid = aid;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}
	
	
	
}
