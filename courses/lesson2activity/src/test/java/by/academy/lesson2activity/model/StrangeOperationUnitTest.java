package by.academy.lesson2activity.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StrangeOperationUnitTest {

    @Test
    public void strangeOperationIsCorrect2() {
        StrangeLogicOperation operation = new StrangeLogicOperation();
        int[] numbers = new int[]{2, 3, 5};
        LogicOperationResult<Double> answerStrangeIntent = operation.calculate(numbers);
        Double expected = (2.0 + 3) / (-5);
        assertEquals(expected, answerStrangeIntent.getResult());
    }
}
