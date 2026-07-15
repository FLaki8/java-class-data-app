package app.data.model;

import java.util.Map;

/**
 * スプレッドシート上の生徒のデータ格納用
 */
public record Student(int id,
                String name,
                int age,
                String address,
                String PhoneNumber,
                Map<Subject, Integer> scores

) {

        // 特定の教科の点数だけを抜くメソッド」
        public int getScore(Subject subject) {
                return scores != null ? scores.getOrDefault(subject, 0) : 0;
        }

}