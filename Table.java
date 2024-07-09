import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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

                sizeOfNextColumn = stream.readInt();

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

    public void insert(int id, String name) throws IOException {
        var stream = new DataOutputStream(new FileOutputStream(this.getPath().toString(), true));

        stream.writeInt(id);

        stream.writeInt(name.length());

        for (byte b : name.getBytes(StandardCharsets.US_ASCII)) {
            stream.writeByte(b);
        }

        stream.close();
    }

    public String selectById2(int id) throws IOException {
        var raf = new RandomAccessFile(this.getPath().toString(), "r");

        raf.seek(0);

        this.seekFile(raf, id);

        return this.readOneRow(raf);
    }

//    public List<String> selectByIds(int[] ids) throws IOException {
//        List<String> results = new ArrayList<>();
//
//        for (int id : ids) {
//            results.add(this.selectById(id));
//        }
//
//        return results;
//    }

    private String readOneRow(RandomAccessFile raf) throws IOException {
        var data = this.readUntilNextSpace(raf);

        var sb = new StringBuilder().append(data);

        var sizeOfNextColumn = Character.getNumericValue((char) raf.readByte()) + 1;

        for (int i = 0; i < sizeOfNextColumn; i++) {
            sb.append((char) raf.readByte());
        }

        return sb.toString();
    }

    private long seekFile(RandomAccessFile raf, int id) throws IOException {
        var data = this.readUntilNextSpace(raf);

        int foundId = -1;

        try {
            foundId = Integer.parseInt(data);
        } catch (NumberFormatException _) {
        }

        if (foundId == id) {
            raf.seek(raf.getFilePointer() - (1 + data.length()));

            return raf.getFilePointer();
        } else {
            var sizeOfNextColumn = Character.getNumericValue((char) raf.readByte()) + 1;

            raf.skipBytes(sizeOfNextColumn);

            return this.seekFile(raf, id);
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
