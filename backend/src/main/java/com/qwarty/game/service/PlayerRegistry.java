package com.qwarty.game.service;

import com.qwarty.auth.lov.UserType;
import com.qwarty.game.model.PlayerInfo;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class PlayerRegistry {

    private final Map<String, PlayerInfo> players = new ConcurrentHashMap<>();

    public void register(String sessionUid, String username, UserType userType) {
        players.put(sessionUid, new PlayerInfo(sessionUid, username, userType));
    }

    public void unregister(String sessionUid) {
        players.remove(sessionUid);
    }

    public PlayerInfo get(String sessionUid) {
        return players.get(sessionUid);
    }

    public Collection<PlayerInfo> getAll(Collection<String> sessionUids) {
        return sessionUids.stream().map(players::get).filter(Objects::nonNull).toList();
    }
}
