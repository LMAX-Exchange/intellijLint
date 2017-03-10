@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private void foos()
    {
        LongStream.of(1, 2, 3).map(x -> (@foo long) x).collect(Collectors.toList());
    }
}
