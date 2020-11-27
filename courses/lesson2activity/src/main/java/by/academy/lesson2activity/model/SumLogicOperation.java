package by.academy.lesson2activity.model;

import static by.academy.lesson2activity.model.Extras.EXTRA_TYPE_VALUE_LONG;
import static by.academy.lesson2activity.model.Extras.EXTRA_OPERATION_VALUE_SUM;

public class SumLogicOperation extends LogicOperation<Long> {

    public SumLogicOperation() {
        super(EXTRA_OPERATION_VALUE_SUM);
    }

    public LogicOperationResult<Long> calculate(int[] numbers) {

        long sum = 0;
        for (int number : numbers) {
            sum += number;
        }

        return new LogicOperationResult<>(EXTRA_TYPE_VALUE_LONG, sum);
    }

}
