package app.data.infrastructure;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 外部サーバーとのHTTP通信を担当するクラス
 */
public class NetworkClient {

        // アプリ全体で使い回すのが推奨されるため、フィールドとして1つだけ持つ
        private final OkHttpClient client = new OkHttpClient();

        public String fetchText(String url) throws IOException {

                /**
                 * 指定されたURLからテキストデータ（CSVなど）を同期的に取得
                 * ※今回はTimeOutや認証などは除外
                 * @param url 取得先のURL
                 * @return 取得した文字列（CSVデータ）
                 * @throws IOException 通信エラーやレスポンスが空の場合に例外を投げる
                 */
                Request request = new Request.Builder()
                                .url(url)
                                .build();

                /*
                 * execute() メソッドで同期通信を実行
                 * データが届くと、resposeに格納する
                 */
                try(Response response = client.newCall(request).execute()){
                        
                        //サーバーからエラー（404）などのチェック
                        if( ! response.isSuccessful()) {
                                throw new IOException
                                ("通信に失敗しました。ステータスコード: " + response.code());
                        }
                        
                        // データ本体（body）を文字列として取り出して返す
                        if(response.body() != null) {
                                return response.body().string();
                        }else {
                                throw new IOException("レスポンスのデータが空です。");
                        }
                }
        }
}