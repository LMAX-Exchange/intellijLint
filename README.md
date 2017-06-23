### IntellijLint

This is an intellij plugin providing a subset of the functionality available in the 
[units checker of the checker framework](https://checkerframework.org/manual/#units-checker).
At the moment you should not rely solely on this plugin to replace the static analysis provided by the
Checker Framework, but rather just to make it easier to work with code that is expected to pass the 
unit check provided by checker.

Also includes a bonus inspection checking that you don't assign null to an Optional, or variations thereof.

#### Why?

At [LMAX](https://www.lmax.com/) we rely on the checker framework to make sure we keep our price and 
quantity fields at the same measurement (for instance quantity can be represented as 
[contract quantity](http://www.investopedia.com/terms/c/contractsize.asp), or the underlying quantity)

The time it takes to run the checker framework over our entire codebase can be measured in minutes,
and we only run it just before committing. This leads to us finding out about type bugs when we think
we're already done, rather than when we're working on the code that contains the incorrect typing.
Basically, we just want faster feedback, so I spent some time making that happen.

#### What does it actually do?

At this stage, it just checks that annotations on usages of a primitive match whatever value you provide.
For example:

```java
@org.checkerframework.framework.qual.SubtypeOf
@interface Price {}

class TestClass {
    private @Price long aField;
    
    TestClass()
    {
        aField = 2; //<-- not ok, will be flagged
        aField = (@Price long) 2; //<-- ok, will not be flagged
    }
    
    public void exampleMethod(final @Price long aParam)
    {
        aField = aParam;
    }
    
    public void useExampleMethod()
    {
        exampleMethod(0); //<-- not ok
        exampleMethod(aField); //<-- ok
    }
}
```

Note it doesn't do any of the stuff related to combinations of units like checker does. 
I don't intend to support that at this stage, since we don't use it, but I'm happy to accept PRs for it,
or even just PRs of test cases.

Currently it checks:

- [x] Arguments passed to methods
- [x] Field and variable assignments
- [x] Method implementations
- [x] Return types
- [ ] Ternary expressions
- [ ] Methods overriding super-class methods
- [ ] Polyadic expressions (i.e. `1 + 1 + 2`)
- [ ] Collections
- [ ] Preserving functions (i.e. `OptionalLong.of((@Price long) 123)` should return an `@Price OptionalLong` 
whose `.get()` method should return an `@Price long`. Without having to do anything funny to the optional classes)