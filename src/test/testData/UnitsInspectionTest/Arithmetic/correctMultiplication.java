@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private void foos()
    {
        @foo long foo = (@foo long) 2;

        @foo long test = foo * (@foo long) 2;
        @foo long test2 = foo * 2;
    }
}
