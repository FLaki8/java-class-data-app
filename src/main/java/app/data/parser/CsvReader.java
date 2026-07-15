package app.data.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import app.data.infrastructure.NetworkClient;
import app.data.util.AppLogger;

public class CsvReader {

        //OkHttp通信のクラスのインスタンス
        private final NetworkClient networkClient;

        public CsvReader(NetworkClient networkClient) {
                if (networkClient == null) {
                        throw new IllegalArgumentException("NetworkClientがnullです。");
                }
                this.networkClient = networkClient;
        }

        /**
         * 指定されたURLからCSVデータを非同期で取得し、
         * 指定されたパーサーでオブジェクトのリストに変換します。
         * @param <T>
         * @param url
         * @param parser
         * @return
         */
        public <T> CompletableFuture<List<T>> fetchAllAsync(String url, RowParser<T> parser) {
                if (url == null || parser == null) {
                        throw new IllegalArgumentException("URLまたはパーサーがnullです。");
                }

                /*
                 * 【非同期】ExecutorService（裏側のプール）を使い、
                 * メインスレッドから処理を切り離す
                 */
                return CompletableFuture.supplyAsync(() -> {
                        AppLogger.print("CSVの非同期取得・解析を開始します。URL: " + url);
                        List<T> resultList = new ArrayList<>();

                        String csvContent;
                        try {
                                // ※NetworkClientに定義されているメソッドを呼び出す
                                csvContent = this.networkClient.fetchText(url);
                        } catch (IOException e) {
                                AppLogger.print("CSVのダウンロードに失敗しました。URL: " + url);
                                throw new RuntimeException("通信エラーによりCSVを取得できませんでした。", e);
                        }

                        // 3. 【解析】取得した文字列を1行ずつ安全に処理
                        try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {
                                String line;
                                boolean isHeader = true;

                                while ((line = reader.readLine()) != null) {
                                        // 最初の1行（ヘッダー）はデータではないため読み飛ばす
                                        if (isHeader) {
                                                isHeader = false;
                                                continue;
                                        }

                                        // 空行はスキップ
                                        if (line.trim().isEmpty()) {
                                                continue;
                                        }

                                        // 1行ごとの解析をパーサーに任せる
                                        try {
                                                T domainObject = parser.parseRow(line);
                                                resultList.add(domainObject);
                                        } catch (Exception e) {
                                                // 列数不足や型変換エラー
                                                AppLogger.print("不正な行を検出したためスキップしました。"
                                                                + "行データ: [" + line + "] 理由: " + e.getMessage());
                                        }
                                }
                        } catch (IOException e) {
                                AppLogger.print("CSV文字列の読み込み中に致命的なエラーが発生しました。"+e);
                                throw new RuntimeException("データの解析中にエラーが発生しました。", e);
                        }

                        AppLogger.print("CSVの非同期取得・解析が完了しました。総件数: " 
                        + resultList.size() + "件");
                        return resultList; // 呼び出し元へリストを返却
                });

        }

}