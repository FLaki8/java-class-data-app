package app.data.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import app.data.model.Student;
import app.data.parser.CsvReader;
import app.data.parser.StudentRowParser;
import app.data.repository.StudentRepository;
import app.data.view.DataView;

public class SyncController {

        private final CsvReader csvReader;
        private final StudentRepository studentRepository;
        private final StudentRowParser parser;

        public SyncController(CsvReader csvReader, StudentRepository studentRepository) {
                this.csvReader = csvReader;
                this.studentRepository = studentRepository;
                this.parser = new StudentRowParser();
        }

        /**
         * 非同期処理をキックするメソッド
         */
        public void startSync(String url, DataView view) {
                // 1. 画面をローディング＆入力不可にする
                view.onSyncStarted();

                // 2. CsvReaderを発動させて非同期処理（通信・解析）をスタート
                CompletableFuture<List<Student>> future = csvReader.fetchAllAsync(url, parser);

                // 3. 裏での処理が終わった後のコンボ（バトンリレー）を登録
                future.thenAccept(studentList -> {
                        try {
                                // データベースへ保存
                                studentRepository.saveAll(studentList);
                                // 画面に完了を通知
                                view.onSyncCompleted(studentList.size());
                                // 解析・保存したリストを画面のJTableに届けて描画させる
                                view.displayStudents(studentList);
                                
                        } catch (Exception e) {
                                view.onSyncFailed("DB保存エラー: " + e.getMessage());
                        }
                });

                // 4. 途中で通信エラーなどが起きた場合の処理を登録
                future.exceptionally(ex -> {
                        view.onSyncFailed("通信または解析エラー");
                        return null;
                });
        }
}