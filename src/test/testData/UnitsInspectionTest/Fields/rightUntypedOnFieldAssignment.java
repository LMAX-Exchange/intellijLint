@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    @foo final long bar;

    public void setToBar()
    {
        bar = emitsUntyped();
    }

    private long emitsUntyped()
    {
        return 2;
    }
}
