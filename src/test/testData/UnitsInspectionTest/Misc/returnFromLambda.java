public class Foo
{
    private final ThreadLocal<Integer> anInt = ThreadLocal.withInitial(() -> {
        return 1;
    });
}
