import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {
//        var db = Database.create("example");
//        var table = db.createTable("products");
//
        var db = new Database("example");
//        var table = db.createTable("users");
        var table = db.getTable("users");
//
//        table.read();
//        table.insert(1, "John Doe");
//        table.insert(2, "Jane Doe");
//        table.insert(3, "Joe Doe");
//        table.insert(27, "James Doe");

//        var result = table.selectById(10);
//
//        if (result.isPresent()) {
//            System.out.println(result.get());
//        } else {
//            System.out.println("Not found");
//        }

        int[] ids = new int[]{119,231};
        var results = table.selectByIds(ids);

        System.out.println(results);
    }
}
