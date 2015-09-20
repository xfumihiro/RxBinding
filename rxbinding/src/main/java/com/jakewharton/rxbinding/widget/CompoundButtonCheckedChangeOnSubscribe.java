package com.jakewharton.rxbinding.widget;

import android.widget.CompoundButton;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.weaving.annotation.Exclusive;
import rx.Observable;
import rx.Subscriber;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

@Exclusive
final class CompoundButtonCheckedChangeOnSubscribe implements Observable.OnSubscribe<Boolean> {
  private final CompoundButton view;

  public CompoundButtonCheckedChangeOnSubscribe(CompoundButton view) {
    this.view = view;
  }

  @Override public void call(final Subscriber<? super Boolean> subscriber) {
    checkUiThread();

    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
      @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(isChecked);
        }
      }
    };
    view.setOnCheckedChangeListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setOnCheckedChangeListener(null);
      }
    });

    // Emit initial value.
    subscriber.onNext(view.isChecked());
  }
}
