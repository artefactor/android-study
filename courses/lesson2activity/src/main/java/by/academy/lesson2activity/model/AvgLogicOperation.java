package by.academy.lesson2activity.model;

import static by.academy.lesson2activity.model.Extras.EXTRA_TYPE_VALUE_DOUBLE;
import static by.academy.lesson2activity.model.Extras.EXTRA_OPERATION_VALUE_AVG;

public class AvgLogicOperation extends LogicOperation<Double> {

    public AvgLogicOperation() {
        super(EXTRA_OPERATION_VALUE_AVG);
    }

    public LogicOperationResult<Double> calculate(int[] numbers) {
        int length = numbers.length;
        if (length <= 0) {
            return new LogicOperationResult<>("empty data");
        }

        long sum = 0;
        for (int number : numbers) {
            sum += number;
        }


        double asDouble = sum / (double)numbers.length;
        return new LogicOperationResult<>(EXTRA_TYPE_VALUE_DOUBLE, asDouble);
    }

}
