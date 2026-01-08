package com.qwarty.game.session;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.qwarty.game.dto.CountdownPayload;
import com.qwarty.game.dto.GameStatePayload;
import com.qwarty.game.lov.GameStatus;
import com.qwarty.game.model.Room;

public class RacerGameSession implements GameSession {

    private final Room room;
    private final GameBroadcaster gameBroadcaster;
    private final ScheduledExecutorService scheduler;

    private final int COUNTDOWN_SECONDS = 10;

    private GameStatus status;
    private Instant countdownStart;
    private String prompt;
    private Map<String, Object> playerProgress;

    private ScheduledFuture<?> tickTask;

    public RacerGameSession(Room room, GameBroadcaster gameBroadcaster, ScheduledExecutorService scheduler) {
        this.room = room;
        this.gameBroadcaster = gameBroadcaster;
        this.scheduler = scheduler;

        this.status = GameStatus.WAITING;
        this.prompt = "The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog.";

        this.playerProgress = new ConcurrentHashMap<>();
        for (String player : room.getPlayers()) {
            this.playerProgress.put(player, 0);
        }
    }

    @Override
    public boolean canStart() {
        if (room.getPlayers().size() >= 2 && this.status == GameStatus.WAITING) {
            return true;
        }
        return false;
    }

    /**
     * Used to broadcast to a specific user who is joining during `COUNTDOWN` state
     * the remaining time left in the countdown
     */
    @Override
    public void broadcastUserOngoingCountdown(Principal principal) {
        if (countdownStart == null || status != GameStatus.COUNTDOWN) return;

        String roomId = room.getId();

        double elapsed = Duration.between(countdownStart, Instant.now()).toMillis() / 1000.0;
        double remaining = COUNTDOWN_SECONDS - elapsed;

        CountdownPayload payload = CountdownPayload.builder()
            .time(remaining)
            .build();

        gameBroadcaster.broadcastUserCountdown(roomId, principal, payload);
    }

    /**
     * Begins the `COUNTDOWN` state of a game, then schedules the game to {@link #start()} after
     * the countdown ends
     */
    @Override
    public void broadcastAllAndStartCountdown() {
        String roomId = room.getId();

        // Update state and record countdown start
        this.status = GameStatus.COUNTDOWN;
        this.countdownStart = Instant.now();

        CountdownPayload payload = CountdownPayload.builder()
            .time(COUNTDOWN_SECONDS)
            .build();

        // Announce to already-connected clients that the game is starting in `COUNTDOWN_SECONDS`
        gameBroadcaster.broadcastGlobalCountdown(roomId, payload);

        // Schedule the start action after countdown is done
        scheduler.schedule(this::start, COUNTDOWN_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    public void start() {
        String roomId = room.getId();
        this.status = GameStatus.IN_PROGRESS;

        gameBroadcaster.broadcastGameStart(roomId);
        startTick();
    }

    private void startTick() {
        System.out.println("START TICK");
        tickTask = scheduler.scheduleAtFixedRate(() -> tick(),0, 2,TimeUnit.SECONDS);
    }

    private void stopTicking() {
        if (tickTask != null) {
            tickTask.cancel(false);
        }
    }

    private void tick() {
        String roomId = room.getId();
        GameStatePayload payload = GameStatePayload.builder()
            .prompt(prompt)
            .playerProgress(playerProgress)
            .build();

        gameBroadcaster.broadcastGlobalGameState(roomId, payload);
    }

    @Override
    public void end() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'end'");
    }

    @Override
    public GameStatus getStatus() {
        return this.status;
        // // TODO Auto-generated method stub
        // throw new UnsupportedOperationException("Unimplemented method 'getStatus'");
    }

    @Override
    public Object getState() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getState'");
    }

    @Override
    public Room getRoom() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRoom'");
    }

    @Override
    public boolean handleAction(String username, Object actionData) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleAction'");
    }

    @Override
    public void addPlayer (String username) {
        this.playerProgress.put(username, 0);
    }
}
