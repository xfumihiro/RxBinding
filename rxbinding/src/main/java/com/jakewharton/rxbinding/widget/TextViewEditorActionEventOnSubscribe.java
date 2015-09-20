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
final class TextViewEditorActionEventOnSubscribe
    implements Observable.OnSubscribe<TextViewEditorActionEvent> {
  private final TextView view;
  private final Func1<? super TextViewEditorActionEvent, Boolean> handled;

  public TextViewEditorActionEventOnSubscribe(TextView view,
      Func1<? super TextViewEditorActionEvent, Boolean> handled) {
    this.view = view;
    this.handled = handled;
  }

  @Override public void call(final Subscriber<? super TextViewEditorActionEvent> subscriber) {
    checkUiThread();

    TextView.OnEditorActionListener listener = new TextView.OnEditorActionListener() {
      @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
        TextViewEditorActionEvent event = TextViewEditorActionEvent.create(v, actionId, keyEvent);
        if (handled.call(event)) {
          if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(event);
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
