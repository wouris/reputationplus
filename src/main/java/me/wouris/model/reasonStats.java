package me.wouris.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class reasonStats {

    private final String target;
    private final String rater;
    private final String reason;
    private final String decision;
    private final Timestamp date;

    public reasonStats(String target, String rater, String decision, String reason, Timestamp date) {
        this.target = target;
        this.rater = rater;
        this.decision = decision;
        this.reason = reason;
        this.date = date;
    }

    public String getDecision() { return decision; }
    public String getTarget() {
        return target;
    }
    public String getRater() {
        return rater;
    }
    public String getReason() {
        return reason;
    }
    public Timestamp getDate() {
        return date;
    }

}
