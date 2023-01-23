package org.scoreboard.service;

import org.scoreboard.model.MatchScore;
import org.scoreboard.repository.MatchScoreRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

public class ScoreBoardService {

    public ScoreBoardService(MatchScoreRepository repository) {
        this.repository = repository;
    }

    public ScoreBoardService() {
        this(new MatchScoreRepository());
    }

    private final MatchScoreRepository repository;

    public List<MatchScore> getLiveScoreBoard() {
        return repository.findAllUnfinishedMatchScoresSorted();
    }

    public MatchScore startMatch(String homeTeamName, String awayTeamName) {
        MatchScore matchScore = new MatchScore(homeTeamName, awayTeamName);
        return repository.save(matchScore);
    }

    public MatchScore updateMatchScore(String homeTeamName, String awayTeamName, LocalDate matchDate, int homeTeamScore, int awayTeamScore) {
        MatchScore matchScore = getExistingMatchScore(homeTeamName, awayTeamName, matchDate);
        MatchScore matchScoreToUpdate = matchScore
                .withHomeTeamScore(homeTeamScore)
                .withAwayTeamScore(awayTeamScore);
        return repository.save(matchScoreToUpdate);
    }

    public MatchScore finishMatch(String homeTeamName, String awayTeamName, LocalDate matchDate) {
        MatchScore matchScoreFound = getValidatedMatchScoreToFinish(homeTeamName, awayTeamName, matchDate);
        MatchScore matchScoreToUpdate = matchScoreFound.withIsFinishedFlag(true);
        return repository.save(matchScoreToUpdate);
    }

    private MatchScore getValidatedMatchScoreToFinish(String homeTeamName, String awayTeamName, LocalDate matchDate) {
        MatchScore matchScore = getExistingMatchScore(homeTeamName, awayTeamName, matchDate);
        if (matchScore.isFinished()) {
            throw new IllegalStateException("Cannot finish match that is already finished!");
        }
        return matchScore;
    }

    private MatchScore getExistingMatchScore(String homeTeamName, String awayTeamName, LocalDate matchDate) {
        return repository.find(homeTeamName, awayTeamName, matchDate)
                .orElseThrow(() -> new NoSuchElementException("Match for given teams and date does not exist!"));
    }

}
