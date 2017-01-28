@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    public void setToBar()
    {
        final long bar = emitsFoo();
    }

    private @foo long emitsFoo()
    {
        return 2;
    }
}
