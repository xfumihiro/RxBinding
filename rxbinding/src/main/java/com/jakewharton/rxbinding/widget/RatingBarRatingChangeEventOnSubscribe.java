package com.jakewharton.rxbinding.widget;

import android.widget.RatingBar;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.weaving.annotation.Exclusive;
import rx.Observable;
import rx.Subscriber;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

@Exclusive
final class RatingBarRatingChangeEventOnSubscribe
    implements Observable.OnSubscribe<RatingBarChangeEvent> {
  private final RatingBar view;

  public RatingBarRatingChangeEventOnSubscribe(RatingBar view) {
    this.view = view;
  }

  @Override public void call(final Subscriber<? super RatingBarChangeEvent> subscriber) {
    checkUiThread();

    RatingBar.OnRatingBarChangeListener listener = new RatingBar.OnRatingBarChangeListener() {
      @Override public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(RatingBarChangeEvent.create(ratingBar, rating, fromUser));
        }
      }
    };
    view.setOnRatingBarChangeListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setOnRatingBarChangeListener(null);
      }
    });

    // Emit initial value.
    subscriber.onNext(RatingBarChangeEvent.create(view, view.getRating(), false));
  }
}
