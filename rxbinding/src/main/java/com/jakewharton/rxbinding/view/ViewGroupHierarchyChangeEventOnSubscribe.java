package com.jakewharton.rxbinding.view;

import android.view.View;
import android.view.ViewGroup;
import com.jakewharton.rxbinding.internal.MainThreadSubscription;
import rx.Observable;
import rx.Subscriber;

import static com.jakewharton.rxbinding.internal.Preconditions.checkUiThread;

final class ViewGroupHierarchyChangeEventOnSubscribe implements Observable.OnSubscribe<ViewGroupHierarchyChangeEvent> {
  private final ViewGroup viewGroup;

  ViewGroupHierarchyChangeEventOnSubscribe(ViewGroup viewGroup) {
    this.viewGroup = viewGroup;
  }

  @Override public void call(final Subscriber<? super ViewGroupHierarchyChangeEvent> subscriber) {
    checkUiThread();

    ViewGroup.OnHierarchyChangeListener listener = new ViewGroup.OnHierarchyChangeListener() {
      @Override public void onChildViewAdded(View parent, View child) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(ViewGroupHierarchyChildViewAddEvent.create(((ViewGroup) parent), child));
        }
      }

      @Override public void onChildViewRemoved(View parent, View child) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(ViewGroupHierarchyChildViewRemoveEvent.create(((ViewGroup) parent), child));
        }
      }
    };

    viewGroup.setOnHierarchyChangeListener(listener);

    subscriber.add(new MainThreadSubscription() {
      @Override protected void onUnsubscribe() {
        viewGroup.setOnHierarchyChangeListener(null);
      }
    });
  }
}
