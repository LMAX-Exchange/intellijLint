@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private void foos()
    {
        LongStream.of(1, 2, 3).map(x -> frobulate(x)).collect(Collectors.toList());
    }

    private void frobulate(@foo long test)
    {
    }
}
