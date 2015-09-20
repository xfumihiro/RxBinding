package com.jakewharton.rxbinding.view;

import android.annotation.TargetApi;
import android.view.View;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.weaving.annotation.Exclusive;
import rx.Observable;
import rx.Subscriber;

import static android.os.Build.VERSION_CODES.M;
import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

@Exclusive
@TargetApi(M)
final class ViewScrollChangeEventOnSubscribe implements Observable.OnSubscribe<ViewScrollChangeEvent> {
  private final View view;

  ViewScrollChangeEventOnSubscribe(View view) {
    this.view = view;
  }

  @Override public void call(final Subscriber<? super ViewScrollChangeEvent> subscriber) {
    checkUiThread();

    final View.OnScrollChangeListener listener = new View.OnScrollChangeListener() {
      @Override
      public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(ViewScrollChangeEvent.create(view, scrollX, scrollY, oldScrollX, oldScrollY));
        }
      }
    };
    view.setOnScrollChangeListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setOnScrollChangeListener(null);
      }
    });
  }
}
