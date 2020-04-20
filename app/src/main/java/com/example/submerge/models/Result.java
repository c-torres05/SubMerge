package com.example.submerge.models;

public final class Result<SuccessTypeT, FailureTypeT> {
    private final SuccessTypeT result;
    private final FailureTypeT failureResult;
    private final boolean isSuccessful;

    public Result(
            final SuccessTypeT result, final FailureTypeT failureResult, final boolean isSuccessful) {
        this.result = result;
        this.failureResult = failureResult;
        this.isSuccessful = isSuccessful;
    }

    public static <T, U> Result<T, U> successfulResultOf(final T value) {
        return new Result<>(value, null, true);
    }

    public static <T, U> Result<T, U> failedResultOf(final U value) {
        return new Result<>(null, value, false);
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    /**
     * Gets the result of the operation, if successful.
     *
     * @return The result of the operation.
     */
    public SuccessTypeT getResult() {
        if (!isSuccessful) {
            throw new IllegalStateException("operation was failed, not successful");
        }
        return result;
    }

    /**
     * Gets the failure reason for the operation, if it failed.
     *
     * @return The failure reason of the operation.
     */
    public FailureTypeT getFailure() {
        if (isSuccessful) {
            throw new IllegalStateException("operation was successful, not failed");
        }
        return failureResult;
    }
}
