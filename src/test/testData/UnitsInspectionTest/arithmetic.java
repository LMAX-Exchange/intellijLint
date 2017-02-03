@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private @foo long emitsFoo()
    {
        @foo long test = 1 + (@foo long) 2;
        return (@foo long) 1 + (@foo long) 2;
    }
}
