package com.jakewharton.rxbinding.widget;

import android.view.View;
import android.widget.AdapterView;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.weaving.annotation.Exclusive;
import rx.Observable;
import rx.Subscriber;

import static android.widget.AdapterView.INVALID_POSITION;
import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

@Exclusive
final class AdapterViewItemSelectionOnSubscribe implements Observable.OnSubscribe<Integer> {
  private final AdapterView<?> view;

  public AdapterViewItemSelectionOnSubscribe(AdapterView<?> view) {
    this.view = view;
  }

  @Override public void call(final Subscriber<? super Integer> subscriber) {
    checkUiThread();

    AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(position);
        }
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(INVALID_POSITION);
        }
      }
    };
    view.setOnItemSelectedListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setOnItemSelectedListener(null);
      }
    });

    // Emit initial value.
    subscriber.onNext(view.getSelectedItemPosition());
  }
}
