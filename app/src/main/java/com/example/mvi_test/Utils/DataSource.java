package com.example.mvi_test.Utils;

import java.util.List;

import io.reactivex.Observable;

public class DataSource {

    private List<String> imageList;

    public DataSource(List<String> imageList) {
        this.imageList = imageList;
    }

    public Observable<String> getImageLinkFromList(int index) {
        return Observable.just(imageList.get(index));
    }

}
