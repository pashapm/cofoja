## Are there any pre-packaged JAR files I can download? ##
> Pre-built JAR files are available on the [GitHub release page](https://github.com/nhatminhle/cofoja/releases).

## What do I need in order to use Cofoja? ##
> Java 6 or higher and a recent version of the [ASM library](http://asm.ow2.org). See the [README.md section on dependencies](https://github.com/nhatminhle/cofoja#dependencies) for specific versions and instructions.

## How is this better than using assert? ##
  * Contracts are inherited from superclasses and interfaces.
  * Inherited contracts can be extended, but only within the limits imposed by the superclass -- as in the Liskov substitution principle.
  * `@Ensures` can use the `old` keyword to refer to the state at the start of the method, e.g. `@Ensures("size() == old(size()) + 1")`.
  * `@Invariant` adds checks to every public method.
  * `@ThrowEnsures` adds checks about conditions when an exception is thrown.
  * Disabled by default -- so you can and should write expensive checks. Can be enabled all at once or selectively, i.e. just for a particular library or group of libraries.
  * Clean separation of assumptions and guarantees from the actual method code. The annotations become part of the javadoc.

## Is this framework related to 'ModernJass' from Sourceforge.net? ##
> Yes, Cofoja is a significant rewrite of ModernJass. We worked closely with the original author of ModernJass (Johannes Rieken) on this.

## Is code annotated with contracts slower than code not annotated? ##
> If runtime checking is enabled, yes. If runtime checking is disable the code is as fast as if there were no annotations.

## Is the contract code compiled? I only see strings in annotations! ##
> The annotation processor takes care of compiling the strings into bytecode and runs along the Java compiler, so you get static syntax and typing errors at compile time, as usual.

## Is there any IDE support yet? ##
> Not yet. The eclipse plugin from the ModernJass sourceforge.net page is obsolete. Let us know if you would like to help!

## What about debugger support? ##
> Contracts for Java adds standard debugging information, such as source file and line numbers, to contract code. Debugging has been tested under JDB, Eclipse and IntelliJ (using remote debugging); only Eclipse has issues with setting breakpoints on contract annotations (source correspondence and step-by-step debugging works, though).

## Is the source code of Contracts for Java annotated with contracts? (I.e. are you eating your own dogfood?) ##
> Yes, all the classes in Cofoja that could bear contracts have been contracted; some classes needed at run time cannot be annotated because of technical reasons. New classes are only added to the project with proper contracts.