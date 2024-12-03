import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class TermProject {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://192.168.56.101:4567/Club_management", "haejeong", "1234");
            Class.forName("com.mysql.cj.jdbc.Driver");

            Scanner scanner = new Scanner(System.in);

            // 역할 선택 메뉴
            while (true) {
                System.out.println("----- 역할을 선택해주세요 -----");
                System.out.println("1. 관리자");
                System.out.println("2. 동아리 임원");
                System.out.println("3. 동아리 부원");
                System.out.println("4. 종료");

                System.out.print("선택: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        AdminOp(conn);  // 관리자
                        break;
                    case 2:
                        PresidentOp(conn);  // 동아리 임원
                        break;
                    case 3:
                        MemberOp(conn);  // 동아리 부원
                        break;
                    case 4:
                        System.out.println("시스템을 종료합니다.");
                        conn.close();
                        scanner.close();
                        System.exit(0);  // 프로그램 종료
                    default:
                        System.out.println("다시 선택해주세요.");
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 관리자
    private static void AdminOp(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("관리자 고유번호를 입력하세요: ");
        int adminNo = scanner.nextInt();

        // 관리자 고유번호 확인
        try {
            String sql = "SELECT * FROM Admin WHERE AdminNo = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, adminNo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                System.out.println("관리자로 접속했습니다.");
                adminMenu(conn);  // 관리자 메뉴
            } else {
                System.out.println("유효하지 않은 관리자 고유번호입니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 관리자 메뉴
    private static void adminMenu(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("----- 관리자 메뉴 -----");
            System.out.println("1. 동아리 목록");
            System.out.println("2. 동아리 생성");
            System.out.println("3. 동아리 수정");
            System.out.println("4. 동아리 삭제");
            System.out.println("5. 종료");

            System.out.print("선택: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    viewClubList(conn);  // 동아리 목록 보기
                    break;
                case 2:
                    createClub(conn);  // 동아리 생성
                    break;
                case 3:
                    modifyClub(conn);  // 동아리 수정
                    break;
                case 4:
                    deleteClub(conn);  // 동아리 삭제
                    break;
                case 5:
                    System.out.println("관리자 메뉴를 종료합니다.");
                    return;
                default:
                    System.out.println("다시 선택해주세요.");
            }
        }
    }

    // 동아리 목록 보기
    private static void viewClubList(Connection conn) {
        try {
            String sql = "SELECT * FROM Club";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("----- 동아리 목록 -----");
            while (rs.next()) {
                System.out.println("동아리 ID: " + rs.getInt("ClubID"));
                System.out.println("동아리 이름: " + rs.getString("ClubName"));
                System.out.println("소개글: " + rs.getString("Introduction"));
                System.out.println("지도 교수: " + rs.getString("Advisor"));
                System.out.println("-------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 동아리 생성
    private static void createClub(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("동아리 이름: ");
        String clubName = scanner.nextLine();

        System.out.print("동아리 소개글: ");
        String introduction = scanner.nextLine();

        System.out.print("지도 교수: ");
        String advisor = scanner.nextLine();

        System.out.print("동아리 패스워드: ");
        String password = scanner.nextLine();

        try {
            String sql = "INSERT INTO Club (ClubName, Introduction, Advisor, Password) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, clubName);
            pstmt.setString(2, introduction);
            pstmt.setString(3, advisor);
            pstmt.setString(4, password);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("동아리 생성 완료.");
            } else {
                System.out.println("동아리 생성 실패.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 동아리 수정
    private static void modifyClub(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("수정할 동아리의 ID를 입력하세요: ");
        int clubID = scanner.nextInt();
        scanner.nextLine();  // 버퍼 비우기

        try {
            // 동아리 ID가 존재하는지 확인
            String checkSql = "SELECT * FROM Club WHERE ClubID = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, clubID);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // 동아리가 존재하면 수정
                System.out.println("동아리 정보 수정");

                System.out.print("동아리 이름: ");
                String clubName = scanner.nextLine();

                System.out.print("동아리 소개글: ");
                String introduction = scanner.nextLine();

                System.out.print("지도 교수명: ");
                String advisor = scanner.nextLine();

                System.out.print("동아리 패스워드: ");
                String password = scanner.nextLine();

                String updateSql = "UPDATE Club SET ClubName = ?, Introduction = ?, Advisor = ?, Password = ? WHERE ClubID = ?";
                PreparedStatement pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, clubName);
                pstmt.setString(2, introduction);
                pstmt.setString(3, advisor);
                pstmt.setString(4, password);
                pstmt.setInt(5, clubID);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("동아리 수정 완료.");
                } else {
                    System.out.println("동아리 수정 실패.");
                }
            } else {
                System.out.println("존재하지 않는 동아리 ID 입니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 동아리 삭제
    private static void deleteClub(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("삭제할 동아리 ID를 입력하세요: ");
        int clubID = scanner.nextInt();

        try {
            // 동아리 ID가 존재하는지 확인
            String checkSql = "SELECT * FROM Club WHERE ClubID = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, clubID);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // 동아리가 존재하면 삭제
                String deleteSql = "DELETE FROM Club WHERE ClubID = ?";
                PreparedStatement pstmt = conn.prepareStatement(deleteSql);
                pstmt.setInt(1, clubID);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("동아리 삭제 완료.");
                } else {
                    System.out.println("동아리 삭제 실패.");
                }
            } else {
                System.out.println("존재하지 않는 동아리 ID 입니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 동아리 임원
    private static void PresidentOp(Connection conn) {
    }


    // 동아리 부원
    private static void MemberOp(Connection conn) {
        // 동아리 부원 메뉴 구현
    }
}
