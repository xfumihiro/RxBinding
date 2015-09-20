package com.jakewharton.rxbinding.support.v4.widget;

import android.support.v4.widget.SwipeRefreshLayout;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.weaving.annotation.Exclusive;
import rx.Observable;
import rx.Subscriber;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

@Exclusive
final class SwipeRefreshLayoutRefreshOnSubscribe implements Observable.OnSubscribe<Void> {
  private final SwipeRefreshLayout view;

  SwipeRefreshLayoutRefreshOnSubscribe(SwipeRefreshLayout view) {
    this.view = view;
  }

  @Override public void call(final Subscriber<? super Void> subscriber) {
    checkUiThread();

    SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        subscriber.onNext(null);
      }
    };
    view.setOnRefreshListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setOnRefreshListener(null);
      }
    });
  }
}
