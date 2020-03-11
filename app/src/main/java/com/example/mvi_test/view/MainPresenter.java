package com.example.mvi_test.view;

import com.example.mvi_test.Utils.DataSource;
import com.example.mvi_test.model.MainView;
import com.example.mvi_test.model.PartialMainState;
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public class MainPresenter extends MviBasePresenter<MainView, MainViewState> {

    // DataSource to control data flow
    private DataSource dataSource;

    public MainPresenter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    protected void bindIntents() {
        Observable<PartialMainState> gotData = intent(MainView::getImageIntent)
                .switchMap(index -> dataSource.getImageLinkFromList(index))
                .map(imageLink -> (PartialMainState) new PartialMainState.GotImageLink(imageLink))
                .startWith(new PartialMainState.Loading())
                .onErrorReturn(PartialMainState.Error::new)
                .subscribeOn(Schedulers.io());

        MainViewState initState = new MainViewState(false,false,"",null);

        Observable<PartialMainState> initIntent = gotData.observeOn(AndroidSchedulers.mainThread());

        subscribeViewState(initIntent.scan(initState, this::viewStateReducer), MainView::render);
    }

    // After getting changes in view state,
    // it’s time for method viewStateReducer to apply the changes to previously used view state
    // and for sending it to our Activity via it’s render() method.
    private MainViewState viewStateReducer(MainViewState prevState, PartialMainState changedState) {

        if (changedState instanceof PartialMainState.Loading) {
            prevState.isLoading = true;
            prevState.isImageViewVisible = false;
        }

        if (changedState instanceof PartialMainState.GotImageLink) {
            prevState.isLoading = false;
            prevState.isImageViewVisible = true;
            prevState.imageLink = ((PartialMainState.GotImageLink) changedState).imageLink;
        }

        if (changedState instanceof PartialMainState.Error) {
            prevState.isLoading = true;
            prevState.isImageViewVisible = false;
            prevState.error = ((PartialMainState.Error) changedState).error;
        }

        return prevState;
    }

}
