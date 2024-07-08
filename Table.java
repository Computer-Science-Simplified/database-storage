import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Table {
    private final String name;
    private final Database db;

    public Path getPath() {
        return this.db.getPath().resolve(this.name).toAbsolutePath();
    }

    public String getName() {
        return name;
    }

    public Table(Database db, String name) {
        this.db = db;
        this.name = name;
    }

    public static Table create(Database db, String name) throws IOException {
        File file = db.getPath().resolve(name).toAbsolutePath().toFile();

        if (file.createNewFile()) {
            System.out.printf("Table created [%s]", name);
            System.out.println();
        } else {
            throw new RuntimeException("Unable to create table");
        }

        return new Table(db, name);
    }
}
