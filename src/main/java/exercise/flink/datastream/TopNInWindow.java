package exercise.flink.datastream;

import com.google.gson.Gson;
import exercise.flink.model.RawEvent;
import exercise.flink.model.UserEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.flink.api.common.eventtime.SerializableTimestampAssigner;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

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
        .window(TumblingEventTimeWindows.of(Time.seconds(10)))
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
            new EventProcessWindowFunction())
        .keyBy((KeySelector<Tuple2<Long, UserEvent>, Long>) t -> t.f0)
        .process(new TopNWindowFunction(2))
        .print();

    env.execute("TopNInWindow");
  }

  private static class EventProcessWindowFunction
      extends ProcessWindowFunction<UserEvent, Tuple2<Long, UserEvent>, String, TimeWindow> {

    @Override
    public void process(
        String key,
        ProcessWindowFunction<UserEvent, Tuple2<Long, UserEvent>, String, TimeWindow>.Context
            context,
        Iterable<UserEvent> elements,
        Collector<Tuple2<Long, UserEvent>> out) {
      long window = context.window().getStart();
      out.collect(new Tuple2<>(window, elements.iterator().next()));
    }
  }

  private static class TopNWindowFunction
      extends KeyedProcessFunction<Long, Tuple2<Long, UserEvent>, String> {
    private final int n;
    List<Tuple2<Long, UserEvent>> list;

    public TopNWindowFunction(int n) {
      this.n = n;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
      super.open(parameters);
      list = new ArrayList<>();
    }

    @Override
    public void processElement(
        Tuple2<Long, UserEvent> value,
        KeyedProcessFunction<Long, Tuple2<Long, UserEvent>, String>.Context ctx,
        Collector<String> out)
        throws Exception {
      list.add(value);
      list.sort(Comparator.comparingInt(t -> -t.f1.count));
      if (list.size() > n) {
        list.remove(n);
      }

      ctx.timerService().registerEventTimeTimer(value.f0);
    }

    @Override
    public void onTimer(
        long timestamp,
        KeyedProcessFunction<Long, Tuple2<Long, UserEvent>, String>.OnTimerContext ctx,
        Collector<String> out)
        throws Exception {
      // 输出队列中的元素
      StringBuilder topN = new StringBuilder();
      AtomicReference<Long> key = new AtomicReference<>(0L);

      list.forEach(
          (Tuple2<Long, UserEvent> tuple2) -> {
            key.set(tuple2.f0);
            topN.append("name = ")
                .append(tuple2.f1.userName)
                .append(", count = ")
                .append(tuple2.f1.count)
                .append(";");
          });

      out.collect(key + ":" + topN);
      list.clear();
    }
  }
}
