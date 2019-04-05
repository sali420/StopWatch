package stopwatch.terramc.us;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;

public class SPlayer {
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    public final LocalTime defTime = LocalTime.parse("00:00:00", dtf);
    private UUID playerUUID;
    private int mode = 1;
    private LocalDateTime playTime;
    private LocalTime timeLeft = defTime;
    private LocalTime timeRan = defTime;
    private LocalTime timeToRun = defTime;

    public LocalDateTime getPlayTime() {
        return playTime;
    } public void setPlayTime(long ticks) {
        this.playTime = LocalDateTime.parse("2019-01-01T00:00:00");

        this.playTime = playTime.plusSeconds(ticks / 20);
    } public UUID getPlayerUUID() {
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
