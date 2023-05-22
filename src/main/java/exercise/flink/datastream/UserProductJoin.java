package exercise.flink.datastream;

import com.google.gson.Gson;
import exercise.flink.model.Product;
import exercise.flink.model.RawEvent;
import exercise.flink.model.UserEvent;
import exercise.flink.model.UserProductEvent;
import org.apache.flink.api.common.eventtime.AscendingTimestampsWatermarks;
import org.apache.flink.api.common.eventtime.TimestampAssigner;
import org.apache.flink.api.common.eventtime.TimestampAssignerSupplier;
import org.apache.flink.api.common.eventtime.WatermarkGenerator;
import org.apache.flink.api.common.eventtime.WatermarkGeneratorSupplier;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.TextLineInputFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class UserProductJoin {
  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);
    env.setParallelism(2);

    FileSource<String> userEventSource =
        FileSource.forRecordStreamFormat(
                new TextLineInputFormat("UTF-8"), new Path("./data/events.txt"))
            .build();
    DataStreamSource<String> userEventStream =
        env.fromSource(userEventSource, WatermarkStrategy.noWatermarks(), "events");
    SingleOutputStreamOperator<UserEvent> userEventOperator =
        userEventStream
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
            .assignTimestampsAndWatermarks(IngestionTimeWatermarkStrategy.create());

    FileSource<String> productSource =
        FileSource.forRecordStreamFormat(
                new TextLineInputFormat("UTF-8"), new Path("./data/product.txt"))
            .build();
    DataStreamSource<String> productEventStream =
        env.fromSource(productSource, WatermarkStrategy.noWatermarks(), "products");
    SingleOutputStreamOperator<Product> productOperator =
        productEventStream
            .map((MapFunction<String, Product>) value -> new Gson().fromJson(value, Product.class))
            .assignTimestampsAndWatermarks(IngestionTimeWatermarkStrategy.create());

    userEventOperator
        .keyBy((KeySelector<UserEvent, String>) UserEvent::getProductId)
        .join(productOperator.keyBy((KeySelector<Product, String>) Product::getId))
        .where((KeySelector<UserEvent, String>) UserEvent::getProductId)
        .equalTo((KeySelector<Product, String>) Product::getId)
        .window(TumblingEventTimeWindows.of(Time.milliseconds(20)))
        .apply(
            (JoinFunction<UserEvent, Product, UserProductEvent>)
                (userEvent, product) -> {
                  System.out.println("userEvent = " + userEvent);
                  return new UserProductEvent(
                      userEvent.userName,
                      userEvent.productId,
                      product.name,
                      userEvent.path,
                      userEvent.timestamp);
                })
        .print();

    env.execute("UserProductJoin");
  }

  private static class IngestionTimeWatermarkStrategy<T> implements WatermarkStrategy<T> {

    private IngestionTimeWatermarkStrategy() {}

    public static <T> IngestionTimeWatermarkStrategy<T> create() {
      return new IngestionTimeWatermarkStrategy<>();
    }

    @Override
    public WatermarkGenerator<T> createWatermarkGenerator(
        WatermarkGeneratorSupplier.Context context) {
      return new AscendingTimestampsWatermarks<>();
    }

    @Override
    public TimestampAssigner<T> createTimestampAssigner(TimestampAssignerSupplier.Context context) {
      return (event, timestamp) -> System.currentTimeMillis();
    }
  }
}
