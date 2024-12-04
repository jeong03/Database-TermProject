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
                System.out.println("\n------- 역할 선택 -------");
                System.out.println("1. 관리자");
                System.out.println("2. 동아리 임원");
                System.out.println("3. 동아리 부원");
                System.out.println("4. 종료");

                System.out.print("선택: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1: // 관리자
                        AdminOp(conn);
                        break;
                    case 2: // 동아리 임원
                        PresidentOp(conn);
                        break;
                    case 3: // 동아리 부원
                        MemberOp(conn);
                        break;
                    case 4: // 종료
                        System.out.println("시스템 종료");
                        conn.close();
                        scanner.close();
                        System.exit(0);
                    default:
                        System.out.println("잘못된 선택입니다. 다시 선택해주세요.");
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 관리자
    private static void AdminOp(Connection conn) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("관리자 고유번호 입력: ");
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
            System.out.println("\n------- 관리자 메뉴 -------");
            System.out.println("1. 동아리 목록");
            System.out.println("2. 동아리 생성");
            System.out.println("3. 동아리 수정");
            System.out.println("4. 동아리 삭제");
            System.out.println("5. 동아리별 활동 보고서 목록");
            System.out.println("6. 동아리별 부원 목록");
            System.out.println("7. 종료");

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
                    viewActivityReportList(conn);  // 동아리별 활동 보고서 목록 보기
                    break;
                case 6:
                    viewMemberList(conn);  // 동아리별 부원 목록 보기
                    break;
                case 7:
                    System.out.println("관리자 메뉴를 종료합니다.");
                    return;
                default:
                    System.out.println("잘못된 선택입니다. 다시 선택해주세요.");
            }
        }
    }

    // 동아리 목록 보기
    private static void viewClubList(Connection conn) {
        try {
            String sql = "SELECT * FROM Club";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n------- 동아리 목록 -------");
            while (rs.next()) {
                System.out.println("동아리 ID: " + rs.getInt("ClubID"));
                System.out.println("동아리 이름: " + rs.getString("ClubName"));
                System.out.println("소개글: " + rs.getString("Introduction"));
                System.out.println("지도 교수: " + rs.getString("Advisor"));
                System.out.println("------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 동아리 생성
    private static void createClub(Connection conn) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n동아리 생성하기");

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

        System.out.println("\n동아리 정보 수정하기");
        System.out.print("수정할 동아리의 ID를 입력하세요.: ");
        int clubID = scanner.nextInt();
        scanner.nextLine();  // 버퍼 비우기

        try {
            // 동아리 ID가 존재하는지 확인
            String checkSql = "SELECT * FROM Club WHERE ClubID = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, clubID);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
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
        System.out.println("\n동아리 삭제하기");

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

    // 동아리별 활동 보고서 목록 보기
    private static void viewActivityReportList(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\n활동 보고서 목록을 조회할 동아리 ID를 입력하세요: ");
        int clubID = scanner.nextInt();

        try {
            String sql = "SELECT * FROM ActivityReport WHERE ClubID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, clubID);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n------- 활동 보고서 목록 -------");
            while (rs.next()) {
                System.out.println("활동 ID: " + rs.getInt("ActivityID"));
                System.out.println("활동 일시: " + rs.getString("ActivityDate"));
                System.out.println("활동 내용: " + rs.getString("ActivityContent"));
                System.out.println("------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 동아리별 부원 목록
    private static void viewMemberList(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n");
        System.out.print("부원 목록을 조회할 동아리 ID를 입력하세요: ");
        int clubID = scanner.nextInt();

        try {
            String sql = "SELECT * FROM Members WHERE ClubID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, clubID);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n------- 부원 목록 -------");
            while (rs.next()) {
                System.out.println("학번: " + rs.getString("StudentID"));
                System.out.println("이름: " + rs.getString("Name"));
                System.out.println("------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 동아리 임원
    private static void PresidentOp(Connection conn) {
        Scanner scanner = new Scanner(System.in);
        try {
            // 동아리 목록 가져오기
            String sql = "SELECT ClubID, ClubName FROM Club";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            // 동아리 목록 출력
            System.out.println("\n------- 동아리 목록 -------");
            while (rs.next()) {
                int clubID = rs.getInt("ClubID");
                String clubName = rs.getString("ClubName");
                System.out.println("동아리 ID: " + clubID + ", 동아리 이름: " + clubName);
                System.out.println("-------------------------");
            }

            // 동아리 ID 입력
            System.out.print("소속 동아리 ID 입력: ");
            int clubID = scanner.nextInt();
            scanner.nextLine();  // 버퍼 비우기

            System.out.print("동아리 패스워드 입력: ");
            String password = scanner.nextLine();

            // 동아리 ID와 패스워드 확인
            String checkSql = "SELECT * FROM Club WHERE ClubID = ? AND Password = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, clubID);
            checkStmt.setString(2, password);
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next()) {
                // 동아리 이름 표시
                String clubName = checkRs.getString("ClubName");
                System.out.println(clubName + " 동아리 임원으로 접속했습니다.");
                presidentMenu(conn, clubID, clubName);  // 동아리 임원 메뉴
            } else {
                System.out.println("유효하지 않은 동아리 ID 또는 패스워드입니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 동아리 임원 메뉴
    private static void presidentMenu(Connection conn, int clubID, String clubName) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n\n------- " + clubName + " 동아리 임원 메뉴 -------");
            System.out.println("1. 동아리 목록");
            System.out.println("2. 동아리 수정");
            System.out.println("3. 활동 보고서 목록");
            System.out.println("4. 활동 보고서 작성");
            System.out.println("5. 부원 관리");
            System.out.println("6. 종료");

            System.out.print("선택: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    viewClubList(conn);  // 동아리 목록
                    break;
                case 2:
                    modifyClubForPresident(conn, clubID, clubName);  // 동아리 수정
                    break;
                case 3:
                    viewActivityReportList(conn, clubID);  // 활동 보고서 목록
                    break;
                case 4:
                    createActivityReport(conn, clubID);  // 활동 보고서 작성
                    break;
                case 5:
                    manageMembers(conn, clubID);  // 부원 관리
                    break;
                case 6:
                    System.out.println("동아리 임원 메뉴 종료.");
                    return;
                default:
                    System.out.println("잘못된 선택입니다. 다시 선택해주세요.");
            }
        }
    }

    private static void modifyClubForPresident(Connection conn, int clubID, String clubName) {
        Scanner scanner = new Scanner(System.in);

        try {
            String checkSql = "SELECT * FROM Club WHERE ClubID = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, clubID);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {

                System.out.println("\n동아리 정보 수정");

                System.out.print("동아리 이름: ");
                String clubNameInput = scanner.nextLine();

                System.out.print("동아리 소개글: ");
                String introduction = scanner.nextLine();

                System.out.print("지도 교수명: ");
                String advisor = scanner.nextLine();

                System.out.print("동아리 패스워드: ");
                String password = scanner.nextLine();

                String updateSql = "UPDATE Club SET ClubName = ?, Introduction = ?, Advisor = ?, Password = ? WHERE ClubID = ?";
                PreparedStatement pstmt = conn.prepareStatement(updateSql);
                pstmt.setString(1, clubNameInput);
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 활동 보고서 목록
    private static void viewActivityReportList(Connection conn, int clubID) {
        try {
            String sql = "SELECT * FROM ActivityReport WHERE ClubID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, clubID);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n------- 활동 보고서 목록 -------");
            while (rs.next()) {
                System.out.println("활동 ID: " + rs.getInt("ActivityID"));
                System.out.println("활동 일시: " + rs.getString("ActivityDate"));
                System.out.println("활동 내용: " + rs.getString("ActivityContent"));
                System.out.println("-----------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 활동 보고서 작성
    private static void createActivityReport(Connection conn, int clubID) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\n활동 보고서 작성하기\n");
        System.out.print("활동 일시: ");
        String date = scanner.nextLine();

        System.out.print("활동 내용: ");
        String content = scanner.nextLine();

        try {
            String sql = "INSERT INTO ActivityReport (ClubID, ActivityDate, ActivityContent) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, clubID);
            pstmt.setString(2, date);
            pstmt.setString(3, content);

            pstmt.executeUpdate();
            System.out.println("활동 보고서 작성 완료.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 부원 관리 메뉴
    private static void manageMembers(Connection conn, int clubID) {
        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println("\n------- 부원 관리 메뉴 -------");
            System.out.println("1. 부원 목록 보기");
            System.out.println("2. 부원 추가");
            System.out.println("3. 부원 삭제");
            System.out.println("4. 뒤로가기");

            System.out.print("선택: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewMemberList(conn, clubID);  // 부원 목록
                    break;
                case 2:
                    addMember(conn, clubID);  // 부원 추가
                    break;
                case 3:
                    removeMember(conn, clubID);  // 부원 삭제
                    break;
                case 4:
                    return;
                default:
                    System.out.println("다시 선택해주세요.");
            }
        }
    }

    // 부원 목록
    private static void viewMemberList(Connection conn, int clubID) {
        try {
            String sql = "SELECT * FROM Members WHERE ClubID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, clubID);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n------- 부원 목록 -------");
            while (rs.next()) {
                System.out.println("학번: " + rs.getString("StudentID"));
                System.out.println("이름: " + rs.getString("Name"));
                System.out.println("-----------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 부원 추가 (임원은 자신의 동아리만 추가 가능)
    private static void addMember(Connection conn, int clubID) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n부원 추가히기");
        System.out.print("추가할 부원의 학번: ");
        String studentID = scanner.nextLine();

        System.out.print("추가할 부원의 이름: ");
        String name = scanner.nextLine();

        try {
            String sql = "INSERT INTO Members (ClubID, StudentID, Name) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, clubID);
            pstmt.setString(2, studentID);
            pstmt.setString(3, name);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("부원 추가 완료.");
            } else {
                System.out.println("부원 추가 실패.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 부원 삭제
    private static void removeMember(Connection conn, int clubID) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n부원 삭제하기");
        System.out.print("삭제할 부원의 학번: ");
        String studentID = scanner.nextLine();

        try {
            String sql = "DELETE FROM Members WHERE ClubID = ? AND StudentID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, clubID);
            pstmt.setString(2, studentID);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("부원 삭제 완료.");
            } else {
                System.out.println("부원 삭제 실패.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 동아리 부원
    private static void MemberOp(Connection conn) {
        Scanner scanner = new Scanner(System.in);

        // 학번 (student ID) 입력
        System.out.print("학번 입력: ");
        String studentID = scanner.nextLine();

        try {
            // 학생이 속한 동아리의 ClubID 가져오기
            String clubQuery = "SELECT ClubID FROM Members WHERE StudentID = ?";
            PreparedStatement pstmtClub = conn.prepareStatement(clubQuery);
            pstmtClub.setString(1, studentID);
            ResultSet rsClub = pstmtClub.executeQuery();

            if (rsClub.next()) {
                int clubID = rsClub.getInt("ClubID");

                // 동아리 이름 가져오기
                String clubNameQuery = "SELECT ClubName FROM Club WHERE ClubID = ?";
                PreparedStatement pstmtClubName = conn.prepareStatement(clubNameQuery);
                pstmtClubName.setInt(1, clubID);
                ResultSet rsClubName = pstmtClubName.executeQuery();

                if (rsClubName.next()) {
                    String clubName = rsClubName.getString("ClubName");

                    System.out.println(clubName+" 동아리의 부원입니다.");

                    // 부원 메뉴 출력
                    while (true) {
                        System.out.println("\n------- 동아리 부원 메뉴 -------");
                        System.out.println("1. 동아리 목록");
                        System.out.println("2. 부원 목록");
                        System.out.println("3. 활동 보고서 목록");
                        System.out.println("4. 종료");
                        System.out.print("선택: ");
                        int choice = scanner.nextInt();

                        switch (choice) {
                            case 1:
                                viewClubList(conn);  // 동아리 목록
                                break;
                            case 2:
                                viewMemberList(conn, clubID);  // 부원 목록
                                break;
                            case 3:
                                viewActivityReportList(conn, clubID);  // 활동 보고서 목록 보기
                                break;
                            case 4:
                                System.out.println("시스템 종료.");
                                return;  // 종료
                            default:
                                System.out.println("잘못된 선택입니다. 다시 선택해주세요.");
                                break;
                        }
                    }
                }
            } else {
                System.out.println("소속 동아리를 찾을 수 없습니다.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
