@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private void foos()
    {
        @foo long test = 4 * 2;
    }
}
