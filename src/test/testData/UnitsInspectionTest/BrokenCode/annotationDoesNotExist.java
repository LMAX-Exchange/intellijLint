@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    @baz long bar;

    public void setToBar()
    {
        bar = 3;
    }
}
