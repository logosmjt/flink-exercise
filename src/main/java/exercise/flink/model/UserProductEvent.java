package exercise.flink.model;

public class UserProductEvent {
    public String userName;
    public String productId;
    public String productName;
    public String path;
    public long timestamp;

    public UserProductEvent() {
    }

    public UserProductEvent(String userName, String productId, String productName, String path, long timestamp) {
        this.userName = userName;
        this.productId = productId;
        this.productName = productName;
        this.path = path;
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UserProductEvent{" +
                "userName='" + userName + '\'' +
                ", productId='" + productId + '\'' +
                ", productName='" + productName + '\'' +
                ", path='" + path + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
