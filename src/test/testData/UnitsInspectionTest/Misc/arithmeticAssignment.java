@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private @foo long foo = (@foo long) 2;

    private void waffles()
    {
        foo += 4;
    }
}
