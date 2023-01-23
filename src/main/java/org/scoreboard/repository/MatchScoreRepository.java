package org.scoreboard.repository;

import org.scoreboard.model.MatchScore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MatchScoreRepository {

    private final List<MatchScore> matchScoreDatabase = new ArrayList<>();

    public MatchScore save(MatchScore matchScore) {
        matchScoreDatabase.removeIf(matchScore::hasSameBusinessKey);
        matchScoreDatabase.add(matchScore);
        return matchScore;
    }

    public Optional<MatchScore> find(String homeTeamName, String awayTeamName, LocalDate matchDate) {
        MatchScore matchScoreExample = new MatchScore(homeTeamName, awayTeamName, matchDate);
        return findByBusinessKey(matchScoreExample);
    }

    public List<MatchScore> findAllUnfinishedMatchScoresSorted() {
        return matchScoreDatabase.stream()
                .filter(Predicate.not(MatchScore::isFinished))
                .sorted()
                .collect(Collectors.toList());
    }

    private Optional<MatchScore> findByBusinessKey(MatchScore matchScoreExample) {
        return matchScoreDatabase.stream()
                .filter(matchScoreExample::hasSameBusinessKey)
                .findAny();
    }

}
