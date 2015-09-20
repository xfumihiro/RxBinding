package com.jakewharton.rxbinding.view;

import android.view.View;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.weaving.annotation.Exclusive;
import rx.Observable;
import rx.Subscriber;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

@Exclusive
final class ViewSystemUiVisibilityChangeEventOnSubscribe
    implements Observable.OnSubscribe<ViewSystemUiVisibilityChangeEvent> {
  private final View view;

  ViewSystemUiVisibilityChangeEventOnSubscribe(View view) {
    this.view = view;
  }

  @Override public void call(final Subscriber<? super ViewSystemUiVisibilityChangeEvent> subscriber) {
    checkUiThread();

    final View.OnSystemUiVisibilityChangeListener listener =
        new View.OnSystemUiVisibilityChangeListener() {
          @Override public void onSystemUiVisibilityChange(int visibility) {
            if (!subscriber.isUnsubscribed()) {
              subscriber.onNext(ViewSystemUiVisibilityChangeEvent.create(view, visibility));
            }
          }
        };
    view.setOnSystemUiVisibilityChangeListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setOnSystemUiVisibilityChangeListener(null);
      }
    });
  }
}
