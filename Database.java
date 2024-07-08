import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Database {
    private final String name;
    private final List<Table> tables = new ArrayList<>();

    public Path getPath() {
        return Paths.get("workdir", this.name).toAbsolutePath();
    }

    public Database(String name) throws IOException {
        this.name = name;

        File folder = this.getPath().toFile();

        if (!folder.exists()) {
            throw new RuntimeException("Database does not exists");
        }

        var tablePaths = this.readTablePaths();

        for (Path tablePath : tablePaths) {
            this.tables.add(new Table(this, tablePath.getFileName().toString()));
        }
    }

    public static Database create(String name) throws IOException {
        File folder = new File(
            Paths.get("workdir", name).toAbsolutePath().toString()
        );

        if (folder.mkdir()) {
            System.out.printf("Database created [%s]", name);
            System.out.println();
        } else {
            throw new RuntimeException("Unable to create database");
        }

        return new Database(name);
    }

    public Table createTable(String tableName) throws IOException {
        var table = Table.create(this, tableName);

        this.tables.add(table);

        return table;
    }

    public Table getTable(String tableName) {
        Optional<Table> table = this.tables.stream()
                .filter(x -> x.getName().equals(tableName))
                .findFirst();

        if (table.isEmpty()) {
            throw new RuntimeException("Table not found");
        }

        return table.get();
    }

    private List<Path> readTablePaths() throws IOException {
        Stream<Path> paths = Files.walk(this.getPath());

        return paths
                .filter(Files::isRegularFile)
                .map(Path::getFileName)
                .toList();
    }
}
