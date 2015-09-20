package com.jakewharton.rxbinding.support.v4.widget;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.view.View;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import com.jakewharton.rxbinding.weaving.annotation.Exclusive;
import rx.Observable;
import rx.Subscriber;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

@Exclusive
final class DrawerLayoutDrawerOpenedOnSubscribe implements Observable.OnSubscribe<Boolean> {
  private final DrawerLayout view;
  private final int gravity;

  DrawerLayoutDrawerOpenedOnSubscribe(DrawerLayout view, int gravity) {
    this.view = view;
    this.gravity = gravity;
  }

  @Override public void call(final Subscriber<? super Boolean> subscriber) {
    checkUiThread();

    DrawerLayout.DrawerListener listener = new DrawerLayout.SimpleDrawerListener() {
      @Override public void onDrawerOpened(View drawerView) {
        if (!subscriber.isUnsubscribed()) {
          int drawerGravity = ((LayoutParams) drawerView.getLayoutParams()).gravity;
          if (drawerGravity == gravity) {
            subscriber.onNext(true);
          }
        }
      }

      @Override public void onDrawerSlide(View drawerView, float slideOffset) {
      }

      @Override public void onDrawerStateChanged(int newState) {
      }

      @Override public void onDrawerClosed(View drawerView) {
        if (!subscriber.isUnsubscribed()) {
          int drawerGravity = ((LayoutParams) drawerView.getLayoutParams()).gravity;
          if (drawerGravity == gravity) {
            subscriber.onNext(false);
          }
        }
      }
    };
    view.setDrawerListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        view.setDrawerListener(null);
      }
    });

    // Emit initial value.
    subscriber.onNext(view.isDrawerOpen(gravity));
  }
}
