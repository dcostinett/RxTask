package com.nicolasmilliard.rxlocation;

import android.annotation.SuppressLint;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.Exceptions;
import io.reactivex.plugins.RxJavaPlugins;

final class LocationAvailabilityObservable extends Observable<LocationAvailability> {
    private final FusedLocationProviderClient client;
    private final LocationRequest request;

    public LocationAvailabilityObservable(FusedLocationProviderClient client, LocationRequest request) {
        this.client = client;
        this.request = request;
    }

    @SuppressLint({"MissingPermission"})
    protected void subscribeActual(Observer observer) {
        LocationAvailabilityObservable.ResultCallback callback = new LocationAvailabilityObservable.ResultCallback(this.client, observer);
        observer.onSubscribe(callback);
        this.client.requestLocationUpdates(this.request, callback, null)
                .addOnCompleteListener(callback);
    }

    public static final class ResultCallback extends LocationCallback implements Disposable, OnCompleteListener {
        private final FusedLocationProviderClient client;
        private final Observer<LocationAvailability> observer;
        private boolean disposed;

        public ResultCallback(FusedLocationProviderClient client, Observer<LocationAvailability> observer) {
            this.client = client;
            this.observer = observer;
        }

        public void onLocationAvailability(LocationAvailability result) {
            super.onLocationAvailability(result);
            if (!this.disposed) {
                this.observer.onNext(result);
            }
        }

        public void onComplete(Task task) {
            if (this.disposed) return;
            if (!task.isSuccessful()) {
                try {
                    observer.onError(task.getException());
                } catch (Throwable t) {
                    Exceptions.throwIfFatal(t);
                    RxJavaPlugins.onError(new CompositeException(task.getException(), t));
                }
            }

        }

        public boolean isDisposed() {
            return this.disposed;
        }

        public void dispose() {
            this.disposed = true;
            this.client.removeLocationUpdates(this).addOnCompleteListener(task -> {

            });
        }
    }
}
