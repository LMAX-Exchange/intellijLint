@org.checkerframework.framework.qual.SubtypeOf
private @interface foo {}

public class Foo {
    private void foo(){
        @foo long a = (@foo long) 1;
        @foo long b = (@foo long) 2;
        @foo long c = (@foo long) 3;

        @foo long result = (a + b) * c;
    }
}
