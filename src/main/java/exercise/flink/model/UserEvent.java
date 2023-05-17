package exercise.flink.model;

public class UserEvent {
    public String userName;
    public int count;
    public String productId;
    public String path;
    public boolean isSuccess;
    public long timestamp;

    public UserEvent() {
    }

    public UserEvent(String userName, int count, String productId, String path, boolean isSuccess, long timestamp) {
        this.userName = userName;
        this.count = count;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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
        return "UserEvent{" +
                "userName='" + userName + '\'' +
                ", count=" + count +
                ", productId='" + productId + '\'' +
                ", path='" + path + '\'' +
                ", isSuccess=" + isSuccess +
                ", timestamp=" + timestamp +
                '}';
    }
}
