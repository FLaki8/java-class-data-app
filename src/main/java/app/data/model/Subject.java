package app.data.model;

public enum Subject {
        /**
        * 科目名とDBのカラム名
        */
        JAPANESE("国語", "score_japanese"), 
        MATH("数学", "score_math"), 
        ENGLISH("英語", "score_english"), 
        SCIENCE("理科","score_science"), 
        SOCIETY("社会", "score_society");

        private final String japaneserName;
        private final String columnName;

        private Subject(String japaneserName, String columnName) {
                this.japaneserName = japaneserName;
                this.columnName = columnName;
        }

        public String getJapaneserName() {
                return japaneserName;
        }

        public String getColumnName() {
                return columnName;
        }
}