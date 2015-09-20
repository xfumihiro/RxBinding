package com.jakewharton.rxbinding.view;

import android.view.DragEvent;
import android.view.View;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.weaving.annotation.Exclusive;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

@Exclusive
final class ViewDragOnSubscribe implements Observable.OnSubscribe<DragEvent> {
  private final View view;
  private final Func1<? super DragEvent, Boolean> handled;

  ViewDragOnSubscribe(View view, Func1<? super DragEvent, Boolean> handled) {
    this.view = view;
    this.handled = handled;
  }

  @Override public void call(final Subscriber<? super DragEvent> subscriber) {
    checkUiThread();

    View.OnDragListener listener = new View.OnDragListener() {
      @Override public boolean onDrag(View v, DragEvent event) {
        if (handled.call(event)) {
          if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(event);
          }
          return true;
        }
        return false;
      }
    };
    view.setOnDragListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setOnDragListener(null);
      }
    });
  }
}
