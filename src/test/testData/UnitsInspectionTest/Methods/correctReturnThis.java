@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private @foo long baz = (@foo long) 2;
    private long emitsFoo()
    {
        return this;
    }
}