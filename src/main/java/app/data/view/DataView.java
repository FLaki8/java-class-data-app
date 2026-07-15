package app.data.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import app.data.controller.SyncController;
import app.data.model.Student;
import app.data.model.Subject;

public class DataView extends JFrame {

        private final JButton syncButton;
        private final JLabel statusLabel;
        private final JTable studentTable;
        private final DefaultTableModel tableModel;
        private SyncController controller;

        public DataView() {
                super("生徒データ同期システム");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setSize(800, 500); // テーブルを表示するため、横幅と高さを広げます
                setLayout(new BorderLayout()); // 配置を綺麗に制御するため BorderLayout 

                // --- 上部：操作パネルの配置 ---
                JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                this.syncButton = new JButton("データ読み込み");
                this.statusLabel = new JLabel("待機中");
                topPanel.add(syncButton);
                topPanel.add(statusLabel);
                add(topPanel, BorderLayout.NORTH); // 画面の上部に配置

                // --- 中央：テーブル（全データ表示部）の配置 ---
                // 表のヘッダー定義（10列分）
                String[] columns = {
                        "ID", "名前", "年齢", "住所", "電話番号", 
                        "国語", "数学", "英語", "理科", "社会"
                };
                
                // データを管理するモデル（初期状態は行数0）
                this.tableModel = new DefaultTableModel(columns, 0) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                                return false; // ダブルクリックしてもセルを直接編集できないようにロック
                        }
                };
                
                this.studentTable = new JTable(tableModel);
                
                // スクロールバー付きのパネルでテーブルを包む
                JScrollPane scrollPane = new JScrollPane(studentTable);
                add(scrollPane, BorderLayout.CENTER); // 画面の中央（残り領域全体）に配置

                // ボタンが押された時の処理
                syncButton.addActionListener(e -> {
                        if (controller != null) {
                                String url = "https://docs.google.com/spreadsheets/d/1JImhMGkMco59cuUzwIv6euxmOpXL2hXFa2f6SDSC0SM/export?format=csv&gid=1031378451";
                                controller.startSync(url, this);
                        }
                });
        }

        /**
         * 取得した生徒データをJTableに描画するメソッド
         * コントローラー等から呼び出されてテーブルを更新します。
         */
        public void displayStudents(List<Student> students) {
                SwingUtilities.invokeLater(() -> {
                        // 現在表示されているデータを一度すべてクリア
                        tableModel.setRowCount(0);

                        if (students == null) return;

                        // リストから1件ずつ取り出してテーブルの行として追加
                        for (Student student : students) {
                                Object[] rowData = {
                                        student.id(),
                                        student.name(),
                                        student.age(),
                                        student.address(),
                                        student.PhoneNumber(),
                                        student.getScore(Subject.JAPANESE),
                                        student.getScore(Subject.MATH),
                                        student.getScore(Subject.ENGLISH),
                                        student.getScore(Subject.SCIENCE),
                                        student.getScore(Subject.SOCIETY)
                                };
                                tableModel.addRow(rowData);
                        }
                });
        }

        /**
         * コントローラーを外部からセットする
         */
        public void setController(SyncController controller) {
                this.controller = controller;
        }

        /**
         * 【状態変化】同期開始：ボタンを入力不可にし、ローディング表示にする
         */
        public void onSyncStarted() {
                syncButton.setEnabled(false);
                statusLabel.setText("🔄 ローディング中...（通信・解析を実行中）");
        }

        /**
         * 【状態変化】同期完了：ボタンを一瞬戻すか、完了メッセージを出す
         */
        public void onSyncCompleted(int totalCount) {
                SwingUtilities.invokeLater(() -> {
                        syncButton.setEnabled(true);
                        statusLabel.setText("✅ 同期完了！ " + totalCount + " 件のデータを保存しました。");
                });
        }

        /**
         * 【状態変化】同期失敗：ボタンを復帰させ、エラーを出す
         */
        public void onSyncFailed(String message) {
                SwingUtilities.invokeLater(() -> {
                        syncButton.setEnabled(true);
                        statusLabel.setText("❌ エラー: " + message);
                });
        }
}