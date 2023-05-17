package exercise.flink.datastream;

import com.google.gson.Gson;
import exercise.flink.model.RawEvent;
import exercise.flink.model.UserEvent;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.time.Duration;

public class TopNInWindow {

  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
    FileSource<String> source =
        FileSource.forRecordStreamFormat(
                new TextLineInputFormat("UTF-8"), new Path("./data/events.txt"))
            .build();
    DataStreamSource<String> eventStream =
        env.fromSource(source, WatermarkStrategy.forBoundedOutOfOrderness(Duration.ZERO), "events");
    SingleOutputStreamOperator<UserEvent> userEventOperator =
        eventStream
            .map(
                (MapFunction<String, UserEvent>)
                    value -> {
                      RawEvent rawEvent = new Gson().fromJson(value, RawEvent.class);
                      return new UserEvent(
                          rawEvent.userName,
                          1,
                          rawEvent.productId,
                          rawEvent.path,
                          rawEvent.isSuccess,
                          rawEvent.timestamp);
                    })
            .assignTimestampsAndWatermarks(
                WatermarkStrategy.<UserEvent>forBoundedOutOfOrderness(Duration.ZERO)
                    .withTimestampAssigner(
                        (SerializableTimestampAssigner<UserEvent>)
                            (element, recordTimestamp) -> element.timestamp * 1000L));

    userEventOperator
        .keyBy((KeySelector<UserEvent, String>) UserEvent::getUserName)
        //                .window(SlidingEventTimeWindows.of(Time.seconds(5), Time.seconds(3)))
        .window(TumblingEventTimeWindows.of(Time.seconds(5)))
        .reduce(
            (ReduceFunction<UserEvent>)
                (value1, value2) ->
                    new UserEvent(
                        value1.userName,
                        value1.count + value2.count,
                        value1.productId,
                        value1.path + ", " + value2.path,
                        value1.isSuccess,
                        value2.timestamp),
            new CustomProcessWindowFunction())
        .print();
    env.execute("TopNInWindow");
  }

  private static class CustomProcessWindowFunction
      extends ProcessWindowFunction<UserEvent, Tuple2<String, UserEvent>, String, TimeWindow> {

    @Override
    public void process(
        String s,
        ProcessWindowFunction<UserEvent, Tuple2<String, UserEvent>, String, TimeWindow>.Context
            context,
        Iterable<UserEvent> elements,
        Collector<Tuple2<String, UserEvent>> out) {
      String window = context.window().getStart() + " " + context.window().getEnd();
      elements.forEach(
          userEvent -> {
            out.collect(new Tuple2<>(window, userEvent));
          });
    }
  }
}
