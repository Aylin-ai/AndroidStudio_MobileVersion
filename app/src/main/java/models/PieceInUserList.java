package models;

public class PieceInUserList {
    private long pieceId;
    public long getPieceId() {
        return pieceId;
    }
    public void setPieceId(long pieceId) {
        this.pieceId = pieceId;
    }
    private String userList;
    public String getUserList() {
        return userList;
    }
    public void setUserList(String userList) {
        this.userList = userList;
    }
    private String userEmail;
    public String getUserEmail() {
        return userEmail;
    }
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
