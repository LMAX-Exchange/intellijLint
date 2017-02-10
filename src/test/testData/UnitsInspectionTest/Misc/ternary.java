@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private @foo long emitsFoo()
    {
        @foo long test = true ? 1 : (@foo long) 2;
        return false ? (@foo long) 1 : (@foo long) 2;
    }
}
