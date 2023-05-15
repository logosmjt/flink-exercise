package exercise.flink;

import com.google.gson.Gson;
import exercise.flink.model.Event;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.time.Duration;

public class HelloWorld {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        FileSource<String> source = FileSource.forRecordStreamFormat(new TextLineInputFormat("UTF-8"), new Path("./data/events.txt")).build();
        DataStreamSource<String> eventStream = env.fromSource(source, WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(2)), "events");
        SingleOutputStreamOperator<Event> eventOperator = eventStream.map((MapFunction<String, Event>) value -> new Gson().fromJson(value, Event.class));
        eventOperator.keyBy((KeySelector<Event, String>) Event::getUserName).maxBy("timestamp").print();
        env.execute();
    }
}
