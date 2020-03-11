package com.example.mvi_test.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import com.example.mvi_test.R;
import com.example.mvi_test.Utils.DataSource;
import com.example.mvi_test.model.MainView;
import com.hannesdorfmann.mosby3.mvi.MviActivity;
import com.jakewharton.rxbinding3.view.RxView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;

public class MainActivity extends MviActivity<MainView, MainPresenter> implements MainView {

    ImageView imageView;
    Button button;
    ProgressBar progressBar;

    List<String> imageList;

    private Boolean testParameter = false;

    private CompositeDisposable disposables = new CompositeDisposable();

    private BehaviorSubject<Boolean> testParameterSubject = BehaviorSubject.create();

    public Observable<Boolean> getSubjectObservable() {
        return this.testParameterSubject;
    }
    public void setSubjectObservable(Boolean testParameter) {
        testParameterSubject.onNext(testParameter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();

        setObserver();
    }

    private void setObserver() {
        this.disposables.add(this.
                getSubjectObservable().
                observeOn(AndroidSchedulers.mainThread()).
                distinctUntilChanged().
                subscribe(testParameter -> this.observationFetched(testParameter)));
    }

    public void observationFetched(Boolean testParameter) {
        if (testParameter) {
            button.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        else {
            button.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.dispose();
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return new MainPresenter(new DataSource(imageList));
    }

    @Override
    public Observable<Integer> getImageIntent() {
        return RxView.clicks(button)
                .map(click -> getRandomImageFromList(0, imageList.size()-1));
    }

    @Override
    public void render(MainViewState viewState) {
        // process change state to display view

        if (viewState.isLoading) {
            Log.d("viewState", "isLoading");
            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            button.setEnabled(true);
        }
        else if (viewState.isImageViewVisible) {
            Log.d("viewState", "isImageViewVisible");
            button.setEnabled(true);

            Picasso.get().load(viewState.imageLink).fetch(new Callback() {
                @Override
                public void onSuccess() {
                    imageView.setAlpha(0f);
                    Picasso.get().load(viewState.imageLink).into(imageView);
                    imageView.animate().setDuration(300).alpha(1f).start();

                    imageView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    testParameter = !testParameter;
                    setSubjectObservable(testParameter);
                    Log.d("testParameter", "testParameter " + testParameter);
                }

                @Override
                public void onError(Exception e) {
                    imageView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        else if (viewState.error != null) {
            Log.d("viewState", "error");
            progressBar.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            button.setEnabled(true);
        }
    }

    private Integer getRandomImageFromList(Integer min, Integer max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min!");
        }
        Random random = new Random();
        Integer randomNumber = random.nextInt((max - min) + 1) + min;
        return randomNumber;
    }

    private void initComponents() {
        button = findViewById(R.id.button);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        imageList = createImageList();
    }

    private List<String> createImageList() {
        return Arrays.asList(
                "https://i.pinimg.com/originals/2c/47/15/2c471543f4a7b174a520be05badd6ea4.jpg",
                "https://i.pinimg.com/474x/9c/c2/44/9cc244f56023cf4adbb6ea2ba4a672fa--mobile-wallpaper-wallpaper-desktop.jpg",
                "https://www.itl.cat/pngfile/big/85-854329_animal-cat-mobile-wallpaper-cat-desktop-wallpaper-1080.jpg",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcQwzhXd0jiQoDHfTy1_zt13yTryr40Isx4g_yH6R8hcZw_peZql",
                "https://i.pinimg.com/originals/65/5c/29/655c2956ea755119b8d91f009e4e464d.png",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTi1LYyrKn2XSYXLWRqM7YxcjGVQSUnroRa2wFyN_MrecEC4vQO",
                "https://i.pinimg.com/originals/d7/6b/d0/d76bd044a56784e422a96237f3318894.jpg"
        );
    }
}
