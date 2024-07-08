import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
//        var db = Database.create("example");
//        var table = db.createTable("products");

        var db = new Database("example");
        var table = db.getTable("products");

        System.out.println(table.getPath());
    }
}
