package exercise.flink.model;

public class Event {
    public String userName;
    public String productId;
    public String path;
    public boolean isSuccess;
    public long timestamp;

    public Event() {
    }

    public Event(String userName, String productId, String path, boolean isSuccess, long timestamp) {
        this.userName = userName;
        this.productId = productId;
        this.path = path;
        this.isSuccess = isSuccess;
        this.timestamp = timestamp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Event{" +
                "userName='" + userName + '\'' +
                ", productId='" + productId + '\'' +
                ", path='" + path + '\'' +
                ", isSuccess=" + isSuccess +
                ", timestamp=" + timestamp +
                '}';
    }
}
