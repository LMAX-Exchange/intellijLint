@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private void baz()
    {
        @foo long test1 = (@foo long) 0;
        @foo long test2 = (@foo long) 0;

        if (test1 == test2)
        {
        }
    }
}
