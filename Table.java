import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Table {
    private final String name;
    private final String filename;
    private final Database db;

    public Path getPath() {
        return this.db.getPath().resolve(this.filename).toAbsolutePath();
    }

    public String getName() {
        return name;
    }

    public Table(Database db, String name) {
        this.db = db;
        this.name = name;
        this.filename = name + ".bin";
    }

    public static Table create(Database db, String name) throws IOException {
        File file = db.getPath().resolve(name + ".bin").toAbsolutePath().toFile();

        if (file.createNewFile()) {
            System.out.printf("Table created [%s]", name);
            System.out.println();
        } else {
            throw new RuntimeException("Unable to create table");
        }

        return new Table(db, name);
    }

    public Optional<String> selectById(int id) throws IOException {
        var stream = new DataInputStream(new FileInputStream(this.getPath().toString()));

        int foundId = -1;

        int sizeOfNextColumn = 0;

        while (foundId != id) {
            try {
                foundId = stream.readInt();

                sizeOfNextColumn = stream.readByte();

                if (foundId == id) {
                    break;
                }

                stream.skipBytes(sizeOfNextColumn);
            } catch (EOFException _) {
                return Optional.empty();
            }
        }

        byte[] bytes = stream.readNBytes(sizeOfNextColumn);

        String data = new String(bytes);

        stream.close();

        return Optional.of(foundId + data);
    }

    public List<Optional<String>> selectByIds(int[] ids) throws IOException {
        List<Optional<String>> rows = new ArrayList<>();

        for (int id : ids) {
            var row = this.selectById(id);

            if (row.isEmpty()) {
                continue;
            }

            rows.add(row);
        }

        return rows;
    }

    public void insert(int id, String name) throws IOException {
        var stream = new DataOutputStream(
                new FileOutputStream(
                        this.getPath().toString(),
                        true
                )
        );

        stream.writeInt(id);

        stream.writeByte(name.length());

        for (byte b : name.getBytes(StandardCharsets.US_ASCII)) {
            stream.writeByte(b);
        }

        stream.close();
    }
}
