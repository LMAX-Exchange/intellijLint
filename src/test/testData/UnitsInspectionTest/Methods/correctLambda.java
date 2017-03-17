@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private void foos()
    {
        java.util.function.LongFunction longFn = x -> (@foo long) x;
        @foo long test = longFn.apply(2);
    }
}
