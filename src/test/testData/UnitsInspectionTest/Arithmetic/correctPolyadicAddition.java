@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private @foo long foo = (@foo long) 2;
    private void foos()
    {
        waffles();
        @foo long test = foo + (@foo long) 2 + foo;
    }

    private void waffles()
    {
        foo = (@foo long) 6;
    }
}
