package exercise.flink;

import com.google.gson.Gson;
import exercise.flink.model.RawEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EventsGenerator {
    public static void main(String[] args) throws Exception{
        List<String> names = List.of("Mary", "Bob", "Alice", "James");
        List<String> ids = List.of("1001", "1002", "1003", "1004");
        List<String> paths = List.of("product", "order", "comments");
        Random random = new Random();
        Gson gson = new Gson();
        ArrayList<RawEvent> rawEvents = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            rawEvents.add(new RawEvent(
                    names.get(random.nextInt(names.size())),
                    ids.get(random.nextInt(ids.size())),
                    paths.get(random.nextInt(paths.size())),
                    true,
                    1000 + i * random.nextInt(20)
            ));
        }
        System.out.println(gson.toJson(rawEvents));
    }
}
