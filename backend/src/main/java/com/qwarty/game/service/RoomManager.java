package com.qwarty.game.service;

import com.qwarty.auth.lov.UserType;
import com.qwarty.exception.code.AppExceptionCode;
import com.qwarty.exception.type.AppException;
import com.qwarty.game.dto.PlayerInfoDTO;
import com.qwarty.game.dto.RoomDetailsDTO;
import com.qwarty.game.lov.GameMode;
import com.qwarty.game.model.PlayerInfo;
import com.qwarty.game.model.Room;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomManager {

    private final PlayerRegistry playerRegistry;

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public RoomDetailsDTO createRoom(GameMode mode, String sessionUid, String username, UserType userType) {
        if (sessionUid == null) {
            throw new AppException(AppExceptionCode.SESSION_UID_NOT_FOUND);
        }

        String roomId = generateUniqueRoomId();
        Room room = new Room(roomId, mode);

        registerPlayer(sessionUid, username, userType);
        room.addPlayer(sessionUid);

        rooms.put(roomId, room);
        return retrieveRoomDetails(roomId);
    }

    public RoomDetailsDTO joinRoom(String roomId, String sessionUid, String username, UserType userType) {
        if (sessionUid == null) {
            throw new AppException(AppExceptionCode.SESSION_UID_NOT_FOUND);
        }

        Room room = getRoom(roomId);
        if (room == null) {
            throw new AppException(AppExceptionCode.ROOM_NOT_FOUND);
        }
        if (room.hasPlayer(sessionUid)) {
            return retrieveRoomDetails(roomId);
        }
        if (room.isFull()) {
            throw new AppException(AppExceptionCode.ROOM_FULL);
        }

        registerPlayer(sessionUid, username, userType);
        room.addPlayer(sessionUid);
        return retrieveRoomDetails(roomId);
    }

    private RoomDetailsDTO retrieveRoomDetails(String roomId) {
        Room room = getRoom(roomId);
        if (room == null) {
            throw new AppException(AppExceptionCode.ROOM_NOT_FOUND);
        }

        GameMode gameMode = room.getGameMode();

        Collection<PlayerInfo> playerInfos = playerRegistry.getAll(room.getPlayers());
        Collection<PlayerInfoDTO> playerDTOs = playerInfos.stream()
                .map(playerInfo -> new PlayerInfoDTO(playerInfo.username(), playerInfo.userType()))
                .toList();

        return new RoomDetailsDTO(roomId, playerDTOs, gameMode);
    }

    /**
     * Registers a player as an active player in {@link #PlayerRegistry}
     */
    private void registerPlayer(String sessionUid, String username, UserType userType) {
        if (playerRegistry.get(sessionUid) == null) {
            playerRegistry.register(sessionUid, username, userType);
        }
    }

    private String generateUniqueRoomId() {
        String roomId;

        do {
            roomId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        } while (rooms.containsKey(roomId));

        return roomId;
    }
}
