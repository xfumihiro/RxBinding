package com.jakewharton.rxbinding.widget;

import android.view.View;
import android.widget.AdapterView;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.weaving.annotation.Exclusive;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

@Exclusive
final class AdapterViewItemLongClickEventOnSubscribe
    implements Observable.OnSubscribe<AdapterViewItemLongClickEvent> {
  private final AdapterView<?> view;
  private final Func1<? super AdapterViewItemLongClickEvent, Boolean> handled;

  public AdapterViewItemLongClickEventOnSubscribe(AdapterView<?> view,
      Func1<? super AdapterViewItemLongClickEvent, Boolean> handled) {
    this.view = view;
    this.handled = handled;
  }

  @Override public void call(final Subscriber<? super AdapterViewItemLongClickEvent> subscriber) {
    checkUiThread();

    AdapterView.OnItemLongClickListener listener = new AdapterView.OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AdapterViewItemLongClickEvent event =
            AdapterViewItemLongClickEvent.create(parent, view, position, id);
        if (handled.call(event)) {
          if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(event);
          }
          return true;
        }
        return false;
      }
    };
    view.setOnItemLongClickListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setOnItemLongClickListener(null);
      }
    });
  }
}
