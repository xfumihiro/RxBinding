package com.jakewharton.rxbinding.support.design.widget;

import android.support.design.widget.NavigationView;
import android.view.Menu;
import android.view.MenuItem;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.weaving.annotation.Exclusive;
import rx.Observable;
import rx.Subscriber;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

@Exclusive
final class NavigationViewItemSelectionsOnSubscribe implements Observable.OnSubscribe<MenuItem> {
  private final NavigationView view;

  public NavigationViewItemSelectionsOnSubscribe(NavigationView view) {
    this.view = view;
  }

  @Override public void call(final Subscriber<? super MenuItem> subscriber) {
    checkUiThread();

    NavigationView.OnNavigationItemSelectedListener listener =
        new NavigationView.OnNavigationItemSelectedListener() {
          @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
            if (!subscriber.isUnsubscribed()) {
              subscriber.onNext(menuItem);
            }
            return true;
          }
        };
    view.setNavigationItemSelectedListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setNavigationItemSelectedListener(null);
      }
    });

    // Emit initial checked item, if one can be found.
    Menu menu = view.getMenu();
    for (int i = 0, count = menu.size(); i < count; i++) {
      MenuItem item = menu.getItem(i);
      if (item.isChecked()) {
        subscriber.onNext(item);
        break;
      }
    }
  }
}
