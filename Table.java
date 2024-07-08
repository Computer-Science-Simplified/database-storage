import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

        byte[] bytes = content.getBytes(StandardCharsets.US_ASCII);

        var stream = new FileOutputStream(this.getPath().toString(), true);

        stream.write(bytes);
        stream.close();
    }

    public void selectById(int id) throws IOException {
        /**
         * - Read the first byte
         * - Check if it's the given ID
         * - If not, seek the appropriate number of bytes
         * - Repeat
         */

        var stream = new FileInputStream(this.getPath().toString());

        StringBuilder foundIdBuilder = new StringBuilder();
        int b = stream.read();

        while (b != 32) {
            foundIdBuilder.append((char) b);

            b = stream.read();
        }

        var foundId = Integer.parseInt(foundIdBuilder.toString());

        if (foundId == id) {
            System.out.println("found");
        } else {
            System.out.println("not yet");
        }
    }
}
