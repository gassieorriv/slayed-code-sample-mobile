package models.users;

public class Schedule {
    public int id;
    public int userId;
    public int dayOfWeekId;
    public String specificDate;
    public int startHour;
    public int startMinute;
    public int closedHour;
    public int closedMinute;
    public boolean deleted;
    public boolean closed;
    public boolean overwritten;
}
