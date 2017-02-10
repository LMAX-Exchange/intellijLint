@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private void foos()
    {
        @foo long test = true ? 1 : (@foo long) 2;
    }
}
