package org.scoreboard.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.scoreboard.model.MatchScore;
import org.scoreboard.repository.MatchScoreRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoreBoardServiceTest {

    @Mock
    private MatchScoreRepository repositoryMock;
    @InjectMocks
    private ScoreBoardService service;

    @Test
    void testGetLiveScoreBoard() {
        //given
        MatchScore matchScore1 = new MatchScore("Mexico", "Canada");
        MatchScore matchScore2 = new MatchScore("Poland", "Germany");
        when(repositoryMock.findAllUnfinishedMatchScoresSorted()).thenReturn(List.of(matchScore1, matchScore2));

        //when
        List<MatchScore> liveScoreBoard = service.getLiveScoreBoard();

        //then
        assertEquals(2, liveScoreBoard.size());
        verify(repositoryMock).findAllUnfinishedMatchScoresSorted();
    }

    @Test
    void testStartMatch() {
        //given
        final String homeTeamName = "Mexico";
        final String awayTeamName = "Canada";
        when(repositoryMock.save(any())).thenAnswer(i -> i.getArguments()[0]);

        //when
        MatchScore matchScore = service.startMatch(homeTeamName, awayTeamName);

        //then
        assertNotNull(matchScore);
        assertEquals(homeTeamName, matchScore.homeTeamName());
        assertEquals(awayTeamName, matchScore.awayTeamName());
        assertEquals(0, matchScore.homeTeamScore());
        assertEquals(0, matchScore.awayTeamScore());
        assertEquals(LocalDate.now(), matchScore.playedOn());
        assertFalse(matchScore.isFinished());
        verify(repositoryMock).save(matchScore);
    }

    @Test
    void testFinishMatch() {
        //given
        final String homeTeamName = "Mexico";
        final String awayTeamName = "Canada";
        final LocalDate playedOn = LocalDate.now();
        MatchScore matchScore = new MatchScore(homeTeamName, awayTeamName);
        when(repositoryMock.find(homeTeamName, awayTeamName, playedOn)).thenReturn(Optional.of(matchScore));
        when(repositoryMock.save(any())).thenAnswer(i -> i.getArguments()[0]);

        //when
        MatchScore finishedMatch = service.finishMatch(homeTeamName, awayTeamName, playedOn);

        //then
        assertNotNull(finishedMatch);
        assertEquals(homeTeamName, finishedMatch.homeTeamName());
        assertEquals(awayTeamName, finishedMatch.awayTeamName());
        assertEquals(0, finishedMatch.homeTeamScore());
        assertEquals(0, finishedMatch.awayTeamScore());
        assertTrue(finishedMatch.isFinished());
    }

    @Test
    void testFinishMatch_matchDoesNotExistScenario() {
        //given
        final String homeTeamName = "Mexico";
        final String awayTeamName = "Canada";
        final LocalDate playedOn = LocalDate.now();

        //when - then
        assertThrows(NoSuchElementException.class,
                () -> service.finishMatch(homeTeamName, awayTeamName, playedOn));
    }

    @Test
    void testFinishMatch_alreadyFinishedScenario() {
        //given
        final String homeTeamName = "Mexico";
        final String awayTeamName = "Canada";
        final LocalDate playedOn = LocalDate.now();
        MatchScore matchScore = new MatchScore(homeTeamName, awayTeamName).withIsFinishedFlag(true);
        when(repositoryMock.find(homeTeamName, awayTeamName, playedOn)).thenReturn(Optional.of(matchScore));

        //when - then
        assertThrows(IllegalStateException.class,
                () -> service.finishMatch(homeTeamName, awayTeamName, playedOn));
        verify(repositoryMock, never()).save(matchScore);
    }

    @Test
    void testUpdateMatchScore() {
        //given
        final String homeTeamName = "Mexico";
        final String awayTeamName = "Canada";
        final LocalDate playedOn = LocalDate.now();
        MatchScore matchScore = new MatchScore(homeTeamName, awayTeamName);
        when(repositoryMock.find(homeTeamName, awayTeamName, playedOn)).thenReturn(Optional.of(matchScore));
        when(repositoryMock.save(any())).thenAnswer(i -> i.getArguments()[0]);

        //when
        MatchScore updatedMatchScore = service.updateMatchScore(homeTeamName, awayTeamName, playedOn, 1, 0);

        //then
        assertNotNull(updatedMatchScore);
        assertEquals(homeTeamName, updatedMatchScore.homeTeamName());
        assertEquals(awayTeamName, updatedMatchScore.awayTeamName());
        assertEquals(1, updatedMatchScore.homeTeamScore());
        assertEquals(0, updatedMatchScore.awayTeamScore());
        assertEquals(LocalDate.now(), updatedMatchScore.playedOn());
        assertFalse(updatedMatchScore.isFinished());
        verify(repositoryMock).save(updatedMatchScore);
    }

    @Test
    void testUpdateMatchScore_negativeGoalCountScenario() {
        //given
        final String homeTeamName = "Mexico";
        final String awayTeamName = "Canada";
        final LocalDate playedOn = LocalDate.now();

        //when
        assertThrows(IllegalArgumentException.class,
                () -> service.updateMatchScore(homeTeamName, awayTeamName, playedOn, -1, 0));

    }

}