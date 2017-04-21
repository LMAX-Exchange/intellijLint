@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    @foo final long bar = emitsUntyped();

    private long emitsUntyped()
    {
        return 2;
    }
}
