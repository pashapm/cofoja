Contracts for Java enables you to annotate your code with contracts in the form of preconditions, postconditions and invariants.

These contract annotations are

  * easy to write and read,
  * and checked at runtime.

Annotating code with contracts helps you:
  * design,
  * document,
  * test, and
  * debug
your programs.


Here is a simple example of a Java class annotated with contracts:
```
interface Time {
  ...

  @Ensures({
    "result >= 0",
    "result <= 23"
  })
  int getHour();

  @Requires({
    "h >= 0",
    "h <= 23"
  })
  @Ensures("getHour() == h")
  void setHour(int h);

  ...
}
```


Here is what the contract for setHour regulates.


| |Obligation | Benefit |
|:|:----------|:--------|
| Calling method | Satisfy precondition; Make sure 'h' is neither too small nor too large.| (From postcondition) getHour() returns h.|
|Called method	| Satisfy postcondition; Must make getHour() return h.| (From precondition) May assume h is neither too small nor too large.|



To use Cofoja you need to do the following two things:

  * [Download one of the JARs](http://code.google.com/p/cofoja/downloads/list) (Or grab an [unofficial build](http://www.huoc.org/~minh/cofoja/); see the [FAQ](FAQ.md) for more information)
  * [Read the README](http://code.google.com/p/cofoja/source/browse/trunk/README)
  * [Add contracts to your classes](AddContracts.md)


The following provide more information about Cofoja:

  * [Add support for contracts to your Ant project](AntCofoja.md)
  * [Quick reference](QuickReference.md)
  * [Fine tune the runtime checking of contracts](RuntimeCheckingOptions.md)
  * [How to write good contracts](HowtoWriteGoodContracts.md)
  * [Frequently asked questions](FAQ.md)
  * [Using Contracts with Eclipse](http://fsteeg.com/2011/02/07/setting-up-contracts-for-java-in-eclipse/) (external link, thanks to Fabian Steeg)
  * [Using Contracts with IntelliJ IDEA](http://java.dzone.com/articles/using-google-contracts-java) (external link, java.dzone.com)