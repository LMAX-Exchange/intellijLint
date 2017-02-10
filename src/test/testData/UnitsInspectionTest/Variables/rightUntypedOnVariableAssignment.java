@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    public void setToBar()
    {
        @foo final long bar;

        bar = emitsUntyped();
    }

    private long emitsUntyped()
    {
        return 2;
    }
}
