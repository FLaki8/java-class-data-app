package app.data.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import app.data.model.Student;
import app.data.model.Subject;
import app.data.util.AppLogger;

public class StudentRepository {

        private final String dbUrl;

        public StudentRepository(String dbUrl) {
                if (dbUrl == null || dbUrl.isEmpty()) {
                        throw new IllegalArgumentException("データベースURLが不正です。");
                }
                this.dbUrl = dbUrl;
                initializeTable();
        }

        /**
         * 【初期化】テーブルの作成
         */
        private void initializeTable() {
                String sql = """
                                CREATE TABLE IF NOT EXISTS students (
                                    id INTEGER PRIMARY KEY,
                                    name TEXT NOT NULL,
                                    age INTEGER,
                                    address TEXT,
                                    phone_number TEXT,
                                    score_japanese INTEGER,
                                    score_math INTEGER,
                                    score_english INTEGER,
                                    score_science INTEGER,
                                    score_society INTEGER
                                );
                                """;

                try (Connection conn = DriverManager.getConnection(dbUrl);
                                Statement stmt = conn.createStatement()) {

                        stmt.execute(sql);
                        AppLogger.print("[INFO] データベースの初期化（テーブル確認）が完了しました。");

                } catch (SQLException e) {
                        AppLogger.print("[ERROR] テーブルの初期化中にエラーが発生しました。"
                                        + "理由: " + e.getMessage());
                        AppLogger.error(e);
                        throw new RuntimeException("データベースの起動に失敗しました。", e);
                }
        }

        /**
         * 【一括保存】
         */
        public void saveAll(List<Student> students) {
                if (students == null || students.isEmpty()) {
                        AppLogger.print("[WARN] 保存対象の生徒リストが空のため、"
                                        + "処理をスキップしました。");
                        return;
                }

                // 5教科対応のINSERT SQL文
                String sql = "INSERT OR REPLACE INTO students "
                                + "(id, name, age, address, phone_number, "
                                + "score_japanese, score_math, score_english, score_science, score_society) "
                                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
                Connection conn = null;

                try {
                        conn = DriverManager.getConnection(dbUrl);
                        conn.setAutoCommit(false);

                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                                for (Student student : students) {
                                        pstmt.setInt(1, student.id());
                                        pstmt.setString(2, student.name());
                                        pstmt.setInt(3, student.age());
                                        pstmt.setString(4, student.address());
                                        pstmt.setString(5, student.PhoneNumber());
                                        
                                        // Student.javaのgetScoreメソッドを使用して、Enumをキーに値を取得
                                        pstmt.setInt(6, student.getScore(Subject.JAPANESE));
                                        pstmt.setInt(7, student.getScore(Subject.MATH));
                                        pstmt.setInt(8, student.getScore(Subject.ENGLISH));
                                        pstmt.setInt(9, student.getScore(Subject.SCIENCE));
                                        pstmt.setInt(10, student.getScore(Subject.SOCIETY));

                                        pstmt.addBatch();
                                }

                                int[] result = pstmt.executeBatch();
                                conn.commit();
                                AppLogger.print("[INFO] 生徒データの一括保存が完了しました。"
                                                + "件数: " + result.length + "件");

                        } catch (SQLException e) {
                                throw e;
                        }

                } catch (SQLException e) {
                        if (conn != null) {
                                try {
                                        AppLogger.print("[WARN] エラーを検知したため、"
                                                        + "トランザクションをロールバックします。");
                                        conn.rollback();
                                } catch (SQLException rollbackEx) {
                                        AppLogger.print("[ERROR] ロールバックの実行に失敗しました。"
                                                        + "理由: " + rollbackEx.getMessage());
                                }
                        }

                        AppLogger.print("[ERROR] 生徒データの保存中にSQLエラーが発生しました。"
                                        + "理由: " + e.getMessage());
                        AppLogger.error(e);
                        throw new RuntimeException("データベースへの保存に失敗しました。", e);

                } finally {
                        if (conn != null) {
                                try {
                                        conn.close();
                                } catch (SQLException closeEx) {
                                        AppLogger.print("[ERROR] データベース接続のクローズに失敗しました。"
                                                        + "理由: " + closeEx.getMessage());
                                }
                        }
                }
        }
}