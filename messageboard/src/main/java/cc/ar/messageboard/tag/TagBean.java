package cc.ar.messageboard.tag;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "TAG")
public class TagBean {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int tid;
	private String tagname;
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public String getTagname() {
		return tagname;
	}
	public void setTagname(String tagname) {
		this.tagname = tagname;
	}
	
	public TagBean() {
		
	}
	public TagBean(Integer tid, String tagname) {
		this.tid = tid;
		this.tagname = tagname;
	}
	
	@Override
	public String toString() {
		return tagname;
	}    
}
