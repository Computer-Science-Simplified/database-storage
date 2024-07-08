import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        var db = Database.create("example");
//        var table = db.createTable("products");

        var db = new Database("example");
        var table = db.getTable("products");

        table.insert(119, "John Doe");
        table.insert(2, "Jane Doe");
        table.insert(3, "Joe Doe");

        table.selectById(119);
    }
}
