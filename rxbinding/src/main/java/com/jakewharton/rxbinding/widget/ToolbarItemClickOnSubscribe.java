package com.jakewharton.rxbinding.widget;

import android.annotation.TargetApi;
import android.view.MenuItem;
import android.widget.Toolbar;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.weaving.annotation.Exclusive;
import rx.Observable;
import rx.Subscriber;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

@Exclusive
@TargetApi(LOLLIPOP)
final class ToolbarItemClickOnSubscribe implements Observable.OnSubscribe<MenuItem> {
  private final Toolbar view;

  public ToolbarItemClickOnSubscribe(Toolbar view) {
    this.view = view;
  }

  @Override public void call(final Subscriber<? super MenuItem> subscriber) {
    checkUiThread();

    Toolbar.OnMenuItemClickListener listener = new Toolbar.OnMenuItemClickListener() {
      @Override public boolean onMenuItemClick(MenuItem item) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(item);
        }
        return true;
      }
    };
    view.setOnMenuItemClickListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setOnMenuItemClickListener(null);
      }
    });
  }
}
