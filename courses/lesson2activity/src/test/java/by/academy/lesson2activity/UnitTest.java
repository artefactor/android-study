package by.academy.lesson2activity;

import android.content.Intent;

import org.junit.Test;

import static by.academy.lesson2activity.model.Extras.EXTRA_OPERATION;
import static by.academy.lesson2activity.model.Extras.EXTRA_RESULT;
import static by.academy.lesson2activity.model.Extras.EXTRA_OPERATION_VALUE_STRANGE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UnitTest {
    @Test
    public void strangeOperationIsCorrect() {
        SecondActivity activity = new SecondActivity();
        int[] numbers = new int[]{100, 20, 30, 40, 50};
        Intent answerIntent = mock(Intent.class);
        Intent answerStrangeIntent = activity.getAnswerStrangeIntent(numbers, answerIntent);
        Double expected = (100.0 + 20 + 30) / (-40 - 50);
        verify(answerIntent).putExtra(EXTRA_OPERATION, EXTRA_OPERATION_VALUE_STRANGE);
        verify(answerIntent).putExtra(EXTRA_RESULT, expected);
    }

    @Test
    public void strangeOperationIsCorrect2() {
        SecondActivity activity = new SecondActivity();
        int[] numbers = new int[]{2, 3, 5};
        Intent answerIntent = mock(Intent.class);
        Intent answerStrangeIntent = activity.getAnswerStrangeIntent(numbers, answerIntent);
        Double expected = (2.0 + 3) / (-5);
        verify(answerIntent).putExtra(EXTRA_OPERATION, EXTRA_OPERATION_VALUE_STRANGE);
        verify(answerIntent).putExtra(EXTRA_RESULT, expected);
    }
}
