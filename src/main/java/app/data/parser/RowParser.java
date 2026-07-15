package app.data.parser;

/**
 * 
 * CSVなどのテキストデータの１行を
 * 指定した型のオブジェクトに変換する共通ルール
 * 
 * @param <T>変換後のオブジェクトの型
 * 
 */
public interface RowParser<T> {

        /**
         *        カンマなどで区切られた1行の文字列を、オブジェクトに解析（パース）する
         *        @param row カンマ区切りの生データ（例: "田中太郎,18,大阪市..."）
     *         @return 解析されて中身が詰まったオブジェクト
     *         @throws IllegalArgumentException データの列数が足りない、
     *        または数値変換に失敗した場合に投げる
     * 
         */        
        T parseRow(String row) throws IllegalArgumentException;
}