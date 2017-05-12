@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    final long bar;

    public @foo long getBar()
    {
        return bar;
    }
}
