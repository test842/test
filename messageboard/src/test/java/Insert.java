import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Insert {

	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:9001", "sa", "");
			
			String insertMember = "insert into article(title,content,ref,uid) values (?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(insertMember);
			for (int i = 1; i <= 20; i++) {
				pstmt.setString(1, String.valueOf(i));
				pstmt.setString(2, String.valueOf(i));
				
				if (i % 4 != 1) {
					int ref = (int) (Math.random() * i + 1);
					pstmt.setInt(3, ref != i ? ref : 0);
				}
				else
					pstmt.setInt(3, 0);
				pstmt.setInt(4, (int) (Math.random() * 5 + 1));
				pstmt.addBatch();
			}
			int[] articles = pstmt.executeBatch();
			System.out.println("articles=" + articles.length);
			
			String insertTag = "insert into tagdetail(aid,tid) values (?,?)";
			pstmt = conn.prepareStatement(insertTag);
			for (int i = 1; i <= 20; i++) 
				for (int j = 0; j < 5; j++) {
					if (Math.random() > 0.5)
						continue;
					pstmt.setInt(1, i);
					pstmt.setInt(2, j);
					pstmt.addBatch();
				}
			int[] tags = pstmt.executeBatch();
			System.out.println("tags=" + tags.length);


		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
