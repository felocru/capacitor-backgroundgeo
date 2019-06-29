package com.felocru.backgroundgeo;
import io.reactivex.disposables.Disposable;
import android.support.annotation.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
public final class RxBus {
    private static final BehaviorSubject<Object> behaviorSubject = BehaviorSubject.create();

    public static Disposable subscribe(@NonNull Consumer<Object> action){
        return behaviorSubject.subscribe(action);
    }

    public static void publish(@NonNull Object message){
        behaviorSubject.onNext(message);
    }
}
