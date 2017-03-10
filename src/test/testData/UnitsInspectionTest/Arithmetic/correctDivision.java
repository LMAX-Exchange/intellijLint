@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private void foos()
    {
        @foo long test = (@foo long) 4 /  2;
        @foo long test2 = (@foo long) 4 /  (@foo long) 2;
    }
}
