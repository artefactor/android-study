package by.academy.lesson2activity.model;

public abstract class LogicOperation<T extends Number> {
    private final String operationName;

    public LogicOperation(String operationName) {
        this.operationName = operationName;
    }

    public String getOperationName() {
        return operationName;
    }

    public abstract LogicOperationResult<T> calculate(int[] numbers);


}
