package com.jakewharton.rxbinding.widget;

import android.widget.SearchView;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.weaving.annotation.Exclusive;
import rx.Observable;
import rx.Subscriber;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

@Exclusive
final class SearchViewQueryTextChangeEventsOnSubscribe
    implements Observable.OnSubscribe<SearchViewQueryTextEvent> {
  private final SearchView view;

  SearchViewQueryTextChangeEventsOnSubscribe(SearchView view) {
    this.view = view;
  }

  @Override public void call(final Subscriber<? super SearchViewQueryTextEvent> subscriber) {
    checkUiThread();

    SearchView.OnQueryTextListener watcher = new SearchView.OnQueryTextListener() {
      @Override public boolean onQueryTextChange(String s) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(SearchViewQueryTextEvent.create(view, s, false));
          return true;
        }
        return false;
      }

      @Override public boolean onQueryTextSubmit(String query) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(SearchViewQueryTextEvent.create(view, view.getQuery(), true));
          return true;
        }
        return false;
      }
    };

    view.setOnQueryTextListener(watcher);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setOnQueryTextListener(null);
      }
    });

    // Emit initial value.
    subscriber.onNext(SearchViewQueryTextEvent.create(view, view.getQuery(), false));
  }
}
