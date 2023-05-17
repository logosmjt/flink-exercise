package exercise.flink;

import com.google.gson.Gson;
import exercise.flink.model.RawEvent;
import exercise.flink.model.UserEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class HelloWorld {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        FileSource<String> source = FileSource.forRecordStreamFormat(new TextLineInputFormat("UTF-8"),
                new Path("./data/events.txt")).build();
        DataStreamSource<String> eventStream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "events");
        SingleOutputStreamOperator<UserEvent> eventOperator = eventStream.map((MapFunction<String, UserEvent>) value ->
        {
            RawEvent rawEvent = new Gson().fromJson(value, RawEvent.class);
            return new UserEvent(rawEvent.userName, 1, rawEvent.productId, rawEvent.path, rawEvent.isSuccess, rawEvent.timestamp);
        });
        KeyedStream<UserEvent, String> eventKeyedStream = eventOperator.keyBy((KeySelector<UserEvent, String>) UserEvent::getUserName);
        eventKeyedStream.reduce((ReduceFunction<UserEvent>) (value1, value2) ->
                new UserEvent(value1.userName, value1.count + value2.count, value1.productId,
                        value1.path + ", " + value2.path, value1.isSuccess, value2.timestamp)).print();
        env.execute();
    }
}
