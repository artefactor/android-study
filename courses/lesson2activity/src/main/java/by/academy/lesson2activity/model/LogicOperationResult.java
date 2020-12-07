package by.academy.lesson2activity.model;

public class LogicOperationResult<T extends Number> {

   private String error;
   private String resultType;
   private T result;

    public LogicOperationResult(String error) {
        this.error = error;
    }

    public LogicOperationResult(String resultType, T result) {
        this.resultType = resultType;
        this.result = result;
    }

    public boolean isError() {
        return error != null;
    }

    public String getError() {
        return error;
    }

    public T getResult() {
        return result;
    }

    public String getResultType() {
        return resultType;
    }
}
