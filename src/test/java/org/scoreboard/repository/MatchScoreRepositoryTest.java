package org.scoreboard.repository;

import org.junit.jupiter.api.Test;
import org.scoreboard.model.MatchScore;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class MatchScoreRepositoryTest {

    private final MatchScoreRepository repository = new MatchScoreRepository();

    @Test
    void testSaveAndFindScenario() {
        /* Save scenario*/
        //given
        MatchScore matchScore = new MatchScore("Mexico", "Canada");

        //when
        MatchScore matchScoreSaved = repository.save(matchScore);

        //then
        assertEquals("Mexico", matchScoreSaved.homeTeamName());
        assertEquals("Canada", matchScoreSaved.awayTeamName());
        assertEquals(0, matchScoreSaved.homeTeamScore());
        assertEquals(0, matchScoreSaved.awayTeamScore());
        assertFalse(matchScoreSaved.isFinished());
        assertEquals(LocalDate.now(), matchScoreSaved.playedOn());

        /* Find scenario */
        //given
        MatchScore unwantedMatchScore = new MatchScore("France", "Germany");
        repository.save(unwantedMatchScore);

        //when
        Optional<MatchScore> matchScoreOptional = repository.find("Mexico", "Canada", LocalDate.now());

        //then
        assertTrue(matchScoreOptional.isPresent());
        MatchScore matchScoreFound = matchScoreOptional.get();
        assertEquals("Mexico", matchScoreFound.homeTeamName());
        assertEquals("Canada", matchScoreFound.awayTeamName());
        assertEquals(0, matchScoreFound.homeTeamScore());
        assertEquals(0, matchScoreFound.awayTeamScore());
        assertFalse(matchScoreFound.isFinished());
        assertEquals(LocalDate.now(), matchScoreFound.playedOn());
    }

    @Test
    void testFind_differentMatchDateScenario() {
        //given
        MatchScore matchScoreInThePast = new MatchScore("Mexico", "Canada", LocalDate.now().minusDays(3L));
        repository.save(matchScoreInThePast);

        //when
        Optional<MatchScore> matchScoreOptional = repository.find("Mexico", "Canada", LocalDate.now());

        //then
        assertTrue(matchScoreOptional.isEmpty());
    }


    @Test
    void testFindUnfinishedMatchesOnly() {
        /* No finished match scenario */
        //given
        MatchScore matchScore1 = new MatchScore("Mexico", "Canada");
        MatchScore matchScore2 = new MatchScore("Argentina", "Australia");
        MatchScore matchScore3 = new MatchScore("Poland", "Germany");
        repository.save(matchScore1);
        repository.save(matchScore2);
        repository.save(matchScore3);

        //when
        List<MatchScore> allUnfinishedMatchScoresSorted = repository.findAllUnfinishedMatchScoresSorted();

        //then
        assertEquals(3, allUnfinishedMatchScoresSorted.size());

        /* One finished match scenario */
        //given
        repository.save(matchScore2.withIsFinishedFlag(true));

        //when
        allUnfinishedMatchScoresSorted = repository.findAllUnfinishedMatchScoresSorted();

        //then
        assertEquals(2, allUnfinishedMatchScoresSorted.size());

        /* All matches finished scenario */
        //given
        repository.save(matchScore1.withIsFinishedFlag(true));
        repository.save(matchScore3.withIsFinishedFlag(true));

        //when
        allUnfinishedMatchScoresSorted = repository.findAllUnfinishedMatchScoresSorted();

        //then
        assertTrue(allUnfinishedMatchScoresSorted.isEmpty());
    }

    @Test
    void testFindUnfinishedMatchesSortedByScore() {
        //given
        MatchScore matchScore1 = new MatchScore("Mexico", "Canada").withHomeTeamScore(1).withAwayTeamScore(2);
        MatchScore matchScore2 = new MatchScore("Argentina", "Australia").withHomeTeamScore(2).withAwayTeamScore(3);
        MatchScore matchScore3 = new MatchScore("Poland", "Germany").withHomeTeamScore(2).withAwayTeamScore(1);
        MatchScore matchScore4 = new MatchScore("Spain", "Brazil").withHomeTeamScore(1).withAwayTeamScore(0);
        MatchScore matchScore5 = new MatchScore("Italy", "Croatia").withHomeTeamScore(3).withAwayTeamScore(2);
        repository.save(matchScore1);
        repository.save(matchScore2);
        repository.save(matchScore3);
        repository.save(matchScore4);
        repository.save(matchScore5);

        //when
        List<MatchScore> scoreList = repository.findAllUnfinishedMatchScoresSorted();

        //then
        assertEquals(5, scoreList.size());
        assertEquals(List.of(matchScore2, matchScore5, matchScore1, matchScore3, matchScore4), scoreList); //assert elements order
    }

}