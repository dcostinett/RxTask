package com.nicolasmilliard.rxlocation;


import android.app.PendingIntent;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

import static com.nicolasmilliard.rxtask.RxTask.completable;
import static com.nicolasmilliard.rxtask.internal.Preconditions.checkNotNull;

/**
 * FusedLocationProviderClient wrapper {@linkplain Observable observables}.
 */
public class RxGeofencingClient {

    private final GeofencingClient client;

    public RxGeofencingClient(@NonNull GeofencingClient client) {
        checkNotNull(client, "client == null");
        this.client = client;
    }

    @NonNull
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    public Completable addGeofences(GeofencingRequest request, PendingIntent intent) {
        return completable(client.addGeofences(request, intent));
    }

    @NonNull
    public Completable removeGeofences(List<String> geofenceRequestIds) {
        return completable(client.removeGeofences(geofenceRequestIds));
    }

    @NonNull
    public Completable removeGeofences(PendingIntent pendingIntent) {
        return completable(client.removeGeofences(pendingIntent));
    }
}
