import java.io.*;
import java.nio.charset.StandardCharsets;
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

        byte[] bytes = content.getBytes(StandardCharsets.US_ASCII);

        var stream = new FileOutputStream(this.getPath().toString(), true);

        stream.write(bytes);
        stream.close();
    }

    public void selectById(int id) throws IOException {
        var raf = new RandomAccessFile(this.getPath().toString(), "r");

        raf.seek(0);

        var foundId = this.readId(raf, id);

        System.out.println(foundId);
    }

    private int readId(RandomAccessFile raf, int id) throws IOException {
        var data = this.readUntilNextSpace(raf);

        System.out.println(data);

        int foundId = -1;

        try {
            foundId = Integer.parseInt(data);
        } catch (NumberFormatException e) {
        }

        if (foundId == id) {
            return foundId;
        } else {
            var sizeOfNextColumn = Character.getNumericValue((char) raf.readByte()) + 1;

            raf.skipBytes(sizeOfNextColumn);

            return this.readId(raf, id);
        }
    }

    private String readUntilNextSpace(RandomAccessFile raf) throws IOException {
        StringBuilder data = new StringBuilder();

        int b = raf.read();

        while (b != 32) {
            data.append((char) b);

            b = raf.read();
        }

        return data.toString();
    }
}
