package stopwatch.terramc.us;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class SPlayer {

    private UUID playerUUID;
    private int mode = 1;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    public final LocalTime defTime = LocalTime.parse("00:00:00", dtf);
    private LocalTime timeLeft = defTime;
    private LocalTime timeRan = defTime;
    private LocalTime timeToRun = defTime;

    public UUID getPlayerUUID() {
        return  playerUUID;
    } public void setPlayerUUID(UUID uuid) {
        this.playerUUID = uuid;
    } public int getMode() {
        return mode;
    } public void setMode(int mode) {
        this.mode = mode;
    } public LocalTime getTimeRan() {
        return timeRan;
    } public void setTimeRan(LocalTime timeRan) {
        this.timeRan = timeRan;
    } public LocalTime getTimeToRun() {
        return timeToRun;
    } public void setTimeToRun(LocalTime timeToRun) {
        this.timeToRun = timeToRun;
    } public LocalTime getTimeLeft() {
        return timeLeft;
    } public void setTimeLeft(LocalTime timeLeft) {
        this.timeLeft = timeLeft;
    }

}
