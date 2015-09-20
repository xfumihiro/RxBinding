package com.jakewharton.rxbinding.widget;

import android.view.KeyEvent;
import android.widget.TextView;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.weaving.annotation.Exclusive;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

@Exclusive
final class TextViewEditorActionOnSubscribe implements Observable.OnSubscribe<Integer> {
  private final TextView view;
  private final Func1<? super Integer, Boolean> handled;

  public TextViewEditorActionOnSubscribe(TextView view, Func1<? super Integer, Boolean> handled) {
    this.view = view;
    this.handled = handled;
  }

  @Override public void call(final Subscriber<? super Integer> subscriber) {
    checkUiThread();

    TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (handled.call(actionId)) {
          if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(actionId);
          }
          return true;
        }
        return false;
      }
    };
    view.setOnEditorActionListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setOnEditorActionListener(null);
      }
    });
  }
}
