import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class Table {
    private final String name;
    private final Database db;
    private final File file;

    public Path getPath() {
        return this.db.getPath().resolve(this.name).toAbsolutePath();
    }

    public String getName() {
        return name;
    }

    public Table(Database db, String name) {
        this.db = db;
        this.name = name;

        this.file = db.getPath().resolve(this.name).toAbsolutePath().toFile();
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

    public void insert(int id, String name) throws IOException {
        var content = id +
                " " +
                name.length() +
                " " +
                name;

        System.out.println(content);

        var writer = new BufferedWriter(new FileWriter(this.getPath().toString(), true));

        writer.write(content);
        writer.newLine();
        writer.close();

        System.out.println("here");
    }
}
