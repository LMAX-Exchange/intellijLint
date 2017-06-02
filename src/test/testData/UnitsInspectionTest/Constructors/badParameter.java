@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private final @foo long test;
    Foo(long test)
    {
        this.test = test;
    }
}