package me.wouris.model;

import java.util.UUID;

public class voteStats {

    private final UUID player;
    private final UUID target;
    private int votes;

    public voteStats(UUID player, UUID target, int votes) {
        this.player = player;
        this.target = target;
        this.votes = votes;
    }

    public UUID getPlayer() {
        return player;
    }

    public UUID getTarget() { return target; }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
