package com.hfad2.projectmanagmentapplication.repositories;

// Callback interface
public interface OperationCallback<T> {
    void onSuccess(T result);

    void onError(String error);
}
