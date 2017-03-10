@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private void foos()
    {
        String test = "Hello " + (@foo long) 2 + " world";
    }
}
