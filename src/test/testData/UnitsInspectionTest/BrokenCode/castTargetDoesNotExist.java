@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    long bar;

    public void setToBar()
    {
        bar = (@fez long) 0;
    }
}
