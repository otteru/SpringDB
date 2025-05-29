package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * jdbc - driverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql); //SQL 문을 미리 컴파일해서 실행 계획을 준비하는 메서드
            // 이렇게 파라미터 바인딩을 해야지 sql insertion 공격을 예방할 수 있다.
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate(); // 이러면 쿼리가 실행이 된다.
            // 얘가 숫자를 반환하는데 몇개의 로우에 영향을 미쳤는지 혹은 생성했는지 갯수를 알려줌
            return member;
        } catch (SQLException e) { // Database 연결 실패 혹은 sql 문제를 있을 때 생기는 exception
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null); // DB는 외부에서 사용하는 것들이기 때문에 꼭 사용하고 close해야 함(리소스 정리)
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery(); // 쿼리 실행 결과를 rs에 넣어줌
            if(rs.next()) { // next를 호출해야 데이터로 넘어가서 호출해줘야 한다.
                // 만약 하나만 조회하는 게 아니라면 while문
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId="+ memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        // close에서도 exception이 날 수 있다.

        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
        // 이렇게 해줘야지 stmt.close에서 예외가 발생해도 catch로 잡기 때문에 con.close가 실행된다.

        if(con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }

        }
    }

    public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1,money);
            pstmt.setString(2, memberId);

            int resultSize = pstmt.executeUpdate();
            // 얘가 숫자를 반환하는데 몇개의 로우에 영향을 미쳤는지 혹은 생성했는지 갯수를 알려줌
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    private static Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
