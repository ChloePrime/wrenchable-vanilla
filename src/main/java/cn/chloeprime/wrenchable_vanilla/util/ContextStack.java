package cn.chloeprime.wrenchable_vanilla.util;

import javax.annotation.Nonnull;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import java.util.Optional;

public class ContextStack<T> {
    private final ThreadLocal<Deque<T>> contextStackByThread = ThreadLocal.withInitial(ArrayDeque::new);

    public Optional<T> current() {
        return Optional.ofNullable(contextStackByThread.get().peek());
    }

    public void wrap(@Nonnull T value, Runnable code) {
        Objects.requireNonNull(value);

        var stack = contextStackByThread.get();
        try {
            stack.push(value);
            code.run();
        } finally {
            stack.pop();
        }
    }
}
