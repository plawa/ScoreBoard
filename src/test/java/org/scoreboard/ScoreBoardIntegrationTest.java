package org.scoreboard;

import org.junit.jupiter.api.Test;
import org.scoreboard.model.MatchScore;
import org.scoreboard.service.ScoreBoardService;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ScoreBoardIntegrationTest {

    private final ScoreBoardService service = new ScoreBoardService();

    @Test
    void testGetLiveScoreBoard() {
        final LocalDate matchesDate = LocalDate.now();

        service.startMatch("Mexico", "Canada");
        service.startMatch("Spain", "Brazil");
        service.startMatch("Germany", "France");
        service.startMatch("Uruguay", "Italy");
        service.startMatch("Argentina", "Australia");

        assertEquals(5, service.getLiveScoreBoard().size());

        //shoot some goals
        service.updateMatchScore("Mexico", "Canada", matchesDate, 1, 0);
        service.updateMatchScore("Germany", "France", matchesDate, 0, 2);
        service.updateMatchScore("Uruguay", "Italy", matchesDate, 0, 1);
        service.updateMatchScore("Argentina", "Australia", matchesDate, 2, 2);
        List<MatchScore> liveScoreBoard = service.getLiveScoreBoard();
        assertEquals(5, liveScoreBoard.size());

        //validate order
        assertEquals("Argentina", liveScoreBoard.get(0).homeTeamName());
        assertEquals("Germany", liveScoreBoard.get(1).homeTeamName());
        assertEquals("Mexico", liveScoreBoard.get(2).homeTeamName());
        assertEquals("Uruguay", liveScoreBoard.get(3).homeTeamName());
        assertEquals("Spain", liveScoreBoard.get(4).homeTeamName());

        //finish some matches
        service.finishMatch("Uruguay", "Italy", matchesDate);
        assertEquals(4, service.getLiveScoreBoard().size());
    }

    @Test
    void testStartMatch() {
        final String homeTeamName = "Mexico";
        final String awayTeamName = "Canada";

        MatchScore matchScore = service.startMatch(homeTeamName, awayTeamName);

        assertNotNull(matchScore);
        assertEquals(homeTeamName, matchScore.homeTeamName());
        assertEquals(awayTeamName, matchScore.awayTeamName());
        assertEquals(0, matchScore.homeTeamScore());
        assertEquals(0, matchScore.awayTeamScore());
        assertEquals(LocalDate.now(), matchScore.playedOn());
        assertFalse(matchScore.isFinished());
    }

    @Test
    void testFinishMatch() {
        final String homeTeamName = "Mexico";
        final String awayTeamName = "Canada";

        MatchScore matchScore = service.startMatch(homeTeamName, awayTeamName);

        MatchScore finishedMatch = service.finishMatch(homeTeamName, awayTeamName, matchScore.playedOn());
        assertNotNull(finishedMatch);
        assertEquals(homeTeamName, finishedMatch.homeTeamName());
        assertEquals(awayTeamName, finishedMatch.awayTeamName());
        assertEquals(0, finishedMatch.homeTeamScore());
        assertEquals(0, finishedMatch.awayTeamScore());
        assertTrue(finishedMatch.isFinished());
    }

    @Test
    void testFinishMatch_doesNotExistScenario() {
        final String homeTeamName = "Mexico";
        final String awayTeamName = "Canada";

        MatchScore matchScore = service.startMatch(homeTeamName, awayTeamName);

        final String changedHomeTeamName = "Argentina";

        assertThrows(NoSuchElementException.class,
                () -> service.finishMatch(changedHomeTeamName, awayTeamName, matchScore.playedOn()));
    }

    @Test
    void testFinishMatch_alreadyFinishedScenario() {
        final String homeTeamName = "Mexico";
        final String awayTeamName = "Canada";

        MatchScore matchScore = service.startMatch(homeTeamName, awayTeamName);

        service.finishMatch(homeTeamName, awayTeamName, matchScore.playedOn());

        assertThrows(IllegalStateException.class,
                () -> service.finishMatch(homeTeamName, awayTeamName, matchScore.playedOn()));
    }

    @Test
    void testUpdateMatch() {
        final String homeTeamName = "Mexico";
        final String awayTeamName = "Canada";

        MatchScore matchScore = service.startMatch(homeTeamName, awayTeamName);

        //Mexico scores
        MatchScore updatedMatchScore = service.updateMatchScore(homeTeamName, awayTeamName, matchScore.playedOn(), 1, 0);

        assertNotNull(updatedMatchScore);
        assertEquals(homeTeamName, updatedMatchScore.homeTeamName());
        assertEquals(awayTeamName, updatedMatchScore.awayTeamName());
        assertEquals(1, updatedMatchScore.homeTeamScore());
        assertEquals(0, updatedMatchScore.awayTeamScore());
        assertEquals(LocalDate.now(), updatedMatchScore.playedOn());
        assertFalse(updatedMatchScore.isFinished());
    }

}