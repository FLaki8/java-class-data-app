package app.data;

import javax.swing.SwingUtilities;

import app.data.controller.SyncController;
import app.data.infrastructure.NetworkClient;
import app.data.parser.CsvReader;
import app.data.repository.StudentRepository;
import app.data.view.DataView;

public class Main {

        public static void main(String[] args) {
                // 各種部品（インスタンス）の生成と組み立て
                NetworkClient client = new NetworkClient();
                CsvReader csvReader = new CsvReader(client);
                StudentRepository repository = new StudentRepository("jdbc:sqlite:data/school.db");

                SyncController controller = new SyncController(csvReader, repository);

                // GUIの起動
                SwingUtilities.invokeLater(() -> {
                        DataView view = new DataView();
                        view.setController(controller); // 画面にコントローラーを渡す
                        view.setVisible(true); // 画面を表示
                });
        }
}