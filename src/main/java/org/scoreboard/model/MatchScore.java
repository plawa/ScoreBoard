package org.scoreboard.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MatchScore(String homeTeamName,
                         String awayTeamName,
                         LocalDateTime addedOn,
                         LocalDate playedOn,
                         int homeTeamScore,
                         int awayTeamScore,
                         boolean isFinished) implements BusinessKeyProvidingEntity, Comparable<MatchScore> {

    public MatchScore(String homeTeamName, String awayTeamName) {
        this(homeTeamName, awayTeamName, LocalDateTime.now(), LocalDate.now(), 0, 0, false);
    }

    public MatchScore(String homeTeamName, String awayTeamName, LocalDate playedOn) {
        this(homeTeamName, awayTeamName, LocalDateTime.now(), playedOn, 0, 0, false);
    }

    public MatchScore withHomeTeamScore(int newHomeTeamScore) {
        return new MatchScore(homeTeamName(), awayTeamName(), addedOn(), playedOn(), newHomeTeamScore, awayTeamScore(), isFinished());
    }

    public MatchScore withAwayTeamScore(int newAwayTeamScore) {
        return new MatchScore(homeTeamName(), awayTeamName(), addedOn(), playedOn(), homeTeamScore(), newAwayTeamScore, isFinished());
    }

    public MatchScore withIsFinishedFlag(boolean newIsFinishedFlag) {
        return new MatchScore(homeTeamName(), awayTeamName(), addedOn(), playedOn(), homeTeamScore(), awayTeamScore(), newIsFinishedFlag);
    }

    @Override
    public Object[] getBusinessKey() {
        return new Object[]{homeTeamName(), awayTeamName(), playedOn()};
    }

    @Override
    public int compareTo(MatchScore otherObject) {
        int thisMatchTotalGoals = homeTeamScore() + awayTeamScore();
        int otherMatchTotalGoals = otherObject.homeTeamScore() + otherObject.awayTeamScore();
        if (thisMatchTotalGoals == otherMatchTotalGoals) {
            return addedOn().compareTo(otherObject.addedOn());
        }
        return thisMatchTotalGoals < otherMatchTotalGoals ? 1 : -1;
    }

}
