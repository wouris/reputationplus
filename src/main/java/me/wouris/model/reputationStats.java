package me.wouris.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;

public class reputationStats {

    private final UUID uuid;
    private int reputation;
    private int votes;
    private Timestamp lastVote;

    // used only for top list because top list does not require other stats but these
    public reputationStats(UUID uuid, int reputation){
        this.uuid = uuid;
        this.reputation = reputation;
    }

    public reputationStats(UUID uuid, int reputation, int votes, Timestamp lastVote) {
        this.uuid = uuid;
        this.reputation = reputation;
        this.votes = votes;
        this.lastVote = lastVote;
    }

    public void setLastVote (Timestamp date) { this.lastVote = date; }

    public Timestamp getLastVote () { return lastVote; }

    public UUID getUUID() {
        return uuid;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
