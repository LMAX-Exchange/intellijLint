@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    @foo long bar;

    public void setToBar()
    {
        bar = fez;
    }
}
