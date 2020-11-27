package by.academy.lesson2activity.model;

import static by.academy.lesson2activity.model.Extras.EXTRA_TYPE_VALUE_DOUBLE;
import static by.academy.lesson2activity.model.Extras.EXTRA_OPERATION_VALUE_STRANGE;

public class StrangeLogicOperation extends LogicOperation<Double> {

    public StrangeLogicOperation() {
        super(EXTRA_OPERATION_VALUE_STRANGE);
    }

    public LogicOperationResult<Double> calculate(int[] numbers) {
        int length = numbers.length;
        if (length <= 0) {
            return new LogicOperationResult<>("empty data");
        }
        double strangeResult = getStrangeResult(numbers, length);
        return new LogicOperationResult<>(EXTRA_TYPE_VALUE_DOUBLE, strangeResult);
    }

    private double getStrangeResult(int[] numbers, int length) {
        int halfLength = length / 2;

        int sum = 0;
        int i;
        for (i = 0; i <= halfLength; i++) {
            sum += numbers[i];
        }

        int subtract = 0;
        for (; i < length; i++) {
            subtract -= numbers[i];
        }

        return (double) sum / subtract;
    }
}
