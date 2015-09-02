package com.jakewharton.rxbinding.support.v7.widget

import android.support.v7.widget.RecyclerView
import rx.Observable
import rx.functions.Action1

/**
 * Create an observable of scroll events on `recyclerView`.
 * 
 * *Warning:* The created observable keeps a strong reference to `recyclerView`.
 * Unsubscribe to free this reference.
 */
public inline fun RecyclerView.scrollEvents(): Observable<RecyclerViewScrollEvent> = RxRecyclerView.scrollEvents(this)

/**
 * Create an observable of scroll state change events on `recyclerView`.
 * 
 * *Warning:* The created observable keeps a strong reference to `recyclerView`.
 * Unsubscribe to free this reference.
 */
public inline fun RecyclerView.scrollStateChangeEvents(): Observable<RecyclerViewScrollStateChangeEvent> = RxRecyclerView.scrollStateChangeEvents(this)

/**
 * Create an observable of the position of item clicks for `recyclerView`.
 * 
 * *Warning:* The created observable keeps a strong reference to `recyclerView`.
 * Unsubscribe to free this reference.
 */
public inline fun RecyclerView.itemClicks(): Observable<Int> = RxRecyclerView.itemClicks(this)
