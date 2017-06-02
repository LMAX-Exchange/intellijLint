@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private final @foo long test;
    Foo(@foo long test)
    {
        this.test = test;
    }

    private Foo baz()
    {
        return new Foo(0);
    }
}