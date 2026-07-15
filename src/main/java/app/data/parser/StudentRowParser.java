package app.data.parser;

import java.util.EnumMap;
import java.util.Map;

import app.data.model.Student;
import app.data.model.Subject;

/**
 * 
 * 生徒データ（Studentレコード）専用のRowParser実装クラス
 * CSVの1行（カンマ区切り文字列）を解析し、
 * Studentオブジェクトを生成する。
 * 
 */
public class StudentRowParser implements RowParser<Student>{

        //CSVの列の並び順（内部定数としてカプセル化）
        private static final int COL_ID = 0;
        private static final int COL_NAME = 1;
        private static final int COL_AGE = 2;
        private static final int COL_ADDRESS = 3;
        private static final int COL_SCORE_JAPANESE = 4;        // 国語 (score_japanese)
        private static final int COL_SCORE_MATH = 5;                // 数学 (score_math)
        private static final int COL_SCORE_ENGLISH = 6;        // 英語 (score_english)
        private static int COL_SCORE_SCIENCE = 7;                        // 理科 (score_science)
        private static final int COL_SCORE_SOCIETY = 8;        // 社会 (score_society)
        private static final int COL_PHONE = 9;
        
        //5教科を含むカラム数
        private static final int REQUIRED_COLUMNS = 10;
    
        
        @Override
        public Student parseRow(String row) throws IllegalArgumentException {
                //１行がnullもしくは空の場合
                if(row == null || row.trim().isEmpty()) {
                        throw new IllegalArgumentException("空白の行データは解析できません。");
                }
                
                //カンマで分割して配列に入れる（-1なので空の列データも分割数に入れます）
                String[] columns = row.split(",",-1);
                
                //バリデーション（列数のチェック）
                if(columns.length < REQUIRED_COLUMNS) {
                        String.format("データの列数が足りません（必要: %d, 実際: %d）。行データ: [%s]",
                                        REQUIRED_COLUMNS, columns.length, row);
                }
                
                
                try {
                        //IDの取得。数値に変換する際に例外発生の可能性あり
                        int id = Integer.parseInt(columns[COL_ID].trim());
                        
                        //配列0=名前の取得。trim()で文頭と文末の空白を削除
                        String name = columns[COL_NAME].trim();
                        
                        //年齢の取得。数値に変換する際に例外発生の可能性あり
                        int age = Integer.parseInt(columns[COL_AGE].trim());
                        
                        //住所の取得。
                        String address = columns[COL_ADDRESS].trim();
                        
                        //電話番号の取得
                        String phoneNumber = columns[COL_PHONE].trim();
                        

                        /*
                         * 点数データのMapを作成（keyはEnumになります）
                         * EnumMapは標準クラスで、keyがEnumであるときに使える
                         * 特殊なMapになります。
                         */
                        Map<Subject, Integer> scores = new EnumMap<>(Subject.class);
                        
                        scores.put(Subject.JAPANESE, Integer.parseInt(columns[COL_SCORE_JAPANESE].trim()));
            scores.put(Subject.MATH, Integer.parseInt(columns[COL_SCORE_MATH].trim()));
            scores.put(Subject.ENGLISH, Integer.parseInt(columns[COL_SCORE_ENGLISH].trim()));
            scores.put(Subject.SCIENCE, Integer.parseInt(columns[COL_SCORE_SCIENCE].trim()));
            scores.put(Subject.SOCIETY, Integer.parseInt(columns[COL_SCORE_SOCIETY].trim()));
                        
                        
                        //Studentレコードの生成
            return new Student(id,name, age, address, phoneNumber, scores);
                        
                        
                }catch(NumberFormatException e) {
                        //Integer.parseIntで例外が出た場合
                        throw new IllegalArgumentException("年齢または点数のデータが数値ではありません。", e);
                }
                
        }

}