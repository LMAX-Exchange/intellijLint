@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    public void setToBar()
    {
        long bar;

        bar = (@foo long) 2;
    }
}
