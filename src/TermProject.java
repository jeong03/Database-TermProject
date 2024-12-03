import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TermProject {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.56.101:4567/Club_management", "haejeong", "1234");
            Class.forName("com.mysql.cj.jdbc.Driver");


            // 연결 종료
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
