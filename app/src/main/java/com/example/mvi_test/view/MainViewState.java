package com.example.mvi_test.view;

public class MainViewState {

    boolean isLoading;
    boolean isImageViewVisible;
    String imageLink;
    Throwable error;

    public MainViewState(boolean isLoading, boolean isImageViewVisible, String imageLink, Throwable error) {
        this.isLoading = isLoading;
        this.isImageViewVisible = isImageViewVisible;
        this.imageLink = imageLink;
        this.error = error;
    }

}
