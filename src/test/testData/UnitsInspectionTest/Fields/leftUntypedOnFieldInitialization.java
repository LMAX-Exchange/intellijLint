@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    final long bar = (@foo long) 2;
}
